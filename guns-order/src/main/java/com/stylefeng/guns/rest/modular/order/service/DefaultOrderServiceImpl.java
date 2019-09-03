package com.stylefeng.guns.rest.modular.order.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FieldInfoVo;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.api.uid.UidGenAPI;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderServiceAPI.class, filter = "tracing")
public class DefaultOrderServiceImpl implements OrderServiceAPI {

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    @Override
    public Page<OrderInfoVO> getOrderByUser(int userId, int nowPage, int pageSize) {
        int startPage = (nowPage - 1) * pageSize;
        int endPage = nowPage * pageSize;
        List<OrderInfoVO> orderList = moocOrderTMapper.getOrdersByUser(userId + "", startPage, endPage);
        for (OrderInfoVO order : orderList) {
            String status = order.getOrderStatus();
            switch (status) {
                case "0":
                    order.setOrderStatus("待支付");
                case "1":
                    order.setOrderStatus("已支付");
                case "2":
                    order.setOrderStatus("已关闭");
            }
        }
        Page<OrderInfoVO> orderPage = new Page<>();
        // 查找用户订单总量，放入page
        EntityWrapper<MoocOrderT> orderTWrapper = new EntityWrapper<>();
        orderTWrapper.eq("order_user", userId);
        int count = moocOrderTMapper.selectCount(orderTWrapper);

        orderPage.setRecords(orderList);
        orderPage.setTotal(count);
        orderPage.setCurrent(nowPage);

        return orderPage;
    }

    @Override
    public OrderInfoVO getOrderById(String orderId) {
        return moocOrderTMapper.getOrderInfoById(orderId);
    }

    @Override
    public boolean paySuccess(String orderId) {
        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(1);

        Integer result = moocOrderTMapper.updateById(moocOrderT);
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean payFail(String orderId) {
        // 好像不用写。。。
        return false;
    }
}
