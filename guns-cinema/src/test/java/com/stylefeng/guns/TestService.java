package com.stylefeng.guns;

import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocFieldTMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TestService extends GunsRestApplicationTests {
    @Autowired
    MoocFieldTMapper moocFieldTMapper;

    @Test
    public void test() {
        List<FilmInfoVO> filmInfos = moocFieldTMapper.getFilmInfos(1);
        System.out.println(filmInfos.toString());
    }

}
