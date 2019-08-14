package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.MoocAreaDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocBrandDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MoocHallDictT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = CinemaServiceAPI.class, executes = 10)
@Component
public class DefaultCinemaServiceImpl implements CinemaServiceAPI {
    @Autowired
    MoocCinemaTMapper moocCinemaTMapper;
    @Autowired
    MoocBrandDictTMapper moocBrandDictTMapper;
    @Autowired
    MoocAreaDictTMapper moocAreaDictTMapper;
    @Autowired
    MoocHallDictTMapper moocHallDictTMapper;
    @Autowired
    MoocFieldTMapper moocFieldTMapper;
    @Autowired
    MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;

    @Override
    public Page<CinemaVO> getCinemas(CinemaRequestVO requestVO) {
        EntityWrapper<MoocCinemaT> cinemaWrapper = new EntityWrapper<>();
        if (requestVO.getBrandId() != 99) {
            cinemaWrapper.eq("brand_id", requestVO.getBrandId());
        }
        if (requestVO.getDistrictId() != 99) {
            cinemaWrapper.eq("area_id", requestVO.getDistrictId());
        }
        if (requestVO.getHallType() != 99) {
            cinemaWrapper.like("hall_ids", "%#" + requestVO.getHallType() + "#%");
        }

        Page<CinemaVO> page = new Page<>(requestVO.getNowPage(), requestVO.getPageSize());
        List<MoocCinemaT> moocCinemaTS = moocCinemaTMapper.selectPage(page, cinemaWrapper);
        int total = moocCinemaTMapper.selectCount(cinemaWrapper);

        List<CinemaVO> cinemaVOS = new ArrayList<>();
        for (MoocCinemaT cinemaT : moocCinemaTS) {
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(cinemaT.getUuid());
            cinemaVO.setMinimumPrice(cinemaT.getMinimumPrice() + "");
            cinemaVO.setCinemaName(cinemaT.getCinemaName());
            cinemaVO.setAddress(cinemaT.getCinemaAddress());
            cinemaVOS.add(cinemaVO);
        }

        Page<CinemaVO> resultPage = new Page<>();
        resultPage.setRecords(cinemaVOS);
        resultPage.setCurrent(requestVO.getNowPage());
        resultPage.setTotal(total / requestVO.getPageSize() + 1);
        return resultPage;
    }

    @Override
    public List<BrandVO> getBrandList(int brandId) {
        boolean flag = false; // 用于标记是否需要设置“全部”为激活
        MoocBrandDictT brandDictT = moocBrandDictTMapper.selectById(brandId);
        if (brandId == 99 || brandDictT == null || brandDictT.getUuid() == null) {
            flag = true;
        }

        List<MoocBrandDictT> brandDictTS = moocBrandDictTMapper.selectList(null);
        List<BrandVO> brandVOS = new ArrayList<>();
        for (MoocBrandDictT brand : brandDictTS) {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandName(brand.getShowName());
            brandVO.setBrandId(brand.getUuid());
            if (flag) {
                if (brand.getUuid() == 99) {
                    brandVO.setActive(true);
                }
            } else {
                if (brand.getUuid() == brandId) {
                    brandVO.setActive(true);
                }
            }
            brandVOS.add(brandVO);
        }

        return brandVOS;
    }

    @Override
    public List<AreaVO> getAreaList(int areaId) {
        boolean flag = false; // 用于标记是否需要设置“全部”为激活
        MoocAreaDictT areaDictT = moocAreaDictTMapper.selectById(areaId);
        if (areaId == 99 || areaDictT == null || areaDictT.getUuid() == null) {
            flag = true;
        }

        List<MoocAreaDictT> areaDictTS = moocAreaDictTMapper.selectList(null);
        List<AreaVO> areaVOS = new ArrayList<>();
        for (MoocAreaDictT area : areaDictTS) {
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid());
            if (flag) {
                if (area.getUuid() == 99) {
                    areaVO.setActive(true);
                }
            } else {
                if (area.getUuid() == areaId) {
                    areaVO.setActive(true);
                }
            }
            areaVOS.add(areaVO);
        }

        return areaVOS;
    }

    @Override
    public List<HallTypeVO> getHallTypeList(int halltypeId) {
        boolean flag = false; // 用于标记是否需要设置“全部”为激活
        MoocHallDictT hallDictT = moocHallDictTMapper.selectById(halltypeId);
        if (halltypeId == 99 || hallDictT == null || hallDictT.getUuid() == null) {
            flag = true;
        }

        List<MoocHallDictT> hallDictTS = moocHallDictTMapper.selectList(null);
        List<HallTypeVO> hallTypeVOS = new ArrayList<>();
        for (MoocHallDictT hall : hallDictTS) {
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHalltypeName(hall.getShowName());
            hallTypeVO.setHalltypeId(hall.getUuid());
            if (flag) {
                if (hall.getUuid() == 99) {
                    hallTypeVO.setActive(true);
                }
            } else {
                if (hall.getUuid() == halltypeId) {
                    hallTypeVO.setActive(true);
                }
            }
            hallTypeVOS.add(hallTypeVO);
        }

        return hallTypeVOS;
    }

    @Override
    public CinemaInfoVO getCinemaInfo(int cinemaId) {
        MoocCinemaT cinemaT = moocCinemaTMapper.selectById(cinemaId);
        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setImgUrl(cinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(cinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(cinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(cinemaT.getUuid());
        cinemaInfoVO.setCinemaAddress(cinemaT.getCinemaAddress());

        return cinemaInfoVO;
    }

    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {
        return moocFieldTMapper.getFilmInfos(cinemaId);
    }

    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {
        return moocHallFilmInfoTMapper.getFilmInfoByFieldId(fieldId);
    }

    @Override
    public HallInfoVO getHallInfoByFieldId(int fieldId) {
        return moocFieldTMapper.getHallInfoByFieldId(fieldId);
    }
}
