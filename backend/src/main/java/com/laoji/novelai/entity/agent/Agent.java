package com.laoji.novelai.entity.agent;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 智能体角色实体
 * 扩展基础人物角色，添加AI对话和行为能力
 */
@Entity
@Table(name = "ai_agent")
@Data
@EqualsAndHashCode(callSuper = true)
public class Agent extends BaseEntity {

    /**
     * 关联的人物角色ID
     */
    @Column(nullable = false)
    private Long characterId;

    /**
     * 智能体名称（可不同于人物名称）
     */
    @Column(nullable = false)
    private String agentName;

    /**
     * 智能体类型：CHARACTER-角色扮演, ASSISTANT-助手, NARRATOR-旁白, SYSTEM-系统
     */
    @Column(nullable = false)
    private String agentType = "CHARACTER";

    /**
     * AI模型配置（JSON格式）
     * 包含：model, temperature, maxTokens, topP, frequencyPenalty, presencePenalty等
     */
    @Column(columnDefinition = "JSON")
    private String modelConfig;

    /**
     * 对话风格（JSON格式）
     * 包含：tone, formality, humorLevel, empathyLevel, creativityLevel等
     */
    @Column(columnDefinition = "JSON")
    private String conversationStyle;

    /**
     * 知识库配置（JSON格式）
     * 包含：knowledgeSources, memorySize, retrievalStrategy等
     */
    @Column(columnDefinition = "JSON")
    private String knowledgeConfig;

    /**
     * 行为模式配置（JSON格式）
     * 包含：behaviorPatterns, decisionMakingStyle, emotionalResponsePattern等
     */
    @Column(columnDefinition = "JSON")
    private String behaviorConfig;

    /**
     * 语音配置（JSON格式）
     * 关联VoicePreset或自定义TTS参数
     */
    @Column(columnDefinition = "JSON")
    private String voiceConfig;

    /**
     * 记忆系统配置（JSON格式）
     * 包含：memoryType, retentionPeriod, importanceWeights等
     */
    @Column(columnDefinition = "JSON")
    private String memoryConfig;

    /**
     * 学习能力配置（JSON格式）
     * 包含：learningRate, adaptationSpeed, feedbackMechanism等
     */
    @Column(columnDefinition = "JSON")
    private String learningConfig;

    /**
     * 目标与动机系统（JSON格式）
     * 包含：primaryGoals, secondaryGoals, motivationLevels等
     */
    @Column(columnDefinition = "JSON")
    private String goalsConfig;

    /**
     * 情感系统配置（JSON格式）
     * 包含：emotionStates, emotionalTriggers, recoveryRate等
     */
    @Column(columnDefinition = "JSON")
    private String emotionConfig;

    /**
     * 社交关系系统（JSON格式）
     * 包含：relationshipNetwork, trustLevels, interactionHistory等
     */
    @Column(columnDefinition = "JSON")
    private String socialConfig;

    /**
     * 活跃状态：ACTIVE-活跃, INACTIVE-未激活, TRAINING-训练中, SLEEPING-休眠
     */
    @Column(nullable = false)
    private String activeStatus = "INACTIVE";

    /**
     * 启用状态：true-启用, false-禁用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 对话次数统计
     */
    @Column(nullable = false)
    private Integer conversationCount = 0;

    /**
     * 总学习时长（秒）
     */
    @Column(nullable = false)
    private Long totalLearningTime = 0L;

    /**
     * 性能评分（0-100）
     */
    private Integer performanceScore;

    /**
     * 上次活跃时间
     */
    private LocalDateTime lastActiveAt;

    /**
     * 版本号
     */
    @Column(nullable = false)
    private String version = "1.0.0";

    /**
     * 元数据（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String metadata;

    /**
     * 关联的小说ID
     */
    private Long novelId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标签（JSON数组格式）
     */
    @Column(columnDefinition = "JSON")
    private String tags;
}