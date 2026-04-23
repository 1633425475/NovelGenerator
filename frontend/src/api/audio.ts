import request from '@/utils/request'

// ========== 音频生成相关接口 ==========

/**
 * 语音预设DTO
 */
export interface VoicePresetDTO {
  id?: number
  novelId: number
  name: string
  provider: string  // azure, elevenlabs, aliyun, google, custom
  voiceName: string
  language?: string
  gender?: string  // male, female, neutral
  ageGroup?: string  // child, teenager, young, middle, old
  emotion?: string  // neutral, happy, sad, angry, calm, excited
  speakingStyle?: string
  sampleUrl?: string
  tags?: string[]
  isDefault?: boolean
  usageCount?: number
  qualityScore?: number
  parameters?: Record<string, any>
  createdAt?: string
  updatedAt?: string
}

/**
 * 音频资源DTO
 */
export interface AudioResourceDTO {
  id?: number
  novelId: number
  fileName: string
  fileUrl: string
  originalText: string
  voicePresetId?: number
  characterId?: number
  chapterId?: number
  eventId?: number
  format: string  // audio/mp3, audio/wav, audio/ogg
  duration?: number  // 秒
  fileSize?: number  // 字节
  provider: string
  status: string  // PENDING, PROCESSING, COMPLETED, FAILED
  postProcessingStatus?: string
  reviewStatus?: string
  reviewComment?: string
  qualityScore?: number  // 0-100
  playCount?: number
  downloadCount?: number
  isPublic?: boolean
  tags?: string[]
  createdAt?: string
  updatedAt?: string
}

/**
 * 音频生成请求
 */
export interface AudioGenerateRequest {
  novelId: number
  text: string
  voicePresetId: number
  characterId?: number
  chapterId?: number
  eventId?: number
  params?: Record<string, any>
  tags?: string[]
}

/**
 * 音频生成响应
 */
export interface AudioGenerateResponse {
  audioId: number
  fileUrl: string
  duration: number
  fileSize: number
  format: string
  status: string
  generatedAt: string
  voicePresetId: number
  voicePresetName: string
}

/**
 * 脚本项DTO（批量生成用）
 */
export interface ScriptItemDTO {
  text: string
  voicePresetId: number
  characterId?: number
  chapterId?: number
  eventId?: number
  effects?: Record<string, any>
  tags?: string[]
}

/**
 * 批量音频生成请求
 */
export interface BatchAudioGenerateRequest {
  novelId: number
  scriptItems: ScriptItemDTO[]
  params?: Record<string, any>
}

/**
 * 批量音频生成响应
 */
export interface BatchAudioGenerateResponse {
  taskId: string
  totalCount: number
  completedCount: number
  failedCount: number
  audioIds: number[]
  estimatedDuration: number
  generatedAt: string
}

/**
 * TTS任务状态
 */
export interface TtsTaskStatus {
  taskId: string
  status: string  // PENDING, PROCESSING, COMPLETED, FAILED
  progress: number  // 0-1
  message?: string
  audioId?: number
  startTime?: number
  endTime?: number
}

// ========== 音频生成API ==========

/**
 * 生成音频
 */
export const generateAudio = (data: AudioGenerateRequest): Promise<AudioGenerateResponse> => {
  return request.post(`/novels/${data.novelId}/audio/generate`, data)
}

/**
 * 异步生成音频
 */
export const generateAudioAsync = (data: AudioGenerateRequest): Promise<string> => {
  return request.post(`/novels/${data.novelId}/audio/generate/async`, data)
}

/**
 * 批量生成音频
 */
export const batchGenerateAudio = (data: BatchAudioGenerateRequest): Promise<BatchAudioGenerateResponse> => {
  return request.post(`/novels/${data.novelId}/audio/generate/batch`, data)
}

/**
 * 获取生成任务状态
 */
export const getGenerationTaskStatus = (taskId: string): Promise<TtsTaskStatus> => {
  return request.get(`/audio/tasks/${taskId}`)
}

/**
 * 取消生成任务
 */
export const cancelGenerationTask = (taskId: string): Promise<boolean> => {
  return request.post(`/audio/tasks/${taskId}/cancel`)
}

