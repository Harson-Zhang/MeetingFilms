package com.stylefeng.guns.api.order;

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
     * @param seatName
     * @return
     */
    OrderInfoVO createOrder(int fieldId, int [] seatIds, int userId, String seatName);

    /**
     * 根据用户获取订单信息
     * @param userId
     * @return
     */
    List<OrderInfoVO> getOrderByUser(int userId);

    /**
     * 根据场次获取已售座位
     * @param fieldId
     * @return
     */
    String getSoldSeatByFieldId(int fieldId);
}
