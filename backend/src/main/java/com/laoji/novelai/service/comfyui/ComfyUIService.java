package com.laoji.novelai.service.comfyui;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ComfyUI服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComfyUIService {

    private final ComfyUIClient comfyUIClient;
    private final ObjectMapper objectMapper;

    /**
     * 生成图片
     */
    public String generateImage(ImageGenerationRequest request) throws Exception {
        log.info("生成图片请求: {}", request);

        // 构建工作流
        Map<String, Object> workflow = buildWorkflow(request);

        // 提交工作流
        String promptId = comfyUIClient.submitWorkflow(workflow);

        // 获取生成的图片
        String imageUrl = comfyUIClient.getImage(promptId, 0);

        log.info("图片生成成功，URL: {}", imageUrl);
        return imageUrl;
    }

    /**
     * 构建工作流
     */
    private Map<String, Object> buildWorkflow(ImageGenerationRequest request) {
        // 这里使用一个简单的工作流模板，实际项目中应该从数据库或配置文件中加载
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

        // 3. 潜在空间生成节点
        Map<String, Object> latentNode = new HashMap<>();
        latentNode.put("inputs", Map.of(
                "width", request.getWidth(),
                "height", request.getHeight(),
                "batch_size", 1
        ));
        latentNode.put("class_type", "EmptyLatentImage");
        nodes.put("3", latentNode);

        // 4. 模型加载节点
        Map<String, Object> modelNode = new HashMap<>();
        modelNode.put("inputs", Map.of(
                "model_name", request.getModel()
        ));
        modelNode.put("class_type", "CheckpointLoaderSimple");
        nodes.put("4", modelNode);

        // 5. 采样节点
        Map<String, Object> samplerNode = new HashMap<>();
        samplerNode.put("inputs", Map.of(
                "seed", request.getSeed() != null ? request.getSeed() : System.currentTimeMillis(),
                "steps", request.getSteps(),
                "cfg", request.getCfgScale(),
                "sampler_name", request.getSampler(),
                "scheduler", "normal",
                "denoise", 1.0,
                "model", Map.of("node", 4, "field", "model"),
                "positive", Map.of("node", 1, "field", "conditioning"),
                "negative", Map.of("node", 2, "field", "conditioning"),
                "latent_image", Map.of("node", 3, "field", "samples")
        ));
        samplerNode.put("class_type", "KSampler");
        nodes.put("5", samplerNode);

        // 6. 图像解码节点
        Map<String, Object> decodeNode = new HashMap<>();
        decodeNode.put("inputs", Map.of(
                "samples", Map.of("node", 5, "field", "samples"),
                "vae", Map.of("node", 4, "field", "vae")
        ));
        decodeNode.put("class_type", "VAEDecode");
        nodes.put("6", decodeNode);

        // 7. 保存图像节点
        Map<String, Object> saveNode = new HashMap<>();
        saveNode.put("inputs", Map.of(
                "filename_prefix", "comfyui",
                "images", Map.of("node", 6, "field", "images")
        ));
        saveNode.put("class_type", "SaveImage");
        nodes.put("7", saveNode);

        workflow.put("nodes", nodes);
        workflow.put("last_node_id", 7);
        workflow.put("links", new HashMap<>());
        workflow.put("groups", new HashMap<>());
        workflow.put("config", new HashMap<>());

        return workflow;
    }

    /**
     * 批量生成图片
     */
    public Map<String, Object> batchGenerateImages(BatchImageGenerationRequest request) throws Exception {
        log.info("批量生成图片请求: {}", request);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "completed");
        result.put("images", new HashMap<>());

        for (ImageGenerationRequest imageRequest : request.getRequests()) {
            try {
                String imageUrl = generateImage(imageRequest);
                ((Map<String, Object>) result.get("images")).put(imageRequest.getId(), imageUrl);
            } catch (Exception e) {
                log.error("生成图片失败: {}", e.getMessage());
                ((Map<String, Object>) result.get("images")).put(imageRequest.getId(), null);
            }
        }

        log.info("批量图片生成完成");
        return result;
    }

    /**
     * 获取ComfyUI服务状态
     */
    public Map<String, Object> getStatus() throws Exception {
        return comfyUIClient.getStatus();
    }

    /**
     * 取消任务
     */
    public void cancelTask(String promptId) throws Exception {
        comfyUIClient.cancelTask(promptId);
    }

    /**
     * 图片生成请求
     */
    public static class ImageGenerationRequest {
        private String id;
        private String prompt;
        private String negativePrompt;
        private String model;
        private int width;
        private int height;
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
        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
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
     * 批量图片生成请求
     */
    public static class BatchImageGenerationRequest {
        private List<ImageGenerationRequest> requests;
        private Map<String, Object> batchParams;

        // Getters and setters
        public List<ImageGenerationRequest> getRequests() { return requests; }
        public void setRequests(List<ImageGenerationRequest> requests) { this.requests = requests; }
        public Map<String, Object> getBatchParams() { return batchParams; }
        public void setBatchParams(Map<String, Object> batchParams) { this.batchParams = batchParams; }
    }
}
