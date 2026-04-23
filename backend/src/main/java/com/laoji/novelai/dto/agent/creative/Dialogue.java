package com.laoji.novelai.dto.agent.creative;

import java.util.Map;

/**
 * 对话
 */
public class Dialogue {
    private String id;
    private String character;
    private String text;
    private String duration;
    private String voicePreset;
    private String emotion;
    private String audioUrl;
    private Map<String, Object> metadata;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCharacter() { return character; }
    public void setCharacter(String character) { this.character = character; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getVoicePreset() { return voicePreset; }
    public void setVoicePreset(String voicePreset) { this.voicePreset = voicePreset; }
    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
