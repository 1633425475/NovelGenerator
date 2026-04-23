package com.laoji.novelai.repository.video;

import com.laoji.novelai.entity.video.VideoSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 视频片段仓库
 */
@Repository
public interface VideoSegmentRepository extends JpaRepository<VideoSegment, Long>, JpaSpecificationExecutor<VideoSegment> {

    /**
     * 根据小说ID查询视频片段
     */
    List<VideoSegment> findByNovelIdAndDeletedFalseOrderBySceneOrderAsc(Long novelId);

    /**
     * 根据章节ID查询视频片段
     */
    List<VideoSegment> findByChapterIdAndDeletedFalseOrderBySceneOrderAsc(Long chapterId);

    /**
     * 根据状态查询视频片段
     */
    List<VideoSegment> findByStatusAndDeletedFalse(String status);

    /**
     * 根据AI审核状态查询视频片段
     */
    List<VideoSegment> findByAiReviewStatusAndDeletedFalse(String aiReviewStatus);

    /**
     * 根据人工审核状态查询视频片段
     */
    List<VideoSegment> findByHumanReviewStatusAndDeletedFalse(String humanReviewStatus);

    /**
     * 根据小说ID和审核状态查询视频片段
     */
    List<VideoSegment> findByNovelIdAndStatusAndDeletedFalse(Long novelId, String status);
}
