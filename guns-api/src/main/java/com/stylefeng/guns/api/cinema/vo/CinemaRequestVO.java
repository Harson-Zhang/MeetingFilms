package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaRequestVO implements Serializable {
    private int brandId = 99;
    private int hallType = 99;
    private int districtId = 99;
    private int pageSize = 12;
    private int nowPage = 1;
}
