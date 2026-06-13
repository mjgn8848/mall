package com.macro.mall.portal.service.impl;

import com.macro.mall.portal.domain.WechatPayParam;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.WechatPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付Service实现
 * TODO: 需要集成微信支付SDK并完善实现
 */
@Service
public class WechatPayServiceImpl implements WechatPayService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatPayServiceImpl.class);

    @Autowired
    private OmsPortalOrderService portalOrderService;

    @Override
    public Object pay(WechatPayParam param) {
        // TODO: 调用微信支付API发起支付
        // 返回支付参数供小程序调用
        Map<String, Object> result = new HashMap<>();
        result.put("outTradeNo", param.getOutTradeNo());
        result.put("totalAmount", param.getTotalAmount());
        LOGGER.info("发起微信支付: {}", param);
        return result;
    }

    @Override
    public void notify(String result) {
        // TODO: 处理微信支付回调
        LOGGER.info("微信支付回调: {}", result);
        // 更新订单状态
        // portalOrderService.paySuccessByOrderSn(orderSn, 2); // 2表示微信支付
    }
}