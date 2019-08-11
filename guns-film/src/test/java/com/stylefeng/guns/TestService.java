package com.stylefeng.guns;

import com.stylefeng.guns.api.film.vo.ActorVO;
import com.stylefeng.guns.api.film.vo.FilmDetailVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocFilmActorTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocFilmTMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TestService extends GunsRestApplicationTests{
    @Autowired
    MoocFilmTMapper moocFilmTMapper;
    @Autowired
    MoocFilmActorTMapper moocFilmActorTMapper;

    @Test
    public void test(){
        FilmDetailVO filmDetailsById = moocFilmTMapper.getFilmDetailsByName("药神");
        System.out.println(filmDetailsById.toString());
    }

    @Test
    public void test2(){
        List<ActorVO> filmActorsVO = moocFilmActorTMapper.getActorsByFilmId(2);
        System.out.println(filmActorsVO);
    }
}
