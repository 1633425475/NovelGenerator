package com.laoji.novelai.service.novel.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laoji.novelai.dto.novel.OutlineGenerateRequest;
import com.laoji.novelai.dto.novel.OutlineGenerateResponse;
import com.laoji.novelai.entity.novel.Chapter;
import com.laoji.novelai.entity.novel.MainCharacter;
import com.laoji.novelai.entity.novel.NovelOutline;
import com.laoji.novelai.repository.novel.ChapterRepository;
import com.laoji.novelai.repository.novel.MainCharacterRepository;
import com.laoji.novelai.repository.novel.NovelOutlineRepository;
import com.laoji.novelai.service.novel.OutlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.ai.chat.client.ChatClient; // 暂时注释，因为依赖问题
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 小说大纲服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OutlineServiceImpl implements OutlineService {

    // private final ChatClient chatClient; // 暂时注释，因为依赖问题
    private final NovelOutlineRepository outlineRepository;
    private final ChapterRepository chapterRepository;
    private final MainCharacterRepository mainCharacterRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${spring.ai.openai.chat.options.model:gpt-4-turbo-preview}")
    private String model;

    @Value("${ollama.api.url:http://localhost:11434/api}")
    private String ollamaApiUrl;

    @Value("${ollama.model:qwen2.5:7b}")
    private String ollamaModel;

    @Override
    @Transactional
    public OutlineGenerateResponse generateOutline(OutlineGenerateRequest request) {
        log.info("开始生成小说大纲，参数：{}", request);

        // 1. 生成参数哈希
        String paramsHash = generateParamsHash(request);
        log.info("生成的参数哈希：{}", paramsHash);

        // 2. 调用AI生成大纲
        String aiResponse = callAIForOutline(request);
        log.info("AI响应长度：{} 字符", aiResponse.length());
        
        // 3. 解析AI响应
        OutlineData outlineData = parseAIResponse(aiResponse);
        log.info("解析后的大纲数据：标题={}, 章节数={}, 人物数={}", 
                outlineData.getTitle(), 
                outlineData.getChapters() != null ? outlineData.getChapters().size() : 0, 
                outlineData.getCharacters() != null ? outlineData.getCharacters().size() : 0);
        
        // 4. 保存到数据库
        NovelOutline outline = saveOutline(request, outlineData, paramsHash);
        log.info("保存到数据库成功，大纲ID：{}", outline.getId());
        
        // 5. 返回结果
        OutlineGenerateResponse response = convertToResponse(outline);
        log.info("返回大纲生成结果，ID：{}", response.getId());
        return response;
    }

    @Override
    public String generateOutlineAsync(OutlineGenerateRequest request) {
        String taskId = UUID.randomUUID().toString();
        
        // 将任务放入Redis队列
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("request", request);
        taskData.put("status", "PENDING");
        taskData.put("createdAt", LocalDateTime.now());
        
        redisTemplate.opsForValue().set("outline:task:" + taskId, taskData, 1, TimeUnit.HOURS);
        
        // 异步执行任务
        asyncGenerateOutline(taskId, request);
        
        return taskId;
    }

    @Async
    public void asyncGenerateOutline(String taskId, OutlineGenerateRequest request) {
        try {
            // 更新任务状态
            Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get("outline:task:" + taskId);
            taskData.put("status", "PROCESSING");
            taskData.put("startedAt", LocalDateTime.now());
            redisTemplate.opsForValue().set("outline:task:" + taskId, taskData, 1, TimeUnit.HOURS);
            
            // 生成大纲
            OutlineGenerateResponse response = generateOutline(request);
            
            // 保存结果
            taskData.put("status", "COMPLETED");
            taskData.put("completedAt", LocalDateTime.now());
            taskData.put("result", response);
            redisTemplate.opsForValue().set("outline:task:" + taskId, taskData, 1, TimeUnit.HOURS);
            
        } catch (Exception e) {
            log.error("异步生成大纲失败，taskId: {}", taskId, e);
            
            Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get("outline:task:" + taskId);
            taskData.put("status", "FAILED");
            taskData.put("error", e.getMessage());
            taskData.put("failedAt", LocalDateTime.now());
            redisTemplate.opsForValue().set("outline:task:" + taskId, taskData, 1, TimeUnit.HOURS);
        }
    }

    @Override
    public OutlineGenerateResponse getAsyncResult(String taskId) {
        Map<String, Object> taskData = (Map<String, Object>) redisTemplate.opsForValue().get("outline:task:" + taskId);
        if (taskData == null) {
            throw new RuntimeException("任务不存在或已过期");
        }
        
        String status = (String) taskData.get("status");
        if ("COMPLETED".equals(status)) {
            return (OutlineGenerateResponse) taskData.get("result");
        } else if ("PROCESSING".equals(status)) {
            throw new RuntimeException("任务正在处理中");
        } else if ("FAILED".equals(status)) {
            throw new RuntimeException("任务处理失败: " + taskData.get("error"));
        } else {
            throw new RuntimeException("任务状态异常: " + status);
        }
    }

    @Override
    public OutlineGenerateResponse getOutlineDetail(Long outlineId) {
        NovelOutline outline = outlineRepository.findById(outlineId)
                .orElseThrow(() -> new RuntimeException("大纲不存在"));
        return convertToResponse(outline);
    }

    @Override
    public Object getUserOutlines(Long userId) {
        List<NovelOutline> outlines = outlineRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId);
        return outlines.stream().map(this::convertToSimpleResponse).toList();
    }

    @Override
    @Transactional
    public void deleteOutline(Long outlineId) {
        NovelOutline outline = outlineRepository.findById(outlineId)
                .orElseThrow(() -> new RuntimeException("大纲不存在"));
        outline.softDelete();
        outlineRepository.save(outline);
    }

    @Override
    @Transactional
    public OutlineGenerateResponse updateOutline(Long outlineId, OutlineGenerateRequest request) {
        NovelOutline oldOutline = outlineRepository.findById(outlineId)
                .orElseThrow(() -> new RuntimeException("大纲不存在"));
        
        // 创建新版本
        String paramsHash = generateParamsHash(request);
        String aiResponse = callAIForOutline(request);
        OutlineData outlineData = parseAIResponse(aiResponse);
        
        NovelOutline newOutline = saveOutline(request, outlineData, paramsHash);
        newOutline.setParentId(oldOutline.getId());
        newOutline.setVersion(generateNextVersion(oldOutline.getVersion()));
        outlineRepository.save(newOutline);
        
        return convertToResponse(newOutline);
    }

    @Override
    public Object getOutlineVersions(Long outlineId) {
        List<NovelOutline> versions = outlineRepository.findVersionsByParentId(outlineId);
        return versions.stream().map(this::convertToSimpleResponse).toList();
    }

    // ========== 私有方法 ==========

    private String generateParamsHash(OutlineGenerateRequest request) {
        try {
            String input = request.getIdea() + "|" + request.getStyle() + "|" + 
                          request.getTargetWordCount() + "|" + request.getChapterCount();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成参数哈希失败", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String callAIForOutline(OutlineGenerateRequest request) {
        log.info("开始调用AI生成大纲");
        try {
            log.info("调用Ollama API生成大纲，模型：{}", ollamaModel);
            log.info("Ollama API URL: {}", ollamaApiUrl);
            String prompt = buildOutlinePrompt(request);
            log.info("生成的提示词长度：{} 字符", prompt.length());
            log.info("提示词内容：{}", prompt.substring(0, Math.min(100, prompt.length())) + "...");
            
            log.info("开始调用callOllamaAPI方法");
            String aiResponse = callOllamaAPI(prompt);
            log.info("callOllamaAPI方法调用成功，响应长度：{} 字符", aiResponse.length());
            log.info("响应内容：{}", aiResponse.substring(0, Math.min(100, aiResponse.length())) + "...");
            return aiResponse;
        } catch (Exception e) {
            log.error("调用Ollama API失败，使用模拟响应", e);
            // 失败时返回模拟的AI响应
            return """
                {
                    "title": "模拟小说标题",
                    "worldBuilding": "这是一个模拟的世界观设定",
                    "mainStory": "这是一个模拟的故事主线",
                    "chapters": [
                        {"chapterNumber": 1, "title": "第一章", "summary": "第一章内容", "estimatedWordCount": 3000, "keyEvents": "关键事件1", "importantScenes": "重要场景1"},
                        {"chapterNumber": 2, "title": "第二章", "summary": "第二章内容", "estimatedWordCount": 3000, "keyEvents": "关键事件2", "importantScenes": "重要场景2"}
                    ],
                    "characters": [
                        {"name": "主角", "gender": "男", "age": "18", "identity": "学生", "personality": "勇敢", "background": "普通家庭", "goals": "拯救世界", "growthArc": "从平凡到英雄"}
                    ]
                }
                """;
        }
    }

    private String callOllamaAPI(String prompt) throws Exception {
        String url = ollamaApiUrl + "/chat/completions";
        log.info("调用Ollama API，URL: {}", url);
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaModel);
        requestBody.put("stream", false);
        
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        
        requestBody.put("messages", messages);
        
        // 记录请求体
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        log.info("Ollama API请求体长度：{} 字符", requestBodyStr.length());
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            // 发送请求
            log.info("开始发送Ollama API请求");
            Map<String, Object> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class
            ).getBody();
            
            // 记录响应
            String responseStr = objectMapper.writeValueAsString(response);
            log.info("Ollama API响应长度：{} 字符", responseStr.length());
            log.info("Ollama API响应：{}", responseStr);
            
            // 解析响应
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> messageResponse = (Map<String, Object>) choice.get("message");
                    String content = (String) messageResponse.get("content");
                    log.info("Ollama API返回的内容长度：{} 字符", content.length());
                    return content;
                }
            }
            
            throw new RuntimeException("Ollama API响应格式错误");
        } catch (Exception e) {
            log.error("调用Ollama API失败", e);
            throw e;
        }
    }

    private String buildOutlinePrompt(OutlineGenerateRequest request) {
        return String.format("""
            请根据以下参数生成一个小说大纲：
            
            创意描述：%s
            小说风格：%s
            目标字数：%d字
            章节数量：%d章
            
            请按照以下JSON格式返回结果：
            {
              "title": "小说标题",
              "worldBuilding": "世界观设定描述",
              "mainStory": "故事主线描述",
              "characters": [
                {
                  "name": "人物名称",
                  "gender": "性别",
                  "age": "年龄",
                  "identity": "身份/职业",
                  "personality": "性格描述",
                  "background": "背景故事",
                  "goals": "目标与动机",
                  "growthArc": "成长弧线"
                }
              ],
              "chapters": [
                {
                  "chapterNumber": 1,
                  "title": "章节标题",
                  "summary": "章节概要",
                  "estimatedWordCount": 3000,
                  "keyEvents": "关键事件描述",
                  "importantScenes": "重要场景描述"
                }
              ]
            }
            
            注意：请生成完整的%d个章节，每个章节的概要要详细。
            """,
            request.getIdea(), request.getStyle(), request.getTargetWordCount(), 
            request.getChapterCount(), request.getChapterCount());
    }

    private OutlineData parseAIResponse(String aiResponse) {
        try {
            // 提取JSON部分（AI可能会在回答中添加额外文本）
            int jsonStart = aiResponse.indexOf("{");
            int jsonEnd = aiResponse.lastIndexOf("}") + 1;
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                String json = aiResponse.substring(jsonStart, jsonEnd);
                return objectMapper.readValue(json, OutlineData.class);
            }
            throw new RuntimeException("无法解析AI响应中的JSON数据");
        } catch (JsonProcessingException e) {
            log.error("解析AI响应失败：{}", aiResponse, e);
            throw new RuntimeException("解析AI响应失败", e);
        }
    }

    @Transactional
    private NovelOutline saveOutline(OutlineGenerateRequest request, OutlineData outlineData, String paramsHash) {
        // 保存大纲
        NovelOutline outline = new NovelOutline();
        outline.setTitle(outlineData.getTitle());
        outline.setIdea(request.getIdea());
        outline.setStyle(request.getStyle());
        outline.setTargetWordCount(request.getTargetWordCount());
        outline.setChapterCount(request.getChapterCount());
        // 确保worldBuilding是有效的JSON格式
        outline.setWorldBuilding(ensureValidJson(outlineData.getWorldBuilding()));
        // 确保mainStory是有效的JSON格式
        outline.setMainStory(ensureValidJson(outlineData.getMainStory()));
        outline.setStatus("GENERATED");
        outline.setVersion("v1.0");
        outline.setUserId(1L); // TODO: 从当前登录用户获取
        outline.setParamsHash(paramsHash);
        
        NovelOutline savedOutline = outlineRepository.save(outline);
        
        // 保存人物
        if (outlineData.getCharacters() != null) {
            List<MainCharacter> characters = outlineData.getCharacters().stream()
                    .map(charData -> {
                        MainCharacter character = new MainCharacter();
                        character.setName(charData.getName());
                        character.setGender(charData.getGender());
                        character.setAge(charData.getAge());
                        character.setIdentity(charData.getIdentity());
                        character.setPersonality(charData.getPersonality());
                        character.setBackground(charData.getBackground());
                        character.setGoals(charData.getGoals());
                        character.setGrowthArc(charData.getGrowthArc());
                        character.setOutline(savedOutline);
                        return character;
                    })
                    .toList();
            mainCharacterRepository.saveAll(characters);
        }
        
        // 保存章节
        if (outlineData.getChapters() != null) {
            List<Chapter> chapters = new ArrayList<>();
            for (int i = 0; i < outlineData.getChapters().size(); i++) {
                ChapterData chapterData = outlineData.getChapters().get(i);
                Chapter chapter = new Chapter();
                // 如果chapterNumber为null，使用索引+1作为章节号
                chapter.setChapterNumber(chapterData.getChapterNumber() != null ? chapterData.getChapterNumber() : i + 1);
                chapter.setTitle(chapterData.getTitle());
                chapter.setSummary(chapterData.getSummary());
                chapter.setEstimatedWordCount(chapterData.getEstimatedWordCount() != null ? chapterData.getEstimatedWordCount() : 3000);
                chapter.setKeyEvents(chapterData.getKeyEvents());
                chapter.setImportantScenes(chapterData.getImportantScenes());
                chapter.setOutline(savedOutline);
                chapters.add(chapter);
            }
            chapterRepository.saveAll(chapters);
        }
        
        return savedOutline;
    }

    private OutlineGenerateResponse convertToResponse(NovelOutline outline) {
        OutlineGenerateResponse response = new OutlineGenerateResponse();
        response.setId(outline.getId());
        response.setTitle(outline.getTitle());
        response.setIdea(outline.getIdea());
        response.setStyle(outline.getStyle());
        response.setTargetWordCount(outline.getTargetWordCount());
        response.setChapterCount(outline.getChapterCount());
        response.setWorldBuilding(outline.getWorldBuilding());
        response.setMainStory(outline.getMainStory());
        response.setStatus(outline.getStatus());
        response.setVersion(outline.getVersion());
        response.setCreatedAt(outline.getCreatedAt());
        
        // 设置章节
        List<Chapter> chapters = chapterRepository.findByOutlineIdAndDeletedFalseOrderByChapterNumber(outline.getId());
        response.setChapters(chapters.stream()
                .map(chapter -> {
                    OutlineGenerateResponse.ChapterDTO dto = new OutlineGenerateResponse.ChapterDTO();
                    dto.setId(chapter.getId());
                    dto.setChapterNumber(chapter.getChapterNumber());
                    dto.setTitle(chapter.getTitle());
                    dto.setSummary(chapter.getSummary());
                    dto.setEstimatedWordCount(chapter.getEstimatedWordCount());
                    return dto;
                })
                .toList());
        
        // 设置人物
        List<MainCharacter> characters = mainCharacterRepository.findByOutlineIdAndDeletedFalse(outline.getId());
        response.setMainCharacters(characters.stream()
                .map(character -> {
                    OutlineGenerateResponse.MainCharacterDTO dto = new OutlineGenerateResponse.MainCharacterDTO();
                    dto.setId(character.getId());
                    dto.setName(character.getName());
                    dto.setGender(character.getGender());
                    dto.setAge(character.getAge());
                    dto.setIdentity(character.getIdentity());
                    dto.setPersonality(character.getPersonality());
                    return dto;
                })
                .toList());
        
        return response;
    }

    private Object convertToSimpleResponse(NovelOutline outline) {
        Map<String, Object> simple = new HashMap<>();
        simple.put("id", outline.getId());
        simple.put("title", outline.getTitle());
        simple.put("style", outline.getStyle());
        simple.put("status", outline.getStatus());
        simple.put("version", outline.getVersion());
        simple.put("createdAt", outline.getCreatedAt());
        simple.put("chapterCount", outline.getChapterCount());
        simple.put("targetWordCount", outline.getTargetWordCount());
        return simple;
    }

    private String generateNextVersion(String currentVersion) {
        if (currentVersion == null || currentVersion.isEmpty()) {
            return "v1.0";
        }
        
        try {
            String versionNum = currentVersion.substring(1); // 去掉'v'
            String[] parts = versionNum.split("\\.");
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            
            return "v" + major + "." + (minor + 1);
        } catch (Exception e) {
            return "v1.1";
        }
    }

    /**
     * 确保字符串是有效的JSON格式
     */
    private String ensureValidJson(String content) {
        if (content == null || content.isEmpty()) {
            return "{}";
        }
        
        try {
            // 尝试解析JSON
            objectMapper.readTree(content);
            return content;
        } catch (Exception e) {
            // 如果不是有效的JSON，包装成JSON格式
            try {
                Map<String, String> map = new HashMap<>();
                map.put("content", content);
                return objectMapper.writeValueAsString(map);
            } catch (Exception ex) {
                // 如果还是失败，返回空JSON
                return "{}";
            }
        }
    }

    /**
     * AI响应数据类
     */
    @lombok.Data
    static class OutlineData {
        private String title;
        private String worldBuilding;
        private String mainStory;
        private List<CharacterData> characters;
        private List<ChapterData> chapters;
    }

    @lombok.Data
    static class CharacterData {
        private String name;
        private String gender;
        private String age;
        private String identity;
        private String personality;
        private String background;
        private String goals;
        private String growthArc;
    }

    @lombok.Data
    static class ChapterData {
        private Integer chapterNumber;
        private String title;
        private String summary;
        private Integer estimatedWordCount;
        private String keyEvents;
        private String importantScenes;
    }
}