package com.stylefeng.guns.rest.modular.film.vo;

import com.stylefeng.guns.api.film.vo.CatVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmConditionVO implements Serializable {
    List<CatVO> catInfo;
    List<SourceVO> sourceInfo;
    List<YearVO> yearInfo;
}
