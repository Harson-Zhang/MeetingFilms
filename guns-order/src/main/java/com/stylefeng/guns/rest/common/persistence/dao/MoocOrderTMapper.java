package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author Harson
 * @since 2019-08-15
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {
    OrderInfoVO getOrderInfoById(@Param("orderId") String uuid);

    List<OrderInfoVO> getOrdersByUser(@Param("userId") String userId, @Param("start") int startPage, @Param("end") int endPage);
}
