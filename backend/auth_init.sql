-- ================================================
-- 用户认证系统初始化数据脚本
-- ================================================

USE novel_ai;

-- 确保事务安全
START TRANSACTION;

-- ================================================
-- 1. 插入权限数据
-- ================================================
INSERT INTO `permission` (`name`, `description`, `resource_type`, `action`, `created_at`, `updated_at`) VALUES
-- 大纲相关权限
('outline:create', '创建大纲', 'OUTLINE', 'CREATE', NOW(), NOW()),
('outline:read', '查看大纲', 'OUTLINE', 'READ', NOW(), NOW()),
('outline:update', '更新大纲', 'OUTLINE', 'UPDATE', NOW(), NOW()),
('outline:delete', '删除大纲', 'OUTLINE', 'DELETE', NOW(), NOW()),

-- 资源相关权限
('resource:create', '创建资源', 'RESOURCE', 'CREATE', NOW(), NOW()),
('resource:read', '查看资源', 'RESOURCE', 'READ', NOW(), NOW()),
('resource:update', '更新资源', 'RESOURCE', 'UPDATE', NOW(), NOW()),
('resource:delete', '删除资源', 'RESOURCE', 'DELETE', NOW(), NOW()),

-- 音频相关权限
('audio:generate', '生成音频', 'AUDIO', 'GENERATE', NOW(), NOW()),
('audio:read', '查看音频', 'AUDIO', 'READ', NOW(), NOW()),
('audio:update', '更新音频', 'AUDIO', 'UPDATE', NOW(), NOW()),
('audio:delete', '删除音频', 'AUDIO', 'DELETE', NOW(), NOW()),
('voice_preset:manage', '管理语音预设', 'VOICE_PRESET', 'MANAGE', NOW(), NOW()),

-- 视频相关权限
('video:generate', '生成视频', 'VIDEO', 'GENERATE', NOW(), NOW()),
('video:read', '查看视频', 'VIDEO', 'READ', NOW(), NOW()),
('video:update', '更新视频', 'VIDEO', 'UPDATE', NOW(), NOW()),
('video:delete', '删除视频', 'VIDEO', 'DELETE', NOW(), NOW()),
('video:review', '审核视频', 'VIDEO', 'REVIEW', NOW(), NOW()),

-- 智能体相关权限
('agent:create', '创建智能体', 'AGENT', 'CREATE', NOW(), NOW()),
('agent:read', '查看智能体', 'AGENT', 'READ', NOW(), NOW()),
('agent:update', '更新智能体', 'AGENT', 'UPDATE', NOW(), NOW()),
('agent:delete', '删除智能体', 'AGENT', 'DELETE', NOW(), NOW()),
('agent:chat', '与智能体对话', 'AGENT', 'CHAT', NOW(), NOW()),

-- 系统管理权限
('system:config', '系统配置', 'SYSTEM', 'CONFIG', NOW(), NOW()),
('system:statistics', '查看统计', 'SYSTEM', 'STATISTICS', NOW(), NOW()),
('system:backup', '数据备份', 'SYSTEM', 'BACKUP', NOW(), NOW());

-- ================================================
-- 2. 插入角色数据
-- ================================================
INSERT INTO `role` (`name`, `description`, `created_at`, `updated_at`) VALUES
('ADMIN', '系统管理员', NOW(), NOW()),
('USER', '普通用户', NOW(), NOW()),
('GUEST', '访客', NOW(), NOW());

-- ================================================
-- 3. 角色权限关联
-- ================================================
-- 管理员拥有所有权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `permission`;

-- 普通用户权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `permission`
WHERE `name` IN (
    'outline:create', 'outline:read', 'outline:update', 'outline:delete',
    'resource:create', 'resource:read', 'resource:update', 'resource:delete',
    'audio:generate', 'audio:read', 'audio:update', 'audio:delete',
    'voice_preset:manage',
    'video:generate', 'video:read', 'video:update', 'video:delete',
    'agent:create', 'agent:read', 'agent:update', 'agent:delete', 'agent:chat'
);

-- 访客权限（只读）
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `permission`
WHERE `name` IN (
    'outline:read',
    'resource:read',
    'audio:read',
    'video:read',
    'agent:read'
);

-- ================================================
-- 4. 插入默认用户数据
-- ================================================
-- 密码使用 BCrypt 加密，原始密码: admin123
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E 是 admin123 的 BCrypt 哈希
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `nickname`, `avatar_url`, `enabled`, `account_non_expired`, `account_non_locked`, `credentials_non_expired`, `created_at`, `updated_at`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', 'admin@example.com', '13800138000', '系统管理员', NULL, 1, 1, 1, 1, NOW(), NOW()),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', 'user@example.com', '13800138001', '普通用户', NULL, 1, 1, 1, 1, NOW(), NOW());

-- ================================================
-- 5. 用户角色关联
-- ================================================
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
(1, 1), -- admin -> ADMIN
(2, 2); -- user -> USER

-- 提交事务
COMMIT;

-- ================================================
-- 验证数据
-- ================================================
SELECT '初始化数据验证：' AS info;

SELECT '用户数量:' AS type, COUNT(*) AS count FROM `user`
UNION ALL
SELECT '角色数量:', COUNT(*) FROM `role`
UNION ALL
SELECT '权限数量:', COUNT(*) FROM `permission`;

SELECT '默认账号：' AS info;
SELECT '用户名' AS username, 'admin' AS value, '密码' AS password, 'admin123' AS value2
UNION ALL
SELECT 'username', 'user', 'password', 'admin123';

-- ================================================
-- 结束语
-- ================================================
SELECT '认证系统初始化完成！' AS message;
SELECT '默认管理员账号: admin / admin123' AS admin_account;
SELECT '默认用户账号: user / admin123' AS user_account;