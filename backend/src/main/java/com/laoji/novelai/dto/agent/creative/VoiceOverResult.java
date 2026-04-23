package com.laoji.novelai.dto.agent.creative;

import java.util.List;
import java.util.Map;

/**
 * 配音结果
 */
public class VoiceOverResult {
    private String id;
    private String scriptId;
    private List<VoiceOverItem> items;
    private String totalDuration;
    private Map<String, Object> metadata;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getScriptId() { return scriptId; }
    public void setScriptId(String scriptId) { this.scriptId = scriptId; }
    public List<VoiceOverItem> getItems() { return items; }
    public void setItems(List<VoiceOverItem> items) { this.items = items; }
    public String getTotalDuration() { return totalDuration; }
    public void setTotalDuration(String totalDuration) { this.totalDuration = totalDuration; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
