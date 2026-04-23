package com.laoji.novelai.service.resource;

import com.laoji.novelai.dto.resource.CharacterDTO;
import com.laoji.novelai.dto.resource.LocationDTO;
import com.laoji.novelai.dto.resource.TimelineDTO;
import com.laoji.novelai.dto.resource.EventDTO;
import com.laoji.novelai.dto.resource.ItemResourceDTO;

import java.util.List;

/**
 * 资源管理服务接口
 */
public interface ResourceService {

    // ========== 人物资源管理 ==========
    
    CharacterDTO createCharacter(CharacterDTO characterDTO);
    
    CharacterDTO updateCharacter(Long characterId, CharacterDTO characterDTO);
    
    CharacterDTO getCharacter(Long characterId);
    
    List<CharacterDTO> listCharacters(Long novelId, String search, List<String> tags);
    
    void deleteCharacter(Long characterId);
    
    CharacterDTO enhanceCharacterWithAI(Long characterId, String enhancementType);
    
    // ========== 地点资源管理 ==========
    
    LocationDTO createLocation(LocationDTO locationDTO);
    
    LocationDTO updateLocation(Long locationId, LocationDTO locationDTO);
    
    LocationDTO getLocation(Long locationId);
    
    List<LocationDTO> listLocations(Long novelId, String search, List<String> tags);
    
    void deleteLocation(Long locationId);
    
    // ========== 时间线资源管理 ==========
    
    TimelineDTO createTimeline(TimelineDTO timelineDTO);
    
    TimelineDTO updateTimeline(Long timelineId, TimelineDTO timelineDTO);
    
    TimelineDTO getTimeline(Long timelineId);
    
    List<TimelineDTO> listTimelines(Long novelId);
    
    void deleteTimeline(Long timelineId);
    
    // ========== 事件资源管理 ==========
    
    EventDTO createEvent(EventDTO eventDTO);
    
    EventDTO updateEvent(Long eventId, EventDTO eventDTO);
    
    EventDTO getEvent(Long eventId);
    
    List<EventDTO> listEvents(Long novelId, Long timelineId, String search);
    
    void deleteEvent(Long eventId);
    
    // ========== 物品资源管理 ==========
    
    ItemResourceDTO createItemResource(ItemResourceDTO itemResourceDTO);
    
    ItemResourceDTO updateItemResource(Long itemId, ItemResourceDTO itemResourceDTO);
    
    ItemResourceDTO getItemResource(Long itemId);
    
    List<ItemResourceDTO> listItemResources(Long novelId, String search, List<String> tags);
    
    void deleteItemResource(Long itemId);
    
    // ========== 批量操作 ==========
    
    List<CharacterDTO> batchCreateCharacters(List<CharacterDTO> characterDTOs);
    
    List<LocationDTO> batchCreateLocations(List<LocationDTO> locationDTOs);
    
    // ========== 关系图谱 ==========
    
    Object getRelationshipGraph(Long novelId);
    
    // ========== 搜索与统计 ==========
    
    Object searchResources(Long novelId, String keyword, String resourceType);
    
    Object getResourceStatistics(Long novelId);
}