package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmIndexVo implements Serializable {
    private List<BannerVo> banners;
    private FilmVo hotFilms;
    private FilmVo soonFilms;
    private List<FilmInfoVo> boxRanking;
    private List<FilmInfoVo> expectRanking;
    private List<FilmInfoVo> top100;
}
