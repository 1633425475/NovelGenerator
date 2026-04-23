package com.laoji.novelai.dto.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 事件资源DTO
 */
@Data
public class EventDTO {

    private Long id;

    @NotBlank(message = "事件名称不能为空")
    private String title;

    private String type;

    private String description;

    private String timePoint;

    private String duration;

    private Long locationId;
    
    private String locationName;

    private List<Participant> participants;

    private String cause;

    private String process;

    private String result;

    private String impact;

    private Boolean isTurningPoint;

    private Long timelineId;

    private Long novelId;

    private String status;

    private List<String> tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 参与者
     */
    @Data
    public static class Participant {
        private Long characterId;
        private String characterName;
        private String role; // 角色：主角、反派、旁观者等
    }
}