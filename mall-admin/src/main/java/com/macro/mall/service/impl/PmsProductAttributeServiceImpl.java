package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.dao.PmsProductAttributeDao;
import com.macro.mall.dto.PmsProductAttributeParam;
import com.macro.mall.dto.ProductAttrInfo;
import com.macro.mall.mapper.PmsProductAttributeMapper;
import com.macro.mall.model.PmsProductAttribute;
import com.macro.mall.service.PmsProductAttributeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品属性管理Service实现类（MyBatis-Plus 简化版）
 */
@Service
public class PmsProductAttributeServiceImpl extends ServiceImpl<PmsProductAttributeMapper, PmsProductAttribute> implements PmsProductAttributeService {
    @Autowired
    private PmsProductAttributeDao productAttributeDao;

    @Override
    public IPage<PmsProductAttribute> getList(Integer type, Integer pageSize, Integer pageNum) {
        Page<PmsProductAttribute> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsProductAttribute> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type).orderByDesc("sort");
        return this.page(page, wrapper);
    }

    @Override
    public int create(PmsProductAttributeParam pmsProductAttributeParam) {
        PmsProductAttribute pmsProductAttribute = new PmsProductAttribute();
        BeanUtils.copyProperties(pmsProductAttributeParam, pmsProductAttribute);
        return this.baseMapper.insert(pmsProductAttribute);
    }

    @Override
    public int update(Long id, PmsProductAttributeParam productAttributeParam) {
        PmsProductAttribute pmsProductAttribute = new PmsProductAttribute();
        pmsProductAttribute.setId(id);
        BeanUtils.copyProperties(productAttributeParam, pmsProductAttribute);
        return this.baseMapper.updateById(pmsProductAttribute);
    }

    @Override
    public PmsProductAttribute getItem(Long id) {
        return this.getById(id);
    }

    @Override
    public int delete(List<Long> ids) {
        return this.baseMapper.deleteBatchIds(ids);
    }

    @Override
    public List<ProductAttrInfo> getProductAttrInfo(Long productCategoryId) {
        return productAttributeDao.getProductAttrInfo(productCategoryId);
    }
}
