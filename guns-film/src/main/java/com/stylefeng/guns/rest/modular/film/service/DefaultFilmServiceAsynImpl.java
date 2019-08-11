package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.FilmServiceAsynAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = FilmServiceAsynAPI.class, loadbalance = "roundrobin")
@Component
public class DefaultFilmServiceAsynImpl implements FilmServiceAsynAPI {

    @Autowired
    MoocFilmInfoTMapper moocFilmInfoTMapper;
    @Autowired
    MoocActorTMapper moocActorTMapper;
    @Autowired
    MoocFilmActorTMapper moocFilmActorTMapper;

    @Override
    public String getBiography(int filmId) {
        MoocFilmInfoT moocFilmInfoT = moocFilmInfoTMapper.selectById(filmId);
        return moocFilmInfoT.getBiography();
    }

    @Override
    public FilmActorsVO getDirectorAndActors(int filmId) {
        FilmActorsVO filmActorsVO = new FilmActorsVO();
        // 选出导演
        MoocFilmInfoT moocFilmInfoT = moocFilmInfoTMapper.selectById(filmId);
        MoocActorT moocActorT  = moocActorTMapper.selectById(moocFilmInfoT.getDirectorId());

        ActorVO directorVO = new ActorVO();
        directorVO.setDirectorName(moocActorT.getActorName());
        directorVO.setImgAddress(moocActorT.getActorImg());
        filmActorsVO.setDirector(directorVO);

        // 选出所有演员
        List<ActorVO> actorVOS = moocFilmActorTMapper.getActorsByFilmId(filmId);
        filmActorsVO.setActors(actorVOS);

        return filmActorsVO;
    }

    @Override
    public ImgsVO getImgs(int filmId) {
        MoocFilmInfoT moocFilmInfoT = moocFilmInfoTMapper.selectById(filmId);
        String filmImgs = moocFilmInfoT.getFilmImgs();
        String [] imgArray = filmImgs.split(",");

        ImgsVO imgsVO = new ImgsVO();
        imgsVO.setMainImg(imgArray[0]);
        imgsVO.setImg01(imgArray[1]);
        imgsVO.setImg02(imgArray[2]);
        imgsVO.setImg03(imgArray[3]);
        imgsVO.setImg04(imgArray[4]);

        return imgsVO;
    }
}
