package com.laoji.novelai.service.audio.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laoji.novelai.dto.audio.*;
import com.laoji.novelai.entity.audio.AudioResource;
import com.laoji.novelai.entity.audio.VoicePreset;
import com.laoji.novelai.repository.audio.AudioResourceRepository;
import com.laoji.novelai.repository.audio.VoicePresetRepository;
import com.laoji.novelai.service.audio.AudioService;
import com.laoji.novelai.service.audio.TtsService;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音频服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AudioServiceImpl implements AudioService {

    private final VoicePresetRepository voicePresetRepository;
    private final AudioResourceRepository audioResourceRepository;
    private final TtsService ttsService;
    private final MinioClient minioClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Override
    @Transactional
    public AudioGenerateResponse generateAudio(AudioGenerateRequest request) {
        log.info("开始生成音频: {}", request);
        
        try {
            // 1. 验证请求
            if (request.getText() == null || request.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("文本内容不能为空");
            }
            
            if (request.getVoicePresetId() == null) {
                throw new IllegalArgumentException("语音预设ID不能为空");
            }
            
            // 2. 获取语音预设
            VoicePreset voicePreset = voicePresetRepository.findById(request.getVoicePresetId())
                    .orElseThrow(() -> new IllegalArgumentException("语音预设不存在: " + request.getVoicePresetId()));
            
            // 3. 验证语音预设
            TtsService.ValidationResult validation = ttsService.validateVoicePreset(voicePreset);
            if (!validation.isValid()) {
                throw new IllegalArgumentException("语音预设验证失败: " + validation.getMessage());
            }
            
            // 4. 调用TTS服务生成音频
            log.info("调用TTS服务生成音频: textLength={}, voicePreset={}", request.getText().length(), voicePreset);
            byte[] audioData = ttsService.synthesizeSpeech(request.getText(), voicePreset, request.getParams());
            
            // 5. 上传音频到MinIO
            String fileName = generateAudioFileName(Map.of(
                "novelId", request.getNovelId(),
                "voicePresetId", voicePreset.getId(),
                "timestamp", System.currentTimeMillis()
            ));
            
            String fileUrl = uploadToMinio(audioData, fileName, "audio/mp3");
            
            // 6. 创建音频资源记录
            AudioResource audioResource = new AudioResource();
            audioResource.setNovelId(request.getNovelId());
            audioResource.setVoicePresetId(voicePreset.getId());
            audioResource.setOriginalText(request.getText());
            audioResource.setFileUrl(fileUrl);
            audioResource.setFileName(fileName);
            audioResource.setFormat("audio/mp3");
            audioResource.setFileSize((long) audioData.length);
            audioResource.setDuration(estimateAudioDuration(request.getText(), voicePreset));
            audioResource.setProvider(voicePreset.getProvider());
            audioResource.setStatus("COMPLETED");
            audioResource.setQualityScore(80); // 默认质量评分
            audioResource.setTags(String.join(",", request.getTags()));
            
            AudioResource savedResource = audioResourceRepository.save(audioResource);
            
            // 7. 更新语音预设使用次数
            voicePreset.setUsageCount(voicePreset.getUsageCount() + 1);
            voicePreset.setLastUsedAt(java.time.LocalDateTime.now());
            voicePresetRepository.save(voicePreset);
            
            // 8. 返回响应
            AudioGenerateResponse response = new AudioGenerateResponse();
            response.setAudioId(savedResource.getId());
            response.setFileUrl(savedResource.getFileUrl());
            response.setDuration(savedResource.getDuration());
            response.setFileSize(savedResource.getFileSize());
            response.setFormat(savedResource.getFormat());
            response.setStatus(savedResource.getStatus());
            response.setGeneratedAt(savedResource.getCreatedAt());
            response.setVoicePresetId(voicePreset.getId());
            response.setVoicePresetName(voicePreset.getName());
            
            log.info("音频生成成功: audioId={}, fileUrl={}", savedResource.getId(), savedResource.getFileUrl());
            return response;
            
        } catch (Exception e) {
            log.error("音频生成失败", e);
            throw new RuntimeException("音频生成失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateAudioAsync(AudioGenerateRequest request) {
        log.info("开始异步生成音频: {}", request);
        // TODO: 实现异步音频生成逻辑
        return null;
    }

    @Override
    public BatchAudioGenerateResponse batchGenerateAudio(BatchAudioGenerateRequest request) {
        log.info("开始批量生成音频: {}", request);
        // TODO: 实现批量音频生成逻辑
        return null;
    }

    @Override
    public Map<String, Object> getGenerationTaskStatus(String taskId) {
        // TODO: 实现获取生成任务状态逻辑
        return null;
    }

    @Override
    public boolean cancelGenerationTask(String taskId) {
        // TODO: 实现取消生成任务逻辑
        return false;
    }

    @Override
    public boolean retryGenerationTask(String taskId) {
        // TODO: 实现重试生成任务逻辑
        return false;
    }

    @Override
    public VoicePresetDTO createVoicePreset(VoicePresetDTO voicePresetDTO) {
        log.info("创建语音预设: {}", voicePresetDTO);
        // TODO: 实现创建语音预设逻辑
        return null;
    }

    @Override
    public VoicePresetDTO updateVoicePreset(Long id, VoicePresetDTO voicePresetDTO) {
        log.info("更新语音预设: id={}, data={}", id, voicePresetDTO);
        // TODO: 实现更新语音预设逻辑
        return null;
    }

    @Override
    public VoicePresetDTO getVoicePreset(Long id) {
        // TODO: 实现获取语音预设逻辑
        return null;
    }

    @Override
    public boolean deleteVoicePreset(Long id) {
        log.info("删除语音预设: id={}", id);
        // TODO: 实现删除语音预设逻辑
        return false;
    }

    @Override
    public List<VoicePresetDTO> listVoicePresets(Long novelId, Map<String, Object> filters) {
        // TODO: 实现获取小说语音预设列表逻辑
        return null;
    }

    @Override
    public List<VoicePresetDTO> searchVoicePresets(Long novelId, String keyword, Map<String, Object> filters) {
        // TODO: 实现搜索语音预设逻辑
        return null;
    }

    @Override
    public boolean setDefaultVoicePreset(Long novelId, Long voicePresetId) {
        log.info("设置默认语音预设: novelId={}, voicePresetId={}", novelId, voicePresetId);
        // TODO: 实现设置默认语音预设逻辑
        return false;
    }

    @Override
    public VoicePresetDTO getDefaultVoicePreset(Long novelId) {
        // TODO: 实现获取默认语音预设逻辑
        return null;
    }

    @Override
    public AudioGenerateResponse testVoicePreset(Long voicePresetId, String testText) {
        log.info("测试语音预设: voicePresetId={}, testText={}", voicePresetId, testText);
        // TODO: 实现测试语音预设逻辑
        return null;
    }

    @Override
    public String cloneVoicePreset(Long sourceVoicePresetId, String cloneName, Map<String, Object> params) {
        log.info("克隆语音预设: sourceVoicePresetId={}, cloneName={}", sourceVoicePresetId, cloneName);
        // TODO: 实现克隆语音预设逻辑
        return null;
    }

    @Override
    public Map<String, Object> getVoiceCloningStatus(String cloneTaskId) {
        // TODO: 实现获取语音克隆状态逻辑
        return null;
    }

    @Override
    public AudioResourceDTO getAudioResource(Long id) {
        // TODO: 实现获取音频资源逻辑
        return null;
    }

    @Override
    public boolean deleteAudioResource(Long id) {
        log.info("删除音频资源: id={}", id);
        // TODO: 实现删除音频资源逻辑
        return false;
    }

    @Override
    public List<AudioResourceDTO> listAudioResources(Long novelId, Map<String, Object> filters) {
        // TODO: 实现获取音频资源列表逻辑
        return null;
    }

    @Override
    public List<AudioResourceDTO> searchAudioResources(Long novelId, String keyword, Map<String, Object> filters) {
        // TODO: 实现搜索音频资源逻辑
        return null;
    }

    @Override
    public AudioResourceDTO updateAudioResource(Long id, AudioResourceDTO audioResourceDTO) {
        log.info("更新音频资源: id={}, data={}", id, audioResourceDTO);
        // TODO: 实现更新音频资源逻辑
        return null;
    }

    @Override
    public AudioResourceDTO playAudio(Long audioId) {
        log.info("播放音频: audioId={}", audioId);
        // TODO: 实现播放音频逻辑
        return null;
    }

    @Override
    public Map<String, Object> downloadAudio(Long audioId) {
        // TODO: 实现下载音频逻辑
        return null;
    }

    @Override
    public Map<String, Object> getAudioWaveform(Long audioId) {
        // TODO: 实现获取音频波形数据逻辑
        return null;
    }

    @Override
    public Map<String, Object> getAudioSpectrum(Long audioId) {
        // TODO: 实现获取音频频谱数据逻辑
        return null;
    }

    @Override
    @Transactional
    public AudioResourceDTO mergeAudios(List<Long> audioIds, Map<String, Object> params) {
        log.info("合并音频: audioIds={}, params={}", audioIds, params);
        
        try {
            // 1. 验证输入
            if (audioIds == null || audioIds.isEmpty()) {
                throw new IllegalArgumentException("音频ID列表不能为空");
            }
            if (audioIds.size() < 2) {
                throw new IllegalArgumentException("至少需要2个音频进行合并");
            }
            
            // 2. 获取音频资源
            List<AudioResource> audioResources = new ArrayList<>();
            for (Long audioId : audioIds) {
                AudioResource audioResource = audioResourceRepository.findById(audioId)
                        .orElseThrow(() -> new IllegalArgumentException("音频资源不存在: " + audioId));
                audioResources.add(audioResource);
            }
            
            // 3. 智能排序（根据参数决定排序方式）
            String sortBy = params != null ? (String) params.get("sortBy") : null;
            if (sortBy != null) {
                switch (sortBy.toLowerCase()) {
                    case "created":
                        // 按创建时间排序
                        audioResources.sort((a, b) -> {
                            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                            if (a.getCreatedAt() == null) return 1;
                            if (b.getCreatedAt() == null) return -1;
                            return a.getCreatedAt().compareTo(b.getCreatedAt());
                        });
                        log.info("按创建时间排序音频: 数量={}", audioResources.size());
                        break;
                        
                    case "chapter":
                        // 按章节排序（需要chapterId字段）
                        audioResources.sort((a, b) -> {
                            if (a.getChapterId() == null && b.getChapterId() == null) return 0;
                            if (a.getChapterId() == null) return 1;
                            if (b.getChapterId() == null) return -1;
                            return a.getChapterId().compareTo(b.getChapterId());
                        });
                        log.info("按章节排序音频: 数量={}", audioResources.size());
                        break;
                        
                    case "event":
                        // 按事件排序（需要eventId字段）
                        audioResources.sort((a, b) -> {
                            if (a.getEventId() == null && b.getEventId() == null) return 0;
                            if (a.getEventId() == null) return 1;
                            if (b.getEventId() == null) return -1;
                            return a.getEventId().compareTo(b.getEventId());
                        });
                        log.info("按事件排序音频: 数量={}", audioResources.size());
                        break;
                        
                    case "duration":
                        // 按时长排序
                        audioResources.sort((a, b) -> {
                            if (a.getDuration() == null && b.getDuration() == null) return 0;
                            if (a.getDuration() == null) return 1;
                            if (b.getDuration() == null) return -1;
                            return a.getDuration().compareTo(b.getDuration());
                        });
                        log.info("按时长排序音频: 数量={}", audioResources.size());
                        break;
                        
                    default:
                        log.info("使用默认排序（输入顺序）");
                        break;
                }
            }
            
            // 4. 检查音频格式是否一致
            String firstFormat = audioResources.get(0).getFormat();
            for (AudioResource resource : audioResources) {
                if (!firstFormat.equals(resource.getFormat())) {
                    log.warn("音频格式不一致: {} vs {}", firstFormat, resource.getFormat());
                    // 可以在这里添加格式转换逻辑
                }
            }
            
            // 4. 下载音频文件（模拟）
            List<byte[]> audioFiles = new ArrayList<>();
            double totalDuration = 0.0;
            long totalFileSize = 0L;
            Long novelId = audioResources.get(0).getNovelId();
            
            for (AudioResource resource : audioResources) {
                byte[] audioData = downloadFromMinio(resource.getFileUrl());
                audioFiles.add(audioData);
                totalDuration += resource.getDuration() != null ? resource.getDuration() : 0;
                totalFileSize += resource.getFileSize() != null ? resource.getFileSize() : 0;
                novelId = resource.getNovelId(); // 使用最后一个音频的小说ID
            }
            
            // 5. 合并音频（模拟实现）
            log.info("开始合并{}个音频文件，总时长: {}秒，总大小: {}字节", 
                    audioFiles.size(), totalDuration, totalFileSize);
            
            byte[] mergedAudioData = mergeAudioData(audioFiles, params);
            
            // 6. 上传合并后的音频到MinIO
            String fileName = generateAudioFileName(Map.of(
                "novelId", novelId,
                "operation", "merge",
                "timestamp", System.currentTimeMillis(),
                "count", audioFiles.size()
            ));
            
            String fileUrl = uploadToMinio(mergedAudioData, fileName, firstFormat);
            
            // 7. 创建新的音频资源记录
            AudioResource mergedResource = new AudioResource();
            mergedResource.setNovelId(novelId);
            mergedResource.setFileName(fileName);
            mergedResource.setFileUrl(fileUrl);
            mergedResource.setOriginalText("合并音频: " + audioIds);
            mergedResource.setVoicePresetId(audioResources.get(0).getVoicePresetId());
            mergedResource.setFormat(firstFormat);
            mergedResource.setFileSize((long) mergedAudioData.length);
            mergedResource.setDuration(totalDuration);
            mergedResource.setProvider("audio_processor");
            mergedResource.setStatus("COMPLETED");
            mergedResource.setPostProcessingStatus("MERGED");
            mergedResource.setQualityScore(calculateQualityScore(audioResources));
            
            // 添加标签
            List<String> tags = new ArrayList<>();
            tags.add("merged");
            tags.add("count_" + audioFiles.size());
            if (params != null && params.containsKey("tags")) {
                Object paramTags = params.get("tags");
                if (paramTags instanceof List) {
                    tags.addAll((List<String>) paramTags);
                }
            }
            mergedResource.setTags(String.join(",", tags));
            
            AudioResource savedResource = audioResourceRepository.save(mergedResource);
            
            // 8. 记录合并关系（可扩展）
            log.info("音频合并完成: newAudioId={}, mergedFrom={}", savedResource.getId(), audioIds);
            
            // 9. 返回结果
            return convertToDTO(savedResource);
            
        } catch (Exception e) {
            log.error("合并音频失败", e);
            throw new RuntimeException("合并音频失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AudioResourceDTO trimAudio(Long audioId, Double startTime, Double endTime) {
        log.info("剪辑音频: audioId={}, startTime={}, endTime={}", audioId, startTime, endTime);
        
        try {
            // 1. 验证输入
            if (audioId == null) {
                throw new IllegalArgumentException("音频ID不能为空");
            }
            if (startTime == null || startTime < 0) {
                throw new IllegalArgumentException("开始时间必须大于等于0");
            }
            if (endTime == null || endTime <= startTime) {
                throw new IllegalArgumentException("结束时间必须大于开始时间");
            }
            
            // 2. 获取音频资源
            AudioResource originalResource = audioResourceRepository.findById(audioId)
                    .orElseThrow(() -> new IllegalArgumentException("音频资源不存在: " + audioId));
            
            // 3. 验证时间范围
            Double originalDuration = originalResource.getDuration();
            if (originalDuration != null && endTime > originalDuration) {
                throw new IllegalArgumentException("结束时间不能超过音频总时长: " + originalDuration + "秒");
            }
            
            // 4. 下载原始音频
            byte[] originalAudioData = downloadFromMinio(originalResource.getFileUrl());
            
            // 5. 剪辑音频（模拟实现）
            log.info("剪辑音频: 从{}秒到{}秒，原时长{}秒", startTime, endTime, originalDuration);
            byte[] trimmedAudioData = clipAudioData(originalAudioData, startTime, endTime, originalResource.getFormat());
            
            // 6. 计算剪辑后的时长
            double trimmedDuration = endTime - startTime;
            
            // 7. 上传剪辑后的音频到MinIO
            String fileName = generateAudioFileName(Map.of(
                "novelId", originalResource.getNovelId(),
                "operation", "trim",
                "timestamp", System.currentTimeMillis(),
                "startTime", startTime,
                "endTime", endTime
            ));
            
            String fileUrl = uploadToMinio(trimmedAudioData, fileName, originalResource.getFormat());
            
            // 8. 创建新的音频资源记录
            AudioResource trimmedResource = new AudioResource();
            trimmedResource.setNovelId(originalResource.getNovelId());
            trimmedResource.setFileName(fileName);
            trimmedResource.setFileUrl(fileUrl);
            trimmedResource.setOriginalText(originalResource.getOriginalText() + " [剪辑: " + startTime + "-" + endTime + "秒]");
            trimmedResource.setVoicePresetId(originalResource.getVoicePresetId());
            trimmedResource.setCharacterId(originalResource.getCharacterId());
            trimmedResource.setChapterId(originalResource.getChapterId());
            trimmedResource.setEventId(originalResource.getEventId());
            trimmedResource.setFormat(originalResource.getFormat());
            trimmedResource.setFileSize((long) trimmedAudioData.length);
            trimmedResource.setDuration(trimmedDuration);
            trimmedResource.setProvider(originalResource.getProvider());
            trimmedResource.setStatus("COMPLETED");
            trimmedResource.setPostProcessingStatus("TRIMMED");
            trimmedResource.setQualityScore(originalResource.getQualityScore());
            
            // 添加标签
            List<String> tags = new ArrayList<>();
            if (originalResource.getTags() != null && !originalResource.getTags().trim().isEmpty()) {
                tags.addAll(Arrays.asList(originalResource.getTags().split(",")));
            }
            tags.add("trimmed");
            tags.add("trim_" + startTime + "_" + endTime);
            trimmedResource.setTags(String.join(",", tags));
            
            AudioResource savedResource = audioResourceRepository.save(trimmedResource);
            
            // 9. 记录剪辑关系
            log.info("音频剪辑完成: newAudioId={}, originalAudioId={}, duration={}秒", 
                    savedResource.getId(), audioId, trimmedDuration);
            
            // 10. 返回结果
            return convertToDTO(savedResource);
            
        } catch (Exception e) {
            log.error("剪辑音频失败", e);
            throw new RuntimeException("剪辑音频失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AudioResourceDTO adjustAudioVolume(Long audioId, Double volume) {
        log.info("调整音频音量: audioId={}, volume={}", audioId, volume);
        
        try {
            // 1. 验证输入
            if (audioId == null) {
                throw new IllegalArgumentException("音频ID不能为空");
            }
            if (volume == null || volume < 0 || volume > 10) {
                throw new IllegalArgumentException("音量必须在0到10之间");
            }
            
            // 2. 获取音频资源
            AudioResource originalResource = audioResourceRepository.findById(audioId)
                    .orElseThrow(() -> new IllegalArgumentException("音频资源不存在: " + audioId));
            
            // 3. 下载原始音频
            byte[] originalAudioData = downloadFromMinio(originalResource.getFileUrl());
            
            // 4. 调整音量（模拟实现）
            log.info("调整音频音量: 原音频大小={}字节, 音量倍数={}", originalAudioData.length, volume);
            byte[] adjustedAudioData = adjustVolumeData(originalAudioData, volume, originalResource.getFormat());
            
            // 5. 上传调整后的音频到MinIO
            String fileName = generateAudioFileName(Map.of(
                "novelId", originalResource.getNovelId(),
                "operation", "volume_adjust",
                "timestamp", System.currentTimeMillis(),
                "volume", volume
            ));
            
            String fileUrl = uploadToMinio(adjustedAudioData, fileName, originalResource.getFormat());
            
            // 6. 创建新的音频资源记录
            AudioResource adjustedResource = new AudioResource();
            adjustedResource.setNovelId(originalResource.getNovelId());
            adjustedResource.setFileName(fileName);
            adjustedResource.setFileUrl(fileUrl);
            adjustedResource.setOriginalText(originalResource.getOriginalText() + " [音量调整: " + volume + "倍]");
            adjustedResource.setVoicePresetId(originalResource.getVoicePresetId());
            adjustedResource.setCharacterId(originalResource.getCharacterId());
            adjustedResource.setChapterId(originalResource.getChapterId());
            adjustedResource.setEventId(originalResource.getEventId());
            adjustedResource.setFormat(originalResource.getFormat());
            adjustedResource.setFileSize((long) adjustedAudioData.length);
            adjustedResource.setDuration(originalResource.getDuration());
            adjustedResource.setProvider(originalResource.getProvider());
            adjustedResource.setStatus("COMPLETED");
            adjustedResource.setPostProcessingStatus("VOLUME_ADJUSTED");
            adjustedResource.setQualityScore(calculateAdjustedQuality(originalResource.getQualityScore(), volume));
            
            // 添加标签
            List<String> tags = new ArrayList<>();
            if (originalResource.getTags() != null && !originalResource.getTags().trim().isEmpty()) {
                tags.addAll(Arrays.asList(originalResource.getTags().split(",")));
            }
            tags.add("volume_adjusted");
            tags.add("volume_" + volume);
            adjustedResource.setTags(String.join(",", tags));
            
            AudioResource savedResource = audioResourceRepository.save(adjustedResource);
            
            // 7. 记录音量调整关系
            log.info("音频音量调整完成: newAudioId={}, originalAudioId={}, volume={}", 
                    savedResource.getId(), audioId, volume);
            
            // 8. 返回结果
            return convertToDTO(savedResource);
            
        } catch (Exception e) {
            log.error("调整音频音量失败", e);
            throw new RuntimeException("调整音频音量失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AudioResourceDTO addSoundEffect(Long audioId, Map<String, Object> soundEffect) {
        log.info("添加音效: audioId={}, soundEffect={}", audioId, soundEffect);
        
        try {
            // 1. 验证输入
            if (audioId == null) {
                throw new IllegalArgumentException("音频ID不能为空");
            }
            if (soundEffect == null || soundEffect.isEmpty()) {
                throw new IllegalArgumentException("音效参数不能为空");
            }
            
            // 2. 获取音频资源
            AudioResource originalResource = audioResourceRepository.findById(audioId)
                    .orElseThrow(() -> new IllegalArgumentException("音频资源不存在: " + audioId));
            
            // 3. 解析音效参数
            String effectType = (String) soundEffect.get("type");
            Double effectIntensity = (Double) soundEffect.get("intensity");
            Double startTime = (Double) soundEffect.get("startTime");
            Double endTime = (Double) soundEffect.get("endTime");
            
            if (effectType == null) {
                effectType = "echo"; // 默认音效
            }
            if (effectIntensity == null) {
                effectIntensity = 1.0;
            }
            
            // 4. 下载原始音频
            byte[] originalAudioData = downloadFromMinio(originalResource.getFileUrl());
            
            // 5. 应用音效（模拟实现）
            log.info("应用音效: type={}, intensity={}, startTime={}, endTime={}", 
                    effectType, effectIntensity, startTime, endTime);
            byte[] effectedAudioData = applySoundEffect(originalAudioData, soundEffect, originalResource.getFormat());
            
            // 6. 上传处理后的音频到MinIO
            String fileName = generateAudioFileName(Map.of(
                "novelId", originalResource.getNovelId(),
                "operation", "sound_effect",
                "timestamp", System.currentTimeMillis(),
                "effectType", effectType
            ));
            
            String fileUrl = uploadToMinio(effectedAudioData, fileName, originalResource.getFormat());
            
            // 7. 创建新的音频资源记录
            AudioResource effectedResource = new AudioResource();
            effectedResource.setNovelId(originalResource.getNovelId());
            effectedResource.setFileName(fileName);
            effectedResource.setFileUrl(fileUrl);
            effectedResource.setOriginalText(originalResource.getOriginalText() + " [音效: " + effectType + "]");
            effectedResource.setVoicePresetId(originalResource.getVoicePresetId());
            effectedResource.setCharacterId(originalResource.getCharacterId());
            effectedResource.setChapterId(originalResource.getChapterId());
            effectedResource.setEventId(originalResource.getEventId());
            effectedResource.setFormat(originalResource.getFormat());
            effectedResource.setFileSize((long) effectedAudioData.length);
            effectedResource.setDuration(originalResource.getDuration());
            effectedResource.setProvider(originalResource.getProvider());
            effectedResource.setStatus("COMPLETED");
            effectedResource.setPostProcessingStatus("SOUND_EFFECT_ADDED");
            effectedResource.setQualityScore(calculateEffectQuality(originalResource.getQualityScore(), effectType, effectIntensity));
            
            // 添加标签
            List<String> tags = new ArrayList<>();
            if (originalResource.getTags() != null && !originalResource.getTags().trim().isEmpty()) {
                tags.addAll(Arrays.asList(originalResource.getTags().split(",")));
            }
            tags.add("sound_effect");
            tags.add("effect_" + effectType);
            tags.add("intensity_" + effectIntensity);
            effectedResource.setTags(String.join(",", tags));
            
            AudioResource savedResource = audioResourceRepository.save(effectedResource);
            
            // 8. 记录音效添加关系
            log.info("音效添加完成: newAudioId={}, originalAudioId={}, effectType={}", 
                    savedResource.getId(), audioId, effectType);
            
            // 9. 返回结果
            return convertToDTO(savedResource);
            
        } catch (Exception e) {
            log.error("添加音效失败", e);
            throw new RuntimeException("添加音效失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AudioResourceDTO addBackgroundMusic(Long audioId, Map<String, Object> backgroundMusic) {
        log.info("添加背景音乐: audioId={}, backgroundMusic={}", audioId, backgroundMusic);
        
        try {
            // 1. 验证输入
            if (audioId == null) {
                throw new IllegalArgumentException("音频ID不能为空");
            }
            if (backgroundMusic == null || backgroundMusic.isEmpty()) {
                throw new IllegalArgumentException("背景音乐参数不能为空");
            }
            
            // 2. 获取音频资源
            AudioResource originalResource = audioResourceRepository.findById(audioId)
                    .orElseThrow(() -> new IllegalArgumentException("音频资源不存在: " + audioId));
            
            // 3. 解析背景音乐参数
            String musicType = (String) backgroundMusic.get("type");
            String musicUrl = (String) backgroundMusic.get("url");
            Double volume = (Double) backgroundMusic.get("volume");
            Double startTime = (Double) backgroundMusic.get("startTime");
            Double endTime = (Double) backgroundMusic.get("endTime");
            
            if (musicType == null) {
                musicType = "ambient"; // 默认背景音乐类型
            }
            if (volume == null) {
                volume = 0.5; // 默认背景音乐音量（相对主音频）
            }
            
            // 4. 下载原始音频
            byte[] originalAudioData = downloadFromMinio(originalResource.getFileUrl());
            
            // 5. 下载背景音乐（模拟）
            byte[] backgroundMusicData;
            if (musicUrl != null && !musicUrl.isEmpty()) {
                log.info("下载背景音乐: url={}", musicUrl);
                backgroundMusicData = downloadFromUrl(musicUrl);
            } else {
                log.info("生成默认背景音乐: type={}", musicType);
                backgroundMusicData = generateBackgroundMusic(originalResource.getDuration(), musicType);
            }
            
            // 6. 混合背景音乐（模拟实现）
            log.info("混合背景音乐: type={}, volume={}, startTime={}, endTime={}", 
                    musicType, volume, startTime, endTime);
            byte[] mixedAudioData = mixBackgroundMusic(originalAudioData, backgroundMusicData, 
                    backgroundMusic, originalResource.getFormat());
            
            // 7. 上传处理后的音频到MinIO
            String fileName = generateAudioFileName(Map.of(
                "novelId", originalResource.getNovelId(),
                "operation", "background_music",
                "timestamp", System.currentTimeMillis(),
                "musicType", musicType
            ));
            
            String fileUrl = uploadToMinio(mixedAudioData, fileName, originalResource.getFormat());
            
            // 8. 创建新的音频资源记录
            AudioResource mixedResource = new AudioResource();
            mixedResource.setNovelId(originalResource.getNovelId());
            mixedResource.setFileName(fileName);
            mixedResource.setFileUrl(fileUrl);
            mixedResource.setOriginalText(originalResource.getOriginalText() + " [背景音乐: " + musicType + "]");
            mixedResource.setVoicePresetId(originalResource.getVoicePresetId());
            mixedResource.setCharacterId(originalResource.getCharacterId());
            mixedResource.setChapterId(originalResource.getChapterId());
            mixedResource.setEventId(originalResource.getEventId());
            mixedResource.setFormat(originalResource.getFormat());
            mixedResource.setFileSize((long) mixedAudioData.length);
            mixedResource.setDuration(originalResource.getDuration());
            mixedResource.setProvider(originalResource.getProvider());
            mixedResource.setStatus("COMPLETED");
            mixedResource.setPostProcessingStatus("BACKGROUND_MUSIC_ADDED");
            mixedResource.setQualityScore(calculateMixedQuality(originalResource.getQualityScore(), musicType, volume));
            
            // 添加标签
            List<String> tags = new ArrayList<>();
            if (originalResource.getTags() != null && !originalResource.getTags().trim().isEmpty()) {
                tags.addAll(Arrays.asList(originalResource.getTags().split(",")));
            }
            tags.add("background_music");
            tags.add("music_" + musicType);
            tags.add("bg_volume_" + volume);
            mixedResource.setTags(String.join(",", tags));
            
            AudioResource savedResource = audioResourceRepository.save(mixedResource);
            
            // 9. 记录背景音乐添加关系
            log.info("背景音乐添加完成: newAudioId={}, originalAudioId={}, musicType={}", 
                    savedResource.getId(), audioId, musicType);
            
            // 10. 返回结果
            return convertToDTO(savedResource);
            
        } catch (Exception e) {
            log.error("添加背景音乐失败", e);
            throw new RuntimeException("添加背景音乐失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> extractSubtitle(Long audioId, Map<String, Object> params) {
        // TODO: 实现提取音频字幕逻辑
        return null;
    }

    @Override
    public Map<String, Object> translateSubtitle(Long audioId, String targetLanguage, Map<String, Object> params) {
        // TODO: 实现翻译音频字幕逻辑
        return null;
    }

    @Override
    public Map<String, Object> analyzeAudioQuality(Long audioId) {
        // TODO: 实现分析音频质量逻辑
        return null;
    }

    @Override
    public AudioResourceDTO enhanceAudioQuality(Long audioId, Map<String, Object> params) {
        log.info("优化音频质量: audioId={}", audioId);
        // TODO: 实现优化音频质量逻辑
        return null;
    }

    @Override
    public AudioResourceDTO convertAudioFormat(Long audioId, String targetFormat, Map<String, Object> params) {
        log.info("转换音频格式: audioId={}, targetFormat={}", audioId, targetFormat);
        // TODO: 实现转换音频格式逻辑
        return null;
    }

    @Override
    public Map<String, Object> batchDeleteAudioResources(List<Long> audioIds) {
        log.info("批量删除音频资源: audioIds={}", audioIds);
        // TODO: 实现批量删除音频资源逻辑
        return null;
    }

    @Override
    public Map<String, Object> batchUpdateAudioTags(List<Long> audioIds, List<String> tags) {
        log.info("批量更新音频资源标签: audioIds={}, tags={}", audioIds, tags);
        // TODO: 实现批量更新音频资源标签逻辑
        return null;
    }

    @Override
    public Map<String, Object> batchUpdateAudioStatus(List<Long> audioIds, String status) {
        log.info("批量更新音频资源状态: audioIds={}, status={}", audioIds, status);
        // TODO: 实现批量更新音频资源状态逻辑
        return null;
    }

    @Override
    @Transactional
    public Map<String, Object> batchReviewAudioResources(List<Long> audioIds, String reviewStatus, String reviewComment) {
        log.info("批量审核音频资源: audioIds={}, reviewStatus={}, reviewComment={}", audioIds, reviewStatus, reviewComment);
        
        try {
            // 验证输入
            if (audioIds == null || audioIds.isEmpty()) {
                throw new IllegalArgumentException("音频ID列表不能为空");
            }
            
            // 验证审核状态
            List<String> validStatuses = Arrays.asList("PENDING", "APPROVED", "REJECTED", "NEEDS_REVISION");
            if (!validStatuses.contains(reviewStatus)) {
                throw new IllegalArgumentException("无效的审核状态: " + reviewStatus + "，有效状态: " + validStatuses);
            }
            
            // 获取当前用户ID（实际项目中应从安全上下文获取）
            Long reviewerId = 1L; // 模拟用户ID
            
            int successCount = 0;
            int failedCount = 0;
            List<Long> processedIds = new ArrayList<>();
            List<String> errorMessages = new ArrayList<>();
            
            // 批量更新审核状态
            for (Long audioId : audioIds) {
                try {
                    AudioResource audioResource = audioResourceRepository.findById(audioId)
                            .orElseThrow(() -> new IllegalArgumentException("音频资源不存在: " + audioId));
                    
                    // 更新审核信息
                    audioResource.setReviewStatus(reviewStatus);
                    audioResource.setReviewComment(reviewComment);
                    audioResource.setReviewedAt(java.time.LocalDateTime.now());
                    audioResource.setReviewerId(reviewerId);
                    
                    // 如果审核通过，可以自动发布
                    if ("APPROVED".equals(reviewStatus)) {
                        audioResource.setIsPublic(true);
                        audioResource.setStatus("PUBLISHED");
                    } else if ("REJECTED".equals(reviewStatus)) {
                        audioResource.setIsPublic(false);
                        audioResource.setStatus("REJECTED");
                    }
                    
                    audioResourceRepository.save(audioResource);
                    processedIds.add(audioId);
                    successCount++;
                    
                    // 触发审核事件
                    handleAudioReviewed(audioId, reviewStatus, reviewComment, reviewerId);
                    
                    log.info("音频审核成功: audioId={}, reviewStatus={}", audioId, reviewStatus);
                    
                } catch (Exception e) {
                    failedCount++;
                    errorMessages.add("音频ID " + audioId + ": " + e.getMessage());
                    log.error("音频审核失败: audioId={}", audioId, e);
                }
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", audioIds.size());
            result.put("successCount", successCount);
            result.put("failedCount", failedCount);
            result.put("processedIds", processedIds);
            result.put("reviewStatus", reviewStatus);
            result.put("reviewerId", reviewerId);
            result.put("reviewedAt", java.time.LocalDateTime.now().toString());
            
            if (!errorMessages.isEmpty()) {
                result.put("errors", errorMessages);
            }
            
            log.info("批量审核完成: 成功{}个，失败{}个", successCount, failedCount);
            return result;
            
        } catch (Exception e) {
            log.error("批量审核音频资源失败", e);
            throw new RuntimeException("批量审核音频资源失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> batchExportAudioResources(List<Long> audioIds, Map<String, Object> exportParams) {
        log.info("批量导出音频资源: audioIds={}, exportParams={}", audioIds, exportParams);
        
        try {
            // 验证输入
            if (audioIds == null || audioIds.isEmpty()) {
                throw new IllegalArgumentException("音频ID列表不能为空");
            }
            
            // 获取音频资源
            List<AudioResource> audioResources = new ArrayList<>();
            for (Long audioId : audioIds) {
                AudioResource audioResource = audioResourceRepository.findById(audioId)
                        .orElseThrow(() -> new IllegalArgumentException("音频资源不存在: " + audioId));
                audioResources.add(audioResource);
            }
            
            // 获取导出参数
            String exportFormat = exportParams != null ? (String) exportParams.get("format") : "zip";
            boolean includeMetadata = exportParams != null ? (Boolean) exportParams.getOrDefault("includeMetadata", true) : true;
            boolean includeWaveform = exportParams != null ? (Boolean) exportParams.getOrDefault("includeWaveform", false) : false;
            
            // 创建导出任务ID
            String exportTaskId = "export-" + System.currentTimeMillis() + "-" + audioIds.hashCode();
            
            // 模拟导出过程（实际实现应创建ZIP文件并上传到MinIO）
            log.info("开始批量导出 {} 个音频资源，格式: {}, 包含元数据: {}, 包含波形: {}", 
                    audioResources.size(), exportFormat, includeMetadata, includeWaveform);
            
            // 计算导出文件大小估计
            long estimatedSize = 0;
            double totalDuration = 0;
            for (AudioResource resource : audioResources) {
                estimatedSize += resource.getFileSize() != null ? resource.getFileSize() : 0;
                totalDuration += resource.getDuration() != null ? resource.getDuration() : 0;
            }
            
            // 创建导出结果
            Map<String, Object> result = new HashMap<>();
            result.put("exportTaskId", exportTaskId);
            result.put("audioCount", audioResources.size());
            result.put("estimatedSize", estimatedSize);
            result.put("totalDuration", totalDuration);
            result.put("exportFormat", exportFormat);
            result.put("includeMetadata", includeMetadata);
            result.put("includeWaveform", includeWaveform);
            result.put("status", "PROCESSING");
            result.put("startedAt", java.time.LocalDateTime.now().toString());
            result.put("estimatedCompletionTime", java.time.LocalDateTime.now().plusMinutes(5).toString());
            
            // 模拟下载URL
            String fileName = "audio-export-" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".zip";
            String downloadUrl = minioEndpoint + "/exports/" + fileName;
            result.put("fileName", fileName);
            result.put("downloadUrl", downloadUrl);
            result.put("message", "导出任务已创建，请稍后下载");
            
            // 异步处理导出任务（模拟）
            executorService.submit(() -> {
                try {
                    log.info("开始异步导出任务: {}", exportTaskId);
                    Thread.sleep(3000); // 模拟处理时间
                    
                    // 更新导出状态为完成
                    log.info("导出任务完成: {}", exportTaskId);
                    
                } catch (Exception e) {
                    log.error("导出任务失败: {}", exportTaskId, e);
                }
            });
            
            log.info("批量导出任务创建成功: taskId={}, audioCount={}", exportTaskId, audioResources.size());
            return result;
            
        } catch (Exception e) {
            log.error("批量导出音频资源失败", e);
            throw new RuntimeException("批量导出音频资源失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> batchImportAudioResources(List<Map<String, Object>> importData, Long novelId) {
        log.info("批量导入音频资源: novelId={}, count={}", novelId, importData.size());
        // TODO: 实现批量导入音频资源逻辑
        return null;
    }

    @Override
    public Map<String, Object> getAudioStatistics(Long novelId) {
        // TODO: 实现获取音频统计信息逻辑
        return null;
    }

    @Override
    public Map<String, Object> getUsageStatistics(Long novelId) {
        // TODO: 实现获取使用统计逻辑
        return null;
    }

    @Override
    public Map<String, Object> getCostStatistics(Long novelId) {
        // TODO: 实现获取成本统计逻辑
        return null;
    }

    @Override
    public Map<String, Object> getQualityStatistics(Long novelId) {
        // TODO: 实现获取质量统计逻辑
        return null;
    }

    @Override
    public Map<String, Object> getProviderStatistics(Long novelId) {
        // TODO: 实现获取提供商统计逻辑
        return null;
    }

    @Override
    public Map<String, Object> getTrendAnalysis(Long novelId, String timeRange) {
        // TODO: 实现获取趋势分析逻辑
        return null;
    }

    @Override
    public Map<String, Object> getHotspotAnalysis(Long novelId) {
        // TODO: 实现获取热点分析逻辑
        return null;
    }

    @Override
    public Map<String, Object> getRecommendationAnalysis(Long novelId) {
        // TODO: 实现获取推荐分析逻辑
        return null;
    }

    @Override
    public Map<String, Object> getTtsServiceStatus() {
        // TODO: 实现获取TTS服务状态逻辑
        return null;
    }

    @Override
    public boolean switchTtsProvider(String provider, Map<String, Object> config) {
        log.info("切换TTS服务提供商: provider={}", provider);
        // TODO: 实现切换TTS服务提供商逻辑
        return false;
    }

    @Override
    public Map<String, Object> testTtsConnection(String provider, Map<String, Object> config) {
        log.info("测试TTS服务连接: provider={}", provider);
        // TODO: 实现测试TTS服务连接逻辑
        return null;
    }

    @Override
    public Map<String, Object> getSystemConfiguration() {
        // TODO: 实现获取系统配置逻辑
        return null;
    }

    @Override
    public boolean updateSystemConfiguration(Map<String, Object> config) {
        log.info("更新系统配置");
        // TODO: 实现更新系统配置逻辑
        return false;
    }

    @Override
    public Map<String, Object> cleanupExpiredCache() {
        log.info("清理过期缓存");
        // TODO: 实现清理过期缓存逻辑
        return null;
    }

    @Override
    public Map<String, Object> cleanupTempFiles() {
        log.info("清理临时文件");
        // TODO: 实现清理临时文件逻辑
        return null;
    }

    @Override
    public Map<String, Object> backupAudioData(Map<String, Object> params) {
        log.info("备份音频数据");
        // TODO: 实现备份音频数据逻辑
        return null;
    }

    @Override
    public Map<String, Object> restoreAudioData(Map<String, Object> params) {
        log.info("恢复音频数据");
        // TODO: 实现恢复音频数据逻辑
        return null;
    }

    @Override
    public Map<String, Object> healthCheck() {
        // TODO: 实现系统健康检查逻辑
        return null;
    }



    @Override
    public String generateAudioFileName(Map<String, Object> params) {
        Long novelId = (Long) params.get("novelId");
        Long voicePresetId = (Long) params.get("voicePresetId");
        Long timestamp = (Long) params.get("timestamp");
        
        String prefix = "audio";
        if (novelId != null) {
            prefix += "-novel" + novelId;
        }
        if (voicePresetId != null) {
            prefix += "-voice" + voicePresetId;
        }
        
        String timeStr = timestamp != null ? 
                java.time.Instant.ofEpochMilli(timestamp).atZone(java.time.ZoneId.systemDefault())
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) :
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        String randomStr = java.util.UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("%s-%s-%s.mp3", prefix, timeStr, randomStr);
    }

    @Override
    public Map<String, Object> parseSsmlContent(String ssml) {
        // TODO: 实现解析SSML内容逻辑
        return null;
    }

    @Override
    public String generateSsmlContent(String text, Map<String, Object> params) {
        // TODO: 实现生成SSML内容逻辑
        return null;
    }

    @Override
    public String detectTextLanguage(String text) {
        // TODO: 实现检测文本语言逻辑
        return null;
    }

    @Override
    public List<String> splitLongText(String text, Integer maxLength, Integer overlap) {
        // TODO: 实现分割长文本逻辑
        return null;
    }

    @Override
    public String mergeShortTexts(List<String> texts, String separator) {
        // TODO: 实现合并短文本逻辑
        return null;
    }

    @Override
    public void handleAudioGenerationComplete(String taskId, AudioResource audioResource) {
        log.info("处理音频生成完成事件: taskId={}, audioResourceId={}", taskId, audioResource.getId());
        // TODO: 实现处理音频生成完成事件逻辑
    }

    @Override
    public void handleAudioGenerationFailed(String taskId, String errorMessage) {
        log.error("处理音频生成失败事件: taskId={}, errorMessage={}", taskId, errorMessage);
        // TODO: 实现处理音频生成失败事件逻辑
    }

    @Override
    public void handleAudioPlayed(Long audioId, Long userId) {
        log.info("处理音频播放事件: audioId={}, userId={}", audioId, userId);
        // TODO: 实现处理音频播放事件逻辑
    }

    @Override
    public void handleAudioDownloaded(Long audioId, Long userId) {
        log.info("处理音频下载事件: audioId={}, userId={}", audioId, userId);
        // TODO: 实现处理音频下载事件逻辑
    }

    @Override
    public void handleAudioReviewed(Long audioId, String reviewStatus, String reviewComment, Long reviewerId) {
        log.info("处理音频审核事件: audioId={}, reviewStatus={}, reviewerId={}", audioId, reviewStatus, reviewerId);
        
        try {
            // 1. 记录审核日志
            Map<String, Object> reviewLog = new HashMap<>();
            reviewLog.put("audioId", audioId);
            reviewLog.put("reviewStatus", reviewStatus);
            reviewLog.put("reviewComment", reviewComment);
            reviewLog.put("reviewerId", reviewerId);
            reviewLog.put("reviewedAt", java.time.LocalDateTime.now().toString());
            
            // 将审核日志存储到Redis或数据库
            String logKey = "audio:review:log:" + audioId;
            redisTemplate.opsForValue().set(logKey, objectMapper.writeValueAsString(reviewLog), 
                    java.time.Duration.ofHours(24));
            
            // 2. 更新审核统计
            String statsKey = "audio:review:stats:" + reviewerId;
            redisTemplate.opsForHash().increment(statsKey, reviewStatus.toLowerCase(), 1);
            redisTemplate.opsForHash().increment(statsKey, "total", 1);
            
            // 3. 发送通知（模拟实现）
            if ("APPROVED".equals(reviewStatus)) {
                log.info("音频审核通过通知: audioId={}, reviewerId={}", audioId, reviewerId);
                // 实际实现中应发送消息队列或推送通知
            } else if ("REJECTED".equals(reviewStatus)) {
                log.warn("音频审核拒绝通知: audioId={}, reviewerId={}, comment={}", audioId, reviewerId, reviewComment);
                // 实际实现中应发送消息队列或推送通知
            }
            
            // 4. 触发后续工作流（如审核通过后自动发布）
            if ("APPROVED".equals(reviewStatus)) {
                // 可以触发音频发布流程
                log.info("音频审核通过，触发发布流程: audioId={}", audioId);
            } else if ("NEEDS_REVISION".equals(reviewStatus)) {
                // 需要修改的音频，可以发送修改通知
                log.info("音频需要修改通知: audioId={}, comment={}", audioId, reviewComment);
            }
            
            log.info("音频审核事件处理完成: audioId={}", audioId);
            
        } catch (Exception e) {
            log.error("处理音频审核事件失败", e);
            // 不抛出异常，避免影响主流程
        }
    }

    @Override
    public void registerTaskCallback(String taskId, AudioTaskCallback callback) {
        log.info("注册任务回调: taskId={}", taskId);
        // TODO: 实现注册任务回调逻辑
    }

    @Override
    public void removeTaskCallback(String taskId) {
        log.info("移除任务回调: taskId={}", taskId);
        // TODO: 实现移除任务回调逻辑
    }
    
    // 私有辅助方法
    
    /**
     * 上传文件到MinIO
     */
    private String uploadToMinio(byte[] data, String fileName, String contentType) {
        try {
            // 检查MinIO连接
            minioClient.statObject(io.minio.StatObjectArgs.builder()
                    .bucket("novel-ai")
                    .object("test-connection")
                    .build());
        } catch (Exception e) {
            log.warn("MinIO连接测试失败，使用模拟URL");
            // 返回模拟URL
            return "http://localhost:9000/novel-ai/" + fileName;
        }
        
        try {
            // 上传文件到MinIO
            minioClient.putObject(io.minio.PutObjectArgs.builder()
                    .bucket("novel-ai")
                    .object(fileName)
                    .stream(new java.io.ByteArrayInputStream(data), data.length, -1)
                    .contentType(contentType)
                    .build());
            
            String url = minioEndpoint + "/novel-ai/" + fileName;
            log.info("文件上传到MinIO成功: url={}", url);
            return url;
            
        } catch (Exception e) {
            log.error("上传文件到MinIO失败", e);
            throw new RuntimeException("上传文件到MinIO失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 估计音频时长（秒）
     */
    @Override
    public Double estimateAudioDuration(String text, VoicePreset voicePreset) {
        // 简单估算：平均阅读速度约150字/分钟
        int wordCount = text.length();
        double minutes = wordCount / 150.0;
        return minutes * 60.0; // 转换为秒
    }
    
    /**
     * 估计音频成本
     */
    @Override
    public Double estimateAudioCost(String text, VoicePreset voicePreset) {
        // 调用TTS服务的成本估算
        TtsService.CostEstimation estimation = ttsService.estimateCost(text, voicePreset, Map.of());
        return estimation.getEstimatedCost();
    }
    
    /**
     * 验证音频参数
     */
    @Override
    public Map<String, Object> validateAudioParameters(Map<String, Object> params) {
        Map<String, Object> result = new java.util.HashMap<>();
        List<String> errors = new java.util.ArrayList<>();
        List<String> warnings = new java.util.ArrayList<>();
        
        // 验证文本长度
        String text = (String) params.get("text");
        if (text != null && text.length() > 5000) {
            warnings.add("文本长度超过推荐值(5000字符)，可能影响生成质量");
        }
        
        // 验证语音预设
        Long voicePresetId = (Long) params.get("voicePresetId");
        if (voicePresetId != null) {
            try {
                VoicePreset voicePreset = voicePresetRepository.findById(voicePresetId)
                        .orElseThrow(() -> new IllegalArgumentException("语音预设不存在"));
                TtsService.ValidationResult validation = ttsService.validateVoicePreset(voicePreset);
                if (!validation.isValid()) {
                    errors.add("语音预设验证失败: " + validation.getMessage());
                }
            } catch (Exception e) {
                errors.add("语音预设验证异常: " + e.getMessage());
            }
        }
        
        result.put("valid", errors.isEmpty());
        result.put("errors", errors);
        result.put("warnings", warnings);
        result.put("timestamp", System.currentTimeMillis());
        
        return result;
    }
    
    /**
     * 从MinIO下载音频文件
     */
    private byte[] downloadFromMinio(String fileUrl) {
        try {
            // 解析文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            
            // 从MinIO下载文件
            try (java.io.InputStream stream = minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket("novel-ai")
                            .object(fileName)
                            .build())) {
                
                java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                byte[] data = new byte[8192];
                int bytesRead;
                while ((bytesRead = stream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                buffer.flush();
                
                byte[] audioData = buffer.toByteArray();
                log.debug("从MinIO下载音频成功: fileName={}, size={} bytes", fileName, audioData.length);
                return audioData;
                
            }
        } catch (Exception e) {
            log.warn("MinIO下载失败，返回模拟数据: {}", e.getMessage());
            // 返回模拟音频数据
            return generateMockAudioData(2000); // 2秒模拟音频
        }
    }
    
    /**
     * 合并音频数据（模拟实现）
     * 实际实现应使用FFmpeg或音频处理库
     */
    private byte[] mergeAudioData(List<byte[]> audioFiles, Map<String, Object> params) {
        log.info("模拟合并{}个音频文件", audioFiles.size());
        
        // 模拟合并：简单地将所有数据连接起来（实际应进行音频格式处理）
        int totalSize = 0;
        for (byte[] audioData : audioFiles) {
            totalSize += audioData.length;
        }
        
        byte[] mergedData = new byte[totalSize];
        int offset = 0;
        for (byte[] audioData : audioFiles) {
            System.arraycopy(audioData, 0, mergedData, offset, audioData.length);
            offset += audioData.length;
        }
        
        log.info("音频合并完成: totalSize={} bytes", mergedData.length);
        return mergedData;
    }
    
    /**
     * 计算合并音频的质量评分
     */
    private Integer calculateQualityScore(List<AudioResource> audioResources) {
        if (audioResources == null || audioResources.isEmpty()) {
            return 70; // 默认评分
        }
        
        // 简单计算平均质量评分
        double totalScore = 0;
        int count = 0;
        for (AudioResource resource : audioResources) {
            if (resource.getQualityScore() != null) {
                totalScore += resource.getQualityScore();
                count++;
            }
        }
        
        if (count > 0) {
            return (int) Math.round(totalScore / count);
        } else {
            return 70;
        }
    }
    
    /**
     * 将AudioResource转换为AudioResourceDTO
     */
    private AudioResourceDTO convertToDTO(AudioResource audioResource) {
        if (audioResource == null) {
            return null;
        }
        
        AudioResourceDTO dto = new AudioResourceDTO();
        dto.setId(audioResource.getId());
        dto.setNovelId(audioResource.getNovelId());
        dto.setFileName(audioResource.getFileName());
        dto.setFileUrl(audioResource.getFileUrl());
        dto.setOriginalText(audioResource.getOriginalText());
        dto.setVoicePresetId(audioResource.getVoicePresetId());
        dto.setCharacterId(audioResource.getCharacterId());
        dto.setChapterId(audioResource.getChapterId());
        dto.setEventId(audioResource.getEventId());
        dto.setFormat(audioResource.getFormat());
        dto.setDuration(audioResource.getDuration());
        dto.setFileSize(audioResource.getFileSize());
        dto.setProvider(audioResource.getProvider());
        dto.setStatus(audioResource.getStatus());
        dto.setPostProcessingStatus(audioResource.getPostProcessingStatus());
        dto.setReviewStatus(audioResource.getReviewStatus());
        dto.setReviewComment(audioResource.getReviewComment());
        dto.setQualityScore(audioResource.getQualityScore());
        dto.setPlayCount(audioResource.getPlayCount());
        dto.setDownloadCount(audioResource.getDownloadCount());
        dto.setIsPublic(audioResource.getIsPublic());
        if (audioResource.getTags() != null && !audioResource.getTags().trim().isEmpty()) {
            dto.setTags(Arrays.asList(audioResource.getTags().split(",")));
        } else {
            dto.setTags(new java.util.ArrayList<>());
        }
        dto.setCreatedAt(audioResource.getCreatedAt());
        dto.setUpdatedAt(audioResource.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * 生成模拟音频数据（用于开发和测试）
     */
    private byte[] generateMockAudioData(int durationMs) {
        // 生成简单的WAV格式模拟数据
        // 实际实现应根据需要生成特定格式的音频
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        
        // 简单的WAV头（44字节）
        byte[] wavHeader = new byte[44];
        // 设置WAV头信息
        System.arraycopy("RIFF".getBytes(), 0, wavHeader, 0, 4);
        // 文件大小（数据大小 + 36）
        int fileSize = durationMs * 44 + 36; // 模拟计算
        wavHeader[4] = (byte) (fileSize & 0xFF);
        wavHeader[5] = (byte) ((fileSize >> 8) & 0xFF);
        wavHeader[6] = (byte) ((fileSize >> 16) & 0xFF);
        wavHeader[7] = (byte) ((fileSize >> 24) & 0xFF);
        System.arraycopy("WAVE".getBytes(), 0, wavHeader, 8, 4);
        
        // 添加静音数据
        byte[] silentData = new byte[durationMs * 44]; // 模拟音频数据
        for (int i = 0; i < silentData.length; i++) {
            silentData[i] = (byte) (i % 256);
        }
        
        try {
            baos.write(wavHeader);
            baos.write(silentData);
        } catch (Exception e) {
            log.error("生成模拟音频数据失败", e);
        }
        
        return baos.toByteArray();
    }
    
    /**
     * 剪辑音频数据（模拟实现）
     * 实际实现应使用FFmpeg或音频处理库进行精确时间剪辑
     */
    private byte[] clipAudioData(byte[] audioData, double startTime, double endTime, String format) {
        log.info("模拟剪辑音频: startTime={}秒, endTime={}秒, format={}", startTime, endTime, format);
        
        // 模拟剪辑：简单截取数据的一部分
        // 实际实现应根据音频格式、采样率、比特率等参数进行精确剪辑
        
        int startByte = (int) (startTime * 1000); // 简单模拟：每秒对应1000字节
        int endByte = (int) (endTime * 1000);
        
        if (startByte < 0) startByte = 0;
        if (endByte > audioData.length) endByte = audioData.length;
        if (startByte >= endByte) {
            startByte = 0;
            endByte = Math.min(audioData.length, 1000); // 默认1秒
        }
        
        int clippedLength = endByte - startByte;
        byte[] clippedData = new byte[clippedLength];
        System.arraycopy(audioData, startByte, clippedData, 0, clippedLength);
        
        log.info("音频剪辑完成: 原始大小={}字节, 剪辑后大小={}字节", audioData.length, clippedData.length);
        return clippedData;
    }
    
    /**
     * 调整音频音量数据（模拟实现）
     * 实际实现应使用音频处理库进行音量调整
     */
    private byte[] adjustVolumeData(byte[] audioData, double volume, String format) {
        log.info("模拟调整音频音量: volume={}倍, format={}", volume, format);
        
        // 模拟音量调整：简单处理数据
        // 实际实现应根据音频格式调整样本值
        
        byte[] adjustedData = new byte[audioData.length];
        
        // 简单模拟：根据音量系数调整字节值
        for (int i = 0; i < audioData.length; i++) {
            int byteValue = audioData[i] & 0xFF;
            int adjustedValue = (int) (byteValue * volume);
            if (adjustedValue > 255) adjustedValue = 255;
            if (adjustedValue < 0) adjustedValue = 0;
            adjustedData[i] = (byte) adjustedValue;
        }
        
        log.info("音频音量调整完成: 原始大小={}字节, 调整后大小={}字节", audioData.length, adjustedData.length);
        return adjustedData;
    }
    
    /**
     * 计算调整后的音频质量评分
     */
    private Integer calculateAdjustedQuality(Integer originalQuality, double volume) {
        if (originalQuality == null) {
            return 70;
        }
        
        // 音量调整可能影响质量
        double qualityFactor = 1.0;
        if (volume < 0.5 || volume > 2.0) {
            // 音量调整过大可能降低质量
            qualityFactor = 0.9;
        } else if (volume >= 0.8 && volume <= 1.2) {
            // 小范围调整对质量影响较小
            qualityFactor = 0.98;
        }
        
        int adjustedQuality = (int) (originalQuality * qualityFactor);
        return Math.max(0, Math.min(100, adjustedQuality));
    }
    
    /**
     * 应用音效到音频数据（模拟实现）
     * 实际实现应使用音频处理库应用各种音效
     */
    private byte[] applySoundEffect(byte[] audioData, Map<String, Object> soundEffect, String format) {
        String effectType = (String) soundEffect.get("type");
        Double intensity = (Double) soundEffect.get("intensity");
        
        if (effectType == null) effectType = "echo";
        if (intensity == null) intensity = 1.0;
        
        log.info("模拟应用音效: type={}, intensity={}, format={}", effectType, intensity, format);
        
        // 模拟音效处理：简单修改音频数据
        byte[] effectedData = new byte[audioData.length];
        
        switch (effectType.toLowerCase()) {
            case "echo":
                // 模拟回声效果
                for (int i = 0; i < audioData.length; i++) {
                    int echoIndex = i - 1000; // 简单回声延迟
                    if (echoIndex >= 0) {
                        int combined = (audioData[i] & 0xFF) + (int)((audioData[echoIndex] & 0xFF) * 0.3 * intensity);
                        effectedData[i] = (byte) Math.min(255, Math.max(0, combined));
                    } else {
                        effectedData[i] = audioData[i];
                    }
                }
                break;
                
            case "reverb":
                // 模拟混响效果
                for (int i = 0; i < audioData.length; i++) {
                    int reverbAmount = (int)((audioData[i] & 0xFF) * intensity * 1.2);
                    effectedData[i] = (byte) Math.min(255, Math.max(0, reverbAmount));
                }
                break;
                
            case "pitch":
                // 模拟音高调整
                Double pitchShift = (Double) soundEffect.get("pitchShift");
                if (pitchShift == null) pitchShift = 1.0;
                
                for (int i = 0; i < audioData.length; i++) {
                    int pitchIndex = (int)(i / pitchShift);
                    if (pitchIndex < audioData.length) {
                        effectedData[i] = audioData[pitchIndex];
                    } else {
                        effectedData[i] = 0;
                    }
                }
                break;
                
            default:
                // 默认：简单放大
                for (int i = 0; i < audioData.length; i++) {
                    int adjustedValue = (int)((audioData[i] & 0xFF) * intensity);
                    effectedData[i] = (byte) Math.min(255, Math.max(0, adjustedValue));
                }
        }
        
        log.info("音效应用完成: 原始大小={}字节, 处理后大小={}字节", audioData.length, effectedData.length);
        return effectedData;
    }
    
    /**
     * 计算添加音效后的音频质量评分
     */
    private Integer calculateEffectQuality(Integer originalQuality, String effectType, double intensity) {
        if (originalQuality == null) {
            return 70;
        }
        
        // 不同音效对质量的影响不同
        double qualityFactor = 1.0;
        
        switch (effectType.toLowerCase()) {
            case "echo":
                qualityFactor = (intensity > 2.0) ? 0.85 : 0.95;
                break;
            case "reverb":
                qualityFactor = (intensity > 1.5) ? 0.80 : 0.90;
                break;
            case "pitch":
                qualityFactor = 0.75; // 音高调整通常影响较大
                break;
            default:
                qualityFactor = 0.9;
        }
        
        int effectedQuality = (int) (originalQuality * qualityFactor);
        return Math.max(0, Math.min(100, effectedQuality));
    }
    
    /**
     * 从URL下载音频文件（模拟实现）
     */
    private byte[] downloadFromUrl(String url) {
        log.info("模拟从URL下载音频: url={}", url);
        
        // 模拟下载：返回模拟音频数据
        // 实际实现应使用HTTP客户端下载文件
        return generateMockAudioData(3000); // 3秒模拟音频
    }
    
    /**
     * 生成背景音乐数据（模拟实现）
     */
    private byte[] generateBackgroundMusic(Double duration, String musicType) {
        log.info("生成背景音乐: duration={}秒, type={}", duration, musicType);
        
        // 模拟生成背景音乐
        int durationMs = (duration != null) ? (int)(duration * 1000) : 10000; // 默认10秒
        return generateMockAudioData(durationMs);
    }
    
    /**
     * 混合背景音乐（模拟实现）
     * 实际实现应使用音频处理库进行精确混合
     */
    private byte[] mixBackgroundMusic(byte[] mainAudio, byte[] backgroundMusic, 
                                      Map<String, Object> params, String format) {
        log.info("模拟混合背景音乐: mainSize={}字节, bgSize={}字节, format={}", 
                mainAudio.length, backgroundMusic.length, format);
        
        // 简单混合：将两个音频数据交错混合
        int maxLength = Math.max(mainAudio.length, backgroundMusic.length);
        byte[] mixedData = new byte[maxLength];
        
        Double volume = (Double) params.get("volume");
        if (volume == null) volume = 0.5;
        
        for (int i = 0; i < maxLength; i++) {
            int mainValue = (i < mainAudio.length) ? (mainAudio[i] & 0xFF) : 0;
            int bgValue = (i < backgroundMusic.length) ? (backgroundMusic[i] & 0xFF) : 0;
            
            // 混合公式：主音频 + 背景音乐 * 音量系数
            int mixedValue = mainValue + (int)(bgValue * volume);
            if (mixedValue > 255) mixedValue = 255;
            if (mixedValue < 0) mixedValue = 0;
            
            mixedData[i] = (byte) mixedValue;
        }
        
        log.info("背景音乐混合完成: 混合后大小={}字节", mixedData.length);
        return mixedData;
    }
    
    /**
     * 计算添加背景音乐后的音频质量评分
     */
    private Integer calculateMixedQuality(Integer originalQuality, String musicType, double volume) {
        if (originalQuality == null) {
            return 70;
        }
        
        // 背景音乐添加可能影响质量
        double qualityFactor = 1.0;
        
        // 根据音乐类型调整质量因子
        switch (musicType.toLowerCase()) {
            case "ambient":
                qualityFactor = 0.95;
                break;
            case "orchestral":
                qualityFactor = 0.90;
                break;
            case "electronic":
                qualityFactor = 0.85;
                break;
            case "nature":
                qualityFactor = 0.92;
                break;
            default:
                qualityFactor = 0.9;
        }
        
        // 音量过大可能降低质量
        if (volume > 0.8) {
            qualityFactor *= 0.9;
        } else if (volume < 0.2) {
            qualityFactor *= 0.95; // 音量太小影响较小
        }
        
        int mixedQuality = (int) (originalQuality * qualityFactor);
        return Math.max(0, Math.min(100, mixedQuality));
    }
}