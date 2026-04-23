package com.laoji.novelai.dto.audio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 音频生成请求数据传输对象
 */
@Data
public class AudioGenerateRequest {
    
    @NotBlank(message = "文本内容不能为空")
    private String text;
    
    @NotNull(message = "语音预设ID不能为空")
    private Long voicePresetId;
    
    @NotNull(message = "小说ID不能为空")
    private Long novelId;
    
    private Long characterId;
    
    private Long eventId;
    
    private Long chapterId;
    
    private String fileName;
    
    private String format = "MP3";
    
    private Integer sampleRate = 44100;
    
    private Integer bitrate = 128;
    
    private String channels = "STEREO";
    
    private Double rate = 1.0;
    
    private Integer pitch = 0;
    
    private Double volume = 1.0;
    
    private String emotion;
    
    private Boolean enablePostProcessing = false;
    
    private String postProcessingType;
    
    private String postProcessingParams;
    
    private Boolean enableSubtitle = false;
    
    private String subtitleFormat = "SRT";
    
    private Boolean isPublic = true;
    
    private String[] tags;
    
    /**
     * 是否异步生成
     */
    private Boolean async = false;
    
    /**
     * 异步任务回调URL
     */
    private String callbackUrl;
    
    /**
     * 任务优先级（1-10，1最高）
     */
    private Integer priority = 5;
    
    /**
     * 是否保存音频资源
     */
    private Boolean saveResource = true;
    
    /**
     * 是否立即返回音频URL
     */
    private Boolean returnUrlImmediately = true;
    
    /**
     * 音频质量偏好
     * STANDARD - 标准质量
     * HIGH - 高质量
     * ULTRA - 超高质量
     */
    private String qualityPreference = "STANDARD";
    
    /**
     * 音频编码器偏好
     */
    private String encoderPreference;
    
    /**
     * 语音风格参数（JSON格式）
     */
    private String styleParams;
    
    /**
     * 发音人参数（JSON格式）
     */
    private String speakerParams;
    
    /**
     * 语言检测模式
     * AUTO - 自动检测
     * MANUAL - 手动指定
     */
    private String languageDetection = "AUTO";
    
    /**
     * 手动指定的语言代码
     */
    private String manualLanguage;
    
    /**
     * 是否启用SSML
     */
    private Boolean enableSsml = false;
    
    /**
     * SSML内容（如果启用SSML，text字段将被忽略）
     */
    private String ssmlContent;
    
    /**
     * 音频分块生成设置
     */
    private Boolean enableChunking = true;
    
    /**
     * 最大分块长度（字符数）
     */
    private Integer maxChunkLength = 2000;
    
    /**
     * 分块重叠长度（字符数）
     */
    private Integer chunkOverlap = 100;
    
    /**
     * 附加参数（Map类型，JSON格式）
     */
    private java.util.Map<String, Object> params = new java.util.HashMap<>();
}