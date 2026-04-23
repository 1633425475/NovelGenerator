# 基于 Vue 3 的小说生成工具 - 前端

## 项目概述

这是基于 Spring Boot AI 的小说生成工具的前端界面，采用 Vue 3 + TypeScript + Element Plus 构建，提供从小说创作到多媒体生成的全流程可视化操作界面。

## 技术栈

- **框架**: Vue 3.4 + Composition API
- **构建工具**: Vite 5.0
- **语言**: TypeScript 5.3
- **UI组件库**: Element Plus 2.4
- **路由**: Vue Router 4
- **状态管理**: Pinia 2
- **HTTP客户端**: Axios
- **图表**: ECharts 5
- **拖拽**: Vue Draggable 4
- **代码规范**: ESLint + Prettier

## 快速开始

### 环境要求

1. Node.js 18.0+ 
2. npm 9.0+ 或 yarn 1.22+

### 安装依赖

```bash
# 进入项目目录
cd frontend

# 安装依赖
npm install
# 或使用 yarn
yarn install
```

### 开发环境运行

```bash
# 启动开发服务器
npm run dev
# 或
yarn dev
```

开发服务器运行在 `http://localhost:5173`，支持热重载。

### 构建生产版本

```bash
# 构建生产版本
npm run build
# 或
yarn build
```

构建产物位于 `dist` 目录。

### 预览生产版本

```bash
# 预览生产版本
npm run preview
# 或
yarn preview
```


# 1. 运行所有测试
mvn test

# 2. 运行特定测试类
mvn test -Dtest=OutlineServiceImplTest

# 3. 运行特定测试方法
mvn test -Dtest=OutlineServiceImplTest#generateOutline_WhenCacheHit_ShouldReturnCachedOutline

# 4. 生成测试报告
mvn surefire-report:report

## 项目结构

```
src/
├── assets/           # 静态资源（图片、字体等）
├── components/       # 公共组件
├── views/           # 页面组件
│   ├── outline/     # 大纲生成页面
│   ├── character/   # 人物管理页面
│   ├── audio/       # 语音生成页面
│   ├── image/       # 图片生成页面
│   ├── video/       # 视频生成页面
│   └── error/       # 错误页面
├── router/          # 路由配置
├── store/           # Pinia 状态管理
├── api/             # API 接口封装
├── utils/           # 工具函数
├── types/           # TypeScript 类型定义
├── styles/          # 全局样式
└── main.ts         # 应用入口
```

## 功能模块

### 1. 登录与仪表板
- 用户登录认证
- 系统仪表板展示创作统计
- 快捷操作入口

### 2. 小说大纲生成
- 创意描述输入
- 风格与参数选择
- AI生成结果预览与编辑
- 章节时间线展示

### 3. 小说资源管理
- 人物、地点、事件CRUD操作
- 关系图谱可视化
- 批量导入导出
- 高级搜索与筛选

### 4. 语音生成
- 多音色预设选择
- 文本转语音生成
- 音频波形编辑
- 语音克隆功能

### 5. 图片生成
- AI图片生成参数调节
- 参考图上传与风格迁移
- 生成结果画廊
- 图片库分类管理

### 6. 视频生成
- 多图动画视频生成
- 运动参数调节
- 任务队列监控
- 视频预览与下载

### 7. 系统管理
- 用户权限管理
- 系统设置
- 任务监控
- 资源统计

## 开发指南

### 添加新页面

1. 在 `views` 目录下创建页面组件
2. 在 `router/index.ts` 中添加路由配置
3. 使用 `@/` 别名导入组件和工具

### 组件开发规范

1. 使用 `<script setup lang="ts">` 语法
2. 组件使用大驼峰命名（如 `UserAvatar.vue`）
3. 样式使用 `scoped` 和 `lang="scss"`
4. 复杂组件拆分为子组件

### API接口调用

1. 在 `api` 目录下创建对应模块的API文件
2. 使用 Axios 封装请求
3. 统一错误处理
4. 支持请求拦截器和响应拦截器

### 状态管理

1. 使用 Pinia 进行状态管理
2. 每个模块创建独立的 store
3. 避免在组件中直接操作复杂状态

## 配置说明

### 环境变量

创建 `.env` 文件配置环境变量：

```env
# API 基础地址
VITE_API_BASE_URL=http://localhost:8080/api

# 应用标题
VITE_APP_TITLE=小说AI创作平台

# 是否启用调试模式
VITE_DEBUG=true
```

### 代理配置

开发环境下，配置 `vite.config.ts` 中的代理：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: false,
    }
  }
}
```

### Element Plus 按需导入

项目已配置 Element Plus 的自动按需导入，无需手动导入组件：

```typescript
// 自动导入配置已启用，可直接使用组件
<el-button type="primary">按钮</el-button>
```

## 代码规范

### ESLint 配置

项目使用 ESLint 进行代码检查，规则包括：
- Vue 3 推荐规则
- TypeScript 严格模式
- 代码格式化规范

运行检查：
```bash
npm run lint
```

### Git 提交规范

建议使用 Conventional Commits 规范：
- `feat`: 新功能
- `fix`: 修复问题
- `docs`: 文档更新
- `style`: 代码样式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具变动

## 部署说明

### 构建优化

1. 代码分割：Vite 自动处理
2. 图片压缩：使用合适的图片格式和尺寸
3. Gzip 压缩：服务器端启用
4. CDN 加速：静态资源使用 CDN

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /path/to/frontend/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### Docker 部署

```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 常见问题

### Q: 启动时提示端口被占用
A: 修改 `vite.config.ts` 中的 `server.port` 配置或使用其他端口。

### Q: 组件导入提示找不到模块
A: 检查 TypeScript 路径别名配置，确保使用 `@/` 正确导入。

### Q: Element Plus 图标不显示
A: 确保已安装 `@element-plus/icons-vue` 并在使用前正确导入图标组件。

### Q: API 请求跨域问题
A: 开发环境下配置代理，生产环境下配置 Nginx 反向代理或后端 CORS。

## 浏览器兼容性

- Chrome 90+
- Firefox 88+
- Edge 90+
- Safari 14+

## 许可证

本项目仅供学习和研究使用，请遵守相关法律法规。