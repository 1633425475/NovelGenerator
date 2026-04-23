# 基于 Spring Boot AI 的小说生成工具 API 接口列表

## 概述

本文档列出了小说生成工具的所有 API 接口，共 **4 个 Controller 模块**，**约 117 个 RESTful 接口**。

---

## 1. 小说大纲管理 (OutlineController)

**基路径**: `/api/novel/outline`

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 1 | POST | `/generate` | 生成小说大纲（支持同步/异步） |
| 2 | GET | `/async-result/{taskId}` | 获取异步任务结果 |
| 3 | GET | `/{outlineId}` | 获取大纲详情 |
| 4 | GET | `/list` | 获取用户大纲列表 |
| 5 | DELETE | `/{outlineId}` | 删除大纲（逻辑删除） |
| 6 | PUT | `/{outlineId}` | 更新大纲（创建新版本） |
| 7 | GET | `/{outlineId}/versions` | 获取大纲版本历史 |

### 接口详情

#### 1. 生成小说大纲
```http
POST /api/novel/outline/generate
Content-Type: application/json

{
  "concept": "一部玄幻小说",
  "genre": "FANTASY",
  "style": "TRADITIONAL",
  "async": false
}
```

#### 2. 获取异步任务结果
```http
GET /api/novel/outline/async-result/{taskId}
```

---

## 2. 资源管理 (ResourceController)

**基路径**: `/api/novels/{novelId}/resources`

### 2.1 人物资源管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 1 | POST | `/characters` | 创建人物 |
| 2 | PUT | `/characters/{characterId}` | 更新人物 |
| 3 | GET | `/characters/{characterId}` | 获取人物详情 |
| 4 | GET | `/characters` | 获取人物列表 |
| 5 | DELETE | `/characters/{characterId}` | 删除人物 |
| 6 | POST | `/characters/{characterId}/enhance` | AI 增强人物 |

### 2.2 地点资源管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 7 | POST | `/locations` | 创建地点 |
| 8 | PUT | `/locations/{locationId}` | 更新地点 |
| 9 | GET | `/locations/{locationId}` | 获取地点详情 |
| 10 | GET | `/locations` | 获取地点列表 |
| 11 | DELETE | `/locations/{locationId}` | 删除地点 |

### 2.3 时间线资源管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 12 | POST | `/timelines` | 创建时间线 |
| 13 | PUT | `/timelines/{timelineId}` | 更新时间线 |
| 14 | GET | `/timelines/{timelineId}` | 获取时间线详情 |
| 15 | GET | `/timelines` | 获取时间线列表 |
| 16 | DELETE | `/timelines/{timelineId}` | 删除时间线 |

### 2.4 事件资源管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 17 | POST | `/events` | 创建事件 |
| 18 | PUT | `/events/{eventId}` | 更新事件 |
| 19 | GET | `/events/{eventId}` | 获取事件详情 |
| 20 | GET | `/events` | 获取事件列表 |
| 21 | DELETE | `/events/{eventId}` | 删除事件 |

### 2.5 物品资源管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 22 | POST | `/items` | 创建物品 |
| 23 | PUT | `/items/{itemId}` | 更新物品 |
| 24 | GET | `/items/{itemId}` | 获取物品详情 |
| 25 | GET | `/items` | 获取物品列表 |
| 26 | DELETE | `/items/{itemId}` | 删除物品 |

### 2.6 批量操作

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 27 | POST | `/characters/batch` | 批量创建人物 |
| 28 | POST | `/locations/batch` | 批量创建地点 |

### 2.7 其他功能

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 29 | GET | `/relationship-graph` | 获取关系图谱 |
| 30 | GET | `/search` | 搜索资源 |
| 31 | GET | `/statistics` | 获取资源统计 |

---

## 3. 音频生成与管理 (AudioController)

**基路径**: `/api/novels/{novelId}/audio`

### 3.1 音频生成

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 1 | POST | `/generate` | 生成音频 |
| 2 | POST | `/generate/batch` | 批量生成音频 |
| 3 | GET | `/tasks/{taskId}` | 获取生成任务状态 |
| 4 | DELETE | `/tasks/{taskId}` | 取消生成任务 |
| 5 | POST | `/tasks/{taskId}/retry` | 重试生成任务 |

### 3.2 语音预设管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 6 | POST | `/voice-presets` | 创建语音预设 |
| 7 | PUT | `/voice-presets/{voicePresetId}` | 更新语音预设 |
| 8 | GET | `/voice-presets/{voicePresetId}` | 获取语音预设详情 |
| 9 | DELETE | `/voice-presets/{voicePresetId}` | 删除语音预设 |
| 10 | GET | `/voice-presets` | 获取语音预设列表 |
| 11 | GET | `/voice-presets/search` | 搜索语音预设 |
| 12 | POST | `/voice-presets/{voicePresetId}/set-default` | 设置默认语音预设 |
| 13 | GET | `/voice-presets/default` | 获取默认语音预设 |
| 14 | POST | `/voice-presets/{voicePresetId}/test` | 测试语音预设 |
| 15 | POST | `/voice-presets/{voicePresetId}/clone` | 克隆语音预设 |
| 16 | GET | `/voice-cloning/{taskId}` | 获取语音克隆状态 |

