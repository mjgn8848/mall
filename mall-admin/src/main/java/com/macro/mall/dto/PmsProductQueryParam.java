package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品查询参数（简化版）
 */
@Data
public class PmsProductQueryParam {
    @Schema(title = "上架状态")
    private Integer publishStatus;
    @Schema(title = "商品名称模糊关键字")
    private String keyword;
    @Schema(title = "商品货号")
    private String productSn;
    @Schema(title = "商品分类编号")
    private Long productCategoryId;
}
