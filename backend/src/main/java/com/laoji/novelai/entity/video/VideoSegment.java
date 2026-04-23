package com.laoji.novelai.entity.video;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频片段
 */
@Entity
@Table(name = "video_segment")
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoSegment extends BaseEntity {

    private Long novelId;
    private Long chapterId;
    private Long sceneId;
    private String title;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer duration;
    private Integer width;
    private Integer height;
    private Integer sceneOrder;
    private String status;
    private String aiReviewStatus;
    private String humanReviewStatus;
    private String aiReviewComment;
    private String humanReviewComment;
    private Long aiReviewerId;
    private Long humanReviewerId;
    private String metadata;
    private Boolean deleted = false;
}
