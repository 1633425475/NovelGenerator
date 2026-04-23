# 基于 Spring Boot AI 的小说生成工具：系统架构与详细功能设计

## 一、总体架构设计

### 1.1 架构图（逻辑分层）

text

```
┌─────────────────────────────────────────────────────────────────┐
│                         前端（Vue/React）                        │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐│
│  │创作工作台│ │资源管理  │ │生成任务  │ │审核面板  │ │系统配置││
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └────────┘│
└─────────────────────────────┬───────────────────────────────────┘
                              │ REST API / WebSocket
┌─────────────────────────────▼───────────────────────────────────┐
│                    后端（Spring Boot + Spring AI）               │
│ ┌─────────────────────────────────────────────────────────────┐│
│ │                      接入层                                 ││
│ │  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐││
│ │  │ Controller   │ │ WebSocket    │ │ 认证/鉴权（Spring    │││
│ │  │ (REST API)   │ │ 通知服务     │ │ Security + JWT）    │││
│ │  └──────────────┘ └──────────────┘ └──────────────────────┘││
│ └─────────────────────────────────────────────────────────────┘│
│ ┌─────────────────────────────────────────────────────────────┐│
│ │                      业务服务层                             ││
│ │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌─────────┐ ││
│ │  │小说创作服务│ │资源管理服务│ │智能体服务  │ │审核服务 │ ││
│ │  │- 大纲生成  │ │- 人物管理  │ │- 编剧Agent │ │- AI初审 │ ││
│ │  │- 章节生成  │ │- 地图管理  │ │- 摄影师   │ │- 人工复审│ ││
│ │  │- 场景拆分  │ │- 时间线    │ │  Agent    │ │- 拼接管理│ ││
│ │  └────────────┘ │- 事件记录  │ │- 审核Agent │ └─────────┘ ││
│ │                 └────────────┘ └────────────┘               ││
│ │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌─────────┐ ││
│ │  │语音生成服务│ │ComfyUI集成 │ │任务调度服务│ │文件存储 │ ││
│ │  │- TTS调用   │ │- 图片生成  │ │- 异步任务  │ │ 服务    │ ││
│ │  │- 预设管理  │ │- 视频生成  │ │- 任务队列  │ │- OSS/本地│ ││
│ │  └────────────┘ └────────────┘ └────────────┘ └─────────┘ ││
│ └─────────────────────────────────────────────────────────────┘│
│ ┌─────────────────────────────────────────────────────────────┐│
│ │                      数据访问层                             ││
│ │  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────┐││
│ │  │ JPA/MyBatis  │ │ Redis缓存    │ │ 文件元数据管理       │││
│ │  └──────────────┘ └──────────────┘ └──────────────────────┘││
│ └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────┐
│                      外部服务与存储                              │
│ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌─────────┐│
│ │ LLM（OpenAI/ │ │ TTS（Azure/  │ │ ComfyUI      │ │ 对象存储││
│ │ 本地模型）   │ │ ElevenLabs） │ │ 服务集群     │ │（MinIO/ ││
│ └──────────────┘ └──────────────┘ └──────────────┘ │  OSS）  ││
│ ┌──────────────┐ ┌──────────────┐                  └─────────┘│
│ │ MySQL/       │ │ Redis        │                              │
│ │ PostgreSQL  │ │              │                              │
│ └──────────────┘ └──────────────┘                              │
└─────────────────────────────────────────────────────────────────┘
```



### 1.2 技术选型建议

| 层级          | 技术栈                                                       |
| :------------ | :----------------------------------------------------------- |
| **前端**      | Vue 3 / React + TypeScript + Element Plus / Ant Design + Axios |
| **后端框架**  | Spring Boot 3.x + Spring AI（集成 LLM）                      |
| **AI 模型**   | OpenAI GPT-4 / 阿里通义千问 / 本地部署（如 Llama 3）         |
| **TTS**       | Azure TTS / ElevenLabs / 阿里云 TTS                          |
| **图像/视频** | ComfyUI（通过 REST API 调用，需部署 ComfyUI 服务）           |
| **数据库**    | MySQL 8.0 / PostgreSQL（存储元数据） + Redis（缓存、任务队列） |
| **文件存储**  | MinIO / 阿里云 OSS / AWS S3                                  |
| **任务调度**  | Spring Async + 线程池 / 消息队列（RabbitMQ/Kafka）可选       |
| **认证授权**  | Spring Security + JWT                                        |
| **API 文档**  | SpringDoc OpenAPI (Swagger)                                  |

