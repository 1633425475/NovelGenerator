package com.laoji.novelai.service.agent.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laoji.novelai.dto.agent.AgentDTO;
import com.laoji.novelai.dto.agent.AgentMessageDTO;
import com.laoji.novelai.entity.agent.Agent;
import com.laoji.novelai.entity.agent.AgentMessage;
import com.laoji.novelai.repository.agent.AgentMessageRepository;
import com.laoji.novelai.repository.agent.AgentRepository;
import com.laoji.novelai.service.agent.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 智能体服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final AgentMessageRepository agentMessageRepository;
    private final ObjectMapper objectMapper;

    @Override
    public AgentDTO createAgent(AgentDTO agentDTO) {
        log.info("创建智能体: {}", agentDTO);
        
        // 验证必需字段
        if (agentDTO.getCharacterId() == null) {
            throw new IllegalArgumentException("关联的人物角色ID不能为空");
        }
        if (agentDTO.getAgentName() == null || agentDTO.getAgentName().trim().isEmpty()) {
            throw new IllegalArgumentException("智能体名称不能为空");
        }
        
        // 检查是否已存在相同人物角色的智能体
        Optional<Agent> existingAgent = agentRepository.findByCharacterId(agentDTO.getCharacterId());
        if (existingAgent.isPresent() && !existingAgent.get().getDeleted()) {
            throw new IllegalArgumentException("该人物角色已关联智能体，ID: " + existingAgent.get().getId());
        }
        
        // 转换DTO为实体并设置默认值
        Agent agent = convertToEntity(agentDTO);
        
        // 设置默认值
        if (agent.getAgentType() == null) {
            agent.setAgentType("CHARACTER");
        }
        if (agent.getActiveStatus() == null) {
            agent.setActiveStatus("INACTIVE");
        }
        if (agent.getEnabled() == null) {
            agent.setEnabled(true);
        }
        if (agent.getConversationCount() == null) {
            agent.setConversationCount(0);
        }
        if (agent.getTotalLearningTime() == null) {
            agent.setTotalLearningTime(0L);
        }
        if (agent.getVersion() == null) {
            agent.setVersion("1.0.0");
        }
        if (agent.getLastActiveAt() == null) {
            agent.setLastActiveAt(LocalDateTime.now());
        }
        
        // 保存到数据库
        Agent savedAgent = agentRepository.save(agent);
        log.info("智能体创建成功，ID: {}", savedAgent.getId());
        
        // 返回转换后的DTO
        return convertToDTO(savedAgent);
    }

    @Override
    public AgentDTO updateAgent(Long agentId, AgentDTO agentDTO) {
        log.info("更新智能体: id={}, data={}", agentId, agentDTO);
        
        if (agentId == null) {
            throw new IllegalArgumentException("智能体ID不能为空");
        }
        if (agentDTO == null) {
            throw new IllegalArgumentException("更新数据不能为空");
        }
        
        // 获取现有智能体
        Agent existingAgent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("智能体不存在，ID: " + agentId));
        
        // 检查是否已被逻辑删除
        if (existingAgent.getDeleted()) {
            throw new IllegalArgumentException("智能体已被删除，ID: " + agentId);
        }
        
        // 检查是否正在更新characterId（一般不允许修改）
        if (agentDTO.getCharacterId() != null && !agentDTO.getCharacterId().equals(existingAgent.getCharacterId())) {
            // 检查新的characterId是否已关联其他智能体
            Optional<Agent> agentWithSameCharacter = agentRepository.findByCharacterId(agentDTO.getCharacterId());
            if (agentWithSameCharacter.isPresent() && 
                !agentWithSameCharacter.get().getId().equals(agentId) && 
                !agentWithSameCharacter.get().getDeleted()) {
                throw new IllegalArgumentException("该人物角色已关联其他智能体，ID: " + agentWithSameCharacter.get().getId());
            }
        }
        
        // 使用辅助方法更新实体
        Agent updatedAgent = updateEntityFromDTO(existingAgent, agentDTO);
        
        // 保存更新
        Agent savedAgent = agentRepository.save(updatedAgent);
        log.info("智能体更新成功，ID: {}", savedAgent.getId());
        
        // 返回更新后的DTO
        return convertToDTO(savedAgent);
    }

    @Override
    public AgentDTO getAgent(Long agentId) {
        log.info("获取智能体详情: id={}", agentId);
        
        if (agentId == null) {
            throw new IllegalArgumentException("智能体ID不能为空");
        }
        
        // 从数据库获取智能体（包括未删除的）
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("智能体不存在，ID: " + agentId));
        
        // 检查是否已被逻辑删除
        if (agent.getDeleted()) {
            throw new IllegalArgumentException("智能体已被删除，ID: " + agentId);
        }
        
        // 转换为DTO并返回
        return convertToDTO(agent);
    }

    @Override
    public boolean deleteAgent(Long agentId) {
        log.info("删除智能体: id={}", agentId);
        
        if (agentId == null) {
            throw new IllegalArgumentException("智能体ID不能为空");
        }
        
        // 获取现有智能体
        Agent existingAgent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("智能体不存在，ID: " + agentId));
        
        // 检查是否已被逻辑删除
        if (existingAgent.getDeleted()) {
            log.warn("智能体已被删除，ID: {}", agentId);
            return true; // 已经删除，返回成功
        }
        
        // 执行逻辑删除
        existingAgent.softDelete();
        existingAgent.setEnabled(false);
        existingAgent.setActiveStatus("INACTIVE");
        
        // 保存更新
        agentRepository.save(existingAgent);
        log.info("智能体删除成功，ID: {}", agentId);
        
        return true;
    }

    @Override
    public List<AgentDTO> listAgents(Long novelId, Map<String, Object> filters) {
        log.info("获取智能体列表: novelId={}, filters={}", novelId, filters);
        
        if (novelId == null) {
            throw new IllegalArgumentException("小说ID不能为空");
        }
        
        List<Agent> agents;
        
        // 根据过滤器动态查询
        if (filters != null && !filters.isEmpty()) {
            // 构建动态查询条件
            String agentType = (String) filters.get("agentType");
            String activeStatus = (String) filters.get("activeStatus");
            Boolean enabled = (Boolean) filters.get("enabled");
            String keyword = (String) filters.get("keyword");
            
            // 根据不同的过滤条件查询
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 关键词搜索（按名称模糊匹配）
                agents = agentRepository.findByAgentNameContainingAndDeletedFalseOrderByCreatedAtDesc(keyword.trim());
                // 过滤小说ID
                agents = agents.stream()
                        .filter(agent -> novelId.equals(agent.getNovelId()))
                        .collect(Collectors.toList());
            } else if (agentType != null && !agentType.trim().isEmpty()) {
                // 按智能体类型过滤
                agents = agentRepository.findByAgentTypeAndDeletedFalseOrderByCreatedAtDesc(agentType);
                // 过滤小说ID
                agents = agents.stream()
                        .filter(agent -> novelId.equals(agent.getNovelId()))
                        .collect(Collectors.toList());
            } else if (activeStatus != null && !activeStatus.trim().isEmpty()) {
                // 按活跃状态过滤
                agents = agentRepository.findByActiveStatusAndDeletedFalseOrderByCreatedAtDesc(activeStatus);
                // 过滤小说ID
                agents = agents.stream()
                        .filter(agent -> novelId.equals(agent.getNovelId()))
                        .collect(Collectors.toList());
            } else if (enabled != null) {
                // 按启用状态过滤（启用或禁用）
                if (enabled) {
                    agents = agentRepository.findByEnabledTrueAndDeletedFalseOrderByCreatedAtDesc();
                } else {
                    // 禁用状态的智能体，需要自定义查询
                    // 这里简化处理：先获取所有，再过滤
                    agents = agentRepository.findByNovelIdAndDeletedFalseOrderByCreatedAtDesc(novelId);
                    agents = agents.stream()
                            .filter(agent -> Boolean.FALSE.equals(agent.getEnabled()))
                            .collect(Collectors.toList());
                }
            } else {
                // 无特殊过滤条件，按小说ID查询
                agents = agentRepository.findByNovelIdAndDeletedFalseOrderByCreatedAtDesc(novelId);
            }
        } else {
            // 无过滤条件，直接按小说ID查询
            agents = agentRepository.findByNovelIdAndDeletedFalseOrderByCreatedAtDesc(novelId);
        }
        
        // 转换为DTO列表
        return agents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> searchAgents(Long novelId, String keyword, Map<String, Object> filters) {
        // TODO: 实现搜索智能体逻辑
        return null;
    }

    @Override
    public boolean activateAgent(Long agentId) {
        log.info("激活智能体: id={}", agentId);
        // TODO: 实现激活智能体逻辑
        return false;
    }

    @Override
    public boolean deactivateAgent(Long agentId) {
        log.info("停用智能体: id={}", agentId);
        // TODO: 实现停用智能体逻辑
        return false;
    }

    @Override
    public Map<String, Object> trainAgent(Long agentId, Map<String, Object> trainingData) {
        log.info("训练智能体: id={}, data={}", agentId, trainingData);
        // TODO: 实现训练智能体逻辑
        return null;
    }

    @Override
    public Map<String, Object> getTrainingStatus(Long agentId) {
        // TODO: 实现获取训练状态逻辑
        return null;
    }

    @Override
    public AgentDTO updateAgentConfig(Long agentId, Map<String, Object> config) {
        log.info("更新智能体配置: id={}, config={}", agentId, config);
        // TODO: 实现更新智能体配置逻辑
        return null;
    }

    @Override
    public AgentDTO cloneAgent(Long sourceAgentId, String cloneName, Map<String, Object> params) {
        log.info("克隆智能体: sourceAgentId={}, cloneName={}", sourceAgentId, cloneName);
        // TODO: 实现克隆智能体逻辑
        return null;
    }

    @Override
    public AgentDTO importAgent(Map<String, Object> agentData) {
        log.info("导入智能体: data={}", agentData);
        // TODO: 实现导入智能体逻辑
        return null;
    }

    @Override
    public Map<String, Object> exportAgent(Long agentId, Map<String, Object> params) {
        log.info("导出智能体: id={}", agentId);
        // TODO: 实现导出智能体逻辑
        return null;
    }

    @Override
    public Map<String, Object> evaluateAgent(Long agentId, Map<String, Object> testData) {
        log.info("评估智能体性能: id={}", agentId);
        // TODO: 实现评估智能体性能逻辑
        return null;
    }

    @Override
    public Map<String, Object> getAgentStatistics(Long agentId) {
        // TODO: 实现获取智能体统计信息逻辑
        return null;
    }

    @Override
    public Map<String, Object> batchUpdateAgents(List<Long> agentIds, Map<String, Object> updates) {
        log.info("批量更新智能体: agentIds={}, updates={}", agentIds, updates);
        // TODO: 实现批量更新智能体逻辑
        return null;
    }

    @Override
    public Map<String, Object> batchDeleteAgents(List<Long> agentIds) {
        log.info("批量删除智能体: agentIds={}", agentIds);
        // TODO: 实现批量删除智能体逻辑
        return null;
    }

    @Override
    public Map<String, Object> batchExportAgents(List<Long> agentIds, Map<String, Object> params) {
        log.info("批量导出智能体: agentIds={}", agentIds);
        // TODO: 实现批量导出智能体逻辑
        return null;
    }

    @Override
    public AgentMessageDTO sendMessage(Long agentId, String message, Map<String, Object> params) {
        log.info("发送消息给智能体: agentId={}, message={}", agentId, message);
        
        if (agentId == null) {
            throw new IllegalArgumentException("智能体ID不能为空");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        
        // 获取智能体
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("智能体不存在，ID: " + agentId));
        
        // 检查智能体状态
        if (agent.getDeleted()) {
            throw new IllegalArgumentException("智能体已被删除，ID: " + agentId);
        }
        if (!Boolean.TRUE.equals(agent.getEnabled())) {
            throw new IllegalArgumentException("智能体未启用，ID: " + agentId);
        }
        if (!"ACTIVE".equals(agent.getActiveStatus())) {
            log.warn("智能体未处于活跃状态: {}", agent.getActiveStatus());
        }
        
        // 从参数中获取或生成sessionId
        String sessionId = (String) params.get("sessionId");
        Long userId = (Long) params.get("userId");
        if (sessionId == null || sessionId.trim().isEmpty()) {
            // 生成新的sessionId
            sessionId = "session_" + System.currentTimeMillis() + "_" + agentId;
        }
        if (userId == null) {
            userId = agent.getUserId(); // 默认使用智能体的用户ID
        }
        
        // 1. 保存用户消息
        AgentMessage userMessage = new AgentMessage();
        userMessage.setAgentId(agentId);
        userMessage.setSessionId(sessionId);
        userMessage.setMessageType("USER");
        userMessage.setSenderId(userId);
        userMessage.setSenderType("USER");
        userMessage.setReceiverId(agentId);
        userMessage.setReceiverType("AGENT");
        userMessage.setContent(message);
        userMessage.setMetadata("{}");
        
        // 保存用户消息
        AgentMessage savedUserMessage = agentMessageRepository.save(userMessage);
        
        // 2. 调用AI模型生成回复（模拟实现）
        String aiResponse = generateAiResponse(agent, message, params);
        
        // 3. 保存AI回复消息
        AgentMessage aiMessage = new AgentMessage();
        aiMessage.setAgentId(agentId);
        aiMessage.setSessionId(sessionId);
        aiMessage.setMessageType("AGENT");
        aiMessage.setSenderId(agentId);
        aiMessage.setSenderType("AGENT");
        aiMessage.setReceiverId(userId);
        aiMessage.setReceiverType("USER");
        aiMessage.setContent(aiResponse);
        
        // 设置元数据（分析结果）
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("tokens", aiResponse.length() / 4); // 估算token数
        metadata.put("responseTime", System.currentTimeMillis());
        try {
            aiMessage.setMetadata(objectMapper.writeValueAsString(metadata));
        } catch (JsonProcessingException e) {
            log.error("设置消息元数据失败: {}", e.getMessage(), e);
            aiMessage.setMetadata("{}");
        }
        
        // 保存AI消息
        AgentMessage savedAiMessage = agentMessageRepository.save(aiMessage);
        
        // 4. 更新智能体统计信息
        agent.setConversationCount(agent.getConversationCount() + 1);
        agent.setLastActiveAt(LocalDateTime.now());
        agentRepository.save(agent);
        
        // 5. 转换为DTO并返回
        AgentMessageDTO responseDTO = new AgentMessageDTO();
        responseDTO.setId(savedAiMessage.getId());
        responseDTO.setAgentId(savedAiMessage.getAgentId());
        responseDTO.setSessionId(savedAiMessage.getSessionId());
        responseDTO.setMessageType(savedAiMessage.getMessageType());
        responseDTO.setSenderId(savedAiMessage.getSenderId());
        responseDTO.setSenderType(savedAiMessage.getSenderType());
        responseDTO.setReceiverId(savedAiMessage.getReceiverId());
        responseDTO.setReceiverType(savedAiMessage.getReceiverType());
        responseDTO.setContent(savedAiMessage.getContent());
        responseDTO.setCreatedAt(savedAiMessage.getCreatedAt());
        responseDTO.setUpdatedAt(savedAiMessage.getUpdatedAt());
        
        // 设置分析结果（模拟）
        Map<String, Object> sentimentAnalysis = new HashMap<>();
        sentimentAnalysis.put("sentiment", "neutral");
        sentimentAnalysis.put("confidence", 0.85);
        sentimentAnalysis.put("polarity", 0.2);
        responseDTO.setSentimentAnalysis(sentimentAnalysis);
        
        Map<String, Object> intentAnalysis = new HashMap<>();
        intentAnalysis.put("intent", "respond");
        intentAnalysis.put("confidence", 0.9);
        intentAnalysis.put("category", "dialogue");
        responseDTO.setIntentAnalysis(intentAnalysis);
        
        responseDTO.setSummary("智能体回复了用户的消息");
        responseDTO.setKeyTopics(List.of("对话", "回复"));
        responseDTO.setKeyEntities(List.of());
        
        log.info("消息处理完成，AI回复已保存，消息ID: {}", savedAiMessage.getId());
        return responseDTO;
    }

    @Override
    public String sendMessageAsync(Long agentId, String message, Map<String, Object> params) {
        log.info("异步发送消息给智能体: agentId={}, message={}", agentId, message);
        // TODO: 实现异步发送消息逻辑
        return null;
    }

    @Override
    public AgentMessageDTO getMessageResponse(Long messageId) {
        // TODO: 实现获取消息响应逻辑
        return null;
    }

    @Override
    public List<AgentMessageDTO> getConversationHistory(String sessionId, Map<String, Object> params) {
        // TODO: 实现获取会话历史逻辑
        return null;
    }

    @Override
    public String createConversation(Long agentId, Map<String, Object> params) {
        log.info("创建新会话: agentId={}", agentId);
        // TODO: 实现创建新会话逻辑
        return null;
    }

    @Override
    public boolean endConversation(String sessionId) {
        log.info("结束会话: sessionId={}", sessionId);
        // TODO: 实现结束会话逻辑
        return false;
    }

    @Override
    public List<Map<String, Object>> getActiveConversations(Long agentId) {
        // TODO: 实现获取活跃会话列表逻辑
        return null;
    }

    @Override
    public Map<String, Object> analyzeConversation(String sessionId, Map<String, Object> params) {
        // TODO: 实现分析会话内容逻辑
        return null;
    }

    @Override
    public Map<String, Object> summarizeConversation(String sessionId, Map<String, Object> params) {
        // TODO: 实现总结会话逻辑
        return null;
    }

    @Override
    public Map<String, Object> exportConversation(String sessionId, Map<String, Object> params) {
        log.info("导出会话: sessionId={}", sessionId);
        // TODO: 实现导出会话逻辑
        return null;
    }

    @Override
    public Map<String, Object> cleanupExpiredConversations(Map<String, Object> params) {
        log.info("清理过期会话");
        // TODO: 实现清理过期会话逻辑
        return null;
    }

    @Override
    public Map<String, Object> agentThink(Long agentId, String context, Map<String, Object> params) {
        log.info("智能体思考: agentId={}, context={}", agentId, context);
        // TODO: 实现智能体思考逻辑
        return null;
    }

    @Override
    public Map<String, Object> agentDecision(Long agentId, String situation, Map<String, Object> options) {
        log.info("智能体决策: agentId={}, situation={}", agentId, situation);
        // TODO: 实现智能体决策逻辑
        return null;
    }

    @Override
    public Map<String, Object> agentLearn(Long agentId, Map<String, Object> learningData) {
        log.info("智能体学习: agentId={}, learningData={}", agentId, learningData);
        // TODO: 实现智能体学习逻辑
        return null;
    }

    @Override
    public Map<String, Object> agentMemory(Long agentId, String operation, Map<String, Object> params) {
        log.info("智能体记忆操作: agentId={}, operation={}", agentId, operation);
        // TODO: 实现智能体记忆操作逻辑
        return null;
    }

    @Override
    public Map<String, Object> agentEmotion(Long agentId, String stimulus, Map<String, Object> params) {
        log.info("智能体情感响应: agentId={}, stimulus={}", agentId, stimulus);
        // TODO: 实现智能体情感响应逻辑
        return null;
    }

    @Override
    public Map<String, Object> agentAction(Long agentId, String action, Map<String, Object> params) {
        log.info("智能体行为执行: agentId={}, action={}", agentId, action);
        // TODO: 实现智能体行为执行逻辑
        return null;
    }

    @Override
    public Map<String, Object> agentGoalUpdate(Long agentId, Map<String, Object> goals) {
        log.info("智能体目标更新: agentId={}, goals={}", agentId, goals);
        // TODO: 实现智能体目标更新逻辑
        return null;
    }

    @Override
    public Map<String, Object> processMessageQueue(Map<String, Object> params) {
        log.info("处理消息队列");
        // TODO: 实现处理消息队列逻辑
        return null;
    }

    @Override
    public Map<String, Object> retryFailedMessages(List<Long> messageIds) {
        log.info("重试失败消息: messageIds={}", messageIds);
        // TODO: 实现重试失败消息逻辑
        return null;
    }

    @Override
    public Map<String, Object> batchMarkMessagesRead(List<Long> messageIds) {
        log.info("批量标记消息已读: messageIds={}", messageIds);
        // TODO: 实现批量标记消息已读逻辑
        return null;
    }

    @Override
    public Map<String, Object> batchDeleteMessages(List<Long> messageIds) {
        log.info("批量删除消息: messageIds={}", messageIds);
        // TODO: 实现批量删除消息逻辑
        return null;
    }

    @Override
    public Map<String, Object> evaluateMessageQuality(Long messageId, Map<String, Object> criteria) {
        // TODO: 实现消息质量评估逻辑
        return null;
    }

    @Override
    public Map<String, Object> analyzeMessageSentiment(Long messageId) {
        // TODO: 实现消息情感分析逻辑
        return null;
    }

    @Override
    public Map<String, Object> analyzeMessageIntent(Long messageId) {
        // TODO: 实现消息意图识别逻辑
        return null;
    }

    @Override
    public Map<String, Object> analyzeMessageEntities(Long messageId) {
        // TODO: 实现消息实体识别逻辑
        return null;
    }

    @Override
    public Map<String, Object> getSystemStatus() {
        // TODO: 实现获取系统状态逻辑
        return null;
    }

    @Override
    public Map<String, Object> healthCheck() {
        // TODO: 实现健康检查逻辑
        return null;
    }

    @Override
    public Map<String, Object> getSystemConfiguration() {
        // TODO: 实现获取系统配置逻辑
        return null;
    }

    @Override
    public boolean updateSystemConfiguration(Map<String, Object> config) {
        log.info("更新系统配置");
        // TODO: 实现更新系统配置逻辑
        return false;
    }

    @Override
    public Map<String, Object> cleanupSystemResources(Map<String, Object> params) {
        log.info("清理系统资源");
        // TODO: 实现清理系统资源逻辑
        return null;
    }

    @Override
    public Map<String, Object> backupAgentData(Map<String, Object> params) {
        log.info("备份智能体数据");
        // TODO: 实现备份智能体数据逻辑
        return null;
    }

    @Override
    public Map<String, Object> restoreAgentData(Map<String, Object> params) {
        log.info("恢复智能体数据");
        // TODO: 实现恢复智能体数据逻辑
        return null;
    }

    @Override
    public Map<String, Object> systemUpgrade(Map<String, Object> upgradePlan) {
        log.info("系统升级");
        // TODO: 实现系统升级逻辑
        return null;
    }

    @Override
    public Map<String, Object> validateAgentConfig(Map<String, Object> config) {
        // TODO: 实现验证智能体配置逻辑
        return null;
    }

    @Override
    public String generateAgentName(Map<String, Object> params) {
        // TODO: 实现生成智能体名称逻辑
        return null;
    }

    @Override
    public Map<String, Object> estimateAgentCost(Long agentId, Map<String, Object> usage) {
        // TODO: 实现估算智能体成本逻辑
        return null;
    }

    @Override
    public List<String> getAgentCapabilities(Long agentId) {
        // TODO: 实现获取智能体能力列表逻辑
        return null;
    }

    @Override
    public Map<String, Object> checkAgentCompatibility(Long agentId1, Long agentId2) {
        // TODO: 实现检查智能体兼容性逻辑
        return null;
    }

    @Override
    public Map<String, Object> agentCollaboration(List<Long> agentIds, String task, Map<String, Object> params) {
        log.info("智能体协作: agentIds={}, task={}", agentIds, task);
        // TODO: 实现智能体协作逻辑
        return null;
    }

    // ========== 辅助方法 ==========

    /**
     * 将Agent实体转换为AgentDTO
     */
    private AgentDTO convertToDTO(Agent agent) {
        if (agent == null) {
            return null;
        }

        AgentDTO dto = new AgentDTO();
        dto.setId(agent.getId());
        dto.setCharacterId(agent.getCharacterId());
        dto.setAgentName(agent.getAgentName());
        dto.setAgentType(agent.getAgentType());
        dto.setActiveStatus(agent.getActiveStatus());
        dto.setEnabled(agent.getEnabled());
        dto.setConversationCount(agent.getConversationCount());
        dto.setTotalLearningTime(agent.getTotalLearningTime());
        dto.setPerformanceScore(agent.getPerformanceScore());
        dto.setLastActiveAt(agent.getLastActiveAt());
        dto.setVersion(agent.getVersion());
        dto.setNovelId(agent.getNovelId());
        dto.setUserId(agent.getUserId());
        dto.setCreatedAt(agent.getCreatedAt());
        dto.setUpdatedAt(agent.getUpdatedAt());

        // 转换JSON字段
        try {
            if (agent.getModelConfig() != null) {
                dto.setModelConfig(objectMapper.readValue(agent.getModelConfig(), Map.class));
            }
            if (agent.getConversationStyle() != null) {
                dto.setConversationStyle(objectMapper.readValue(agent.getConversationStyle(), Map.class));
            }
            if (agent.getKnowledgeConfig() != null) {
                dto.setKnowledgeConfig(objectMapper.readValue(agent.getKnowledgeConfig(), Map.class));
            }
            if (agent.getBehaviorConfig() != null) {
                dto.setBehaviorConfig(objectMapper.readValue(agent.getBehaviorConfig(), Map.class));
            }
            if (agent.getVoiceConfig() != null) {
                dto.setVoiceConfig(objectMapper.readValue(agent.getVoiceConfig(), Map.class));
            }
            if (agent.getMemoryConfig() != null) {
                dto.setMemoryConfig(objectMapper.readValue(agent.getMemoryConfig(), Map.class));
            }
            if (agent.getLearningConfig() != null) {
                dto.setLearningConfig(objectMapper.readValue(agent.getLearningConfig(), Map.class));
            }
            if (agent.getGoalsConfig() != null) {
                dto.setGoalsConfig(objectMapper.readValue(agent.getGoalsConfig(), Map.class));
            }
            if (agent.getEmotionConfig() != null) {
                dto.setEmotionConfig(objectMapper.readValue(agent.getEmotionConfig(), Map.class));
            }
            if (agent.getSocialConfig() != null) {
                dto.setSocialConfig(objectMapper.readValue(agent.getSocialConfig(), Map.class));
            }
            if (agent.getMetadata() != null) {
                dto.setMetadata(objectMapper.readValue(agent.getMetadata(), Map.class));
            }
            if (agent.getTags() != null) {
                dto.setTags(objectMapper.readValue(agent.getTags(), List.class));
            }
        } catch (JsonProcessingException e) {
            log.error("转换JSON字段失败: {}", e.getMessage(), e);
        }

        // TODO: 添加关联的人物信息和统计信息（需要额外的查询）

        return dto;
    }

    /**
     * 将AgentDTO转换为Agent实体
     */
    private Agent convertToEntity(AgentDTO dto) {
        if (dto == null) {
            return null;
        }

        Agent agent = new Agent();
        agent.setId(dto.getId());
        agent.setCharacterId(dto.getCharacterId());
        agent.setAgentName(dto.getAgentName());
        agent.setAgentType(dto.getAgentType() != null ? dto.getAgentType() : "CHARACTER");
        agent.setActiveStatus(dto.getActiveStatus() != null ? dto.getActiveStatus() : "INACTIVE");
        agent.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        agent.setConversationCount(dto.getConversationCount() != null ? dto.getConversationCount() : 0);
        agent.setTotalLearningTime(dto.getTotalLearningTime() != null ? dto.getTotalLearningTime() : 0L);
        agent.setPerformanceScore(dto.getPerformanceScore());
        agent.setLastActiveAt(dto.getLastActiveAt());
        agent.setVersion(dto.getVersion() != null ? dto.getVersion() : "1.0.0");
        agent.setNovelId(dto.getNovelId());
        agent.setUserId(dto.getUserId());

        // 转换JSON字段
        try {
            if (dto.getModelConfig() != null) {
                agent.setModelConfig(objectMapper.writeValueAsString(dto.getModelConfig()));
            }
            if (dto.getConversationStyle() != null) {
                agent.setConversationStyle(objectMapper.writeValueAsString(dto.getConversationStyle()));
            }
            if (dto.getKnowledgeConfig() != null) {
                agent.setKnowledgeConfig(objectMapper.writeValueAsString(dto.getKnowledgeConfig()));
            }
            if (dto.getBehaviorConfig() != null) {
                agent.setBehaviorConfig(objectMapper.writeValueAsString(dto.getBehaviorConfig()));
            }
            if (dto.getVoiceConfig() != null) {
                agent.setVoiceConfig(objectMapper.writeValueAsString(dto.getVoiceConfig()));
            }
            if (dto.getMemoryConfig() != null) {
                agent.setMemoryConfig(objectMapper.writeValueAsString(dto.getMemoryConfig()));
            }
            if (dto.getLearningConfig() != null) {
                agent.setLearningConfig(objectMapper.writeValueAsString(dto.getLearningConfig()));
            }
            if (dto.getGoalsConfig() != null) {
                agent.setGoalsConfig(objectMapper.writeValueAsString(dto.getGoalsConfig()));
            }
            if (dto.getEmotionConfig() != null) {
                agent.setEmotionConfig(objectMapper.writeValueAsString(dto.getEmotionConfig()));
            }
            if (dto.getSocialConfig() != null) {
                agent.setSocialConfig(objectMapper.writeValueAsString(dto.getSocialConfig()));
            }
            if (dto.getMetadata() != null) {
                agent.setMetadata(objectMapper.writeValueAsString(dto.getMetadata()));
            }
            if (dto.getTags() != null) {
                agent.setTags(objectMapper.writeValueAsString(dto.getTags()));
            }
        } catch (JsonProcessingException e) {
            log.error("转换JSON字段失败: {}", e.getMessage(), e);
        }

        return agent;
    }

    /**
     * 更新Agent实体，保留原有值（如果新值为null）
     */
    private Agent updateEntityFromDTO(Agent existingAgent, AgentDTO dto) {
        if (existingAgent == null || dto == null) {
            return existingAgent;
        }

        if (dto.getAgentName() != null) {
            existingAgent.setAgentName(dto.getAgentName());
        }
        if (dto.getAgentType() != null) {
            existingAgent.setAgentType(dto.getAgentType());
        }
        if (dto.getActiveStatus() != null) {
            existingAgent.setActiveStatus(dto.getActiveStatus());
        }
        if (dto.getEnabled() != null) {
            existingAgent.setEnabled(dto.getEnabled());
        }
        if (dto.getConversationCount() != null) {
            existingAgent.setConversationCount(dto.getConversationCount());
        }
        if (dto.getTotalLearningTime() != null) {
            existingAgent.setTotalLearningTime(dto.getTotalLearningTime());
        }
        if (dto.getPerformanceScore() != null) {
            existingAgent.setPerformanceScore(dto.getPerformanceScore());
        }
        if (dto.getLastActiveAt() != null) {
            existingAgent.setLastActiveAt(dto.getLastActiveAt());
        }
        if (dto.getVersion() != null) {
            existingAgent.setVersion(dto.getVersion());
        }
        if (dto.getNovelId() != null) {
            existingAgent.setNovelId(dto.getNovelId());
        }
        if (dto.getUserId() != null) {
            existingAgent.setUserId(dto.getUserId());
        }

        // 更新JSON字段
        try {
            if (dto.getModelConfig() != null) {
                existingAgent.setModelConfig(objectMapper.writeValueAsString(dto.getModelConfig()));
            }
            if (dto.getConversationStyle() != null) {
                existingAgent.setConversationStyle(objectMapper.writeValueAsString(dto.getConversationStyle()));
            }
            if (dto.getKnowledgeConfig() != null) {
                existingAgent.setKnowledgeConfig(objectMapper.writeValueAsString(dto.getKnowledgeConfig()));
            }
            if (dto.getBehaviorConfig() != null) {
                existingAgent.setBehaviorConfig(objectMapper.writeValueAsString(dto.getBehaviorConfig()));
            }
            if (dto.getVoiceConfig() != null) {
                existingAgent.setVoiceConfig(objectMapper.writeValueAsString(dto.getVoiceConfig()));
            }
            if (dto.getMemoryConfig() != null) {
                existingAgent.setMemoryConfig(objectMapper.writeValueAsString(dto.getMemoryConfig()));
            }
            if (dto.getLearningConfig() != null) {
                existingAgent.setLearningConfig(objectMapper.writeValueAsString(dto.getLearningConfig()));
            }
            if (dto.getGoalsConfig() != null) {
                existingAgent.setGoalsConfig(objectMapper.writeValueAsString(dto.getGoalsConfig()));
            }
            if (dto.getEmotionConfig() != null) {
                existingAgent.setEmotionConfig(objectMapper.writeValueAsString(dto.getEmotionConfig()));
            }
            if (dto.getSocialConfig() != null) {
                existingAgent.setSocialConfig(objectMapper.writeValueAsString(dto.getSocialConfig()));
            }
            if (dto.getMetadata() != null) {
                existingAgent.setMetadata(objectMapper.writeValueAsString(dto.getMetadata()));
            }
            if (dto.getTags() != null) {
                existingAgent.setTags(objectMapper.writeValueAsString(dto.getTags()));
            }
        } catch (JsonProcessingException e) {
            log.error("更新JSON字段失败: {}", e.getMessage(), e);
        }

        return existingAgent;
    }

    /**
     * 生成AI回复（模拟实现）
     * 实际项目中应集成真实的AI模型服务
     */
    private String generateAiResponse(Agent agent, String userMessage, Map<String, Object> params) {
        log.info("生成AI回复，智能体: {}, 用户消息长度: {}", agent.getAgentName(), userMessage.length());
        
        // 模拟基于智能体配置的回复
        String agentName = agent.getAgentName();
        String agentType = agent.getAgentType();
        
        // 根据智能体类型生成不同的回复
        String response;
        switch (agentType) {
            case "CHARACTER":
                response = String.format("【%s】说：\"%s\"\n\n作为%s，我理解你的意思。让我思考一下如何回应...", 
                    agentName, 
                    userMessage,
                    agentName);
                break;
            case "ASSISTANT":
                response = String.format("助理回复：\n\n您好！关于您提到的\"%s\"，我可以提供以下信息或帮助：\n\n1. 这是一个重要的对话主题\n2. 我建议进一步探讨相关细节\n3. 您是否需要更具体的帮助？", 
                    userMessage.length() > 50 ? userMessage.substring(0, 50) + "..." : userMessage);
                break;
            case "NARRATOR":
                response = String.format("旁白：\n\n场景继续发展，用户表达了\"%s\"的意思。这为故事增添了新的维度，让我们看看接下来会发生什么...", 
                    userMessage.length() > 30 ? userMessage.substring(0, 30) + "..." : userMessage);
                break;
            case "SYSTEM":
                response = String.format("系统消息：\n\n收到用户输入：%s\n\n处理状态：已接收\n处理时间：%s\n智能体：%s", 
                    userMessage,
                    LocalDateTime.now(),
                    agentName);
                break;
            default:
                response = String.format("【%s】回复：\n\n我收到了你的消息：\"%s\"\n\n我会认真考虑并给出恰当的回应。", 
                    agentName,
                    userMessage.length() > 100 ? userMessage.substring(0, 100) + "..." : userMessage);
        }
        
        // 添加一些随机性，使回复看起来更自然
        String[] connectors = {"另外，", "同时，", "值得一提的是，", "此外，", "另一方面，"};
        String connector = connectors[(int) (System.currentTimeMillis() % connectors.length)];
        
        String[] endings = {"期待您的继续交流。", "希望这能帮助您。", "请告诉我您的想法。", "我们可以继续深入探讨这个话题。", "这是我的初步回应。"};
        String ending = endings[(int) (System.currentTimeMillis() % endings.length)];
        
        response += "\n\n" + connector + ending;
        
        return response;
    }
}