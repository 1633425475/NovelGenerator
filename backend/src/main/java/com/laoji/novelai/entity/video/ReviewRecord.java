package com.laoji.novelai.entity.video;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审核记录
 */
@Entity
@Table(name = "review_record")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewRecord extends BaseEntity {

    private Long videoSegmentId;
    private String reviewType;
    private String reviewerType;
    private Long reviewerId;
    private String result;
    private String comment;
    private Integer score;
    private String metadata;
    private Boolean deleted = false;
}
