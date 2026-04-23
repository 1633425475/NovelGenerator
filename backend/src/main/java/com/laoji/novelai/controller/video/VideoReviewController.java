package com.laoji.novelai.controller.video;

import com.laoji.novelai.dto.video.ReviewRequest;
import com.laoji.novelai.entity.video.ReviewRecord;
import com.laoji.novelai.entity.video.VideoSegment;
import com.laoji.novelai.service.video.VideoReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 视频审核控制器
 */
@RestController
@RequestMapping("/api/video/review")
@RequiredArgsConstructor
@Slf4j
public class VideoReviewController {

    private final VideoReviewService videoReviewService;

    // ========== 视频片段管理 ==========

    /**
     * 创建视频片段
     */
    @PostMapping("/segment")
    public VideoSegment createVideoSegment(@RequestBody VideoSegment videoSegment) {
        log.info("创建视频片段请求: {}", videoSegment);
        return videoReviewService.createVideoSegment(videoSegment);
    }

    /**
     * 更新视频片段
     */
    @PutMapping("/segment/{segmentId}")
    public VideoSegment updateVideoSegment(@PathVariable Long segmentId, @RequestBody VideoSegment videoSegment) {
        log.info("更新视频片段请求: id={}, data={}", segmentId, videoSegment);
        return videoReviewService.updateVideoSegment(segmentId, videoSegment);
    }

    /**
     * 获取视频片段
     */
    @GetMapping("/segment/{segmentId}")
    public VideoSegment getVideoSegment(@PathVariable Long segmentId) {
        log.info("获取视频片段请求: id={}", segmentId);
        return videoReviewService.getVideoSegment(segmentId);
    }

    /**
     * 删除视频片段
     */
    @DeleteMapping("/segment/{segmentId}")
    public Map<String, Object> deleteVideoSegment(@PathVariable Long segmentId) {
        log.info("删除视频片段请求: id={}", segmentId);
        boolean result = videoReviewService.deleteVideoSegment(segmentId);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("status", result ? "success" : "error");
        response.put("message", result ? "视频片段删除成功" : "视频片段删除失败");
        return response;
    }

    /**
     * 获取视频片段列表
     */
    @GetMapping("/segments")
    public List<VideoSegment> listVideoSegments(@RequestParam Long novelId, @RequestParam(required = false) Map<String, Object> filters) {
        log.info("获取视频片段列表请求: novelId={}, filters={}", novelId, filters);
        return videoReviewService.listVideoSegments(novelId, filters);
    }

    // ========== AI初审 ==========

    /**
     * 对视频片段进行AI初审
     */
    @PostMapping("/ai/{segmentId}")
    public ReviewRecord reviewVideoByAI(@PathVariable Long segmentId, @RequestBody Map<String, Object> params) {
        log.info("AI初审视频片段请求: id={}, params={}", segmentId, params);
        return videoReviewService.reviewVideoByAI(segmentId, params);
    }

    /**
     * 批量进行AI初审
     */
    @PostMapping("/ai/batch")
    public Map<String, Object> batchReviewByAI(@RequestBody Map<String, Object> request) {
        log.info("批量AI初审视频片段请求: {}", request);
        List<Long> segmentIds = (List<Long>) request.get("segmentIds");
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        return videoReviewService.batchReviewByAI(segmentIds, params);
    }

    /**
     * 获取AI初审状态
     */
    @GetMapping("/ai/status/{segmentId}")
    public Map<String, Object> getAIReviewStatus(@PathVariable Long segmentId) {
        log.info("获取AI初审状态请求: segmentId={}", segmentId);
        return videoReviewService.getAIReviewStatus(segmentId);
    }

    // ========== 人工复审 ==========

    /**
     * 对视频片段进行人工复审
     */
    @PostMapping("/human/{segmentId}")
    public ReviewRecord reviewVideoByHuman(@PathVariable Long segmentId, @RequestBody ReviewRequest reviewRequest) {
        log.info("人工复审视频片段请求: id={}, request={}", segmentId, reviewRequest);
        return videoReviewService.reviewVideoByHuman(segmentId, reviewRequest);
    }

    /**
     * 批量进行人工复审
     */
    @PostMapping("/human/batch")
    public Map<String, Object> batchReviewByHuman(@RequestBody Map<String, Object> request) {
        log.info("批量人工复审视频片段请求: {}", request);
        Map<Long, ReviewRequest> reviewRequests = new java.util.HashMap<>();
        Map<String, Object> reviews = (Map<String, Object>) request.get("reviews");
        for (Map.Entry<String, Object> entry : reviews.entrySet()) {
            Long segmentId = Long.parseLong(entry.getKey());
            ReviewRequest reviewRequest = new ReviewRequest();
            Map<String, Object> reviewData = (Map<String, Object>) entry.getValue();
            reviewRequest.setResult((String) reviewData.get("result"));
            reviewRequest.setComment((String) reviewData.get("comment"));
            reviewRequest.setScore((Integer) reviewData.get("score"));
            reviewRequest.setMetadata((Map<String, Object>) reviewData.get("metadata"));
            reviewRequests.put(segmentId, reviewRequest);
        }
        return videoReviewService.batchReviewByHuman(reviewRequests);
    }

    /**
     * 获取人工复审状态
     */
    @GetMapping("/human/status/{segmentId}")
    public Map<String, Object> getHumanReviewStatus(@PathVariable Long segmentId) {
        log.info("获取人工复审状态请求: segmentId={}", segmentId);
        return videoReviewService.getHumanReviewStatus(segmentId);
    }

    // ========== 审核记录管理 ==========

    /**
     * 获取审核记录
     */
    @GetMapping("/records/{segmentId}")
    public List<ReviewRecord> getReviewRecords(@PathVariable Long segmentId) {
        log.info("获取审核记录请求: segmentId={}", segmentId);
        return videoReviewService.getReviewRecords(segmentId);
    }

    /**
     * 获取审核记录详情
     */
    @GetMapping("/record/{recordId}")
    public ReviewRecord getReviewRecord(@PathVariable Long recordId) {
        log.info("获取审核记录详情请求: id={}", recordId);
        return videoReviewService.getReviewRecord(recordId);
    }

    /**
     * 统计审核数据
     */
    @GetMapping("/statistics")
    public Map<String, Object> getReviewStatistics(@RequestParam(required = false) Map<String, Object> filters) {
        log.info("统计审核数据请求: filters={}", filters);
        return videoReviewService.getReviewStatistics(filters);
    }

    // ========== 视频状态管理 ==========

    /**
     * 更新视频片段状态
     */
    @PutMapping("/status/{segmentId}")
    public VideoSegment updateVideoStatus(@PathVariable Long segmentId, @RequestParam String status) {
        log.info("更新视频片段状态请求: id={}, status={}", segmentId, status);
        return videoReviewService.updateVideoStatus(segmentId, status);
    }

    /**
     * 批量更新视频片段状态
     */
    @PutMapping("/status/batch")
    public Map<String, Object> batchUpdateVideoStatus(@RequestBody Map<String, Object> request) {
        log.info("批量更新视频片段状态请求: {}", request);
        List<Long> segmentIds = (List<Long>) request.get("segmentIds");
        String status = (String) request.get("status");
        return videoReviewService.batchUpdateVideoStatus(segmentIds, status);
    }

    /**
     * 获取视频片段状态统计
     */
    @GetMapping("/status/statistics")
    public Map<String, Object> getVideoStatusStatistics(@RequestParam Long novelId) {
        log.info("获取视频片段状态统计请求: novelId={}", novelId);
        return videoReviewService.getVideoStatusStatistics(novelId);
    }
}
