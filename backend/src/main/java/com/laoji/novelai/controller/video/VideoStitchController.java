package com.laoji.novelai.controller.video;

import com.laoji.novelai.entity.video.FinalVideo;
import com.laoji.novelai.service.video.VideoStitchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 视频拼接控制器
 */
@RestController
@RequestMapping("/api/video/stitch")
@RequiredArgsConstructor
@Slf4j
public class VideoStitchController {

    private final VideoStitchService videoStitchService;

    // ========== 视频拼接管理 ==========

    /**
     * 创建最终视频
     */
    @PostMapping("/final-video")
    public FinalVideo createFinalVideo(@RequestBody FinalVideo finalVideo) {
        log.info("创建最终视频请求: {}", finalVideo);
        return videoStitchService.createFinalVideo(finalVideo);
    }

    /**
     * 更新最终视频
     */
    @PutMapping("/final-video/{videoId}")
    public FinalVideo updateFinalVideo(@PathVariable Long videoId, @RequestBody FinalVideo finalVideo) {
        log.info("更新最终视频请求: id={}, data={}", videoId, finalVideo);
        return videoStitchService.updateFinalVideo(videoId, finalVideo);
    }

    /**
     * 获取最终视频
     */
    @GetMapping("/final-video/{videoId}")
    public FinalVideo getFinalVideo(@PathVariable Long videoId) {
        log.info("获取最终视频请求: id={}", videoId);
        return videoStitchService.getFinalVideo(videoId);
    }

    /**
     * 删除最终视频
     */
    @DeleteMapping("/final-video/{videoId}")
    public Map<String, Object> deleteFinalVideo(@PathVariable Long videoId) {
        log.info("删除最终视频请求: id={}", videoId);
        boolean result = videoStitchService.deleteFinalVideo(videoId);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("status", result ? "success" : "error");
        response.put("message", result ? "最终视频删除成功" : "最终视频删除失败");
        return response;
    }

    /**
     * 获取最终视频列表
     */
    @GetMapping("/final-videos")
    public List<FinalVideo> listFinalVideos(@RequestParam Long novelId, @RequestParam(required = false) Map<String, Object> filters) {
        log.info("获取最终视频列表请求: novelId={}, filters={}", novelId, filters);
        return videoStitchService.listFinalVideos(novelId, filters);
    }

    // ========== 视频拼接功能 ==========

    /**
     * 拼接视频片段
     */
    @PostMapping
    public FinalVideo stitchVideoSegments(@RequestBody Map<String, Object> request) {
        log.info("拼接视频片段请求: {}", request);
        Long novelId = (Long) request.get("novelId");
        List<Long> segmentIds = (List<Long>) request.get("segmentIds");
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        return videoStitchService.stitchVideoSegments(novelId, segmentIds, params);
    }

    /**
     * 批量拼接视频片段
     */
    @PostMapping("/batch")
    public Map<String, Object> batchStitchVideoSegments(@RequestBody List<Map<String, Object>> stitchRequests) {
        log.info("批量拼接视频片段请求: 请求数量={}", stitchRequests.size());
        return videoStitchService.batchStitchVideoSegments(stitchRequests);
    }

    /**
     * 获取拼接任务状态
     */
    @GetMapping("/status/{videoId}")
    public Map<String, Object> getStitchStatus(@PathVariable Long videoId) {
        log.info("获取拼接任务状态请求: videoId={}", videoId);
        return videoStitchService.getStitchStatus(videoId);
    }

    // ========== 视频片段管理 ==========

    /**
     * 向最终视频添加片段
     */
    @PostMapping("/final-video/{videoId}/segments")
    public Map<String, Object> addSegmentToFinalVideo(@PathVariable Long videoId, @RequestBody Map<String, Object> request) {
        log.info("向最终视频添加片段请求: videoId={}, data={}", videoId, request);
        Long segmentId = (Long) request.get("segmentId");
        Integer order = (Integer) request.get("order");
        return videoStitchService.addSegmentToFinalVideo(videoId, segmentId, order);
    }

    /**
     * 从最终视频移除片段
     */
    @DeleteMapping("/final-video/{videoId}/segments/{segmentId}")
    public Map<String, Object> removeSegmentFromFinalVideo(@PathVariable Long videoId, @PathVariable Long segmentId) {
        log.info("从最终视频移除片段请求: videoId={}, segmentId={}", videoId, segmentId);
        return videoStitchService.removeSegmentFromFinalVideo(videoId, segmentId);
    }

    /**
     * 调整最终视频中片段的顺序
     */
    @PutMapping("/final-video/{videoId}/segments/order")
    public Map<String, Object> reorderSegmentsInFinalVideo(@PathVariable Long videoId, @RequestBody List<Map<String, Object>> segmentOrders) {
        log.info("调整最终视频中片段的顺序请求: videoId={}, orders={}", videoId, segmentOrders);
        return videoStitchService.reorderSegmentsInFinalVideo(videoId, segmentOrders);
    }

    // ========== 视频状态管理 ==========

    /**
     * 更新最终视频状态
     */
    @PutMapping("/final-video/{videoId}/status")
    public FinalVideo updateFinalVideoStatus(@PathVariable Long videoId, @RequestParam String status) {
        log.info("更新最终视频状态请求: id={}, status={}", videoId, status);
        return videoStitchService.updateFinalVideoStatus(videoId, status);
    }

    /**
     * 批量更新最终视频状态
     */
    @PutMapping("/final-video/status/batch")
    public Map<String, Object> batchUpdateFinalVideoStatus(@RequestBody Map<String, Object> request) {
        log.info("批量更新最终视频状态请求: {}", request);
        List<Long> videoIds = (List<Long>) request.get("videoIds");
        String status = (String) request.get("status");
        return videoStitchService.batchUpdateFinalVideoStatus(videoIds, status);
    }

    /**
     * 获取最终视频状态统计
     */
    @GetMapping("/final-video/status/statistics")
    public Map<String, Object> getFinalVideoStatusStatistics(@RequestParam Long novelId) {
        log.info("获取最终视频状态统计请求: novelId={}", novelId);
        return videoStitchService.getFinalVideoStatusStatistics(novelId);
    }

    // ========== 视频后处理 ==========

    /**
     * 添加背景音乐
     */
    @PostMapping("/final-video/{videoId}/background-music")
    public FinalVideo addBackgroundMusic(@PathVariable Long videoId, @RequestBody Map<String, Object> request) {
        log.info("添加背景音乐请求: videoId={}, data={}", videoId, request);
        String musicUrl = (String) request.get("musicUrl");
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        return videoStitchService.addBackgroundMusic(videoId, musicUrl, params);
    }

    /**
     * 添加字幕
     */
    @PostMapping("/final-video/{videoId}/subtitles")
    public FinalVideo addSubtitles(@PathVariable Long videoId, @RequestBody Map<String, Object> request) {
        log.info("添加字幕请求: videoId={}, data={}", videoId, request);
        String subtitleUrl = (String) request.get("subtitleUrl");
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        return videoStitchService.addSubtitles(videoId, subtitleUrl, params);
    }

    /**
     * 视频格式转换
     */
    @PostMapping("/final-video/{videoId}/convert")
    public FinalVideo convertVideoFormat(@PathVariable Long videoId, @RequestBody Map<String, Object> request) {
        log.info("视频格式转换请求: videoId={}, data={}", videoId, request);
        String format = (String) request.get("format");
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        return videoStitchService.convertVideoFormat(videoId, format, params);
    }
}
