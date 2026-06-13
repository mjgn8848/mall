package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.dao.PmsProductAttributeCategoryDao;
import com.macro.mall.dto.PmsProductAttributeCategoryItem;
import com.macro.mall.mapper.PmsProductAttributeCategoryMapper;
import com.macro.mall.model.PmsProductAttributeCategory;
import com.macro.mall.service.PmsProductAttributeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品属性分类管理Service实现类（MyBatis-Plus）
 */
@Service
public class PmsProductAttributeCategoryServiceImpl extends ServiceImpl<PmsProductAttributeCategoryMapper, PmsProductAttributeCategory> implements PmsProductAttributeCategoryService {
    @Autowired
    private PmsProductAttributeCategoryDao productAttributeCategoryDao;

    @Override
    public int create(String name) {
        PmsProductAttributeCategory productAttributeCategory = new PmsProductAttributeCategory();
        productAttributeCategory.setName(name);
        return this.save(productAttributeCategory) ? 1 : 0;
    }

    @Override
    public int update(Long id, String name) {
        PmsProductAttributeCategory productAttributeCategory = new PmsProductAttributeCategory();
        productAttributeCategory.setName(name);
        productAttributeCategory.setId(id);
        return this.updateById(productAttributeCategory) ? 1 : 0;
    }

    @Override
    public int delete(Long id) {
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    public PmsProductAttributeCategory getItem(Long id) {
        return this.getById(id);
    }

    @Override
    public IPage<PmsProductAttributeCategory> getList(Integer pageSize, Integer pageNum) {
        Page<PmsProductAttributeCategory> page = new Page<>(pageNum, pageSize);
        return this.page(page, null);
    }

    @Override
    public List<PmsProductAttributeCategoryItem> getListWithAttr() {
        return productAttributeCategoryDao.getListWithAttr();
    }
}
