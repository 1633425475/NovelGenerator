# 用户认证系统配置指南

## 概述

本文档说明了如何配置和使用用户认证系统，解决 403 Forbidden 错误。

---

## 已完成的工作

### 1. 后端代码

| 文件 | 功能 |
|------|------|
| [entity/auth/User.java](src/main/java/com/laoji/novelai/entity/auth/User.java) | 用户实体 |
| [entity/auth/Role.java](src/main/java/com/laoji/novelai/entity/auth/Role.java) | 角色实体 |
| [entity/auth/Permission.java](src/main/java/com/laoji/novelai/entity/auth/Permission.java) | 权限实体 |
| [repository/auth/UserRepository.java](src/main/java/com/laoji/novelai/repository/auth/UserRepository.java) | 用户仓库 |
| [repository/auth/RoleRepository.java](src/main/java/com/laoji/novelai/repository/auth/RoleRepository.java) | 角色仓库 |
| [repository/auth/PermissionRepository.java](src/main/java/com/laoji/novelai/repository/auth/PermissionRepository.java) | 权限仓库 |
| [service/auth/UserDetailsServiceImpl.java](src/main/java/com/laoji/novelai/service/auth/UserDetailsServiceImpl.java) | 用户详情服务 |
| [util/JwtUtil.java](src/main/java/com/laoji/novelai/util/JwtUtil.java) | JWT 工具类 |
| [config/JwtAuthenticationFilter.java](src/main/java/com/laoji/novelai/config/JwtAuthenticationFilter.java) | JWT 认证过滤器 |
| [config/SecurityConfig.java](src/main/java/com/laoji/novelai/config/SecurityConfig.java) | 安全配置（已更新） |
| [controller/auth/AuthController.java](src/main/java/com/laoji/novelai/controller/auth/AuthController.java) | 认证控制器 |
| [dto/auth/LoginRequest.java](src/main/java/com/laoji/novelai/dto/auth/LoginRequest.java) | 登录请求 DTO |
| [dto/auth/LoginResponse.java](src/main/java/com/laoji/novelai/dto/auth/LoginResponse.java) | 登录响应 DTO |

### 2. 前端代码

| 文件 | 功能 |
|------|------|
| [api/auth.ts](frontend/src/api/auth.ts) | 认证 API 接口 |
| [views/Login.vue](frontend/src/views/Login.vue) | 登录页面（已更新） |

### 3. 数据库脚本

| 文件 | 功能 |
|------|------|
| [auth_init.sql](auth_init.sql) | 认证系统初始化数据 |

---

## 必须执行的步骤

### 步骤 1：执行数据库初始化脚本

**必须执行！** 这是解决问题的关键。

#### 方法一：使用 MySQL 命令行
```bash
mysql -u root -p < "D:\Workspace\laoji\backend\auth_init.sql"
```

#### 方法二：使用 MySQL Workbench/Navicat
1. 打开数据库管理工具
2. 连接到 `novel_ai` 数据库
3. 打开 `auth_init.sql` 文件
4. 执行整个脚本

**脚本会创建：**
- 28 个权限
- 3 个角色（ADMIN、USER、GUEST）
- 2 个用户（admin、user）

### 步骤 2：重新编译并启动后端

```bash
cd D:\Workspace\laoji\backend

# 停止当前运行的后端（如果在运行）
# 然后重新编译并启动
mvn clean spring-boot:run '-Dmaven.test.skip=true'
```

### 步骤 3：确保前端正在运行

前端应该已经在运行，无需重启。如果没有运行：

```bash
cd D:\Workspace\laoji\frontend
npm run dev
```

---

## 默认登录账号

执行完 `auth_init.sql` 后，您可以使用以下账号登录：

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 管理员 | `admin` | `admin123` | 全部权限 |
| 普通用户 | `user` | `admin123` | 基本操作权限 |

---

## API 接口说明

### 登录接口

**POST** `/api/auth/login`

请求体：
```json
{
  "username": "admin",
  "password": "admin123"
}
```

响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "avatarUrl": null,
    "roles": ["ADMIN"],
    "permissions": ["outline:create", "outline:read", ...]
  }
}
```

### 获取当前用户信息

**GET** `/api/auth/me`

请求头：
```
Authorization: Bearer {token}
```

### 登出

**POST** `/api/auth/logout`

---

## 前端登录流程

1. 用户在登录页面输入用户名和密码（admin / admin123）
2. 点击"登录"按钮或按回车键
3. 前端调用 `/api/auth/login` 接口
4. 后端验证用户名和密码
5. 后端返回 JWT token 和用户信息（包含角色和权限）
6. 前端保存 token 和用户信息到 localStorage
7. 跳转到 dashboard 页面
8. 后续请求在请求头中携带 `Authorization: Bearer {token}`

---

## 角色和权限说明

### 角色

| 角色 | 描述 |
|------|------|
| ADMIN | 系统管理员，拥有所有权限 |
| USER | 普通用户，拥有基本操作权限 |
| GUEST | 访客，只有只读权限 |

### 权限分类

| 模块 | 权限 |
|------|------|
| 大纲 | outline:create, outline:read, outline:update, outline:delete |
| 资源 | resource:create, resource:read, resource:update, resource:delete |
| 音频 | audio:generate, audio:read, audio:update, audio:delete, voice_preset:manage |
| 视频 | video:generate, video:read, video:update, video:delete, video:review |
| 智能体 | agent:create, agent:read, agent:update, agent:delete, agent:chat |
| 系统 | system:config, system:statistics, system:backup |

---

## 常见问题

### Q1: 执行 auth_init.sql 时出现错误？

**A:** 确保：
1. 数据库 `novel_ai` 已存在
2. 您有足够的权限
3. 如果表已存在，可以先删除相关表：
   ```sql
   DROP TABLE IF EXISTS user_role;
   DROP TABLE IF EXISTS role_permission;
   DROP TABLE IF EXISTS user;
   DROP TABLE IF EXISTS role;
   DROP TABLE IF EXISTS permission;
   ```
   然后重新执行脚本。

### Q2: 登录时提示用户名或密码错误？

**A:** 
1. 确认已执行 `auth_init.sql`
2. 确认使用的账号是 `admin` 或 `user`，密码是 `admin123`
3. 查看后端日志获取详细错误信息

### Q3: 登录成功后访问其他接口还是 403？

**A:** 
1. 确认前端请求头中包含 `Authorization: Bearer {token}`
2. 查看浏览器开发者工具的 Network 标签
3. 确认 token 没有过期（默认 24 小时）

### Q4: 如何修改密码？

**A:** 可以直接在数据库中更新：
```sql
-- 密码需要使用 BCrypt 加密
-- 这里提供一个临时方案：使用 admin123 的哈希值
UPDATE user SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E' WHERE username = 'admin';
```

---

## 下一步

1. **立即执行** `auth_init.sql` 数据库脚本
2. 重启后端服务
3. 使用 admin / admin123 登录测试
4. 确认 403 问题已解决

---

## 技术细节

### JWT Token 配置

在 `application.yml` 中可以配置：
```yaml
jwt:
  secret: your-secret-key-here  # JWT 密钥（至少 256 位）
  expiration: 86400000          # 过期时间（毫秒，默认 24 小时）
```

### 密码加密

使用 BCrypt 加密算法，强度为 10。

### Token 过期时间

默认 24 小时（86400000 毫秒），可在配置文件中修改。

---

**总结：必须执行 auth_init.sql 来初始化用户数据，否则无法登录！**