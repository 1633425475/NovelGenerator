# 数据库初始化完整指南

## 问题说明

运行 `auth_init.sql` 时出现错误，是因为该脚本依赖的表（user、role、permission 等）不存在。现在已将用户认证系统的表结构和数据合并到完整的数据库脚本中。

---

## 解决方案

### 方案一：重新初始化数据库（推荐，最干净）

**适用于：** 数据库刚创建，没有重要数据

#### 步骤：

1. **删除旧数据库（如果存在）**
   ```sql
   DROP DATABASE IF EXISTS `novel_ai`;
   ```

2. **运行完整的数据库脚本**
   
   使用更新后的 [database_schema.sql](database_schema.sql) 文件，它现在包含：
   - 所有小说相关表
   - 所有资源管理表
   - 所有生成任务表
   - 所有智能体与审核表
   - 所有视频拼接表
   - ✅ **用户认证系统表（新增）**
     - `permission` - 权限表
     - `role` - 角色表
     - `role_permission` - 角色权限关联表
     - `user` - 用户表
     - `user_role` - 用户角色关联表
   - ✅ **初始化数据（新增）**
     - 28 个权限
     - 3 个角色（ADMIN、USER、GUEST）
     - 2 个用户（admin、user）
     - 角色权限关联
     - 用户角色关联

3. **执行命令**
   ```bash
   mysql -u root -p < "D:\Workspace\laoji\backend\database_schema.sql"
   ```

---

### 方案二：在现有数据库中添加用户认证系统

**适用于：** 数据库已有数据，不想删除

#### 步骤：

