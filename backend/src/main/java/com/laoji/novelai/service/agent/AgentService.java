package com.laoji.novelai.service.agent;

import com.laoji.novelai.dto.agent.AgentDTO;
import com.laoji.novelai.dto.agent.AgentMessageDTO;

import java.util.List;
import java.util.Map;

/**
 * 智能体服务接口
 */
public interface AgentService {

    // ========== 智能体管理 ==========
    
    /**
     * 创建智能体
     */
    AgentDTO createAgent(AgentDTO agentDTO);
    
    /**
     * 更新智能体
     */
    AgentDTO updateAgent(Long agentId, AgentDTO agentDTO);
    
    /**
     * 获取智能体
     */
    AgentDTO getAgent(Long agentId);
    
    /**
     * 删除智能体
     */
    boolean deleteAgent(Long agentId);
    
    /**
     * 获取智能体列表
     */
    List<AgentDTO> listAgents(Long novelId, Map<String, Object> filters);
    
    /**
     * 搜索智能体
     */
    List<AgentDTO> searchAgents(Long novelId, String keyword, Map<String, Object> filters);
    
    /**
     * 激活智能体
     */
    boolean activateAgent(Long agentId);
    
    /**
     * 停用智能体
     */
    boolean deactivateAgent(Long agentId);
    
    /**
     * 训练智能体
     */
    Map<String, Object> trainAgent(Long agentId, Map<String, Object> trainingData);
    
    /**
     * 获取训练状态
     */
    Map<String, Object> getTrainingStatus(Long agentId);
    
    /**
     * 更新智能体配置
     */
    AgentDTO updateAgentConfig(Long agentId, Map<String, Object> config);
    
    /**
     * 克隆智能体
     */
    AgentDTO cloneAgent(Long sourceAgentId, String cloneName, Map<String, Object> params);
    
    /**
     * 导入智能体
     */
    AgentDTO importAgent(Map<String, Object> agentData);
    
    /**
     * 导出智能体
     */
    Map<String, Object> exportAgent(Long agentId, Map<String, Object> params);
    
    /**
     * 评估智能体性能
     */
    Map<String, Object> evaluateAgent(Long agentId, Map<String, Object> testData);
    
    /**
     * 获取智能体统计信息
     */
    Map<String, Object> getAgentStatistics(Long agentId);
    
    /**
     * 批量操作
     */
    Map<String, Object> batchUpdateAgents(List<Long> agentIds, Map<String, Object> updates);
    
    Map<String, Object> batchDeleteAgents(List<Long> agentIds);
    
    Map<String, Object> batchExportAgents(List<Long> agentIds, Map<String, Object> params);
    
    // ========== 对话管理 ==========
    
    /**
     * 发送消息给智能体
     */
    AgentMessageDTO sendMessage(Long agentId, String message, Map<String, Object> params);
    
    /**
     * 发送消息（异步）
     */
    String sendMessageAsync(Long agentId, String message, Map<String, Object> params);
    
    /**
     * 获取消息响应
     */
    AgentMessageDTO getMessageResponse(Long messageId);
    
    /**
     * 获取会话历史
     */
    List<AgentMessageDTO> getConversationHistory(String sessionId, Map<String, Object> params);
    
    /**
     * 创建新会话
     */
    String createConversation(Long agentId, Map<String, Object> params);
    
    /**
     * 结束会话
     */
    boolean endConversation(String sessionId);
    
    /**
     * 获取活跃会话列表
     */
    List<Map<String, Object>> getActiveConversations(Long agentId);
    
    /**
     * 分析会话内容
     */
    Map<String, Object> analyzeConversation(String sessionId, Map<String, Object> params);
    
    /**
     * 总结会话
     */
    Map<String, Object> summarizeConversation(String sessionId, Map<String, Object> params);
    
    /**
     * 导出会话
     */
    Map<String, Object> exportConversation(String sessionId, Map<String, Object> params);
    