------

## 二、详细功能描述

### 2.1 小说大纲生成

### 业务目标

用户输入一句话创意或关键词，系统自动生成结构化的小说大纲，支持多次微调。

### 实现路径

#### 后端设计

**1. 模型与接口**

- `NovelOutline` 实体：存储小说基本信息（标题、简介、风格）和大纲内容（JSON 字符串）。
- `OutlineGenerateRequest`：接收前端参数，包含 `idea`（创意描述）、`style`（风格如玄幻/都市）、`wordCount`（目标字数）等。
- 服务层 `OutlineService`：核心方法 `generateOutline(OutlineGenerateRequest req)`。

**2. Spring AI 集成**

- 引入 Spring AI 依赖，配置 `ChatClient` Bean，指向目标 LLM（如 OpenAI GPT-4）。

- 使用 `Function Calling` 强制返回 JSON 格式。定义一个 Java 类 `OutlineStructure` 表示大纲结构，用 `@JsonClassDescription` 注解描述各字段，LLM 自动填充。

  java

  ```
  @JsonClassDescription("小说大纲结构")
  public record OutlineStructure(
      @JsonPropertyDescription("小说标题") String title,
      @JsonPropertyDescription("世界观设定描述") String worldBuilding,
      @JsonPropertyDescription("主要人物列表，每个包含姓名、简介") List<CharacterBrief> characters,
      @JsonPropertyDescription("故事主线，包含起因、发展、高潮、结局") String mainStory,
      @JsonPropertyDescription("分章列表，每章有标题和概要") List<ChapterOutline> chapters
  ) {}
  ```

  

- Prompt 模板：包含创作要求、输出格式说明。例如：

  text

  ```
  你是一位资深小说编辑，请根据用户创意生成小说大纲。
  创意：{idea}
  风格：{style}
  目标字数：{wordCount}
  请严格按照以下 JSON 格式输出，不要添加额外说明：
  {json_schema}
  ```

  

**3. 交互流程**

- 用户输入创意 → 前端 POST `/api/outline/generate` → 后端调用 LLM → 解析 JSON → 存入数据库 → 返回给前端。
- 前端使用 JSON 渲染表单，允许用户修改并保存。
- 支持“重新生成部分章节”：用户选中某章节，输入修改意见，后端仅调用 LLM 针对该部分生成，替换原有内容。

**4. 代码示例**

java

```
@Service
public class OutlineService {
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public OutlineService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public NovelOutline generateOutline(OutlineGenerateRequest req) {
        String prompt = buildPrompt(req);
        String response = chatClient.prompt(prompt)
                .options(ChatOptions.builder().temperature(0.7).build())
                .call()
                .content();
        OutlineStructure outline = objectMapper.readValue(response, OutlineStructure.class);
        return convertToEntity(outline, req);
    }
}
```


#### 完善与扩展

**错误处理与重试机制**
- LLM 调用可能因网络或服务限流失败，需实现指数退避重试（如 Spring Retry）。
- JSON 解析失败时，捕获 `JsonProcessingException` 并返回友好错误，同时记录原始响应供调试。
- 设置超时时间（如 30 秒），避免前端长时间等待。

**提示词工程优化**
- 根据小说风格动态调整 prompt 模板，例如玄幻小说强调“世界观”、“修炼体系”，都市小说侧重“人物关系”、“社会背景”。
- 支持用户上传参考作品（如类似小说简介），系统提取关键词并注入 prompt，使生成更贴近用户偏好。
- 提供 prompt 模板管理功能，允许管理员添加、测试和发布新模板。

