package com.stylefeng.guns.rest.modular.test.dao;

import com.stylefeng.guns.rest.modular.test.entity.Test;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Harson
 * @version 1.0
 * @date 2019/9/5 11:06
 */
@Mapper
public interface TestMutiDSDao {
    @Select("select * from test")
    List<Test> selectAll();
}
