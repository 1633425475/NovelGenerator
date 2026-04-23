package com.laoji.novelai.dto.agent;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能体数据传输对象
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentDTO {

    private Long id;
    private Long characterId;
    private String agentName;
    private String agentType;
    private Map<String, Object> modelConfig;
    private Map<String, Object> conversationStyle;
    private Map<String, Object> knowledgeConfig;
    private Map<String, Object> behaviorConfig;
    private Map<String, Object> voiceConfig;
    private Map<String, Object> memoryConfig;
    private Map<String, Object> learningConfig;
    private Map<String, Object> goalsConfig;
    private Map<String, Object> emotionConfig;
    private Map<String, Object> socialConfig;
    private String activeStatus;
    private Boolean enabled;
    private Integer conversationCount;
    private Long totalLearningTime;
    private Integer performanceScore;
    private LocalDateTime lastActiveAt;
    private String version;
    private Map<String, Object> metadata;
    private Long novelId;
    private Long userId;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联的人物信息
    private String characterName;
    private String characterGender;
    private String characterPersonality;
    
    // 统计信息
    private Long messageCount;
    private Long sessionCount;
    private Double averageResponseTime;
    private Double averageQualityScore;
    
    // 系统信息
    private String systemStatus;
    private String healthStatus;
    private List<String> capabilities;
}