**大纲版本管理**
- 每次用户修改大纲后生成新版本，保留历史记录，支持版本对比与回滚。
- 版本号格式：`v{主版本}.{次版本}`，每次重大重构（如重写主线）递增主版本，微调（如修改章节标题）递增次版本。

**性能优化建议**
- 对相同创意参数（idea hash）缓存生成结果，有效期内直接返回，减少 LLM 调用成本。
- 异步生成：对于长大纲（>50 章），可提交异步任务，通过 WebSocket 推送进度和结果。
- 数据库索引优化：对 `novel_id`、`created_at` 建立索引，加快查询速度。

------

### 2.2 小说人物、地图、时间、事件、资源记录

### 业务目标

提供类似“世界观构建器”的功能，让用户系统化创建和关联小说中的各类元素，并借助 AI 丰富内容。

### 实现路径

#### 数据模型设计

- **人物表** `character`：id, name, alias, gender, age, appearance, personality, background, relationships (JSON 数组，如 `[{"characterId":2, "relation":"师徒"}]`), faction, novel_id
- **地点表** `location`：id, name, type, description, associated_characters (JSON), associated_events (JSON)
- **时间线表** `timeline`：id, time_point, event_description, chapter_id
- **事件表** `event`：id, name, description, trigger, result, characters (JSON), location_id
- **资源表** `resource`：id, type (image/audio/video), url, metadata (JSON), associated_entity (polymorphic: character_id/location_id/event_id)

#### 后端服务

- 为每个实体提供标准 CRUD REST API。
- 添加 AI 增强端点：`POST /api/character/{id}/enhance` 传入补充要求，服务从数据库获取当前描述，调用 LLM 生成更丰富的文本，返回并更新数据库。
- 关系图谱查询：`GET /api/character/relationships/{novelId}` 返回节点和边数据，供前端使用 ECharts 或 G6 绘制。

#### 前端交互

- 使用表单组件（如 Element Plus 的 Form）管理每个实体。
- 人物关系图：拖拽式画布，可添加/删除关系。
- 时间线甘特图：使用 `vis-timeline` 展示事件与章节的对应。

#### 实现细节

- AI 增强 Prompt 示例：

  text

  ```
  你是一位角色设定专家，请根据以下角色基础信息，扩展其外貌、性格和背景故事：
  姓名：{name}
  已有描述：{existingDesc}
  扩展要求：{userRequirement}
  输出格式：用 Markdown 段落描述，不要包含标题。
  ```

  

- 返回后前端展示预览，用户确认后保存。

#### 完善与扩展

**数据验证与一致性检查**
- 对 JSON 字段（如 relationships、associated_characters）进行 Schema 验证，确保格式正确。
- 建立实体间的引用完整性约束，例如删除人物时，自动清理相关事件中的关联。
- 提供一致性检查工具，检测孤立实体（如未关联任何章节的地点）并提示用户。

**批量导入导出功能**
- 支持从 CSV/Excel 导入人物、地点列表，模板可下载。
- 导出功能：将整个世界观数据打包为 JSON 文件，包含所有实体及关系，便于备份或迁移。
- 批量 AI 增强：选中多个实体，一次性调用 LLM 进行描述扩展，提升效率。

**高级搜索与过滤**
- 全文搜索：对人物描述、地点详情等文本字段建立 Elasticsearch 索引，支持关键词模糊匹配。
- 组合过滤：例如“查找所有年龄 >20 且属于‘正道’阵营的女性角色”。
- 历史版本对比：记录每次修改的 diff，支持查看任意两个版本间的差异。

**关系图谱增强**
- 图谱支持力导向布局、缩放到画布、节点拖拽固定。
- 点击节点显示详情面板，并可快速编辑关联关系。
- 提供图谱分析功能：自动识别核心人物（连接度最高）、社区发现（将关系紧密的节点聚类）。

------

### 2.3 各种语音预设生成

### 业务目标

为不同角色和旁白提供多种音色选择，支持文本转语音并管理音频资源。

### 实现路径

