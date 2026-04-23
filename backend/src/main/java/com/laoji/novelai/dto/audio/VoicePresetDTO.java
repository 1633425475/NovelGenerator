package com.laoji.novelai.dto.audio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 语音预设数据传输对象
 */
@Data
public class VoicePresetDTO {
    
    private Long id;
    
    @NotBlank(message = "预设名称不能为空")
    private String name;
    
    private String description;
    
    @NotBlank(message = "TTS提供商不能为空")
    private String provider;
    
    private String providerParams;
    
    private String gender = "UNKNOWN";
    
    private String ageGroup = "ADULT";
    
    private String emotion = "NEUTRAL";
    
    private String language = "zh-CN";
    
    private String sampleUrl;
    
    private Boolean enabled = true;
    
    private Boolean isDefault = false;
    
    private List<String> tags;
    
    private Long characterId;
    
    @NotNull(message = "小说ID不能为空")
    private Long novelId;
    
    private Long usageCount = 0L;
    
    private Long cloneSourceId;
    
    private String cloneSampleUrl;
    
    private String cloneStatus = "COMPLETED";
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * 提供商标识符（例如Azure的voiceName）
     */
    private String providerVoiceId;
    
    /**
     * 提供商风格（例如Azure的style）
     */
    private String providerStyle;
    
    /**
     * 语速（0.5-2.0）
     */
    private Double rate = 1.0;
    
    /**
     * 音高（-100到100）
     */
    private Integer pitch = 0;
    
    /**
     * 音量（0.0-1.0）
     */
    private Double volume = 1.0;
    
    /**
     * 试听文本
     */
    private String sampleText;
    
    /**
     * 预设分类
     */
    private String category;
    
    /**
     * 预设创建者ID
     */
    private Long creatorId;
    
    /**
     * 是否系统预设
     */
    private Boolean isSystemPreset = false;
    
    /**
     * 克隆任务ID
     */
    private String cloneTaskId;
    
    /**
     * 克隆进度（0-100）
     */
    private Integer cloneProgress = 100;
    
    /**
     * 音频质量评级（1-5）
     */
    private Integer qualityRating;
    
    /**
     * 自然度评分（1-5）
     */
    private Integer naturalnessScore;
    
    /**
     * 清晰度评分（1-5）
     */
    private Integer clarityScore;
}