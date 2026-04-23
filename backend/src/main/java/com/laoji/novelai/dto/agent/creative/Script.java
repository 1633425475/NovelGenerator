package com.laoji.novelai.dto.agent.creative;

import java.util.List;
import java.util.Map;

/**
 * 分镜脚本
 */
public class Script {
    private String id;
    private String title;
    private String chapterId;
    private String novelId;
    private List<Shot> shots;
    private String totalDuration;
    private String createdBy;
    private String createdAt;
    private String updatedAt;
    private Map<String, Object> metadata;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getChapterId() { return chapterId; }
    public void setChapterId(String chapterId) { this.chapterId = chapterId; }
    public String getNovelId() { return novelId; }
    public void setNovelId(String novelId) { this.novelId = novelId; }
    public List<Shot> getShots() { return shots; }
    public void setShots(List<Shot> shots) { this.shots = shots; }
    public String getTotalDuration() { return totalDuration; }
    public void setTotalDuration(String totalDuration) { this.totalDuration = totalDuration; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
