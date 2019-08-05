package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.BannerVo;
import com.stylefeng.guns.api.film.vo.FilmInfoVo;
import com.stylefeng.guns.api.film.vo.FilmVo;

import java.util.List;

public interface FilmServiceAPI {

    /**
     * 获取轮播图信息
     * @return 轮播图
     */
    List<BannerVo> getBanners();

    /**
     * 获取正在热映的电影
     * @param isLimit 限制数目
     * @param nums 数目
     * @return 热映电影s
     */
    FilmVo getHotFilms(boolean isLimit, int nums);

    /**
     * 即将上映的电影
     * @param isLimit 限制数目
     * @param nums 数目
     * @return 即将上映的电影s
     */
    FilmVo getSoonFilms(boolean isLimit, int nums);

    /**
     * 票房排行榜
     * @return 排行榜上的电影信息
     */
    List<FilmInfoVo> getBoxRanking();

    /**
     * 获取受欢迎的榜单
     * @return 受欢迎的电影信息
     */
    List<FilmInfoVo> getExpectRanking();

    /**
     * 获取前100个电影的信息
     * @return 前100个电影的信息
     */
    List<FilmInfoVo> getTop();
}
