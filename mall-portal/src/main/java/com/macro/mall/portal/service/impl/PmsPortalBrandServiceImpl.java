package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.mapper.PmsBrandMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsBrand;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.portal.service.PmsPortalBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台品牌管理Service实现类（简化版）
 */
@Service
public class PmsPortalBrandServiceImpl implements PmsPortalBrandService {
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> recommendList(Integer pageNum, Integer pageSize) {
        QueryWrapper<PmsBrand> wrapper = new QueryWrapper<>();
        wrapper.eq("show_status", 1).orderByDesc("sort");
        Page<PmsBrand> page = new Page<>(pageNum, pageSize);
        return brandMapper.selectPage(page, wrapper).getRecords();
    }

    @Override
    public PmsBrand detail(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Override
    public CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize) {
        Page<PmsProduct> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("delete_status", 0).eq("publish_status", 1).eq("brand_id", brandId);
        IPage<PmsProduct> productPage = productMapper.selectPage(page, wrapper);
        return CommonPage.restPage(productPage);
    }
}
