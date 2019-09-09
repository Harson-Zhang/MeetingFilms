package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmServiceAPI {

    /**
     * 获取类型列表
     * @return
     */
    List<CatVO> getCats();

    /**
     * 获取片源列表
     * @return
     */
    List<SourceVO> getSources();

    /**
     * 获取年代列表
     * @return
     */
    List<YearVO> getYears();

    /**
     * 获取正在热映/即将上映/经典电影的电影（filmStatus判断， 条件查询）

     * @return 电影s
     */
    FilmVo getHotFilms(int filmStatus, int sortId, int catId, int sourceId, int yearId, int nowPage, int pageSize);

    /**
     * 获取电影主体详情
     * @param searchType
     * @param searchParam
     * @return
     */
    FilmDetailVO getFilmDetails(int searchType, String searchParam);

/*    *//**
     * 获取电影简介
     * @param filmId
     * @return
     *//*
    String getBiography(int filmId);

    *//**
     * 获取导演演员信息
     * @param filmId
     * @return
     *//*
    FilmActorsVO getDirectorAndActors(int filmId);

    *//**
     * 获取图集
     * @param filmId
     * @return
     *//*
    ImgsVO getImgs(int filmId);*/
}
