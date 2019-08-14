package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocHallFilmInfoT;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 影厅电影信息表 Mapper 接口
 * </p>
 *
 * @author Harson
 * @since 2019-08-11
 */
public interface MoocHallFilmInfoTMapper extends BaseMapper<MoocHallFilmInfoT> {
    FilmInfoVO getFilmInfoByFieldId(int fieldId);
}
