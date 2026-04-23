package com.laoji.novelai.service.video;

import com.laoji.novelai.entity.video.FinalVideo;
import com.laoji.novelai.entity.video.VideoSegment;

import java.util.List;
import java.util.Map;

/**
 * 视频拼接服务
 */
public interface VideoStitchService {

    // ========== 视频拼接管理 ==========

    /**
     * 创建最终视频
     */
    FinalVideo createFinalVideo(FinalVideo finalVideo);

    /**
     * 更新最终视频
     */
    FinalVideo updateFinalVideo(Long videoId, FinalVideo finalVideo);

    /**
     * 获取最终视频
     */
    FinalVideo getFinalVideo(Long videoId);

    /**
     * 删除最终视频
     */
    boolean deleteFinalVideo(Long videoId);

    /**
     * 获取最终视频列表
     */
    List<FinalVideo> listFinalVideos(Long novelId, Map<String, Object> filters);

    // ========== 视频拼接功能 ==========

    /**
     * 拼接视频片段
     */
    FinalVideo stitchVideoSegments(Long novelId, List<Long> segmentIds, Map<String, Object> params);

    /**
     * 批量拼接视频片段
     */
    Map<String, Object> batchStitchVideoSegments(List<Map<String, Object>> stitchRequests);

    /**
     * 获取拼接任务状态
     */
    Map<String, Object> getStitchStatus(Long videoId);

    // ========== 视频片段管理 ==========

    /**
     * 向最终视频添加片段
     */
    Map<String, Object> addSegmentToFinalVideo(Long videoId, Long segmentId, Integer order);

    /**
     * 从最终视频移除片段
     */
    Map<String, Object> removeSegmentFromFinalVideo(Long videoId, Long segmentId);

    /**
     * 调整最终视频中片段的顺序
     */
    Map<String, Object> reorderSegmentsInFinalVideo(Long videoId, List<Map<String, Object>> segmentOrders);

    // ========== 视频状态管理 ==========

    /**
     * 更新最终视频状态
     */
    FinalVideo updateFinalVideoStatus(Long videoId, String status);

    /**
     * 批量更新最终视频状态
     */
    Map<String, Object> batchUpdateFinalVideoStatus(List<Long> videoIds, String status);

    /**
     * 获取最终视频状态统计
     */
    Map<String, Object> getFinalVideoStatusStatistics(Long novelId);

    // ========== 视频后处理 ==========

    /**
     * 添加背景音乐
     */
    FinalVideo addBackgroundMusic(Long videoId, String musicUrl, Map<String, Object> params);

    /**
     * 添加字幕
     */
    FinalVideo addSubtitles(Long videoId, String subtitleUrl, Map<String, Object> params);

    /**
     * 视频格式转换
     */
    FinalVideo convertVideoFormat(Long videoId, String format, Map<String, Object> params);
}
