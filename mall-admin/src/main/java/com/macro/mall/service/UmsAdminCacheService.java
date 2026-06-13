package com.macro.mall.service;

import com.macro.mall.model.UmsAdmin;

/**
 * 后台用户缓存管理Service（简化版）
 */
public interface UmsAdminCacheService {
    /**
     * 删除后台用户缓存
     */
    void delAdmin(Long adminId);

    /**
     * 获取缓存后台用户信息
     */
    UmsAdmin getAdmin(String username);

    /**
     * 设置缓存后台用户信息
     */
    void setAdmin(UmsAdmin admin);
}