package com.stylefeng.guns.rest.modular.vo;

import lombok.Data;


/**
 * 用户登录的相应体（参照接口文档）
 */
@Data
public class ResponseVO<M> {

    private int status; //返回状态
    private String msg; //信息
    private M data; //实体数据
    private String imgPre; //图片地址
    private int nowPage; //当前页
    private int totalPage; //总页数

    //响应成功
    public static<M> ResponseVO success(M data){
        ResponseVO vo = new ResponseVO();
        vo.setData(data);
        vo.setStatus(0);
        return vo;
    }

    //响应成功后返回提示
    public static ResponseVO success(String msg){
        ResponseVO vo = new ResponseVO();
        vo.setMsg(msg);
        vo.setStatus(0);
        return vo;
    }

    //首页响应成功后返回
    public static <M> ResponseVO success(M data, String imgPre){
        ResponseVO vo = new ResponseVO();
        vo.setData(data);
        vo.setStatus(0);
        vo.setImgPre(imgPre);
        return vo;
    }

    //首页响应成功后返回
    public static <M> ResponseVO success(M data, int nowPage, int totalPage, String imgPre){
        ResponseVO vo = new ResponseVO();
        vo.setData(data);
        vo.setNowPage(nowPage);
        vo.setTotalPage(totalPage);
        vo.setStatus(0);
        vo.setImgPre(imgPre);
        return vo;
    }

    //业务异常
    public static ResponseVO serviceFail(String msg){
        ResponseVO vo = new ResponseVO();
        vo.setMsg(msg);
        vo.setStatus(1);
        return vo;
    }

    //系统异常
    public static ResponseVO appFail(String msg){
        ResponseVO vo = new ResponseVO();
        vo.setMsg(msg);
        vo.setStatus(999);
        return vo;
    }
}
