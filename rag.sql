/*
 Navicat Premium Data Transfer

 Source Server         : test
 Source Server Type    : MySQL
 Source Server Version : 80024
 Source Host           : localhost:3306
 Source Schema         : rag

 Target Server Type    : MySQL
 Target Server Version : 80024
 File Encoding         : 65001

 Date: 02/05/2023 13:07:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for db_info
-- ----------------------------
DROP TABLE IF EXISTS `db_info`;
CREATE TABLE `db_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `dbName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据库名',
  `dbType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据库类型',
  `driver` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'driver',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '地址',
  `host` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主机',
  `port` int NOT NULL COMMENT '端口',
  `property` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'db参数',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_info
-- ----------------------------
INSERT INTO `db_info` VALUES (13, 'test', 'myservice', 'MYSQL', NULL, 'jdbc:mysql://localhost:3306/myservice', 'localhost', 3306, NULL, 'root', '123456', 1, '2023-04-21 15:04:45', '2023-04-21 15:04:45', 0);

-- ----------------------------
-- Table structure for dict_info
-- ----------------------------
DROP TABLE IF EXISTS `dict_info`;
CREATE TABLE `dict_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '词库名称',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '词库内容（json 数组）',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '词库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dict_info
-- ----------------------------
INSERT INTO `dict_info` VALUES (1, 'abcd', '[\"123\",\"234\",\"345\",\"456\"]', 1, '2023-04-02 11:15:46', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (2, 'abcd', '[\"123\",\"234\",\"345\",\"456\"]', 1, '2023-04-02 11:15:46', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (3, 'abcdfe', '[\"12315d\",\"1534\",\"865\",\"4\",\"35\",\"31\"]', 1, '2023-04-17 16:14:16', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (4, 'abcdfe', '[\"12315d\",\"1534\",\"865\",\"4\",\"35\",\"31\"]', 1, '2023-04-17 16:14:16', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (5, 'abcdfe', '[\"12315d\",\"1534\",\"865\",\"4\",\"35\",\"31\"]', 1, '2023-04-17 16:14:16', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (6, 'abcdfe', '[\"12315d\",\"1534\",\"865\",\"4\",\"35\",\"31\"]', 1, '2023-04-17 16:14:16', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (7, 'abcdfe', '[\"12315d\",\"1534\",\"865\",\"4\",\"35\",\"31\"]', 1, '2023-04-17 16:14:16', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (8, 'abcdfe', '[\"12315d\",\"1534\",\"865\",\"4\",\"35\",\"31\"]', 1, '2023-04-17 16:14:16', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (9, 'abcdfe', '[\"12315d\",\"1534\",\"865\",\"4\",\"35\",\"31\"]', 1, '2023-04-17 16:14:16', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (10, 'abcdfe', '[\"12315d\",\"1534\",\"865\",\"4\",\"35\",\"31\"]', 1, '2023-04-17 16:14:16', '2023-04-20 17:48:57', 0);
INSERT INTO `dict_info` VALUES (11, 'abcdfe', '[\"12315d\",\"1534\",\"865\",\"4\",\"35\",\"31\"]', 1, '2023-04-17 16:14:16', '2023-04-20 17:48:57', 0);

-- ----------------------------
-- Table structure for field_info
-- ----------------------------
DROP TABLE IF EXISTS `field_info`;
CREATE TABLE `field_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `fieldName` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字段名称',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字段信息（json）',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_fieldName`(`fieldName`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字段信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of field_info
-- ----------------------------
INSERT INTO `field_info` VALUES (1, '创建时间', 'create_time', '{\"fieldName\":\"create_time\",\"comment\":\"创建时间\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"fieldType\":\"datetime\",\"mockType\":\"不模拟\",\"notNull\":true,\"primaryKey\":false,\"autoIncrement\":false}', 1, '2023-04-03 08:57:30', '2023-04-20 17:50:41', 0);
INSERT INTO `field_info` VALUES (2, '123', 'id', '{\"id\":\"rkEib3Tl6e6Hk3QPWs7DK\",\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"}', 1, '2023-04-18 14:27:55', '2023-04-20 17:50:41', 0);
INSERT INTO `field_info` VALUES (3, '123', 'id', '{\"id\":\"\",\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"}', 1, '2023-04-18 14:30:27', '2023-04-20 17:50:41', 0);
INSERT INTO `field_info` VALUES (4, '1231', 'id', '{\"id\":\"vj__e6Pem_uURDjd7Ezmz\",\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"}', 1, '2023-04-18 14:32:59', '2023-04-20 17:50:41', 0);
INSERT INTO `field_info` VALUES (5, '1231', 'id', '{\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"}', 1, '2023-04-18 14:33:38', '2023-04-20 17:50:41', 0);
INSERT INTO `field_info` VALUES (6, '创建时间', 'create_time', '{\"fieldName\":\"create_time\",\"fieldType\":\"DATETIME\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"}', 1, '2023-04-18 14:37:11', '2023-04-20 17:50:41', 0);
INSERT INTO `field_info` VALUES (7, 'id', 'id', '{\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"}', 1, '2023-04-18 15:08:07', '2023-04-18 15:08:07', 0);
INSERT INTO `field_info` VALUES (8, 'id', 'id', '{\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"}', 1, '2023-04-18 15:08:07', '2023-04-18 15:08:07', 0);
INSERT INTO `field_info` VALUES (9, 'id', 'id', '{\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"}', 1, '2023-04-18 15:08:07', '2023-04-18 15:08:07', 0);
INSERT INTO `field_info` VALUES (10, 'id', 'id', '{\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"}', 1, '2023-04-18 15:08:07', '2023-04-18 15:08:07', 0);
INSERT INTO `field_info` VALUES (11, 'id', 'id', '{\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"}', 1, '2023-04-18 15:08:07', '2023-04-18 15:08:07', 0);

-- ----------------------------
-- Table structure for job_info
-- ----------------------------
DROP TABLE IF EXISTS `job_info`;
CREATE TABLE `job_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `dbId` bigint NOT NULL COMMENT 'dbInfoid',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `status` tinyint NOT NULL COMMENT '作业状态(0=未开始,1=执行中,2=已完成,3=已撤销,4=作业失败)',
  `finishedNum` int UNSIGNED NOT NULL COMMENT '已完成条数',
  `mockNum` int UNSIGNED NOT NULL COMMENT '模拟条数',
  `dbName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据库名',
  `dbType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据库类型',
  `host` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主机',
  `port` int NOT NULL COMMENT '端口',
  `property` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'db参数',
  `tableName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表名',
  `exception` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '失败原因',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_info
-- ----------------------------
INSERT INTO `job_info` VALUES (18, 13, '{\"dbName\":\"\",\"tableName\":\"123\",\"tableComment\":\"\",\"mockNum\":1000000,\"metaFieldList\":[{\"fieldName\":\"id\",\"fieldType\":\"bigint\",\"defaultValue\":\"\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"NONE\",\"mockParams\":\"0\"},{\"fieldName\":\"create_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"update_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"更新时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"is_deleted\",\"fieldType\":\"tinyint\",\"defaultValue\":\"0\",\"notNull\":false,\"comment\":\"删除标记\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"FIXED\",\"mockParams\":\"0\"}]}', 3, 10000, 10000, 'myservice', 'MYSQL', 'localhost', 3306, NULL, '123', NULL, 1, '2023-04-23 02:26:51', '2023-04-23 03:28:23', 0);
INSERT INTO `job_info` VALUES (19, 13, '{\"dbName\":\"\",\"tableName\":\"123\",\"tableComment\":\"\",\"mockNum\":1000000,\"metaFieldList\":[{\"fieldName\":\"id\",\"fieldType\":\"bigint\",\"defaultValue\":\"\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"NONE\",\"mockParams\":\"0\"},{\"fieldName\":\"create_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"update_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"更新时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"is_deleted\",\"fieldType\":\"tinyint\",\"defaultValue\":\"0\",\"notNull\":false,\"comment\":\"删除标记\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"FIXED\",\"mockParams\":\"0\"}]}', 2, 10000, 10000, 'myservice', 'MYSQL', 'localhost', 3306, NULL, '123', NULL, 1, '2023-04-23 02:26:53', '2023-04-23 03:28:27', 0);
INSERT INTO `job_info` VALUES (20, 13, '{\"dbName\":\"\",\"tableName\":\"123\",\"tableComment\":\"\",\"mockNum\":1,\"metaFieldList\":[{\"fieldName\":\"id\",\"fieldType\":\"bigint\",\"defaultValue\":\"\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"NONE\",\"mockParams\":\"0\"},{\"fieldName\":\"create_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"update_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"更新时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"is_deleted\",\"fieldType\":\"tinyint\",\"defaultValue\":\"0\",\"notNull\":false,\"comment\":\"删除标记\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"FIXED\",\"mockParams\":\"0\"},{\"fieldName\":\"id\",\"fieldType\":\"bigint\",\"defaultValue\":\"\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"NONE\",\"mockParams\":\"0\"},{\"fieldName\":\"create_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"update_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"更新时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"is_deleted\",\"fieldType\":\"tinyint\",\"defaultValue\":\"0\",\"notNull\":false,\"comment\":\"删除标记\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"FIXED\",\"mockParams\":\"0\"},{\"fieldName\":\"id\",\"fieldType\":\"bigint\",\"defaultValue\":\"\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"NONE\",\"mockParams\":\"0\"},{\"fieldName\":\"create_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"update_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"更新时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"is_deleted\",\"fieldType\":\"tinyint\",\"defaultValue\":\"0\",\"notNull\":false,\"comment\":\"删除标记\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"FIXED\",\"mockParams\":\"0\"},{\"fieldName\":\"id\",\"fieldType\":\"bigint\",\"defaultValue\":\"\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"NONE\",\"mockParams\":\"0\"},{\"fieldName\":\"create_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"update_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"更新时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"is_deleted\",\"fieldType\":\"tinyint\",\"defaultValue\":\"0\",\"notNull\":false,\"comment\":\"删除标记\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"FIXED\",\"mockParams\":\"0\"}]}', 0, 0, 1, 'myservice', 'MYSQL', 'localhost', 3306, NULL, '123', NULL, 1, '2023-04-23 14:26:10', '2023-04-23 14:26:10', 0);

-- ----------------------------
-- Table structure for table_info
-- ----------------------------
DROP TABLE IF EXISTS `table_info`;
CREATE TABLE `table_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '表信息（json）',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '表信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of table_info
-- ----------------------------
INSERT INTO `table_info` VALUES (1, '用户表', '{\"dbName\":\"yupi_db\",\"tableName\":\"user\",\"tableComment\":\"用户表\",\"mockNum\":20,\"metaFieldList\":[{\"fieldName\":\"username\",\"comment\":\"用户名\",\"fieldType\":\"varchar(256)\",\"mockType\":\"随机\",\"mockParams\":\"人名\",\"notNull\":true,\"primaryKey\":false,\"autoIncrement\":false},{\"fieldName\":\"id\",\"comment\":\"主键\",\"fieldType\":\"bigint\",\"mockType\":\"固定\",\"notNull\":true,\"primaryKey\":true,\"autoIncrement\":true},{\"fieldName\":\"create_time\",\"comment\":\"创建时间\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"fieldType\":\"datetime\",\"mockType\":\"固定\",\"notNull\":true,\"primaryKey\":false,\"autoIncrement\":false},{\"fieldName\":\"update_time\",\"comment\":\"更新时间\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"fieldType\":\"datetime\",\"mockType\":\"固定\",\"notNull\":true,\"primaryKey\":false,\"autoIncrement\":false,\"extra\":\"on update CURRENT_TIMESTAMP\"},{\"fieldName\":\"is_deleted\",\"comment\":\"是否删除(0-未删, 1-已删)\",\"defaultValue\":\"0\",\"fieldType\":\"tinyint\",\"mockType\":\"固定\",\"notNull\":true,\"primaryKey\":false,\"autoIncrement\":false}]}', 1, '2023-04-02 11:12:44', '2023-04-17 16:53:47', 1);
INSERT INTO `table_info` VALUES (2, 'abc', '{\"dbName\":\"\",\"tableName\":\"123\",\"tableComment\":\"\",\"mockNum\":1,\"metaFieldList\":[{\"fieldName\":\"id\",\"fieldType\":\"BIGINT\",\"defaultValue\":\"0\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"INCREASE\",\"mockParams\":\"0\"},{\"fieldName\":\"create_time\",\"fieldType\":\"DATETIME\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"update_time\",\"fieldType\":\"DATETIME\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"更新时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"is_deleted\",\"fieldType\":\"TINYINT\",\"defaultValue\":\"0\",\"notNull\":false,\"comment\":\"删除标记\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"FIXED\",\"mockParams\":\"0\"}]}', 1, '2023-04-18 17:55:47', '2023-04-18 17:55:47', 0);
INSERT INTO `table_info` VALUES (3, 'adaw', '{\"dbName\":\"\",\"tableName\":\"123\",\"tableComment\":\"\",\"mockNum\":1,\"metaFieldList\":[{\"fieldName\":\"id\",\"fieldType\":\"bigint\",\"defaultValue\":\"\",\"notNull\":true,\"comment\":\"主键\",\"primaryKey\":true,\"autoIncrement\":true,\"mockType\":\"NONE\",\"mockParams\":\"0\"},{\"fieldName\":\"create_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"创建时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"update_time\",\"fieldType\":\"datetime\",\"defaultValue\":\"CURRENT_TIMESTAMP\",\"notNull\":true,\"comment\":\"更新时间\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"NONE\",\"mockParams\":\"\"},{\"fieldName\":\"is_deleted\",\"fieldType\":\"tinyint\",\"defaultValue\":\"0\",\"notNull\":false,\"comment\":\"删除标记\",\"primaryKey\":false,\"autoIncrement\":false,\"mockType\":\"FIXED\",\"mockParams\":\"0\"}]}', 1, '2023-04-22 18:04:25', '2023-04-22 18:04:25', 0);

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `userRole` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/ admin',
  `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_userAccount`(`userAccount`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, 'viamamo', 'viamamo', 'user', 'bcc986c81ed7d5b754d7deab956d98c101c4dbddf94548ca9d373a958489ae7c', '2023-04-02 11:11:51', '2023-04-16 20:23:17', 0);
INSERT INTO `user_info` VALUES (2, 'v2', 'viamamo2', 'user', 'bcc986c81ed7d5b754d7deab956d98c101c4dbddf94548ca9d373a958489ae7c', '2023-04-16 20:20:40', '2023-04-16 20:20:40', 0);
INSERT INTO `user_info` VALUES (3, '123', 'viamamo3', 'user', 'bcc986c81ed7d5b754d7deab956d98c101c4dbddf94548ca9d373a958489ae7c', '2023-04-16 20:23:05', '2023-04-16 20:23:05', 0);

SET FOREIGN_KEY_CHECKS = 1;
