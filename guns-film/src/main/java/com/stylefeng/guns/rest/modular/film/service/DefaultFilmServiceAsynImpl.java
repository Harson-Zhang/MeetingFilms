package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.FilmServiceAsynAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = FilmServiceAsynAPI.class, loadbalance = "roundrobin", filter = "tracing")
@Component
public class DefaultFilmServiceAsynImpl implements FilmServiceAsynAPI {
    @Autowired
    MoocBannerTMapper bannerTMapper;
    @Autowired
    MoocFilmTMapper moocFilmTMapper;
    @Autowired
    MoocCatDictTMapper moocCatDictTMapper;
    @Autowired
    MoocSourceDictTMapper moocSourceDictTMapper;
    @Autowired
    MoocYearDictTMapper moocYearDictTMapper;
    @Autowired
    MoocFilmInfoTMapper moocFilmInfoTMapper;
    @Autowired
    MoocActorTMapper moocActorTMapper;
    @Autowired
    MoocFilmActorTMapper moocFilmActorTMapper;

    /**
     * 获取轮播图信息
     *
     * @return 轮播图
     */
    @Override
    public List<BannerVo> getBanners() {
        List<BannerVo> res = new ArrayList<>();
        List<MoocBannerT> bannerTS = bannerTMapper.selectList(null);
        for (MoocBannerT bannerT : bannerTS) {
            BannerVo bannerVo = new BannerVo();
            bannerVo.setBannerUrl(bannerT.getBannerUrl());
            bannerVo.setBannerId("" + bannerT.getUuid());
            bannerVo.setBannerAddress(bannerT.getBannerAddress());
            res.add(bannerVo);
        }
        return res;
    }

    /**
     * 获取正在热映的电影
     *
     * @param isLimit 限制数目
     * @param nums    数目
     * @return 热映电影s
     */
    @Override
    public FilmVo getHotFilms(boolean isLimit, int nums) {
        FilmVo filmVo = new FilmVo();

        //按isLimit查询电影。查询条件：film_status == 1
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", 1);
        // 查询热门电影的数量
        int count = moocFilmTMapper.selectCount(wrapper);

        List<MoocFilmT> filmTS = null;
        if (isLimit) {
            // 首页的影片数量会进行限制，采用分页进行限制
            Page<FilmInfoVo> page = new Page<>(1, nums);
            filmTS = moocFilmTMapper.selectPage(page, wrapper);

        } else {
            // 列表页的影片数量不做限制
            filmTS = moocFilmTMapper.selectList(wrapper);
        }
        List<FilmInfoVo> filmInfoVos = transform2FilmInfo(filmTS);

        filmVo.setFilmNum(count);
        filmVo.setFilmInfo(filmInfoVos);
        return filmVo;
    }

    /**
     * 即将上映的电影
     *
     * @param isLimit 限制数目
     * @param nums    数目
     * @return 即将上映的电影s
     */
    @Override
    public FilmVo getSoonFilms(boolean isLimit, int nums) {
        FilmVo filmVo = new FilmVo();

        //按isLimit查询电影。查询条件：film_status == 2
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", 2);
        // 查询即将上映电影的数量
        int count = moocFilmTMapper.selectCount(wrapper);

        List<MoocFilmT> filmTS = null;
        if (isLimit) {
            // 首页的影片数量会进行限制，采用分页进行限制
            Page<FilmInfoVo> page = new Page<>(1, nums);
            filmTS = moocFilmTMapper.selectPage(page, wrapper);
        } else {
            // 列表页的影片数量不做限制
            filmTS = moocFilmTMapper.selectList(wrapper);
        }
        List<FilmInfoVo> filmInfoVos = transform2FilmInfo(filmTS);

        filmVo.setFilmNum(count);
        filmVo.setFilmInfo(filmInfoVos);

        return filmVo;
    }

