package com.laoji.novelai.service.audio.impl;

import com.laoji.novelai.entity.audio.VoicePreset;
import com.laoji.novelai.service.audio.BaseTtsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 模拟TTS服务，用于开发和测试
 */
@Service
@Primary
@ConditionalOnProperty(name = "tts.provider", havingValue = "mock", matchIfMissing = true)
@Slf4j
public class MockTtsService extends BaseTtsService {

    @Override
    public byte[] synthesizeSpeech(String text, VoicePreset voicePreset, Map<String, Object> params) {
        log.info("模拟TTS生成音频: textLength={}, voicePreset={}", text.length(), voicePreset);
        
        // 生成模拟音频数据（静音MP3）
        byte[] mockAudio = generateSilentMp3(1000); // 1秒静音
        
        log.info("模拟TTS生成完成: audioSize={} bytes", mockAudio.length);
        return mockAudio;
    }

    @Override
    public String synthesizeSpeechAsync(String text, VoicePreset voicePreset, Map<String, Object> params, TtsCallback callback) {
        log.info("模拟TTS异步生成音频: textLength={}, voicePreset={}", text.length(), voicePreset);
        
        String taskId = "mock-" + System.currentTimeMillis() + "-" + text.hashCode();
        
        new Thread(() -> {
            try {
                callback.onProgress(0.1, "开始模拟生成");
                Thread.sleep(100);
                
                callback.onProgress(0.5, "生成模拟音频");
                Thread.sleep(200);
                
                callback.onProgress(1.0, "生成完成");
                
                byte[] audioData = generateSilentMp3(2000); // 2秒静音
                callback.onSuccess(audioData, Map.of(
                    "provider", "mock",
                    "voicePresetId", voicePreset != null ? voicePreset.getId() : null,
                    "textLength", text.length(),
                    "taskId", taskId,
                    "duration", 2.0,
                    "format", "audio/mp3"
                ));
            } catch (Exception e) {
                callback.onError("模拟TTS生成失败: " + e.getMessage(), e);
            }
        }).start();
        
        return taskId;
    }

    @Override
    public TtsTaskStatus getTaskStatus(String taskId) {
        TtsTaskStatus status = new TtsTaskStatus();
        status.setTaskId(taskId);
        status.setStatus("COMPLETED");
        status.setProgress(1.0);
        status.setMessage("模拟任务已完成");
        status.setStartTime(System.currentTimeMillis() - 5000);
        status.setEndTime(System.currentTimeMillis());
        return status;
    }

    @Override
    public Map<String, Object> getAvailableVoices(String language) {
        return Map.of(
            "voices", new Object[]{
                Map.of(
                    "name", "mock-male",
                    "displayName", "模拟男声",
                    "gender", "male",
                    "locale", "zh-CN",
                    "styleList", new String[]{"general"}
                ),
                Map.of(
                    "name", "mock-female", 
                    "displayName", "模拟女声",
                    "gender", "female",
                    "locale", "zh-CN",
                    "styleList", new String[]{"general"}
                )
            },
            "provider", "mock",
            "language", language,
            "supportedFeatures", new String[]{"basic"}
        );
    }

    @Override
    public Map<String, Object> getServiceStatus() {
        return Map.of(
            "provider", "mock",
            "status", "available",
            "lastCheckTime", System.currentTimeMillis()
        );
    }

    @Override
    public String getProviderName() {
        return "Mock TTS Service";
    }

    @Override
    public String getProviderType() {
        return "mock";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        return Map.of(
            "type", "mock",
            "description", "模拟TTS服务，用于开发和测试",
            "maxTextLength", 10000,
            "maxAudioLength", 60
        );
    }

    @Override
    public Map<String, Object> getLimitations() {
        return Map.of(
            "maxTextLength", 10000,
            "maxAudioLength", 60,
            "supportedLanguages", new String[]{"zh-CN", "en-US"},
            "audioFormats", getSupportedFormats(),
            "sampleRates", getSupportedSampleRates()
        );
    }

    @Override
    public String[] getSupportedFormats() {
        return new String[]{"audio/mp3", "audio/wav"};
    }

    @Override
    public Integer[] getSupportedSampleRates() {
        return new Integer[]{16000, 22050, 44100};
    }

    @Override
    public Integer[] getSupportedBitrates() {
        return new Integer[]{64000, 128000, 192000};
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    // 抽象方法实现
    @Override
    protected boolean isLanguageSupported(String language) {
        return "zh-CN".equals(language) || "en-US".equals(language);
    }

    @Override
    protected String getDefaultFormat() {
        return "audio/mp3";
    }

    @Override
    protected Integer getDefaultSampleRate() {
        return 22050;
    }

    @Override
    protected Integer getDefaultBitrate() {
        return 128000;
    }

    @Override
    protected String getDefaultCurrency() {
        return "USD";
    }

    @Override
    protected double getCharacterCost() {
        return 0.0; // 免费
    }

    @Override
    protected Long estimateGenerationTime(String text, VoicePreset voicePreset) {
        return 1000L; // 1秒
    }

    @Override
    protected Long getUptime() {
        return System.currentTimeMillis(); // 从启动开始
    }

    @Override
    protected String[] getDependencies() {
        return new String[]{"none"};
    }

    @Override
    protected Map<String, String> getDependencyStatus() {
        return Map.of("none", "ok");
    }

    // 生成静音MP3数据
    private byte[] generateSilentMp3(int durationMs) {
        // 简单的MP3帧头（静音）
        // 实际实现中应该生成有效的MP3数据
        // 这里返回模拟数据
        byte[] data = new byte[1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        return data;
    }
}