/**
 * 重试生成任务
 */
export const retryGenerationTask = (taskId: string): Promise<boolean> => {
  return request.post(`/audio/tasks/${taskId}/retry`)
}

// ========== 语音预设管理API ==========

/**
 * 创建语音预设
 */
export const createVoicePreset = (novelId: number, data: VoicePresetDTO): Promise<VoicePresetDTO> => {
  return request.post(`/novels/${novelId}/audio/voice-presets`, data)
}

/**
 * 更新语音预设
 */
export const updateVoicePreset = (novelId: number, voicePresetId: number, data: VoicePresetDTO): Promise<VoicePresetDTO> => {
  return request.put(`/novels/${novelId}/audio/voice-presets/${voicePresetId}`, data)
}

/**
 * 获取语音预设
 */
export const getVoicePreset = (novelId: number, voicePresetId: number): Promise<VoicePresetDTO> => {
  return request.get(`/novels/${novelId}/audio/voice-presets/${voicePresetId}`)
}

/**
 * 删除语音预设
 */
export const deleteVoicePreset = (novelId: number, voicePresetId: number): Promise<boolean> => {
  return request.delete(`/novels/${novelId}/audio/voice-presets/${voicePresetId}`)
}

/**
 * 获取小说语音预设列表
 */
export const listVoicePresets = (novelId: number, filters?: Record<string, any>): Promise<VoicePresetDTO[]> => {
  return request.get(`/novels/${novelId}/audio/voice-presets`, { params: filters })
}

/**
 * 搜索语音预设
 */
export const searchVoicePresets = (novelId: number, keyword: string, filters?: Record<string, any>): Promise<VoicePresetDTO[]> => {
  return request.get(`/novels/${novelId}/audio/voice-presets/search`, { 
    params: { keyword, ...filters }
  })
}

/**
 * 设置默认语音预设
 */
export const setDefaultVoicePreset = (novelId: number, voicePresetId: number): Promise<boolean> => {
  return request.post(`/novels/${novelId}/audio/voice-presets/${voicePresetId}/set-default`)
}

/**
 * 获取默认语音预设
 */
export const getDefaultVoicePreset = (novelId: number): Promise<VoicePresetDTO> => {
  return request.get(`/novels/${novelId}/audio/voice-presets/default`)
}

/**
 * 测试语音预设
 */
export const testVoicePreset = (novelId: number, voicePresetId: number, testText: string): Promise<AudioGenerateResponse> => {
  return request.post(`/novels/${novelId}/audio/voice-presets/${voicePresetId}/test`, { testText })
}

/**
 * 克隆语音预设
 */
export const cloneVoicePreset = (novelId: number, sourceVoicePresetId: number, cloneName: string, params?: Record<string, any>): Promise<string> => {
  return request.post(`/novels/${novelId}/audio/voice-presets/${sourceVoicePresetId}/clone`, { cloneName, params })
}

/**
 * 获取语音克隆状态
 */
export const getVoiceCloningStatus = (cloneTaskId: string): Promise<Record<string, any>> => {
  return request.get(`/audio/clone-tasks/${cloneTaskId}`)
}

// ========== 音频资源管理API ==========

/**
 * 获取音频资源
 */
export const getAudioResource = (novelId: number, audioId: number): Promise<AudioResourceDTO> => {
  return request.get(`/novels/${novelId}/audio/resources/${audioId}`)
}

/**
 * 删除音频资源
 */
export const deleteAudioResource = (novelId: number, audioId: number): Promise<boolean> => {
  return request.delete(`/novels/${novelId}/audio/resources/${audioId}`)
}

/**
 * 获取音频资源列表
 */
export const listAudioResources = (novelId: number, filters?: Record<string, any>): Promise<AudioResourceDTO[]> => {
  return request.get(`/novels/${novelId}/audio/resources`, { params: filters })
}

/**
 * 搜索音频资源
 */
export const searchAudioResources = (novelId: number, keyword: string, filters?: Record<string, any>): Promise<AudioResourceDTO[]> => {
  return request.get(`/novels/${novelId}/audio/resources/search`, { 
    params: { keyword, ...filters }
  })
}

