package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.alipay.AlipayServiceAPI;
import com.stylefeng.guns.api.alipay.vo.PayInfoVO;
import com.stylefeng.guns.api.alipay.vo.PayResultVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.OrderServiceAsyncAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.api.uid.UidGenAPI;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.common.util.TokenBucket;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.dubbo.context.DubboTransactionContextEditor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference(interfaceClass = OrderServiceAPI.class, check = false, filter = "tracing")
    OrderServiceAPI orderServiceAPI;

    @Reference(interfaceClass = OrderServiceAsyncAPI.class, check = false, async = true, filter = "tracing")
    OrderServiceAsyncAPI orderServiceAsyncAPI;

    @Reference(interfaceClass = AlipayServiceAPI.class, check = false, mock = "com.stylefeng.guns.api.alipay.AlipayServiceMock", filter = "tracing")
    AlipayServiceAPI alipayServiceAPI;

    @Reference(interfaceClass = UidGenAPI.class, check = false, filter = "tracing")
    UidGenAPI uidGenAPI;

    private static final String IMG_PRE = "http://img.meetingshop.cn/";
    private TokenBucket tokenBucket = new TokenBucket();

    public ResponseVO buyTicketsError(int fieldId, String soldSeats, String seatsName) {
        return ResponseVO.serviceFail("抱歉，下单的人太多，请稍候重试！");
    }

    @HystrixCommand(fallbackMethod = "buyTicketsError", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
    }, threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "1"),
            @HystrixProperty(name = "maxQueueSize", value = "10"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
    })
    @Compensable(confirmMethod = "buyTicketsConfirm", cancelMethod = "buyTicketsCancel", transactionContextEditor = DubboTransactionContextEditor.class)
    @RequestMapping(value = "/buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(@RequestParam(name = "fieldId") int fieldId,
                                 @RequestParam(name = "soldSeats") String soldSeats,
                                 @RequestParam(name = "seatsName") String seatsName) throws ExecutionException, InterruptedException {
        // 判非法
        if (fieldId < 0 || StringUtils.isEmpty(soldSeats) || "".equals(soldSeats.trim()) ||
                StringUtils.isEmpty(seatsName) || "".equals(seatsName.trim())) {
            log.error("入参异常，fieldId: {}，soldSeats: {}，seatsName: {}", fieldId, soldSeats, seatsName);
            return ResponseVO.serviceFail("输入参数异常！");
        }
        // 令牌判断
        if (!tokenBucket.getToken()) {
            return ResponseVO.serviceFail("T^T 抱歉，下单的人太多，请稍候重试！");
        }

        // 验证用户是否登录
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || "".equals(userId.trim())) {
            return ResponseVO.serviceFail("用户未登录");
        }
        log.info("用户已登录");

        //获取订单号
        String uuid = uidGenAPI.getUid()+"";

        String[] seatsArr = soldSeats.split(",");
        int[] seatsInt = new int[seatsArr.length];
        for (int i = 0; i < seatsArr.length; i++) {
            seatsInt[i] = Integer.parseInt(seatsArr[i]);
        }

        // 验证座位是否合法
        orderServiceAsyncAPI.isTrueSeats(fieldId, seatsInt);
        Future<Boolean> isSeatsLegal = RpcContext.getContext().getFuture();

        // 验证当前座位是否为空
        orderServiceAsyncAPI.isNotSold(fieldId, seatsInt);
        Future<Boolean> isNotSold = RpcContext.getContext().getFuture();

        // 创建座位订单
        orderServiceAsyncAPI.createOrder(uuid, fieldId, seatsInt, Integer.parseInt(userId), seatsName);
        Future<OrderInfoVO> orderInfoVOFuture = RpcContext.getContext().getFuture();

        log.info("用户创建订单");
        if (orderInfoVOFuture.get() == null) {
            log.error("订单生成失败, 座位合法：{}, 座位未售：{}",isSeatsLegal.get(), isNotSold.get());
            return ResponseVO.serviceFail("购票业务异常");
        } else {
            log.info("订单生成成功, 座位合法：{}, 座位未售：{}",isSeatsLegal.get(), isNotSold.get());
            return ResponseVO.success(orderInfoVOFuture.get());
        }
    }

    public ResponseVO buyTicketsConfirm(int fieldId, String soldSeats, String seatsName) {
        log.info("整个下订单事务提交");
        return null;
    }
    public ResponseVO buyTicketsCancel(int fieldId, String soldSeats, String seatsName) {
        log.info("整个下订单事务取消");
        return null;
    }


    @RequestMapping(value = "/getOrderInfo", method = RequestMethod.POST)
    public ResponseVO getOrderInfo(
            @RequestParam(name = "nowPage", required = false, defaultValue = "1") int nowPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize
    ) {
        // 验证用户是否登录
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || "".equals(userId.trim())) {
            return ResponseVO.serviceFail("用户未登录");
        }
        // 根据用户id，查找他的订单
        Page<OrderInfoVO> orderPageList = orderServiceAPI.getOrderByUser(Integer.parseInt(userId), nowPage, pageSize);
        if (orderPageList.getRecords().size() > 0) {
            return ResponseVO.success(orderPageList.getRecords(), nowPage, ((int) orderPageList.getTotal()) / pageSize + 1, "");
        } else {
            return ResponseVO.serviceFail("订单列表为空哦");
        }
    }

    /**
     * 根据订单号，获取支付信息(二维码)
     *
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/getPayInfo", method = RequestMethod.POST)
    public ResponseVO getPayInfoByOrderId(String orderId) {
        // 验证用户是否登录
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || "".equals(userId.trim())) {
            return ResponseVO.serviceFail("当前用户未登录");
        }
        try {
            PayInfoVO payInfo = alipayServiceAPI.getQRCodeByOrderId(orderId);
            return ResponseVO.success(payInfo, IMG_PRE);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseVO.serviceFail("订单支付失败，请稍候重试");
        }
    }

    /**
     * 根据订单号，获取支付结果
     *
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/getPayResult", method = RequestMethod.POST)
    public ResponseVO getPayResultByOrderId(
            @RequestParam(name = "orderId") String orderId,
            @RequestParam(name = "tryNums", required = false, defaultValue = "1") int tryNums) {
        // 验证用户是否登录
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || "".equals(userId.trim())) {
            return ResponseVO.serviceFail("当前用户未登录");
        }
        // 设置隐式参数：userId
        RpcContext.getContext().setAttachment("userId", userId);

        try {
            PayResultVO resultVO = alipayServiceAPI.getPayResult(orderId);
            return ResponseVO.success(resultVO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("异常信息：{}", e.getMessage());
            return ResponseVO.serviceFail("订单查询失败，请稍候重试");
        }
    }
}
