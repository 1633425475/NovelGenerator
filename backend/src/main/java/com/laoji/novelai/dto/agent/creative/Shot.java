package com.laoji.novelai.dto.agent.creative;

import java.util.List;
import java.util.Map;

/**
 * 镜头
 */
public class Shot {
    private String id;
    private int shotNumber;
    private String description;
    private String duration;
    private String cameraAngle;
    private String cameraMovement;
    private String lighting;
    private List<Dialogue> dialogues;
    private List<String> characters;
    private String location;
    private String sceneTime;
    private Map<String, Object> visualElements;
    private Map<String, Object> audioElements;
    private Map<String, Object> metadata;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getShotNumber() { return shotNumber; }
    public void setShotNumber(int shotNumber) { this.shotNumber = shotNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getCameraAngle() { return cameraAngle; }
    public void setCameraAngle(String cameraAngle) { this.cameraAngle = cameraAngle; }
    public String getCameraMovement() { return cameraMovement; }
    public void setCameraMovement(String cameraMovement) { this.cameraMovement = cameraMovement; }
    public String getLighting() { return lighting; }
    public void setLighting(String lighting) { this.lighting = lighting; }
    public List<Dialogue> getDialogues() { return dialogues; }
    public void setDialogues(List<Dialogue> dialogues) { this.dialogues = dialogues; }
    public List<String> getCharacters() { return characters; }
    public void setCharacters(List<String> characters) { this.characters = characters; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSceneTime() { return sceneTime; }
    public void setSceneTime(String sceneTime) { this.sceneTime = sceneTime; }
    public Map<String, Object> getVisualElements() { return visualElements; }
    public void setVisualElements(Map<String, Object> visualElements) { this.visualElements = visualElements; }
    public Map<String, Object> getAudioElements() { return audioElements; }
    public void setAudioElements(Map<String, Object> audioElements) { this.audioElements = audioElements; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
