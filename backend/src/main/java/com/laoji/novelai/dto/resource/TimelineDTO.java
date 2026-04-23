package com.laoji.novelai.dto.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 时间线资源DTO
 */
@Data
public class TimelineDTO {

    private Long id;

    @NotBlank(message = "时间线名称不能为空")
    private String name;

    private String description;

    private String timeUnit;

    private String startTime;

    private String endTime;

    private List<TimePoint> timePoints;

    private Long novelId;

    private String status;

    private Boolean isMain;

    private List<String> tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 时间点
     */
    @Data
    public static class TimePoint {
        private String time;
        private String title;
        private String description;
        private List<RelatedEntity> relatedEntities;
    }

    /**
     * 相关实体
     */
    @Data
    public static class RelatedEntity {
        private String type; // CHARACTER, LOCATION, EVENT, ITEM
        private Long id;
        private String name;
    }
}