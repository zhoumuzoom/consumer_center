-- 删除各业务表的 version（乐观锁版本号）字段
-- 数据库：iwhalecloud (MySQL 5.7)
-- 说明：共 12 张表含有 version 字段；
--       group_report_record、operation_log、report_schedule_config、system_user 无该字段，无需处理

ALTER TABLE `customer`               DROP COLUMN `version`;
ALTER TABLE `customer_branch`        DROP COLUMN `version`;
ALTER TABLE `customer_industry_tag`  DROP COLUMN `version`;
ALTER TABLE `customer_interaction`   DROP COLUMN `version`;
ALTER TABLE `customer_order`         DROP COLUMN `version`;
ALTER TABLE `customer_policy`        DROP COLUMN `version`;
ALTER TABLE `customer_product`       DROP COLUMN `version`;
ALTER TABLE `customer_tree_node`     DROP COLUMN `version`;
ALTER TABLE `decision_chain`         DROP COLUMN `version`;
ALTER TABLE `decision_maker`         DROP COLUMN `version`;
ALTER TABLE `industry_tree_node`     DROP COLUMN `version`;
ALTER TABLE `task`                   DROP COLUMN `version`;
