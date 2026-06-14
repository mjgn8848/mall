package com.macro.mall.portal.domain;

import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsSkuStock;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 促销商品信息（简化版 - 只保留SKU库存）
 */
@Getter
@Setter
public class PromotionProduct extends PmsProduct {
    private List<PmsSkuStock> skuStockList;
}
