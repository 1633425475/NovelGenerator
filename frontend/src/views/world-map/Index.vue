<template>
  <div class="world-map-container">
    <el-page-header @back="goBack" class="page-header">
      <template #content>
        <div class="page-header-content">
          <h1>世界地图</h1>
          <p>可视化小说中的地点、事件、人物和资源</p>
        </div>
      </template>
      <template #extra>
        <div class="header-actions">
          <el-button type="primary" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
          <el-button @click="toggleLegend">
            <el-icon><View /></el-icon>
            {{ showLegend ? '隐藏图例' : '显示图例' }}
          </el-button>
        </div>
      </template>
    </el-page-header>

    <div class="main-content">
      <!-- 左侧控制面板 -->
      <div class="control-panel" v-if="showLegend">
        <div class="panel-section">
          <h3><el-icon><Setting /></el-icon> 地图控制</h3>
          <el-form label-width="80px" size="small">
            <el-form-item label="缩放级别">
              <el-slider
                v-model="zoomLevel"
                :min="1"
                :max="10"
                @change="updateZoom"
                show-stops
              />
            </el-form-item>
            <el-form-item label="地图类型">
              <el-select v-model="mapType" @change="changeMapType">
                <el-option label="标准地图" value="standard" />
                <el-option label="卫星地图" value="satellite" />
                <el-option label="地形图" value="terrain" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>

        <div class="panel-section">
          <h3><el-icon><LocationInformation /></el-icon> 显示选项</h3>
          <el-checkbox-group v-model="visibleLayers">
            <el-checkbox label="locations">地点标记</el-checkbox>
            <el-checkbox label="events">事件标注</el-checkbox>
            <el-checkbox label="characters">人物位置</el-checkbox>
            <el-checkbox label="items">重要物品</el-checkbox>
            <el-checkbox label="connections">地点连接</el-checkbox>
          </el-checkbox-group>
        </div>

        <div class="panel-section">
          <h3><el-icon><Filter /></el-icon> 筛选条件</h3>
          <el-form label-width="80px" size="small">
            <el-form-item label="地点类型">
              <el-select v-model="selectedLocationTypes" multiple placeholder="全部类型">
                <el-option v-for="type in locationTypes" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
            <el-form-item label="事件类型">
              <el-select v-model="selectedEventTypes" multiple placeholder="全部类型">
                <el-option v-for="type in eventTypes" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="timeRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                value-format="YYYY-MM-DD"
                size="small"
              />
            </el-form-item>
          </el-form>
        </div>

        <div class="panel-section">
          <h3><el-icon><InfoFilled /></el-icon> 图例</h3>
          <div class="legend">
            <div class="legend-item">
              <span class="legend-color location"></span>
              <span>地点</span>
            </div>
            <div class="legend-item">
              <span class="legend-color event"></span>
              <span>事件</span>
            </div>
            <div class="legend-item">
              <span class="legend-color character"></span>
              <span>人物</span>
            </div>
            <div class="legend-item">
              <span class="legend-color item"></span>
              <span>物品</span>
            </div>
            <div class="legend-item">
              <span class="legend-line connection"></span>
              <span>连接关系</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 地图区域 -->
      <div class="map-area">
        <div id="world-map" ref="mapContainer"></div>
        
        <!-- 地图工具栏 -->
        <div class="map-toolbar">
          <el-button-group>
            <el-button size="small" @click="zoomIn">
              <el-icon><Plus /></el-icon>
            </el-button>
            <el-button size="small" @click="zoomOut">
              <el-icon><Minus /></el-icon>
            </el-button>
            <el-button size="small" @click="fitBounds">
              <el-icon><FullScreen /></el-icon>
            </el-button>
          </el-button-group>
          
          <div class="coordinate-display" v-if="mouseCoordinates">
            坐标: {{ mouseCoordinates.lat.toFixed(4) }}, {{ mouseCoordinates.lng.toFixed(4) }}
          </div>
        </div>

        <!-- 信息面板 -->
        <div class="info-panel" v-if="selectedFeature">
          <div class="info-header">
            <h4>{{ selectedFeature.name }}</h4>
            <el-button type="text" @click="selectedFeature = null">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="info-content">
            <div v-if="selectedFeature.type === 'location'">
              <p><strong>类型:</strong> {{ selectedFeature.data.type || '未指定' }}</p>
              <p><strong>描述:</strong> {{ selectedFeature.data.description || '无描述' }}</p>
              <p><strong>关联事件:</strong> {{ selectedFeature.data.eventCount || 0 }} 个</p>
              <p><strong>关联人物:</strong> {{ selectedFeature.data.characterCount || 0 }} 个</p>
              <div class="info-actions">
                <el-button size="small" @click="editLocation(selectedFeature.data)">
                  <el-icon><Edit /></el-icon>
                  编辑地点
                </el-button>
                <el-button size="small" @click="viewDetails(selectedFeature.data)">
                  <el-icon><View /></el-icon>
                  查看详情
                </el-button>
              </div>
            </div>
            <div v-else-if="selectedFeature.type === 'event'">
              <p><strong>时间:</strong> {{ selectedFeature.data.timePoint || '未指定' }}</p>
              <p><strong>描述:</strong> {{ selectedFeature.data.description || '无描述' }}</p>
              <p><strong>参与者:</strong> {{ selectedFeature.data.participants?.length || 0 }} 人</p>
              <div class="info-actions">
                <el-button size="small" @click="editEvent(selectedFeature.data)">
                  <el-icon><Edit /></el-icon>
                  编辑事件
                </el-button>
              </div>
            </div>
            <div v-else-if="selectedFeature.type === 'character'">
              <p><strong>身份:</strong> {{ selectedFeature.data.identity || '未指定' }}</p>
              <p><strong>描述:</strong> {{ selectedFeature.data.background || '无描述' }}</p>
              <div class="info-actions">
                <el-button size="small" @click="editCharacter(selectedFeature.data)">
                  <el-icon><Edit /></el-icon>
                  编辑人物
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <el-loading :loading="loading" text="正在加载地图数据..." />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import {
  Refresh, View, Setting, LocationInformation, Filter, InfoFilled,
  Plus, Minus, FullScreen, Close, Edit
} from '@element-plus/icons-vue'
import { listLocations } from '@/api/resource'
import { listEvents } from '@/api/resource'
import { listCharacters } from '@/api/resource'
import { listItemResources } from '@/api/resource'