    /**
     * 清理过期会话
     */
    Map<String, Object> cleanupExpiredConversations(Map<String, Object> params);
    
    // ========== 智能体能力 ==========
    
    /**
     * 智能体思考（内部推理）
     */
    Map<String, Object> agentThink(Long agentId, String context, Map<String, Object> params);
    
    /**
     * 智能体决策
     */
    Map<String, Object> agentDecision(Long agentId, String situation, Map<String, Object> options);
    
    /**
     * 智能体学习
     */
    Map<String, Object> agentLearn(Long agentId, Map<String, Object> learningData);
    
    /**
     * 智能体记忆操作
     */
    Map<String, Object> agentMemory(Long agentId, String operation, Map<String, Object> params);
    
    /**
     * 智能体情感响应
     */
    Map<String, Object> agentEmotion(Long agentId, String stimulus, Map<String, Object> params);
    
    /**
     * 智能体行为执行
     */
    Map<String, Object> agentAction(Long agentId, String action, Map<String, Object> params);
    
    /**
     * 智能体目标更新
     */
    Map<String, Object> agentGoalUpdate(Long agentId, Map<String, Object> goals);
    
    // ========== 消息处理 ==========
    
    /**
     * 处理消息队列
     */
    Map<String, Object> processMessageQueue(Map<String, Object> params);
    
    /**
     * 重试失败消息
     */
    Map<String, Object> retryFailedMessages(List<Long> messageIds);
    
    /**
     * 批量标记消息已读
     */
    Map<String, Object> batchMarkMessagesRead(List<Long> messageIds);
    
    /**
     * 批量删除消息
     */
    Map<String, Object> batchDeleteMessages(List<Long> messageIds);
    
    /**
     * 消息质量评估
     */
    Map<String, Object> evaluateMessageQuality(Long messageId, Map<String, Object> criteria);
    
    /**
     * 消息情感分析
     */
    Map<String, Object> analyzeMessageSentiment(Long messageId);
    
    /**
     * 消息意图识别
     */
    Map<String, Object> analyzeMessageIntent(Long messageId);
    
    /**
     * 消息实体识别
     */
    Map<String, Object> analyzeMessageEntities(Long messageId);
    
    // ========== 系统管理 ==========
    
    /**
     * 获取系统状态
     */
    Map<String, Object> getSystemStatus();
    
    /**
     * 健康检查
     */
    Map<String, Object> healthCheck();
    
    /**
     * 系统配置
     */
    Map<String, Object> getSystemConfiguration();
    
    /**
     * 更新系统配置
     */
    boolean updateSystemConfiguration(Map<String, Object> config);
    
    /**
     * 清理系统资源
     */
    Map<String, Object> cleanupSystemResources(Map<String, Object> params);
    
    /**
     * 备份智能体数据
     */
    Map<String, Object> backupAgentData(Map<String, Object> params);
    
    /**
     * 恢复智能体数据
     */
    Map<String, Object> restoreAgentData(Map<String, Object> params);
    
    /**
     * 系统升级
     */
    Map<String, Object> systemUpgrade(Map<String, Object> upgradePlan);
    
    // ========== 工具方法 ==========
    
    /**
     * 验证智能体配置
     */
    Map<String, Object> validateAgentConfig(Map<String, Object> config);
    
    /**
     * 生成智能体名称
     */
    String generateAgentName(Map<String, Object> params);
    
    /**
     * 估算智能体成本
     */
    Map<String, Object> estimateAgentCost(Long agentId, Map<String, Object> usage);
    
    /**
     * 获取智能体能力列表
     */
    List<String> getAgentCapabilities(Long agentId);
    
    /**
     * 检查智能体兼容性
     */
    Map<String, Object> checkAgentCompatibility(Long agentId1, Long agentId2);
    
    /**
     * 智能体协作
     */
    Map<String, Object> agentCollaboration(List<Long> agentIds, String task, Map<String, Object> params);
}