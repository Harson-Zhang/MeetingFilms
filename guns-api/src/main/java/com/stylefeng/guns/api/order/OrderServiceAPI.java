package com.stylefeng.guns.api.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;

import java.util.List;

public interface OrderServiceAPI {
    /**
     * 验证座位是否为真
     * @param fieldId
     * @param seatIds
     * @return
     */
    boolean isTrueSeats(int fieldId, int [] seatIds);

    /**
     * 验证座位是否未销售
     * @param fieldId
     * @param seatIds
     * @return
     */
    boolean isNotSold(int fieldId, int [] seatIds);

    /**
     * 创建订单
     * @param fieldId
     * @param seatIds
     * @param userId
     * @return
     */
    OrderInfoVO createOrder(int fieldId, int [] seatIds, int userId, String seatsName);

    /**
     * 根据用户获取订单信息
     * @param userId
     * @return
     */
    Page<OrderInfoVO> getOrderByUser(int userId, int nowPage, int pageSize);

}
