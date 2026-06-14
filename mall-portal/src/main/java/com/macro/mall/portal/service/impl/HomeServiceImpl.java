package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.HomeContentResult;
import com.macro.mall.portal.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页内容管理Service实现类（简化版 - 直接从主表查询推荐状态）
 */
@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    private SmsHomeAdvertiseMapper advertiseMapper;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;

    @Override
    public HomeContentResult content() {
        HomeContentResult result = new HomeContentResult();
        result.setAdvertiseList(getHomeAdvertiseList());
        result.setNewProductList(getNewProductList());
        result.setHotProductList(getHotProductList());
        return result;
    }

    @Override
    public List<PmsProduct> recommendProductList(Integer pageSize, Integer pageNum) {
        Page<PmsProduct> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("delete_status", 0).eq("publish_status", 1);
        return productMapper.selectPage(page, wrapper).getRecords();
    }

    @Override
    public List<PmsProductCategory> getProductCateList(Long parentId) {
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria().andShowStatusEqualTo(1).andParentIdEqualTo(parentId);
        example.setOrderByClause("sort desc");
        return productCategoryMapper.selectByExample(example);
    }

    @Override
    public List<CmsSubject> getSubjectList(Long cateId, Integer pageSize, Integer pageNum) {
        return null;
    }

    @Override
    public List<PmsProduct> hotProductList(Integer pageNum, Integer pageSize) {
        return getHotProductList();
    }

    @Override
    public List<PmsProduct> newProductList(Integer pageNum, Integer pageSize) {
        return getNewProductList();
    }

    private List<SmsHomeAdvertise> getHomeAdvertiseList() {
        SmsHomeAdvertiseExample example = new SmsHomeAdvertiseExample();
        example.createCriteria().andTypeEqualTo(1).andStatusEqualTo(1);
        example.setOrderByClause("sort desc");
        return advertiseMapper.selectByExample(example);
    }

    /**
     * 获取新品推荐：直接查商品表 new_status=1、publish_status=1 的前4条
     */
    private List<PmsProduct> getNewProductList() {
        QueryWrapper<PmsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("new_status", 1).eq("publish_status", 1).eq("delete_status", 0)
               .orderByDesc("id").last("LIMIT 4");
        return productMapper.selectList(wrapper);
    }

    /**
     * 获取人气推荐：直接查商品表 recommand_status=1、publish_status=1 的前4条
     */
    private List<PmsProduct> getHotProductList() {
        QueryWrapper<PmsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("recommand_status", 1).eq("publish_status", 1).eq("delete_status", 0)
               .orderByDesc("sale").last("LIMIT 4");
        return productMapper.selectList(wrapper);
    }
}
