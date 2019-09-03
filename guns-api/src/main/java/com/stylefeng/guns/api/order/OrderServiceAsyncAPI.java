package com.stylefeng.guns.api.order;

import com.stylefeng.guns.api.order.vo.OrderInfoVO;

/**
 * @author Harson
 * @version 1.0
 * @date 2019/9/3 14:59
 */
public interface OrderServiceAsyncAPI {
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
}
