package com.laoji.novelai.dto.agent.creative;

import java.util.List;
import java.util.Map;

/**
 * 视频剪辑建议
 */
public class VideoEditingSuggestions {
    private String id;
    private String scriptId;
    private List<EditingSuggestion> suggestions;
    private Map<String, Object> metadata;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getScriptId() { return scriptId; }
    public void setScriptId(String scriptId) { this.scriptId = scriptId; }
    public List<EditingSuggestion> getSuggestions() { return suggestions; }
    public void setSuggestions(List<EditingSuggestion> suggestions) { this.suggestions = suggestions; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    /**
     * 剪辑建议项
     */
    public static class EditingSuggestion {
        private String id;
        private String type;
        private String description;
        private String startTime;
        private String endTime;
        private Map<String, Object> params;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
}
