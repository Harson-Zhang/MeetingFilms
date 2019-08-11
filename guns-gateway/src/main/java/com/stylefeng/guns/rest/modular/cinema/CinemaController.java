package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.CinemaRequestVO;
import com.stylefeng.guns.api.cinema.vo.CinemaVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cinema")
public class CinemaController {

    @Reference(interfaceClass = CinemaServiceAPI.class, check = false)
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
        cinemas.put("cinemas", cinemaVOPage.getRecords());

        return ResponseVO.success(cinemas, cinemaVOPage.getCurrent(), (int)cinemaVOPage.getTotal(), "");
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

        return null;
    }

    /**
     * 获取播放场次
     * @param cinemaId
     * @return
     */
    @RequestMapping(value = "/getFields")
    public ResponseVO getFields(int cinemaId){

        return null;
    }

    /**
     * 获取场次详细信息
     * @param cinemaId
     * @param fieldId
     * @return
     */
    @RequestMapping(value = "/getFieldInfo", method = RequestMethod.POST)
    public ResponseVO getFieldInfo(int cinemaId, int fieldId){

        return null;
    }
}
