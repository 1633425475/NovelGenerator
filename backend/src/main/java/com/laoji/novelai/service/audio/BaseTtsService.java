package com.laoji.novelai.service.audio;

import com.laoji.novelai.entity.audio.VoicePreset;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TTS服务基础抽象类
 * 提供通用实现和工具方法
 */
@Slf4j
public abstract class BaseTtsService implements TtsService {

    @Override
    public ValidationResult validateVoicePreset(VoicePreset voicePreset) {
        ValidationResult result = new ValidationResult();
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        // 通用验证逻辑
        if (!getProviderName().toLowerCase().contains(voicePreset.getProvider().toLowerCase())) {
            errors.add("语音预设的提供商不匹配");
        }
        
        if (voicePreset.getVoiceName() == null || voicePreset.getVoiceName().isEmpty()) {
            errors.add("语音名称不能为空");
        }
        
        if (voicePreset.getLanguage() != null && !isLanguageSupported(voicePreset.getLanguage())) {
            warnings.add("语言 " + voicePreset.getLanguage() + " 可能不被完全支持");
        }
        
        result.setValid(errors.isEmpty());
        result.setMessage(errors.isEmpty() ? "验证通过" : "验证失败");
        result.setWarnings(warnings.toArray(new String[0]));
        result.setErrors(errors.toArray(new String[0]));
        
        if (errors.isEmpty()) {
            result.setSuggestions(Map.of(
                "recommendedFormat", getDefaultFormat(),
                "recommendedSampleRate", getDefaultSampleRate(),
                "recommendedBitrate", getDefaultBitrate()
            ));
        }
        
        return result;
    }

    @Override
    public TestResult testVoicePreset(VoicePreset voicePreset, String testText) {
        log.info("测试语音预设: provider={}, voicePreset={}, testTextLength={}", 
                getProviderName(), voicePreset, testText.length());
        
        TestResult result = new TestResult();
        
        try {
            // 验证语音预设
            ValidationResult validation = validateVoicePreset(voicePreset);
            if (!validation.isValid()) {
                result.setSuccess(false);
                result.setErrorMessage("语音预设验证失败: " + validation.getMessage());
                result.setWarnings(validation.getWarnings());
                return result;
            }
            
            // 生成测试音频
            byte[] audioData = synthesizeSpeech(testText, voicePreset, Map.of());
            
            result.setSuccess(true);
            result.setAudioData(audioData);
            result.setGenerationTime(estimateGenerationTime(testText, voicePreset));
            result.setAudioQuality(0.8); // 默认质量评分
            result.setAudioFormat(getDefaultFormat());
            result.setAudioSize(audioData.length);
            result.setMetrics(Map.of(
                "textLength", testText.length(),
                "provider", getProviderName(),
                "voiceName", voicePreset.getVoiceName(),
                "language", voicePreset.getLanguage(),
                "validationResult", validation
            ));
            result.setRecommendation("语音预设测试通过，建议使用 " + getDefaultFormat() + " 格式");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("测试失败: " + e.getMessage());
            result.setWarnings(new String[]{"请检查TTS服务配置和网络连接"});
            log.error("语音预设测试失败", e);
        }
        
        return result;
    }

    @Override
    public CostEstimation estimateCost(String text, VoicePreset voicePreset, Map<String, Object> params) {
        CostEstimation estimation = new CostEstimation();
        
        // 默认成本估算模型：按字符计费
        double characterCost = getCharacterCost();
        int characterCount = text.length();
        
        estimation.setEstimatedCost(characterCount * characterCost);
        estimation.setCurrency(getDefaultCurrency());
        estimation.setCostModel("per character");
        estimation.setUnit("character");
        estimation.setUnitCost(characterCost);
        estimation.setEstimatedUnits(characterCount);
        estimation.setBreakdown(Map.of(
            "textLength", characterCount,
            "unitCost", characterCost,
            "estimatedCost", characterCount * characterCost,
            "provider", getProviderName()
        ));
        
        return estimation;
    }

    @Override
    public Map<String, Object> getStatistics() {
        return Map.of(
            "provider", getProviderName(),
            "totalRequests", 0,
            "successfulRequests", 0,
            "failedRequests", 0,
            "totalCharacters", 0,
            "totalCost", 0.0,
            "averageResponseTime", 0,
            "lastRequestTime", null,
            "uptime", getUptime(),
            "version", getVersion()
        );
    }

    @Override
    public void resetStatistics() {
        log.info("重置TTS服务统计信息: provider={}", getProviderName());
    }

    @Override
    public HealthStatus getHealthStatus() {
        HealthStatus health = new HealthStatus();
        health.setStatus(isAvailable() ? "healthy" : "unhealthy");
        health.setMessage(isAvailable() ? getProviderName() + " 服务正常" : getProviderName() + " 服务不可用");
        health.setTimestamp(System.currentTimeMillis());
        health.setVersion(getVersion());
        health.setDependencies(getDependencies());
        health.setDependencyStatus(getDependencyStatus());
        
        return health;
    }

    // 抽象方法，子类需要实现
    
    /**
     * 检查是否支持指定语言
     */
    protected abstract boolean isLanguageSupported(String language);
    
    /**
     * 获取默认音频格式
     */
    protected abstract String getDefaultFormat();
    
    /**
     * 获取默认采样率
     */
    protected abstract Integer getDefaultSampleRate();
    
    /**
     * 获取默认比特率
     */
    protected abstract Integer getDefaultBitrate();
    
    /**
     * 获取默认货币
     */
    protected abstract String getDefaultCurrency();
    
    /**
     * 获取每个字符的成本
     */
    protected abstract double getCharacterCost();
    
    /**
     * 估计生成时间（毫秒）
     */
    protected abstract Long estimateGenerationTime(String text, VoicePreset voicePreset);
    
    /**
     * 获取服务运行时间（毫秒）
     */
    protected abstract Long getUptime();
    
    /**
     * 获取依赖列表
     */
    protected abstract String[] getDependencies();
    
    /**
     * 获取依赖状态
     */
    protected abstract Map<String, String> getDependencyStatus();
}