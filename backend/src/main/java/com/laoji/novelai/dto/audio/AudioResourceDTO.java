package com.laoji.novelai.dto.audio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 音频资源数据传输对象
 */
@Data
public class AudioResourceDTO {
    
    private Long id;
    
    @NotBlank(message = "文件名不能为空")
    private String fileName;
    
    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;
    
    @NotBlank(message = "原始文本不能为空")
    private String originalText;
    
    @NotNull(message = "语音预设ID不能为空")
    private Long voicePresetId;
    
    private Double duration;
    
    private Long fileSize;
    
    private String format = "MP3";
    
    private Integer sampleRate = 44100;
    
    private Integer bitrate = 128;
    
    private String channels = "STEREO";
    
    @NotBlank(message = "状态不能为空")
    private String status = "COMPLETED";
    
    private String failureReason;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Long generationTime;
    
    private String provider;
    
    private String providerRequestId;
    
    private Long characterId;
    
    private Long eventId;
    
    private Long chapterId;
    
    @NotNull(message = "小说ID不能为空")
    private Long novelId;
    
    private List<String> tags;
    
    private Long playCount = 0L;
    
    private Long downloadCount = 0L;
    
    private LocalDateTime lastPlayedAt;
    
    private Boolean isPublic = true;
    
    private Integer qualityScore;
    
    private String reviewStatus = "PENDING";
    
    private String reviewComment;
    
    private LocalDateTime reviewedAt;
    
    private Long reviewerId;
    
    private String subtitleUrl;
    
    private String postProcessingStatus = "NONE";
    
    private String postProcessingParams;
    
    private String processedAudioUrl;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * 语音预设名称（用于展示）
     */
    private String voicePresetName;
    
    /**
     * 角色名称（如果有）
     */
    private String characterName;
    
    /**
     * 事件标题（如果有）
     */
    private String eventTitle;
    
    /**
     * 章节标题（如果有）
     */
    private String chapterTitle;
    
    /**
     * 音频波形数据（JSON格式）
     */
    private String waveformData;
    
    /**
     * 音频峰值数据
     */
    private String peakData;
    
    /**
     * 音频频谱数据
     */
    private String spectrumData;
    
    /**
     * 音频元数据（JSON格式）
     */
    private String metadata;
    
    /**
     * 字幕内容（JSON格式）
     */
    private String subtitleContent;
    
    /**
     * 字幕格式（SRT, VTT, ASS等）
     */
    private String subtitleFormat;
    
    /**
     * 音频语言
     */
    private String audioLanguage;
    
    /**
     * 音频编码器
     */
    private String encoder;
    
    /**
     * 音频采样位数
     */
    private Integer sampleBits = 16;
    
    /**
     * 音频声道布局
     */
    private String channelLayout;
    
    /**
     * 音频压缩率
     */
    private Double compressionRatio;
    
    /**
     * 音频信噪比（dB）
     */
    private Double signalToNoiseRatio;
    
    /**
     * 音频动态范围（dB）
     */
    private Double dynamicRange;
    
    /**
     * 音频谐波失真率（%）
     */
    private Double thd;
    
    /**
     * 音频频率响应范围（Hz）
     */
    private String frequencyResponse;
    
    /**
     * 音频电平（dBFS）
     */
    private Double loudness;
    
    /**
     * 音频峰值电平（dBFS）
     */
    private Double peakLevel;
    
    /**
     * 音频平均电平（dBFS）
     */
    private Double averageLevel;
    
    /**
     * 音频过载采样次数
     */
    private Integer oversamples;
}