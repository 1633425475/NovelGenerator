package com.laoji.novelai.dto.audio;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量音频生成请求数据传输对象
 */
@Data
public class BatchAudioGenerateRequest {
    
    @NotNull(message = "小说ID不能为空")
    private Long novelId;
    
    @NotEmpty(message = "脚本条目不能为空")
    private List<ScriptItemDTO> scriptItems;
    
    private String batchName;
    
    private String description;
    
    private Boolean mergeToSingleFile = false;
    
    private String mergedFileName;
    
    private String mergedFileFormat = "MP3";
    
    private Double gapBetweenItems = 0.5;
    
    private Boolean enableCrossfade = true;
    
    private Double crossfadeDuration = 0.3;
    
    private Boolean enableBackgroundMusic = false;
    
    private String backgroundMusicUrl;
    
    private Double backgroundMusicVolume = 0.3;
    
    private Boolean backgroundMusicLoop = true;
    
    private Boolean enableSoundEffects = false;
    
    private List<String> soundEffectUrls;
    
    private Boolean enablePostProcessing = false;
    
    private String postProcessingType;
    
    private String postProcessingParams;
    
    private Boolean enableSubtitle = false;
    
    private String subtitleFormat = "SRT";
    
    private Boolean generateSeparateSubtitles = true;
    
    private Boolean generateMergedSubtitle = true;
    
    private Boolean async = true;
    
    private String callbackUrl;
    
    private Integer priority = 5;
    
    private String[] tags;
    
    private Boolean isPublic = true;
    
    private String qualityPreference = "STANDARD";
    
    private Integer maxConcurrentTasks = 3;
    
    private Boolean stopOnError = false;
    
    private Integer retryCount = 3;
    
    private Integer retryDelay = 1000;
    
    private Boolean saveIndividualResources = true;
    
    private Boolean saveMergedResource = true;
    
    private String outputDirectory;
    
    private String namingPattern = "scene_{scene}_order_{order}";
    
    private Boolean enableAudioAnalysis = true;
    
    private Boolean enableQualityCheck = true;
    
    private Double minAcceptableQuality = 3.0;
    
    private Boolean autoRetryLowQuality = false;
    
    private Boolean enableProgressTracking = true;
    
    private String progressWebhookUrl;
    
    private Boolean enableEmailNotification = false;
    
    private String notificationEmail;
    
    private Boolean compressOutput = false;
    
    private String compressionFormat = "ZIP";
    
    private Integer compressionLevel = 6;
    
    private Boolean deleteTempFiles = true;
    
    private Boolean keepOriginalFiles = true;
    
    private String metadataJson;
    
    /**
     * 是否启用语音克隆优化
     */
    private Boolean enableVoiceCloningOptimization = false;
    
    /**
     * 语音克隆模型ID
     */
    private String voiceCloningModelId;
    
    /**
     * 是否启用语音合成缓存
     */
    private Boolean enableSynthesisCache = true;
    
    /**
     * 缓存过期时间（小时）
     */
    private Integer cacheExpirationHours = 24;
    
    /**
     * 是否启用语音增强
     */
    private Boolean enableVoiceEnhancement = false;
    
    /**
     * 语音增强算法
     */
    private String voiceEnhancementAlgorithm;
    
    /**
     * 是否启用噪声抑制
     */
    private Boolean enableNoiseSuppression = false;
    
    /**
     * 噪声抑制强度（0-1）
     */
    private Double noiseSuppressionStrength = 0.5;
    
    /**
     * 是否启用音量标准化
     */
    private Boolean enableLoudnessNormalization = true;
    
    /**
     * 目标响度（LUFS）
     */
    private Double targetLoudness = -16.0;
    
    /**
     * 是否启用自动音高校正
     */
    private Boolean enablePitchCorrection = false;
    
    /**
     * 是否启用时间拉伸
     */
    private Boolean enableTimeStretching = false;
    
    /**
     * 时间拉伸因子（0.5-2.0）
     */
    private Double timeStretchFactor = 1.0;
    
    /**
     * 是否启用音频水印
     */
    private Boolean enableAudioWatermark = false;
    
    /**
     * 水印类型
     */
    private String watermarkType;
    
    /**
     * 水印内容
     */
    private String watermarkContent;
    
    /**
     * 是否启用DRM保护
     */
    private Boolean enableDrmProtection = false;
    
    /**
     * DRM方案
     */
    private String drmScheme;
}