package com.laoji.novelai.repository.agent;

import com.laoji.novelai.entity.agent.AgentMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 智能体消息仓库接口
 */
@Repository
public interface AgentMessageRepository extends JpaRepository<AgentMessage, Long>, JpaSpecificationExecutor<AgentMessage> {

    /**
     * 根据会话ID查找消息列表（按序列号排序）
     */
    List<AgentMessage> findBySessionIdAndDeletedFalseOrderBySequenceNumberAsc(String sessionId);

    /**
     * 根据智能体ID查找消息列表
     */
    List<AgentMessage> findByAgentIdAndDeletedFalseOrderByCreatedAtDesc(Long agentId);

    /**
     * 根据发送者ID查找消息列表
     */
    List<AgentMessage> findBySenderIdAndDeletedFalseOrderByCreatedAtDesc(Long senderId);

    /**
     * 根据接收者ID查找消息列表
     */
    List<AgentMessage> findByReceiverIdAndDeletedFalseOrderByCreatedAtDesc(Long receiverId);

    /**
     * 根据消息类型查找消息列表
     */
    List<AgentMessage> findByMessageTypeAndDeletedFalseOrderByCreatedAtDesc(String messageType);

    /**
     * 根据处理状态查找消息列表
     */
    List<AgentMessage> findByProcessingStatusAndDeletedFalseOrderByCreatedAtDesc(String processingStatus);

    /**
     * 根据是否已读查找消息列表
     */
    List<AgentMessage> findByIsReadAndDeletedFalseOrderByCreatedAtDesc(Boolean isRead);

    /**
     * 根据是否已回复查找消息列表
     */
    List<AgentMessage> findByIsRepliedAndDeletedFalseOrderByCreatedAtDesc(Boolean isReplied);

    /**
     * 查找未读消息数量
     */
    Long countByReceiverIdAndIsReadFalseAndDeletedFalse(Long receiverId);

    /**
     * 查找未回复消息数量
     */
    Long countByIsRepliedFalseAndDeletedFalse();

    /**
     * 根据会话ID统计消息数量
     */
    Long countBySessionIdAndDeletedFalse(String sessionId);

    /**
     * 根据智能体ID统计对话次数
     */
    Long countByAgentIdAndDeletedFalse(Long agentId);

    /**
     * 获取最新的消息
     */
    List<AgentMessage> findTop10ByDeletedFalseOrderByCreatedAtDesc();

    /**
     * 根据质量评分筛选消息
     */
    List<AgentMessage> findByQualityScoreGreaterThanAndDeletedFalseOrderByCreatedAtDesc(Integer minScore);

    /**
     * 根据情感分析结果查找消息
     */
    @Query(value = "SELECT * FROM ai_agent_message WHERE JSON_EXTRACT(sentimentAnalysis, '$.sentiment') = ?1 AND is_deleted = false", nativeQuery = true)
    List<AgentMessage> findBySentiment(String sentiment);

    /**
     * 根据意图识别结果查找消息
     */
    @Query(value = "SELECT * FROM ai_agent_message WHERE JSON_EXTRACT(intentAnalysis, '$.intent') = ?1 AND is_deleted = false", nativeQuery = true)
    List<AgentMessage> findByIntent(String intent);

    /**
     * 统计平均处理时间
     */
    @Query(value = "SELECT AVG(processing_time) FROM ai_agent_message WHERE processing_time IS NOT NULL AND is_deleted = false", nativeQuery = true)
    Double getAverageProcessingTime();

    /**
     * 统计消息质量评分分布
     */
    @Query(value = "SELECT quality_score, COUNT(*) FROM ai_agent_message WHERE quality_score IS NOT NULL AND is_deleted = false GROUP BY quality_score", nativeQuery = true)
    List<Object[]> getQualityScoreDistribution();

    /**
     * 查找需要处理的消息（状态为PENDING）
     */
    List<AgentMessage> findByProcessingStatusAndDeletedFalseOrderByCreatedAtAsc(String processingStatus);

    /**
     * 查找超时未处理的消息
     */
    @Query(value = "SELECT * FROM ai_agent_message WHERE processing_status = 'PROCESSING' AND created_at < DATE_SUB(NOW(), INTERVAL 5 MINUTE) AND is_deleted = false", nativeQuery = true)
    List<AgentMessage> findTimeoutMessages();
}