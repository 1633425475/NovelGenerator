package com.laoji.novelai.dto.audio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 脚本条目数据传输对象
 * 表示批量配音脚本中的一个条目
 */
@Data
public class ScriptItemDTO {
    
    @NotBlank(message = "文本内容不能为空")
    private String text;
    
    @NotNull(message = "语音预设ID不能为空")
    private Long voicePresetId;
    
    private Long characterId;
    
    private String characterName;
    
    private String emotion;
    
    private Double rate = 1.0;
    
    private Integer pitch = 0;
    
    private Double volume = 1.0;
    
    private Integer order = 0;
    
    private Double startTime;
    
    private Double duration;
    
    private String scene;
    
    private String[] tags;
    
    private String fileName;
    
    private Boolean enableSubtitle = false;
    
    private String subtitleFormat = "SRT";
    
    private Boolean enableSsml = false;
    
    private String ssmlContent;
    
    private String styleParams;
    
    private String speakerParams;
    
    private String audioFormat = "MP3";
    
    private Integer sampleRate = 44100;
    
    private Integer bitrate = 128;
    
    /**
     * 是否合并到前一个音频
     */
    private Boolean mergeWithPrevious = false;
    
    /**
     * 合并间隔（秒）
     */
    private Double mergeGap = 0.5;
    
    /**
     * 淡入淡出效果
     */
    private Boolean enableFade = true;
    
    /**
     * 淡入时长（秒）
     */
    private Double fadeInDuration = 0.2;
    
    /**
     * 淡出时长（秒）
     */
    private Double fadeOutDuration = 0.3;
    
    /**
     * 音量调整（相对于预设）
     */
    private Double volumeAdjustment = 0.0;
    
    /**
     * 音效文件URL
     */
    private String soundEffectUrl;
    
    /**
     * 音效开始时间（相对于音频开始）
     */
    private Double soundEffectStart;
    
    /**
     * 音效音量
     */
    private Double soundEffectVolume = 1.0;
    
    /**
     * 音效循环次数
     */
    private Integer soundEffectLoop = 1;
    
    /**
     * 是否背景音乐
     */
    private Boolean isBackgroundMusic = false;
}