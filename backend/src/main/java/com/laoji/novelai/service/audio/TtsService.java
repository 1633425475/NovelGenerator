package com.laoji.novelai.service.audio;

import com.laoji.novelai.entity.audio.VoicePreset;

import java.util.Map;

/**
 * TTS服务抽象接口
 * 支持多种TTS提供商（Azure、ElevenLabs、阿里云等）
 */
public interface TtsService {

    /**
     * 生成音频
     * 
     * @param text 文本内容
     * @param voicePreset 语音预设
     * @param params 额外参数
     * @return 音频数据字节数组
     */
    byte[] synthesizeSpeech(String text, VoicePreset voicePreset, Map<String, Object> params);

    /**
     * 异步生成音频
     * 
     * @param text 文本内容
     * @param voicePreset 语音预设
     * @param params 额外参数
     * @param callback 回调函数
     * @return 任务ID
     */
    String synthesizeSpeechAsync(String text, VoicePreset voicePreset, Map<String, Object> params, TtsCallback callback);

    /**
     * 获取音频生成状态
     * 
     * @param taskId 任务ID
     * @return 任务状态
     */
    TtsTaskStatus getTaskStatus(String taskId);

    /**
     * 获取支持的语音列表
     * 
     * @param language 语言代码
     * @return 语音列表
     */
    Map<String, Object> getAvailableVoices(String language);

    /**
     * 获取服务状态
     * 
     * @return 服务状态信息
     */
    Map<String, Object> getServiceStatus();

    /**
     * 获取服务提供商名称
     * 
     * @return 提供商名称
     */
    String getProviderName();

    /**
     * 获取服务提供商类型
     * 
     * @return 提供商类型
     */
    String getProviderType();

    /**
     * 检查服务是否可用
     * 
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * 获取服务的配置参数
     * 
     * @return 配置参数
     */
    Map<String, Object> getConfiguration();

    /**
     * 获取服务的限制信息
     * 
     * @return 限制信息
     */
    Map<String, Object> getLimitations();

    /**
     * 估算生成成本
     * 
     * @param text 文本内容
     * @param voicePreset 语音预设
     * @param params 额外参数
     * @return 成本估算
     */
    CostEstimation estimateCost(String text, VoicePreset voicePreset, Map<String, Object> params);

    /**
     * 获取音频格式支持
     * 
     * @return 支持的音频格式
     */
    String[] getSupportedFormats();

    /**
     * 获取支持的采样率
     * 
     * @return 支持的采样率
     */
    Integer[] getSupportedSampleRates();

    /**
     * 获取支持的比特率
     * 
     * @return 支持的比特率
     */
    Integer[] getSupportedBitrates();

    /**
     * 验证语音预设是否有效
     * 
     * @param voicePreset 语音预设
     * @return 验证结果
     */
    ValidationResult validateVoicePreset(VoicePreset voicePreset);

    /**
     * 测试语音预设
     * 
     * @param voicePreset 语音预设
     * @param testText 测试文本
     * @return 测试结果
     */
    TestResult testVoicePreset(VoicePreset voicePreset, String testText);

    /**
     * 获取服务统计信息
     * 
     * @return 统计信息
     */
    Map<String, Object> getStatistics();

    /**
     * 重置服务统计信息
     */
    void resetStatistics();

    /**
     * 获取服务版本
     * 
     * @return 版本信息
     */
    String getVersion();

    /**
     * 获取服务健康状态
     * 
     * @return 健康状态
     */
    HealthStatus getHealthStatus();

    /**
     * TTS回调接口
     */
    interface TtsCallback {
        void onSuccess(byte[] audioData, Map<String, Object> metadata);
        void onError(String errorMessage, Throwable throwable);
        void onProgress(double progress, String status);
    }

    /**
     * TTS任务状态
     */
    class TtsTaskStatus {
        private String taskId;
        private String status;
        private double progress;
        private String message;
        private byte[] audioData;
        private Map<String, Object> metadata;
        private String errorMessage;
        private Long startTime;
        private Long endTime;
        private Long estimatedTimeRemaining;

