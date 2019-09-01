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
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.common.util.TokenBucket;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference(interfaceClass = OrderServiceAPI.class, check = false, filter = "tracing")
    OrderServiceAPI orderServiceAPI;

    @Reference(interfaceClass = AlipayServiceAPI.class, check = false, mock = "com.stylefeng.guns.api.alipay.AlipayServiceMock", filter = "tracing")
    AlipayServiceAPI alipayServiceAPI;

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
    @RequestMapping(value = "/buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(@RequestParam(name = "fieldId") int fieldId,
                                 @RequestParam(name = "soldSeats") String soldSeats,
                                 @RequestParam(name = "seatsName") String seatsName) {
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

        String[] seatsArr = soldSeats.split(",");
        int[] seatsInt = new int[seatsArr.length];
        for (int i = 0; i < seatsArr.length; i++) {
            seatsInt[i] = Integer.parseInt(seatsArr[i]);
        }
        // 验证座位是否合法
        boolean isSeatsLegal = orderServiceAPI.isTrueSeats(fieldId, seatsInt);
        log.info("座位合法");
        // 验证当前座位是否为空
        boolean isNotSold = orderServiceAPI.isNotSold(fieldId, seatsInt);
        log.info("座位不为空");
        // 验证用户是否登录
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || "".equals(userId.trim())) {
            return ResponseVO.serviceFail("用户未登录");
        }
        log.info("用户已登录");
        // 创建座位订单
        OrderInfoVO orderInfoVO;
        if (isSeatsLegal && isNotSold) {
            orderInfoVO = orderServiceAPI.createOrder(fieldId, seatsInt, Integer.parseInt(userId), seatsName);
            log.info("用户创建订单");
            if (orderInfoVO == null) {
                log.error("订单生成失败");
                return ResponseVO.serviceFail("购票业务异常");
            } else {
                return ResponseVO.success(orderInfoVO);
            }
        } else {
            return ResponseVO.serviceFail("该订单座位编号异常");
        }

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
            log.error("异常信息：{}",e.getMessage());
            return ResponseVO.serviceFail("订单查询失败，请稍候重试");
        }
    }
}
