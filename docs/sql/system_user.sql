-- ============================================================
-- CMI 要客运营平台 - 用户登录模块 建表脚本
-- 数据库：iwhalecloud @ 119.23.9.44:3306
-- MySQL版本：5.7.31
-- ============================================================

-- -----------------------------------------------------------
-- 1. 系统用户表 (system_user)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `system_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `password` VARCHAR(128) NOT NULL COMMENT '密码(明文,后续迭代加密)',
  `external_id` VARCHAR(64) DEFAULT NULL COMMENT '外部平台(门户)用户唯一标识',
  `source` VARCHAR(32) NOT NULL DEFAULT 'local' COMMENT '账号来源(local:本地账号,portal:门户SSO)',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:启用,0:禁用)',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_external_id` (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- -----------------------------------------------------------
-- 2. 初始化测试账号 (admin / 123456)
-- -----------------------------------------------------------
INSERT INTO `system_user` (`username`, `password`, `real_name`, `status`)
SELECT 'admin', '123456', '系统管理员', 1
WHERE NOT EXISTS (SELECT 1 FROM `system_user` WHERE `username` = 'admin');
