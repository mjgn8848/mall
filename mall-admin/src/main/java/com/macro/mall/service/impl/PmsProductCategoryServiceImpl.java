package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.dao.PmsProductCategoryDao;
import com.macro.mall.dto.PmsProductCategoryParam;
import com.macro.mall.dto.PmsProductCategoryWithChildrenItem;
import com.macro.mall.mapper.PmsProductCategoryMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.service.PmsProductCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类管理Service实现类（MyBatis-Plus 简化版）
 */
@Service
public class PmsProductCategoryServiceImpl extends ServiceImpl<PmsProductCategoryMapper, PmsProductCategory> implements PmsProductCategoryService {
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryDao productCategoryDao;

    @Override
    public int create(PmsProductCategoryParam pmsProductCategoryParam) {
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setProductCount(0);
        BeanUtils.copyProperties(pmsProductCategoryParam, productCategory);
        setCategoryLevel(productCategory);
        return this.save(productCategory) ? 1 : 0;
    }

    @Override
    public int update(Long id, PmsProductCategoryParam pmsProductCategoryParam) {
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setId(id);
        BeanUtils.copyProperties(pmsProductCategoryParam, productCategory);
        setCategoryLevel(productCategory);
        productMapper.update(
            new PmsProduct().setProductCategoryName(productCategory.getName()),
            new QueryWrapper<PmsProduct>().eq("product_category_id", id)
        );
        return this.updateById(productCategory) ? 1 : 0;
    }

    @Override
    public IPage<PmsProductCategory> getList(Long parentId, Integer pageSize, Integer pageNum) {
        Page<PmsProductCategory> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsProductCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId).orderByDesc("sort");
        return this.page(page, wrapper);
    }

    @Override
    public int delete(Long id) {
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    public PmsProductCategory getItem(Long id) {
        return this.getById(id);
    }

    @Override
    public int updateNavStatus(List<Long> ids, Integer navStatus) {
        return this.update(
            new LambdaUpdateWrapper<PmsProductCategory>()
                .in(PmsProductCategory::getId, ids)
                .set(PmsProductCategory::getNavStatus, navStatus)
        ) ? 1 : 0;
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        return this.update(
            new LambdaUpdateWrapper<PmsProductCategory>()
                .in(PmsProductCategory::getId, ids)
                .set(PmsProductCategory::getShowStatus, showStatus)
        ) ? 1 : 0;
    }

    @Override
    public List<PmsProductCategoryWithChildrenItem> listWithChildren() {
        return productCategoryDao.listWithChildren();
    }

    private void setCategoryLevel(PmsProductCategory productCategory) {
        if (productCategory.getParentId() == 0) {
            productCategory.setLevel(0);
        } else {
            PmsProductCategory parentCategory = this.getById(productCategory.getParentId());
            if (parentCategory != null) {
                productCategory.setLevel(parentCategory.getLevel() + 1);
            } else {
                productCategory.setLevel(0);
            }
        }
    }
}
