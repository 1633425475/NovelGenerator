<template>
  <div class="coordinate-picker">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="地图拾取" name="map">
        <div class="map-container">
          <div ref="mapContainer" class="map"></div>
          <div class="map-controls">
            <el-button size="small" @click="useCurrentViewCenter">
              <el-icon><Location /></el-icon> 使用当前视图中心
            </el-button>
            <el-button size="small" @click="clearCoordinates">
              <el-icon><Delete /></el-icon> 清除坐标
            </el-button>
          </div>
          <div class="map-hint">
            <el-icon><InfoFilled /></el-icon>
            点击地图任意位置设置坐标
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="手动输入" name="manual">
        <el-form :model="manualForm" label-width="80px">
          <el-form-item label="纬度">
            <el-input-number
              v-model="manualForm.lat"
              :precision="6"
              :step="0.0001"
              :min="-90"
              :max="90"
              placeholder="例如：39.9042"
            />
            <span class="unit">°</span>
          </el-form-item>
          <el-form-item label="经度">
            <el-input-number
              v-model="manualForm.lng"
              :precision="6"
              :step="0.0001"
              :min="-180"
              :max="180"
              placeholder="例如：116.4074"
            />
            <span class="unit">°</span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="applyManualCoordinates">应用坐标</el-button>
            <el-button @click="clearCoordinates">清除</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

    <div class="current-coordinates" v-if="hasCoordinates">
      <el-divider />
      <div class="coordinates-display">
        <span class="label">当前坐标：</span>
        <span class="value">{{ formattedCoordinates }}</span>
        <el-button size="small" text @click="copyCoordinates">
          <el-icon><CopyDocument /></el-icon> 复制
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Location, Delete, InfoFilled, CopyDocument } from '@element-plus/icons-vue'
import * as L from 'leaflet'
import 'leaflet/dist/leaflet.css'

// Props
interface Props {
  modelValue?: Record<string, any>
  defaultCenter?: [number, number]
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  defaultCenter: () => [30, 120] as [number, number] // 中国中心
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: Record<string, any>]
}>()

// Reactive state
const activeTab = ref('map')
const mapContainer = ref<HTMLElement>()
const mapInstance = ref<L.Map>()
const marker = ref<L.Marker>()
const manualForm = reactive({
  lat: 30,
  lng: 120
})

// Current coordinates
const currentCoordinates = computed(() => {
  return props.modelValue
})

const hasCoordinates = computed(() => {
  return currentCoordinates.value && 
    (currentCoordinates.value.lat !== undefined || 
     currentCoordinates.value.lng !== undefined ||
     currentCoordinates.value.x !== undefined || 
     currentCoordinates.value.y !== undefined)
})

const formattedCoordinates = computed(() => {
  const coords = currentCoordinates.value
  if (!coords) return '未设置'
  
  if (coords.lat !== undefined && coords.lng !== undefined) {
    return `${coords.lat.toFixed(6)}°, ${coords.lng.toFixed(6)}°`
  } else if (coords.x !== undefined && coords.y !== undefined) {
    return `${coords.x.toFixed(2)}, ${coords.y.toFixed(2)}`
  }
  return JSON.stringify(coords)
})

// Initialize map
onMounted(() => {
  if (activeTab.value === 'map') {
    setTimeout(initMap, 100) // Wait for DOM
  }
  
  // Initialize manual form from current coordinates
  if (props.modelValue) {
    manualForm.lat = props.modelValue.lat || props.modelValue.y || 30
    manualForm.lng = props.modelValue.lng || props.modelValue.x || 120
  }
})

onUnmounted(() => {
  if (mapInstance.value) {
    mapInstance.value.remove()
    mapInstance.value = undefined
  }
})

watch(activeTab, (newTab) => {
  if (newTab === 'map' && !mapInstance.value && mapContainer.value) {
    setTimeout(initMap, 100)
  }
})

// Initialize Leaflet map
const initMap = () => {
  if (!mapContainer.value) return
  
  // Create map instance
  mapInstance.value = L.map(mapContainer.value, {
    center: props.defaultCenter,
    zoom: 4,
    zoomControl: false
  })

  // Add tile layer
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(mapInstance.value)

  // Add zoom control
  L.control.zoom({
    position: 'topright'
  }).addTo(mapInstance.value)

  // Add click event
  mapInstance.value.on('click', (e: L.LeafletMouseEvent) => {
    setCoordinatesFromMap(e.latlng.lat, e.latlng.lng)
  })

  // If we have existing coordinates, add marker
  if (hasCoordinates.value) {
    const lat = currentCoordinates.value.lat || currentCoordinates.value.y
    const lng = currentCoordinates.value.lng || currentCoordinates.value.x
    if (lat !== undefined && lng !== undefined) {
      addMarker(lat, lng)
      mapInstance.value.panTo([lat, lng])
    }
  }
}