#### TTS 服务抽象

- 定义接口 `TtsService`：

  java

  ```
  public interface TtsService {
      TtsResult synthesize(String text, VoicePreset preset);
  }
  ```

  

- 实现类如 `AzureTtsService`、`ElevenLabsTtsService`，通过配置选择。

- 语音预设实体 `VoicePreset`：id, name, description, provider, provider_params (JSON, 如 `{"voiceName":"zh-CN-XiaoxiaoNeural","style":"cheerful"}`)

#### 生成流程

1. 前端调用 `POST /api/audio/generate`，参数：text（文本）、presetId。
2. 后端根据 presetId 获取对应 TTS 实现，调用 `synthesize` 方法。
3. 返回的音频二进制流上传到 OSS，生成访问 URL，并将元数据存入 `resource` 表。
4. 返回前端音频播放 URL。

#### 批量配音

- 服务方法 `generateBatch(Script script)`：脚本结构包含多个条目（角色+文本），后端自动匹配预设（角色映射表），并行调用 TTS，返回所有音频链接。

#### 音频同步与剪辑

- 生成的音频文件需记录时长，前端可进行波形显示，供后续视频合成时对齐时间轴。
- 可提供时间轴编辑工具，调整音频片段顺序和起始时间。

#### 完善与扩展

**语音预设管理增强**
- 预设分类：按性别、年龄、情绪（欢快、悲伤、愤怒）等标签分类，方便快速筛选。
- 试听功能：每个预设提供示例音频片段，用户可试听后再选择。
- 预设分享：支持将自定义预设导出为 JSON 文件，供团队其他成员导入使用。

**语音克隆功能**
- 支持用户上传 1-2 分钟干净人声样本，调用 ElevenLabs 或类似服务的语音克隆 API，生成专属音色。
- 克隆后的音色可作为新预设保存，关联到特定角色，实现高度定制化配音。
- 克隆过程需明确提示用户隐私政策，样本音频在生成后立即删除。

**音频后处理**
- 集成音频处理库（如 FFmpeg），提供降噪、音量均衡、添加背景音乐等后处理选项。
- 支持音频剪辑：用户可在波形图上选择区间，删除或裁剪片段。
- 多轨道混合：允许同时加载多个音频（如对话、背景音乐、音效），调整各轨道音量和淡入淡出效果。

**多提供商负载均衡**
- 配置多个 TTS 提供商（Azure、ElevenLabs、阿里云），根据当前配额、延迟自动选择最优服务。
- 设置费用限制：每月预算上限，达到阈值后自动切换至更经济的提供商。
- 故障转移：当某个提供商服务不可用时，自动切换到备用提供商，保证生成任务不中断。

------

### 2.4 连接 ComfyUI 生成图片

### 业务目标

通过 ComfyUI 工作流生成高质量漫画/插画图片，支持单张和批量。

### 实现路径

#### ComfyUI 集成封装

- 定义 `ComfyUIClient`，负责 HTTP 通信：

  java

  ```
  public class ComfyUIClient {
      private final RestTemplate restTemplate;
      private final String baseUrl;
  
      public String submitWorkflow(Map<String, Object> workflow) {
          // 提交工作流，返回 prompt_id
      }
  
      public String getImage(String promptId, int index) {
          // 轮询获取生成的图片 URL
      }
  }
  ```

  

#### 工作流模板管理

- 系统预设多种画风的工作流 JSON（如写实、日漫、水墨），存储在数据库 `workflow_template` 表中。
- 前端选择模板后，用户填写提示词等参数，后端将参数注入工作流模板，生成最终的 workflow JSON。

#### 生成图片流程

1. 前端提交生成请求：`{ "prompt": "...", "negativePrompt": "...", "width": 512, "height": 512, "model": "sd_xl", "templateId": 1 }`
2. 后端从模板表取出 workflow JSON，替换其中的提示词占位符。
3. 调用 ComfyUIClient.submitWorkflow，获得 prompt_id。
4. 开启异步线程轮询结果（或使用 WebSocket 推送进度）。
5. 图片生成后，下载到 OSS，记录元数据到 `resource` 表，关联当前场景或角色。
6. 前端展示图片，用户可标记为角色立绘、场景图等。

