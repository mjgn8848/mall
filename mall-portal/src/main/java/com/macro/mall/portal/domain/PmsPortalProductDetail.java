package com.macro.mall.portal.domain;

import com.macro.mall.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 前台商品详情（简化版 - 去掉品牌/阶梯价/满减价）
 */
@Getter
@Setter
public class PmsPortalProductDetail {
    @Schema(title = "商品信息")
    private PmsProduct product;
    @Schema(title = "商品属性与参数值")
    private List<PmsProductAttributeValue> productAttributeValueList;
    @Schema(title = "商品的sku库存信息")
    private List<PmsSkuStock> skuStockList;
    @Schema(title = "商品可用优惠券")
    private List<SmsCoupon> couponList;
}
