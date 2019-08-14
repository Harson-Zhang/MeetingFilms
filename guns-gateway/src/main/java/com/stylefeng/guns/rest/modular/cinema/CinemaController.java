package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cinema")
public class CinemaController {
    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = CinemaServiceAPI.class, cache = "lru", check = false)
    CinemaServiceAPI cinemaServiceAPI;

    /**
     * 查询影院列表-根据条件查询所有影院
     * @param cinemaRequestVO
     * @return
     */
    @RequestMapping("/getCinemas")
    public ResponseVO getCinemas(CinemaRequestVO cinemaRequestVO){
        Map<String, Object> cinemas = new HashMap<>(2);

        Page<CinemaVO> cinemaVOPage = cinemaServiceAPI.getCinemas(cinemaRequestVO);
        if (cinemaVOPage.getRecords() == null || cinemaVOPage.getRecords().size() == 0){
            return ResponseVO.serviceFail("没有符合条件的影院");
        } else{
            cinemas.put("cinemas", cinemaVOPage.getRecords());
            return ResponseVO.success(cinemas, cinemaVOPage.getCurrent(), (int)cinemaVOPage.getTotal(), "");
        }
    }

    /**
     * 获取影院列表查询条件
     * @param brandId
     * @param hallType
     * @param areaId
     * @return
     */
    @RequestMapping(value = "/getCondition")
    public ResponseVO getCondition(@RequestParam(defaultValue = "99", required = false) int brandId,
                                   @RequestParam(defaultValue = "99", required = false) int hallType,
                                   @RequestParam(defaultValue = "99", required = false) int areaId){
        try{
            List<BrandVO> brandList = cinemaServiceAPI.getBrandList(brandId);
            List<HallTypeVO> hallTypeList = cinemaServiceAPI.getHallTypeList(hallType);
            List<AreaVO> areaList = cinemaServiceAPI.getAreaList(areaId);

            Map resultMap = new HashMap(4);
            resultMap.put("brandList", brandList);
            resultMap.put("halltypeList", hallTypeList);
            resultMap.put("areaList", areaList);

            return ResponseVO.success(resultMap);
        }catch(Exception e){
            log.error("获取影院列表查询条件失败", e);
            return ResponseVO.serviceFail("获取影院列表查询条件失败");
        }
    }

    /**
     * 获取播放场次
     * @param cinemaId
     * @return
     */
    @RequestMapping(value = "/getFields")
    public ResponseVO getFields(int cinemaId){
        CinemaInfoVO cinemaInfo = cinemaServiceAPI.getCinemaInfo(cinemaId);
        List<FilmInfoVO> filmList = cinemaServiceAPI.getFilmInfoByCinemaId(cinemaId);

        Map resultMap = new HashMap(2);
        resultMap.put("cinemaInfo", cinemaInfo);
        resultMap.put("filmList", filmList);

        return ResponseVO.success(resultMap, IMG_PRE);
    }

    /**
     * 获取场次详细信息
     * @param cinemaId
     * @param fieldId
     * @return
     */
    @RequestMapping(value = "/getFieldInfo", method = RequestMethod.POST)
    public ResponseVO getFieldInfo(int cinemaId, int fieldId){
        CinemaInfoVO cinemaInfo = cinemaServiceAPI.getCinemaInfo(cinemaId);
        FilmInfoVO filmInfo = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
        HallInfoVO hallInfo = cinemaServiceAPI.getHallInfoByFieldId(fieldId);

        Map resultMap = new HashMap(4);
        resultMap.put("cinemaInfo", cinemaInfo);
        resultMap.put("filmInfo", filmInfo);
        resultMap.put("hallInfo", hallInfo);
        return ResponseVO.success(resultMap, IMG_PRE);
    }
}
