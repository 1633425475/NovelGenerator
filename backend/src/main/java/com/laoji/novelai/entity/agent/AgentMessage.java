package com.laoji.novelai.entity.agent;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 智能体对话消息实体
 */
@Entity
@Table(name = "ai_agent_message")
@Data
@EqualsAndHashCode(callSuper = true)
public class AgentMessage extends BaseEntity {

    /**
     * 关联的智能体ID
     */
    @Column(nullable = false)
    private Long agentId;

    /**
     * 对话会话ID
     */
    @Column(nullable = false)
    private String sessionId;

    /**
     * 消息类型：USER-用户消息, AGENT-智能体回复, SYSTEM-系统消息
     */
    @Column(nullable = false)
    private String messageType;

    /**
     * 发送者ID（用户ID或智能体ID）
     */
    @Column(nullable = false)
    private Long senderId;

    /**
     * 发送者类型：USER-用户, AGENT-智能体, SYSTEM-系统
     */
    @Column(nullable = false)
    private String senderType;

    /**
     * 接收者ID（用户ID或智能体ID）
     */
    private Long receiverId;

    /**
     * 接收者类型：USER-用户, AGENT-智能体, SYSTEM-系统
     */
    private String receiverType;

    /**
     * 消息内容（文本）
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 消息元数据（JSON格式）
     * 包含：tokens, sentiment, topics, entities等
     */
    @Column(columnDefinition = "JSON")
    private String metadata;

    /**
     * 情感分析结果（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String sentimentAnalysis;

    /**
     * 意图识别结果（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String intentAnalysis;

    /**
     * 实体识别结果（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String entityAnalysis;

    /**
     * 关联的音频ID（如果有语音消息）
     */
    private Long audioId;

    /**
     * 关联的章节ID
     */
    private Long chapterId;

    /**
     * 关联的事件ID
     */
    private Long eventId;

    /**
     * 消息序列号（在会话中的顺序）
     */
    @Column(nullable = false)
    private Integer sequenceNumber;

    /**
     * 是否已读
     */
    @Column(nullable = false)
    private Boolean isRead = false;

    /**
     * 是否已回复
     */
    @Column(nullable = false)
    private Boolean isReplied = false;

    /**
     * 回复消息ID（如果是回复消息）
     */
    private Long replyToMessageId;

    /**
     * 消息质量评分（0-100）
     */
    private Integer qualityScore;

    /**
     * 处理状态：PENDING-待处理, PROCESSING-处理中, COMPLETED-已完成, FAILED-失败
     */
    @Column(nullable = false)
    private String processingStatus = "PENDING";

    /**
     * 处理耗时（毫秒）
     */
    private Long processingTime;

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