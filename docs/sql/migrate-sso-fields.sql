-- ============================================================
-- CMI 要客运营平台 - 用户登录模块 SSO 对接预留字段
-- 数据库：iwhalecloud @ 119.23.9.44:3306
-- MySQL版本：5.7.31
-- 说明：为门户网站 SSO 免登录对接预留外部身份映射字段。
--       external_id 存门户侧用户唯一标识，source 标记账号来源。
--       现有本地账号 external_id 为 NULL（MySQL 唯一索引允许多个 NULL）。
-- ============================================================

ALTER TABLE `system_user`
  ADD COLUMN `external_id` VARCHAR(64) DEFAULT NULL COMMENT '外部平台(门户)用户唯一标识' AFTER `password`,
  ADD COLUMN `source` VARCHAR(32) NOT NULL DEFAULT 'local' COMMENT '账号来源(local:本地账号,portal:门户SSO)' AFTER `external_id`,
  ADD UNIQUE KEY `uk_external_id` (`external_id`);
