package com.laoji.novelai.service.novel;

import com.laoji.novelai.dto.novel.OutlineGenerateRequest;
import com.laoji.novelai.dto.novel.OutlineGenerateResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
public class OllamaApiTest {

    @Autowired
    private OutlineService outlineService;

    @Value("${ollama.api.url:http://localhost:11434/api}")
    private String ollamaApiUrl;

    @Value("${ollama.model:qwen2.5:7b}")
    private String ollamaModel;

    @Test
    public void testOllamaConnection() {
        log.info("测试 Ollama API 连接...");
        log.info("Ollama API URL: {}", ollamaApiUrl);
        log.info("Ollama Model: {}", ollamaModel);

        RestTemplate restTemplate = new RestTemplate();

        String url = ollamaApiUrl + "/chat";
        log.info("发送请求到: {}", url);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaModel);
        requestBody.put("stream", false);

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "你好，请回复'连接成功'");
        messages.add(message);

        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class
            );

            log.info("响应状态码: {}", response.getStatusCode());
            assertEquals(HttpStatus.OK, response.getStatusCode());

            Map<String, Object> body = response.getBody();
            assertNotNull(body, "响应体不应为空");

            if (body != null && body.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                assertFalse(choices.isEmpty(), "choices 不应为空");

                Map<String, Object> choice = choices.get(0);
                Map<String, Object> messageResponse = (Map<String, Object>) choice.get("message");
                String content = (String) messageResponse.get("content");

                log.info("Ollama 响应内容: {}", content);
                assertNotNull(content, "响应内容不应为空");
                assertTrue(content.length() > 0, "响应内容不应为空字符串");

                log.info("✅ Ollama API 连接测试成功！");
            } else {
                log.warn("响应格式不符合预期: {}", body);
            }
        } catch (Exception e) {
            log.error("❌ Ollama API 连接失败: {}", e.getMessage());
            fail("Ollama API 连接失败: " + e.getMessage());
        }
    }

    @Test
    public void testGenerateOutlineWithOllama() {
        log.info("测试使用 Ollama API 生成小说大纲...");

        OutlineGenerateRequest request = new OutlineGenerateRequest();
        request.setIdea("一个平凡的大学生意外获得了能够看到未来的能力，他发现自己的未来与一个神秘的组织有关");
        request.setStyle("玄幻");
        request.setTargetWordCount(50000);
        request.setChapterCount(10);

        try {
            OutlineGenerateResponse response = outlineService.generateOutline(request);

            log.info("大纲生成成功!");
            log.info("标题: {}", response.getTitle());
            log.info("世界观: {}", response.getWorldBuilding());
            log.info("故事主线: {}", response.getMainStory());
            log.info("章节数: {}", response.getChapters() != null ? response.getChapters().size() : 0);
            log.info("人物数: {}", response.getMainCharacters() != null ? response.getMainCharacters().size() : 0);

            assertNotNull(response, "响应不应为空");
            assertNotNull(response.getTitle(), "标题不应为空");

            log.info("✅ 大纲生成测试成功！");
        } catch (Exception e) {
            log.error("❌ 大纲生成失败: {}", e.getMessage());
            fail("大纲生成失败: " + e.getMessage());
        }
    }

    @Test
    public void testOllamaModels() {
        log.info("测试获取 Ollama 可用模型...");

        RestTemplate restTemplate = new RestTemplate();

        try {
            String tagsUrl = ollamaApiUrl.replace("/api", "") + "/api/tags";
            log.info("获取模型列表 from: {}", tagsUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(tagsUrl, Map.class);

            log.info("响应状态码: {}", response.getStatusCode());
            assertEquals(HttpStatus.OK, response.getStatusCode());

            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("models")) {
                List<Map<String, Object>> models = (List<Map<String, Object>>) body.get("models");
                log.info("可用模型数量: {}", models.size());

                for (Map<String, Object> model : models) {
                    log.info("  - 模型: {}", model.get("name"));
                }

                assertNotNull(models, "模型列表不应为空");
                log.info("✅ 获取模型列表测试成功！");
            } else {
                log.warn("响应格式不符合预期: {}", body);
            }
        } catch (Exception e) {
            log.error("❌ 获取模型列表失败: {}", e.getMessage());
            fail("获取模型列表失败: " + e.getMessage());
        }
    }
}
