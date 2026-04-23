package com.laoji.novelai.service.audio;

import com.laoji.novelai.dto.audio.*;
import com.laoji.novelai.entity.audio.AudioResource;
import com.laoji.novelai.entity.audio.VoicePreset;

import java.util.List;
import java.util.Map;

/**
 * 音频服务接口
 * 负责音频生成、管理、处理等业务逻辑
 */
public interface AudioService {

    // ========== 音频生成 ==========

    /**
     * 生成音频（同步）
     */
    AudioGenerateResponse generateAudio(AudioGenerateRequest request);

    /**
     * 生成音频（异步）
     */
    String generateAudioAsync(AudioGenerateRequest request);

    /**
     * 批量生成音频
     */
    BatchAudioGenerateResponse batchGenerateAudio(BatchAudioGenerateRequest request);

    /**
     * 获取生成任务状态
     */
    Map<String, Object> getGenerationTaskStatus(String taskId);

    /**
     * 取消生成任务
     */
    boolean cancelGenerationTask(String taskId);

    /**
     * 重试生成任务
     */
    boolean retryGenerationTask(String taskId);

    // ========== 语音预设管理 ==========

    /**
     * 创建语音预设
     */
    VoicePresetDTO createVoicePreset(VoicePresetDTO voicePresetDTO);

    /**
     * 更新语音预设
     */
    VoicePresetDTO updateVoicePreset(Long id, VoicePresetDTO voicePresetDTO);

    /**
     * 获取语音预设
     */
    VoicePresetDTO getVoicePreset(Long id);

    /**
     * 删除语音预设（逻辑删除）
     */
    boolean deleteVoicePreset(Long id);

    /**
     * 获取小说语音预设列表
     */
    List<VoicePresetDTO> listVoicePresets(Long novelId, Map<String, Object> filters);

    /**
     * 搜索语音预设
     */
    List<VoicePresetDTO> searchVoicePresets(Long novelId, String keyword, Map<String, Object> filters);

    /**
     * 设置默认语音预设
     */
    boolean setDefaultVoicePreset(Long novelId, Long voicePresetId);

    /**
     * 获取默认语音预设
     */
    VoicePresetDTO getDefaultVoicePreset(Long novelId);

    /**
     * 测试语音预设
     */
    AudioGenerateResponse testVoicePreset(Long voicePresetId, String testText);

    /**
     * 克隆语音预设（语音克隆）
     */
    String cloneVoicePreset(Long sourceVoicePresetId, String cloneName, Map<String, Object> params);

    /**
     * 获取语音克隆状态
     */
    Map<String, Object> getVoiceCloningStatus(String cloneTaskId);

    // ========== 音频资源管理 ==========

    /**
     * 获取音频资源
     */
    AudioResourceDTO getAudioResource(Long id);

    /**
     * 删除音频资源（逻辑删除）
     */
    boolean deleteAudioResource(Long id);

    /**
     * 获取音频资源列表
     */
    List<AudioResourceDTO> listAudioResources(Long novelId, Map<String, Object> filters);

    /**
     * 搜索音频资源
     */
    List<AudioResourceDTO> searchAudioResources(Long novelId, String keyword, Map<String, Object> filters);

    /**
     * 更新音频资源信息
     */
    AudioResourceDTO updateAudioResource(Long id, AudioResourceDTO audioResourceDTO);

    /**
     * 播放音频（增加播放计数）
     */
    AudioResourceDTO playAudio(Long audioId);

    /**
     * 下载音频
     */
    Map<String, Object> downloadAudio(Long audioId);

    /**
     * 获取音频波形数据
     */
    Map<String, Object> getAudioWaveform(Long audioId);

    /**
     * 获取音频频谱数据
     */
    Map<String, Object> getAudioSpectrum(Long audioId);

    // ========== 音频处理 ==========

    /**
     * 合并多个音频文件
     */
    AudioResourceDTO mergeAudios(List<Long> audioIds, Map<String, Object> params);

    /**
     * 剪辑音频
     */
    AudioResourceDTO trimAudio(Long audioId, Double startTime, Double endTime);

    /**
     * 调整音频音量
     */
    AudioResourceDTO adjustAudioVolume(Long audioId, Double volume);

    /**
     * 添加音效
     */
    AudioResourceDTO addSoundEffect(Long audioId, Map<String, Object> soundEffect);

    /**
     * 添加背景音乐
     */
    AudioResourceDTO addBackgroundMusic(Long audioId, Map<String, Object> backgroundMusic);

    /**
     * 提取音频字幕
     */
    Map<String, Object> extractSubtitle(Long audioId, Map<String, Object> params);

    /**
     * 翻译音频字幕
     */
    Map<String, Object> translateSubtitle(Long audioId, String targetLanguage, Map<String, Object> params);

    /**
     * 分析音频质量
     */
    Map<String, Object> analyzeAudioQuality(Long audioId);

    /**
     * 优化音频质量
     */
    AudioResourceDTO enhanceAudioQuality(Long audioId, Map<String, Object> params);

    /**
     * 转换音频格式
     */
    AudioResourceDTO convertAudioFormat(Long audioId, String targetFormat, Map<String, Object> params);

