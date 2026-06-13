package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.WechatPayParam;

/**
 * 微信支付Service
 */
public interface WechatPayService {
    /**
     * 发起微信支付
     */
    Object pay(WechatPayParam param);

    /**
     * 微信支付回调处理
     */
    void notify(String result);
}