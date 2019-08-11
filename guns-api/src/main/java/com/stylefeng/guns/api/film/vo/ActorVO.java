package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 演员与导演vo
 */
@Data
public class ActorVO implements Serializable {
    private String imgAddress;
    private String directorName ;  //演员名
    private String roleName ;   //饰演人物
}
