import request from '@/utils/request'

// 大纲生成请求接口
export interface OutlineGenerateRequest {
  idea: string
  style: string
  targetWordCount: number
  chapterCount: number
  referenceWorks?: string
  async?: boolean
}

// 章节DTO
export interface ChapterDTO {
  id: number
  chapterNumber: number
  title: string
  summary: string
  estimatedWordCount: number
}

// 主要人物DTO
export interface MainCharacterDTO {
  id: number
  name: string
  gender: string
  age: string
  identity: string
  personality: string
}

// 大纲生成响应接口
export interface OutlineGenerateResponse {
  id: number
  title: string
  idea: string
  style: string
  targetWordCount: number
  chapterCount: number
  worldBuilding: string
  mainStory: string
  status: string
  version: string
  chapters: ChapterDTO[]
  mainCharacters: MainCharacterDTO[]
  createdAt: string
  async?: boolean
  taskId?: string
}

// 简化版大纲信息
export interface SimpleOutline {
  id: number
  title: string
  style: string
  status: string
  version: string
  createdAt: string
  chapterCount: number
  targetWordCount: number
}

// 异步任务响应
export interface AsyncTaskResponse {
  taskId: string
  message: string
}

/**
 * 生成小说大纲
 */
export function generateOutline(data: OutlineGenerateRequest): Promise<OutlineGenerateResponse> {
  return request.post('/novel/outline/generate', data)
}

/**
 * 获取异步任务结果
 */
export function getAsyncResult(taskId: string): Promise<OutlineGenerateResponse> {
  return request.get(`/novel/outline/async-result/${taskId}`)
}

/**
 * 获取大纲详情
 */
export function getOutlineDetail(outlineId: number): Promise<OutlineGenerateResponse> {
  return request.get(`/novel/outline/${outlineId}`)
}

/**
 * 获取用户大纲列表
 */
export function getUserOutlines(): Promise<SimpleOutline[]> {
  return request.get('/novel/outline/list')
}

/**
 * 删除大纲
 */
export function deleteOutline(outlineId: number): Promise<void> {
  return request.delete(`/novel/outline/${outlineId}`)
}

/**
 * 更新大纲（创建新版本）
 */
export function updateOutline(outlineId: number, data: OutlineGenerateRequest): Promise<OutlineGenerateResponse> {
  return request.put(`/novel/outline/${outlineId}`, data)
}

/**
 * 获取大纲版本历史
 */
export function getOutlineVersions(outlineId: number): Promise<SimpleOutline[]> {
  return request.get(`/novel/outline/${outlineId}/versions`)
}