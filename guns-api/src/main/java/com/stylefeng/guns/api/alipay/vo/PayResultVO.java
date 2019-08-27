package com.stylefeng.guns.api.alipay.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayResultVO implements Serializable {
    private String orderId;
    private int orderStatus;
    private String orderMsg;
}
