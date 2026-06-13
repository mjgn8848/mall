package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.mapper.SmsCouponHistoryMapper;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.service.SmsCouponHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 优惠券领取记录管理Service实现类
 * Created by macro on 2018/11/6.
 */
@Service
public class SmsCouponHistoryServiceImpl implements SmsCouponHistoryService {
    @Autowired
    private SmsCouponHistoryMapper historyMapper;
    @Override
    public IPage<SmsCouponHistory> list(Long couponId, Integer useStatus, String orderSn, Integer pageSize, Integer pageNum) {
        Page<SmsCouponHistory> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SmsCouponHistory> wrapper = new QueryWrapper<>();
        if (couponId != null) {
            wrapper.eq("coupon_id", couponId);
        }
        if (useStatus != null) {
            wrapper.eq("use_status", useStatus);
        }
        if (!StrUtil.isEmpty(orderSn)) {
            wrapper.like("order_sn", orderSn);
        }
        return historyMapper.selectPage(page, wrapper);
    }
}
