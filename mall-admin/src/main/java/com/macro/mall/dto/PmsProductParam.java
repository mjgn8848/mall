package com.macro.mall.dto;

import com.macro.mall.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 创建和修改商品的请求参数（简化版）
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PmsProductParam extends PmsProduct{
    @Schema(title = "商品的sku库存信息")
    private List<PmsSkuStock> skuStockList;
}