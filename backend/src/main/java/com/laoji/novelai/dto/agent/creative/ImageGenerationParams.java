package com.laoji.novelai.dto.agent.creative;

import java.util.Map;

/**
 * 图片生成参数
 */
public class ImageGenerationParams {
    private String id;
    private String prompt;
    private String negativePrompt;
    private String model;
    private int width;
    private int height;
    private float cfgScale;
    private int steps;
    private String sampler;
    private Map<String, Object> additionalParams;
    private String shotId;
    private Map<String, Object> metadata;

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
    public Map<String, Object> getAdditionalParams() { return additionalParams; }
    public void setAdditionalParams(Map<String, Object> additionalParams) { this.additionalParams = additionalParams; }
    public String getShotId() { return shotId; }
    public void setShotId(String shotId) { this.shotId = shotId; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
