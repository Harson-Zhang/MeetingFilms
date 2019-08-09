package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

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
     * 获取电影详情
     * @param searchType
     * @param searchParam
     * @return
     */
    FilmDetailVO getFilmDetails(int searchType, String searchParam);

}
