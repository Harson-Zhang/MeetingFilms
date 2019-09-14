package com.stylefeng.guns.api.alipay;

import com.alibaba.dubbo.rpc.RpcException;
import com.stylefeng.guns.api.alipay.vo.PayInfoVO;
import com.stylefeng.guns.api.alipay.vo.PayResultVO;

public class AlipayServiceStub implements AlipayServiceAPI {
    private final AlipayServiceAPI alipayServiceAPI;

    public AlipayServiceStub(AlipayServiceAPI alipayServiceAPI){
        this.alipayServiceAPI = alipayServiceAPI;
    }

    @Override
    public PayInfoVO getQRCodeByOrderId(String orderId) {
        return null;
    }

    @Override
    public PayResultVO getPayResult(String orderId) {
        try{
            return alipayServiceAPI.getPayResult(orderId);
        }catch (RpcException e){
            PayResultVO payResultVO = new PayResultVO();
            payResultVO.setOrderId(orderId);
            payResultVO.setOrderMsg("查询支付结果失败(Mock)！");
            return payResultVO;
        }
    }
}
