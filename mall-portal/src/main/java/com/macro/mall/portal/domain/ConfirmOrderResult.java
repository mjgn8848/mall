package com.macro.mall.portal.domain;

import com.macro.mall.model.UmsMemberReceiveAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 确认单信息封装（简化版）
 */
@Getter
@Setter
public class ConfirmOrderResult {
    @Schema(title = "包含优惠信息的购物车信息")
    private List<CartPromotionItem> cartPromotionItemList;
    @Schema(title = "用户收货地址列表")
    private List<UmsMemberReceiveAddress> memberReceiveAddressList;
    @Schema(title = "用户可用优惠券列表")
    private List<SmsCouponHistoryDetail> couponHistoryDetailList;
    @Schema(title = "计算的金额")
    private CalcAmount calcAmount;

    @Getter
    @Setter
    public static class CalcAmount {
        @Schema(title = "订单商品总金额")
        private BigDecimal totalAmount;
        @Schema(title = "运费")
        private BigDecimal freightAmount;
        @Schema(title = "活动优惠")
        private BigDecimal promotionAmount;
        @Schema(title = "应付金额")
        private BigDecimal payAmount;
    }
}
