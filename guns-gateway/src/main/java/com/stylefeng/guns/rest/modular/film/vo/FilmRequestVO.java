package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;

/**
 * getFilm请求参数vo
 */
@Data
public class FilmRequestVO {
    private Integer showType = 1;   //上映情况
    private Integer sortId = 1;     //排序条件
    private Integer catId = 99;     //类型id
    private Integer sourceId = 99;  //片源id
    private Integer yearId = 99;    //年代id
    private Integer nowPage = 1;    //当前页
    private Integer pageSize = 18;  //每页数量
}
