package com.stylefeng.guns.rest.modular.test.service;

import com.stylefeng.guns.core.mutidatasource.annotion.DataSource;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.modular.test.dao.TestMutiDSDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Harson
 * @version 1.0
 * @date 2019/9/5 11:03
 */
@Service
public class TestMutiDSService {
    @Autowired
    TestMutiDSDao testMutiDSDao;

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    @DataSource(name = "test")
    public void selectAll(){
        System.out.println(testMutiDSDao.selectAll());
    }

    public void selectOrder(){
        System.out.println(moocOrderTMapper.getOrderInfoById("29129sdde32eds92ws"));
    }
}
