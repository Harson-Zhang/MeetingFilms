package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.film.vo.FilmDetailVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocFilmT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 影片主表 Mapper 接口
 * </p>
 *
 * @author harson
 * @since 2019-08-04
 */
public interface MoocFilmTMapper extends BaseMapper<MoocFilmT> {
    FilmDetailVO getFilmDetailsById(@Param("id")int id);
    FilmDetailVO getFilmDetailsByName(@Param("filmName") String filmName);
}
