package com.laoji.novelai.service.comfyui;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ComfyUI视频服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComfyUIVideoService {

    private final ComfyUIClient comfyUIClient;
    private final ObjectMapper objectMapper;

    /**
     * 生成视频
     */
    public String generateVideo(VideoGenerationRequest request) throws Exception {
        log.info("生成视频请求: {}", request);

        // 构建视频生成工作流
        Map<String, Object> workflow = buildVideoWorkflow(request);

        // 提交工作流
        String promptId = comfyUIClient.submitWorkflow(workflow);

        // 获取生成的视频
        String videoUrl = getVideo(promptId, 0);

        log.info("视频生成成功，URL: {}", videoUrl);
        return videoUrl;
    }

    /**
     * 构建视频生成工作流
     */
    private Map<String, Object> buildVideoWorkflow(VideoGenerationRequest request) {
        // 这里使用一个简单的视频生成工作流模板，实际项目中应该从数据库或配置文件中加载
        Map<String, Object> workflow = new HashMap<>();

        // 节点映射
        Map<String, Object> nodes = new HashMap<>();

        // 1. 提示词节点
        Map<String, Object> promptNode = new HashMap<>();
        promptNode.put("inputs", Map.of(
                "text", request.getPrompt(),
                "clip", "",
                "skip_normalize", false
        ));
        promptNode.put("class_type", "CLIPTextEncode");
        nodes.put("1", promptNode);

        // 2. 负向提示词节点
        Map<String, Object> negativePromptNode = new HashMap<>();
        negativePromptNode.put("inputs", Map.of(
                "text", request.getNegativePrompt(),
                "clip", "",
                "skip_normalize", false
        ));
        negativePromptNode.put("class_type", "CLIPTextEncode");
        nodes.put("2", negativePromptNode);

        // 3. 图像加载节点
        Map<String, Object> imageLoadNode = new HashMap<>();
        imageLoadNode.put("inputs", Map.of(
                "image", request.getImageUrl()
        ));
        imageLoadNode.put("class_type", "LoadImage");
        nodes.put("3", imageLoadNode);

        // 4. 模型加载节点
        Map<String, Object> modelNode = new HashMap<>();
        modelNode.put("inputs", Map.of(
                "model_name", request.getModel()
        ));
        modelNode.put("class_type", "CheckpointLoaderSimple");
        nodes.put("4", modelNode);

        // 5. 视频生成节点
        Map<String, Object> videoNode = new HashMap<>();
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("seed", request.getSeed() != null ? request.getSeed() : System.currentTimeMillis());
        inputs.put("steps", request.getSteps());
        inputs.put("cfg", request.getCfgScale());
        inputs.put("sampler_name", request.getSampler());
        inputs.put("scheduler", "normal");
        inputs.put("denoise", 1.0);
        
        Map<String, Object> modelRef = new HashMap<>();
        modelRef.put("node", 4);
        modelRef.put("field", "model");
        inputs.put("model", modelRef);
        
        Map<String, Object> positiveRef = new HashMap<>();
        positiveRef.put("node", 1);
        positiveRef.put("field", "conditioning");
        inputs.put("positive", positiveRef);
        
        Map<String, Object> negativeRef = new HashMap<>();
        negativeRef.put("node", 2);
        negativeRef.put("field", "conditioning");
        inputs.put("negative", negativeRef);
        
        Map<String, Object> imageRef = new HashMap<>();
        imageRef.put("node", 3);
        imageRef.put("field", "image");
        inputs.put("image", imageRef);
        
        inputs.put("duration", request.getDuration());
        inputs.put("fps", request.getFps());
        
        videoNode.put("inputs", inputs);
        videoNode.put("class_type", "StableVideoDiffusion");
        nodes.put("5", videoNode);

        // 6. 保存视频节点
        Map<String, Object> saveNode = new HashMap<>();
        saveNode.put("inputs", Map.of(
                "filename_prefix", "comfyui_video",
                "videos", Map.of("node", 5, "field", "videos")
        ));
        saveNode.put("class_type", "SaveVideo");
        nodes.put("6", saveNode);

        workflow.put("nodes", nodes);
        workflow.put("last_node_id", 6);
        workflow.put("links", new HashMap<>());
        workflow.put("groups", new HashMap<>());
        workflow.put("config", new HashMap<>());

        return workflow;
    }

    /**
     * 获取生成的视频
     */
    private String getVideo(String promptId, int index) throws Exception {
        log.info("获取生成的视频，prompt_id: {}, index: {}", promptId, index);

        // 轮询获取结果
        String url = "http://localhost:8188/history/" + promptId;
        int maxRetries = 60; // 视频生成可能需要更长时间
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                Map<String, Object> response = comfyUIClient.getStatus();

                if (response != null && response.containsKey(promptId)) {
                    Map<String, Object> historyItem = (Map<String, Object>) response.get(promptId);
                    if (historyItem.containsKey("outputs")) {
                        Map<String, Object> outputs = (Map<String, Object>) historyItem.get("outputs");
                        // 查找视频输出
                        for (Map.Entry<String, Object> entry : outputs.entrySet()) {
                            Map<String, Object> output = (Map<String, Object>) entry.getValue();
                            if (output.containsKey("videos")) {
                                List<Map<String, Object>> videos = (List<Map<String, Object>>) output.get("videos");
                                if (index < videos.size()) {
                                    Map<String, Object> video = videos.get(index);
                                    String filename = (String) video.get("filename");
                                    log.info("视频生成成功，文件名: {}", filename);
                                    return "http://localhost:8188/view?filename=" + filename;
                                }
                            }
                        }
                    }
                }

                // 等待一段时间后重试
                Thread.sleep(3000); // 视频生成需要更长时间
                retryCount++;
            } catch (Exception e) {
                log.error("获取视频失败", e);
                Thread.sleep(3000);
                retryCount++;
            }
        }

        throw new Exception("视频生成超时");
    }

    /**
     * 批量生成视频
     */
    public Map<String, Object> batchGenerateVideos(BatchVideoGenerationRequest request) throws Exception {
        log.info("批量生成视频请求: {}", request);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "completed");
        result.put("videos", new HashMap<>());

        for (VideoGenerationRequest videoRequest : request.getRequests()) {
            try {
                String videoUrl = generateVideo(videoRequest);
                ((Map<String, Object>) result.get("videos")).put(videoRequest.getId(), videoUrl);
            } catch (Exception e) {
                log.error("生成视频失败: {}", e.getMessage());
                ((Map<String, Object>) result.get("videos")).put(videoRequest.getId(), null);
            }
        }

        log.info("批量视频生成完成");
        return result;
    }

    /**
     * 视频生成请求
     */
    public static class VideoGenerationRequest {
        private String id;
        private String prompt;
        private String negativePrompt;
        private String model;
        private String imageUrl;
        private int duration;
        private int fps;
        private float cfgScale;
        private int steps;
        private String sampler;
        private Long seed;
        private Map<String, Object> additionalParams;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        public String getNegativePrompt() { return negativePrompt; }
        public void setNegativePrompt(String negativePrompt) { this.negativePrompt = negativePrompt; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public int getFps() { return fps; }
        public void setFps(int fps) { this.fps = fps; }
        public float getCfgScale() { return cfgScale; }
        public void setCfgScale(float cfgScale) { this.cfgScale = cfgScale; }
        public int getSteps() { return steps; }
        public void setSteps(int steps) { this.steps = steps; }
        public String getSampler() { return sampler; }
        public void setSampler(String sampler) { this.sampler = sampler; }
        public Long getSeed() { return seed; }
        public void setSeed(Long seed) { this.seed = seed; }
        public Map<String, Object> getAdditionalParams() { return additionalParams; }
        public void setAdditionalParams(Map<String, Object> additionalParams) { this.additionalParams = additionalParams; }
    }

    /**
     * 批量视频生成请求
     */
    public static class BatchVideoGenerationRequest {
        private List<VideoGenerationRequest> requests;
        private Map<String, Object> batchParams;

        // Getters and setters
        public List<VideoGenerationRequest> getRequests() { return requests; }
        public void setRequests(List<VideoGenerationRequest> requests) { this.requests = requests; }
        public Map<String, Object> getBatchParams() { return batchParams; }
        public void setBatchParams(Map<String, Object> batchParams) { this.batchParams = batchParams; }
    }
}
