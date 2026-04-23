import request from '@/utils/request'

// ========== 人物资源接口 ==========

/**
 * 人物关系
 */
export interface Relationship {
  characterId: number
  characterName: string
  relationType: string  // FRIEND-朋友, ENEMY-敌人, FAMILY-家人, LOVER-爱人, MENTOR-导师等
  description: string
}

/**
 * 人物资源DTO
 */
export interface CharacterDTO {
  id?: number
  name: string
  alias?: string
  gender?: string
  age?: string
  race?: string
  identity?: string
  faction?: string
  personality?: string
  appearance?: string
  background?: string
  abilities?: string
  weaknesses?: string
  goals?: string
  growthArc?: string
  relationships?: Relationship[]
  quotes?: string
  novelId: number
  status?: string
  tags?: string[]
  createdAt?: string
  updatedAt?: string
}

/**
 * 创建人物请求
 */
export interface CreateCharacterRequest extends Omit<CharacterDTO, 'id' | 'createdAt' | 'updatedAt'> {}

/**
 * 更新人物请求
 */
export interface UpdateCharacterRequest extends Omit<CharacterDTO, 'createdAt' | 'updatedAt'> {}

// ========== 地点资源接口 ==========

/**
 * 地点连接关系
 */
export interface Connection {
  locationId: number
  locationName: string
  connectionType: string  // NEARBY-附近, CONNECTED-连接, PART_OF-属于
  description: string
}

/**
 * 地点资源DTO
 */
export interface LocationDTO {
  id?: number
  name: string
  alias?: string
  type?: string
  geography?: string
  climate?: string
  architecture?: string
  culture?: string
  history?: string
  importantPlaces?: string
  politics?: string
  economy?: string
  connections?: Connection[]
  coordinates?: Record<string, any>
  novelId: number
  status?: string
  tags?: string[]
  createdAt?: string
  updatedAt?: string
}

/**
 * 创建地点请求
 */
export interface CreateLocationRequest extends Omit<LocationDTO, 'id' | 'createdAt' | 'updatedAt'> {}

/**
 * 更新地点请求
 */
export interface UpdateLocationRequest extends Omit<LocationDTO, 'createdAt' | 'updatedAt'> {}

// ========== 时间线资源接口 ==========

/**
 * 相关实体
 */
export interface RelatedEntity {
  type: string  // CHARACTER, LOCATION, EVENT, ITEM
  id: number
  name: string
}

/**
 * 时间点
 */
export interface TimePoint {
  time: string
  title: string
  description: string
  relatedEntities?: RelatedEntity[]
}

/**
 * 时间线资源DTO
 */
export interface TimelineDTO {
  id?: number
  name: string
  description?: string
  timeUnit?: string
  startTime?: string
  endTime?: string
  timePoints?: TimePoint[]
  novelId: number
  status?: string
  isMain?: boolean
  tags?: string[]
  createdAt?: string
  updatedAt?: string
}

/**
 * 创建时间线请求
 */
export interface CreateTimelineRequest extends Omit<TimelineDTO, 'id' | 'createdAt' | 'updatedAt'> {}

/**
 * 更新时间线请求
 */
export interface UpdateTimelineRequest extends Omit<TimelineDTO, 'createdAt' | 'updatedAt'> {}

// ========== 事件资源接口 ==========

/**
 * 参与者
 */
export interface Participant {
  characterId: number
  characterName: string
  role: string  // 角色：主角、反派、旁观者等
}

/**
 * 事件资源DTO
 */
export interface EventDTO {
  id?: number
  title: string
  type?: string
  description?: string
  timePoint?: string
  duration?: string
  locationId?: number
  locationName?: string
  participants?: Participant[]
  cause?: string
  process?: string
  result?: string
  impact?: string
  isTurningPoint?: boolean
  timelineId?: number
  novelId: number
  status?: string
  tags?: string[]
  createdAt?: string
  updatedAt?: string
}

/**
 * 创建事件请求
 */
export interface CreateEventRequest extends Omit<EventDTO, 'id' | 'createdAt' | 'updatedAt'> {}

/**
 * 更新事件请求
 */
export interface UpdateEventRequest extends Omit<EventDTO, 'createdAt' | 'updatedAt'> {}

// ========== 物品资源接口 ==========

/**
 * 持有者
 */
export interface Holder {
  characterId: number
  characterName: string
  period: string  // 持有时期
}

/**
 * 物品资源DTO
 */
