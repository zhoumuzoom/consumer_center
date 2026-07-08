-- ============================================================
-- CMI 要客运营平台 - 客户管理模块 建表脚本
-- 数据库：zhoumu @ 119.23.9.44:3306
-- MySQL版本：5.7.31
-- ============================================================

-- -----------------------------------------------------------
-- 1. 客户表 (customer)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `customer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_code` VARCHAR(50) NOT NULL COMMENT '客户编号',
  `customer_name` VARCHAR(200) NOT NULL COMMENT '客户名称',
  `customer_name_en` VARCHAR(200) DEFAULT NULL COMMENT '客户英文名称',
  `industry` VARCHAR(100) DEFAULT NULL COMMENT '所属行业',
  `region` VARCHAR(100) DEFAULT NULL COMMENT '所属区域',
  `scale` VARCHAR(50) DEFAULT NULL COMMENT '企业规模(大型/中型/小型)',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '看管状态(0:未看管,1:已看管,2:已剔除)',
  `manager_id` BIGINT DEFAULT NULL COMMENT '客户经理ID',
  `manager_name` VARCHAR(100) DEFAULT NULL COMMENT '客户经理姓名',
  `join_time` DATETIME DEFAULT NULL COMMENT '入库时间',
  `source` VARCHAR(50) DEFAULT NULL COMMENT '数据来源(139系统/出海系统/手动录入)',
  `contact_person` VARCHAR(100) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(50) DEFAULT NULL COMMENT '联系电话',
  `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '地址',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_code` (`customer_code`),
  KEY `idx_customer_name` (`customer_name`),
  KEY `idx_industry` (`industry`),
  KEY `idx_region` (`region`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- -----------------------------------------------------------
-- 2. 客户树节点表 (customer_tree_node)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `customer_tree_node` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `node_name` VARCHAR(200) NOT NULL COMMENT '节点名称',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父节点ID(0为根节点)',
  `level` INT NOT NULL DEFAULT 1 COMMENT '层级(1:一级,2:二级...)',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `node_type` VARCHAR(50) DEFAULT NULL COMMENT '节点类型(集团/区域/部门)',
  `responsibility` VARCHAR(500) DEFAULT NULL COMMENT '职责描述',
  `manager_id` BIGINT DEFAULT NULL COMMENT '责任人ID',
  `manager_name` VARCHAR(100) DEFAULT NULL COMMENT '责任人姓名',
  `customer_count` INT DEFAULT 0 COMMENT '下属客户数量',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`),
  KEY `idx_node_name` (`node_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户树节点表';

-- -----------------------------------------------------------
-- 3. 行业树节点表 (industry_tree_node)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `industry_tree_node` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `node_name` VARCHAR(200) NOT NULL COMMENT '行业名称',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父节点ID(0为根节点)',
  `level` INT NOT NULL DEFAULT 1 COMMENT '层级',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `node_code` VARCHAR(50) NOT NULL COMMENT '行业编码',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '行业描述',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_node_code` (`node_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行业树节点表';

-- -----------------------------------------------------------
-- 4. 客户行业标签表 (customer_industry_tag)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `customer_industry_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `industry_node_id` BIGINT NOT NULL COMMENT '行业节点ID',
  `tag_level` VARCHAR(50) DEFAULT NULL COMMENT '标签级别(战略要客/重点客户)',
  `cooperation_status` VARCHAR(50) DEFAULT NULL COMMENT '合作状态(合作中/洽谈中/已终止)',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_industry` (`customer_id`, `industry_node_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_industry_node_id` (`industry_node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户行业标签表';

-- -----------------------------------------------------------
-- 5. 决策关键人表 (decision_maker)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `decision_maker` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `position` VARCHAR(100) DEFAULT NULL COMMENT '职位',
  `department` VARCHAR(200) DEFAULT NULL COMMENT '部门',
  `phone` VARCHAR(50) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `role` VARCHAR(50) DEFAULT NULL COMMENT '角色(需求发起/方案审批/项目实施)',
  `company` VARCHAR(200) DEFAULT NULL COMMENT '所属公司',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_position` (`position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='决策关键人表';

-- -----------------------------------------------------------
-- 6. 决策链关系表 (decision_chain)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `decision_chain` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `decision_maker_id` BIGINT NOT NULL COMMENT '决策人ID',
  `project_name` VARCHAR(200) DEFAULT NULL COMMENT '项目名称',
  `decision_stage` VARCHAR(50) DEFAULT NULL COMMENT '决策阶段(需求发起/方案审批/项目实施)',
  `influence_level` VARCHAR(50) DEFAULT NULL COMMENT '影响力级别(高/中/低)',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_decision_maker_id` (`decision_maker_id`),
  KEY `idx_project_name` (`project_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='决策链关系表';

-- -----------------------------------------------------------
-- 7. 客户分支表 (customer_branch)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `customer_branch` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `branch_name` VARCHAR(200) NOT NULL COMMENT '分支名称',
  `region` VARCHAR(100) DEFAULT NULL COMMENT '所在区域',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '详细地址',
  `contact_person` VARCHAR(100) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(50) DEFAULT NULL COMMENT '联系电话',
  `business_scope` VARCHAR(500) DEFAULT NULL COMMENT '业务范围',
  `parent_branch_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父分支ID(0为总部直属)',
  `employee_count` INT DEFAULT NULL COMMENT '员工人数',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_parent_branch_id` (`parent_branch_id`),
  KEY `idx_region` (`region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户分支表';

-- -----------------------------------------------------------
-- 8. 客户交互记录表 (customer_interaction)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `customer_interaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `interaction_type` VARCHAR(50) NOT NULL COMMENT '交互类型(沟通/方案提交/客户反馈/拜访/会议)',
  `interaction_content` TEXT DEFAULT NULL COMMENT '交互内容',
  `participant` VARCHAR(200) DEFAULT NULL COMMENT '参与人',
  `interaction_time` DATETIME DEFAULT NULL COMMENT '交互时间',
  `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL',
  `result` VARCHAR(500) DEFAULT NULL COMMENT '交互结果',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_interaction_type` (`interaction_type`),
  KEY `idx_interaction_time` (`interaction_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户交互记录表';

-- -----------------------------------------------------------
-- 9. 任务表 (task)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tree_node_id` BIGINT DEFAULT NULL COMMENT '客户树节点ID',
  `customer_id` BIGINT DEFAULT NULL COMMENT '客户ID',
  `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
  `task_type` VARCHAR(50) DEFAULT NULL COMMENT '任务类型(营销/运营)',
  `task_content` TEXT DEFAULT NULL COMMENT '任务内容',
  `priority` VARCHAR(20) DEFAULT '中' COMMENT '优先级(高/中/低)',
  `assignee_id` BIGINT DEFAULT NULL COMMENT '执行人ID',
  `assignee_name` VARCHAR(100) DEFAULT NULL COMMENT '执行人姓名',
  `status` VARCHAR(50) NOT NULL DEFAULT '待处理' COMMENT '任务状态(待处理/进行中/已完成/已取消)',
  `deadline` DATETIME DEFAULT NULL COMMENT '截止时间',
  `completed_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:正常,1:删除)',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tree_node_id` (`tree_node_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_assignee_id` (`assignee_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- -----------------------------------------------------------
-- 10. 操作日志表 (operation_log)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `entity_id` BIGINT NOT NULL COMMENT '实体ID',
  `entity_type` VARCHAR(50) NOT NULL COMMENT '实体类型(customer/tree_node/industry_node等)',
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型(CREATE/UPDATE/DELETE/STATUS_CHANGE)',
  `operation_desc` VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
  `before_data` TEXT DEFAULT NULL COMMENT '操作前数据(JSON)',
  `after_data` TEXT DEFAULT NULL COMMENT '操作后数据(JSON)',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名',
  `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_entity` (`entity_type`, `entity_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- -----------------------------------------------------------
-- 11. 集团上报记录表 (group_report_record)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `group_report_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `report_batch` VARCHAR(50) NOT NULL COMMENT '上报批次号',
  `report_type` VARCHAR(50) DEFAULT NULL COMMENT '上报类型(手动/定时)',
  `total_count` INT NOT NULL DEFAULT 0 COMMENT '总数据量',
  `success_count` INT NOT NULL DEFAULT 0 COMMENT '成功数量',
  `fail_count` INT NOT NULL DEFAULT 0 COMMENT '失败数量',
  `status` VARCHAR(50) NOT NULL DEFAULT '处理中' COMMENT '上报状态(处理中/成功/部分失败/失败)',
  `report_data` LONGTEXT DEFAULT NULL COMMENT '上报数据JSON',
  `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
  `report_time` DATETIME DEFAULT NULL COMMENT '上报时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_report_batch` (`report_batch`),
  KEY `idx_report_time` (`report_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团上报记录表';

-- -----------------------------------------------------------
-- 12. 定时上报配置表 (report_schedule_config)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `report_schedule_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用(0:禁用,1:启用)',
  `cron_expression` VARCHAR(50) DEFAULT NULL COMMENT 'Cron表达式',
  `include_abnormal` TINYINT NOT NULL DEFAULT 0 COMMENT '是否包含异常数据(0:不包含,1:包含)',
  `last_execute_time` DATETIME DEFAULT NULL COMMENT '上次执行时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时上报配置表';
