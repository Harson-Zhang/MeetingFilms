package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderInfoVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.utils.FTPUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@Service(interfaceClass = OrderServiceAPI.class)
public class DefaultOrderServiceImpl implements OrderServiceAPI {
    @Reference(interfaceClass = CinemaServiceAPI.class, check = false)
    CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    @Autowired
    FTPUtils ftpUtils;

    @Override
    public boolean isTrueSeats(int fieldId, int[] seatIds) {
        // 1. 获取json文件路径（应该由cinema模块提供服务）
        HallInfoVO hallInfoVO = cinemaServiceAPI.getHallInfoByFieldId(fieldId);
        String seatPath = hallInfoVO.getSeatFile();

        // 2.从ftp服务器中获取json文件的String形式
        String fileStr = ftpUtils.getFileStrByAddress(seatPath);
        JSONObject jsonObject = JSONObject.parseObject(fileStr);
        String seatsFileStr = jsonObject.getString("ids");
        String[] seatsFileArr = seatsFileStr.split(",");

        // 3. 查看位置编号是否在json中存在（seatId）
        return areSeatsExist(seatIds, seatsFileArr);
    }

    @Override
    public boolean isNotSold(int fieldId, int[] seatIds) {
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("field_id",fieldId);
        List<MoocOrderT> orderTS = moocOrderTMapper.selectList(wrapper);
        HashSet<Integer> hashSet = new HashSet<>();
        String [] seatsIdArr;
        for(MoocOrderT orderT : orderTS){
            String seatsIdStr = orderT.getSeatsIds();
            seatsIdArr = seatsIdStr.split(",");
            for (String seat: seatsIdArr){
                hashSet.add(Integer.parseInt(seat));
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
    public OrderInfoVO createOrder(int fieldId, int[] seatIds, int userId, String seatName) {


        return null;
    }

    @Override
    public List<OrderInfoVO> getOrderByUser(int userId) {


        return null;
    }

    @Override
    public String getSoldSeatByFieldId(int fieldId) {
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

//    @Test
//    void test() {
//        int[] seatIds = {1, 2, 3};
//        String[] seatsFileArr = {"1","2", "3", "4"};
//        System.out.println(areSeatsExist(seatIds, seatsFileArr));
//    }
}
