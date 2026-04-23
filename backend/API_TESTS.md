# API 接口单元测试文档

## 概述

本文档提供了基于 Spring Boot AI 的小说生成工具的完整 API 接口单元测试方案。

---

## 已完成的工作

### 1. API 接口整理

已整理 **4 个 Controller 模块**，共 **112 个 RESTful 接口**：

| 模块 | Controller | 接口数量 | 文件位置 |
|------|------------|----------|----------|
| 小说大纲管理 | OutlineController | 7 | [API_INTERFACES.md](API_INTERFACES.md) |
| 资源管理 | ResourceController | 31 | [API_INTERFACES.md](API_INTERFACES.md) |
| 音频生成与管理 | AudioController | 59 | [API_INTERFACES.md](API_INTERFACES.md) |
| 智能体角色管理 | AgentController | 15 | [API_INTERFACES.md](API_INTERFACES.md) |

### 2. 测试文件创建

已创建以下测试文件：

| 文件 | 描述 | 测试用例数量 |
|------|------|--------------|
| [BaseControllerTest.java](src/test/java/com/laoji/novelai/controller/BaseControllerTest.java) | 测试基类，提供通用功能 | - |
| [OutlineControllerTest.java](src/test/java/com/laoji/novelai/controller/novel/OutlineControllerTest.java) | 大纲管理测试 | 5 |
| [ResourceControllerTest.java](src/test/java/com/laoji/novelai/controller/resource/ResourceControllerTest.java) | 资源管理测试 | 13 |
| [AudioControllerTest.java](src/test/java/com/laoji/novelai/controller/audio/AudioControllerTest.java) | 音频管理测试 | 14 |
| [AgentControllerTest.java](src/test/java/com/laoji/novelai/controller/agent/AgentControllerTest.java) | 智能体测试 | 12 |

**总计：** 57 个单元测试用例

---

## 测试技术栈

- **测试框架**：JUnit 5 (Jupiter)
- **Mock 框架**：Spring Boot Test + MockMvc
- **断言库**：AssertJ / Spring MockMvc ResultMatchers
- **JSON 处理**：Jackson ObjectMapper

---

## 运行测试

### 运行所有测试

```bash
cd D:\Workspace\laoji\backend
mvn test
```

### 运行特定测试类

```bash
# 运行大纲管理测试
mvn test -Dtest=OutlineControllerTest

# 运行资源管理测试
mvn test -Dtest=ResourceControllerTest

# 运行音频管理测试
mvn test -Dtest=AudioControllerTest

# 运行智能体测试
mvn test -Dtest=AgentControllerTest
```

### 运行单个测试方法

```bash
mvn test -Dtest=OutlineControllerTest#testGenerateOutline_Success
```

---

## 测试文件说明

### 1. BaseControllerTest（基类）

**文件位置**：[src/test/java/com/laoji/novelai/controller/BaseControllerTest.java](src/test/java/com/laoji/novelai/controller/BaseControllerTest.java)

**功能**：
- 提供 `MockMvc` 和 `ObjectMapper` 的自动注入
- 提供 `asJsonString()` 工具方法，用于对象转 JSON
- 所有 Controller 测试类都继承此类

### 2. OutlineControllerTest（大纲管理测试）

**文件位置**：[src/test/java/com/laoji/novelai/controller/novel/OutlineControllerTest.java](src/test/java/com/laoji/novelai/controller/novel/OutlineControllerTest.java)

**测试用例**：
1. `testGenerateOutline_Success` - 测试生成大纲接口
2. `testGetOutlineDetail_NotFound` - 测试获取不存在的大纲详情
3. `testGetUserOutlines_Success` - 测试获取用户大纲列表
4. `testDeleteOutline_NotExist` - 测试删除不存在的大纲
5. `testGetOutlineVersions_NotExist` - 测试获取不存在的大纲版本历史

### 3. ResourceControllerTest（资源管理测试）

**文件位置**：[src/test/java/com/laoji/novelai/controller/resource/ResourceControllerTest.java](src/test/java/com/laoji/novelai/controller/resource/ResourceControllerTest.java)

**测试用例**：
- **人物资源测试**（6个）：创建、获取、列表、删除、AI 增强、批量创建
- **地点资源测试**（1个）：获取列表
- **时间线资源测试**（1个）：获取列表
- **事件资源测试**（1个）：获取列表（带搜索）
- **物品资源测试**（1个）：获取列表
- **其他功能测试**（3个）：关系图谱、搜索、统计

