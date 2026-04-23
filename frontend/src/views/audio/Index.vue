<template>
  <div class="audio-container">
    <el-page-header @back="$router.push('/dashboard')" title="返回">
      <template #content>
        <h2>语音生成与管理</h2>
      </template>
    </el-page-header>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>语音生成</span>
          </template>
          <el-form :model="form" label-width="100px">
            <el-form-item label="选择音色">
              <el-select 
                v-model="form.presetId" 
                placeholder="请选择音色预设" 
                style="width: 100%"
                :loading="loadingPresets"
                :disabled="loadingPresets"
              >
                <el-option 
                  v-for="preset in presets" 
                  :key="preset.id" 
                  :label="preset.name" 
                  :value="preset.id" 
                >
                  <span>{{ preset.name }}</span>
                  <el-tag size="small" style="margin-left: 8px">{{ preset.gender || '未知' }}</el-tag>
                  <el-tag size="small" type="info" style="margin-left: 4px">{{ preset.provider }}</el-tag>
                </el-option>
              </el-select>
              <div class="preset-actions">
                <el-button 
                  type="text" 
                  size="small" 
                  :disabled="!form.presetId"
                  @click="form.presetId && testVoicePreset(form.presetId)"
                >
                  试听
                </el-button>
                <el-button type="text" size="small">语音克隆</el-button>
                <el-button type="text" size="small" @click="loadVoicePresets">刷新</el-button>
              </div>
            </el-form-item>
            <el-form-item label="输入文本">
              <el-input
                v-model="form.text"
                type="textarea"
                :rows="6"
                placeholder="请输入要转换为语音的文本..."
              />
              <div class="text-actions">
                <el-button type="text" size="small">从剧本导入</el-button>
                <el-button type="text" size="small">批量导入</el-button>
              </div>
            </el-form-item>
            <el-form-item label="语速">
              <el-slider v-model="form.speed" :min="0.5" :max="2" :step="0.1" show-input />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="generating" @click="generateAudio">
                <el-icon><VideoPlay /></el-icon>
                生成语音
              </el-button>
              <el-button type="success" :disabled="!audioUrl">下载音频</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card class="mt-3">
          <template #header>
            <span>音频波形编辑</span>
          </template>
          <div class="waveform-editor">
            <div class="waveform-placeholder">
              <el-icon size="60"><Headset /></el-icon>
              <p>音频波形编辑器</p>
              <div class="waveform-controls">
                <el-button size="small">播放</el-button>
                <el-button size="small">暂停</el-button>
                <el-button size="small">剪辑</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="preset-card">
          <template #header>
            <span>音色预设库</span>
            <div style="float: right">
              <el-button type="text" size="small" @click="loadVoicePresets" :loading="loadingPresets">
                刷新
              </el-button>
              <el-button type="text" size="small">管理</el-button>
            </div>
          </template>
          <div class="preset-list">
            <div v-for="preset in presets" :key="preset.id" class="preset-item">
              <div class="preset-info">
                <h4>
                  {{ preset.name }}
                  <el-tag v-if="preset.isDefault" size="mini" type="success" style="margin-left: 5px">默认</el-tag>
                </h4>
                <p>{{ preset.description || '暂无描述' }}</p>
                <div class="preset-tags">
                  <el-tag size="small">{{ preset.gender || '未知' }}</el-tag>
                  <el-tag size="small" type="info">{{ preset.provider }}</el-tag>
                  <el-tag v-if="preset.language" size="small" type="warning">{{ preset.language }}</el-tag>
                </div>
              </div>
              <div class="preset-actions">
                <el-button 
                  type="primary" 
                  size="small" 
                  text 
                  @click="preset.id && testVoicePreset(preset.id)"
                >
                  试听
                </el-button>
                <el-button 
                  type="primary" 
                  size="small" 
                  text 
                  @click="preset.id && (form.presetId = preset.id)"
                >
                  使用
                </el-button>
              </div>
            </div>
            <div v-if="presets.length === 0" class="empty-state">
              <p>暂无语音预设</p>
              <el-button type="text" size="small">创建预设</el-button>
            </div>
          </div>
        </el-card>
        
        <el-card class="mt-3">
          <template #header>
            <span>音频文件库</span>
            <div style="float: right">
              <el-button type="text" size="small" @click="loadAudioResources" :loading="loadingAudioResources">
                刷新
              </el-button>
              <el-button type="text" size="small" @click="handleBatchDelete">批量删除</el-button>
              <el-button type="text" size="small" @click="handleBatchExport">批量导出</el-button>
            </div>
          </template>
          <el-table 
            :data="audioResources" 
            border 
            height="300" 
            v-loading="loadingAudioResources"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="fileName" label="文件名" min-width="120">
              <template #default="{ row }">
                <div class="file-name">
                  <el-icon><Headset /></el-icon>
                  <span>{{ row.fileName }}</span>
                  <el-tag v-if="row.status === 'COMPLETED'" size="mini" type="success">完成</el-tag>
                  <el-tag v-else-if="row.status === 'PROCESSING'" size="mini" type="warning">处理中</el-tag>
                  <el-tag v-else-if="row.status === 'FAILED'" size="mini" type="danger">失败</el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="时长" width="80">
              <template #default="{ row }">
                {{ row.duration ? row.duration.toFixed(1) + 's' : '未知' }}
              </template>
            </el-table-column>
            <el-table-column prop="format" label="格式" width="70" />
            <el-table-column prop="qualityScore" label="质量" width="70">
              <template #default="{ row }">
                <el-rate 
                  v-model="row.qualityScore" 
                  disabled 
                  :max="10" 
                  :scores="[1,2,3,4,5,6,7,8,9,10]"
                  show-score 
                  text-color="#ff9900" 
                  score-template="{value}" 
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250" fixed="right">
              <template #default="{ row }">
                <el-button 
                  type="primary" 
                  size="small" 
                  text 
                  :icon="PlayCircle" 
                  @click="row.id && playAudio(row.id)"
                  :disabled="row.status !== 'COMPLETED'"
                  title="播放音频"
                >
                  播放
                </el-button>
                <el-button 
                  type="success" 
                  size="small" 
                  text 
                  :icon="Download" 
                  @click="row.id && downloadAudio(row.id)"
                  :disabled="row.status !== 'COMPLETED'"
                  title="下载音频"
                >
                  下载
                </el-button>
                <el-button 
                  type="warning" 
                  size="small" 
                  text 
                  :icon="Edit" 
                  @click="row.id && handleTrimAudio(row.id)"
                  :disabled="row.status !== 'COMPLETED'"
                  title="剪辑音频"
                >
                  剪辑
                </el-button>
                <el-button 
                  type="info" 
                  size="small" 
                  text 
                  :icon="Setting" 
                  @click="row.id && handleAdjustVolume(row.id)"
                  :disabled="row.status !== 'COMPLETED'"
                  title="调整音量"
                >
                  音量
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  text 
                  :icon="Delete" 
                  @click="row.id && deleteAudio(row.id)"
                  title="删除音频"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="table-footer">
            <span>共 {{ audioResources.length }} 个音频文件</span>
            <div class="audio-actions">
              <el-button size="small" @click="handleMergeAudios">合并选中</el-button>
              <el-button size="small">批量处理</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { VideoPlay, Headset, Delete, Download, PlayCircle, Edit, Setting } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as audioApi from '@/api/audio'

