package com.laoji.novelai.entity.audio;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 音频资源实体
 * 存储通过TTS服务生成的音频文件元数据
 */
@Entity
@Table(name = "audio_resource")
@Data
@EqualsAndHashCode(callSuper = true)
public class AudioResource extends BaseEntity {

    /**
     * 音频文件名称
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * 音频文件URL（OSS存储路径）
     */
    @Column(nullable = false)
    private String fileUrl;

    /**
     * 原始文本内容
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalText;

    /**
     * 使用的语音预设ID
     */
    @Column(nullable = false)
    private Long voicePresetId;

    /**
     * 音频时长（秒）
     */
    private Double duration;

    /**
     * 音频文件大小（字节）
     */
    private Long fileSize;

    /**
     * 音频格式
     * MP3, WAV, OGG, AAC
     */
    private String format = "MP3";

    /**
     * 音频采样率（Hz）
     */
    private Integer sampleRate = 44100;

    /**
     * 音频比特率（kbps）
     */
    private Integer bitrate = 128;

    /**
     * 声道数
     * MONO - 单声道
     * STEREO - 立体声
     */
    private String channels = "STEREO";

    /**
     * 生成状态
     * PENDING - 等待生成
     * PROCESSING - 生成中
     * COMPLETED - 已完成
     * FAILED - 失败
     */
    @Column(nullable = false)
    private String status = "COMPLETED";

    /**
     * 失败原因（如果生成失败）
     */
    private String failureReason;

    /**
     * 开始生成时间
     */
    private LocalDateTime startTime;

    /**
     * 完成生成时间
     */
    private LocalDateTime endTime;

    /**
     * 生成耗时（毫秒）
     */
    private Long generationTime;

    /**
     * 使用的TTS提供商
     */
    private String provider;

    /**
     * 提供商请求ID（用于调试）
     */
    private String providerRequestId;

    /**
     * 关联的角色ID（如果有）
     */
    private Long characterId;

    /**
     * 关联的事件ID（如果有）
     */
    private Long eventId;

    /**
     * 关联的章节ID（如果有）
     */
    private Long chapterId;

    /**
     * 关联的小说ID
     */
    @Column(nullable = false)
    private Long novelId;

    /**
     * 标签（JSON数组格式）
     * 例如：["对话", "旁白", "重要", "测试"]
     */
    @Column(columnDefinition = "JSON")
    private String tags;

    /**
     * 播放次数统计
     */
    private Long playCount = 0L;

    /**
     * 下载次数统计
     */
    private Long downloadCount = 0L;

    /**
     * 最后播放时间
     */
    private LocalDateTime lastPlayedAt;

    /**
     * 是否公开
     */
    private Boolean isPublic = true;

    /**
     * 音频质量评分（1-5）
     */
    private Integer qualityScore;

    /**
     * 人工审核状态
     * PENDING - 待审核
     * APPROVED - 已通过
     * REJECTED - 已拒绝
     */
    private String reviewStatus = "PENDING";

    /**
     * 审核意见
     */
    private String reviewComment;

    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 字幕文件URL（如果有）
     */
    private String subtitleUrl;

    /**
     * 后处理状态
     * NONE - 未处理
     * PROCESSED - 已处理
     * PROCESSING - 处理中
     */
    private String postProcessingStatus = "NONE";

    /**
     * 后处理参数（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String postProcessingParams;

    /**
     * 后处理后的音频URL
     */
    private String processedAudioUrl;
}