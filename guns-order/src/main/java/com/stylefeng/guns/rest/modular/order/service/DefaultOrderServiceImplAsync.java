package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FieldInfoVo;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;
import com.stylefeng.guns.api.order.OrderServiceAsyncAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.utils.FTPUtils;
import com.stylefeng.guns.rest.modular.order.exception.OrderCreateException;
import com.stylefeng.guns.rest.modular.order.exception.SeatException;
import lombok.extern.slf4j.Slf4j;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.dubbo.context.DubboTransactionContextEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderServiceAsyncAPI.class, filter = "tracing")
public class DefaultOrderServiceImplAsync implements OrderServiceAsyncAPI {
    @Reference(interfaceClass = CinemaServiceAPI.class, check = false, filter = "tracing")
    CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    @Autowired
    FTPUtils ftpUtils;

    @Override
    @Compensable(confirmMethod = "seatConfirm", cancelMethod = "seatCancel", transactionContextEditor = DubboTransactionContextEditor.class)
    public boolean isTrueSeats(int fieldId, int[] seatIds) {
        // 1. 获取jsonObject, 取出影院位置信息
        JSONObject jsonObject = getJsonObjByFieldId(fieldId);
        String seatsFileStr = jsonObject.getString("ids");
        String[] seatsFileArr = seatsFileStr.split(",");

        // 2. 查看位置编号是否在json中存在（seatId）
        if(!areSeatsExist(seatIds, seatsFileArr)){
            log.error("用户所选位置不存在");
            throw new SeatException("用户所选位置不存在");
        }
        return true;
    }

    @Override
    @Compensable(confirmMethod = "notSoldConfirm", cancelMethod = "notSoldCancel", transactionContextEditor = DubboTransactionContextEditor.class)
    public boolean isNotSold(int fieldId, int[] seatIds) {
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("field_id", fieldId);
        List<MoocOrderT> orderTS = moocOrderTMapper.selectList(wrapper);
        HashSet<Integer> hashSet = new HashSet<>();
        String[] seatsIdArr;

        for (MoocOrderT orderT : orderTS) {
            if(orderT.getOrderStatus() != 2){
                String seatsIdStr = orderT.getSeatsIds();
                seatsIdArr = seatsIdStr.split(",");
                for (String seat : seatsIdArr) {
                    hashSet.add(Integer.parseInt(seat));
                }
            }
        }

        for (int seatId : seatIds) {
            if (hashSet.contains(seatId)) {
                log.error("当前座位已售");
                throw new SeatException("当前座位已售");
            }
        }
        return true;
    }

    @Override
    @Compensable(confirmMethod = "orderConfirm", cancelMethod = "orderCancel", transactionContextEditor = DubboTransactionContextEditor.class)
    public OrderInfoVO createOrder(String uuid, int fieldId, int[] seatIds, int userId, String seatsName) {
        FieldInfoVo fieldInfoVo = cinemaServiceAPI.getFieldInfoByFieldId(fieldId);

        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(uuid);
        moocOrderT.setCinemaId(fieldInfoVo.getCinemaId());
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setFilmId(fieldInfoVo.getFilmId());

        String[] strarr = new String[seatIds.length];
        for (int i = 0; i < seatIds.length; i++) {
            strarr[i] = seatIds[i] + "";
        }
        moocOrderT.setSeatsIds(String.join(",", strarr));
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setFilmPrice((double) fieldInfoVo.getPrice());
        moocOrderT.setOrderPrice(getOrderPrice((double) fieldInfoVo.getPrice(), seatIds.length));
        moocOrderT.setOrderUser(userId);
        moocOrderT.setOrderStatus(3); //此处修改为草稿状态，以便实现幂等性

        Integer insert = moocOrderTMapper.insert(moocOrderT);
        if (insert > 0) {
            //创建成功，返回OrderInfoVO
            OrderInfoVO orderInfoVO = moocOrderTMapper.getOrderInfoById(uuid);
            if (orderInfoVO != null && orderInfoVO.getOrderId() != null) {
                return orderInfoVO;
            }
        } else {
            log.error("数据库插入失败！");
            throw new OrderCreateException("订单创建失败");
        }
        return null;
    }

    public boolean seatConfirm(int fieldId, int[] seatIds){
        log.info("位置存在确认！");
        return true;
    }

    public boolean seatCancel(int fieldId, int[] seatIds){
        log.info("位置存在取消！");
        return false;
    }

    public boolean notSoldConfirm(int fieldId, int[] seatIds) {
        log.info("位置未售确认！");
        return true;
    }

    public boolean notSoldCancel(int fieldId, int[] seatIds) {
        log.info("位置未售取消！");
        return false;
    }

    public OrderInfoVO orderConfirm(String uuid, int fieldId, int[] seatIds, int userId, String seatsName) {
        OrderInfoVO orderInfoVO = moocOrderTMapper.getOrderInfoById(uuid);
        if(orderInfoVO!=null && "3".equals(orderInfoVO.getOrderStatus())) {
            MoocOrderT moocOrderT = new MoocOrderT();
            moocOrderT.setUuid(uuid);
            moocOrderT.setOrderStatus(0);
            moocOrderTMapper.updateById(moocOrderT);
            log.info("创建订单确认");
        }
        return null;
    }

    public OrderInfoVO orderCancel(String uuid, int fieldId, int[] seatIds, int userId, String seatsName) {
        OrderInfoVO orderInfoVO = moocOrderTMapper.getOrderInfoById(uuid);
        if(orderInfoVO!=null && "3".equals(orderInfoVO.getOrderStatus())) {
            MoocOrderT moocOrderT = new MoocOrderT();
            moocOrderT.setUuid(uuid);
            moocOrderT.setOrderStatus(2);
            moocOrderTMapper.updateById(moocOrderT);
            log.info("创建订单取消");
        }
        return null;
    }

    // 因为默认seatIds, seatsFileArr都是增序，所以写一个方法，保证时间复杂度O(n)
    private boolean areSeatsExist(int[] seatIds, String[] seatsFileArr) {
        int count = 0;
        int p = 0, q = 0;
        while (p < seatIds.length && q < seatsFileArr.length) {
            if (seatIds[p] < Integer.parseInt(seatsFileArr[q])) {
                p++;
            } else if (seatIds[p] > Integer.parseInt(seatsFileArr[q])) {
                q++;
            } else {
                count++;
                p++;
                q++;
            }
        }
        return count == seatIds.length;
    }

    // 根据场次id获取对应影院位置分布的json文件
    private JSONObject getJsonObjByFieldId(int fieldId) {
        // 1. 获取json文件路径（应该由cinema模块提供服务）
        HallInfoVO hallInfoVO = cinemaServiceAPI.getHallInfoByFieldId(fieldId);
        String seatPath = hallInfoVO.getSeatFile();

        // 2.从ftp服务器中获取json文件的String形式
        String fileStr = ftpUtils.getFileStrByAddress(seatPath);
        JSONObject jsonObject = JSONObject.parseObject(fileStr);
        return jsonObject;
    }

    // 订单总价计算+取精度
    private Double getOrderPrice(double price, int number) {
        BigDecimal bigDecimal = new BigDecimal(price * number);
        bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
