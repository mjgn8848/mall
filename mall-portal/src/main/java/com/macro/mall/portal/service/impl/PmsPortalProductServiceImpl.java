package com.macro.mall.portal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.PmsPortalProductDetail;
import com.macro.mall.portal.domain.PmsProductCategoryNode;
import com.macro.mall.portal.service.PmsPortalProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 前台订单管理Service实现类（简化版）
 */
@Service
public class PmsPortalProductServiceImpl implements PmsPortalProductService {
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsProductLadderMapper productLadderMapper;
    @Autowired
    private PmsProductFullReductionMapper productFullReductionMapper;
    @Autowired
    private PortalProductDao portalProductDao;

    @Override
    public IPage<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort) {
        Page<PmsProduct> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("delete_status", 0).eq("publish_status", 1);
        if (StrUtil.isNotEmpty(keyword)) {
            wrapper.like("name", keyword);
        }
        if (brandId != null) {
            wrapper.eq("brand_id", brandId);
        }
        if (productCategoryId != null) {
            wrapper.eq("product_category_id", productCategoryId);
        }
        if (sort == 1) {
            wrapper.orderByDesc("id");
        } else if (sort == 2) {
            wrapper.orderByDesc("sale");
        } else if (sort == 3) {
            wrapper.orderByAsc("price");
        } else if (sort == 4) {
            wrapper.orderByDesc("price");
        }
        return productMapper.selectPage(page, wrapper);
    }

    @Override
    public List<PmsProductCategoryNode> categoryTreeList() {
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        List<PmsProductCategory> allList = productCategoryMapper.selectByExample(example);
        List<PmsProductCategoryNode> result = allList.stream()
                .filter(item -> item.getParentId().equals(0L))
                .map(item -> covert(item, allList))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public PmsPortalProductDetail detail(Long id) {
        PmsPortalProductDetail result = new PmsPortalProductDetail();
        PmsProduct product = productMapper.selectByPrimaryKey(id);
        result.setProduct(product);
        PmsBrand brand = brandMapper.selectByPrimaryKey(product.getBrandId());
        result.setBrand(brand);
        PmsProductAttributeValueExample attributeValueExample = new PmsProductAttributeValueExample();
        attributeValueExample.createCriteria().andProductIdEqualTo(product.getId());
        List<PmsProductAttributeValue> productAttributeValueList = productAttributeValueMapper.selectByExample(attributeValueExample);
        result.setProductAttributeValueList(productAttributeValueList);
        PmsSkuStockExample skuExample = new PmsSkuStockExample();
        skuExample.createCriteria().andProductIdEqualTo(product.getId());
        List<PmsSkuStock> skuStockList = skuStockMapper.selectByExample(skuExample);
        result.setSkuStockList(skuStockList);
        if (product.getPromotionType() == 3) {
            PmsProductLadderExample ladderExample = new PmsProductLadderExample();
            ladderExample.createCriteria().andProductIdEqualTo(product.getId());
            List<PmsProductLadder> productLadderList = productLadderMapper.selectByExample(ladderExample);
            result.setProductLadderList(productLadderList);
        }
        if (product.getPromotionType() == 4) {
            PmsProductFullReductionExample fullReductionExample = new PmsProductFullReductionExample();
            fullReductionExample.createCriteria().andProductIdEqualTo(product.getId());
            List<PmsProductFullReduction> productFullReductionList = productFullReductionMapper.selectByExample(fullReductionExample);
            result.setProductFullReductionList(productFullReductionList);
        }
        result.setCouponList(portalProductDao.getAvailableCouponList(product.getId(), product.getProductCategoryId()));
        return result;
    }

    private PmsProductCategoryNode covert(PmsProductCategory item, List<PmsProductCategory> allList) {
        PmsProductCategoryNode node = new PmsProductCategoryNode();
        BeanUtils.copyProperties(item, node);
        List<PmsProductCategoryNode> children = allList.stream()
                .filter(subItem -> subItem.getParentId().equals(item.getId()))
                .map(subItem -> covert(subItem, allList)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }
}