#### 批量生成

- 支持上传 Excel 或从剧本自动提取场景描述，批量提交多个生成任务。
- 使用消息队列（如 Redis List）管理任务队列，避免瞬时过载。

#### 完善与扩展

**工作流版本管理**
- 工作流模板支持版本控制，每次修改生成新版本，旧版本仍可被历史任务引用。
- 提供版本对比功能，可视化查看 JSON 差异。
- 允许回滚到任一历史版本，确保生成效果的一致性。

**图片风格迁移与编辑**
- 集成风格迁移模型（如 Stable Diffusion 的 Style Transfer），允许用户上传参考图，将相同风格应用到生成图片。
- 内置简易图片编辑器：裁剪、调整亮度/对比度、添加文字标签（如角色名称）。
- 支持图片修复（inpainting）：用户涂抹图片中不满意的区域，系统调用 ComfyUI 的 inpainting 工作流进行局部重绘。

**生成失败处理与重试**
- 监控 ComfyUI 节点状态，若节点崩溃自动重启或切换到备用实例。
- 任务失败后自动分析原因（如提示词冲突、内存不足），调整参数后重试（最多 3 次）。
- 提供失败任务诊断面板，展示错误日志和中间输出，方便管理员排查。

**图片资产管理**
- 为每张图片生成缩略图和多尺寸版本（大图、中图、小图），适应不同展示场景。
- 图片标签系统：自动通过 CLIP 模型识别图片内容（如“城堡”、“骑士”），支持按标签搜索。
- 重复检测：计算图片感知哈希，避免同一场景生成重复图片，节省存储空间。

------

### 2.5 连接 ComfyUI 基于多图和文本生成视频

### 业务目标

将多张图片（关键帧）和文本描述输入 ComfyUI 工作流，生成短视频片段。

### 实现路径

#### 工作流选择

- ComfyUI 的图生视频常用工作流：AnimateDiff + ControlNet、SVD（Stable Video Diffusion）、Mochi 等。
- 系统需预先配置好这些工作流，并对外暴露接口。

#### 后端实现

- 接口 `POST /api/video/generate` 接收参数：

  json

  ```
  {
    "images": ["url1", "url2", ...],
    "prompt": "女孩在花园中奔跑",
    "duration": 5,
    "fps": 24,
    "workflowTemplateId": 2
  }
  ```

  

- 服务层：根据模板 ID 获取工作流 JSON，替换其中图片 URL 和 prompt 字段（注意图片需要先下载到 ComfyUI 可访问的路径，或使用 base64 传入）。

- 调用 ComfyUI 提交任务，轮询结果。生成视频文件后存储至 OSS。

- 前端可预览生成的视频片段，并关联到剧本的某个场景。

#### 性能与可靠性

- 视频生成耗时较长，采用异步任务 + 进度推送（WebSocket）。
- 若 ComfyUI 服务繁忙，使用任务队列排队，避免请求丢失。

#### 完善与扩展

**视频参数优化**
- 自动参数调优：根据输入图片数量和内容，自动推荐 duration、fps 等参数，以达到最佳视觉效果。
- 运动控制增强：支持指定运动轨迹（如镜头平移、缩放），通过 ControlNet 参数注入工作流，实现更精细的动画控制。
- 分辨率自适应：根据用户订阅等级自动调整输出视频分辨率（如 480p、720p、1080p），平衡质量与生成速度。

**关键帧提取算法**
- 智能关键帧选择：对输入图片序列，通过视觉差异度分析自动挑选关键帧，避免冗余。
- 关键帧插值：若关键帧数量不足，可使用插值算法（如 FILM）生成中间帧，使运动更平滑。
- 关键帧编辑：允许用户手动调整关键帧顺序、替换或删除不满意的帧。

