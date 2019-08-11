package com.stylefeng.guns.rest.modular.film;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.FilmServiceAsynAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVo;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/film/")
public class FilmController {
    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmServiceAPI.class, check = false)
    FilmServiceAPI filmServiceAPI;

    @Reference(interfaceClass = FilmServiceAsynAPI.class, check = false, async = true)
    FilmServiceAsynAPI filmServiceAsynAPI;

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
     *
     * @param catId
     * @param sourceId
     * @param yearId
     * @return
     */
    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(required = false, defaultValue = "99") int catId,
                                       @RequestParam(required = false, defaultValue = "99") int sourceId,
                                       @RequestParam(required = false, defaultValue = "99") int yearId) {
        // 获取catVoList，进行isActive设置
        List<CatVO> cats = filmServiceAPI.getCats();
        if (catId != 99) {
            for (CatVO cat : cats) {
                cat.setActive(false);
                if (cat.getCatId().equals(catId + "")) {
                    cat.setActive(true);
                }
            }
        } else {
            for (CatVO cat : cats) {
                cat.setActive(true);
            }
        }

        // 获取sourceVoList，进行isActive设置
        List<SourceVO> sources = filmServiceAPI.getSources();
        if (sourceId != 99) {
            for (SourceVO source : sources) {
                source.setActive(false);
                if (source.getSourceId().equals(sourceId + "")) {
                    source.setActive(true);
                }
            }
        } else {
            for (SourceVO source : sources) {
                source.setActive(true);
            }
        }

        // 获取yearVoList，进行isActive设置
        List<YearVO> years = filmServiceAPI.getYears();
        if (yearId != 99) {
            for (YearVO year : years) {
                year.setActive(false);
                if (year.getYearId().equals(yearId + "")) {
                    year.setActive(true);
                }
            }
        } else {
            for (YearVO year : years) {
                year.setActive(true);
            }
        }

        FilmConditionVO conditionVO = new FilmConditionVO();
        conditionVO.setCatInfo(cats);
        conditionVO.setSourceInfo(sources);
        conditionVO.setYearInfo(years);

        ResponseVO<FilmConditionVO> responseVO = new ResponseVO<>();
        responseVO.setStatus(0);
        responseVO.setData(conditionVO);
        return responseVO;
    }

    /**
     * 影片查询接口
     *
     * @param filmRequestVO
     * @return
     */
    @RequestMapping(value = "/getFilms", method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO) {
        int showType = filmRequestVO.getShowType();
        FilmVo filmVo = null;
        filmVo = filmServiceAPI.getHotFilms(showType, filmRequestVO.getSortId(), filmRequestVO.getCatId(),
                filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                filmRequestVO.getNowPage(), filmRequestVO.getPageSize());

        int totalPage = filmVo.getFilmNum() / filmRequestVO.getPageSize() + 1;
        List filmInfoList = filmVo.getFilmInfo();
        return ResponseVO.success(filmInfoList, filmRequestVO.getNowPage(),totalPage, IMG_PRE);
    }

    /**
     * 影片详情查询
     *
     * @param searchType
     * @param searchParam
     * @return
     */
    @RequestMapping(value = "/films/{searchParam}", method = RequestMethod.GET)
    public ResponseVO getFilmDetails(int searchType, @PathVariable("searchParam") String searchParam) throws ExecutionException, InterruptedException {
        //获取电影信息
        FilmDetailVO filmDetailVO = filmServiceAPI.getFilmDetails(searchType, searchParam);
        if (filmDetailVO == null){
            return ResponseVO.serviceFail("查询失败，无影片可加载");
        }

        int filmId = Integer.parseInt(filmDetailVO.getFilmId());

        //获取导演、演员信息
        filmServiceAsynAPI.getBiography(filmId);
        Future<String> biographyFuture = RpcContext.getContext().getFuture();

        filmServiceAsynAPI.getDirectorAndActors(filmId);
        Future<FilmActorsVO> filmActorsFuture = RpcContext.getContext().getFuture();

        //获取图集
        filmServiceAsynAPI.getImgs(filmId);
        Future<ImgsVO> imgsFuture = RpcContext.getContext().getFuture();

        Info04VO info04VO = new Info04VO();
        info04VO.setActors(filmActorsFuture.get());
        info04VO.setBiography(biographyFuture.get());
        filmDetailVO.setInfo04(info04VO);
        filmDetailVO.setImgs(imgsFuture.get());

        return ResponseVO.success(filmDetailVO, IMG_PRE);
    }
}
