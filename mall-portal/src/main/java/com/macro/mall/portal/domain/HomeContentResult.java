package com.macro.mall.portal.domain;

import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.SmsHomeAdvertise;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 首页内容返回信息封装（简化版 - 去掉品牌推荐）
 */
@Getter
@Setter
public class HomeContentResult {
    @Schema(title = "轮播广告")
    private List<SmsHomeAdvertise> advertiseList;
    @Schema(title = "新品推荐")
    private List<PmsProduct> newProductList;
    @Schema(title = "人气推荐")
    private List<PmsProduct> hotProductList;
}