export interface ItemResourceDTO {
  id?: number
  name: string
  alias?: string
  type?: string
  rarity?: string
  description?: string
  appearance?: string
  abilities?: string
  usage?: string
  acquisition?: string
  history?: string
  currentHolderId?: number
  currentHolderName?: string
  previousHolders?: Holder[]
  locationId?: number
  locationName?: string
  eventId?: number
  eventTitle?: string
  novelId: number
  status?: string
  tags?: string[]
  createdAt?: string
  updatedAt?: string
}

/**
 * 创建物品请求
 */
export interface CreateItemResourceRequest extends Omit<ItemResourceDTO, 'id' | 'createdAt' | 'updatedAt'> {}

/**
 * 更新物品请求
 */
export interface UpdateItemResourceRequest extends Omit<ItemResourceDTO, 'createdAt' | 'updatedAt'> {}

// ========== 搜索与统计接口 ==========

/**
 * 搜索结果
 */
export interface SearchResult {
  type: string
  id: number
  name: string
  description?: string
  relevance: number
}

/**
 * 资源统计
 */
export interface ResourceStatistics {
  totalCharacters: number
  totalLocations: number
  totalTimelines: number
  totalEvents: number
  totalItems: number
  charactersByFaction: Record<string, number>
  locationsByType: Record<string, number>
  eventsByType: Record<string, number>
}

/**
 * 关系图节点
 */
export interface RelationshipNode {
  id: number
  name: string
  type: string
}

/**
 * 关系图边
 */
export interface RelationshipEdge {
  source: number
  target: number
  type: string
  description: string
}

/**
 * 关系图谱
 */
export interface RelationshipGraph {
  nodes: RelationshipNode[]
  edges: RelationshipEdge[]
}

// ========== API函数 ==========

/**
 * 创建人物
 */
export function createCharacter(novelId: number, data: CreateCharacterRequest): Promise<CharacterDTO> {
  return request.post(`/api/novels/${novelId}/resources/characters`, data)
}

/**
 * 更新人物
 */
export function updateCharacter(novelId: number, characterId: number, data: UpdateCharacterRequest): Promise<CharacterDTO> {
  return request.put(`/api/novels/${novelId}/resources/characters/${characterId}`, data)
}

/**
 * 获取人物详情
 */
export function getCharacter(novelId: number, characterId: number): Promise<CharacterDTO> {
  return request.get(`/api/novels/${novelId}/resources/characters/${characterId}`)
}

/**
 * 获取人物列表
 */
export function listCharacters(novelId: number, search?: string, tags?: string[]): Promise<CharacterDTO[]> {
  const params = new URLSearchParams()
  if (search) params.append('search', search)
  if (tags && tags.length > 0) params.append('tags', tags.join(','))
  return request.get(`/api/novels/${novelId}/resources/characters?${params.toString()}`)
}

/**
 * 删除人物
 */
export function deleteCharacter(novelId: number, characterId: number): Promise<void> {
  return request.delete(`/api/novels/${novelId}/resources/characters/${characterId}`)
}

/**
 * AI增强人物
 */
export function enhanceCharacterWithAI(novelId: number, characterId: number, enhancementType: string = 'DETAIL'): Promise<CharacterDTO> {
  return request.post(`/api/novels/${novelId}/resources/characters/${characterId}/enhance?enhancementType=${enhancementType}`)
}

/**
 * 创建地点
 */
export function createLocation(novelId: number, data: CreateLocationRequest): Promise<LocationDTO> {
  return request.post(`/api/novels/${novelId}/resources/locations`, data)
}

/**
 * 更新地点
 */
export function updateLocation(novelId: number, locationId: number, data: UpdateLocationRequest): Promise<LocationDTO> {
  return request.put(`/api/novels/${novelId}/resources/locations/${locationId}`, data)
}

/**
 * 获取地点详情
 */
export function getLocation(novelId: number, locationId: number): Promise<LocationDTO> {
  return request.get(`/api/novels/${novelId}/resources/locations/${locationId}`)
}

/**
 * 获取地点列表
 */
export function listLocations(novelId: number, search?: string, tags?: string[]): Promise<LocationDTO[]> {
  const params = new URLSearchParams()
  if (search) params.append('search', search)
  if (tags && tags.length > 0) params.append('tags', tags.join(','))
  return request.get(`/api/novels/${novelId}/resources/locations?${params.toString()}`)
}

/**
 * 删除地点
 */
export function deleteLocation(novelId: number, locationId: number): Promise<void> {
  return request.delete(`/api/novels/${novelId}/resources/locations/${locationId}`)
}

/**
 * 创建时间线
 */
export function createTimeline(novelId: number, data: CreateTimelineRequest): Promise<TimelineDTO> {
  return request.post(`/api/novels/${novelId}/resources/timelines`, data)
}

