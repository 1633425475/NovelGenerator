package com.laoji.novelai.dto.audio;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 音频生成响应数据传输对象
 */
@Data
public class AudioGenerateResponse {
    
    private Boolean success;
    
    private String message;
    
    private AudioResourceDTO audioResource;
    
    private String audioUrl;
    
    private String fileUrl;
    
    private Double duration;
    
    private Long fileSize;
    
    private String format;
    
    private LocalDateTime generatedAt;
    
    private Long voicePresetId;
    
    private String voicePresetName;
    
    private String taskId;
    
    private String status;
    
    private Double progress;
    
    private Long estimatedTimeRemaining;
    
    private String errorCode;
    
    private String errorDetails;
    
    private Long generationTime;
    
    private String provider;
    
    private String providerRequestId;
    
    private Double cost;
    
    private String currency;
    
    private String subtitleUrl;
    
    private String subtitleContent;
    
    private String waveformDataUrl;
    
    private String peakDataUrl;
    
    private String spectrumDataUrl;
    
    private String metadataUrl;
    
    private String postProcessingStatus;
    
    private String processedAudioUrl;
    
    private Boolean isCached;
    
    private String cacheKey;
    
    private Long cacheHitCount;
    
    private Long audioId;
    
    private String downloadUrl;
    
    private String previewUrl;
    
    private String thumbnailUrl;
    
    private String[] relatedAudioUrls;
    
    private Boolean isMergedAudio;
    
    private Integer mergedItemCount;
    
    private String mergedAudioUrl;
    
    private String[] mergedSubtitleUrls;
    
    /**
     * 音频分析结果
     */
    private AudioAnalysisResult analysisResult;
    
    /**
     * 音频质量评估
     */
    private AudioQualityAssessment qualityAssessment;
    
    /**
     * 音频格式信息
     */
    private AudioFormatInfo formatInfo;
    
    /**
     * 音频统计信息
     */
    private AudioStatistics statistics;
    
    /**
     * 建议的后续操作
     */
    private String[] suggestedActions;
    
    /**
     * 警告信息
     */
    private String[] warnings;
    
    /**
     * 调试信息
     */
    private String debugInfo;
    
    /**
     * 版本信息
     */
    private String version;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    @Data
    public static class AudioAnalysisResult {
        private Double averageLoudness;
        private Double peakLoudness;
        private Double dynamicRange;
        private Double signalToNoiseRatio;
        private Double thd;
        private String frequencyResponse;
        private String[] detectedProblems;
        private String[] suggestedImprovements;
    }
    
    @Data
    public static class AudioQualityAssessment {
        private Double overallScore;
        private Double clarityScore;
        private Double naturalnessScore;
        private Double fluencyScore;
        private Double pronunciationScore;
        private Double emotionScore;
        private Double prosodyScore;
        private String qualityLevel;
        private Boolean meetsRequirements;
        private String[] improvementSuggestions;
    }
    
    @Data
    public static class AudioFormatInfo {
        private String format;
        private Integer sampleRate;
        private Integer bitrate;
        private String channels;
        private Integer duration;
        private Long fileSize;
        private String codec;
        private String container;
        private String encoding;
        private String bitDepth;
        private String channelLayout;
        private Boolean lossless;
        private String compressionRatio;
    }
    
    @Data
    public static class AudioStatistics {
        private Integer totalAudioCount;
        private Double totalDuration;
        private Long totalFileSize;
        private Double averageQualityScore;
        private Double successRate;
        private Double averageGenerationTime;
        private Double averageCost;
        private String mostUsedProvider;
        private String mostUsedVoice;
        private Integer cacheHitRate;
        private Integer errorRate;
        private Integer retryRate;
    }
}