    // ========== 批量操作 ==========

    /**
     * 批量删除音频资源
     */
    Map<String, Object> batchDeleteAudioResources(List<Long> audioIds);

    /**
     * 批量更新音频资源标签
     */
    Map<String, Object> batchUpdateAudioTags(List<Long> audioIds, List<String> tags);

    /**
     * 批量更新音频资源状态
     */
    Map<String, Object> batchUpdateAudioStatus(List<Long> audioIds, String status);

    /**
     * 批量审核音频资源
     */
    Map<String, Object> batchReviewAudioResources(List<Long> audioIds, String reviewStatus, String reviewComment);

    /**
     * 批量导出音频资源
     */
    Map<String, Object> batchExportAudioResources(List<Long> audioIds, Map<String, Object> exportParams);

    /**
     * 批量导入音频资源
     */
    Map<String, Object> batchImportAudioResources(List<Map<String, Object>> importData, Long novelId);

    // ========== 统计与分析 ==========

    /**
     * 获取音频统计信息
     */
    Map<String, Object> getAudioStatistics(Long novelId);

    /**
     * 获取使用统计
     */
    Map<String, Object> getUsageStatistics(Long novelId);

    /**
     * 获取成本统计
     */
    Map<String, Object> getCostStatistics(Long novelId);

    /**
     * 获取质量统计
     */
    Map<String, Object> getQualityStatistics(Long novelId);

    /**
     * 获取提供商统计
     */
    Map<String, Object> getProviderStatistics(Long novelId);

    /**
     * 获取趋势分析
     */
    Map<String, Object> getTrendAnalysis(Long novelId, String timeRange);

    /**
     * 获取热点分析
     */
    Map<String, Object> getHotspotAnalysis(Long novelId);

    /**
     * 获取推荐分析
     */
    Map<String, Object> getRecommendationAnalysis(Long novelId);

    // ========== 系统管理 ==========

    /**
     * 获取TTS服务状态
     */
    Map<String, Object> getTtsServiceStatus();

    /**
     * 切换TTS服务提供商
     */
    boolean switchTtsProvider(String provider, Map<String, Object> config);

    /**
     * 测试TTS服务连接
     */
    Map<String, Object> testTtsConnection(String provider, Map<String, Object> config);

    /**
     * 获取系统配置
     */
    Map<String, Object> getSystemConfiguration();

    /**
     * 更新系统配置
     */
    boolean updateSystemConfiguration(Map<String, Object> config);

    /**
     * 清理过期缓存
     */
    Map<String, Object> cleanupExpiredCache();

    /**
     * 清理临时文件
     */
    Map<String, Object> cleanupTempFiles();

    /**
     * 备份音频数据
     */
    Map<String, Object> backupAudioData(Map<String, Object> params);

    /**
     * 恢复音频数据
     */
    Map<String, Object> restoreAudioData(Map<String, Object> params);

    /**
     * 系统健康检查
     */
    Map<String, Object> healthCheck();

    // ========== 工具方法 ==========

    /**
     * 计算文本到音频的估计时长
     */
    Double estimateAudioDuration(String text, VoicePreset voicePreset);

    /**
     * 计算文本到音频的估计成本
     */
    Double estimateAudioCost(String text, VoicePreset voicePreset);

    /**
     * 验证音频参数
     */
    Map<String, Object> validateAudioParameters(Map<String, Object> params);

    /**
     * 生成音频文件名
     */
    String generateAudioFileName(Map<String, Object> params);

    /**
     * 解析SSML内容
     */
    Map<String, Object> parseSsmlContent(String ssml);

    /**
     * 生成SSML内容
     */
    String generateSsmlContent(String text, Map<String, Object> params);

    /**
     * 检测文本语言
     */
    String detectTextLanguage(String text);

    /**
     * 分割长文本
     */
    List<String> splitLongText(String text, Integer maxLength, Integer overlap);

    /**
     * 合并短文本
     */
    String mergeShortTexts(List<String> texts, String separator);

    // ========== 事件处理 ==========

    /**
     * 处理音频生成完成事件
     */
    void handleAudioGenerationComplete(String taskId, AudioResource audioResource);

    /**
     * 处理音频生成失败事件
     */
    void handleAudioGenerationFailed(String taskId, String errorMessage);

    /**
     * 处理音频播放事件
     */
    void handleAudioPlayed(Long audioId, Long userId);

    /**
     * 处理音频下载事件
     */
    void handleAudioDownloaded(Long audioId, Long userId);

    /**
     * 处理音频审核事件
     */
    void handleAudioReviewed(Long audioId, String reviewStatus, String reviewComment, Long reviewerId);

    // ========== 回调接口 ==========

    /**
     * 注册任务回调
     */
    void registerTaskCallback(String taskId, AudioTaskCallback callback);

    /**
     * 移除任务回调
     */
    void removeTaskCallback(String taskId);

    /**
     * 音频任务回调接口
     */
    interface AudioTaskCallback {
        void onProgress(String taskId, double progress, String status);
        void onComplete(String taskId, AudioResource audioResource);
        void onError(String taskId, String errorMessage);
        void onCancelled(String taskId);
    }
}