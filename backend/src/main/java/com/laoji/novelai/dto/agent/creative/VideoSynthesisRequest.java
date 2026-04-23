package com.laoji.novelai.dto.agent.creative;

import java.util.List;
import java.util.Map;

/**
 * 视频合成请求
 */
public class VideoSynthesisRequest {
    private String scriptId;
    private List<String> imageUrls;
    private List<String> audioUrls;
    private String backgroundMusicUrl;
    private Map<String, Object> videoParams;
    private Map<String, Object> metadata;

    // Getters and setters
    public String getScriptId() { return scriptId; }
    public void setScriptId(String scriptId) { this.scriptId = scriptId; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public List<String> getAudioUrls() { return audioUrls; }
    public void setAudioUrls(List<String> audioUrls) { this.audioUrls = audioUrls; }
    public String getBackgroundMusicUrl() { return backgroundMusicUrl; }
    public void setBackgroundMusicUrl(String backgroundMusicUrl) { this.backgroundMusicUrl = backgroundMusicUrl; }
    public Map<String, Object> getVideoParams() { return videoParams; }
    public void setVideoParams(Map<String, Object> videoParams) { this.videoParams = videoParams; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
