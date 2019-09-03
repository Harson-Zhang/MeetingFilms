package com.stylefeng.guns.api.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;

public interface OrderServiceAPI {
    /**
     * 根据用户获取订单信息
     * @param userId
     * @return
     */
    Page<OrderInfoVO> getOrderByUser(int userId, int nowPage, int pageSize);

    /**
     * 根据订单编号获取订单信息
     * @param orderId
     * @return
     */
    OrderInfoVO getOrderById(String orderId);

    /**
     * 订单支付成功后，业务系统的执行方法
     * @param orderId
     * @return
     */
    boolean paySuccess(String orderId);

    /**
     * 订单支付失败后，业务系统的执行方法
     * @param orderId
     * @return
     */
    boolean payFail(String orderId);
}