**视频质量评估**
- 内置质量评估模型：对生成的视频片段进行自动评分（基于清晰度、运动连贯性、艺术风格一致性）。
- 异常检测：识别常见缺陷（如画面闪烁、物体变形），并标记出问题的时间段。
- A/B 测试：同一场景使用不同工作流生成多个版本，让用户对比选择最佳效果。

**视频片段管理**
- 片段元数据丰富：记录每个片段的时长、分辨率、生成参数、关联场景 ID。
- 片段剪辑工具：提供简单的时间轴编辑器，允许用户裁剪片段头尾、调整播放速度。
- 片段版本管理：同一场景可保存多个生成版本，方便后续切换或混合使用。

------

### 2.6 创建编剧、摄影师等智能体角色

### 业务目标

实现多智能体协作，自动将小说章节转换为分镜脚本、生成图片/视频参数，并调用相应服务。

### 实现路径

#### 智能体设计模式

- 每个智能体是一个独立的服务类，内部包含一个 `ChatClient` 实例，并注入工具调用（Function Calling）。
- 智能体之间通过事件驱动或服务编排进行协作。

#### 核心智能体

**1. 编剧 Agent**

- 职责：将小说章节拆分为分镜脚本。

- 输入：章节文本 + 人物设定 + 地点设定。

- 输出：`Script` 对象，包含 `shots` 列表，每个 `Shot` 有：镜头编号、场景描述、对话/旁白、时长（秒）、关键元素。

- 实现方式：

  java

  ```
  @Component
  public class ScreenwriterAgent {
      private final ChatClient chatClient;
  
      public Script generateScript(Chapter chapter, NovelContext context) {
          String prompt = buildPrompt(chapter, context);
          // 使用 Function Calling 返回 Script 对象
          return chatClient.prompt(prompt)
              .functions("getCharacterInfo", "getLocationInfo") // 可调用函数获取实时信息
              .call()
              .entity(Script.class);
      }
  }
  ```

  

**2. 摄影师 Agent**

- 职责：为每个镜头生成图片生成所需的提示词和参数。
- 输入：`Shot` 对象。
- 输出：`ImageGenerationParams`（提示词、负向提示词、模型、尺寸等）。
- 实现：调用 LLM 将镜头描述转化为专业绘图提示词，结合角色立绘参考图（可从资源库检索）生成更精准的描述。

**3. 配音师 Agent**

- 职责：为角色匹配音色，生成配音音频。
- 输入：`Script` 对象。
- 输出：每个对话的音频文件 URL 及时间轴。
- 实现：内部调用 TTS 服务。

**4. 剪辑师 Agent**

- 职责：将生成的图片序列、音频按顺序合成视频。
- 输入：`Script` 对象 + 图片 URL 列表 + 音频 URL 列表。
- 输出：最终视频文件 URL。
- 实现：调用 FFmpeg 命令行或 Java 库。

#### 智能体协作流程

- 用户在前端选择一章，点击“智能生成视频”。
- 后端触发一个工作流：
  1. 编剧 Agent 生成脚本（保存到数据库）。
  2. 摄影师 Agent 为每个镜头生成图片参数，调用图片生成服务（可能并发），得到图片 URL。
  3. 配音师 Agent 生成所有音频。
  4. 剪辑师 Agent 合成视频。
- 整个过程状态通过 WebSocket 推送，前端展示进度条。

#### 技术要点

- 使用 Spring AI 的 `@Tool` 注解定义可调用的函数，让 LLM 自主决定调用时机。
- 智能体之间的状态传递可使用 `AgentContext` 对象，包含小说设定、已生成的资源等。

#### 完善与扩展

**智能体状态管理**
- 每个智能体维护一个状态机，记录当前任务进度、成功/失败历史。
- 提供智能体监控面板，实时查看各智能体的 CPU/内存使用率、最近任务执行时间。
- 支持暂停/恢复智能体：管理员可临时停止某个智能体，而不影响其他智能体运行。

**智能体间通信协议**
- 定义标准化消息格式（JSON Schema），包含 sender、receiver、message_type、payload。
- 支持同步调用（RPC）和异步事件（消息队列），根据交互场景选择。
- 消息历史持久化，便于调试和审计智能体间的协作过程。

