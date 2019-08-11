package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HallTypeVO implements Serializable {
    private int halltypeId;
    private String halltypeName;
    private boolean isActive = false; //默认全不选
}
