package com.stylefeng.guns;

import com.stylefeng.guns.rest.modular.order.service.DefaultOrderServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestService extends GunsRestApplicationTests {
    @Autowired
    DefaultOrderServiceImpl orderService;

    @Test
    public void test() {
        // 主数据源
        String orderId = "3569625041434042369";
        System.out.println(orderService.paySuccess(orderId));

        // 从数据源

    }

}
