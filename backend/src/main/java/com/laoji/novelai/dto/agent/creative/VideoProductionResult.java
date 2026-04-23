package com.laoji.novelai.dto.agent.creative;

import java.util.Map;

/**
 * 视频制作结果
 */
public class VideoProductionResult {
    private String id;
    private String chapterId;
    private String novelId;
    private Script script;
    private VideoResult video;
    private Map<String, Object> productionStats;
    private Map<String, Object> metadata;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getChapterId() { return chapterId; }
    public void setChapterId(String chapterId) { this.chapterId = chapterId; }
    public String getNovelId() { return novelId; }
    public void setNovelId(String novelId) { this.novelId = novelId; }
    public Script getScript() { return script; }
    public void setScript(Script script) { this.script = script; }
    public VideoResult getVideo() { return video; }
    public void setVideo(VideoResult video) { this.video = video; }
    public Map<String, Object> getProductionStats() { return productionStats; }
    public void setProductionStats(Map<String, Object> productionStats) { this.productionStats = productionStats; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