/**
 * 更新时间线
 */
export function updateTimeline(novelId: number, timelineId: number, data: UpdateTimelineRequest): Promise<TimelineDTO> {
  return request.put(`/api/novels/${novelId}/resources/timelines/${timelineId}`, data)
}

/**
 * 获取时间线详情
 */
export function getTimeline(novelId: number, timelineId: number): Promise<TimelineDTO> {
  return request.get(`/api/novels/${novelId}/resources/timelines/${timelineId}`)
}

/**
 * 获取时间线列表
 */
export function listTimelines(novelId: number): Promise<TimelineDTO[]> {
  return request.get(`/api/novels/${novelId}/resources/timelines`)
}

/**
 * 删除时间线
 */
export function deleteTimeline(novelId: number, timelineId: number): Promise<void> {
  return request.delete(`/api/novels/${novelId}/resources/timelines/${timelineId}`)
}

/**
 * 创建事件
 */
export function createEvent(novelId: number, data: CreateEventRequest): Promise<EventDTO> {
  return request.post(`/api/novels/${novelId}/resources/events`, data)
}

/**
 * 更新事件
 */
export function updateEvent(novelId: number, eventId: number, data: UpdateEventRequest): Promise<EventDTO> {
  return request.put(`/api/novels/${novelId}/resources/events/${eventId}`, data)
}

/**
 * 获取事件详情
 */
export function getEvent(novelId: number, eventId: number): Promise<EventDTO> {
  return request.get(`/api/novels/${novelId}/resources/events/${eventId}`)
}

/**
 * 获取事件列表
 */
export function listEvents(novelId: number, timelineId?: number, search?: string): Promise<EventDTO[]> {
  const params = new URLSearchParams()
  if (timelineId) params.append('timelineId', timelineId.toString())
  if (search) params.append('search', search)
  return request.get(`/api/novels/${novelId}/resources/events?${params.toString()}`)
}

/**
 * 删除事件
 */
export function deleteEvent(novelId: number, eventId: number): Promise<void> {
  return request.delete(`/api/novels/${novelId}/resources/events/${eventId}`)
}

/**
 * 创建物品
 */
export function createItemResource(novelId: number, data: CreateItemResourceRequest): Promise<ItemResourceDTO> {
  return request.post(`/api/novels/${novelId}/resources/items`, data)
}

/**
 * 更新物品
 */
export function updateItemResource(novelId: number, itemId: number, data: UpdateItemResourceRequest): Promise<ItemResourceDTO> {
  return request.put(`/api/novels/${novelId}/resources/items/${itemId}`, data)
}

/**
 * 获取物品详情
 */
export function getItemResource(novelId: number, itemId: number): Promise<ItemResourceDTO> {
  return request.get(`/api/novels/${novelId}/resources/items/${itemId}`)
}

/**
 * 获取物品列表
 */
export function listItemResources(novelId: number, search?: string, tags?: string[]): Promise<ItemResourceDTO[]> {
  const params = new URLSearchParams()
  if (search) params.append('search', search)
  if (tags && tags.length > 0) params.append('tags', tags.join(','))
  return request.get(`/api/novels/${novelId}/resources/items?${params.toString()}`)
}

/**
 * 删除物品
 */
export function deleteItemResource(novelId: number, itemId: number): Promise<void> {
  return request.delete(`/api/novels/${novelId}/resources/items/${itemId}`)
}

/**
 * 批量创建人物
 */
export function batchCreateCharacters(novelId: number, characters: CreateCharacterRequest[]): Promise<CharacterDTO[]> {
  return request.post(`/api/novels/${novelId}/resources/characters/batch`, characters)
}

/**
 * 批量创建地点
 */
export function batchCreateLocations(novelId: number, locations: CreateLocationRequest[]): Promise<LocationDTO[]> {
  return request.post(`/api/novels/${novelId}/resources/locations/batch`, locations)
}

/**
 * 获取关系图谱
 */
export function getRelationshipGraph(novelId: number): Promise<RelationshipGraph> {
  return request.get(`/api/novels/${novelId}/resources/relationship-graph`)
}

/**
 * 搜索资源
 */
export function searchResources(novelId: number, keyword: string, resourceType?: string): Promise<SearchResult[]> {
  const params = new URLSearchParams()
  params.append('keyword', keyword)
  if (resourceType) params.append('resourceType', resourceType)
  return request.get(`/api/novels/${novelId}/resources/search?${params.toString()}`)
}

/**
 * 获取资源统计
 */
export function getResourceStatistics(novelId: number): Promise<ResourceStatistics> {
  return request.get(`/api/novels/${novelId}/resources/statistics`)
}