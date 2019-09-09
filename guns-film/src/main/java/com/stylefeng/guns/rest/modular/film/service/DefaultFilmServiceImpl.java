package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = FilmServiceAPI.class, loadbalance = "roundrobin", filter = "tracing")
@Component
public class DefaultFilmServiceImpl implements FilmServiceAPI {
    @Autowired
    MoocBannerTMapper bannerTMapper;
    @Autowired
    MoocFilmTMapper moocFilmTMapper;
    @Autowired
    MoocCatDictTMapper moocCatDictTMapper;
    @Autowired
    MoocSourceDictTMapper moocSourceDictTMapper;
    @Autowired
    MoocYearDictTMapper moocYearDictTMapper;
    @Autowired
    MoocFilmInfoTMapper moocFilmInfoTMapper;
    @Autowired
    MoocActorTMapper moocActorTMapper;
    @Autowired
    MoocFilmActorTMapper moocFilmActorTMapper;

    @Override
    public List<CatVO> getCats() {
        List<MoocCatDictT> catDictTS = moocCatDictTMapper.selectList(null);

        List<CatVO> catVOS = new ArrayList<>();
        for (MoocCatDictT catT : catDictTS) {
            CatVO catVO = new CatVO();
            catVO.setCatId(catT.getUuid() + "");
            catVO.setCatName(catT.getShowName());
            catVOS.add(catVO);
        }
        return catVOS;
    }

    @Override
    public List<SourceVO> getSources() {
        List<MoocSourceDictT> sourceDictTS = moocSourceDictTMapper.selectList(null);

        List<SourceVO> sourceVOS = new ArrayList<>();
        for (MoocSourceDictT sourceT : sourceDictTS) {
            SourceVO sourceVO = new SourceVO();
            sourceVO.setSourceId(sourceT.getUuid() + "");
            sourceVO.setSourceName(sourceT.getShowName());
            sourceVOS.add(sourceVO);
        }
        return sourceVOS;
    }

    @Override
    public List<YearVO> getYears() {
        List<MoocYearDictT> YearDictTS = moocYearDictTMapper.selectList(null);

        List<YearVO> yearVOS = new ArrayList<>();
        for (MoocYearDictT yearT : YearDictTS) {
            YearVO yearVO = new YearVO();
            yearVO.setYearId(yearT.getUuid() + "");
            yearVO.setYearName(yearT.getShowName());
            yearVOS.add(yearVO);
        }
        return yearVOS;
    }

    @Override
    public FilmVo getHotFilms(int filmStatus, int sortId, int catId, int sourceId, int yearId, int nowPage, int pageSize) {
        EntityWrapper<MoocFilmT> filmTWrapper = new EntityWrapper<>();
        if (filmStatus > 0 && filmStatus < 4) {
            filmTWrapper.eq("film_status", filmStatus);
        } else {
            filmTWrapper.eq("film_status", 1);
        }

        if (catId != 99) {
            String catStr = "#" + catId + "#";
            filmTWrapper.like("film_cats", catStr);
        }
        if (sourceId != 99) {
            filmTWrapper.eq("source_id", sourceId);
        }
        if (yearId != 99) {
            filmTWrapper.eq("year_id", yearId);
        }

        String sort = sortId == 3 ? "film_score" : (sortId == 2 ? "film_time" : "film_box_office");
        filmTWrapper.orderBy(sort, false);

        Page<MoocFilmT> page = new Page<>(nowPage, pageSize);
        List<MoocFilmT> moocFilmTS = moocFilmTMapper.selectPage(page, filmTWrapper);
        int totalPage = moocFilmTMapper.selectCount(filmTWrapper);

        FilmVo filmVo = new FilmVo();
        List<FilmInfoVo> filmInfoVos = new ArrayList<>();
        for (MoocFilmT moocFilmT : moocFilmTS) {
            FilmInfoVo filmInfoVo = new FilmInfoVo();
            filmInfoVo.setFilmId(moocFilmT.getUuid() + "");
            filmInfoVo.setScore(moocFilmT.getFilmScore());
            filmInfoVo.setFilmType(moocFilmT.getFilmType());
            filmInfoVo.setFilmName(moocFilmT.getFilmName());
            filmInfoVo.setImgAddress(moocFilmT.getImgAddress());
            filmInfoVos.add(filmInfoVo);
        }
        filmVo.setFilmInfo(filmInfoVos);
        filmVo.setFilmNum(totalPage);
        return filmVo;
    }

    @Override
    public FilmDetailVO getFilmDetails(int searchType, String searchParam) {
        // searchType: 0表示按照编号查找，1表示按照名称查找
        FilmDetailVO filmDetailsById;
        if (searchType == 0){
            filmDetailsById = moocFilmTMapper.getFilmDetailsById(Integer.parseInt(searchParam));
        } else {
            filmDetailsById = moocFilmTMapper.getFilmDetailsByName(searchParam);
        }
        return filmDetailsById;
    }

/*    @Override
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
    }*/
}