// 状态管理
const generating = ref(false)
const loadingPresets = ref(false)
const loadingAudioResources = ref(false)
const audioUrl = ref('')

// 当前小说ID（从路由参数或全局状态获取，这里暂时使用固定值）
const currentNovelId = ref(1)

// 表单数据
const form = reactive({
  presetId: null as number | null,
  text: '你好，欢迎使用小说AI创作平台的语音生成功能。',
  speed: 1.0,
  characterId: null as number | null,
  chapterId: null as number | null,
  eventId: null as number | null,
  tags: [] as string[]
})

// 语音预设列表
const presets = ref<audioApi.VoicePresetDTO[]>([])

// 音频资源列表
const audioResources = ref<audioApi.AudioResourceDTO[]>([])

// 当前播放的音频
const currentPlayingAudio = ref<number | null>(null)

// 加载语音预设
const loadVoicePresets = async () => {
  loadingPresets.value = true
  try {
    const data = await audioApi.listVoicePresets(currentNovelId.value)
    presets.value = data
    if (data.length > 0 && !form.presetId) {
      form.presetId = data[0].id || null
    }
  } catch (error) {
    ElMessage.error('加载语音预设失败')
  } finally {
    loadingPresets.value = false
  }
}

// 加载音频资源
const loadAudioResources = async () => {
  loadingAudioResources.value = true
  try {
    const data = await audioApi.listAudioResources(currentNovelId.value)
    audioResources.value = data
  } catch (error) {
    ElMessage.error('加载音频资源失败')
  } finally {
    loadingAudioResources.value = false
  }
}