**智能体能力评估**
- 定期对每个智能体进行能力测试：给定标准输入，评估输出质量（如脚本合理性、提示词准确性）。
- 收集用户反馈（如“生成的图片不符合预期”），关联到具体智能体，用于改进 prompt 或工具集。
- 能力排行榜：展示各智能体在各项任务上的平均评分，激励优化。

**智能体学习机制**
- 支持在线学习：智能体可从成功案例中提取有效模式，动态调整自身 prompt 或参数。
- 知识库增强：为智能体配备 RAG（检索增强生成）能力，可从小说设定库、优秀作品库中检索相关信息，提升生成质量。
- 联邦学习：多个用户实例的智能体可共享 anonymized 学习成果，共同进化。

------

### 2.7 智能体角色进行视频生成完成后的一次审核，审核通过后再由人工审核后进行完整拼接

### 业务目标

建立 AI 初审 + 人工复审的双重质量保障机制，确保最终视频质量。

### 实现路径

#### AI 初审

- 创建审核 Agent，负责评估视频片段质量。
- 输入：视频片段 URL、对应的分镜脚本。
- 输出：评分（0-100）、通过/不通过、问题描述（如“画面中角色手部变形”）。
- 审核 Agent 可调用图像分析模型（如 CLIP 或 GPT-4V）检测异常，也可结合规则（如检查音频时长是否匹配视频时长）。
- 若通过，状态设为 `AI_PASSED`；否则状态设为 `AI_REJECTED` 并触发重新生成（可设置重试次数）。

**代码示例（审核 Agent）**：

java

```
@Component
public class ReviewerAgent {
    private final ChatClient visionClient; // 支持多模态的 LLM

    public ReviewResult review(VideoSegment segment, Script script) {
        String prompt = buildReviewPrompt(segment, script);
        // 传入视频截图（关键帧）和音频波形图等
        return visionClient.prompt(prompt)
            .user(u -> u.text("请检查视频质量").media(MimeTypeUtils.IMAGE_PNG, segment.getSnapshot()))
            .call()
            .entity(ReviewResult.class);
    }
}
```



#### 人工复审

- 前端提供待审核列表，展示 AI 初审通过的视频片段。
- 管理员点击播放，若满意则点击“通过”，状态变为 `HUMAN_PASSED`；若不满意，可填写修改意见并“驳回”，状态变为 `HUMAN_REJECTED`。
- 驳回后可触发重新生成（修改部分参数），或允许用户手动调整后重新提交。

#### 完整拼接

- 当所有片段（按章节顺序）均通过人工审核后，用户可触发最终合成。

- 后端服务 `VideoMerger` 负责：

  1. 从数据库获取所有 `HUMAN_PASSED` 的片段，按 `scene_order` 排序。

  2. 使用 FFmpeg 进行拼接：

     bash

     ```
     ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
     ```

     

  3. 可选添加背景音乐、字幕（根据音频生成字幕文件）。

  4. 输出最终视频文件，存储至 OSS，记录到 `final_video` 表。

- 前端展示最终视频预览，提供下载和分享链接。

#### 状态机设计

- 视频片段状态流转：

  text

  ```
  PENDING_AI → (AI 审核) → AI_PASSED / AI_REJECTED
  AI_PASSED → (人工审核) → HUMAN_PASSED / HUMAN_REJECTED
  HUMAN_REJECTED → (重新生成) → PENDING_AI
  HUMAN_PASSED → (待拼接) → MERGED
  ```

#### 完善与扩展

**审核标准定义**
- 制定可量化的审核标准，包括画面清晰度、运动流畅度、角色一致性、音频同步误差等。
- 支持自定义审核规则：管理员可添加新规则（如“禁止出现暴力画面”），AI 审核时会额外检查。
- 标准版本化：审核标准随业务发展迭代，旧视频片段仍按原有标准评估，保证公平性。

