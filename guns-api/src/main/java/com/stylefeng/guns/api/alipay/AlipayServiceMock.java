package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.PayInfoVO;
import com.stylefeng.guns.api.alipay.vo.PayResultVO;

/**
 * Rpc调用失败后的容错机制
 */
public class AlipayServiceMock implements AlipayServiceAPI {
    @Override
    public PayInfoVO getQRCodeByOrderId(String orderId) {
        return null;
    }

    @Override
    public PayResultVO getPayResult(String orderId) {
        PayResultVO payResultVO = new PayResultVO();
        payResultVO.setOrderId(orderId);
        payResultVO.setOrderMsg("查询支付结果失败(Mock)！");
        return payResultVO;
    }
}
