package com.laoji.novelai.controller.resource;

import com.laoji.novelai.controller.BaseController;
import com.laoji.novelai.dto.resource.CharacterDTO;
import com.laoji.novelai.dto.resource.LocationDTO;
import com.laoji.novelai.dto.resource.TimelineDTO;
import com.laoji.novelai.dto.resource.EventDTO;
import com.laoji.novelai.dto.resource.ItemResourceDTO;
import com.laoji.novelai.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 资源管理控制器
 */
@RestController
@RequestMapping("/api/novels/{novelId}/resources")
@RequiredArgsConstructor
@Tag(name = "资源管理", description = "小说资源（人物、地点、时间线、事件、物品）的CRUD操作")
public class ResourceController extends BaseController {

    private final ResourceService resourceService;

    // ========== 人物资源管理 ==========

    @PostMapping("/characters")
    @Operation(summary = "创建人物", description = "为指定小说创建新的人物资源")
    public ResponseEntity<?> createCharacter(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody CharacterDTO characterDTO) {
        try {
            characterDTO.setNovelId(novelId);
            CharacterDTO result = resourceService.createCharacter(characterDTO);
            return success(result);
        } catch (Exception e) {
            return error("创建人物失败: " + e.getMessage());
        }
    }

    @PutMapping("/characters/{characterId}")
    @Operation(summary = "更新人物", description = "更新指定人物的信息")
    public ResponseEntity<?> updateCharacter(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "人物ID") Long characterId,
            @Valid @RequestBody CharacterDTO characterDTO) {
        try {
            characterDTO.setNovelId(novelId);
            CharacterDTO result = resourceService.updateCharacter(characterId, characterDTO);
            return success(result);
        } catch (Exception e) {
            return error("更新人物失败: " + e.getMessage());
        }
    }

    @GetMapping("/characters/{characterId}")
    @Operation(summary = "获取人物详情", description = "获取指定人物的详细信息")
    public ResponseEntity<?> getCharacter(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "人物ID") Long characterId) {
        try {
            CharacterDTO result = resourceService.getCharacter(characterId);
            // 验证人物是否属于该小说
            if (!novelId.equals(result.getNovelId())) {
                return forbidden("该人物不属于当前小说");
            }
            return success(result);
        } catch (Exception e) {
            return notFound("人物不存在: " + e.getMessage());
        }
    }

    @GetMapping("/characters")
    @Operation(summary = "获取人物列表", description = "获取指定小说的所有人物资源，支持搜索和标签过滤")
    public ResponseEntity<?> listCharacters(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        try {
            List<CharacterDTO> characters = resourceService.listCharacters(novelId, search, tags);
            return success(characters);
        } catch (Exception e) {
            return error("获取人物列表失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/characters/{characterId}")
    @Operation(summary = "删除人物", description = "逻辑删除指定人物")
    public ResponseEntity<?> deleteCharacter(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "人物ID") Long characterId) {
        try {
            resourceService.deleteCharacter(characterId);
            return success("人物删除成功");
        } catch (Exception e) {
            return error("删除人物失败: " + e.getMessage());
        }
    }

    @PostMapping("/characters/{characterId}/enhance")
    @Operation(summary = "AI增强人物", description = "使用AI增强人物设定，补充详细信息")
    public ResponseEntity<?> enhanceCharacterWithAI(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "人物ID") Long characterId,
            @RequestParam(defaultValue = "DETAIL") String enhancementType) {
        try {
            CharacterDTO result = resourceService.enhanceCharacterWithAI(characterId, enhancementType);
            return success(result);
        } catch (Exception e) {
            return error("AI增强失败: " + e.getMessage());
        }
    }

    // ========== 地点资源管理 ==========

    @PostMapping("/locations")
    @Operation(summary = "创建地点", description = "为指定小说创建新的地点资源")
    public ResponseEntity<?> createLocation(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody LocationDTO locationDTO) {
        try {
            locationDTO.setNovelId(novelId);
            LocationDTO result = resourceService.createLocation(locationDTO);
            return success(result);
        } catch (Exception e) {
            return error("创建地点失败: " + e.getMessage());
        }
    }

    @PutMapping("/locations/{locationId}")
    @Operation(summary = "更新地点", description = "更新指定地点的信息")
    public ResponseEntity<?> updateLocation(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "地点ID") Long locationId,
            @Valid @RequestBody LocationDTO locationDTO) {
        try {
            locationDTO.setNovelId(novelId);
            LocationDTO result = resourceService.updateLocation(locationId, locationDTO);
            return success(result);
        } catch (Exception e) {
            return error("更新地点失败: " + e.getMessage());
        }
    }

    @GetMapping("/locations/{locationId}")
    @Operation(summary = "获取地点详情", description = "获取指定地点的详细信息")
    public ResponseEntity<?> getLocation(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "地点ID") Long locationId) {
        try {
            LocationDTO result = resourceService.getLocation(locationId);
            if (!novelId.equals(result.getNovelId())) {
                return forbidden("该地点不属于当前小说");
            }
            return success(result);
        } catch (Exception e) {
            return notFound("地点不存在: " + e.getMessage());
        }
    }

    @GetMapping("/locations")
    @Operation(summary = "获取地点列表", description = "获取指定小说的所有地点资源，支持搜索和标签过滤")
    public ResponseEntity<?> listLocations(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        try {
            List<LocationDTO> locations = resourceService.listLocations(novelId, search, tags);
            return success(locations);
        } catch (Exception e) {
            return error("获取地点列表失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/locations/{locationId}")
    @Operation(summary = "删除地点", description = "逻辑删除指定地点")
    public ResponseEntity<?> deleteLocation(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "地点ID") Long locationId) {
        try {
            resourceService.deleteLocation(locationId);
            return success("地点删除成功");
        } catch (Exception e) {
            return error("删除地点失败: " + e.getMessage());
        }
    }

    // ========== 时间线资源管理 ==========

    @PostMapping("/timelines")
    @Operation(summary = "创建时间线", description = "为指定小说创建新的时间线资源")
    public ResponseEntity<?> createTimeline(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody TimelineDTO timelineDTO) {
        try {
            timelineDTO.setNovelId(novelId);
            TimelineDTO result = resourceService.createTimeline(timelineDTO);
            return success(result);
        } catch (Exception e) {
            return error("创建时间线失败: " + e.getMessage());
        }
    }

    @PutMapping("/timelines/{timelineId}")
    @Operation(summary = "更新时间线", description = "更新指定时间线的信息")
    public ResponseEntity<?> updateTimeline(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "时间线ID") Long timelineId,
            @Valid @RequestBody TimelineDTO timelineDTO) {
        try {
            timelineDTO.setNovelId(novelId);
            TimelineDTO result = resourceService.updateTimeline(timelineId, timelineDTO);
            return success(result);
        } catch (Exception e) {
            return error("更新时间线失败: " + e.getMessage());
        }
    }

    @GetMapping("/timelines/{timelineId}")
    @Operation(summary = "获取时间线详情", description = "获取指定时间线的详细信息")
    public ResponseEntity<?> getTimeline(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "时间线ID") Long timelineId) {
        try {
            TimelineDTO result = resourceService.getTimeline(timelineId);
            if (!novelId.equals(result.getNovelId())) {
                return forbidden("该时间线不属于当前小说");
            }
            return success(result);
        } catch (Exception e) {
            return notFound("时间线不存在: " + e.getMessage());
        }
    }

    @GetMapping("/timelines")
    @Operation(summary = "获取时间线列表", description = "获取指定小说的所有时间线资源")
    public ResponseEntity<?> listTimelines(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            List<TimelineDTO> timelines = resourceService.listTimelines(novelId);
            return success(timelines);
        } catch (Exception e) {
            return error("获取时间线列表失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/timelines/{timelineId}")
    @Operation(summary = "删除时间线", description = "逻辑删除指定时间线")
    public ResponseEntity<?> deleteTimeline(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "时间线ID") Long timelineId) {
        try {
            resourceService.deleteTimeline(timelineId);
            return success("时间线删除成功");
        } catch (Exception e) {
            return error("删除时间线失败: " + e.getMessage());
        }
    }

    // ========== 事件资源管理 ==========

    @PostMapping("/events")
    @Operation(summary = "创建事件", description = "为指定小说创建新的事件资源")
    public ResponseEntity<?> createEvent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody EventDTO eventDTO) {
        try {
            eventDTO.setNovelId(novelId);
            EventDTO result = resourceService.createEvent(eventDTO);
            return success(result);
        } catch (Exception e) {
            return error("创建事件失败: " + e.getMessage());
        }
    }

    @PutMapping("/events/{eventId}")
    @Operation(summary = "更新事件", description = "更新指定事件的信息")
    public ResponseEntity<?> updateEvent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "事件ID") Long eventId,
            @Valid @RequestBody EventDTO eventDTO) {
        try {
            eventDTO.setNovelId(novelId);
            EventDTO result = resourceService.updateEvent(eventId, eventDTO);
            return success(result);
        } catch (Exception e) {
            return error("更新事件失败: " + e.getMessage());
        }
    }

    @GetMapping("/events/{eventId}")
    @Operation(summary = "获取事件详情", description = "获取指定事件的详细信息")
    public ResponseEntity<?> getEvent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "事件ID") Long eventId) {
        try {
            EventDTO result = resourceService.getEvent(eventId);
            if (!novelId.equals(result.getNovelId())) {
                return forbidden("该事件不属于当前小说");
            }
            return success(result);
        } catch (Exception e) {
            return notFound("事件不存在: " + e.getMessage());
        }
    }

    @GetMapping("/events")
    @Operation(summary = "获取事件列表", description = "获取指定小说的所有事件资源，支持按时间线筛选和搜索")
    public ResponseEntity<?> listEvents(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam(required = false) Long timelineId,
            @RequestParam(required = false) String search) {
        try {
            List<EventDTO> events = resourceService.listEvents(novelId, timelineId, search);
            return success(events);
        } catch (Exception e) {
            return error("获取事件列表失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "删除事件", description = "逻辑删除指定事件")
    public ResponseEntity<?> deleteEvent(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "事件ID") Long eventId) {
        try {
            resourceService.deleteEvent(eventId);
            return success("事件删除成功");
        } catch (Exception e) {
            return error("删除事件失败: " + e.getMessage());
        }
    }

    // ========== 物品资源管理 ==========

    @PostMapping("/items")
    @Operation(summary = "创建物品", description = "为指定小说创建新的物品资源")
    public ResponseEntity<?> createItemResource(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody ItemResourceDTO itemResourceDTO) {
        try {
            itemResourceDTO.setNovelId(novelId);
            ItemResourceDTO result = resourceService.createItemResource(itemResourceDTO);
            return success(result);
        } catch (Exception e) {
            return error("创建物品失败: " + e.getMessage());
        }
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "更新物品", description = "更新指定物品的信息")
    public ResponseEntity<?> updateItemResource(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "物品ID") Long itemId,
            @Valid @RequestBody ItemResourceDTO itemResourceDTO) {
        try {
            itemResourceDTO.setNovelId(novelId);
            ItemResourceDTO result = resourceService.updateItemResource(itemId, itemResourceDTO);
            return success(result);
        } catch (Exception e) {
            return error("更新物品失败: " + e.getMessage());
        }
    }

    @GetMapping("/items/{itemId}")
    @Operation(summary = "获取物品详情", description = "获取指定物品的详细信息")
    public ResponseEntity<?> getItemResource(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "物品ID") Long itemId) {
        try {
            ItemResourceDTO result = resourceService.getItemResource(itemId);
            if (!novelId.equals(result.getNovelId())) {
                return forbidden("该物品不属于当前小说");
            }
            return success(result);
        } catch (Exception e) {
            return notFound("物品不存在: " + e.getMessage());
        }
    }

    @GetMapping("/items")
    @Operation(summary = "获取物品列表", description = "获取指定小说的所有物品资源，支持搜索和标签过滤")
    public ResponseEntity<?> listItemResources(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        try {
            List<ItemResourceDTO> items = resourceService.listItemResources(novelId, search, tags);
            return success(items);
        } catch (Exception e) {
            return error("获取物品列表失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "删除物品", description = "逻辑删除指定物品")
    public ResponseEntity<?> deleteItemResource(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "物品ID") Long itemId) {
        try {
            resourceService.deleteItemResource(itemId);
            return success("物品删除成功");
        } catch (Exception e) {
            return error("删除物品失败: " + e.getMessage());
        }
    }

    // ========== 批量操作 ==========

    @PostMapping("/characters/batch")
    @Operation(summary = "批量创建人物", description = "为指定小说批量创建多个人物资源")
    public ResponseEntity<?> batchCreateCharacters(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody List<CharacterDTO> characterDTOs) {
        try {
            characterDTOs.forEach(dto -> dto.setNovelId(novelId));
            List<CharacterDTO> results = resourceService.batchCreateCharacters(characterDTOs);
            return success(results);
        } catch (Exception e) {
            return error("批量创建人物失败: " + e.getMessage());
        }
    }

    @PostMapping("/locations/batch")
    @Operation(summary = "批量创建地点", description = "为指定小说批量创建多个地点资源")
    public ResponseEntity<?> batchCreateLocations(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody List<LocationDTO> locationDTOs) {
        try {
            locationDTOs.forEach(dto -> dto.setNovelId(novelId));
            List<LocationDTO> results = resourceService.batchCreateLocations(locationDTOs);
            return success(results);
        } catch (Exception e) {
            return error("批量创建地点失败: " + e.getMessage());
        }
    }

    // ========== 关系图谱 ==========

    @GetMapping("/relationship-graph")
    @Operation(summary = "获取关系图谱", description = "获取指定小说的人物关系图谱")
    public ResponseEntity<?> getRelationshipGraph(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            Object graph = resourceService.getRelationshipGraph(novelId);
            return success(graph);
        } catch (Exception e) {
            return error("获取关系图谱失败: " + e.getMessage());
        }
    }

    // ========== 搜索与统计 ==========

    @GetMapping("/search")
    @Operation(summary = "搜索资源", description = "在指定小说中跨类型搜索资源")
    public ResponseEntity<?> searchResources(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam String keyword,
            @RequestParam(required = false) String resourceType) {
        try {
            Object results = resourceService.searchResources(novelId, keyword, resourceType);
            return success(results);
        } catch (Exception e) {
            return error("搜索资源失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取资源统计", description = "获取指定小说的资源数量统计")
    public ResponseEntity<?> getResourceStatistics(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            Object statistics = resourceService.getResourceStatistics(novelId);
            return success(statistics);
        } catch (Exception e) {
            return error("获取资源统计失败: " + e.getMessage());
        }
    }
}