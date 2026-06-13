-- 商城简化版数据库表清理脚本
-- 执行此脚本将删除不需要的数据库表

SET FOREIGN_KEY_CHECKS = 0;

-- =====================
-- 1. CMS内容管理相关表（已移除）
-- =====================
DROP TABLE IF EXISTS `cms_help`;
DROP TABLE IF EXISTS `cms_help_category`;
DROP TABLE IF EXISTS `cms_member_report`;
DROP TABLE IF EXISTS `cms_prefrence_area`;
DROP TABLE IF EXISTS `cms_prefrence_area_product_relation`;
DROP TABLE IF EXISTS `cms_subject`;
DROP TABLE IF EXISTS `cms_subject_category`;
DROP TABLE IF EXISTS `cms_subject_comment`;
DROP TABLE IF EXISTS `cms_subject_product_relation`;
DROP TABLE IF EXISTS `cms_topic`;
DROP TABLE IF EXISTS `cms_topic_category`;
DROP TABLE IF EXISTS `cms_topic_comment`;

-- =====================
-- 2. 秒杀相关表（已移除）
-- =====================
DROP TABLE IF EXISTS `sms_flash_promotion`;
DROP TABLE IF EXISTS `sms_flash_promotion_log`;
DROP TABLE IF EXISTS `sms_flash_promotion_product_relation`;
DROP TABLE IF EXISTS `sms_flash_promotion_session`;

-- =====================
-- 3. 会员等级/积分/标签/任务相关表（已移除）
-- =====================
DROP TABLE IF EXISTS `ums_growth_change_history`;
DROP TABLE IF EXISTS `ums_integration_change_history`;
DROP TABLE IF EXISTS `ums_integration_consume_setting`;
DROP TABLE IF EXISTS `ums_member_level`;
DROP TABLE IF EXISTS `ums_member_login_log`;
DROP TABLE IF EXISTS `ums_member_member_tag_relation`;
DROP TABLE IF EXISTS `ums_member_product_category_relation`;
DROP TABLE IF EXISTS `ums_member_statistics_info`;
DROP TABLE IF EXISTS `ums_member_tag`;
DROP TABLE IF EXISTS `ums_member_task`;

-- =====================
-- 4. 商品价格体系/审核记录相关表（已移除）
-- =====================
DROP TABLE IF EXISTS `pms_member_price`;
DROP TABLE IF EXISTS `pms_product_ladder`;
DROP TABLE IF EXISTS `pms_product_full_reduction`;
DROP TABLE IF EXISTS `pms_product_vertify_record`;
DROP TABLE IF EXISTS `pms_product_operate_log`;

-- =====================
-- 5. 权限系统简化相关表（已移除）
-- =====================
DROP TABLE IF EXISTS `ums_menu`;
DROP TABLE IF EXISTS `ums_permission`;
DROP TABLE IF EXISTS `ums_resource`;
DROP TABLE IF EXISTS `ums_resource_category`;
DROP TABLE IF EXISTS `ums_role`;
DROP TABLE IF EXISTS `ums_role_menu_relation`;
DROP TABLE IF EXISTS `ums_role_permission_relation`;
DROP TABLE IF EXISTS `ums_role_resource_relation`;
DROP TABLE IF EXISTS `ums_admin_permission_relation`;
DROP TABLE IF EXISTS `ums_admin_role_relation`;

-- =====================
-- 6. 其他可移除的表
-- =====================
DROP TABLE IF EXISTS `pms_comment`;
DROP TABLE IF EXISTS `pms_comment_replay`;
DROP TABLE IF EXISTS `pms_album`;
DROP TABLE IF EXISTS `pms_album_pic`;
DROP TABLE IF EXISTS `pms_feight_template`;

SET FOREIGN_KEY_CHECKS = 1;

-- 清理完成
-- 保留的核心表：
-- 商品相关：pms_brand, pms_product, pms_product_category, pms_product_attribute, pms_product_attribute_category, pms_product_attribute_value, pms_product_category_attribute_relation, pms_sku_stock
-- 订单相关：oms_order, oms_order_item, oms_cart_item, oms_order_return_apply, oms_order_return_reason, oms_order_setting, oms_company_address, oms_order_operate_history
-- 会员相关：ums_admin, ums_admin_login_log, ums_member, ums_member_receive_address
-- 促销相关：sms_coupon, sms_coupon_history, sms_coupon_product_relation, sms_coupon_product_category_relation, sms_home_advertise, sms_home_brand, sms_home_new_product, sms_home_recommend_product, sms_home_recommend_subject