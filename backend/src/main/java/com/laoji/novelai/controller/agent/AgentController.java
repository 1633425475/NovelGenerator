package com.laoji.novelai.controller.agent;

import com.laoji.novelai.controller.BaseController;
import com.laoji.novelai.dto.agent.AgentDTO;
import com.laoji.novelai.dto.agent.AgentMessageDTO;
import com.laoji.novelai.service.agent.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能体角色管理控制器
 */
@RestController
@RequestMapping("/api/novels/{novelId}/agents")
@RequiredArgsConstructor
@Tag(name = "智能体角色管理", description = "AI智能体角色创建、管理、对话等功能")
public class AgentController extends BaseController {

    private final AgentService agentService;

    // ========== 智能体管理 ==========

    @PostMapping
    @Operation(summary = "创建智能体", description = "创建新的AI智能体角色")
    public ResponseEntity<?> createAgent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody AgentDTO agentDTO) {
        try {
            agentDTO.setNovelId(novelId);
            AgentDTO createdAgent = agentService.createAgent(agentDTO);
            return success(createdAgent);
        } catch (Exception e) {
            return error("创建智能体失败: " + e.getMessage());
        }
    }

    @GetMapping("/{agentId}")
    @Operation(summary = "获取智能体详情", description = "获取指定智能体的详细信息")
    public ResponseEntity<?> getAgent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId) {
        try {
            AgentDTO agent = agentService.getAgent(agentId);
            return success(agent);
        } catch (Exception e) {
            return error("获取智能体失败: " + e.getMessage());
        }
    }

    @PutMapping("/{agentId}")
    @Operation(summary = "更新智能体", description = "更新智能体配置和属性")
    public ResponseEntity<?> updateAgent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId,
            @Valid @RequestBody AgentDTO agentDTO) {
        try {
            AgentDTO updatedAgent = agentService.updateAgent(agentId, agentDTO);
            return success(updatedAgent);
        } catch (Exception e) {
            return error("更新智能体失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{agentId}")
    @Operation(summary = "删除智能体", description = "删除指定智能体（逻辑删除）")
    public ResponseEntity<?> deleteAgent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId) {
        try {
            boolean success = agentService.deleteAgent(agentId);
            return success(success);
        } catch (Exception e) {
            return error("删除智能体失败: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "获取智能体列表", description = "获取小说下的智能体列表，支持过滤")
    public ResponseEntity<?> listAgents(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam(required = false) @Parameter(description = "智能体类型") String agentType,
            @RequestParam(required = false) @Parameter(description = "活跃状态") String activeStatus,
            @RequestParam(required = false) @Parameter(description = "启用状态") Boolean enabled,
            @RequestParam(required = false) @Parameter(description = "关键词搜索") String keyword) {
        try {
            Map<String, Object> filters = new HashMap<>();
            if (agentType != null) filters.put("agentType", agentType);
            if (activeStatus != null) filters.put("activeStatus", activeStatus);
            if (enabled != null) filters.put("enabled", enabled);
            if (keyword != null) filters.put("keyword", keyword);
            
            List<AgentDTO> agents = agentService.listAgents(novelId, filters);
            return success(agents);
        } catch (Exception e) {
            return error("获取智能体列表失败: " + e.getMessage());
        }
    }

    // ========== 智能体对话 ==========

    @PostMapping("/{agentId}/messages")
    @Operation(summary = "发送消息", description = "向智能体发送消息并获取回复")
    public ResponseEntity<?> sendMessage(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId,
            @Valid @RequestBody Map<String, Object> request) {
        try {
            String message = (String) request.get("message");
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            if (params == null) {
                params = new HashMap<>();
            }
            
            // 添加novelId到参数
            params.put("novelId", novelId);
            
            AgentMessageDTO response = agentService.sendMessage(agentId, message, params);
            return success(response);
        } catch (Exception e) {
            return error("发送消息失败: " + e.getMessage());
        }
    }

    @PostMapping("/{agentId}/activate")
    @Operation(summary = "激活智能体", description = "激活智能体，使其可以接收消息")
    public ResponseEntity<?> activateAgent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId) {
        try {
            boolean success = agentService.activateAgent(agentId);
            return success(success);
        } catch (Exception e) {
            return error("激活智能体失败: " + e.getMessage());
        }
    }

    @PostMapping("/{agentId}/deactivate")
    @Operation(summary = "停用智能体", description = "停用智能体，暂停其功能")
    public ResponseEntity<?> deactivateAgent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId) {
        try {
            boolean success = agentService.deactivateAgent(agentId);
            return success(success);
        } catch (Exception e) {
            return error("停用智能体失败: " + e.getMessage());
        }
    }

    // ========== 批量操作 ==========

    @PostMapping("/batch/delete")
    @Operation(summary = "批量删除智能体", description = "批量删除多个智能体")
    public ResponseEntity<?> batchDeleteAgents(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestBody Map<String, Object> request) {
        try {
            List<Long> agentIds = (List<Long>) request.get("agentIds");
            Map<String, Object> result = agentService.batchDeleteAgents(agentIds);
            return success(result);
        } catch (Exception e) {
            return error("批量删除智能体失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/update")
    @Operation(summary = "批量更新智能体", description = "批量更新多个智能体的属性")
    public ResponseEntity<?> batchUpdateAgents(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestBody Map<String, Object> request) {
        try {
            List<Long> agentIds = (List<Long>) request.get("agentIds");
            Map<String, Object> updates = (Map<String, Object>) request.get("updates");
            Map<String, Object> result = agentService.batchUpdateAgents(agentIds, updates);
            return success(result);
        } catch (Exception e) {
            return error("批量更新智能体失败: " + e.getMessage());
        }
    }

    // ========== 会话管理 ==========

    @GetMapping("/{agentId}/conversations")
    @Operation(summary = "获取会话历史", description = "获取智能体的对话会话历史")
    public ResponseEntity<?> getConversationHistory(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId,
            @RequestParam(required = false) @Parameter(description = "会话ID") String sessionId,
            @RequestParam(required = false) @Parameter(description = "查询参数") Map<String, Object> params) {
        try {
            if (params == null) {
                params = new HashMap<>();
            }
            if (sessionId != null) {
                params.put("sessionId", sessionId);
            }
            
            List<AgentMessageDTO> messages = agentService.getConversationHistory(sessionId, params);
            return success(messages);
        } catch (Exception e) {
            return error("获取会话历史失败: " + e.getMessage());
        }
    }

    @PostMapping("/{agentId}/conversations")
    @Operation(summary = "创建新会话", description = "创建新的对话会话")
    public ResponseEntity<?> createConversation(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId,
            @RequestBody Map<String, Object> params) {
        try {
            String sessionId = agentService.createConversation(agentId, params);
            return success(sessionId);
        } catch (Exception e) {
            return error("创建会话失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{agentId}/conversations/{sessionId}")
    @Operation(summary = "结束会话", description = "结束指定会话")
    public ResponseEntity<?> endConversation(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId,
            @PathVariable @Parameter(description = "会话ID") String sessionId) {
        try {
            boolean success = agentService.endConversation(sessionId);
            return success(success);
        } catch (Exception e) {
            return error("结束会话失败: " + e.getMessage());
        }
    }

    // ========== 统计分析 ==========

    @GetMapping("/{agentId}/statistics")
    @Operation(summary = "获取智能体统计", description = "获取智能体的使用统计信息")
    public ResponseEntity<?> getAgentStatistics(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId) {
        try {
            Map<String, Object> statistics = agentService.getAgentStatistics(agentId);
            return success(statistics);
        } catch (Exception e) {
            return error("获取智能体统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/{agentId}/capabilities")
    @Operation(summary = "获取智能体能力", description = "获取智能体的能力列表")
    public ResponseEntity<?> getAgentCapabilities(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "智能体ID") Long agentId) {
        try {
            List<String> capabilities = agentService.getAgentCapabilities(agentId);
            return success(capabilities);
        } catch (Exception e) {
            return error("获取智能体能力失败: " + e.getMessage());
        }
    }
}