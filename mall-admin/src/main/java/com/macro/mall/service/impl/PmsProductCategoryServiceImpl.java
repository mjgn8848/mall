package com.macro.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.dao.PmsProductCategoryAttributeRelationDao;
import com.macro.mall.dao.PmsProductCategoryDao;
import com.macro.mall.dto.PmsProductCategoryParam;
import com.macro.mall.dto.PmsProductCategoryWithChildrenItem;
import com.macro.mall.mapper.PmsProductCategoryAttributeRelationMapper;
import com.macro.mall.mapper.PmsProductCategoryMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.PmsProductCategoryAttributeRelation;
import com.macro.mall.service.PmsProductCategoryService;
import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类管理Service实现类（MyBatis-Plus）
 */
@Service
public class PmsProductCategoryServiceImpl extends ServiceImpl<PmsProductCategoryMapper, PmsProductCategory> implements PmsProductCategoryService {
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryAttributeRelationMapper productCategoryAttributeRelationMapper;
    @Autowired
    private PmsProductCategoryDao productCategoryDao;

    @Override
    public int create(PmsProductCategoryParam pmsProductCategoryParam) {
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setProductCount(0);
        BeanUtils.copyProperties(pmsProductCategoryParam, productCategory);
        // 没有父分类时为一级分类
        setCategoryLevel(productCategory);
        boolean success = this.save(productCategory);
        // 创建筛选属性关联
        List<Long> productAttributeIdList = pmsProductCategoryParam.getProductAttributeIdList();
        if (!CollUtil.isEmpty(productAttributeIdList)) {
            insertRelationList(productCategory.getId(), productAttributeIdList);
        }
        return success ? 1 : 0;
    }

    /**
     * 批量插入商品分类与筛选属性关系表
     */
    private void insertRelationList(Long productCategoryId, List<Long> productAttributeIdList) {
        List<PmsProductCategoryAttributeRelation> relationList = new ArrayList<>();
        for (Long productAttrId : productAttributeIdList) {
            PmsProductCategoryAttributeRelation relation = new PmsProductCategoryAttributeRelation();
            relation.setProductAttributeId(productAttrId);
            relation.setProductCategoryId(productCategoryId);
            relationList.add(relation);
        }
        // 直接逐条插入（MP insert）
        for (PmsProductCategoryAttributeRelation r : relationList) {
            productCategoryAttributeRelationMapper.insert(r);
        }
    }

    @Override
    public int update(Long id, PmsProductCategoryParam pmsProductCategoryParam) {
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setId(id);
        BeanUtils.copyProperties(pmsProductCategoryParam, productCategory);
        setCategoryLevel(productCategory);
        // 更新商品分类时要更新商品中的名称
        productMapper.update(
            new PmsProduct().setProductCategoryName(productCategory.getName()),
            new QueryWrapper<PmsProduct>().eq("product_category_id", id)
        );
        // 同时更新筛选属性信息
        productCategoryAttributeRelationMapper.delete(
            new QueryWrapper<PmsProductCategoryAttributeRelation>().eq("product_category_id", id)
        );
        if (!CollUtil.isEmpty(pmsProductCategoryParam.getProductAttributeIdList())) {
            insertRelationList(id, pmsProductCategoryParam.getProductAttributeIdList());
        }
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

    /**
     * 根据分类的parentId设置分类的level
     */
    private void setCategoryLevel(PmsProductCategory productCategory) {
        // 没有父分类时为一级分类
        if (productCategory.getParentId() == 0) {
            productCategory.setLevel(0);
        } else {
            // 有父分类时选择根据父分类level设置
            PmsProductCategory parentCategory = this.getById(productCategory.getParentId());
            if (parentCategory != null) {
                productCategory.setLevel(parentCategory.getLevel() + 1);
            } else {
                productCategory.setLevel(0);
            }
        }
    }
}
