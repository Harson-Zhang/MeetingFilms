package com.stylefeng.guns.core.base.tips;

import lombok.extern.slf4j.Slf4j;

/**
 * 返回给前台的错误提示
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午5:05:22
 */
@Slf4j
public class ErrorTip extends Tip {

    public ErrorTip(int status, String msg) {
        super();
        if (status == 700){
            log.error("用户未认证！");
            this.status = 700;
            this.msg = "用户未认证！";
        } else {
            log.error("系统异常，状态码{}，信息{}", status, msg);
            this.status = 999;
            this.msg = "系统出现异常，请联系管理员";
        }
    }
}