// 生成音频
const generateAudio = async () => {
  if (!form.text.trim()) {
    ElMessage.warning('请输入要生成的文本')
    return
  }
  
  if (!form.presetId) {
    ElMessage.warning('请选择语音预设')
    return
  }
  
  generating.value = true
  try {
    const request: audioApi.AudioGenerateRequest = {
      novelId: currentNovelId.value,
      text: form.text,
      voicePresetId: form.presetId,
      characterId: form.characterId || undefined,
      chapterId: form.chapterId || undefined,
      eventId: form.eventId || undefined,
      params: { speed: form.speed },
      tags: form.tags
    }
    
    const response = await audioApi.generateAudio(request)
    audioUrl.value = response.fileUrl
    
    ElMessage.success(`音频生成成功！时长: ${response.duration}秒`)
    
    // 重新加载音频资源列表
    loadAudioResources()
    
  } catch (error) {
    ElMessage.error('音频生成失败')
  } finally {
    generating.value = false
  }
}

// 播放音频
const playAudio = async (audioId: number) => {
  try {
    const audio = await audioApi.playAudio(currentNovelId.value, audioId)
    currentPlayingAudio.value = audioId
    
    // 这里应该实现实际的音频播放逻辑
    // 暂时使用简单的音频元素播放
    const audioElement = new Audio(audio.fileUrl)
    audioElement.play()
    
    ElMessage.success('开始播放音频')
    
    // 更新播放次数
    await audioApi.updateAudioResource(currentNovelId.value, audioId, {
      ...audio,
      playCount: (audio.playCount || 0) + 1
    })
    
    // 重新加载音频资源列表
    loadAudioResources()
    
  } catch (error) {
    ElMessage.error('播放音频失败')
  }
}

// 下载音频
const downloadAudio = async (audioId: number) => {
  try {
    const result = await audioApi.downloadAudio(currentNovelId.value, audioId)
    
    // 创建下载链接
    const link = document.createElement('a')
    link.href = result.fileUrl || result.downloadUrl || ''
    link.download = result.fileName || `audio-${audioId}.mp3`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('开始下载音频')
    
  } catch (error) {
    ElMessage.error('下载音频失败')
  }
}

// 删除音频
const deleteAudio = async (audioId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个音频吗？删除后将无法恢复。', '确认删除', {
      type: 'warning'
    })
    
    await audioApi.deleteAudioResource(currentNovelId.value, audioId)
    ElMessage.success('删除成功')
    
    // 重新加载音频资源列表
    loadAudioResources()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除音频失败')
    }
  }
}

// 测试语音预设
const testVoicePreset = async (presetId: number) => {
  try {
    const testText = '这是一段测试文本，用于测试语音预设的效果。'
    const response = await audioApi.testVoicePreset(currentNovelId.value, presetId, testText)
    
    const audioElement = new Audio(response.fileUrl)
    audioElement.play()
    
    ElMessage.success('开始试听语音预设')
    
  } catch (error) {
    ElMessage.error('试听失败')
  }
}

// 选中音频ID列表
const selectedAudioIds = ref<number[]>([])

// 处理表格选择变化
const handleSelectionChange = (selection: audioApi.AudioResourceDTO[]) => {
  selectedAudioIds.value = selection.map(item => item.id!).filter(id => id !== undefined)
}

