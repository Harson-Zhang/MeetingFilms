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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public M getData() {
        return data;
    }

    public void setData(M data) {
        this.data = data;
    }


}
