package com.macro.mall.portal.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 微信支付请求参数
 */
@Data
public class WechatPayParam {
    /**
     * 商户订单号，商家自定义，保持唯一性
     */
    private String outTradeNo;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 订单总金额，单位为分
     */
    private Integer totalAmount;
}