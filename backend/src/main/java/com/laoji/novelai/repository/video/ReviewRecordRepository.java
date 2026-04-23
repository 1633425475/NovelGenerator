package com.laoji.novelai.repository.video;

import com.laoji.novelai.entity.video.ReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 审核记录仓库
 */
@Repository
public interface ReviewRecordRepository extends JpaRepository<ReviewRecord, Long>, JpaSpecificationExecutor<ReviewRecord> {

    /**
     * 根据视频片段ID查询审核记录
     */
    List<ReviewRecord> findByVideoSegmentIdAndDeletedFalseOrderByCreatedAtDesc(Long videoSegmentId);

    /**
     * 根据审核类型查询审核记录
     */
    List<ReviewRecord> findByReviewTypeAndDeletedFalse(String reviewType);

    /**
     * 根据审核者类型查询审核记录
     */
    List<ReviewRecord> findByReviewerTypeAndDeletedFalse(String reviewerType);

    /**
     * 根据审核结果查询审核记录
     */
    List<ReviewRecord> findByResultAndDeletedFalse(String result);

    /**
     * 根据视频片段ID和审核类型查询审核记录
     */
    List<ReviewRecord> findByVideoSegmentIdAndReviewTypeAndDeletedFalse(Long videoSegmentId, String reviewType);

    /**
     * 根据视频片段ID和审核者类型查询审核记录
     */
    List<ReviewRecord> findByVideoSegmentIdAndReviewerTypeAndDeletedFalse(Long videoSegmentId, String reviewerType);
}
