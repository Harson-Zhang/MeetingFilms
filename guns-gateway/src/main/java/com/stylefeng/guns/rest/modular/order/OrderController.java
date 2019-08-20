package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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
    @Reference(interfaceClass = OrderServiceAPI.class, check = false, loadbalance = "roundrobin")
    OrderServiceAPI orderServiceAPI;

    TokenBucket tokenBucket = new TokenBucket();

    ResponseVO buyTicketsError (int fieldId, String soldSeats, String seatsName) {
        return ResponseVO.serviceFail("抱歉，下单的人太多，请稍候重试！");
    }

    @HystrixCommand(fallbackMethod = "buyTicketsError", commandProperties = {
            @HystrixProperty(name="execution.isolation.strategy", value = "THREAD"),
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
    ResponseVO buyTickets(@RequestParam(name = "fieldId") int fieldId,
                          @RequestParam(name = "soldSeats") String soldSeats,
                          @RequestParam(name = "seatsName") String seatsName) {
        // 判非法
        if(fieldId<0 || StringUtils.isEmpty(soldSeats) || "".equals(soldSeats.trim()) ||
                StringUtils.isEmpty(seatsName) || "".equals(seatsName.trim()) ){
            log.error("入参异常，fieldId: {}，soldSeats: {}，seatsName: {}", fieldId, soldSeats, seatsName);
            return ResponseVO.serviceFail("输入参数异常！");
        }
        // 令牌判断
        if (!tokenBucket.getToken()){
            return ResponseVO.serviceFail("T^T 抱歉，下单的人太多，请稍候重试！");
        }

        String[] seatsArr = soldSeats.split(",");
        int[] seatsInt = new int[seatsArr.length];
        for (int i = 0; i < seatsArr.length; i++) {
            seatsInt[i] = Integer.parseInt(seatsArr[i]);
        }
        // 验证座位是否合法
        boolean isSeatsLegal = orderServiceAPI.isTrueSeats(fieldId, seatsInt);
        // 验证当前座位是否为空
        boolean isNotSold = orderServiceAPI.isNotSold(fieldId, seatsInt);
        // 验证用户是否登录
        String userId = CurrentUser.getCurrentUser();
        if(userId == null || "".equals(userId.trim())){
            return ResponseVO.serviceFail("用户未登录");
        }
        // 创建座位订单
        OrderInfoVO orderInfoVO;
        if (isSeatsLegal && isNotSold) {
            orderInfoVO = orderServiceAPI.createOrder(fieldId, seatsInt, Integer.parseInt(userId), seatsName);
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
    ResponseVO getOrderInfo(
            @RequestParam(name = "nowPage", required = false, defaultValue = "1") int nowPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize
    ) {
        // 验证用户是否登录
        String userId = CurrentUser.getCurrentUser();
        if(userId == null || "".equals(userId.trim())){
            return ResponseVO.serviceFail("用户未登录");
        }
        // 根据用户id，查找他的订单
        Page<OrderInfoVO> orderPageList = orderServiceAPI.getOrderByUser(Integer.parseInt(userId), nowPage, pageSize);
        if(orderPageList.getRecords().size() > 0) {
            return ResponseVO.success(orderPageList.getRecords(), nowPage, ((int)orderPageList.getTotal())/pageSize+1, "");
        } else {
            return ResponseVO.serviceFail("订单列表为空哦");
        }
    }
}