const router = useRouter()
const route = useRoute()

// 响应式数据
const mapContainer = ref<HTMLElement | null>(null)
const mapInstance = ref<L.Map | null>(null)
const loading = ref(false)
const showLegend = ref(true)
const zoomLevel = ref(3)
const mapType = ref('standard')
const visibleLayers = ref(['locations', 'events', 'characters', 'items', 'connections'])
const selectedLocationTypes = ref<string[]>([])
const selectedEventTypes = ref<string[]>([])
const timeRange = ref<[string, string] | null>(null)
const mouseCoordinates = ref<{ lat: number; lng: number } | null>(null)
const selectedFeature = ref<any>(null)

// 数据存储
const locations = ref<any[]>([])
const events = ref<any[]>([])
const characters = ref<any[]>([])
const items = ref<any[]>([])

// 图层组
const locationMarkers = ref<L.LayerGroup | null>(null)
const eventMarkers = ref<L.LayerGroup | null>(null)
const characterMarkers = ref<L.LayerGroup | null>(null)
const itemMarkers = ref<L.LayerGroup | null>(null)
const connectionLines = ref<L.LayerGroup | null>(null)

// 计算属性
const locationTypes = computed(() => {
  const types = new Set<string>()
  locations.value.forEach(loc => {
    if (loc.type) types.add(loc.type)
  })
  return Array.from(types)
})

const eventTypes = computed(() => {
  const types = new Set<string>()
  events.value.forEach(event => {
    if (event.type) types.add(event.type)
  })
  return Array.from(types)
})

// 方法
const goBack = () => {
  router.back()
}

