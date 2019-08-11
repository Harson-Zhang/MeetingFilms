package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.film.vo.ActorVO;
import com.stylefeng.guns.rest.common.persistence.model.MoocFilmActorT;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 影片与演员映射表 Mapper 接口
 * </p>
 *
 * @author Harson
 * @since 2019-08-10
 */
public interface MoocFilmActorTMapper extends BaseMapper<MoocFilmActorT> {

    List<ActorVO> getActorsByFilmId(int filmId);

}
