package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocFieldT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 放映场次表 Mapper 接口
 * </p>
 *
 * @author Harson
 * @since 2019-08-11
 */
public interface MoocFieldTMapper extends BaseMapper<MoocFieldT> {
    List<FilmInfoVO> getFilmInfos(@Param("cinemaId") int cinemaId);

    HallInfoVO getHallInfoByFieldId(@Param("fieldId") int fieldId);
}
