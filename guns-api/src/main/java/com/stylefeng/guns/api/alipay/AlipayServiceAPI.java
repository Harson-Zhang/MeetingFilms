package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.PayInfoVO;
import com.stylefeng.guns.api.alipay.vo.PayResultVO;

public interface AlipayServiceAPI {

    //根据订单ID获取支付二维码
    PayInfoVO getQRCodeByOrderId(String orderId);

    //根据订单ID获取支付结果
    PayResultVO getPayResult(String orderId);
}
