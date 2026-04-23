-- ================================================
-- 基于 Spring Boot AI 的小说生成工具数据库脚本
-- 数据库：MySQL 8.0+
-- 字符集：utf8mb4 (支持完整的 Unicode 字符集，包括表情符号)
-- 排序规则：utf8mb4_unicode_ci
-- ================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `novel_ai` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `novel_ai`;

-- ================================================
-- 1. 小说相关表
-- ================================================

-- 小说基本信息表
CREATE TABLE `novel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '小说ID',
  `title` VARCHAR(200) NOT NULL COMMENT '小说标题',
  `author_id` BIGINT NOT NULL COMMENT '作者用户ID',
  `genre` VARCHAR(50) COMMENT '小说类型（玄幻/都市/科幻等）',
  `description` TEXT COMMENT '小说简介',
  `cover_image_url` VARCHAR(500) COMMENT '封面图片URL',
  `word_count_target` INT DEFAULT 0 COMMENT '目标字数',
  `word_count_current` INT DEFAULT 0 COMMENT '当前字数',
  `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, PUBLISHED-已发布, COMPLETED-已完成, ARCHIVED-已归档',
  `is_public` TINYINT(1) DEFAULT 0 COMMENT '是否公开',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  INDEX `idx_author_id` (`author_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_genre` (`genre`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小说基本信息表';

-- 小说大纲表
CREATE TABLE `outline` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '大纲ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `version` VARCHAR(20) NOT NULL COMMENT '版本号，格式：v主版本.次版本',
  `title` VARCHAR(200) COMMENT '大纲标题',
  `content` JSON NOT NULL COMMENT '大纲内容（JSON格式）',
  `metadata` JSON COMMENT '元数据（如生成参数、风格等）',
  `is_current` TINYINT(1) DEFAULT 1 COMMENT '是否为当前使用版本',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT NOT NULL COMMENT '创建者用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_novel_version` (`novel_id`, `version`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_is_current` (`is_current`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_outline_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小说大纲表';

-- 章节信息表
CREATE TABLE `chapter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `chapter_number` INT NOT NULL COMMENT '章节序号',
  `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
  `content` LONGTEXT COMMENT '章节内容（富文本或纯文本）',
  `word_count` INT DEFAULT 0 COMMENT '章节字数',
  `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, COMPLETED-已完成, REVIEWING-审核中, PUBLISHED-已发布',
  `audio_generated` TINYINT(1) DEFAULT 0 COMMENT '音频是否已生成',
  `video_generated` TINYINT(1) DEFAULT 0 COMMENT '视频是否已生成',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `published_at` TIMESTAMP NULL COMMENT '发布时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_novel_chapter` (`novel_id`, `chapter_number`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_chapter_number` (`chapter_number`),
  CONSTRAINT `fk_chapter_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节信息表';

-- ================================================
-- 2. 资源管理表
-- ================================================

-- 人物表
CREATE TABLE `character` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '人物ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `name` VARCHAR(100) NOT NULL COMMENT '人物姓名',
  `alias` VARCHAR(100) COMMENT '别名/外号',
  `gender` VARCHAR(10) COMMENT '性别：MALE-男, FEMALE-女, UNKNOWN-未知',
  `age` INT COMMENT '年龄',
  `age_range` VARCHAR(50) COMMENT '年龄段（如：青年、中年）',
  `appearance` TEXT COMMENT '外貌描述',
  `personality` TEXT COMMENT '性格描述',
  `background` TEXT COMMENT '背景故事',
  `relationships` JSON COMMENT '人物关系（JSON数组）',
  `faction` VARCHAR(100) COMMENT '所属势力/阵营',
  `role` VARCHAR(50) COMMENT '角色类型：MAIN-主角, SUPPORTING-配角, MINOR-龙套, ANTAGONIST-反派',
  `tags` JSON COMMENT '标签（JSON数组，如：["主角", "学生", "英雄"]）',
  `avatar_url` VARCHAR(500) COMMENT '头像图片URL',
  `reference_images` JSON COMMENT '参考图片URL列表（JSON数组）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_role` (`role`),
  INDEX `idx_faction` (`faction`),
  CONSTRAINT `fk_character_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人物表';

-- 地点表
CREATE TABLE `location` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地点ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `name` VARCHAR(200) NOT NULL COMMENT '地点名称',
  `type` VARCHAR(50) COMMENT '地点类型：CITY-城市, CASTLE-城堡, FOREST-森林, SCHOOL-学校, OTHER-其他',
  `description` TEXT COMMENT '地点描述',
  `geography` TEXT COMMENT '地理环境描述',
  `architecture` TEXT COMMENT '建筑风格描述',
  `associated_characters` JSON COMMENT '关联人物ID列表（JSON数组）',
  `associated_events` JSON COMMENT '关联事件ID列表（JSON数组）',
  `tags` JSON COMMENT '标签（JSON数组，如：["学校", "魔法", "重要场景"]）',
  `map_image_url` VARCHAR(500) COMMENT '地图图片URL',
  `reference_images` JSON COMMENT '参考图片URL列表（JSON数组）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_type` (`type`),
  CONSTRAINT `fk_location_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地点表';

-- 时间线表
CREATE TABLE `timeline` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '时间线ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `name` VARCHAR(200) NOT NULL COMMENT '时间线名称',
  `description` TEXT COMMENT '时间线描述',
  `time_point` VARCHAR(100) NOT NULL COMMENT '时间点（如：元年春、第3天、公元前300年）',
  `event_description` TEXT COMMENT '事件描述',
  `chapter_id` BIGINT COMMENT '关联章节ID',
  `related_entities` JSON COMMENT '关联实体（JSON数组，包含人物、地点、事件ID）',
  `tags` JSON COMMENT '标签（JSON数组，如：["主线", "时间线"]）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_chapter_id` (`chapter_id`),
  INDEX `idx_time_point` (`time_point`),
  CONSTRAINT `fk_timeline_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_timeline_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='时间线表';

-- 事件表
CREATE TABLE `event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '事件ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `name` VARCHAR(200) NOT NULL COMMENT '事件名称',
  `description` TEXT COMMENT '事件描述',
  `trigger` TEXT COMMENT '触发条件',
  `result` TEXT COMMENT '事件结果',
  `characters` JSON COMMENT '参与人物ID列表（JSON数组）',
  `location_id` BIGINT COMMENT '发生地点ID',
  `timeline_id` BIGINT COMMENT '关联时间线ID',
  `chapter_id` BIGINT COMMENT '关联章节ID',
  `importance` VARCHAR(20) DEFAULT 'MEDIUM' COMMENT '重要性：LOW-低, MEDIUM-中, HIGH-高, CRITICAL-关键',
  `tags` JSON COMMENT '标签（JSON数组，如：["转折", "魔法", "开始"]）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_location_id` (`location_id`),
  INDEX `idx_timeline_id` (`timeline_id`),
  INDEX `idx_chapter_id` (`chapter_id`),
  INDEX `idx_importance` (`importance`),
  CONSTRAINT `fk_event_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_event_location` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_event_timeline` FOREIGN KEY (`timeline_id`) REFERENCES `timeline` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_event_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='事件表';

-- 资源文件表（统一存储所有生成的文件：图片、音频、视频等）
CREATE TABLE `resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `type` VARCHAR(50) NOT NULL COMMENT '资源类型：IMAGE-图片, AUDIO-音频, VIDEO-视频, DOCUMENT-文档, OTHER-其他',
  `subtype` VARCHAR(50) COMMENT '子类型：如CHARACTER_AVATAR-角色头像, SCENE_IMAGE-场景图, BACKGROUND_MUSIC-背景音乐等',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
  `file_url` VARCHAR(1000) NOT NULL COMMENT '文件URL（OSS存储路径）',
  `thumbnail_url` VARCHAR(1000) COMMENT '缩略图URL',
  `file_size` BIGINT DEFAULT 0 COMMENT '文件大小（字节）',
  `duration` DOUBLE COMMENT '时长（秒，适用于音频/视频）',
  `format` VARCHAR(50) COMMENT '文件格式：MP3, WAV, MP4, PNG, JPG等',
  `width` INT COMMENT '宽度（像素，适用于图片/视频）',
  `height` INT COMMENT '高度（像素，适用于图片/视频）',
  `metadata` JSON COMMENT '元数据（JSON格式，包含生成参数、模型信息等）',
  `associated_entity_type` VARCHAR(50) COMMENT '关联实体类型：CHARACTER-人物, LOCATION-地点, EVENT-事件, CHAPTER-章节, SCENE-场景',
  `associated_entity_id` BIGINT COMMENT '关联实体ID',
  `tags` JSON COMMENT '标签（JSON数组）',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃, ARCHIVED-已归档, DELETED-已删除',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_subtype` (`subtype`),
  INDEX `idx_associated_entity` (`associated_entity_type`, `associated_entity_id`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_resource_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源文件表';

-- ================================================
-- 3. 生成任务表
-- ================================================

-- 生成任务表
CREATE TABLE `generation_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `type` VARCHAR(50) NOT NULL COMMENT '任务类型：OUTLINE-大纲, CHAPTER-章节, IMAGE-图片, AUDIO-音频, VIDEO-视频, SCRIPT-脚本, AGENT-智能体',
  `subtype` VARCHAR(50) COMMENT '子类型：如CHARACTER_IMAGE-角色图片, SCENE_IMAGE-场景图片等',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-等待中, PROCESSING-处理中, COMPLETED-已完成, FAILED-失败, CANCELLED-已取消',
  `priority` INT DEFAULT 5 COMMENT '优先级（1-10，1最高）',
  `parameters` JSON NOT NULL COMMENT '任务参数（JSON格式）',
  `result_data` JSON COMMENT '任务结果数据（JSON格式）',
  `result_url` VARCHAR(1000) COMMENT '结果文件URL',
  `error_message` TEXT COMMENT '错误信息',
  `retry_count` INT DEFAULT 0 COMMENT '重试次数',
  `max_retries` INT DEFAULT 3 COMMENT '最大重试次数',
  `associated_entity_type` VARCHAR(50) COMMENT '关联实体类型',
  `associated_entity_id` BIGINT COMMENT '关联实体ID',
  `started_at` TIMESTAMP NULL COMMENT '开始时间',
  `completed_at` TIMESTAMP NULL COMMENT '完成时间',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_priority` (`priority`),
  INDEX `idx_associated_entity` (`associated_entity_type`, `associated_entity_id`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_generation_task_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生成任务表';

-- 任务执行日志表
CREATE TABLE `task_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `task_id` BIGINT NOT NULL COMMENT '关联任务ID',
  `log_level` VARCHAR(20) DEFAULT 'INFO' COMMENT '日志级别：DEBUG, INFO, WARN, ERROR',
  `message` TEXT NOT NULL COMMENT '日志消息',
  `details` JSON COMMENT '详细数据（JSON格式）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_task_id` (`task_id`),
  INDEX `idx_log_level` (`log_level`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_task_log_task` FOREIGN KEY (`task_id`) REFERENCES `generation_task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务执行日志表';

-- ================================================
-- 4. 智能体与审核表
-- ================================================

-- 智能体对话会话表
CREATE TABLE `agent_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `agent_type` VARCHAR(50) NOT NULL COMMENT '智能体类型：SCREENWRITER-编剧, PHOTOGRAPHER-摄影师, DUBBING_ARTIST-配音师, EDITOR-剪辑师, REVIEWER-审核员',
  `session_name` VARCHAR(200) NOT NULL COMMENT '会话名称',
  `context_data` JSON COMMENT '上下文数据（JSON格式）',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-活跃, PAUSED-已暂停, COMPLETED-已完成, ARCHIVED-已归档',
  `message_count` INT DEFAULT 0 COMMENT '消息数量',
  `last_activity_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后活动时间',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_agent_type` (`agent_type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_last_activity_at` (`last_activity_at`),
  CONSTRAINT `fk_agent_session_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能体对话会话表';

-- 智能体消息表
CREATE TABLE `agent_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` BIGINT NOT NULL COMMENT '关联会话ID',
  `message_type` VARCHAR(20) NOT NULL COMMENT '消息类型：USER-用户, AGENT-智能体, SYSTEM-系统, TOOL-工具调用',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `tool_calls` JSON COMMENT '工具调用数据（JSON格式）',
  `tool_results` JSON COMMENT '工具调用结果（JSON格式）',
  `metadata` JSON COMMENT '元数据（JSON格式）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_session_id` (`session_id`),
  INDEX `idx_message_type` (`message_type`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_agent_message_session` FOREIGN KEY (`session_id`) REFERENCES `agent_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能体消息表';

-- 审核记录表
CREATE TABLE `review_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `review_type` VARCHAR(50) NOT NULL COMMENT '审核类型：AI_REVIEW-AI审核, HUMAN_REVIEW-人工审核, FINAL_REVIEW-终审',
  `target_type` VARCHAR(50) NOT NULL COMMENT '审核目标类型：CHAPTER-章节, IMAGE-图片, AUDIO-音频, VIDEO_SEGMENT-视频片段, FINAL_VIDEO-最终视频',
  `target_id` BIGINT NOT NULL COMMENT '审核目标ID',
  `reviewer_type` VARCHAR(20) DEFAULT 'AI' COMMENT '审核者类型：AI-人工智能, HUMAN-人工',
  `reviewer_id` BIGINT COMMENT '审核者ID（用户ID，AI审核时为NULL）',
  `score_overall` INT COMMENT '总体评分（0-100）',
  `score_technical` INT COMMENT '技术评分（0-100）',
  `score_artistic` INT COMMENT '艺术评分（0-100）',
  `score_narrative` INT COMMENT '叙事评分（0-100）',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核, PASSED-通过, REJECTED-驳回, NEEDS_REVISION-需要修改',
  `comments` TEXT COMMENT '审核意见',
  `rejection_reasons` JSON COMMENT '驳回原因（JSON数组）',
  `next_action` VARCHAR(100) COMMENT '下一步操作',
  `reviewed_at` TIMESTAMP NULL COMMENT '审核时间',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_review_target` (`target_type`, `target_id`, `review_type`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_review_type` (`review_type`),
  INDEX `idx_target` (`target_type`, `target_id`),
  INDEX `idx_reviewer` (`reviewer_type`, `reviewer_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_reviewed_at` (`reviewed_at`),
  CONSTRAINT `fk_review_record_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核记录表';

-- ================================================
-- 5. 视频拼接表
-- ================================================

-- 视频片段表
CREATE TABLE `video_segment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '视频片段ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `chapter_id` BIGINT NOT NULL COMMENT '关联章节ID',
  `segment_order` INT NOT NULL COMMENT '片段顺序（同一章节内）',
  `segment_name` VARCHAR(200) NOT NULL COMMENT '片段名称',
  `segment_description` TEXT COMMENT '片段描述',
  `file_url` VARCHAR(1000) NOT NULL COMMENT '视频文件URL',
  `thumbnail_url` VARCHAR(1000) COMMENT '缩略图URL',
  `duration` DOUBLE NOT NULL COMMENT '时长（秒）',
  `file_size` BIGINT DEFAULT 0 COMMENT '文件大小（字节）',
  `format` VARCHAR(50) DEFAULT 'MP4' COMMENT '视频格式',
  `width` INT COMMENT '宽度（像素）',
  `height` INT COMMENT '高度（像素）',
  `fps` INT COMMENT '帧率',
  `audio_track_url` VARCHAR(1000) COMMENT '音频轨道URL（如果分离）',
  `subtitle_url` VARCHAR(1000) COMMENT '字幕文件URL',
  `generation_params` JSON COMMENT '生成参数（JSON格式）',
  `review_status` VARCHAR(20) DEFAULT 'PENDING_AI' COMMENT '审核状态：PENDING_AI-待AI审核, AI_PASSED-AI通过, AI_REJECTED-AI驳回, PENDING_HUMAN-待人工审核, HUMAN_PASSED-人工通过, HUMAN_REJECTED-人工驳回, APPROVED-已批准',
  `ai_review_id` BIGINT COMMENT 'AI审核记录ID',
  `human_review_id` BIGINT COMMENT '人工审核记录ID',
  `quality_score` INT COMMENT '质量评分（0-100）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_chapter_segment` (`chapter_id`, `segment_order`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_chapter_id` (`chapter_id`),
  INDEX `idx_review_status` (`review_status`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_video_segment_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_video_segment_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_video_segment_ai_review` FOREIGN KEY (`ai_review_id`) REFERENCES `review_record` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_video_segment_human_review` FOREIGN KEY (`human_review_id`) REFERENCES `review_record` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频片段表';

-- 最终合成视频表
CREATE TABLE `final_video` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '最终视频ID',
  `novel_id` BIGINT NOT NULL COMMENT '关联小说ID',
  `video_name` VARCHAR(200) NOT NULL COMMENT '视频名称',
  `description` TEXT COMMENT '视频描述',
  `file_url` VARCHAR(1000) NOT NULL COMMENT '视频文件URL',
  `thumbnail_url` VARCHAR(1000) COMMENT '缩略图URL',
  `duration` DOUBLE NOT NULL COMMENT '总时长（秒）',
  `file_size` BIGINT DEFAULT 0 COMMENT '文件大小（字节）',
  `format` VARCHAR(50) DEFAULT 'MP4' COMMENT '视频格式',
  `width` INT COMMENT '宽度（像素）',
  `height` INT COMMENT '高度（像素）',
  `fps` INT COMMENT '帧率',
  `segment_count` INT DEFAULT 0 COMMENT '包含的片段数量',
  `merge_params` JSON COMMENT '合并参数（JSON格式）',
  `status` VARCHAR(20) DEFAULT 'PROCESSING' COMMENT '状态：PROCESSING-处理中, COMPLETED-已完成, FAILED-失败',
  `review_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核, APPROVED-已批准, REJECTED-已驳回',
  `final_review_id` BIGINT COMMENT '最终审核记录ID',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `completed_at` TIMESTAMP NULL COMMENT '完成时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  INDEX `idx_novel_id` (`novel_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_review_status` (`review_status`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_final_video_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_final_video_review` FOREIGN KEY (`final_review_id`) REFERENCES `review_record` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='最终合成视频表';

-- 最终视频包含的片段关系表
CREATE TABLE `final_video_segment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `final_video_id` BIGINT NOT NULL COMMENT '最终视频ID',
  `segment_id` BIGINT NOT NULL COMMENT '视频片段ID',
  `segment_order` INT NOT NULL COMMENT '在最终视频中的顺序',
  `start_time` DOUBLE COMMENT '在最终视频中的开始时间（秒）',
  `end_time` DOUBLE COMMENT '在最终视频中的结束时间（秒）',
  `transition_effect` VARCHAR(100) COMMENT '转场效果',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_video_segment` (`final_video_id`, `segment_id`),
  INDEX `idx_final_video_id` (`final_video_id`),
  INDEX `idx_segment_id` (`segment_id`),
  CONSTRAINT `fk_final_video_segment_video` FOREIGN KEY (`final_video_id`) REFERENCES `final_video` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_final_video_segment_segment` FOREIGN KEY (`segment_id`) REFERENCES `video_segment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='最终视频包含的片段关系表';

-- ================================================
-- 6. 其他辅助表
-- ================================================

-- 语音预设表（用于TTS服务）
CREATE TABLE `voice_preset` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预设ID',
  `name` VARCHAR(100) NOT NULL COMMENT '预设名称',
  `description` TEXT COMMENT '预设描述',
  `provider` VARCHAR(50) NOT NULL COMMENT '服务提供商：AZURE, ELEVENLABS, ALIYUN, LOCAL',
  `provider_voice_id` VARCHAR(100) NOT NULL COMMENT '提供商语音ID',
  `language` VARCHAR(20) DEFAULT 'zh-CN' COMMENT '语言',
  `gender` VARCHAR(10) COMMENT '性别：MALE, FEMALE',
  `age_group` VARCHAR(20) COMMENT '年龄段：CHILD, YOUNG, ADULT, ELDER',
  `emotion` VARCHAR(50) COMMENT '情感风格：NEUTRAL, HAPPY, SAD, ANGRY, EXCITED',
  `sample_rate` INT DEFAULT 44100 COMMENT '采样率',
  `bitrate` INT DEFAULT 128 COMMENT '比特率',
  `format` VARCHAR(20) DEFAULT 'MP3' COMMENT '音频格式',
  `provider_params` JSON COMMENT '提供商特定参数（JSON格式）',
  `sample_audio_url` VARCHAR(500) COMMENT '示例音频URL',
  `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否为默认预设',
  `is_public` TINYINT(1) DEFAULT 1 COMMENT '是否公开',
  `usage_count` INT DEFAULT 0 COMMENT '使用次数',
  `last_used_at` TIMESTAMP NULL COMMENT '最后使用时间',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` TIMESTAMP NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_provider_voice` (`provider`, `provider_voice_id`),
  INDEX `idx_provider` (`provider`),
  INDEX `idx_language` (`language`),
  INDEX `idx_gender` (`gender`),
  INDEX `idx_is_default` (`is_default`),
  INDEX `idx_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='语音预设表';

-- 角色与语音预设映射表
CREATE TABLE `character_voice_mapping` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '映射ID',
  `character_id` BIGINT NOT NULL COMMENT '角色ID',
  `voice_preset_id` BIGINT NOT NULL COMMENT '语音预设ID',
  `priority` INT DEFAULT 1 COMMENT '优先级（1最高）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_character_voice` (`character_id`, `voice_preset_id`),
  INDEX `idx_character_id` (`character_id`),
  INDEX `idx_voice_preset_id` (`voice_preset_id`),
  CONSTRAINT `fk_character_voice_mapping_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_character_voice_mapping_voice` FOREIGN KEY (`voice_preset_id`) REFERENCES `voice_preset` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色与语音预设映射表';

-- ================================================
-- 7. 用户认证系统表
-- ================================================

-- 权限表
CREATE TABLE `permission` (
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
CREATE TABLE `role` (
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
CREATE TABLE `role_permission` (
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
CREATE TABLE `user` (
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
CREATE TABLE `user_role` (
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
-- 8. 初始化数据
-- ================================================

-- 插入权限数据
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

-- 插入角色数据
INSERT INTO `role` (`name`, `description`, `created_at`, `updated_at`) VALUES
('ADMIN', '系统管理员', NOW(), NOW()),
('USER', '普通用户', NOW(), NOW()),
('GUEST', '访客', NOW(), NOW());

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

-- 插入默认用户数据
-- 密码使用 BCrypt 加密，原始密码: admin123
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E 是 admin123 的 BCrypt 哈希
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `nickname`, `avatar_url`, `enabled`, `account_non_expired`, `account_non_locked`, `credentials_non_expired`, `created_at`, `updated_at`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', 'admin@example.com', '13800138000', '系统管理员', NULL, 1, 1, 1, 1, NOW(), NOW()),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', 'user@example.com', '13800138001', '普通用户', NULL, 1, 1, 1, 1, NOW(), NOW());

-- 用户角色关联
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
(1, 1), -- admin -> ADMIN
(2, 2); -- user -> USER

-- 插入默认语音预设（示例）
INSERT INTO `voice_preset` (`name`, `description`, `provider`, `provider_voice_id`, `language`, `gender`, `age_group`, `emotion`, `is_default`) VALUES
('晓晓（女声）', 'Azure TTS 中文女声，音色清晰甜美', 'AZURE', 'zh-CN-XiaoxiaoNeural', 'zh-CN', 'FEMALE', 'YOUNG', 'NEUTRAL', 1),
('云扬（男声）', 'Azure TTS 中文男声，音色沉稳有力', 'AZURE', 'zh-CN-YunyangNeural', 'zh-CN', 'MALE', 'ADULT', 'NEUTRAL', 0),
('晓辰（女声）', 'Azure TTS 中文女声，适合旁白', 'AZURE', 'zh-CN-XiaochenNeural', 'zh-CN', 'FEMALE', 'YOUNG', 'NEUTRAL', 0),
('晓萱（女声）', 'Azure TTS 中文女声，情感丰富', 'AZURE', 'zh-CN-XiaoxuanNeural', 'zh-CN', 'FEMALE', 'YOUNG', 'HAPPY', 0);

-- ================================================
-- 9. 视图和存储过程（可选）
-- ================================================

-- 创建小说统计视图
CREATE VIEW `novel_statistics` AS
SELECT 
  n.id AS novel_id,
  n.title AS novel_title,
  n.status AS novel_status,
  COUNT(DISTINCT c.id) AS chapter_count,
  SUM(c.word_count) AS total_word_count,
  COUNT(DISTINCT ch.id) AS character_count,
  COUNT(DISTINCT l.id) AS location_count,
  COUNT(DISTINCT e.id) AS event_count,
  COUNT(DISTINCT r.id) AS resource_count,
  MAX(c.created_at) AS last_chapter_created
FROM `novel` n
LEFT JOIN `chapter` c ON n.id = c.novel_id AND c.deleted_at IS NULL
LEFT JOIN `character` ch ON n.id = ch.novel_id AND ch.deleted_at IS NULL
LEFT JOIN `location` l ON n.id = l.novel_id AND l.deleted_at IS NULL
LEFT JOIN `event` e ON n.id = e.novel_id AND e.deleted_at IS NULL
LEFT JOIN `resource` r ON n.id = r.novel_id AND r.deleted_at IS NULL AND r.status = 'ACTIVE'
WHERE n.deleted_at IS NULL
GROUP BY n.id, n.title, n.status;

-- 创建待审核视频片段视图
CREATE VIEW `pending_review_video_segments` AS
SELECT 
  vs.id AS segment_id,
  vs.segment_name,
  vs.segment_order,
  vs.duration,
  vs.review_status,
  c.id AS chapter_id,
  c.title AS chapter_title,
  c.chapter_number,
  n.id AS novel_id,
  n.title AS novel_title
FROM `video_segment` vs
JOIN `chapter` c ON vs.chapter_id = c.id AND c.deleted_at IS NULL
JOIN `novel` n ON vs.novel_id = n.id AND n.deleted_at IS NULL
WHERE vs.deleted_at IS NULL 
  AND vs.review_status IN ('PENDING_AI', 'PENDING_HUMAN', 'AI_PASSED')
ORDER BY n.id, c.chapter_number, vs.segment_order;

-- ================================================
-- 10. 结束语
-- ================================================

-- 验证数据
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

SELECT '认证系统初始化完成！' AS message;
SELECT '默认管理员账号: admin / admin123' AS admin_account;
SELECT '默认用户账号: user / admin123' AS user_account;

-- ================================================
-- 11. 结束语
-- ================================================

-- 数据库脚本执行完成
-- 注意：本脚本包含了完整的表结构、索引、外键约束和初始化数据
-- 在实际生产环境中，建议根据具体需求调整字段类型、索引策略和初始化数据
-- 执行前请确保MySQL用户有足够的权限创建数据库和表