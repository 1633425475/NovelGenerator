package com.laoji.novelai.service.resource.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laoji.novelai.dto.resource.*;
import com.laoji.novelai.entity.resource.*;
import com.laoji.novelai.repository.resource.*;
import com.laoji.novelai.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 资源管理服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final CharacterRepository characterRepository;
    private final LocationRepository locationRepository;
    private final TimelineRepository timelineRepository;
    private final EventRepository eventRepository;
    private final ItemResourceRepository itemResourceRepository;
    private final ObjectMapper objectMapper;

    // ========== 人物资源管理 ==========

    @Override
    @Transactional
    public CharacterDTO createCharacter(CharacterDTO characterDTO) {
        com.laoji.novelai.entity.resource.Character character = convertToCharacterEntity(characterDTO);
        character.setUserId(1L); // TODO: 从当前登录用户获取
        character.setStatus("DRAFT");
        
        com.laoji.novelai.entity.resource.Character savedCharacter = characterRepository.save(character);
        return convertToCharacterDTO(savedCharacter);
    }

    @Override
    @Transactional
    public CharacterDTO updateCharacter(Long characterId, CharacterDTO characterDTO) {
        com.laoji.novelai.entity.resource.Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("人物不存在"));
        
        updateCharacterFromDTO(character, characterDTO);
        com.laoji.novelai.entity.resource.Character savedCharacter = characterRepository.save(character);
        return convertToCharacterDTO(savedCharacter);
    }

    @Override
    public CharacterDTO getCharacter(Long characterId) {
        com.laoji.novelai.entity.resource.Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("人物不存在"));
        return convertToCharacterDTO(character);
    }

    @Override
    public List<CharacterDTO> listCharacters(Long novelId, String search, List<String> tags) {
        List<com.laoji.novelai.entity.resource.Character> characters;
        if (novelId != null) {
            characters = characterRepository.findByNovelIdAndDeletedFalseOrderByName(novelId);
        } else {
            characters = characterRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(1L); // TODO: 当前用户
        }
        
        // 过滤搜索
        if (search != null && !search.isEmpty()) {
            characters = characters.stream()
                    .filter(c -> c.getName().contains(search) || 
                            (c.getAlias() != null && c.getAlias().contains(search)))
                    .collect(Collectors.toList());
        }
        
        // 过滤标签（简化处理）
        if (tags != null && !tags.isEmpty()) {
            characters = characters.stream()
                    .filter(c -> {
                        try {
                            List<String> characterTags = objectMapper.readValue(
                                    c.getTags() != null ? c.getTags() : "[]", 
                                    new TypeReference<List<String>>() {});
                            return characterTags.stream().anyMatch(tags::contains);
                        } catch (JsonProcessingException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }
        
        return characters.stream()
                .map(this::convertToCharacterDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCharacter(Long characterId) {
        com.laoji.novelai.entity.resource.Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("人物不存在"));
        character.softDelete();
        characterRepository.save(character);
    }

    @Override
    public CharacterDTO enhanceCharacterWithAI(Long characterId, String enhancementType) {
        // TODO: 实现AI增强功能
        throw new UnsupportedOperationException("AI增强功能暂未实现");
    }

    // ========== 地点资源管理 ==========

    @Override
    @Transactional
    public LocationDTO createLocation(LocationDTO locationDTO) {
        Location location = convertToLocationEntity(locationDTO);
        location.setUserId(1L); // TODO: 从当前登录用户获取
        location.setStatus("DRAFT");
        
        Location savedLocation = locationRepository.save(location);
        return convertToLocationDTO(savedLocation);
    }

    @Override
    @Transactional
    public LocationDTO updateLocation(Long locationId, LocationDTO locationDTO) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("地点不存在"));
        
        updateLocationFromDTO(location, locationDTO);
        Location savedLocation = locationRepository.save(location);
        return convertToLocationDTO(savedLocation);
    }

    @Override
    public LocationDTO getLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("地点不存在"));
        return convertToLocationDTO(location);
    }

    @Override
    public List<LocationDTO> listLocations(Long novelId, String search, List<String> tags) {
        List<Location> locations;
        if (novelId != null) {
            locations = locationRepository.findByNovelIdAndDeletedFalseOrderByName(novelId);
        } else {
            locations = locationRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(1L); // TODO: 当前用户
        }
        
        // 过滤搜索
        if (search != null && !search.isEmpty()) {
            locations = locations.stream()
                    .filter(l -> l.getName().contains(search) || 
                            (l.getAlias() != null && l.getAlias().contains(search)))
                    .collect(Collectors.toList());
        }
        
        // 过滤标签（简化处理）
        if (tags != null && !tags.isEmpty()) {
            locations = locations.stream()
                    .filter(l -> {
                        try {
                            List<String> locationTags = objectMapper.readValue(
                                    l.getTags() != null ? l.getTags() : "[]", 
                                    new TypeReference<List<String>>() {});
                            return locationTags.stream().anyMatch(tags::contains);
                        } catch (JsonProcessingException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }
        
        return locations.stream()
                .map(this::convertToLocationDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("地点不存在"));
        location.softDelete();
        locationRepository.save(location);
    }

    // ========== 时间线资源管理 ==========

    @Override
    @Transactional
    public TimelineDTO createTimeline(TimelineDTO timelineDTO) {
        Timeline timeline = convertToTimelineEntity(timelineDTO);
        timeline.setUserId(1L); // TODO: 从当前登录用户获取
        timeline.setStatus("DRAFT");
        
        Timeline savedTimeline = timelineRepository.save(timeline);
        return convertToTimelineDTO(savedTimeline);
    }

    @Override
    @Transactional
    public TimelineDTO updateTimeline(Long timelineId, TimelineDTO timelineDTO) {
        Timeline timeline = timelineRepository.findById(timelineId)
                .orElseThrow(() -> new RuntimeException("时间线不存在"));
        
        updateTimelineFromDTO(timeline, timelineDTO);
        Timeline savedTimeline = timelineRepository.save(timeline);
        return convertToTimelineDTO(savedTimeline);
    }

    @Override
    public TimelineDTO getTimeline(Long timelineId) {
        Timeline timeline = timelineRepository.findById(timelineId)
                .orElseThrow(() -> new RuntimeException("时间线不存在"));
        return convertToTimelineDTO(timeline);
    }

    @Override
    public List<TimelineDTO> listTimelines(Long novelId) {
        List<Timeline> timelines;
        if (novelId != null) {
            timelines = timelineRepository.findByNovelIdAndDeletedFalseOrderByIsMainDescCreatedAtDesc(novelId);
        } else {
            timelines = timelineRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(1L); // TODO: 当前用户
        }
        
        return timelines.stream()
                .map(this::convertToTimelineDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTimeline(Long timelineId) {
        Timeline timeline = timelineRepository.findById(timelineId)
                .orElseThrow(() -> new RuntimeException("时间线不存在"));
        timeline.softDelete();
        timelineRepository.save(timeline);
    }

    // ========== 事件资源管理 ==========

    @Override
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = convertToEventEntity(eventDTO);
        event.setUserId(1L); // TODO: 从当前登录用户获取
        event.setStatus("DRAFT");
        
        Event savedEvent = eventRepository.save(event);
        return convertToEventDTO(savedEvent);
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long eventId, EventDTO eventDTO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("事件不存在"));
        
        updateEventFromDTO(event, eventDTO);
        Event savedEvent = eventRepository.save(event);
        return convertToEventDTO(savedEvent);
    }

    @Override
    public EventDTO getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("事件不存在"));
        return convertToEventDTO(event);
    }

    @Override
    public List<EventDTO> listEvents(Long novelId, Long timelineId, String search) {
        List<Event> events;
        if (timelineId != null) {
            events = eventRepository.findByTimelineIdAndDeletedFalseOrderByTimePoint(timelineId);
        } else if (novelId != null) {
            events = eventRepository.findByNovelIdAndDeletedFalseOrderByTimePoint(novelId);
        } else {
            events = eventRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(1L); // TODO: 当前用户
        }
        
        // 过滤搜索
        if (search != null && !search.isEmpty()) {
            events = events.stream()
                    .filter(e -> e.getTitle().contains(search))
                    .collect(Collectors.toList());
        }
        
        return events.stream()
                .map(this::convertToEventDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("事件不存在"));
        event.softDelete();
        eventRepository.save(event);
    }

    // ========== 物品资源管理 ==========

    @Override
    @Transactional
    public ItemResourceDTO createItemResource(ItemResourceDTO itemResourceDTO) {
        ItemResource itemResource = convertToItemResourceEntity(itemResourceDTO);
        itemResource.setUserId(1L); // TODO: 从当前登录用户获取
        itemResource.setStatus("DRAFT");
        
        ItemResource savedItemResource = itemResourceRepository.save(itemResource);
        return convertToItemResourceDTO(savedItemResource);
    }

    @Override
    @Transactional
    public ItemResourceDTO updateItemResource(Long itemId, ItemResourceDTO itemResourceDTO) {
        ItemResource itemResource = itemResourceRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品不存在"));
        
        updateItemResourceFromDTO(itemResource, itemResourceDTO);
        ItemResource savedItemResource = itemResourceRepository.save(itemResource);
        return convertToItemResourceDTO(savedItemResource);
    }

    @Override
    public ItemResourceDTO getItemResource(Long itemId) {
        ItemResource itemResource = itemResourceRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品不存在"));
        return convertToItemResourceDTO(itemResource);
    }

    @Override
    public List<ItemResourceDTO> listItemResources(Long novelId, String search, List<String> tags) {
        List<ItemResource> itemResources;
        if (novelId != null) {
            itemResources = itemResourceRepository.findByNovelIdAndDeletedFalseOrderByName(novelId);
        } else {
            itemResources = itemResourceRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(1L); // TODO: 当前用户
        }
        
        // 过滤搜索
        if (search != null && !search.isEmpty()) {
            itemResources = itemResources.stream()
                    .filter(i -> i.getName().contains(search) || 
                            (i.getAlias() != null && i.getAlias().contains(search)))
                    .collect(Collectors.toList());
        }
        
        // 过滤标签（简化处理）
        if (tags != null && !tags.isEmpty()) {
            itemResources = itemResources.stream()
                    .filter(i -> {
                        try {
                            List<String> itemTags = objectMapper.readValue(
                                    i.getTags() != null ? i.getTags() : "[]", 
                                    new TypeReference<List<String>>() {});
                            return itemTags.stream().anyMatch(tags::contains);
                        } catch (JsonProcessingException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }
        
        return itemResources.stream()
                .map(this::convertToItemResourceDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteItemResource(Long itemId) {
        ItemResource itemResource = itemResourceRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品不存在"));
        itemResource.softDelete();
        itemResourceRepository.save(itemResource);
    }

    // ========== 批量操作 ==========

    @Override
    @Transactional
    public List<CharacterDTO> batchCreateCharacters(List<CharacterDTO> characterDTOs) {
        List<com.laoji.novelai.entity.resource.Character> characters = characterDTOs.stream()
                .map(dto -> {
                    com.laoji.novelai.entity.resource.Character character = convertToCharacterEntity(dto);
                    character.setUserId(1L); // TODO: 从当前登录用户获取
                    character.setStatus("DRAFT");
                    return character;
                })
                .collect(Collectors.toList());
        
        List<com.laoji.novelai.entity.resource.Character> savedCharacters = characterRepository.saveAll(characters);
        return savedCharacters.stream()
                .map(this::convertToCharacterDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<LocationDTO> batchCreateLocations(List<LocationDTO> locationDTOs) {
        List<Location> locations = locationDTOs.stream()
                .map(dto -> {
                    Location location = convertToLocationEntity(dto);
                    location.setUserId(1L); // TODO: 从当前登录用户获取
                    location.setStatus("DRAFT");
                    return location;
                })
                .collect(Collectors.toList());
        
        List<Location> savedLocations = locationRepository.saveAll(locations);
        return savedLocations.stream()
                .map(this::convertToLocationDTO)
                .collect(Collectors.toList());
    }

    // ========== 关系图谱 ==========

    @Override
    public Object getRelationshipGraph(Long novelId) {
        // 简化实现：返回基本的关系数据
        Map<String, Object> graph = new HashMap<>();
        
        // 获取所有人物
        List<com.laoji.novelai.entity.resource.Character> characters = characterRepository.findByNovelIdAndDeletedFalseOrderByName(novelId);
        List<Map<String, Object>> nodes = characters.stream()
                .map(c -> {
                    Map<String, Object> node = new HashMap<>();
                    node.put("id", c.getId());
                    node.put("name", c.getName());
                    node.put("type", "CHARACTER");
                    node.put("faction", c.getFaction());
                    return node;
                })
                .collect(Collectors.toList());
        
        // 获取关系边（简化处理）
        List<Map<String, Object>> edges = new ArrayList<>();
        for (com.laoji.novelai.entity.resource.Character character : characters) {
            try {
                if (character.getRelationships() != null) {
                    List<CharacterDTO.Relationship> relationships = objectMapper.readValue(
                            character.getRelationships(), 
                            new TypeReference<List<CharacterDTO.Relationship>>() {});
                    
                    for (CharacterDTO.Relationship rel : relationships) {
                        if (rel.getCharacterId() != null) {
                            Map<String, Object> edge = new HashMap<>();
                            edge.put("source", character.getId());
                            edge.put("target", rel.getCharacterId());
                            edge.put("type", rel.getRelationType());
                            edge.put("label", rel.getDescription());
                            edges.add(edge);
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                log.warn("解析人物关系失败: characterId={}", character.getId(), e);
            }
        }
        
        graph.put("nodes", nodes);
        graph.put("edges", edges);
        return graph;
    }

    // ========== 搜索与统计 ==========

    @Override
    public Object searchResources(Long novelId, String keyword, String resourceType) {
        Map<String, Object> result = new HashMap<>();
        
        if (keyword == null || keyword.isEmpty()) {
            result.put("characters", Collections.emptyList());
            result.put("locations", Collections.emptyList());
            result.put("events", Collections.emptyList());
            result.put("items", Collections.emptyList());
            return result;
        }
        
        if (resourceType == null || "CHARACTER".equalsIgnoreCase(resourceType)) {
            List<com.laoji.novelai.entity.resource.Character> characters = characterRepository.findByNameContainingAndDeletedFalse(keyword);
            result.put("characters", characters.stream()
                    .map(this::convertToCharacterDTO)
                    .collect(Collectors.toList()));
        }
        
        if (resourceType == null || "LOCATION".equalsIgnoreCase(resourceType)) {
            List<Location> locations = locationRepository.findByNameContainingAndDeletedFalse(keyword);
            result.put("locations", locations.stream()
                    .map(this::convertToLocationDTO)
                    .collect(Collectors.toList()));
        }
        
        if (resourceType == null || "EVENT".equalsIgnoreCase(resourceType)) {
            List<Event> events = eventRepository.findByTitleContainingAndDeletedFalse(keyword);
            result.put("events", events.stream()
                    .map(this::convertToEventDTO)
                    .collect(Collectors.toList()));
        }
        
        if (resourceType == null || "ITEM".equalsIgnoreCase(resourceType)) {
            List<ItemResource> items = itemResourceRepository.findByNameContainingAndDeletedFalse(keyword);
            result.put("items", items.stream()
                    .map(this::convertToItemResourceDTO)
                    .collect(Collectors.toList()));
        }
        
        return result;
    }

    @Override
    public Object getResourceStatistics(Long novelId) {
        Map<String, Object> stats = new HashMap<>();
        
        Long characterCount = characterRepository.countByNovelIdAndDeletedFalse(novelId);
        Long locationCount = locationRepository.countByNovelIdAndDeletedFalse(novelId);
        Long eventCount = Long.valueOf(eventRepository.findByNovelIdAndDeletedFalseOrderByTimePoint(novelId).size());
        Long itemCount = Long.valueOf(itemResourceRepository.findByNovelIdAndDeletedFalseOrderByName(novelId).size());
        
        stats.put("characterCount", characterCount);
        stats.put("locationCount", locationCount);
        stats.put("eventCount", eventCount);
        stats.put("itemCount", itemCount);
        stats.put("total", characterCount + locationCount + eventCount + itemCount);
        
        // 状态统计
        List<com.laoji.novelai.entity.resource.Character> characters = characterRepository.findByNovelIdAndDeletedFalseOrderByName(novelId);
        long draftCount = characters.stream().filter(c -> "DRAFT".equals(c.getStatus())).count();
        long completedCount = characters.stream().filter(c -> "COMPLETED".equals(c.getStatus())).count();
        
        stats.put("draftCount", draftCount);
        stats.put("completedCount", completedCount);
        
        return stats;
    }

    // ========== 转换方法 ==========

    private com.laoji.novelai.entity.resource.Character convertToCharacterEntity(CharacterDTO dto) {
        com.laoji.novelai.entity.resource.Character character = new com.laoji.novelai.entity.resource.Character();
        updateCharacterFromDTO(character, dto);
        return character;
    }

    private void updateCharacterFromDTO(com.laoji.novelai.entity.resource.Character character, CharacterDTO dto) {
        character.setName(dto.getName());
        character.setAlias(dto.getAlias());
        character.setGender(dto.getGender());
        character.setAge(dto.getAge());
        character.setRace(dto.getRace());
        character.setIdentity(dto.getIdentity());
        character.setFaction(dto.getFaction());
        character.setPersonality(dto.getPersonality());
        character.setAppearance(dto.getAppearance());
        character.setBackground(dto.getBackground());
        character.setAbilities(dto.getAbilities());
        character.setWeaknesses(dto.getWeaknesses());
        character.setGoals(dto.getGoals());
        character.setGrowthArc(dto.getGrowthArc());
        character.setQuotes(dto.getQuotes());
        character.setNovelId(dto.getNovelId());
        
        if (dto.getRelationships() != null) {
            try {
                character.setRelationships(objectMapper.writeValueAsString(dto.getRelationships()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换人物关系失败", e);
            }
        }
        
        if (dto.getTags() != null) {
            try {
                character.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换标签失败", e);
            }
        }
    }

    private CharacterDTO convertToCharacterDTO(com.laoji.novelai.entity.resource.Character character) {
        CharacterDTO dto = new CharacterDTO();
        dto.setId(character.getId());
        dto.setName(character.getName());
        dto.setAlias(character.getAlias());
        dto.setGender(character.getGender());
        dto.setAge(character.getAge());
        dto.setRace(character.getRace());
        dto.setIdentity(character.getIdentity());
        dto.setFaction(character.getFaction());
        dto.setPersonality(character.getPersonality());
        dto.setAppearance(character.getAppearance());
        dto.setBackground(character.getBackground());
        dto.setAbilities(character.getAbilities());
        dto.setWeaknesses(character.getWeaknesses());
        dto.setGoals(character.getGoals());
        dto.setGrowthArc(character.getGrowthArc());
        dto.setQuotes(character.getQuotes());
        dto.setNovelId(character.getNovelId());
        dto.setStatus(character.getStatus());
        dto.setCreatedAt(character.getCreatedAt());
        dto.setUpdatedAt(character.getUpdatedAt());
        
        try {
            if (character.getRelationships() != null) {
                dto.setRelationships(objectMapper.readValue(
                        character.getRelationships(), 
                        new TypeReference<List<CharacterDTO.Relationship>>() {}));
            }
            
            if (character.getTags() != null) {
                dto.setTags(objectMapper.readValue(
                        character.getTags(), 
                        new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            log.warn("转换人物DTO失败: characterId={}", character.getId(), e);
        }
        
        return dto;
    }

    private Location convertToLocationEntity(LocationDTO dto) {
        Location location = new Location();
        updateLocationFromDTO(location, dto);
        return location;
    }

    private void updateLocationFromDTO(Location location, LocationDTO dto) {
        location.setName(dto.getName());
        location.setAlias(dto.getAlias());
        location.setType(dto.getType());
        location.setGeography(dto.getGeography());
        location.setClimate(dto.getClimate());
        location.setArchitecture(dto.getArchitecture());
        location.setCulture(dto.getCulture());
        location.setHistory(dto.getHistory());
        location.setImportantPlaces(dto.getImportantPlaces());
        location.setPolitics(dto.getPolitics());
        location.setEconomy(dto.getEconomy());
        location.setNovelId(dto.getNovelId());
        
        if (dto.getConnections() != null) {
            try {
                location.setConnections(objectMapper.writeValueAsString(dto.getConnections()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换地点连接失败", e);
            }
        }
        
        if (dto.getCoordinates() != null) {
            try {
                location.setCoordinates(objectMapper.writeValueAsString(dto.getCoordinates()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换坐标失败", e);
            }
        }
        
        if (dto.getTags() != null) {
            try {
                location.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换标签失败", e);
            }
        }
    }

    private LocationDTO convertToLocationDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setAlias(location.getAlias());
        dto.setType(location.getType());
        dto.setGeography(location.getGeography());
        dto.setClimate(location.getClimate());
        dto.setArchitecture(location.getArchitecture());
        dto.setCulture(location.getCulture());
        dto.setHistory(location.getHistory());
        dto.setImportantPlaces(location.getImportantPlaces());
        dto.setPolitics(location.getPolitics());
        dto.setEconomy(location.getEconomy());
        dto.setNovelId(location.getNovelId());
        dto.setStatus(location.getStatus());
        dto.setCreatedAt(location.getCreatedAt());
        dto.setUpdatedAt(location.getUpdatedAt());
        
        try {
            if (location.getConnections() != null) {
                dto.setConnections(objectMapper.readValue(
                        location.getConnections(), 
                        new TypeReference<List<LocationDTO.Connection>>() {}));
            }
            
            if (location.getCoordinates() != null) {
                dto.setCoordinates(objectMapper.readValue(
                        location.getCoordinates(), 
                        new TypeReference<Map<String, Object>>() {}));
            }
            
            if (location.getTags() != null) {
                dto.setTags(objectMapper.readValue(
                        location.getTags(), 
                        new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            log.warn("转换地点DTO失败: locationId={}", location.getId(), e);
        }
        
        return dto;
    }

    private Timeline convertToTimelineEntity(TimelineDTO dto) {
        Timeline timeline = new Timeline();
        updateTimelineFromDTO(timeline, dto);
        return timeline;
    }

    private void updateTimelineFromDTO(Timeline timeline, TimelineDTO dto) {
        timeline.setName(dto.getName());
        timeline.setDescription(dto.getDescription());
        timeline.setTimeUnit(dto.getTimeUnit());
        timeline.setStartTime(dto.getStartTime());
        timeline.setEndTime(dto.getEndTime());
        timeline.setNovelId(dto.getNovelId());
        timeline.setIsMain(dto.getIsMain());
        
        if (dto.getTimePoints() != null) {
            try {
                timeline.setTimePoints(objectMapper.writeValueAsString(dto.getTimePoints()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换时间点失败", e);
            }
        }
        
        if (dto.getTags() != null) {
            try {
                timeline.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换标签失败", e);
            }
        }
    }

    private TimelineDTO convertToTimelineDTO(Timeline timeline) {
        TimelineDTO dto = new TimelineDTO();
        dto.setId(timeline.getId());
        dto.setName(timeline.getName());
        dto.setDescription(timeline.getDescription());
        dto.setTimeUnit(timeline.getTimeUnit());
        dto.setStartTime(timeline.getStartTime());
        dto.setEndTime(timeline.getEndTime());
        dto.setNovelId(timeline.getNovelId());
        dto.setStatus(timeline.getStatus());
        dto.setIsMain(timeline.getIsMain());
        dto.setCreatedAt(timeline.getCreatedAt());
        dto.setUpdatedAt(timeline.getUpdatedAt());
        
        try {
            if (timeline.getTimePoints() != null) {
                dto.setTimePoints(objectMapper.readValue(
                        timeline.getTimePoints(), 
                        new TypeReference<List<TimelineDTO.TimePoint>>() {}));
            }
            
            if (timeline.getTags() != null) {
                dto.setTags(objectMapper.readValue(
                        timeline.getTags(), 
                        new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            log.warn("转换时间线DTO失败: timelineId={}", timeline.getId(), e);
        }
        
        return dto;
    }

    private Event convertToEventEntity(EventDTO dto) {
        Event event = new Event();
        updateEventFromDTO(event, dto);
        return event;
    }

    private void updateEventFromDTO(Event event, EventDTO dto) {
        event.setTitle(dto.getTitle());
        event.setType(dto.getType());
        event.setDescription(dto.getDescription());
        event.setTimePoint(dto.getTimePoint());
        event.setDuration(dto.getDuration());
        event.setLocationId(dto.getLocationId());
        event.setCause(dto.getCause());
        event.setProcess(dto.getProcess());
        event.setResult(dto.getResult());
        event.setImpact(dto.getImpact());
        event.setIsTurningPoint(dto.getIsTurningPoint());
        event.setTimelineId(dto.getTimelineId());
        event.setNovelId(dto.getNovelId());
        
        if (dto.getParticipants() != null) {
            try {
                event.setParticipants(objectMapper.writeValueAsString(dto.getParticipants()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换参与者失败", e);
            }
        }
        
        if (dto.getTags() != null) {
            try {
                event.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换标签失败", e);
            }
        }
    }

    private EventDTO convertToEventDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setType(event.getType());
        dto.setDescription(event.getDescription());
        dto.setTimePoint(event.getTimePoint());
        dto.setDuration(event.getDuration());
        dto.setLocationId(event.getLocationId());
        dto.setCause(event.getCause());
        dto.setProcess(event.getProcess());
        dto.setResult(event.getResult());
        dto.setImpact(event.getImpact());
        dto.setIsTurningPoint(event.getIsTurningPoint());
        dto.setTimelineId(event.getTimelineId());
        dto.setNovelId(event.getNovelId());
        dto.setStatus(event.getStatus());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        
        try {
            if (event.getParticipants() != null) {
                dto.setParticipants(objectMapper.readValue(
                        event.getParticipants(), 
                        new TypeReference<List<EventDTO.Participant>>() {}));
            }
            
            if (event.getTags() != null) {
                dto.setTags(objectMapper.readValue(
                        event.getTags(), 
                        new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            log.warn("转换事件DTO失败: eventId={}", event.getId(), e);
        }
        
        return dto;
    }

    private ItemResource convertToItemResourceEntity(ItemResourceDTO dto) {
        ItemResource itemResource = new ItemResource();
        updateItemResourceFromDTO(itemResource, dto);
        return itemResource;
    }

    private void updateItemResourceFromDTO(ItemResource itemResource, ItemResourceDTO dto) {
        itemResource.setName(dto.getName());
        itemResource.setAlias(dto.getAlias());
        itemResource.setType(dto.getType());
        itemResource.setRarity(dto.getRarity());
        itemResource.setDescription(dto.getDescription());
        itemResource.setAppearance(dto.getAppearance());
        itemResource.setAbilities(dto.getAbilities());
        itemResource.setUsage(dto.getUsage());
        itemResource.setAcquisition(dto.getAcquisition());
        itemResource.setHistory(dto.getHistory());
        itemResource.setCurrentHolderId(dto.getCurrentHolderId());
        itemResource.setLocationId(dto.getLocationId());
        itemResource.setEventId(dto.getEventId());
        itemResource.setNovelId(dto.getNovelId());
        
        if (dto.getPreviousHolders() != null) {
            try {
                itemResource.setPreviousHolders(objectMapper.writeValueAsString(dto.getPreviousHolders()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换前持有者失败", e);
            }
        }
        
        if (dto.getTags() != null) {
            try {
                itemResource.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("转换标签失败", e);
            }
        }
    }

    private ItemResourceDTO convertToItemResourceDTO(ItemResource itemResource) {
        ItemResourceDTO dto = new ItemResourceDTO();
        dto.setId(itemResource.getId());
        dto.setName(itemResource.getName());
        dto.setAlias(itemResource.getAlias());
        dto.setType(itemResource.getType());
        dto.setRarity(itemResource.getRarity());
        dto.setDescription(itemResource.getDescription());
        dto.setAppearance(itemResource.getAppearance());
        dto.setAbilities(itemResource.getAbilities());
        dto.setUsage(itemResource.getUsage());
        dto.setAcquisition(itemResource.getAcquisition());
        dto.setHistory(itemResource.getHistory());
        dto.setCurrentHolderId(itemResource.getCurrentHolderId());
        dto.setLocationId(itemResource.getLocationId());
        dto.setEventId(itemResource.getEventId());
        dto.setNovelId(itemResource.getNovelId());
        dto.setStatus(itemResource.getStatus());
        dto.setCreatedAt(itemResource.getCreatedAt());
        dto.setUpdatedAt(itemResource.getUpdatedAt());
        
        try {
            if (itemResource.getPreviousHolders() != null) {
                dto.setPreviousHolders(objectMapper.readValue(
                        itemResource.getPreviousHolders(), 
                        new TypeReference<List<ItemResourceDTO.Holder>>() {}));
            }
            
            if (itemResource.getTags() != null) {
                dto.setTags(objectMapper.readValue(
                        itemResource.getTags(), 
                        new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            log.warn("转换物品DTO失败: itemId={}", itemResource.getId(), e);
        }
        
        return dto;
    }
}