// Add marker to map
const addMarker = (lat: number, lng: number) => {
  if (!mapInstance.value) return
  
  // Remove existing marker
  if (marker.value) {
    mapInstance.value.removeLayer(marker.value)
  }
  
  // Create new marker
  marker.value = L.marker([lat, lng], {
    draggable: true
  }).addTo(mapInstance.value)
  
  // Add dragend event
  marker.value.on('dragend', (e: L.DragEndEvent) => {
    const marker = e.target as L.Marker
    const position = marker.getLatLng()
    setCoordinatesFromMap(position.lat, position.lng)
  })
  
  // Pan to marker
  mapInstance.value.panTo([lat, lng])
}

// Set coordinates from map click
const setCoordinatesFromMap = (lat: number, lng: number) => {
  const newCoords = {
    lat,
    lng,
    x: lng, // Also store as x/y for compatibility
    y: lat
  }
  
  emit('update:modelValue', newCoords)
  addMarker(lat, lng)
  
  // Update manual form
  manualForm.lat = lat
  manualForm.lng = lng
  
  ElMessage.success(`坐标已设置为: ${lat.toFixed(6)}, ${lng.toFixed(6)}`)
}

// Use current map view center
const useCurrentViewCenter = () => {
  if (!mapInstance.value) return
  
  const center = mapInstance.value.getCenter()
  setCoordinatesFromMap(center.lat, center.lng)
}

// Apply manual coordinates
const applyManualCoordinates = () => {
  const lat = manualForm.lat
  const lng = manualForm.lng
  
  if (lat < -90 || lat > 90) {
    ElMessage.error('纬度必须在 -90 到 90 之间')
    return
  }
  
  if (lng < -180 || lng > 180) {
    ElMessage.error('经度必须在 -180 到 180 之间')
    return
  }
  
  setCoordinatesFromMap(lat, lng)
  
  // Switch to map tab and center on coordinates
  activeTab.value = 'map'
  if (mapInstance.value) {
    mapInstance.value.panTo([lat, lng])
  }
}

// Clear coordinates
const clearCoordinates = () => {
  emit('update:modelValue', {})
  
  if (marker.value && mapInstance.value) {
    mapInstance.value.removeLayer(marker.value)
    marker.value = undefined
  }
  
  ElMessage.info('坐标已清除')
}

// Copy coordinates to clipboard
const copyCoordinates = async () => {
  try {
    await navigator.clipboard.writeText(formattedCoordinates.value)
    ElMessage.success('坐标已复制到剪贴板')
  } catch (err) {
    console.error('复制失败:', err)
    ElMessage.error('复制失败')
  }
}

// Watch for external modelValue changes
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    const lat = newValue.lat || newValue.y
    const lng = newValue.lng || newValue.x
    
    if (lat !== undefined && lng !== undefined) {
      manualForm.lat = lat
      manualForm.lng = lng
      
      if (mapInstance.value && activeTab.value === 'map') {
        addMarker(lat, lng)
      }
    }
  }
}, { deep: true })
</script>

<style scoped lang="scss">
.coordinate-picker {
  .map-container {
    position: relative;
    height: 300px;
    border-radius: 4px;
    overflow: hidden;
    
    .map {
      width: 100%;
      height: 100%;
    }
    
    .map-controls {
      position: absolute;
      top: 10px;
      right: 10px;
      z-index: 1000;
      display: flex;
      flex-direction: column;
      gap: 5px;
      
      .el-button {
        background: white;
        border: 1px solid #dcdfe6;
      }
    }
    
    .map-hint {
      position: absolute;
      bottom: 10px;
      left: 50%;
      transform: translateX(-50%);
      background: rgba(255, 255, 255, 0.9);
      padding: 5px 10px;
      border-radius: 4px;
      font-size: 12px;
      color: #666;
      display: flex;
      align-items: center;
      gap: 5px;
      z-index: 1000;
    }
  }
  
  .el-form-item {
    margin-bottom: 20px;
    
    :deep(.el-input-number) {
      width: 100%;
      
      .el-input__wrapper {
        padding-right: 40px;
      }
    }
    
    .unit {
      margin-left: 5px;
      color: #666;
    }
  }
  
  .current-coordinates {
    margin-top: 20px;
    
    .coordinates-display {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px;
      background-color: #f5f7fa;
      border-radius: 4px;
      
      .label {
        font-weight: bold;
        color: #303133;
      }
      
      .value {
        flex: 1;
        font-family: monospace;
        color: #409EFF;
      }
    }
  }
}
</style>