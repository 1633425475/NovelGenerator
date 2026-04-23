package com.laoji.novelai.entity.video;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 最终视频
 */
@Entity
@Table(name = "final_video")
@Data
@EqualsAndHashCode(callSuper = true)
public class FinalVideo extends BaseEntity {

    private Long novelId;
    private String videoName;
    private String description;
    private String fileUrl;
    private String thumbnailUrl;
    private Double duration;
    private Long fileSize;
    private String format;
    private Integer width;
    private Integer height;
    private Integer fps;
    private Integer segmentCount;
    private String mergeParams;
    private String status;
    private String reviewStatus;
    private Long finalReviewId;
    private Integer viewCount;
    private Integer downloadCount;
    private LocalDateTime completedAt;
    private LocalDateTime deletedAt;
}
