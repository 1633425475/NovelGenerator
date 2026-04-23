package com.laoji.novelai.controller.image;

import com.laoji.novelai.service.comfyui.ComfyUIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ComfyUI控制器
 */
@RestController
@RequestMapping("/api/image/generate")
@RequiredArgsConstructor
@Slf4j
public class ComfyUIController {

    private final ComfyUIService comfyUIService;

    /**
     * 生成图片
     */
    @PostMapping
    public Map<String, Object> generateImage(@RequestBody ComfyUIService.ImageGenerationRequest request) {
        log.info("生成图片请求: {}", request);

        try {
            String imageUrl = comfyUIService.generateImage(request);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", "success");
            result.put("imageUrl", imageUrl);
            result.put("message", "图片生成成功");

            log.info("图片生成成功，URL: {}", imageUrl);
            return result;
        } catch (Exception e) {
            log.error("图片生成失败", e);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", "error");
            result.put("message", "图片生成失败: " + e.getMessage());

            return result;
        }
    }

    /**
     * 批量生成图片
     */
    @PostMapping("/batch")
    public Map<String, Object> batchGenerateImages(@RequestBody ComfyUIService.BatchImageGenerationRequest request) {
        log.info("批量生成图片请求: {}", request);

        try {
            Map<String, Object> result = comfyUIService.batchGenerateImages(request);
            log.info("批量图片生成完成");
            return result;
        } catch (Exception e) {
            log.error("批量图片生成失败", e);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", "error");
            result.put("message", "批量图片生成失败: " + e.getMessage());

            return result;
        }
    }

    /**
     * 获取ComfyUI服务状态
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        log.info("获取ComfyUI服务状态");

        try {
            Map<String, Object> status = comfyUIService.getStatus();
            log.info("ComfyUI服务状态: {}", status);
            return status;
        } catch (Exception e) {
            log.error("获取ComfyUI服务状态失败", e);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", "error");
            result.put("message", "获取ComfyUI服务状态失败: " + e.getMessage());

            return result;
        }
    }

    /**
     * 取消任务
     */
    @PostMapping("/cancel/{promptId}")
    public Map<String, Object> cancelTask(@PathVariable String promptId) {
        log.info("取消任务，prompt_id: {}", promptId);

        try {
            comfyUIService.cancelTask(promptId);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", "success");
            result.put("message", "任务取消成功");

            log.info("任务取消成功");
            return result;
        } catch (Exception e) {
            log.error("取消任务失败", e);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", "error");
            result.put("message", "取消任务失败: " + e.getMessage());

            return result;
        }
    }
}
