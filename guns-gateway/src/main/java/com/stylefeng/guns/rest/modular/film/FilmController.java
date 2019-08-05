package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.FilmIndexVo;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/film/")
public class FilmController {
    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmServiceAPI.class, check = false)
    FilmServiceAPI filmServiceAPI;

    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO getIndex() {
        FilmIndexVo indexVo = new FilmIndexVo();

        //获取banner信息
        indexVo.setBanners(filmServiceAPI.getBanners());
        //获取正在热映的电影
        indexVo.setHotFilms(filmServiceAPI.getHotFilms(true, 10));
        //即将上映的电影
        indexVo.setSoonFilms(filmServiceAPI.getSoonFilms(true, 10));
        //票房排行榜
        indexVo.setBoxRanking(filmServiceAPI.getBoxRanking());
        //获我受欢迎的榜单
        indexVo.setExpectRanking(filmServiceAPI.getExpectRanking());
        //获取Top100的前10
        indexVo.setTop100(filmServiceAPI.getTop());

        return ResponseVO.success(indexVo, IMG_PRE);
    }

    /**
     * 获取条件查询列表
     * @param catId
     * @param sourceId
     * @param yearId
     * @return
     */
    @RequestMapping(value = "getFilmCondition", method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(required = false, defaultValue = "99") int catId,
                                       @RequestParam(required = false, defaultValue = "99") int sourceId,
                                       @RequestParam(required = false, defaultValue = "99") int yearId){

        return null;
    }
}
