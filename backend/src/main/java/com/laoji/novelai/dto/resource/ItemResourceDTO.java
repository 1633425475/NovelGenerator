package com.laoji.novelai.dto.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物品资源DTO
 */
@Data
public class ItemResourceDTO {

    private Long id;

    @NotBlank(message = "物品名称不能为空")
    private String name;

    private String alias;

    private String type;

    private String rarity;

    private String description;

    private String appearance;

    private String abilities;

    private String usage;

    private String acquisition;

    private String history;

    private Long currentHolderId;
    
    private String currentHolderName;

    private List<Holder> previousHolders;

    private Long locationId;
    
    private String locationName;

    private Long eventId;
    
    private String eventTitle;

    private Long novelId;

    private String status;

    private List<String> tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 持有者
     */
    @Data
    public static class Holder {
        private Long characterId;
        private String characterName;
        private String period; // 持有时期
    }
}