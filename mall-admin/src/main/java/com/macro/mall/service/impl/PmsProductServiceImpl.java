package com.macro.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.dao.PmsProductDao;
import com.macro.mall.dto.PmsProductParam;
import com.macro.mall.dto.PmsProductQueryParam;
import com.macro.mall.dto.PmsProductResult;
import com.macro.mall.mapper.PmsProductAttributeValueMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductAttributeValue;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.service.PmsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品管理Service实现类（MyBatis-Plus）
 */
@Service
public class PmsProductServiceImpl extends ServiceImpl<PmsProductMapper, PmsProduct> implements PmsProductService {
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private PmsProductDao productDao;

    @Override
    public int create(PmsProductParam productParam) {
        // 创建商品
        PmsProduct product = productParam;
        product.setId(null);
        this.save(product);
        Long productId = product.getId();
        // 处理 sku 编码
        handleSkuStockCode(productParam.getSkuStockList(), productId);
        // 添加 sku 库存信息
        insertSkuStockList(productParam.getSkuStockList(), productId);
        // 添加商品参数及自定义规格属性
        insertProductAttributeValueList(productParam.getProductAttributeValueList(), productId);
        return 1;
    }

    private void handleSkuStockCode(List<PmsSkuStock> skuStockList, Long productId) {
        if (CollectionUtils.isEmpty(skuStockList)) return;
        for (int i = 0; i < skuStockList.size(); i++) {
            PmsSkuStock skuStock = skuStockList.get(i);
            if (StrUtil.isEmpty(skuStock.getSkuCode())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                StringBuilder sb = new StringBuilder();
                sb.append(sdf.format(new Date()));
                sb.append(String.format("%04d", productId));
                sb.append(String.format("%03d", i + 1));
                skuStock.setSkuCode(sb.toString());
            }
        }
    }

    private void insertSkuStockList(List<PmsSkuStock> skuStockList, Long productId) {
        if (CollectionUtils.isEmpty(skuStockList)) return;
        for (PmsSkuStock item : skuStockList) {
            item.setId(null);
            item.setProductId(productId);
            skuStockMapper.insert(item);
        }
    }

    private void insertProductAttributeValueList(List<PmsProductAttributeValue> list, Long productId) {
        if (CollectionUtils.isEmpty(list)) return;
        for (PmsProductAttributeValue item : list) {
            item.setId(null);
            item.setProductId(productId);
            productAttributeValueMapper.insert(item);
        }
    }

    @Override
    public PmsProductResult getUpdateInfo(Long id) {
        return productDao.getUpdateInfo(id);
    }

    @Override
    public int update(Long id, PmsProductParam productParam) {
        // 更新商品信息
        PmsProduct product = productParam;
        product.setId(id);
        this.updateById(product);
        // 修改 sku 库存信息
        handleUpdateSkuStockList(id, productParam);
        // 修改商品参数和自定义规格属性
        productAttributeValueMapper.delete(
            new QueryWrapper<PmsProductAttributeValue>().eq("product_id", id)
        );
        insertProductAttributeValueList(productParam.getProductAttributeValueList(), id);
        return 1;
    }

    private void handleUpdateSkuStockList(Long id, PmsProductParam productParam) {
        List<PmsSkuStock> currSkuList = productParam.getSkuStockList();
        if (CollUtil.isEmpty(currSkuList)) {
            // 无 sku，全部删除
            skuStockMapper.delete(new QueryWrapper<PmsSkuStock>().eq("product_id", id));
            return;
        }
        // 查询原有 sku
        List<PmsSkuStock> oriStuList = skuStockMapper.selectList(
            new QueryWrapper<PmsSkuStock>().eq("product_id", id)
        );
        List<PmsSkuStock> insertSkuList = currSkuList.stream().filter(item -> item.getId() == null).collect(Collectors.toList());
        List<PmsSkuStock> updateSkuList = currSkuList.stream().filter(item -> item.getId() != null).collect(Collectors.toList());
        List<Long> updateSkuIds = updateSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
        List<PmsSkuStock> removeSkuList = oriStuList.stream().filter(item -> !updateSkuIds.contains(item.getId())).collect(Collectors.toList());
        handleSkuStockCode(insertSkuList, id);
        handleSkuStockCode(updateSkuList, id);
        if (CollUtil.isNotEmpty(insertSkuList)) {
            insertSkuStockList(insertSkuList, id);
        }
        if (CollUtil.isNotEmpty(removeSkuList)) {
            List<Long> removeSkuIds = removeSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
            skuStockMapper.deleteBatchIds(removeSkuIds);
        }
        if (CollUtil.isNotEmpty(updateSkuList)) {
            for (PmsSkuStock pmsSkuStock : updateSkuList) {
                skuStockMapper.updateById(pmsSkuStock);
            }
        }
    }

    @Override
    public IPage<PmsProduct> list(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        Page<PmsProduct> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("delete_status", 0);
        if (productQueryParam.getPublishStatus() != null) {
            wrapper.eq("publish_status", productQueryParam.getPublishStatus());
        }
        if (!StrUtil.isEmpty(productQueryParam.getKeyword())) {
            wrapper.like("name", productQueryParam.getKeyword());
        }
        if (!StrUtil.isEmpty(productQueryParam.getProductSn())) {
            wrapper.like("product_sn", productQueryParam.getProductSn());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            wrapper.eq("product_category_id", productQueryParam.getProductCategoryId());
        }
        wrapper.orderByDesc("id");
        return this.page(page, wrapper);
    }

    @Override
    public int updatePublishStatus(List<Long> ids, Integer publishStatus) {
        LambdaUpdateWrapper<PmsProduct> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(PmsProduct::getId, ids);
        wrapper.set(PmsProduct::getPublishStatus, publishStatus);
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        LambdaUpdateWrapper<PmsProduct> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(PmsProduct::getId, ids);
        wrapper.set(PmsProduct::getRecommandStatus, recommendStatus);
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public int updateNewStatus(List<Long> ids, Integer newStatus) {
        LambdaUpdateWrapper<PmsProduct> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(PmsProduct::getId, ids);
        wrapper.set(PmsProduct::getNewStatus, newStatus);
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public int updateDeleteStatus(List<Long> ids, Integer deleteStatus) {
        LambdaUpdateWrapper<PmsProduct> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(PmsProduct::getId, ids);
        wrapper.set(PmsProduct::getDeleteStatus, deleteStatus);
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public List<PmsProduct> list(String keyword) {
        QueryWrapper<PmsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("delete_status", 0);
        if (!StrUtil.isEmpty(keyword)) {
            wrapper.like("name", keyword).or().like("product_sn", keyword);
        }
        return this.list(wrapper);
    }
}