**多维度评分体系**
- 从技术、艺术、叙事三个维度分别评分（各 0-100），加权计算总分。
- 技术维度：检查编码质量、帧率稳定性、音频噪音等。
- 艺术维度：评估构图、色彩、风格一致性等。
- 叙事维度：判断镜头是否准确传达剧本意图，情感表达是否到位。

**审核历史与追溯**
- 记录每次审核的详细日志：审核者（AI/人工）、时间、评分、意见、最终决定。
- 提供追溯视图：点击任意视频片段，可查看其历经的所有审核记录及状态流转。
- 统计分析：生成审核报告，展示通过率、平均审核时长、常见驳回原因等，帮助优化流程。

**拼接后处理增强**
- 智能转场：根据前后场景内容自动添加合适的转场效果（淡入淡出、滑动、溶解）。
- 字幕生成：利用音频 ASR（自动语音识别）生成字幕文件，并支持手动校正。
- 背景音乐库：内置多种情绪的背景音乐，用户可选择并自动匹配视频节奏。
- 最终输出格式多样化：支持 MP4、MOV、GIF 等多种格式，并适配不同平台（抖音、B 站）的编码规范。

------

## 三、数据库设计（关键表结构）

### 3.1 小说相关表

- `novel`：小说基本信息（名称、作者、简介、状态）
- `outline`：大纲内容（JSON 格式存储）
- `chapter`：章节信息（标题、内容、状态）

### 3.2 资源管理表

- `character`：人物（姓名、描述、属性 JSON）
- `location`：地点（名称、描述、关联小说）
- `timeline`：时间线节点（时间点、事件描述）
- `event`：事件（名称、描述、关联人物/地点/章节）
- `resource`：资源文件（类型、URL、元数据）

### 3.3 生成任务表

- `generation_task`：任务记录（类型：image/video/audio，状态，参数 JSON，结果 URL，关联对象 ID）
- `task_log`：任务执行日志

### 3.4 智能体与审核表

- `agent_session`：智能体对话会话（用于保持上下文）
- `review_record`：审核记录（审核类型、对象 ID、审核者（AI/人工）、结果、意见）

### 3.5 视频拼接表

- `video_segment`：视频片段（关联章节、顺序、文件 URL、时长、审核状态）
- `final_video`：最终合成视频（文件 URL、状态）

------

## 四、关键技术难点与解决方案

| 难点                   | 解决方案                                                     |
| :--------------------- | :----------------------------------------------------------- |
| **ComfyUI 集成稳定性** | 封装 API 客户端，使用重试机制；异步任务队列，避免长时间阻塞；支持多 ComfyUI 实例负载均衡。 |
| **长文本生成一致性**   | 使用 Spring AI 的 `ChatMemory` 维护上下文；通过向量数据库存储设定，检索增强生成（RAG）。 |
| **智能体协作复杂度**   | 采用工作流编排引擎（如 Camunda）或状态机，定义智能体执行顺序；使用事件驱动解耦。 |
| **视频生成耗时**       | 前端显示进度轮询；支持断点续传；生成失败自动重试。           |
| **多模态数据关联**     | 统一使用资源表存储所有生成物，通过外键关联到具体业务对象（章节、场景）。 |

------

## 五、部署与扩展性考虑

- **容器化**：使用 Docker 打包 Spring Boot 应用，ComfyUI 服务单独容器化。
- **弹性伸缩**：视频生成任务可水平扩展 ComfyUI 节点，使用 Redis 队列分发任务。
- **存储**：使用对象存储（MinIO/OSS）分离文件与数据库，便于 CDN 加速。
- **安全**：API 限流（Spring Cloud Gateway）、敏感词过滤、用户隔离（每个用户的数据相互独立）。

------

## 六、总结

本系统以 Spring Boot AI 为核心，整合了 LLM、TTS、ComfyUI 等生成式 AI 能力，通过智能体角色模拟创作团队，实现了从小说大纲到视频成品的全自动化流程，同时保留了人工审核环节确保质量。前后端分离的架构保证了良好的扩展性和用户体验，适用于个人创作者、内容工作室等场景。