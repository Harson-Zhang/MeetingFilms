package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FieldInfoVo;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.OrderServiceAsyncAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.api.uid.UidGenAPI;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.utils.FTPUtils;
import lombok.extern.slf4j.Slf4j;
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

    @Reference(interfaceClass = UidGenAPI.class, filter = "tracing")
    UidGenAPI uidGenAPI;

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    @Autowired
    FTPUtils ftpUtils;

    @Override
    public boolean isTrueSeats(int fieldId, int[] seatIds) {
        // 1. 获取jsonObject, 取出影院位置信息
        JSONObject jsonObject = getJsonObjByFieldId(fieldId);
        String seatsFileStr = jsonObject.getString("ids");
        String[] seatsFileArr = seatsFileStr.split(",");

        // 2. 查看位置编号是否在json中存在（seatId）
        return areSeatsExist(seatIds, seatsFileArr);
    }

    @Override
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
                return false;
            }
        }
        return true;
    }

    @Override
    public OrderInfoVO createOrder(int fieldId, int[] seatIds, int userId, String seatsName) {
        String uuid = uidGenAPI.getUid() + "";  //Harson: 万一uid-provider宕掉了呢？
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
        moocOrderT.setOrderStatus(0);

        Integer insert = moocOrderTMapper.insert(moocOrderT);
        if (insert > 0) {
            //创建成功，返回OrderInfoVO
            OrderInfoVO orderInfoVO = moocOrderTMapper.getOrderInfoById(uuid);
            if (orderInfoVO != null && orderInfoVO.getOrderId() != null) {
                return orderInfoVO;
            }
        } else {
            log.error("数据库插入失败！");
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