const refreshData = async () => {
  loading.value = true
  try {
    // 获取当前小说ID（这里假设从路由或store获取，暂时使用固定值）
    const novelId = 1
    
    // 并行获取所有数据
    const [locationsRes, eventsRes, charactersRes, itemsRes] = await Promise.all([
      listLocations(novelId),
      listEvents(novelId),
      listCharacters(novelId),
      listItemResources(novelId)
    ])
    
    locations.value = locationsRes
    events.value = eventsRes
    characters.value = charactersRes
    items.value = itemsRes
    
    // 重新渲染地图
    renderMapFeatures()
    
    // 处理查询参数（聚焦到特定要素）
    handleQueryParameters()
    
    ElMessage.success('数据刷新成功')
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

// 处理查询参数（聚焦到特定要素）
const handleQueryParameters = () => {
  if (!mapInstance.value) return
  
  const query = route.query
  
  // 聚焦到特定地点
  if (query.focus === 'location' && query.locationId) {
    const locationId = parseInt(query.locationId as string)
    if (!isNaN(locationId)) {
      const location = locations.value.find(loc => loc.id === locationId)
      if (location && location.coordinates) {
        const lat = location.coordinates.lat || location.coordinates.y
        const lng = location.coordinates.lng || location.coordinates.x
        if (lat !== undefined && lng !== undefined) {
          mapInstance.value.panTo([lat, lng])
          mapInstance.value.setZoom(8)
          
          // 高亮显示该地点标记
          setTimeout(() => {
            const marker = locationMarkers.value?.getLayers().find((layer: any) => {
              return layer.options?.locationId === locationId
            })
            if (marker) {
              marker.openPopup()
              selectedFeature.value = {
                type: 'location',
                name: location.name,
                data: location
              }
            }
          }, 500)
        }
      }
    }
  }
  
  // 聚焦到特定事件
  if (query.focus === 'event' && query.eventId) {
    const eventId = parseInt(query.eventId as string)
    if (!isNaN(eventId)) {
      const event = events.value.find(ev => ev.id === eventId)
      if (event && event.locationId) {
        const location = locations.value.find(loc => loc.id === event.locationId)
        if (location && location.coordinates) {
          const lat = location.coordinates.lat || location.coordinates.y
          const lng = location.coordinates.lng || location.coordinates.x
          if (lat !== undefined && lng !== undefined) {
            mapInstance.value.panTo([lat, lng])
            mapInstance.value.setZoom(8)
            
            // 高亮显示该事件标记
            setTimeout(() => {
              const marker = eventMarkers.value?.getLayers().find((layer: any) => {
                return layer.options?.eventId === eventId
              })
              if (marker) {
                marker.openPopup()
                selectedFeature.value = {
                  type: 'event',
                  name: event.title,
                  data: event
                }
              }
            }, 500)
          }
        }
      }
    }
  }
  
  // 聚焦到特定人物
  if (query.focus === 'character' && query.characterId) {
    const characterId = parseInt(query.characterId as string)
    if (!isNaN(characterId)) {
      const character = characters.value.find(char => char.id === characterId)
      if (character) {
        // 查找人物位置（通过参与事件）
        const characterEvents = events.value.filter(event => 
          event.participants && 
          event.participants.some((p: any) => p.characterId === characterId)
        )
        
        let lat = 30, lng = 120
        let hasLocation = false
        
        // 获取事件关联的地点
        for (const event of characterEvents) {
          if (event.locationId) {
            const location = locations.value.find(loc => loc.id === event.locationId)
            if (location && location.coordinates) {
              lat = location.coordinates.lat || location.coordinates.y || 30
              lng = location.coordinates.lng || location.coordinates.x || 120
              hasLocation = true
              break
            }
          }
        }
        
        mapInstance.value.panTo([lat, lng])
        mapInstance.value.setZoom(8)
        
        // 高亮显示该人物标记
        setTimeout(() => {
          const marker = characterMarkers.value?.getLayers().find((layer: any) => {
            return layer.options?.characterId === characterId
          })
          if (marker) {
            marker.openPopup()
            selectedFeature.value = {
              type: 'character',
              name: character.name,
              data: character
            }
          }
        }, 500)
      }
    }
  }
  
  // 直接通过坐标聚焦
  if (query.lat && query.lng) {
    const lat = parseFloat(query.lat as string)
    const lng = parseFloat(query.lng as string)
    if (!isNaN(lat) && !isNaN(lng)) {
      mapInstance.value.panTo([lat, lng])
      mapInstance.value.setZoom(8)
    }
  }
}

const initMap = () => {
  if (!mapContainer.value || mapInstance.value) return
  
  // 创建地图实例
  mapInstance.value = L.map(mapContainer.value).setView([30, 120], zoomLevel.value)
  
  // 添加底图（使用OpenStreetMap）
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(mapInstance.value)
  
  // 创建图层组
  locationMarkers.value = L.layerGroup().addTo(mapInstance.value)
  eventMarkers.value = L.layerGroup().addTo(mapInstance.value)
  characterMarkers.value = L.layerGroup().addTo(mapInstance.value)
  itemMarkers.value = L.layerGroup().addTo(mapInstance.value)
  connectionLines.value = L.layerGroup().addTo(mapInstance.value)
  
  // 添加鼠标移动事件监听坐标
  mapInstance.value.on('mousemove', (e: L.LeafletMouseEvent) => {
    mouseCoordinates.value = {
      lat: e.latlng.lat,
      lng: e.latlng.lng
    }
  })
  
  // 添加点击事件清除选中
  mapInstance.value.on('click', () => {
    selectedFeature.value = null
  })
}

const renderMapFeatures = () => {
  if (!mapInstance.value) return
  
  // 清除现有标记
  locationMarkers.value?.clearLayers()
  eventMarkers.value?.clearLayers()
  characterMarkers.value?.clearLayers()
  itemMarkers.value?.clearLayers()
  connectionLines.value?.clearLayers()
  
  // 渲染地点标记
  if (visibleLayers.value.includes('locations')) {
    locations.value.forEach(location => {
      // 检查坐标是否存在
      let lat = 30, lng = 120 // 默认坐标
      if (location.coordinates) {
        // 解析坐标（假设坐标为 { lat: 30, lng: 120 } 格式）
        lat = location.coordinates.lat || location.coordinates.y || 30
        lng = location.coordinates.lng || location.coordinates.x || 120
      }
      
      const marker = L.marker([lat, lng], {
        icon: L.divIcon({
          html: `<div class="location-marker"><i class="el-icon-location"></i></div>`,
          className: 'location-icon',
          iconSize: [30, 30]
        }),
        locationId: location.id
      })
      
      marker.bindPopup(`
        <div class="map-popup">
          <h4>${location.name}</h4>
          <p>${location.type || '未指定类型'}</p>
          <p>${location.description || '无描述'}</p>
          <button onclick="window.vm.viewLocationDetails(${location.id})">查看详情</button>
        </div>
      `)
      
      marker.on('click', () => {
        selectedFeature.value = {
          type: 'location',
          name: location.name,
          data: location
        }
      })
      
      locationMarkers.value?.addLayer(marker)
    })
  }
  
  // 渲染事件标记（关联地点）
  if (visibleLayers.value.includes('events')) {
    events.value.forEach(event => {
      if (!event.locationId) return
      
      // 查找关联的地点
      const relatedLocation = locations.value.find(loc => loc.id === event.locationId)
      if (!relatedLocation || !relatedLocation.coordinates) return
      
      let lat = relatedLocation.coordinates.lat || relatedLocation.coordinates.y || 30
      let lng = relatedLocation.coordinates.lng || relatedLocation.coordinates.x || 120
      
      const marker = L.marker([lat, lng], {
        icon: L.divIcon({
          html: `<div class="event-marker"><i class="el-icon-calendar"></i></div>`,
          className: 'event-icon',
          iconSize: [25, 25]
        }),
        eventId: event.id
      })
      
      marker.bindPopup(`
        <div class="map-popup">
          <h4>${event.title}</h4>
          <p>时间: ${event.timePoint || '未指定'}</p>
          <p>${event.description || '无描述'}</p>
          <button onclick="window.vm.viewEventDetails(${event.id})">查看详情</button>
        </div>
      `)
      
      marker.on('click', () => {
        selectedFeature.value = {
          type: 'event',
          name: event.title,
          data: event
        }
      })
      
      eventMarkers.value?.addLayer(marker)
    })
  }
  
  // 渲染人物位置（根据参与事件的地点确定）
  if (visibleLayers.value.includes('characters')) {
    characters.value.forEach(character => {
      // 查找人物参与的事件
      const characterEvents = events.value.filter(event => 
        event.participants && 
        event.participants.some((p: any) => p.characterId === character.id)
      )
      
      // 获取事件关联的地点
      const eventLocations = characterEvents
        .map(event => {
          if (!event.locationId) return null
          return locations.value.find(loc => loc.id === event.locationId)
        })
        .filter(loc => loc && loc.coordinates)
      
      let lat = 30, lng = 120 // 默认坐标
      
      if (eventLocations.length > 0) {
        // 使用第一个事件地点的坐标
        const location = eventLocations[0]
        lat = location.coordinates.lat || location.coordinates.y || 30
        lng = location.coordinates.lng || location.coordinates.x || 120
      } else {
        // 如果没有参与事件，随机分配坐标（保持原有逻辑）
        lat = 30 + Math.random() * 10 - 5
        lng = 120 + Math.random() * 10 - 5
      }
      
      const marker = L.marker([lat, lng], {
        icon: L.divIcon({
          html: `<div class="character-marker"><i class="el-icon-user"></i></div>`,
          className: 'character-icon',
          iconSize: [25, 25]
        }),
        characterId: character.id
      })
      
      marker.bindPopup(`
        <div class="map-popup">
          <h4>${character.name}</h4>
          <p>身份: ${character.identity || '未指定'}</p>
          <p>${character.background || '无描述'}</p>
          <p>参与事件: ${characterEvents.length} 个</p>
          <button onclick="window.vm.viewCharacterDetails(${character.id})">查看详情</button>
        </div>
      `)
      
      marker.on('click', () => {
        selectedFeature.value = {
          type: 'character',
          name: character.name,
          data: character
        }
      })
      
      characterMarkers.value?.addLayer(marker)
    })
  }
  
  // 渲染地点连接关系
  if (visibleLayers.value.includes('connections')) {
    locations.value.forEach(location => {
      if (!location.connections || !Array.isArray(location.connections)) return
      
      location.connections.forEach((conn: any) => {
        const targetLocation = locations.value.find(loc => loc.id === conn.locationId)
        if (!targetLocation || !targetLocation.coordinates || !location.coordinates) return
        
        const startLat = location.coordinates.lat || location.coordinates.y || 30
        const startLng = location.coordinates.lng || location.coordinates.x || 120
        const endLat = targetLocation.coordinates.lat || targetLocation.coordinates.y || 30
        const endLng = targetLocation.coordinates.lng || targetLocation.coordinates.x || 120
        
        const line = L.polyline([[startLat, startLng], [endLat, endLng]], {
          color: '#409EFF',
          weight: 2,
          dashArray: '5, 5'
        })
        
        line.bindPopup(`
          <div class="map-popup">
            <h4>连接关系</h4>
            <p>${location.name} ↔ ${targetLocation.name}</p>
            <p>类型: ${conn.connectionType || '未指定'}</p>
            <p>描述: ${conn.description || '无描述'}</p>
          </div>
        `)
        
        connectionLines.value?.addLayer(line)
      })
    })
  }
}

const updateZoom = (zoom: number) => {
  if (mapInstance.value) {
    mapInstance.value.setZoom(zoom)
  }
}

const changeMapType = (type: string) => {
  if (!mapInstance.value) return
  
  // 清除现有底图
  mapInstance.value.eachLayer((layer) => {
    if (layer instanceof L.TileLayer) {
      mapInstance.value?.removeLayer(layer)
    }
  })
  
  // 添加新的底图
  let tileUrl = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
  let attribution = '&copy; OpenStreetMap contributors'
  
  if (type === 'satellite') {
    tileUrl = 'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}'
    attribution = '&copy; Esri, Maxar, Earthstar Geographics'
  } else if (type === 'terrain') {
    tileUrl = 'https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png'
    attribution = '&copy; OpenTopoMap contributors'
  }
  
  L.tileLayer(tileUrl, { attribution }).addTo(mapInstance.value)
  
  // 重新添加图层组
  if (locationMarkers.value) mapInstance.value.addLayer(locationMarkers.value)
  if (eventMarkers.value) mapInstance.value.addLayer(eventMarkers.value)
  if (characterMarkers.value) mapInstance.value.addLayer(characterMarkers.value)
  if (itemMarkers.value) mapInstance.value.addLayer(itemMarkers.value)
  if (connectionLines.value) mapInstance.value.addLayer(connectionLines.value)
}

const zoomIn = () => {
  if (mapInstance.value) {
    mapInstance.value.zoomIn()
    zoomLevel.value = mapInstance.value.getZoom()
  }
}

const zoomOut = () => {
  if (mapInstance.value) {
    mapInstance.value.zoomOut()
    zoomLevel.value = mapInstance.value.getZoom()
  }
}

const fitBounds = () => {
  if (mapInstance.value && locations.value.length > 0) {
    const bounds = L.latLngBounds([])
    
    locations.value.forEach(location => {
      if (location.coordinates) {
        const lat = location.coordinates.lat || location.coordinates.y || 30
        const lng = location.coordinates.lng || location.coordinates.x || 120
        bounds.extend([lat, lng])
      }
    })
    
    if (bounds.isValid()) {
      mapInstance.value.fitBounds(bounds)
    }
  }
}

const toggleLegend = () => {
  showLegend.value = !showLegend.value
}

const editLocation = (location: any) => {
  router.push(`/locations?edit=${location.id}`)
}

const editEvent = (event: any) => {
  router.push(`/events?edit=${event.id}`)
}

const editCharacter = (character: any) => {
  router.push(`/characters?edit=${character.id}`)
}

const viewDetails = (data: any) => {
  // 根据类型跳转到详情页
  if (data.id && data.name) {
    // 这里可以根据类型跳转到不同页面
    ElMessage.info('查看详情功能待实现')
  }
}

// 监听显示图层变化
watch(visibleLayers, () => {
  renderMapFeatures()
}, { deep: true })

// 生命周期
onMounted(() => {
  initMap()
  refreshData()
})

onUnmounted(() => {
  if (mapInstance.value) {
    mapInstance.value.remove()
    mapInstance.value = null
  }
})
</script>

<style scoped>
.world-map-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.page-header {
  background: #fff;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.page-header-content h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.page-header-content p {
  margin: 5px 0 0;
  color: #909399;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.control-panel {
  width: 320px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
  padding: 20px;
}

.map-area {
  flex: 1;
  position: relative;
  overflow: hidden;
}

#world-map {
  width: 100%;
  height: 100%;
  min-height: 500px;
}

.panel-section {
  margin-bottom: 24px;
}

.panel-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #303133;
}

.legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 50%;
}

.legend-color.location {
  background-color: #409EFF;
}

.legend-color.event {
  background-color: #F56C6C;
}

.legend-color.character {
  background-color: #67C23A;
}

.legend-color.item {
  background-color: #E6A23C;
}

.legend-line {
  width: 20px;
  height: 2px;
  background-color: #409EFF;
}

.map-toolbar {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  padding: 8px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.coordinate-display {
  font-size: 12px;
  color: #606266;
  padding: 4px 8px;
  background: #f5f7fa;
  border-radius: 2px;
}

.info-panel {
  position: absolute;
  bottom: 20px;
  left: 20px;
  width: 300px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #409EFF;
  color: white;
}

.info-header h4 {
  margin: 0;
  font-size: 16px;
}

.info-content {
  padding: 16px;
}

.info-content p {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.info-actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

/* Leaflet 标记样式 */
:deep(.location-icon) {
  background: transparent;
  border: none;
}

:deep(.event-icon) {
  background: transparent;
  border: none;
}

:deep(.character-icon) {
  background: transparent;
  border: none;
}

.location-marker {
  width: 30px;
  height: 30px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.event-marker {
  width: 25px;
  height: 25px;
  background: #F56C6C;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.character-marker {
  width: 25px;
  height: 25px;
  background: #67C23A;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .control-panel {
    width: 280px;
  }
  
  .info-panel {
    width: 250px;
  }
}
</style>