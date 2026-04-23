package com.laoji.novelai.dto.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 人物资源DTO
 */
@Data
public class CharacterDTO {

    private Long id;

    @NotBlank(message = "人物名称不能为空")
    private String name;

    private String alias;

    private String gender;

    private String age;

    private String race;

    private String identity;

    private String faction;

    private String personality;

    private String appearance;

    private String background;

    private String abilities;

    private String weaknesses;

    private String goals;

    private String growthArc;

    private List<Relationship> relationships;

    private String quotes;

    private Long novelId;

    private String status;

    private List<String> tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 关系对象
     */
    @Data
    public static class Relationship {
        private Long characterId;
        private String characterName;
        private String relationType; // FRIEND-朋友, ENEMY-敌人, FAMILY-家人, LOVER-爱人, MENTOR-导师等
        private String description;
    }
}