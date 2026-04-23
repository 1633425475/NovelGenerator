package com.laoji.novelai.service.video.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laoji.novelai.dto.video.ReviewRequest;
import com.laoji.novelai.entity.video.ReviewRecord;
import com.laoji.novelai.entity.video.VideoSegment;
import com.laoji.novelai.repository.video.ReviewRecordRepository;
import com.laoji.novelai.repository.video.VideoSegmentRepository;
import com.laoji.novelai.service.video.VideoReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 视频审核服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VideoReviewServiceImpl implements VideoReviewService {

    private final VideoSegmentRepository videoSegmentRepository;
    private final ReviewRecordRepository reviewRecordRepository;
    private final ObjectMapper objectMapper;

    // ========== 视频片段管理 ==========

    @Override
    public VideoSegment createVideoSegment(VideoSegment videoSegment) {
        log.info("创建视频片段: {}", videoSegment);

        // 设置默认值
        if (videoSegment.getStatus() == null) {
            videoSegment.setStatus("PENDING_AI");
        }
        if (videoSegment.getAiReviewStatus() == null) {
            videoSegment.setAiReviewStatus("PENDING");
        }
        if (videoSegment.getHumanReviewStatus() == null) {
            videoSegment.setHumanReviewStatus("PENDING");
        }
        if (videoSegment.getSceneOrder() == null) {
            videoSegment.setSceneOrder(0);
        }

        VideoSegment savedSegment = videoSegmentRepository.save(videoSegment);
        log.info("视频片段创建成功，ID: {}", savedSegment.getId());
        return savedSegment;
    }

    @Override
    public VideoSegment updateVideoSegment(Long segmentId, VideoSegment videoSegment) {
        log.info("更新视频片段: id={}, data={}", segmentId, videoSegment);

        VideoSegment existingSegment = videoSegmentRepository.findById(segmentId)
                .orElseThrow(() -> new IllegalArgumentException("视频片段不存在，ID: " + segmentId));

        // 更新字段
        if (videoSegment.getTitle() != null) {
            existingSegment.setTitle(videoSegment.getTitle());
        }
        if (videoSegment.getVideoUrl() != null) {
            existingSegment.setVideoUrl(videoSegment.getVideoUrl());
        }
        if (videoSegment.getThumbnailUrl() != null) {
            existingSegment.setThumbnailUrl(videoSegment.getThumbnailUrl());
        }
        if (videoSegment.getDuration() != null) {
            existingSegment.setDuration(videoSegment.getDuration());
        }
        if (videoSegment.getWidth() != null) {
            existingSegment.setWidth(videoSegment.getWidth());
        }
        if (videoSegment.getHeight() != null) {
            existingSegment.setHeight(videoSegment.getHeight());
        }
        if (videoSegment.getSceneOrder() != null) {
            existingSegment.setSceneOrder(videoSegment.getSceneOrder());
        }
        if (videoSegment.getStatus() != null) {
            existingSegment.setStatus(videoSegment.getStatus());
        }
        if (videoSegment.getAiReviewStatus() != null) {
            existingSegment.setAiReviewStatus(videoSegment.getAiReviewStatus());
        }
        if (videoSegment.getHumanReviewStatus() != null) {
            existingSegment.setHumanReviewStatus(videoSegment.getHumanReviewStatus());
        }
        if (videoSegment.getAiReviewComment() != null) {
            existingSegment.setAiReviewComment(videoSegment.getAiReviewComment());
        }
        if (videoSegment.getHumanReviewComment() != null) {
            existingSegment.setHumanReviewComment(videoSegment.getHumanReviewComment());
        }
        if (videoSegment.getAiReviewerId() != null) {
            existingSegment.setAiReviewerId(videoSegment.getAiReviewerId());
        }
        if (videoSegment.getHumanReviewerId() != null) {
            existingSegment.setHumanReviewerId(videoSegment.getHumanReviewerId());
        }
        if (videoSegment.getMetadata() != null) {
            existingSegment.setMetadata(videoSegment.getMetadata());
        }

        VideoSegment updatedSegment = videoSegmentRepository.save(existingSegment);
        log.info("视频片段更新成功，ID: {}", updatedSegment.getId());
        return updatedSegment;
    }

    @Override
    public VideoSegment getVideoSegment(Long segmentId) {
        log.info("获取视频片段: id={}", segmentId);

        VideoSegment segment = videoSegmentRepository.findById(segmentId)
                .orElseThrow(() -> new IllegalArgumentException("视频片段不存在，ID: " + segmentId));

        if (segment.getDeleted()) {
            throw new IllegalArgumentException("视频片段已被删除，ID: " + segmentId);
        }

        return segment;
    }

    @Override
    public boolean deleteVideoSegment(Long segmentId) {
        log.info("删除视频片段: id={}", segmentId);

        VideoSegment segment = videoSegmentRepository.findById(segmentId)
                .orElseThrow(() -> new IllegalArgumentException("视频片段不存在，ID: " + segmentId));

        if (segment.getDeleted()) {
            log.warn("视频片段已被删除，ID: {}", segmentId);
            return true;
        }

        segment.setDeleted(true);
        segment.setStatus("DELETED");
        videoSegmentRepository.save(segment);
        log.info("视频片段删除成功，ID: {}", segmentId);
        return true;
    }

    @Override
    public List<VideoSegment> listVideoSegments(Long novelId, Map<String, Object> filters) {
        log.info("获取视频片段列表: novelId={}, filters={}", novelId, filters);

        if (novelId == null) {
            throw new IllegalArgumentException("小说ID不能为空");
        }

        List<VideoSegment> segments;

        if (filters != null && !filters.isEmpty()) {
            String status = (String) filters.get("status");
            String aiReviewStatus = (String) filters.get("aiReviewStatus");
            String humanReviewStatus = (String) filters.get("humanReviewStatus");

            if (status != null) {
                segments = videoSegmentRepository.findByNovelIdAndStatusAndDeletedFalse(novelId, status);
            } else if (aiReviewStatus != null) {
                segments = videoSegmentRepository.findByAiReviewStatusAndDeletedFalse(aiReviewStatus);
                segments = segments.stream()
                        .filter(segment -> novelId.equals(segment.getNovelId()))
                        .collect(java.util.stream.Collectors.toList());
            } else if (humanReviewStatus != null) {
                segments = videoSegmentRepository.findByHumanReviewStatusAndDeletedFalse(humanReviewStatus);
                segments = segments.stream()
                        .filter(segment -> novelId.equals(segment.getNovelId()))
                        .collect(java.util.stream.Collectors.toList());
            } else {
                segments = videoSegmentRepository.findByNovelIdAndDeletedFalseOrderBySceneOrderAsc(novelId);
            }
        } else {
            segments = videoSegmentRepository.findByNovelIdAndDeletedFalseOrderBySceneOrderAsc(novelId);
        }

        log.info("获取视频片段列表成功，数量: {}", segments.size());
        return segments;
    }

    // ========== AI初审 ==========

    @Override
    public ReviewRecord reviewVideoByAI(Long segmentId, Map<String, Object> params) {
        log.info("AI初审视频片段: id={}, params={}", segmentId, params);

        VideoSegment segment = getVideoSegment(segmentId);

        // 模拟AI审核过程
        String result = simulateAIReview(segment, params);
        String comment = "AI审核完成";
        int score = 85; // 模拟评分

        // 更新视频片段状态
        segment.setAiReviewStatus(result);
        segment.setAiReviewComment(comment);
        segment.setAiReviewerId(1L); // 模拟AI审核者ID
        
        if ("PASSED".equals(result)) {
            segment.setStatus("AI_PASSED");
            segment.setHumanReviewStatus("PENDING");
        } else {
            segment.setStatus("AI_REJECTED");
        }
        videoSegmentRepository.save(segment);

        // 创建审核记录
        ReviewRecord record = createReviewRecord(segment, "AI", result, comment, score);

        log.info("AI初审完成，结果: {}", result);
        return record;
    }

    @Override
    public Map<String, Object> batchReviewByAI(List<Long> segmentIds, Map<String, Object> params) {
        log.info("批量AI初审视频片段: segmentIds={}, params={}", segmentIds, params);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "completed");
        result.put("reviews", new HashMap<>());

        for (Long segmentId : segmentIds) {
            try {
                ReviewRecord record = reviewVideoByAI(segmentId, params);
                ((Map<String, Object>) result.get("reviews")).put(segmentId.toString(), record.getResult());
            } catch (Exception e) {
                log.error("AI初审失败: segmentId={}, error={}", segmentId, e.getMessage());
                ((Map<String, Object>) result.get("reviews")).put(segmentId.toString(), "ERROR");
            }
        }

        log.info("批量AI初审完成");
        return result;
    }

    @Override
    public Map<String, Object> getAIReviewStatus(Long segmentId) {
        log.info("获取AI初审状态: segmentId={}", segmentId);

        VideoSegment segment = getVideoSegment(segmentId);

        Map<String, Object> status = new HashMap<>();
        status.put("segmentId", segmentId);
        status.put("aiReviewStatus", segment.getAiReviewStatus());
        status.put("aiReviewComment", segment.getAiReviewComment());
        status.put("aiReviewerId", segment.getAiReviewerId());
        status.put("status", segment.getStatus());

        log.info("获取AI初审状态成功: {}", status);
        return status;
    }

    // ========== 人工复审 ==========

    @Override
    public ReviewRecord reviewVideoByHuman(Long segmentId, ReviewRequest reviewRequest) {
        log.info("人工复审视频片段: id={}, request={}", segmentId, reviewRequest);

        VideoSegment segment = getVideoSegment(segmentId);

        // 更新视频片段状态
        segment.setHumanReviewStatus(reviewRequest.getResult());
        segment.setHumanReviewComment(reviewRequest.getComment());
        segment.setHumanReviewerId(2L); // 模拟人工审核者ID
        
        if ("PASSED".equals(reviewRequest.getResult())) {
            segment.setStatus("HUMAN_PASSED");
        } else {
            segment.setStatus("HUMAN_REJECTED");
        }
        videoSegmentRepository.save(segment);

        // 创建审核记录
        ReviewRecord record = createReviewRecord(segment, "HUMAN", reviewRequest.getResult(), reviewRequest.getComment(), reviewRequest.getScore());

        log.info("人工复审完成，结果: {}", reviewRequest.getResult());
        return record;
    }

    @Override
    public Map<String, Object> batchReviewByHuman(Map<Long, ReviewRequest> reviewRequests) {
        log.info("批量人工复审视频片段: reviewRequests={}", reviewRequests.size());

        Map<String, Object> result = new HashMap<>();
        result.put("status", "completed");
        result.put("reviews", new HashMap<>());

        for (Map.Entry<Long, ReviewRequest> entry : reviewRequests.entrySet()) {
            Long segmentId = entry.getKey();
            ReviewRequest request = entry.getValue();
            try {
                ReviewRecord record = reviewVideoByHuman(segmentId, request);
                ((Map<String, Object>) result.get("reviews")).put(segmentId.toString(), record.getResult());
            } catch (Exception e) {
                log.error("人工复审失败: segmentId={}, error={}", segmentId, e.getMessage());
                ((Map<String, Object>) result.get("reviews")).put(segmentId.toString(), "ERROR");
            }
        }

        log.info("批量人工复审完成");
        return result;
    }

    @Override
    public Map<String, Object> getHumanReviewStatus(Long segmentId) {
        log.info("获取人工复审状态: segmentId={}", segmentId);

        VideoSegment segment = getVideoSegment(segmentId);

        Map<String, Object> status = new HashMap<>();
        status.put("segmentId", segmentId);
        status.put("humanReviewStatus", segment.getHumanReviewStatus());
        status.put("humanReviewComment", segment.getHumanReviewComment());
        status.put("humanReviewerId", segment.getHumanReviewerId());
        status.put("status", segment.getStatus());

        log.info("获取人工复审状态成功: {}", status);
        return status;
    }

    // ========== 审核记录管理 ==========

    @Override
    public List<ReviewRecord> getReviewRecords(Long segmentId) {
        log.info("获取审核记录: segmentId={}", segmentId);

        List<ReviewRecord> records = reviewRecordRepository.findByVideoSegmentIdAndDeletedFalseOrderByCreatedAtDesc(segmentId);
        log.info("获取审核记录成功，数量: {}", records.size());
        return records;
    }

    @Override
    public ReviewRecord getReviewRecord(Long recordId) {
        log.info("获取审核记录详情: id={}", recordId);

        ReviewRecord record = reviewRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("审核记录不存在，ID: " + recordId));

        if (record.getDeleted()) {
            throw new IllegalArgumentException("审核记录已被删除，ID: " + recordId);
        }

        return record;
    }

    @Override
    public Map<String, Object> getReviewStatistics(Map<String, Object> filters) {
        log.info("统计审核数据: filters={}", filters);

        // 模拟统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSegments", 100);
        statistics.put("pendingAi", 20);
        statistics.put("aiPassed", 60);
        statistics.put("aiRejected", 10);
        statistics.put("pendingHuman", 30);
        statistics.put("humanPassed", 50);
        statistics.put("humanRejected", 10);
        statistics.put("averageScore", 85);

        log.info("统计审核数据成功: {}", statistics);
        return statistics;
    }

    // ========== 视频状态管理 ==========

    @Override
    public VideoSegment updateVideoStatus(Long segmentId, String status) {
        log.info("更新视频片段状态: id={}, status={}", segmentId, status);

        VideoSegment segment = getVideoSegment(segmentId);
        segment.setStatus(status);
        videoSegmentRepository.save(segment);

        log.info("视频片段状态更新成功，ID: {}", segmentId);
        return segment;
    }

    @Override
    public Map<String, Object> batchUpdateVideoStatus(List<Long> segmentIds, String status) {
        log.info("批量更新视频片段状态: segmentIds={}, status={}", segmentIds, status);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "completed");
        result.put("updated", new ArrayList<>());

        for (Long segmentId : segmentIds) {
            try {
                VideoSegment segment = updateVideoStatus(segmentId, status);
                ((List<Long>) result.get("updated")).add(segmentId);
            } catch (Exception e) {
                log.error("更新视频片段状态失败: segmentId={}, error={}", segmentId, e.getMessage());
            }
        }

        log.info("批量更新视频片段状态完成");
        return result;
    }

    @Override
    public Map<String, Object> getVideoStatusStatistics(Long novelId) {
        log.info("获取视频片段状态统计: novelId={}", novelId);

        // 模拟统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSegments", 50);
        statistics.put("pendingAi", 10);
        statistics.put("aiPassed", 30);
        statistics.put("aiRejected", 5);
        statistics.put("pendingHuman", 15);
        statistics.put("humanPassed", 25);
        statistics.put("humanRejected", 5);
        statistics.put("merged", 20);

        log.info("获取视频片段状态统计成功: {}", statistics);
        return statistics;
    }

    // ========== 辅助方法 ==========

    /**
     * 模拟AI审核过程
     */
    private String simulateAIReview(VideoSegment segment, Map<String, Object> params) {
        // 模拟AI审核逻辑，这里简单返回PASSED
        return "PASSED";
    }

    /**
     * 创建审核记录
     */
    private ReviewRecord createReviewRecord(VideoSegment segment, String reviewerType, String result, String comment, Integer score) {
        ReviewRecord record = new ReviewRecord();
        record.setVideoSegmentId(segment.getId());
        record.setReviewType("VIDEO");
        record.setReviewerType(reviewerType);
        record.setReviewerId(reviewerType.equals("AI") ? 1L : 2L);
        record.setResult(result);
        record.setComment(comment);
        record.setScore(score);
        record.setMetadata("{}");

        return reviewRecordRepository.save(record);
    }
}
