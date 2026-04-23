package com.laoji.novelai.service.comfyui;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ComfyUI客户端
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ComfyUIClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private String baseUrl = "http://localhost:8188";

    /**
     * 设置ComfyUI服务地址
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 提交工作流
     */
    public String submitWorkflow(Map<String, Object> workflow) throws Exception {
        log.info("提交ComfyUI工作流");

        String url = baseUrl + "/prompt";
        int maxRetries = 3;
        int retryCount = 0;
        long backoffTime = 1000; // 1秒

        while (retryCount < maxRetries) {
            try {
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // 构建请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("prompt", workflow);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                // 发送请求
                Map<String, Object> response = restTemplate.exchange(
                        url, HttpMethod.POST, entity, Map.class
                ).getBody();

                // 解析响应
                if (response != null && response.containsKey("prompt_id")) {
                    String promptId = (String) response.get("prompt_id");
                    log.info("工作流提交成功，prompt_id: {}", promptId);
                    return promptId;
                }

                throw new Exception("ComfyUI工作流提交失败: " + response);
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("工作流提交失败，已达到最大重试次数", e);
                    throw e;
                }
                log.warn("工作流提交失败，正在重试 ({}/{})...", retryCount, maxRetries, e);
                Thread.sleep(backoffTime);
                backoffTime *= 2; // 指数退避
            }
        }

        throw new Exception("工作流提交失败，已达到最大重试次数");
    }

    /**
     * 获取生成的图片
     */
    public String getImage(String promptId, int index) throws Exception {
        log.info("获取生成的图片，prompt_id: {}, index: {}", promptId, index);

        // 轮询获取结果
        String url = baseUrl + "/history/" + promptId;
        int maxRetries = 30;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                if (response != null && response.containsKey(promptId)) {
                    Map<String, Object> historyItem = (Map<String, Object>) response.get(promptId);
                    if (historyItem.containsKey("outputs")) {
                        Map<String, Object> outputs = (Map<String, Object>) historyItem.get("outputs");
                        // 查找图像输出
                        for (Map.Entry<String, Object> entry : outputs.entrySet()) {
                            Map<String, Object> output = (Map<String, Object>) entry.getValue();
                            if (output.containsKey("images")) {
                                java.util.List<Map<String, Object>> images = (java.util.List<Map<String, Object>>) output.get("images");
                                if (index < images.size()) {
                                    Map<String, Object> image = images.get(index);
                                    String filename = (String) image.get("filename");
                                    log.info("图片生成成功，文件名: {}", filename);
                                    return baseUrl + "/view?filename=" + filename;
                                }
                            }
                        }
                    }
                }

                // 等待一段时间后重试
                TimeUnit.SECONDS.sleep(2);
                retryCount++;
            } catch (Exception e) {
                log.error("获取图片失败", e);
                TimeUnit.SECONDS.sleep(2);
                retryCount++;
            }
        }

        throw new Exception("图片生成超时");
    }

    /**
     * 获取ComfyUI服务状态
     */
    public Map<String, Object> getStatus() throws Exception {
        log.info("获取ComfyUI服务状态");

        String url = baseUrl + "/status";
        int maxRetries = 3;
        int retryCount = 0;
        long backoffTime = 1000; // 1秒

        while (retryCount < maxRetries) {
            try {
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                log.info("ComfyUI服务状态: {}", response);
                return response;
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("获取服务状态失败，已达到最大重试次数", e);
                    throw e;
                }
                log.warn("获取服务状态失败，正在重试 ({}/{})...", retryCount, maxRetries, e);
                Thread.sleep(backoffTime);
                backoffTime *= 2; // 指数退避
            }
        }

        throw new Exception("获取服务状态失败，已达到最大重试次数");
    }

    /**
     * 获取工作流模板列表
     */
    public Map<String, Object> getWorkflows() throws Exception {
        log.info("获取工作流模板列表");

        String url = baseUrl + "/workflows";
        int maxRetries = 3;
        int retryCount = 0;
        long backoffTime = 1000; // 1秒

        while (retryCount < maxRetries) {
            try {
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                log.info("工作流模板列表: {}", response);
                return response;
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("获取工作流模板列表失败，已达到最大重试次数", e);
                    throw e;
                }
                log.warn("获取工作流模板列表失败，正在重试 ({}/{})...", retryCount, maxRetries, e);
                Thread.sleep(backoffTime);
                backoffTime *= 2; // 指数退避
            }
        }

        throw new Exception("获取工作流模板列表失败，已达到最大重试次数");
    }

    /**
     * 取消任务
     */
    public void cancelTask(String promptId) throws Exception {
        log.info("取消任务，prompt_id: {}", promptId);

        String url = baseUrl + "/cancel/" + promptId;
        int maxRetries = 3;
        int retryCount = 0;
        long backoffTime = 1000; // 1秒

        while (retryCount < maxRetries) {
            try {
                restTemplate.postForObject(url, null, Map.class);
                log.info("任务取消成功");
                return;
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("取消任务失败，已达到最大重试次数", e);
                    throw e;
                }
                log.warn("取消任务失败，正在重试 ({}/{})...", retryCount, maxRetries, e);
                Thread.sleep(backoffTime);
                backoffTime *= 2; // 指数退避
            }
        }

        throw new Exception("取消任务失败，已达到最大重试次数");
    }
}
