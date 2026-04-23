package com.laoji.novelai.dto.agent;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能体消息数据传输对象
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentMessageDTO {

    private Long id;
    private Long agentId;
    private String sessionId;
    private String messageType;
    private Long senderId;
    private String senderType;
    private Long receiverId;
    private String receiverType;
    private String content;
    private Map<String, Object> metadata;
    private Map<String, Object> sentimentAnalysis;
    private Map<String, Object> intentAnalysis;
    private Map<String, Object> entityAnalysis;
    private Long audioId;
    private Long chapterId;
    private Long eventId;
    private Integer sequenceNumber;
    private Boolean isRead;
    private Boolean isReplied;
    private Long replyToMessageId;
    private Integer qualityScore;
    private String processingStatus;
    private Long processingTime;
    private Long novelId;
    private Long userId;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联信息
    private String agentName;
    private String senderName;
    private String receiverName;
    private String audioUrl;
    private String audioFormat;
    
    // 分析结果
    private String summary;
    private List<String> keyTopics;
    private List<String> keyEntities;
    private String suggestedResponse;
    
    // 上下文信息
    private List<AgentMessageDTO> contextMessages;
    private Map<String, Object> conversationContext;
}