// 批量操作
const handleBatchDelete = async () => {
  if (selectedAudioIds.value.length === 0) {
    ElMessage.warning('请先选择要删除的音频')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedAudioIds.value.length} 个音频吗？删除后将无法恢复。`, '确认删除', {
      type: 'warning'
    })
    
    const result = await audioApi.batchDeleteAudioResources(currentNovelId.value, selectedAudioIds.value)
    ElMessage.success(`删除成功，共删除 ${result.deletedCount || selectedAudioIds.value.length} 个音频`)
    
    // 重新加载音频资源列表
    loadAudioResources()
    selectedAudioIds.value = []
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleBatchExport = async () => {
  if (selectedAudioIds.value.length === 0) {
    ElMessage.warning('请先选择要导出的音频')
    return
  }
  
  try {
    const result = await audioApi.batchExportAudioResources(currentNovelId.value, selectedAudioIds.value, {
      format: 'zip',
      includeMetadata: true
    })
    
    // 创建下载链接
    if (result.downloadUrl) {
      const link = document.createElement('a')
      link.href = result.downloadUrl
      link.download = result.fileName || `audio-export-${Date.now()}.zip`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      ElMessage.success(`开始导出 ${selectedAudioIds.value.length} 个音频`)
    } else {
      ElMessage.warning('导出功能暂不可用')
    }
    
  } catch (error) {
    ElMessage.error('批量导出失败')
  }
}

// 音频处理操作
const handleMergeAudios = async () => {
  if (selectedAudioIds.value.length < 2) {
    ElMessage.warning('请至少选择2个音频进行合并')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要合并选中的 ${selectedAudioIds.value.length} 个音频吗？合并后将生成新的音频文件。`, '确认合并', {
      type: 'warning'
    })
    
    const result = await audioApi.mergeAudios(currentNovelId.value, selectedAudioIds.value, {
      format: 'audio/mp3',
      preserveOriginal: true
    })
    
    ElMessage.success(`音频合并成功！新音频ID: ${result.id}`)
    
    // 重新加载音频资源列表
    loadAudioResources()
    selectedAudioIds.value = []
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('音频合并失败')
    }
  }
}

const handleTrimAudio = async (audioId: number) => {
  try {
    const { value: timeRange } = await ElMessageBox.prompt(
      '请输入剪辑时间范围（格式：开始时间-结束时间，单位：秒）',
      '剪辑音频',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+(\.\d+)?-\d+(\.\d+)?$/,
        inputErrorMessage: '时间范围格式错误，请使用如 10.5-25.3 的格式'
      }
    )
    
    if (timeRange) {
      const [startTime, endTime] = timeRange.split('-').map(Number)
      
      if (startTime >= endTime) {
        ElMessage.error('开始时间必须小于结束时间')
        return
      }
      
      const result = await audioApi.trimAudio(currentNovelId.value, audioId, startTime, endTime)
      ElMessage.success(`音频剪辑成功！新音频ID: ${result.id}`)
      
      // 重新加载音频资源列表
      loadAudioResources()
    }
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('音频剪辑失败')
    }
  }
}

const handleAdjustVolume = async (audioId: number) => {
  try {
    const { value: volume } = await ElMessageBox.prompt(
      '请输入音量调整倍数（0.1-10.0，1.0为原始音量）',
      '调整音量',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^(0\.[1-9]|[1-9](\.\d+)?|10(\.0+)?)$/,
        inputErrorMessage: '音量倍数必须在0.1到10.0之间'
      }
    )
    
    if (volume) {
      const volumeNum = parseFloat(volume)
      
      if (volumeNum < 0.1 || volumeNum > 10) {
        ElMessage.error('音量倍数必须在0.1到10.0之间')
        return
      }
      
      const result = await audioApi.adjustAudioVolume(currentNovelId.value, audioId, volumeNum)
      ElMessage.success(`音量调整成功！新音频ID: ${result.id}`)
      
      // 重新加载音频资源列表
      loadAudioResources()
    }
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('音量调整失败')
    }
  }
}

// 初始化加载
onMounted(() => {
  loadVoicePresets()
  loadAudioResources()
})
</script>

<style scoped lang="scss">
.audio-container {
  padding: 20px;
  height: 100%;
  overflow: auto;

  .preset-actions, .text-actions {
    margin-top: 5px;
    display: flex;
    gap: 10px;
  }

  .waveform-editor {
    height: 200px;
    background-color: #f9f9f9;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;

    .waveform-placeholder {
      text-align: center;
      color: #999;

      p {
        margin: 10px 0;
      }

      .waveform-controls {
        margin-top: 10px;
        display: flex;
        gap: 10px;
        justify-content: center;
      }
    }
  }

  .preset-card {
    .preset-list {
      .preset-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px 0;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .preset-info {
          h4 {
            margin: 0 0 5px;
            font-size: 14px;
          }

          p {
            margin: 0 0 5px;
            font-size: 12px;
            color: #666;
          }

          .preset-tags {
            display: flex;
            gap: 5px;
          }
        }
      }
    }
  }
}
</style>