/**
 * 更新音频资源
 */
export const updateAudioResource = (novelId: number, audioId: number, data: AudioResourceDTO): Promise<AudioResourceDTO> => {
  return request.put(`/novels/${novelId}/audio/resources/${audioId}`, data)
}

/**
 * 播放音频（返回播放信息）
 */
export const playAudio = (novelId: number, audioId: number): Promise<AudioResourceDTO> => {
  return request.post(`/novels/${novelId}/audio/resources/${audioId}/play`)
}

/**
 * 下载音频（返回下载信息）
 */
export const downloadAudio = (novelId: number, audioId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/resources/${audioId}/download`)
}

/**
 * 获取音频波形数据
 */
export const getAudioWaveform = (novelId: number, audioId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/resources/${audioId}/waveform`)
}

/**
 * 获取音频频谱数据
 */
export const getAudioSpectrum = (novelId: number, audioId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/resources/${audioId}/spectrum`)
}

// ========== 音频处理API ==========

/**
 * 合并音频
 */
export const mergeAudios = (novelId: number, audioIds: number[], params?: Record<string, any>): Promise<AudioResourceDTO> => {
  return request.post(`/novels/${novelId}/audio/merge`, { audioIds, params })
}

/**
 * 剪辑音频
 */
export const trimAudio = (novelId: number, audioId: number, startTime: number, endTime: number): Promise<AudioResourceDTO> => {
  return request.post(`/novels/${novelId}/audio/resources/${audioId}/trim`, { startTime, endTime })
}

/**
 * 调整音频音量
 */
export const adjustAudioVolume = (novelId: number, audioId: number, volume: number): Promise<AudioResourceDTO> => {
  return request.post(`/novels/${novelId}/audio/resources/${audioId}/volume`, { volume })
}

/**
 * 添加音效
 */
export const addSoundEffect = (novelId: number, audioId: number, soundEffect: Record<string, any>): Promise<AudioResourceDTO> => {
  return request.post(`/novels/${novelId}/audio/resources/${audioId}/effects`, soundEffect)
}

/**
 * 添加背景音乐
 */
export const addBackgroundMusic = (novelId: number, audioId: number, backgroundMusic: Record<string, any>): Promise<AudioResourceDTO> => {
  return request.post(`/novels/${novelId}/audio/resources/${audioId}/background-music`, backgroundMusic)
}

/**
 * 提取音频字幕
 */
export const extractSubtitle = (novelId: number, audioId: number, params?: Record<string, any>): Promise<Record<string, any>> => {
  return request.post(`/novels/${novelId}/audio/resources/${audioId}/subtitle/extract`, params)
}

/**
 * 翻译音频字幕
 */
export const translateSubtitle = (novelId: number, audioId: number, targetLanguage: string, params?: Record<string, any>): Promise<Record<string, any>> => {
  return request.post(`/novels/${novelId}/audio/resources/${audioId}/subtitle/translate`, { targetLanguage, params })
}

/**
 * 分析音频质量
 */
export const analyzeAudioQuality = (novelId: number, audioId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/resources/${audioId}/quality/analyze`)
}

/**
 * 优化音频质量
 */
export const enhanceAudioQuality = (novelId: number, audioId: number, params?: Record<string, any>): Promise<AudioResourceDTO> => {
  return request.post(`/novels/${novelId}/audio/resources/${audioId}/quality/enhance`, params)
}

/**
 * 转换音频格式
 */
export const convertAudioFormat = (novelId: number, audioId: number, targetFormat: string, params?: Record<string, any>): Promise<AudioResourceDTO> => {
  return request.post(`/novels/${novelId}/audio/resources/${audioId}/convert`, { targetFormat, params })
}

// ========== 批量操作API ==========

/**
 * 批量删除音频资源
 */
export const batchDeleteAudioResources = (novelId: number, audioIds: number[]): Promise<Record<string, any>> => {
  return request.post(`/novels/${novelId}/audio/batch/delete`, { audioIds })
}

/**
 * 批量更新音频标签
 */
export const batchUpdateAudioTags = (novelId: number, audioIds: number[], tags: string[]): Promise<Record<string, any>> => {
  return request.post(`/novels/${novelId}/audio/batch/tags`, { audioIds, tags })
}