### 3.3 音频资源管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 17 | GET | `/resources/{audioId}` | 获取音频资源详情 |
| 18 | DELETE | `/resources/{audioId}` | 删除音频资源 |
| 19 | GET | `/resources` | 获取音频资源列表 |
| 20 | GET | `/resources/search` | 搜索音频资源 |
| 21 | PUT | `/resources/{audioId}` | 更新音频资源信息 |
| 22 | POST | `/resources/{audioId}/play` | 播放音频 |
| 23 | GET | `/resources/{audioId}/download` | 下载音频 |
| 24 | GET | `/resources/{audioId}/waveform` | 获取音频波形数据 |
| 25 | GET | `/resources/{audioId}/spectrum` | 获取音频频谱数据 |

### 3.4 音频处理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 26 | POST | `/process/merge` | 合并音频 |
| 27 | POST | `/process/{audioId}/trim` | 剪辑音频 |
| 28 | POST | `/process/{audioId}/adjust-volume` | 调整音频音量 |
| 29 | POST | `/process/{audioId}/add-sound-effect` | 添加音效 |
| 30 | POST | `/process/{audioId}/add-background-music` | 添加背景音乐 |
| 31 | POST | `/process/{audioId}/extract-subtitle` | 提取音频字幕 |
| 32 | POST | `/process/{audioId}/translate-subtitle` | 翻译音频字幕 |
| 33 | GET | `/process/{audioId}/analyze-quality` | 分析音频质量 |
| 34 | POST | `/process/{audioId}/enhance-quality` | 优化音频质量 |
| 35 | POST | `/process/{audioId}/convert-format` | 转换音频格式 |

### 3.5 批量操作

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 36 | DELETE | `/batch/delete` | 批量删除音频资源 |
| 37 | PUT | `/batch/update-tags` | 批量更新音频资源标签 |
| 38 | PUT | `/batch/update-status` | 批量更新音频资源状态 |
| 39 | POST | `/batch/review` | 批量审核音频资源 |
| 40 | POST | `/batch/export` | 批量导出音频资源 |
| 41 | POST | `/batch/import` | 批量导入音频资源 |

### 3.6 统计与分析

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 42 | GET | `/statistics` | 获取音频统计信息 |
| 43 | GET | `/statistics/usage` | 获取使用统计 |
| 44 | GET | `/statistics/cost` | 获取成本统计 |
| 45 | GET | `/statistics/quality` | 获取质量统计 |
| 46 | GET | `/statistics/provider` | 获取提供商统计 |
| 47 | GET | `/analysis/trend` | 获取趋势分析 |
| 48 | GET | `/analysis/hotspot` | 获取热点分析 |
| 49 | GET | `/analysis/recommendation` | 获取推荐分析 |

### 3.7 系统管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 50 | GET | `/system/tts-status` | 获取 TTS 服务状态 |
| 51 | POST | `/system/switch-provider` | 切换 TTS 服务提供商 |
| 52 | POST | `/system/test-connection` | 测试 TTS 服务连接 |
| 53 | GET | `/system/configuration` | 获取系统配置 |
| 54 | PUT | `/system/configuration` | 更新系统配置 |
| 55 | POST | `/system/cleanup-cache` | 清理过期缓存 |
| 56 | POST | `/system/cleanup-temp` | 清理临时文件 |
| 57 | POST | `/system/backup` | 备份音频数据 |
| 58 | POST | `/system/restore` | 恢复音频数据 |
| 59 | GET | `/system/health` | 系统健康检查 |

---

## 4. 智能体角色管理 (AgentController)

**基路径**: `/api/novels/{novelId}/agents`

### 4.1 智能体管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 1 | POST | `/` | 创建智能体 |
| 2 | GET | `/{agentId}` | 获取智能体详情 |
| 3 | PUT | `/{agentId}` | 更新智能体 |
| 4 | DELETE | `/{agentId}` | 删除智能体 |
| 5 | GET | `/` | 获取智能体列表 |

### 4.2 智能体对话

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 6 | POST | `/{agentId}/messages` | 发送消息 |
| 7 | POST | `/{agentId}/activate` | 激活智能体 |
| 8 | POST | `/{agentId}/deactivate` | 停用智能体 |

### 4.3 批量操作

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 9 | POST | `/batch/delete` | 批量删除智能体 |
| 10 | POST | `/batch/update` | 批量更新智能体 |

### 4.4 会话管理

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 11 | GET | `/{agentId}/conversations` | 获取会话历史 |
| 12 | POST | `/{agentId}/conversations` | 创建新会话 |
| 13 | DELETE | `/{agentId}/conversations/{sessionId}` | 结束会话 |

### 4.5 统计分析

| 序号 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 14 | GET | `/{agentId}/statistics` | 获取智能体统计 |
| 15 | GET | `/{agentId}/capabilities` | 获取智能体能力 |

---

## 接口统计汇总

| 模块 | 接口数量 |
|------|----------|
| 小说大纲管理 | 7 |
| 资源管理 | 31 |
| 音频生成与管理 | 59 |
| 智能体角色管理 | 15 |
| **总计** | **112** |

---

## 通用响应格式

所有接口的响应格式统一为：

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

### 404 响应
```json
{
  "code": 404,
  "message": "资源不存在",
  "data": null
}
```

---

## 下一步

请查看 `API_TESTS.md` 文件了解单元测试的编写示例和完整测试代码。