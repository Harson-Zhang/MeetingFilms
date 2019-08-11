package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

/**
 * 用于异步调用的接口
 */
public interface FilmServiceAsynAPI {

    /**
     * 获取电影简介
     * @param filmId
     * @return
     */
    String getBiography(int filmId);

    /**
     * 获取导演演员信息
     * @param filmId
     * @return
     */
    FilmActorsVO getDirectorAndActors(int filmId);

    /**
     * 获取图集
     * @param filmId
     * @return
     */
    ImgsVO getImgs(int filmId);
}