/**
 * 批量更新音频状态
 */
export const batchUpdateAudioStatus = (novelId: number, audioIds: number[], status: string): Promise<Record<string, any>> => {
  return request.post(`/novels/${novelId}/audio/batch/status`, { audioIds, status })
}

/**
 * 批量审核音频资源
 */
export const batchReviewAudioResources = (novelId: number, audioIds: number[], reviewStatus: string, reviewComment?: string): Promise<Record<string, any>> => {
  return request.post(`/novels/${novelId}/audio/batch/review`, { audioIds, reviewStatus, reviewComment })
}

/**
 * 批量导出音频资源
 */
export const batchExportAudioResources = (novelId: number, audioIds: number[], exportParams?: Record<string, any>): Promise<Record<string, any>> => {
  return request.post(`/novels/${novelId}/audio/batch/export`, { audioIds, exportParams })
}

/**
 * 批量导入音频资源
 */
export const batchImportAudioResources = (novelId: number, importData: Record<string, any>[]): Promise<Record<string, any>> => {
  return request.post(`/novels/${novelId}/audio/batch/import`, { importData })
}

// ========== 统计与分析API ==========

/**
 * 获取音频统计信息
 */
export const getAudioStatistics = (novelId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/statistics`)
}

/**
 * 获取使用统计
 */
export const getUsageStatistics = (novelId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/statistics/usage`)
}

/**
 * 获取成本统计
 */
export const getCostStatistics = (novelId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/statistics/cost`)
}

/**
 * 获取质量统计
 */
export const getQualityStatistics = (novelId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/statistics/quality`)
}

/**
 * 获取提供商统计
 */
export const getProviderStatistics = (novelId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/statistics/provider`)
}

/**
 * 获取趋势分析
 */
export const getTrendAnalysis = (novelId: number, timeRange: string): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/statistics/trend`, { params: { timeRange } })
}

/**
 * 获取热点分析
 */
export const getHotspotAnalysis = (novelId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/statistics/hotspot`)
}

/**
 * 获取推荐分析
 */
export const getRecommendationAnalysis = (novelId: number): Promise<Record<string, any>> => {
  return request.get(`/novels/${novelId}/audio/statistics/recommendation`)
}

// ========== 系统管理API ==========

/**
 * 获取TTS服务状态
 */
export const getTtsServiceStatus = (): Promise<Record<string, any>> => {
  return request.get('/audio/system/tts-status')
}

/**
 * 切换TTS服务提供商
 */
export const switchTtsProvider = (provider: string, config?: Record<string, any>): Promise<boolean> => {
  return request.post('/audio/system/switch-provider', { provider, config })
}

/**
 * 测试TTS服务连接
 */
export const testTtsConnection = (provider: string, config?: Record<string, any>): Promise<Record<string, any>> => {
  return request.post('/audio/system/test-connection', { provider, config })
}

/**
 * 获取系统配置
 */
export const getSystemConfiguration = (): Promise<Record<string, any>> => {
  return request.get('/audio/system/configuration')
}

/**
 * 更新系统配置
 */
export const updateSystemConfiguration = (config: Record<string, any>): Promise<boolean> => {
  return request.post('/audio/system/configuration', config)
}

/**
 * 清理过期缓存
 */
export const cleanupExpiredCache = (): Promise<Record<string, any>> => {
  return request.post('/audio/system/cleanup/cache')
}

/**
 * 清理临时文件
 */
export const cleanupTempFiles = (): Promise<Record<string, any>> => {
  return request.post('/audio/system/cleanup/temp-files')
}

/**
 * 备份音频数据
 */
export const backupAudioData = (params?: Record<string, any>): Promise<Record<string, any>> => {
  return request.post('/audio/system/backup', params)
}

/**
 * 恢复音频数据
 */
export const restoreAudioData = (params?: Record<string, any>): Promise<Record<string, any>> => {
  return request.post('/audio/system/restore', params)
}

/**
 * 系统健康检查
 */
export const healthCheck = (): Promise<Record<string, any>> => {
  return request.get('/audio/system/health')
}