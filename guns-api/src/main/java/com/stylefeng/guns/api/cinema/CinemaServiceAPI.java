package com.stylefeng.guns.api.cinema;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.vo.*;

import java.util.List;

public interface CinemaServiceAPI {
    //1、根据CinemaRequestVO，查询影院列表（分页）
    Page<CinemaVO> getCinemas(CinemaRequestVO requestVO);

    //2、获取品牌列表
    List<BrandVO> getBrandList(int brandId);

    //3、获取行政区域列表
    List<AreaVO> getAreaList(int areaId);

    //4、获取影厅类型列表
    List<HallTypeVO> getHallTypeList(int halltypeId);

    //5、根据影院编号，获取影院信息
    CinemaInfoVO getCinemaInfo(int cinemaId);

    //6、获取所有电影的信息和对应的放映场次信息，根据影院编号
    List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId);

    //7、根据放映场次ID获取放映信息
    FilmInfoVO getFilmInfoByFieldId(int fieldId);

    //8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    HallInfoVO getHallInfoByFieldId(int fieldId);

    //9、根据fieldId获取场次信息
    FieldInfoVo getFieldInfoByFieldId(int fieldId);
}
