package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FieldInfoVo implements Serializable {
    private int uuid;
    private int cinemaId;
    private int filmId;
    private String beginTime;
    private String endTime;
    private int hallId;
    private String hallName;
    private int price;
}
