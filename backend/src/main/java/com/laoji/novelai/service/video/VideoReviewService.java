package com.laoji.novelai.service.video;

import com.laoji.novelai.dto.video.ReviewRequest;
import com.laoji.novelai.entity.video.ReviewRecord;
import com.laoji.novelai.entity.video.VideoSegment;

import java.util.List;
import java.util.Map;

/**
 * 视频审核服务
 */
public interface VideoReviewService {

    // ========== 视频片段管理 ==========

    /**
     * 创建视频片段
     */
    VideoSegment createVideoSegment(VideoSegment videoSegment);

    /**
     * 更新视频片段
     */
    VideoSegment updateVideoSegment(Long segmentId, VideoSegment videoSegment);

    /**
     * 获取视频片段
     */
    VideoSegment getVideoSegment(Long segmentId);

    /**
     * 删除视频片段
     */
    boolean deleteVideoSegment(Long segmentId);

    /**
     * 获取视频片段列表
     */
    List<VideoSegment> listVideoSegments(Long novelId, Map<String, Object> filters);

    // ========== AI初审 ==========

    /**
     * 对视频片段进行AI初审
     */
    ReviewRecord reviewVideoByAI(Long segmentId, Map<String, Object> params);

    /**
     * 批量进行AI初审
     */
    Map<String, Object> batchReviewByAI(List<Long> segmentIds, Map<String, Object> params);

    /**
     * 获取AI初审状态
     */
    Map<String, Object> getAIReviewStatus(Long segmentId);

    // ========== 人工复审 ==========

    /**
     * 对视频片段进行人工复审
     */
    ReviewRecord reviewVideoByHuman(Long segmentId, ReviewRequest reviewRequest);

    /**
     * 批量进行人工复审
     */
    Map<String, Object> batchReviewByHuman(Map<Long, ReviewRequest> reviewRequests);

    /**
     * 获取人工复审状态
     */
    Map<String, Object> getHumanReviewStatus(Long segmentId);

    // ========== 审核记录管理 ==========

    /**
     * 获取审核记录
     */
    List<ReviewRecord> getReviewRecords(Long segmentId);

    /**
     * 获取审核记录详情
     */
    ReviewRecord getReviewRecord(Long recordId);

    /**
     * 统计审核数据
     */
    Map<String, Object> getReviewStatistics(Map<String, Object> filters);

    // ========== 视频状态管理 ==========

    /**
     * 更新视频片段状态
     */
    VideoSegment updateVideoStatus(Long segmentId, String status);

    /**
     * 批量更新视频片段状态
     */
    Map<String, Object> batchUpdateVideoStatus(List<Long> segmentIds, String status);

    /**
     * 获取视频片段状态统计
     */
    Map<String, Object> getVideoStatusStatistics(Long novelId);
}
