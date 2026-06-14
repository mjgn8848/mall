package com.macro.mall.portal.service.impl;

import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.CartPromotionItem;
import com.macro.mall.portal.domain.PromotionProduct;
import com.macro.mall.portal.service.OmsPromotionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 促销管理Service实现类（简化版 - 只支持单品促销）
 */
@Service
public class OmsPromotionServiceImpl implements OmsPromotionService {
    @Autowired
    private PortalProductDao portalProductDao;

    @Override
    public List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList) {
        Map<Long, List<OmsCartItem>> productCartMap = groupCartItemBySpu(cartItemList);
        List<PromotionProduct> promotionProductList = getPromotionProductList(cartItemList);
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();

        for (Map.Entry<Long, List<OmsCartItem>> entry : productCartMap.entrySet()) {
            Long productId = entry.getKey();
            PromotionProduct promotionProduct = getPromotionProductById(productId, promotionProductList);
            List<OmsCartItem> itemList = entry.getValue();

            for (OmsCartItem item : itemList) {
                CartPromotionItem cartPromotionItem = new CartPromotionItem();
                BeanUtils.copyProperties(item, cartPromotionItem);
                PmsSkuStock skuStock = getSkuStock(promotionProduct, item.getProductSkuId());
                if (skuStock != null) {
                    BigDecimal price = skuStock.getPrice();
                    cartPromotionItem.setPrice(price);
                    // 单品促销：原价减去促销价
                    if (skuStock.getPromotionPrice() != null && skuStock.getPromotionPrice().compareTo(BigDecimal.ZERO) > 0) {
                        cartPromotionItem.setPromotionMessage("单品促销");
                        cartPromotionItem.setReduceAmount(price.subtract(skuStock.getPromotionPrice()));
                    } else {
                        cartPromotionItem.setPromotionMessage("无优惠");
                        cartPromotionItem.setReduceAmount(BigDecimal.ZERO);
                    }
                    cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
                } else {
                    cartPromotionItem.setPromotionMessage("无优惠");
                    cartPromotionItem.setReduceAmount(BigDecimal.ZERO);
                }
                cartPromotionItemList.add(cartPromotionItem);
            }
        }
        return cartPromotionItemList;
    }

    private List<PromotionProduct> getPromotionProductList(List<OmsCartItem> cartItemList) {
        List<Long> productIdList = cartItemList.stream()
                .map(OmsCartItem::getProductId)
                .collect(Collectors.toList());
        return portalProductDao.getPromotionProductList(productIdList);
    }

    private Map<Long, List<OmsCartItem>> groupCartItemBySpu(List<OmsCartItem> cartItemList) {
        Map<Long, List<OmsCartItem>> productCartMap = new TreeMap<>();
        for (OmsCartItem cartItem : cartItemList) {
            productCartMap.computeIfAbsent(cartItem.getProductId(), k -> new ArrayList<>()).add(cartItem);
        }
        return productCartMap;
    }

    private PromotionProduct getPromotionProductById(Long productId, List<PromotionProduct> promotionProductList) {
        return promotionProductList.stream()
                .filter(p -> productId.equals(p.getId()))
                .findFirst()
                .orElse(null);
    }

    private PmsSkuStock getSkuStock(PromotionProduct promotionProduct, Long productSkuId) {
        if (promotionProduct == null || promotionProduct.getSkuStockList() == null) {
            return null;
        }
        return promotionProduct.getSkuStockList().stream()
                .filter(sku -> productSkuId.equals(sku.getId()))
                .findFirst()
                .orElse(null);
    }
}
