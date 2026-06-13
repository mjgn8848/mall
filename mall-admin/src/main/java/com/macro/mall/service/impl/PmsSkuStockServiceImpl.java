package com.macro.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.service.PmsSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品SKU库存管理Service实现类（MyBatis-Plus）
 */
@Service
public class PmsSkuStockServiceImpl extends ServiceImpl<PmsSkuStockMapper, PmsSkuStock> implements PmsSkuStockService {

    @Override
    public List<PmsSkuStock> getList(Long pid, String keyword) {
        QueryWrapper<PmsSkuStock> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", pid);
        if (!StrUtil.isEmpty(keyword)) {
            wrapper.like("sku_code", keyword);
        }
        return this.list(wrapper);
    }

    @Override
    public int update(Long pid, List<PmsSkuStock> skuStockList) {
        List<PmsSkuStock> filterSkuList = skuStockList.stream()
                .filter(item -> pid.equals(item.getProductId()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(filterSkuList)) return 0;
        int count = 0;
        for (PmsSkuStock sku : filterSkuList) {
            if (sku.getId() == null) {
                // 新增，处理 sku 编码
                if (StrUtil.isEmpty(sku.getSkuCode())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    StringBuilder sb = new StringBuilder();
                    sb.append(sdf.format(new Date()));
                    sb.append(String.format("%04d", sku.getProductId()));
                    sku.setSkuCode(sb.toString());
                }
                count += this.baseMapper.insert(sku);
            } else {
                count += this.baseMapper.updateById(sku);
            }
        }
        return count > 0 ? 1 : 0;
    }
}
