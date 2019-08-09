package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmInfoVo implements Serializable {
    private String filmId;
    private Integer filmType;
    private String imgAddress;
    private String filmName;
    private String filmScore;
    private Integer expectNum;  //期望票房（soonFilm、expectRanking）
    private String showTime ;  //上映时间（soonFilm）
    private Integer boxNum;  //票房数量（boxRanking）
    private String score;  //评分（top100）
}