        // Getters and setters
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public double getProgress() { return progress; }
        public void setProgress(double progress) { this.progress = progress; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public byte[] getAudioData() { return audioData; }
        public void setAudioData(byte[] audioData) { this.audioData = audioData; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public Long getStartTime() { return startTime; }
        public void setStartTime(Long startTime) { this.startTime = startTime; }
        public Long getEndTime() { return endTime; }
        public void setEndTime(Long endTime) { this.endTime = endTime; }
        public Long getEstimatedTimeRemaining() { return estimatedTimeRemaining; }
        public void setEstimatedTimeRemaining(Long estimatedTimeRemaining) { this.estimatedTimeRemaining = estimatedTimeRemaining; }
    }

    /**
     * 成本估算
     */
    class CostEstimation {
        private double estimatedCost;
        private String currency;
        private String costModel;
        private Map<String, Object> breakdown;
        private String unit;
        private double unitCost;
        private int estimatedUnits;
        private String billingPeriod;
        private boolean freeTierAvailable;
        private double freeTierLimit;
        private double freeTierRemaining;

        // Getters and setters
        public double getEstimatedCost() { return estimatedCost; }
        public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getCostModel() { return costModel; }
        public void setCostModel(String costModel) { this.costModel = costModel; }
        public Map<String, Object> getBreakdown() { return breakdown; }
        public void setBreakdown(Map<String, Object> breakdown) { this.breakdown = breakdown; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public double getUnitCost() { return unitCost; }
        public void setUnitCost(double unitCost) { this.unitCost = unitCost; }
        public int getEstimatedUnits() { return estimatedUnits; }
        public void setEstimatedUnits(int estimatedUnits) { this.estimatedUnits = estimatedUnits; }
        public String getBillingPeriod() { return billingPeriod; }
        public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
        public boolean isFreeTierAvailable() { return freeTierAvailable; }
        public void setFreeTierAvailable(boolean freeTierAvailable) { this.freeTierAvailable = freeTierAvailable; }
        public double getFreeTierLimit() { return freeTierLimit; }
        public void setFreeTierLimit(double freeTierLimit) { this.freeTierLimit = freeTierLimit; }
        public double getFreeTierRemaining() { return freeTierRemaining; }
        public void setFreeTierRemaining(double freeTierRemaining) { this.freeTierRemaining = freeTierRemaining; }
    }

    /**
     * 验证结果
     */
    class ValidationResult {
        private boolean valid;
        private String message;
        private String[] warnings;
        private String[] errors;
        private Map<String, Object> suggestions;
        private String severity;

        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String[] getWarnings() { return warnings; }
        public void setWarnings(String[] warnings) { this.warnings = warnings; }
        public String[] getErrors() { return errors; }
        public void setErrors(String[] errors) { this.errors = errors; }
        public Map<String, Object> getSuggestions() { return suggestions; }
        public void setSuggestions(Map<String, Object> suggestions) { this.suggestions = suggestions; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public void addWarning(String warning) {
            if (this.warnings == null) {
                this.warnings = new String[]{warning};
            } else {
                String[] newWarnings = new String[this.warnings.length + 1];
                System.arraycopy(this.warnings, 0, newWarnings, 0, this.warnings.length);
                newWarnings[this.warnings.length] = warning;
                this.warnings = newWarnings;
            }
        }
    }

    /**
     * 测试结果
     */
    class TestResult {
        private boolean success;
        private byte[] audioData;
        private String audioUrl;
        private String errorMessage;
        private Map<String, Object> metrics;
        private String[] warnings;
        private String recommendation;
        private Long generationTime;
        private Double audioQuality;
        private String audioFormat;
        private Integer audioSize;

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public byte[] getAudioData() { return audioData; }
        public void setAudioData(byte[] audioData) { this.audioData = audioData; }
        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
        public String[] getWarnings() { return warnings; }
        public void setWarnings(String[] warnings) { this.warnings = warnings; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        public Long getGenerationTime() { return generationTime; }
        public void setGenerationTime(Long generationTime) { this.generationTime = generationTime; }
        public Double getAudioQuality() { return audioQuality; }
        public void setAudioQuality(Double audioQuality) { this.audioQuality = audioQuality; }
        public String getAudioFormat() { return audioFormat; }
        public void setAudioFormat(String audioFormat) { this.audioFormat = audioFormat; }
        public Integer getAudioSize() { return audioSize; }
        public void setAudioSize(Integer audioSize) { this.audioSize = audioSize; }
    }

    /**
     * 健康状态
     */
    class HealthStatus {
        private String status;
        private String message;
        private Map<String, Object> details;
        private Long timestamp;
        private String version;
        private String[] dependencies;
        private Map<String, String> dependencyStatus;

        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String[] getDependencies() { return dependencies; }
        public void setDependencies(String[] dependencies) { this.dependencies = dependencies; }
        public Map<String, String> getDependencyStatus() { return dependencyStatus; }
        public void setDependencyStatus(Map<String, String> dependencyStatus) { this.dependencyStatus = dependencyStatus; }
    }
}