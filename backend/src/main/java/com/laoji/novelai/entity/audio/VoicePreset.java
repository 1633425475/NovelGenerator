package com.laoji.novelai.entity.audio;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 语音预设实体
 * 用于存储TTS服务的语音配置，支持多种TTS提供商（Azure、ElevenLabs、阿里云等）
 */
@Entity
@Table(name = "audio_voice_preset")
@Data
@EqualsAndHashCode(callSuper = true)
public class VoicePreset extends BaseEntity {

    /**
     * 预设名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 预设描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * TTS提供商
     * AZURE - Microsoft Azure Cognitive Services
     * ELEVENLABS - ElevenLabs
     * ALIYUN - 阿里云
     * GOOGLE - Google Cloud Text-to-Speech
     * CUSTOM - 自定义/语音克隆
     */
    @Column(nullable = false)
    private String provider;

    /**
     * 提供商参数（JSON格式）
     * 例如：{"voiceName":"zh-CN-XiaoxiaoNeural","style":"cheerful","rate":"1.0","pitch":"0"}
     */
    @Column(columnDefinition = "JSON")
    private String providerParams;

    /**
     * 语音性别
     * MALE - 男性
     * FEMALE - 女性
     * UNKNOWN - 未知
     */
    private String gender = "UNKNOWN";

    /**
     * 年龄段
     * CHILD - 儿童
     * YOUNG - 青年
     * ADULT - 成人
     * ELDER - 老年
     */
    private String ageGroup = "ADULT";

    /**
     * 情绪/语调
     * NEUTRAL - 中性
     * CHEERFUL - 欢快
     * SAD - 悲伤
     * ANGRY - 愤怒
     * CALM - 平静
     * EXCITED - 兴奋
     */
    private String emotion = "NEUTRAL";

    /**
     * 语言代码（BCP 47格式）
     * 例如：zh-CN, en-US, ja-JP
     */
    private String language = "zh-CN";

    /**
     * 示例音频URL（用于试听）
     */
    private String sampleUrl;

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 是否默认预设
     */
    private Boolean isDefault = false;

    /**
     * 标签（JSON数组格式）
     * 例如：["旁白", "主角", "反派", "老人"]
     */
    @Column(columnDefinition = "JSON")
    private String tags;

    /**
     * 关联的角色ID（如果有）
     */
    private Long characterId;

    /**
     * 关联的小说ID
     */
    @Column(nullable = false)
    private Long novelId;

    /**
     * 使用次数统计
     */
    private Long usageCount = 0L;

    /**
     * 克隆来源ID（如果是语音克隆生成的预设）
     */
    private Long cloneSourceId;

    /**
     * 克隆样本音频URL
     */
    private String cloneSampleUrl;

    /**
     * 克隆状态
     * PENDING - 等待克隆
     * PROCESSING - 处理中
     * COMPLETED - 已完成
     * FAILED - 失败
     */
    private String cloneStatus = "COMPLETED";

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedAt;

    public String getVoiceName() {
        return this.name;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
}