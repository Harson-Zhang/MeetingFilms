package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class Info04VO implements Serializable {
    private String biography;
    private FilmActorsVO actors;
}