    //数据库表转vo
    private List<FilmInfoVo> transform2FilmInfo(List<MoocFilmT> filmTS) {
        List<FilmInfoVo> filmInfoVos = new ArrayList<>();
        for (MoocFilmT filmT : filmTS) {
            FilmInfoVo filmInfoVo = new FilmInfoVo();
            filmInfoVo.setFilmId(filmT.getUuid() + "");
            filmInfoVo.setFilmType(filmT.getFilmType());
            filmInfoVo.setImgAddress(filmT.getImgAddress());
            filmInfoVo.setFilmName(filmT.getFilmName());
            filmInfoVo.setFilmScore(filmT.getFilmScore());
            filmInfoVo.setExpectNum(filmT.getFilmPresalenum());
            filmInfoVo.setShowTime(DateUtil.getDay(filmT.getFilmTime()));
            filmInfoVo.setBoxNum(filmT.getFilmBoxOffice());
            filmInfoVo.setScore(filmT.getFilmScore());
            filmInfoVos.add(filmInfoVo);
        }
        return filmInfoVos;
    }

    /**
     * 票房排行榜（前10名）
     *
     * @return 排行榜上的电影信息
     */
    @Override
    public List<FilmInfoVo> getBoxRanking() {
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", 1);
        wrapper.orderBy("film_box_office", false);
        Page<MoocFilmT> page = new Page<>(1, 10);
        List<MoocFilmT> filmTS = moocFilmTMapper.selectPage(page, wrapper);

        return transform2FilmInfo(filmTS);
    }

    /**
     * 获取受欢迎的榜单（前10名）
     *
     * @return 受欢迎的电影信息
     */
    @Override
    public List<FilmInfoVo> getExpectRanking() {
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", 2);
        wrapper.orderBy("film_preSaleNum", false);
        Page<MoocFilmT> page = new Page<>(1, 10);
        List<MoocFilmT> filmTS = moocFilmTMapper.selectPage(page, wrapper);

        return transform2FilmInfo(filmTS);
    }

    /**
     * 获取前10个经典电影的信息
     *
     * @return 评分前10的经典电影的信息
     */
    @Override
    public List<FilmInfoVo> getTop() {
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status", 3);
        wrapper.orderBy("film_score", false);
        Page<MoocFilmT> page = new Page<>(1, 10);
        List<MoocFilmT> filmTS = moocFilmTMapper.selectPage(page, wrapper);

        return transform2FilmInfo(filmTS);
    }

    @Override
    public String getBiography(int filmId) {
        MoocFilmInfoT moocFilmInfoT = moocFilmInfoTMapper.selectById(filmId);
        return moocFilmInfoT.getBiography();
    }

    @Override
    public FilmActorsVO getDirectorAndActors(int filmId) {
        FilmActorsVO filmActorsVO = new FilmActorsVO();
        // 选出导演
        MoocFilmInfoT moocFilmInfoT = moocFilmInfoTMapper.selectById(filmId);
        MoocActorT moocActorT  = moocActorTMapper.selectById(moocFilmInfoT.getDirectorId());

        ActorVO directorVO = new ActorVO();
        directorVO.setDirectorName(moocActorT.getActorName());
        directorVO.setImgAddress(moocActorT.getActorImg());
        filmActorsVO.setDirector(directorVO);

        // 选出所有演员
        List<ActorVO> actorVOS = moocFilmActorTMapper.getActorsByFilmId(filmId);
        filmActorsVO.setActors(actorVOS);

        return filmActorsVO;
    }

    @Override
    public ImgsVO getImgs(int filmId) {
        MoocFilmInfoT moocFilmInfoT = moocFilmInfoTMapper.selectById(filmId);
        String filmImgs = moocFilmInfoT.getFilmImgs();
        String [] imgArray = filmImgs.split(",");

        ImgsVO imgsVO = new ImgsVO();
        imgsVO.setMainImg(imgArray[0]);
        imgsVO.setImg01(imgArray[1]);
        imgsVO.setImg02(imgArray[2]);
        imgsVO.setImg03(imgArray[3]);
        imgsVO.setImg04(imgArray[4]);

        return imgsVO;
    }
}
