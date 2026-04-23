package com.laoji.novelai.controller.video;

import com.laoji.novelai.service.comfyui.ComfyUIVideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 视频控制器
 */
@RestController
@RequestMapping("/api/video/generate")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final ComfyUIVideoService comfyUIVideoService;

    /**
     * 生成视频
     */
    @PostMapping
    public Map<String, Object> generateVideo(@RequestBody ComfyUIVideoService.VideoGenerationRequest request) {
        log.info("生成视频请求: {}", request);

        try {
            String videoUrl = comfyUIVideoService.generateVideo(request);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", "success");
            result.put("videoUrl", videoUrl);
            result.put("message", "视频生成成功");

            log.info("视频生成成功，URL: {}", videoUrl);
            return result;
        } catch (Exception e) {
            log.error("视频生成失败", e);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", "error");
            result.put("message", "视频生成失败: " + e.getMessage());

            return result;
        }
    }

    /**
     * 批量生成视频
     */
    @PostMapping("/batch")
    public Map<String, Object> batchGenerateVideos(@RequestBody ComfyUIVideoService.BatchVideoGenerationRequest request) {
        log.info("批量生成视频请求: {}", request);

        try {
            Map<String, Object> result = comfyUIVideoService.batchGenerateVideos(request);
            log.info("批量视频生成完成");
            return result;
        } catch (Exception e) {
            log.error("批量视频生成失败", e);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", "error");
            result.put("message", "批量视频生成失败: " + e.getMessage());

            return result;
        }
    }
}
