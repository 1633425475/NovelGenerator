package com.laoji.novelai.dto.resource;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 地点资源DTO
 */
@Data
public class LocationDTO {

    private Long id;

    @NotBlank(message = "地点名称不能为空")
    private String name;

    private String alias;

    private String type;

    private String geography;

    private String climate;

    private String architecture;

    private String culture;

    private String history;

    private String importantPlaces;

    private String politics;

    private String economy;

    private List<Connection> connections;

    private Map<String, Object> coordinates;

    private Long novelId;

    private String status;

    private List<String> tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 连接关系
     */
    @Data
    public static class Connection {
        private Long locationId;
        private String locationName;
        private String connectionType; // NEARBY-附近, CONNECTED-连接, PART_OF-属于
        private String description;
    }
}