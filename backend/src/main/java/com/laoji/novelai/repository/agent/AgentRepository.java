package com.laoji.novelai.repository.agent;

import com.laoji.novelai.entity.agent.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 智能体仓库接口
 */
@Repository
public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

    /**
     * 根据人物角色ID查找智能体
     */
    Optional<Agent> findByCharacterId(Long characterId);

    /**
     * 根据小说ID查找智能体列表
     */
    List<Agent> findByNovelIdAndDeletedFalseOrderByCreatedAtDesc(Long novelId);

    /**
     * 根据用户ID查找智能体列表
     */
    List<Agent> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 根据智能体类型查找列表
     */
    List<Agent> findByAgentTypeAndDeletedFalseOrderByCreatedAtDesc(String agentType);

    /**
     * 根据活跃状态查找列表
     */
    List<Agent> findByActiveStatusAndDeletedFalseOrderByCreatedAtDesc(String activeStatus);

    /**
     * 查找启用的智能体列表
     */
    List<Agent> findByEnabledTrueAndDeletedFalseOrderByCreatedAtDesc();

    /**
     * 根据标签查找智能体（JSON_CONTAINS查询）
     */
    @Query(value = "SELECT * FROM ai_agent WHERE JSON_CONTAINS(tags, ?1) AND is_deleted = false", nativeQuery = true)
    List<Agent> findByTag(String tag);

    /**
     * 统计小说中的智能体数量
     */
    Long countByNovelIdAndDeletedFalse(Long novelId);

    /**
     * 统计活跃智能体数量
     */
    Long countByActiveStatusAndDeletedFalse(String activeStatus);

    /**
     * 获取性能评分最高的智能体
     */
    List<Agent> findByDeletedFalseOrderByPerformanceScoreDesc();

    /**
     * 获取对话次数最多的智能体
     */
    List<Agent> findByDeletedFalseOrderByConversationCountDesc();

    /**
     * 根据名称模糊搜索
     */
    List<Agent> findByAgentNameContainingAndDeletedFalseOrderByCreatedAtDesc(String keyword);

    /**
     * 查找需要训练的智能体（活跃状态为TRAINING）
     */
    List<Agent> findByActiveStatusAndEnabledTrueAndDeletedFalseOrderByCreatedAtDesc(String activeStatus);

    /**
     * 查找长时间未活跃的智能体
     */
    @Query(value = "SELECT * FROM ai_agent WHERE last_active_at < DATE_SUB(NOW(), INTERVAL 7 DAY) AND is_deleted = false", nativeQuery = true)
    List<Agent> findInactiveAgents();
}