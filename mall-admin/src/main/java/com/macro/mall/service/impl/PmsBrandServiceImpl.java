package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.dto.PmsBrandParam;
import com.macro.mall.mapper.PmsBrandMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsBrand;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.service.PmsBrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品品牌管理Service实现类（MyBatis-Plus）
 */
@Service
public class PmsBrandServiceImpl extends ServiceImpl<PmsBrandMapper, PmsBrand> implements PmsBrandService {
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> listAllBrand() {
        return this.list();
    }

    @Override
    public int createBrand(PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        // 如果创建时首字母为空，取名称的第一个为首字母
        if (StrUtil.isEmpty(pmsBrand.getFirstLetter())) {
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));
        }
        return this.save(pmsBrand) ? 1 : 0;
    }

    @Override
    public int updateBrand(Long id, PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        pmsBrand.setId(id);
        // 如果创建时首字母为空，取名称的第一个为首字母
        if (StrUtil.isEmpty(pmsBrand.getFirstLetter())) {
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));
        }
        // 更新品牌时要更新商品中的品牌名称
        productMapper.update(
            new PmsProduct().setBrandName(pmsBrand.getName()),
            new QueryWrapper<PmsProduct>().eq("brand_id", id)
        );
        return this.updateById(pmsBrand) ? 1 : 0;
    }

    @Override
    public int deleteBrand(Long id) {
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    public int deleteBrand(List<Long> ids) {
        return this.removeByIds(ids) ? 1 : 0;
    }

    @Override
    public IPage<PmsBrand> listBrand(String keyword, Integer showStatus, int pageNum, int pageSize) {
        Page<PmsBrand> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsBrand> wrapper = new QueryWrapper<>();
        if (!StrUtil.isEmpty(keyword)) {
            wrapper.like("name", keyword);
        }
        if (showStatus != null) {
            wrapper.eq("show_status", showStatus);
        }
        wrapper.orderByDesc("sort");
        return this.page(page, wrapper);
    }

    @Override
    public PmsBrand getBrand(Long id) {
        return this.getById(id);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        return this.update(
            new LambdaUpdateWrapper<PmsBrand>()
                .in(PmsBrand::getId, ids)
                .set(PmsBrand::getShowStatus, showStatus)
        ) ? 1 : 0;
    }

    @Override
    public int updateFactoryStatus(List<Long> ids, Integer factoryStatus) {
        return this.update(
            new LambdaUpdateWrapper<PmsBrand>()
                .in(PmsBrand::getId, ids)
                .set(PmsBrand::getFactoryStatus, factoryStatus)
        ) ? 1 : 0;
    }
}
