-- ============================================================
-- 客户画像(360°)功能增强 - 数据迁移脚本
-- 新建3张表 + 修改1张表
-- ============================================================

-- -----------------------------------------------------------
-- 1. 客户政策动态表 (customer_policy)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `customer_policy` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `policy_title` VARCHAR(300) NOT NULL COMMENT '政策标题',
  `policy_type` VARCHAR(50) DEFAULT NULL COMMENT '政策类型(产业/财税/贸易/金融/其他)',
  `policy_level` VARCHAR(50) DEFAULT NULL COMMENT '政策级别(国家/省级/市级/行业)',
  `issuing_authority` VARCHAR(200) DEFAULT NULL COMMENT '发布机构',
  `publish_date` DATETIME DEFAULT NULL COMMENT '发布日期',
  `effective_date` DATETIME DEFAULT NULL COMMENT '生效日期',
  `expire_date` DATETIME DEFAULT NULL COMMENT '失效日期',
  `policy_summary` TEXT DEFAULT NULL COMMENT '政策摘要',
  `impact_analysis` TEXT DEFAULT NULL COMMENT '对客户的影响分析',
  `source_url` VARCHAR(500) DEFAULT NULL COMMENT '政策来源链接',
  `status` VARCHAR(20) NOT NULL DEFAULT '有效' COMMENT '状态(有效/已过期/已废止)',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_policy_type` (`policy_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户政策动态表';

-- -----------------------------------------------------------
-- 2. 客户历史订单/项目表 (customer_order)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `customer_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `order_no` VARCHAR(100) DEFAULT NULL COMMENT '订单编号',
  `project_name` VARCHAR(300) NOT NULL COMMENT '项目名称',
  `project_type` VARCHAR(50) DEFAULT NULL COMMENT '项目类型(信息化/基建/咨询/运维/其他)',
  `order_amount` DECIMAL(15,2) DEFAULT NULL COMMENT '订单金额(万元)',
  `contract_date` DATETIME DEFAULT NULL COMMENT '签约日期',
  `delivery_date` DATETIME DEFAULT NULL COMMENT '交付日期',
  `order_status` VARCHAR(50) NOT NULL DEFAULT '执行中' COMMENT '订单状态(洽谈中/已签约/执行中/已交付/已验收/已终止)',
  `description` TEXT DEFAULT NULL COMMENT '项目描述',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_project_type` (`project_type`),
  KEY `idx_order_status` (`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户历史订单/项目表';

-- -----------------------------------------------------------
-- 3. 客户产品与方案表 (customer_product)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `customer_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `product_name` VARCHAR(300) NOT NULL COMMENT '产品/方案名称',
  `product_type` VARCHAR(50) DEFAULT NULL COMMENT '类型(产品/解决方案)',
  `category` VARCHAR(100) DEFAULT NULL COMMENT '分类(云计算/大数据/AI/安全/网络/办公/其他)',
  `usage_status` VARCHAR(50) NOT NULL DEFAULT '使用中' COMMENT '使用状态(使用中/已停用/评估中)',
  `start_date` DATETIME DEFAULT NULL COMMENT '启用日期',
  `end_date` DATETIME DEFAULT NULL COMMENT '停用日期',
  `annual_fee` DECIMAL(15,2) DEFAULT NULL COMMENT '年费(万元)',
  `description` TEXT DEFAULT NULL COMMENT '描述',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_product_type` (`product_type`),
  KEY `idx_usage_status` (`usage_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户产品与方案表';

-- -----------------------------------------------------------
-- 4. 客户分支表增加分支类型字段
-- -----------------------------------------------------------
ALTER TABLE `customer_branch` ADD COLUMN `branch_type` VARCHAR(50) DEFAULT '总部' COMMENT '分支类型(总部/海外分支/本地实体)' AFTER `parent_branch_id`;
