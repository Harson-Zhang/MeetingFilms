package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * soonFilm和hotFilm的电影信息
 */
@Data
public class FilmVo implements Serializable {
    private int FilmNum;
    private List<FilmInfoVo> filmInfo;
}