### 4. AudioControllerTest（音频管理测试）

**文件位置**：[src/test/java/com/laoji/novelai/controller/audio/AudioControllerTest.java](src/test/java/com/laoji/novelai/controller/audio/AudioControllerTest.java)

**测试用例**：
- **音频生成测试**（2个）：生成音频、获取任务状态
- **语音预设测试**（4个）：创建、列表、搜索、获取默认
- **音频资源测试**（2个）：列表、搜索
- **统计与分析测试**（4个）：统计、使用、成本、趋势
- **系统管理测试**（3个）：TTS 状态、配置、健康检查

### 5. AgentControllerTest（智能体测试）

**文件位置**：[src/test/java/com/laoji/novelai/controller/agent/AgentControllerTest.java](src/test/java/com/laoji/novelai/controller/agent/AgentControllerTest.java)

**测试用例**：
- **智能体管理测试**（4个）：创建、获取、列表、删除
- **智能体对话测试**（3个）：发送消息、激活、停用
- **批量操作测试**（2个）：批量删除、批量更新
- **会话管理测试**（3个）：获取历史、创建会话、结束会话
- **统计分析测试**（2个）：获取统计、获取能力

---

## 测试编写规范

### 1. 测试命名规范

```java
@Test
void test[功能]_[场景]() throws Exception {
    // 测试代码
}
```

示例：
- `testGenerateOutline_Success` - 测试生成大纲成功场景
- `testGetOutlineDetail_NotFound` - 测试获取大纲详情（不存在的情况）

### 2. MockMvc 请求示例

```java
// GET 请求
mockMvc.perform(get("/api/path")
                .param("key", "value")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

// POST 请求
mockMvc.perform(post("/api/path")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestObject)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

// PUT 请求
mockMvc.perform(put("/api/path/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestObject)))
        .andExpect(status().isOk());

// DELETE 请求
mockMvc.perform(delete("/api/path/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
```

### 3. 常用断言

```java
// 状态码断言
.andExpect(status().isOk())
.andExpect(status().isNotFound())
.andExpect(status().isBadRequest())

// JSON 路径断言
.andExpect(jsonPath("$.code").value(200))
.andExpect(jsonPath("$.message").exists())
.andExpect(jsonPath("$.data").isNotEmpty())

// 内容类型断言
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
```

---

## 测试覆盖率建议

### 当前覆盖情况

| 模块 | 接口总数 | 测试覆盖数 | 覆盖率 |
|------|----------|------------|--------|
| 小说大纲管理 | 7 | 5 | 71% |
| 资源管理 | 31 | 13 | 42% |
| 音频生成与管理 | 59 | 14 | 24% |
| 智能体角色管理 | 15 | 12 | 80% |
| **总计** | **112** | **44** | **39%** |

### 建议的扩展测试

为了提高测试覆盖率，建议添加以下测试：

1. **资源管理模块**：
   - 地点的 CRUD 完整测试
   - 时间线的 CRUD 完整测试
   - 事件的 CRUD 完整测试
   - 物品的 CRUD 完整测试

2. **音频管理模块**：
   - 语音预设的完整 CRUD 测试
   - 音频资源的完整 CRUD 测试
   - 音频处理功能的测试
   - 批量操作的测试

3. **边界条件测试**：
   - 空值/空字符串测试
   - 超长字符串测试
   - 特殊字符测试
   - 权限/认证测试

4. **性能测试**：
   - 批量操作性能测试
   - 大数据量查询测试

---

## 注意事项

1. **数据库状态**：测试前确保数据库连接正常，建议使用测试数据库
2. **Mock 对象**：对于外部依赖（如 AI 服务、文件存储），建议使用 Mockito 进行 Mock
3. **测试数据清理**：测试后清理测试数据，避免影响其他测试
4. **并发测试**：对于并发场景，需要考虑线程安全问题
5. **测试顺序**：JUnit 5 测试默认不保证执行顺序，避免测试之间的依赖

---

## 下一步

1. 运行现有测试，验证基础功能
2. 根据需要扩展测试覆盖率
3. 添加集成测试和端到端测试
4. 配置 CI/CD 流程，自动化运行测试

---

## 相关文档

- [API 接口列表](API_INTERFACES.md) - 完整的 API 接口文档
- [数据库设计](../database_schema.sql) - 数据库表结构
- [系统设计文档](../基于%20Spring%20Boot%20AI%20的小说生成工具：系统架构与详细功能设计.md) - 完整的系统设计文档