# 基于 Spring Boot AI 的小说生成工具 - 后端

## 项目概述

这是一个基于 Spring Boot 3.x 和 Spring AI 的小说生成工具后端系统，支持从小说大纲生成到多媒体内容（图片、音频、视频）创作的全流程AI辅助创作。

## 技术栈

- **框架**: Spring Boot 3.2.3
- **AI集成**: Spring AI 0.8.1 (OpenAI)
- **数据库**: MySQL 8.0 / PostgreSQL
- **缓存**: Redis
- **对象存储**: MinIO
- **安全**: Spring Security + JWT
- **API文档**: SpringDoc OpenAPI 3.0
- **任务队列**: Spring Async
- **构建工具**: Maven
- **Java版本**: 17

## 快速开始

### 环境要求

1. JDK 17 或更高版本
2. Maven 3.6+
3. MySQL 8.0+ 或 PostgreSQL 14+
4. Redis 6.0+
5. MinIO (可选，用于文件存储)

### 配置文件

复制 `src/main/resources/application.yml.example` 到 `application.yml` 并根据实际情况修改配置：

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

主要配置项：
- 数据库连接
- Redis配置
- OpenAI API密钥
- MinIO配置
- JWT密钥

### 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE novel_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 应用启动时会自动创建表结构（通过 JPA `ddl-auto: update`）

### 运行应用

```bash
# 编译打包
mvn clean package

# 运行
java -jar target/novel-ai-backend-1.0.0.jar

# 或者使用 Maven 直接运行
mvn spring-boot:run
```

应用默认运行在 `http://localhost:8080`

## API文档

启动应用后，访问以下地址查看API文档：
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/api-docs`

## 项目结构

```
src/main/java/com/laoji/novelai/
├── config/          # 配置类
├── controller/      # REST API控制器
├── service/         # 业务逻辑层
├── repository/      # 数据访问层
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── util/            # 工具类
├── task/            # 定时任务
├── client/          # 第三方客户端
├── exception/       # 异常处理
└── handler/         # 全局处理器
```

## 功能模块

### 1. 小说大纲生成
- 基于AI生成小说大纲
- 支持创意描述、风格选择、字数设定
- 生成完整的故事结构、人物设定、章节概要

### 2. 小说资源管理
- 人物、地点、时间线、事件管理
- 实体关系图谱
- 批量导入导出

### 3. 语音生成
- 多语音预设管理
- 语音克隆功能
- TTS服务集成（Azure、ElevenLabs、阿里云）

### 4. 图片生成
- 集成 ComfyUI 工作流
- 支持提示词工程、负面词
- 图片风格迁移与编辑

### 5. 视频生成
- 基于多图生成动画视频
- 运动控制参数调节
- 视频拼接与后处理

### 6. 智能体系统
- 编剧、摄影师等AI智能体
- 工作流编排与协调
- 任务队列与状态管理

### 7. 审核与拼接
- AI自动审核
- 人工审核流程
- 视频片段拼接

## 开发指南

### 添加新的API接口

1. 在 `controller` 包下创建新的控制器类
2. 使用 `@RestController` 和 `@RequestMapping` 注解
3. 注入对应的 Service 类
4. 添加API文档注解 `@Operation`、`@Parameter` 等

### 添加新的数据实体

1. 在 `entity` 包下创建实体类
2. 使用 JPA 注解（`@Entity`、`@Table`、`@Id` 等）
3. 在 `repository` 包下创建 Repository 接口
4. 继承 `JpaRepository` 接口

### 集成新的AI服务

1. 在 `client` 包下创建客户端类
2. 配置相关API密钥和端点
3. 在 Service 层调用客户端
4. 添加错误处理和重试机制

## 部署说明

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/novel-ai-backend-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 生产环境配置

1. 使用环境变量替代配置文件中的敏感信息
2. 配置数据库连接池参数
3. 启用HTTPS
4. 配置CORS允许的前端域名
5. 设置合适的JWT过期时间

## 常见问题

### Q: 应用启动时报数据库连接错误
A: 检查 `application.yml` 中的数据库配置，确保数据库服务已启动且网络可达。

### Q: OpenAI API调用失败
A: 检查 `spring.ai.openai.api-key` 配置，确保API密钥有效且有足够的额度。

### Q: 文件上传失败
A: 检查MinIO服务配置，确保存储桶存在且有读写权限。

## 许可证

本项目仅供学习和研究使用，请遵守相关法律法规和AI服务提供商的使用条款。

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码变更
4. 发起 Pull Request