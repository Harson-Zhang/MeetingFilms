package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference(interfaceClass = OrderServiceAPI.class, check = false, loadbalance = "roundrobin")
    OrderServiceAPI orderServiceAPI;

    @RequestMapping(value = "/buyTickets", method = RequestMethod.POST)
    ResponseVO buyTickets(int fieldId, String soldSeat, String seatName) {
        // 验证座位是否合法

        // 验证当前座位是否为空

        // 创建座位订单

        return null;
    }

    @RequestMapping(value = "/getOrderInfo", method = RequestMethod.POST)
    ResponseVO getOrderInfo(
            @RequestParam(name = "nowPage", required = false, defaultValue = "1") int nowPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize
    ) {
        // 根据用户id，查找他的订单


        // 根据fieldId获取所有已经销售的座位编号？


        return null;
    }
}
