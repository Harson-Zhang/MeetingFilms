package com.stylefeng.guns.api.order;

import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import org.mengyun.tcctransaction.api.Compensable;

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
    @Compensable
    boolean isTrueSeats(int fieldId, int [] seatIds);

    /**
     * 验证座位是否未销售
     * @param fieldId
     * @param seatIds
     * @return
     */
    @Compensable
    boolean isNotSold(int fieldId, int [] seatIds);

    /**
     * 创建订单
     * @param fieldId
     * @param seatIds
     * @param userId
     * @return
     */
    @Compensable
    OrderInfoVO createOrder(String uuid, int fieldId, int [] seatIds, int userId, String seatsName);

    boolean seatConfirm(int fieldId, int[] seatIds);

    boolean seatCancel(int fieldId, int[] seatIds);

    boolean notSoldConfirm(int fieldId, int[] seatIds);

    boolean notSoldCancel(int fieldId, int[] seatIds);

    OrderInfoVO orderConfirm(String uuid, int fieldId, int[] seatIds, int userId, String seatsName);

    OrderInfoVO orderCancel(String uuid, int fieldId, int[] seatIds, int userId, String seatsName);
}
