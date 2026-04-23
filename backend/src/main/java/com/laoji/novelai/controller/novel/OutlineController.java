package com.laoji.novelai.controller.novel;

import com.laoji.novelai.controller.BaseController;
import com.laoji.novelai.dto.novel.OutlineGenerateRequest;
import com.laoji.novelai.service.novel.OutlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 小说大纲控制器
 */
@RestController
@RequestMapping("/novel/outline")
@RequiredArgsConstructor
@Tag(name = "小说大纲管理", description = "小说大纲的生成、查询、更新等操作")
public class OutlineController extends BaseController {

    private final OutlineService outlineService;

    @PostMapping("/generate")
    @Operation(summary = "生成小说大纲", description = "根据创意描述、风格等参数生成小说大纲")
    public ResponseEntity<?> generateOutline(
            @Valid @RequestBody OutlineGenerateRequest request) {
        try {
            if (Boolean.TRUE.equals(request.getAsync())) {
                String taskId = outlineService.generateOutlineAsync(request);
                return success(Map.of(
                        "taskId", taskId,
                        "message", "大纲生成任务已提交，请使用taskId查询结果"
                ));
            } else {
                var response = outlineService.generateOutline(request);
                return  success(response);
            }
        } catch (Exception e) {
            return error("生成大纲失败: " + e.getMessage());
        }
    }

    @GetMapping("/async-result/{taskId}")
    @Operation(summary = "获取异步任务结果", description = "根据任务ID获取异步生成大纲的结果")
    public ResponseEntity<?> getAsyncResult(
            @PathVariable @Parameter(description = "任务ID") String taskId) {
        try {
            var response = outlineService.getAsyncResult(taskId);
            return  success(response);
        } catch (Exception e) {
            return error("获取任务结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/{outlineId}")
    @Operation(summary = "获取大纲详情", description = "根据大纲ID获取大纲详细信息")
    public ResponseEntity<?> getOutlineDetail(
            @PathVariable @Parameter(description = "大纲ID") Long outlineId) {
        try {
            var response = outlineService.getOutlineDetail(outlineId);
            return (ResponseEntity<?>) success(response);
        } catch (Exception e) {
            return notFound("大纲不存在: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户大纲列表", description = "获取当前用户的所有小说大纲列表")
    public ResponseEntity<?> getUserOutlines() {
        try {
            // TODO: 从当前登录用户获取userId
            Long userId = 1L;
            var outlines = outlineService.getUserOutlines(userId);
            return success(outlines);
        } catch (Exception e) {
            return error("获取大纲列表失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{outlineId}")
    @Operation(summary = "删除大纲", description = "根据大纲ID删除大纲（逻辑删除）")
    public ResponseEntity<?> deleteOutline(
            @PathVariable @Parameter(description = "大纲ID") Long outlineId) {
        try {
            outlineService.deleteOutline(outlineId);
            return success("大纲删除成功");
        } catch (Exception e) {
            return error("删除大纲失败: " + e.getMessage());
        }
    }

    @PutMapping("/{outlineId}")
    @Operation(summary = "更新大纲", description = "根据创意描述重新生成大纲（创建新版本）")
    public ResponseEntity<?> updateOutline(
            @PathVariable @Parameter(description = "大纲ID") Long outlineId,
            @Valid @RequestBody OutlineGenerateRequest request) {
        try {
            var response = outlineService.updateOutline(outlineId, request);
            return success(response);
        } catch (Exception e) {
            return error("更新大纲失败: " + e.getMessage());
        }
    }

    @GetMapping("/{outlineId}/versions")
    @Operation(summary = "获取大纲版本历史", description = "获取指定大纲的所有版本历史")
    public ResponseEntity<?> getOutlineVersions(
            @PathVariable @Parameter(description = "大纲ID") Long outlineId) {
        try {
            var versions = outlineService.getOutlineVersions(outlineId);
            return success(versions);
        } catch (Exception e) {
            return error("获取版本历史失败: " + e.getMessage());
        }
    }

    // 辅助方法
    public ResponseEntity<Map<String, Object>> success(Object data) {
        return super.success(data);
    }

    protected ResponseEntity<Map<String, Object>> error(String message) {
        return super.error(message);
    }

    protected ResponseEntity<Map<String, Object>> notFound(String message) {
        return super.notFound(message);
    }
}