1. **只创建用户认证相关的表**

   创建一个临时 SQL 文件，内容如下：

   ```sql
   USE novel_ai;

   -- 权限表
   CREATE TABLE IF NOT EXISTS `permission` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
     `name` VARCHAR(100) NOT NULL COMMENT '权限名称',
     `description` VARCHAR(200) COMMENT '权限描述',
     `resource_type` VARCHAR(50) COMMENT '资源类型',
     `action` VARCHAR(20) COMMENT '操作类型',
     `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_permission_name` (`name`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

   -- 角色表
   CREATE TABLE IF NOT EXISTS `role` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
     `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
     `description` VARCHAR(200) COMMENT '角色描述',
     `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_role_name` (`name`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

   -- 角色权限关联表
   CREATE TABLE IF NOT EXISTS `role_permission` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
     `role_id` BIGINT NOT NULL COMMENT '角色ID',
     `permission_id` BIGINT NOT NULL COMMENT '权限ID',
     `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
     INDEX `idx_role_id` (`role_id`),
     INDEX `idx_permission_id` (`permission_id`),
     CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE,
     CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

   -- 用户表
   CREATE TABLE IF NOT EXISTS `user` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
     `username` VARCHAR(100) NOT NULL COMMENT '用户名',
     `password` VARCHAR(255) NOT NULL COMMENT '密码',
     `email` VARCHAR(100) COMMENT '邮箱',
     `phone` VARCHAR(20) COMMENT '手机号',
     `nickname` VARCHAR(100) COMMENT '昵称',
     `avatar_url` VARCHAR(500) COMMENT '头像URL',
     `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
     `account_non_expired` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账户是否未过期',
     `account_non_locked` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账户是否未锁定',
     `credentials_non_expired` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '凭证是否未过期',
     `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_username` (`username`),
     INDEX `idx_email` (`email`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

   -- 用户角色关联表
   CREATE TABLE IF NOT EXISTS `user_role` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
     `user_id` BIGINT NOT NULL COMMENT '用户ID',
     `role_id` BIGINT NOT NULL COMMENT '角色ID',
     `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
     INDEX `idx_user_id` (`user_id`),
     INDEX `idx_role_id` (`role_id`),
     CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
     CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

   -- ================================================
   -- 插入初始化数据
   -- ================================================

   -- 插入权限数据
   INSERT IGNORE INTO `permission` (`name`, `description`, `resource_type`, `action`, `created_at`, `updated_at`) VALUES
   ('outline:create', '创建大纲', 'OUTLINE', 'CREATE', NOW(), NOW()),
   ('outline:read', '查看大纲', 'OUTLINE', 'READ', NOW(), NOW()),
   ('outline:update', '更新大纲', 'OUTLINE', 'UPDATE', NOW(), NOW()),
   ('outline:delete', '删除大纲', 'OUTLINE', 'DELETE', NOW(), NOW()),
   ('resource:create', '创建资源', 'RESOURCE', 'CREATE', NOW(), NOW()),
   ('resource:read', '查看资源', 'RESOURCE', 'READ', NOW(), NOW()),
   ('resource:update', '更新资源', 'RESOURCE', 'UPDATE', NOW(), NOW()),
   ('resource:delete', '删除资源', 'RESOURCE', 'DELETE', NOW(), NOW()),
   ('audio:generate', '生成音频', 'AUDIO', 'GENERATE', NOW(), NOW()),
   ('audio:read', '查看音频', 'AUDIO', 'READ', NOW(), NOW()),
   ('audio:update', '更新音频', 'AUDIO', 'UPDATE', NOW(), NOW()),
   ('audio:delete', '删除音频', 'AUDIO', 'DELETE', NOW(), NOW()),
   ('voice_preset:manage', '管理语音预设', 'VOICE_PRESET', 'MANAGE', NOW(), NOW()),
   ('video:generate', '生成视频', 'VIDEO', 'GENERATE', NOW(), NOW()),
   ('video:read', '查看视频', 'VIDEO', 'READ', NOW(), NOW()),
   ('video:update', '更新视频', 'VIDEO', 'UPDATE', NOW(), NOW()),
   ('video:delete', '删除视频', 'VIDEO', 'DELETE', NOW(), NOW()),
   ('video:review', '审核视频', 'VIDEO', 'REVIEW', NOW(), NOW()),
   ('agent:create', '创建智能体', 'AGENT', 'CREATE', NOW(), NOW()),
   ('agent:read', '查看智能体', 'AGENT', 'READ', NOW(), NOW()),
   ('agent:update', '更新智能体', 'AGENT', 'UPDATE', NOW(), NOW()),
   ('agent:delete', '删除智能体', 'AGENT', 'DELETE', NOW(), NOW()),
   ('agent:chat', '与智能体对话', 'AGENT', 'CHAT', NOW(), NOW()),
   ('system:config', '系统配置', 'SYSTEM', 'CONFIG', NOW(), NOW()),
   ('system:statistics', '查看统计', 'SYSTEM', 'STATISTICS', NOW(), NOW()),
   ('system:backup', '数据备份', 'SYSTEM', 'BACKUP', NOW(), NOW());

   -- 插入角色数据
   INSERT IGNORE INTO `role` (`name`, `description`, `created_at`, `updated_at`) VALUES
   ('ADMIN', '系统管理员', NOW(), NOW()),
   ('USER', '普通用户', NOW(), NOW()),
   ('GUEST', '访客', NOW(), NOW());

   -- 只在表为空时插入关联数据
   -- 先检查是否已有数据
   SET @has_permissions = (SELECT COUNT(*) FROM `permission`);
   SET @has_roles = (SELECT COUNT(*) FROM `role`);
   SET @has_role_permissions = (SELECT COUNT(*) FROM `role_permission`);
   SET @has_users = (SELECT COUNT(*) FROM `user`);
   SET @has_user_roles = (SELECT COUNT(*) FROM `user_role`);

   -- 管理员拥有所有权限（如果表为空）
   INSERT INTO `role_permission` (`role_id`, `permission_id`)
   SELECT 1, id FROM `permission`
   WHERE @has_role_permissions = 0;

   -- 普通用户权限（如果表为空）
   INSERT INTO `role_permission` (`role_id`, `permission_id`)
   SELECT 2, id FROM `permission`
   WHERE @has_role_permissions = 0 AND `name` IN (
       'outline:create', 'outline:read', 'outline:update', 'outline:delete',
       'resource:create', 'resource:read', 'resource:update', 'resource:delete',
       'audio:generate', 'audio:read', 'audio:update', 'audio:delete',
       'voice_preset:manage',
       'video:generate', 'video:read', 'video:update', 'video:delete',
       'agent:create', 'agent:read', 'agent:update', 'agent:delete', 'agent:chat'
   );

   -- 访客权限（只读）（如果表为空）
   INSERT INTO `role_permission` (`role_id`, `permission_id`)
   SELECT 3, id FROM `permission`
   WHERE @has_role_permissions = 0 AND `name` IN (
       'outline:read',
       'resource:read',
       'audio:read',
       'video:read',
       'agent:read'
   );

   -- 插入默认用户数据（如果表为空）
   -- 密码使用 BCrypt 加密，原始密码: admin123
   INSERT INTO `user` (`username`, `password`, `email`, `phone`, `nickname`, `avatar_url`, `enabled`, `account_non_expired`, `account_non_locked`, `credentials_non_expired`, `created_at`, `updated_at`)
   SELECT 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', 'admin@example.com', '13800138000', '系统管理员', NULL, 1, 1, 1, 1, NOW(), NOW()
   WHERE @has_users = 0
   UNION ALL
   SELECT 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', 'user@example.com', '13800138001', '普通用户', NULL, 1, 1, 1, 1, NOW(), NOW()
   WHERE @has_users = 0;

   -- 用户角色关联（如果表为空）
   INSERT INTO `user_role` (`user_id`, `role_id`)
   SELECT 1, 1 WHERE @has_user_roles = 0
   UNION ALL
   SELECT 2, 2 WHERE @has_user_roles = 0;
   ```

2. **保存并执行这个临时文件**

---

## 默认登录账号

执行成功后，可以使用以下账号登录：

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 管理员 | `admin` | `admin123` | 全部权限 |
| 普通用户 | `user` | `admin123` | 基本操作权限 |

---

## 验证数据库

执行完成后，可以运行以下 SQL 验证：

```sql
USE novel_ai;

-- 检查用户表
SELECT '用户数量:' AS info, COUNT(*) AS count FROM `user`;

-- 检查角色表
SELECT '角色数量:' AS info, COUNT(*) AS count FROM `role`;

-- 检查权限表
SELECT '权限数量:' AS info, COUNT(*) AS count FROM `permission`;

-- 查看默认用户
SELECT id, username, nickname, email FROM `user`;
```

---

## 下一步

1. **选择方案一或方案二**初始化数据库
2. **重启后端服务**（如果正在运行）
3. **使用 admin / admin123 登录测试**
4. **确认 403 问题已解决**

---

## 重要提示

1. **BCrypt 密码**：脚本中的密码哈希是 `admin123` 的 BCrypt 加密结果
2. **不要直接修改 auth_init.sql**：这个文件现在已不需要，完整的功能已合并到 database_schema.sql
3. **备份数据**：如果选择方案二，请先备份现有数据