package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaInfoVO implements Serializable {
    private int cinemaId;
    private String imgUrl;
    private String cinemaName;
    private String cinemaAddress;  //接口文档这里有错别字
    private String cinemaPhone;
}
