package com.laoji.novelai.controller.audio;

import com.laoji.novelai.controller.BaseController;
import com.laoji.novelai.dto.audio.*;
import com.laoji.novelai.service.audio.AudioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 音频生成与管理控制器
 */
@RestController
@RequestMapping("/api/novels/{novelId}/audio")
@RequiredArgsConstructor
@Tag(name = "音频生成与管理", description = "语音生成、音频管理、语音预设等功能")
public class AudioController extends BaseController {

    private final AudioService audioService;

    // ========== 音频生成 ==========

    @PostMapping("/generate")
    @Operation(summary = "生成音频", description = "根据文本和语音预设生成音频文件")
    public ResponseEntity<?> generateAudio(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody AudioGenerateRequest request) {
        try {
            request.setNovelId(novelId);
            AudioGenerateResponse response = audioService.generateAudio(request);
            return success(response);
        } catch (Exception e) {
            return error("音频生成失败: " + e.getMessage());
        }
    }

    @PostMapping("/generate/batch")
    @Operation(summary = "批量生成音频", description = "批量生成多个音频文件，支持脚本结构")
    public ResponseEntity<?> batchGenerateAudio(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody BatchAudioGenerateRequest request) {
        try {
            request.setNovelId(novelId);
            BatchAudioGenerateResponse response = audioService.batchGenerateAudio(request);
            return success(response);
        } catch (Exception e) {
            return error("批量音频生成失败: " + e.getMessage());
        }
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "获取生成任务状态", description = "获取音频生成任务的当前状态")
    public ResponseEntity<?> getGenerationTaskStatus(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "任务ID") String taskId) {
        try {
            Map<String, Object> status = audioService.getGenerationTaskStatus(taskId);
            return success(status);
        } catch (Exception e) {
            return error("获取任务状态失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/tasks/{taskId}")
    @Operation(summary = "取消生成任务", description = "取消正在进行的音频生成任务")
    public ResponseEntity<?> cancelGenerationTask(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "任务ID") String taskId) {
        try {
            boolean result = audioService.cancelGenerationTask(taskId);
            if (result) {
                return success("任务取消成功");
            } else {
                return error("任务取消失败");
            }
        } catch (Exception e) {
            return error("取消任务失败: " + e.getMessage());
        }
    }

    @PostMapping("/tasks/{taskId}/retry")
    @Operation(summary = "重试生成任务", description = "重试失败的音频生成任务")
    public ResponseEntity<?> retryGenerationTask(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "任务ID") String taskId) {
        try {
            boolean result = audioService.retryGenerationTask(taskId);
            if (result) {
                return success("任务重试成功");
            } else {
                return error("任务重试失败");
            }
        } catch (Exception e) {
            return error("重试任务失败: " + e.getMessage());
        }
    }

    // ========== 语音预设管理 ==========

    @PostMapping("/voice-presets")
    @Operation(summary = "创建语音预设", description = "创建新的语音预设")
    public ResponseEntity<?> createVoicePreset(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @Valid @RequestBody VoicePresetDTO voicePresetDTO) {
        try {
            voicePresetDTO.setNovelId(novelId);
            VoicePresetDTO result = audioService.createVoicePreset(voicePresetDTO);
            return success(result);
        } catch (Exception e) {
            return error("创建语音预设失败: " + e.getMessage());
        }
    }

    @PutMapping("/voice-presets/{voicePresetId}")
    @Operation(summary = "更新语音预设", description = "更新指定语音预设的信息")
    public ResponseEntity<?> updateVoicePreset(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "语音预设ID") Long voicePresetId,
            @Valid @RequestBody VoicePresetDTO voicePresetDTO) {
        try {
            voicePresetDTO.setNovelId(novelId);
            VoicePresetDTO result = audioService.updateVoicePreset(voicePresetId, voicePresetDTO);
            return success(result);
        } catch (Exception e) {
            return error("更新语音预设失败: " + e.getMessage());
        }
    }

    @GetMapping("/voice-presets/{voicePresetId}")
    @Operation(summary = "获取语音预设详情", description = "获取指定语音预设的详细信息")
    public ResponseEntity<?> getVoicePreset(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "语音预设ID") Long voicePresetId) {
        try {
            VoicePresetDTO result = audioService.getVoicePreset(voicePresetId);
            if (!novelId.equals(result.getNovelId())) {
                return forbidden("该语音预设不属于当前小说");
            }
            return success(result);
        } catch (Exception e) {
            return notFound("语音预设不存在: " + e.getMessage());
        }
    }

    @DeleteMapping("/voice-presets/{voicePresetId}")
    @Operation(summary = "删除语音预设", description = "逻辑删除指定语音预设")
    public ResponseEntity<?> deleteVoicePreset(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "语音预设ID") Long voicePresetId) {
        try {
            boolean result = audioService.deleteVoicePreset(voicePresetId);
            if (result) {
                return success("语音预设删除成功");
            } else {
                return error("语音预设删除失败");
            }
        } catch (Exception e) {
            return error("删除语音预设失败: " + e.getMessage());
        }
    }

    @GetMapping("/voice-presets")
    @Operation(summary = "获取语音预设列表", description = "获取指定小说的语音预设列表，支持筛选")
    public ResponseEntity<?> listVoicePresets(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String ageGroup,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Boolean enabled) {
        try {
            Map<String, Object> filters = new HashMap<>();
            if (provider != null) filters.put("provider", provider);
            if (gender != null) filters.put("gender", gender);
            if (ageGroup != null) filters.put("ageGroup", ageGroup);
            if (language != null) filters.put("language", language);
            if (enabled != null) filters.put("enabled", enabled);
            
            List<VoicePresetDTO> result = audioService.listVoicePresets(novelId, filters);
            return success(result);
        } catch (Exception e) {
            return error("获取语音预设列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/voice-presets/search")
    @Operation(summary = "搜索语音预设", description = "搜索指定小说的语音预设")
    public ResponseEntity<?> searchVoicePresets(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam String keyword,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String gender) {
        try {
            Map<String, Object> filters = new HashMap<>();
            if (provider != null) filters.put("provider", provider);
            if (gender != null) filters.put("gender", gender);
            
            List<VoicePresetDTO> result = audioService.searchVoicePresets(novelId, keyword, filters);
            return success(result);
        } catch (Exception e) {
            return error("搜索语音预设失败: " + e.getMessage());
        }
    }

    @PostMapping("/voice-presets/{voicePresetId}/set-default")
    @Operation(summary = "设置默认语音预设", description = "设置指定语音预设为当前小说的默认预设")
    public ResponseEntity<?> setDefaultVoicePreset(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "语音预设ID") Long voicePresetId) {
        try {
            boolean result = audioService.setDefaultVoicePreset(novelId, voicePresetId);
            if (result) {
                return success("默认语音预设设置成功");
            } else {
                return error("默认语音预设设置失败");
            }
        } catch (Exception e) {
            return error("设置默认语音预设失败: " + e.getMessage());
        }
    }

    @GetMapping("/voice-presets/default")
    @Operation(summary = "获取默认语音预设", description = "获取当前小说的默认语音预设")
    public ResponseEntity<?> getDefaultVoicePreset(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            VoicePresetDTO result = audioService.getDefaultVoicePreset(novelId);
            return success(result);
        } catch (Exception e) {
            return notFound("默认语音预设不存在: " + e.getMessage());
        }
    }

    @PostMapping("/voice-presets/{voicePresetId}/test")
    @Operation(summary = "测试语音预设", description = "使用测试文本测试语音预设的效果")
    public ResponseEntity<?> testVoicePreset(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "语音预设ID") Long voicePresetId,
            @RequestParam(defaultValue = "这是一个测试文本，用于测试语音合成效果。") String testText) {
        try {
            AudioGenerateResponse result = audioService.testVoicePreset(voicePresetId, testText);
            return success(result);
        } catch (Exception e) {
            return error("测试语音预设失败: " + e.getMessage());
        }
    }

    @PostMapping("/voice-presets/{voicePresetId}/clone")
    @Operation(summary = "克隆语音预设", description = "通过语音克隆技术克隆语音预设")
    public ResponseEntity<?> cloneVoicePreset(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "源语音预设ID") Long voicePresetId,
            @RequestParam String cloneName,
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            String taskId = audioService.cloneVoicePreset(voicePresetId, cloneName, params);
            Map<String, Object> result = new HashMap<>();
            result.put("taskId", taskId);
            result.put("message", "语音克隆任务已启动");
            return success(result);
        } catch (Exception e) {
            return error("语音克隆失败: " + e.getMessage());
        }
    }

    @GetMapping("/voice-cloning/{taskId}")
    @Operation(summary = "获取语音克隆状态", description = "获取语音克隆任务的当前状态")
    public ResponseEntity<?> getVoiceCloningStatus(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "任务ID") String taskId) {
        try {
            Map<String, Object> status = audioService.getVoiceCloningStatus(taskId);
            return success(status);
        } catch (Exception e) {
            return error("获取语音克隆状态失败: " + e.getMessage());
        }
    }

    // ========== 音频资源管理 ==========

    @GetMapping("/resources/{audioId}")
    @Operation(summary = "获取音频资源详情", description = "获取指定音频资源的详细信息")
    public ResponseEntity<?> getAudioResource(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId) {
        try {
            AudioResourceDTO result = audioService.getAudioResource(audioId);
            if (!novelId.equals(result.getNovelId())) {
                return forbidden("该音频资源不属于当前小说");
            }
            return success(result);
        } catch (Exception e) {
            return notFound("音频资源不存在: " + e.getMessage());
        }
    }

    @DeleteMapping("/resources/{audioId}")
    @Operation(summary = "删除音频资源", description = "逻辑删除指定音频资源")
    public ResponseEntity<?> deleteAudioResource(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId) {
        try {
            boolean result = audioService.deleteAudioResource(audioId);
            if (result) {
                return success("音频资源删除成功");
            } else {
                return error("音频资源删除失败");
            }
        } catch (Exception e) {
            return error("删除音频资源失败: " + e.getMessage());
        }
    }

    @GetMapping("/resources")
    @Operation(summary = "获取音频资源列表", description = "获取指定小说的音频资源列表，支持筛选")
    public ResponseEntity<?> listAudioResources(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam(required = false) Long voicePresetId,
            @RequestParam(required = false) Long characterId,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reviewStatus) {
        try {
            Map<String, Object> filters = new HashMap<>();
            if (voicePresetId != null) filters.put("voicePresetId", voicePresetId);
            if (characterId != null) filters.put("characterId", characterId);
            if (chapterId != null) filters.put("chapterId", chapterId);
            if (status != null) filters.put("status", status);
            if (reviewStatus != null) filters.put("reviewStatus", reviewStatus);
            
            List<AudioResourceDTO> result = audioService.listAudioResources(novelId, filters);
            return success(result);
        } catch (Exception e) {
            return error("获取音频资源列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/resources/search")
    @Operation(summary = "搜索音频资源", description = "搜索指定小说的音频资源")
    public ResponseEntity<?> searchAudioResources(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam String keyword,
            @RequestParam(required = false) Long voicePresetId,
            @RequestParam(required = false) String format) {
        try {
            Map<String, Object> filters = new HashMap<>();
            if (voicePresetId != null) filters.put("voicePresetId", voicePresetId);
            if (format != null) filters.put("format", format);
            
            List<AudioResourceDTO> result = audioService.searchAudioResources(novelId, keyword, filters);
            return success(result);
        } catch (Exception e) {
            return error("搜索音频资源失败: " + e.getMessage());
        }
    }

    @PutMapping("/resources/{audioId}")
    @Operation(summary = "更新音频资源信息", description = "更新指定音频资源的元数据信息")
    public ResponseEntity<?> updateAudioResource(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId,
            @Valid @RequestBody AudioResourceDTO audioResourceDTO) {
        try {
            audioResourceDTO.setNovelId(novelId);
            AudioResourceDTO result = audioService.updateAudioResource(audioId, audioResourceDTO);
            return success(result);
        } catch (Exception e) {
            return error("更新音频资源失败: " + e.getMessage());
        }
    }

    @PostMapping("/resources/{audioId}/play")
    @Operation(summary = "播放音频", description = "播放音频并增加播放计数")
    public ResponseEntity<?> playAudio(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId) {
        try {
            AudioResourceDTO result = audioService.playAudio(audioId);
            return success(result);
        } catch (Exception e) {
            return error("播放音频失败: " + e.getMessage());
        }
    }

    @GetMapping("/resources/{audioId}/download")
    @Operation(summary = "下载音频", description = "下载音频文件")
    public ResponseEntity<?> downloadAudio(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId) {
        try {
            Map<String, Object> result = audioService.downloadAudio(audioId);
            return success(result);
        } catch (Exception e) {
            return error("下载音频失败: " + e.getMessage());
        }
    }

    @GetMapping("/resources/{audioId}/waveform")
    @Operation(summary = "获取音频波形数据", description = "获取音频的波形数据用于可视化")
    public ResponseEntity<?> getAudioWaveform(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId) {
        try {
            Map<String, Object> result = audioService.getAudioWaveform(audioId);
            return success(result);
        } catch (Exception e) {
            return error("获取音频波形数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/resources/{audioId}/spectrum")
    @Operation(summary = "获取音频频谱数据", description = "获取音频的频谱数据用于可视化")
    public ResponseEntity<?> getAudioSpectrum(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId) {
        try {
            Map<String, Object> result = audioService.getAudioSpectrum(audioId);
            return success(result);
        } catch (Exception e) {
            return error("获取音频频谱数据失败: " + e.getMessage());
        }
    }

    // ========== 音频处理 ==========

    @PostMapping("/process/merge")
    @Operation(summary = "合并音频", description = "合并多个音频文件为一个文件")
    public ResponseEntity<?> mergeAudios(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam List<Long> audioIds,
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            AudioResourceDTO result = audioService.mergeAudios(audioIds, params);
            return success(result);
        } catch (Exception e) {
            return error("合并音频失败: " + e.getMessage());
        }
    }

    @PostMapping("/process/{audioId}/trim")
    @Operation(summary = "剪辑音频", description = "剪辑音频的指定时间段")
    public ResponseEntity<?> trimAudio(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId,
            @RequestParam Double startTime,
            @RequestParam Double endTime) {
        try {
            AudioResourceDTO result = audioService.trimAudio(audioId, startTime, endTime);
            return success(result);
        } catch (Exception e) {
            return error("剪辑音频失败: " + e.getMessage());
        }
    }

    @PostMapping("/process/{audioId}/adjust-volume")
    @Operation(summary = "调整音频音量", description = "调整音频的音量大小")
    public ResponseEntity<?> adjustAudioVolume(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId,
            @RequestParam Double volume) {
        try {
            AudioResourceDTO result = audioService.adjustAudioVolume(audioId, volume);
            return success(result);
        } catch (Exception e) {
            return error("调整音频音量失败: " + e.getMessage());
        }
    }

    @PostMapping("/process/{audioId}/add-sound-effect")
    @Operation(summary = "添加音效", description = "为音频添加音效")
    public ResponseEntity<?> addSoundEffect(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId,
            @RequestBody Map<String, Object> soundEffect) {
        try {
            AudioResourceDTO result = audioService.addSoundEffect(audioId, soundEffect);
            return success(result);
        } catch (Exception e) {
            return error("添加音效失败: " + e.getMessage());
        }
    }

    @PostMapping("/process/{audioId}/add-background-music")
    @Operation(summary = "添加背景音乐", description = "为音频添加背景音乐")
    public ResponseEntity<?> addBackgroundMusic(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId,
            @RequestBody Map<String, Object> backgroundMusic) {
        try {
            AudioResourceDTO result = audioService.addBackgroundMusic(audioId, backgroundMusic);
            return success(result);
        } catch (Exception e) {
            return error("添加背景音乐失败: " + e.getMessage());
        }
    }

    @PostMapping("/process/{audioId}/extract-subtitle")
    @Operation(summary = "提取音频字幕", description = "从音频中提取字幕")
    public ResponseEntity<?> extractSubtitle(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId,
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            Map<String, Object> result = audioService.extractSubtitle(audioId, params);
            return success(result);
        } catch (Exception e) {
            return error("提取字幕失败: " + e.getMessage());
        }
    }

    @PostMapping("/process/{audioId}/translate-subtitle")
    @Operation(summary = "翻译音频字幕", description = "翻译音频字幕到指定语言")
    public ResponseEntity<?> translateSubtitle(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId,
            @RequestParam String targetLanguage,
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            Map<String, Object> result = audioService.translateSubtitle(audioId, targetLanguage, params);
            return success(result);
        } catch (Exception e) {
            return error("翻译字幕失败: " + e.getMessage());
        }
    }

    @GetMapping("/process/{audioId}/analyze-quality")
    @Operation(summary = "分析音频质量", description = "分析音频的质量指标")
    public ResponseEntity<?> analyzeAudioQuality(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId) {
        try {
            Map<String, Object> result = audioService.analyzeAudioQuality(audioId);
            return success(result);
        } catch (Exception e) {
            return error("分析音频质量失败: " + e.getMessage());
        }
    }

    @PostMapping("/process/{audioId}/enhance-quality")
    @Operation(summary = "优化音频质量", description = "优化音频的质量")
    public ResponseEntity<?> enhanceAudioQuality(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId,
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            AudioResourceDTO result = audioService.enhanceAudioQuality(audioId, params);
            return success(result);
        } catch (Exception e) {
            return error("优化音频质量失败: " + e.getMessage());
        }
    }

    @PostMapping("/process/{audioId}/convert-format")
    @Operation(summary = "转换音频格式", description = "转换音频到指定格式")
    public ResponseEntity<?> convertAudioFormat(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @PathVariable @Parameter(description = "音频资源ID") Long audioId,
            @RequestParam String targetFormat,
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            AudioResourceDTO result = audioService.convertAudioFormat(audioId, targetFormat, params);
            return success(result);
        } catch (Exception e) {
            return error("转换音频格式失败: " + e.getMessage());
        }
    }

    // ========== 批量操作 ==========

    @DeleteMapping("/batch/delete")
    @Operation(summary = "批量删除音频资源", description = "批量删除多个音频资源")
    public ResponseEntity<?> batchDeleteAudioResources(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam List<Long> audioIds) {
        try {
            Map<String, Object> result = audioService.batchDeleteAudioResources(audioIds);
            return success(result);
        } catch (Exception e) {
            return error("批量删除音频资源失败: " + e.getMessage());
        }
    }

    @PutMapping("/batch/update-tags")
    @Operation(summary = "批量更新音频资源标签", description = "批量更新多个音频资源的标签")
    public ResponseEntity<?> batchUpdateAudioTags(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam List<Long> audioIds,
            @RequestParam List<String> tags) {
        try {
            Map<String, Object> result = audioService.batchUpdateAudioTags(audioIds, tags);
            return success(result);
        } catch (Exception e) {
            return error("批量更新音频资源标签失败: " + e.getMessage());
        }
    }

    @PutMapping("/batch/update-status")
    @Operation(summary = "批量更新音频资源状态", description = "批量更新多个音频资源的状态")
    public ResponseEntity<?> batchUpdateAudioStatus(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam List<Long> audioIds,
            @RequestParam String status) {
        try {
            Map<String, Object> result = audioService.batchUpdateAudioStatus(audioIds, status);
            return success(result);
        } catch (Exception e) {
            return error("批量更新音频资源状态失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/review")
    @Operation(summary = "批量审核音频资源", description = "批量审核多个音频资源")
    public ResponseEntity<?> batchReviewAudioResources(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam List<Long> audioIds,
            @RequestParam String reviewStatus,
            @RequestParam(required = false) String reviewComment) {
        try {
            Map<String, Object> result = audioService.batchReviewAudioResources(audioIds, reviewStatus, reviewComment);
            return success(result);
        } catch (Exception e) {
            return error("批量审核音频资源失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/export")
    @Operation(summary = "批量导出音频资源", description = "批量导出多个音频资源")
    public ResponseEntity<?> batchExportAudioResources(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam List<Long> audioIds,
            @RequestBody(required = false) Map<String, Object> exportParams) {
        try {
            Map<String, Object> result = audioService.batchExportAudioResources(audioIds, exportParams);
            return success(result);
        } catch (Exception e) {
            return error("批量导出音频资源失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/import")
    @Operation(summary = "批量导入音频资源", description = "批量导入音频资源")
    public ResponseEntity<?> batchImportAudioResources(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestBody List<Map<String, Object>> importData) {
        try {
            Map<String, Object> result = audioService.batchImportAudioResources(importData, novelId);
            return success(result);
        } catch (Exception e) {
            return error("批量导入音频资源失败: " + e.getMessage());
        }
    }

    // ========== 统计与分析 ==========

    @GetMapping("/statistics")
    @Operation(summary = "获取音频统计信息", description = "获取音频相关的统计信息")
    public ResponseEntity<?> getAudioStatistics(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            Map<String, Object> result = audioService.getAudioStatistics(novelId);
            return success(result);
        } catch (Exception e) {
            return error("获取音频统计信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics/usage")
    @Operation(summary = "获取使用统计", description = "获取音频使用统计信息")
    public ResponseEntity<?> getUsageStatistics(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            Map<String, Object> result = audioService.getUsageStatistics(novelId);
            return success(result);
        } catch (Exception e) {
            return error("获取使用统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics/cost")
    @Operation(summary = "获取成本统计", description = "获取音频生成成本统计信息")
    public ResponseEntity<?> getCostStatistics(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            Map<String, Object> result = audioService.getCostStatistics(novelId);
            return success(result);
        } catch (Exception e) {
            return error("获取成本统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics/quality")
    @Operation(summary = "获取质量统计", description = "获取音频质量统计信息")
    public ResponseEntity<?> getQualityStatistics(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            Map<String, Object> result = audioService.getQualityStatistics(novelId);
            return success(result);
        } catch (Exception e) {
            return error("获取质量统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics/provider")
    @Operation(summary = "获取提供商统计", description = "获取TTS提供商使用统计信息")
    public ResponseEntity<?> getProviderStatistics(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            Map<String, Object> result = audioService.getProviderStatistics(novelId);
            return success(result);
        } catch (Exception e) {
            return error("获取提供商统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/analysis/trend")
    @Operation(summary = "获取趋势分析", description = "获取音频生成的趋势分析")
    public ResponseEntity<?> getTrendAnalysis(
            @PathVariable @Parameter(description = "小说ID") Long novelId,
            @RequestParam(defaultValue = "7d") String timeRange) {
        try {
            Map<String, Object> result = audioService.getTrendAnalysis(novelId, timeRange);
            return success(result);
        } catch (Exception e) {
            return error("获取趋势分析失败: " + e.getMessage());
        }
    }

    @GetMapping("/analysis/hotspot")
    @Operation(summary = "获取热点分析", description = "获取音频生成的热点分析")
    public ResponseEntity<?> getHotspotAnalysis(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            Map<String, Object> result = audioService.getHotspotAnalysis(novelId);
            return success(result);
        } catch (Exception e) {
            return error("获取热点分析失败: " + e.getMessage());
        }
    }

    @GetMapping("/analysis/recommendation")
    @Operation(summary = "获取推荐分析", description = "获取音频生成的推荐分析")
    public ResponseEntity<?> getRecommendationAnalysis(
            @PathVariable @Parameter(description = "小说ID") Long novelId) {
        try {
            Map<String, Object> result = audioService.getRecommendationAnalysis(novelId);
            return success(result);
        } catch (Exception e) {
            return error("获取推荐分析失败: " + e.getMessage());
        }
    }

    // ========== 系统管理 ==========

    @GetMapping("/system/tts-status")
    @Operation(summary = "获取TTS服务状态", description = "获取TTS服务的状态信息")
    public ResponseEntity<?> getTtsServiceStatus() {
        try {
            Map<String, Object> result = audioService.getTtsServiceStatus();
            return success(result);
        } catch (Exception e) {
            return error("获取TTS服务状态失败: " + e.getMessage());
        }
    }

    @PostMapping("/system/switch-provider")
    @Operation(summary = "切换TTS服务提供商", description = "切换TTS服务提供商")
    public ResponseEntity<?> switchTtsProvider(
            @RequestParam String provider,
            @RequestBody(required = false) Map<String, Object> config) {
        try {
            boolean result = audioService.switchTtsProvider(provider, config);
            if (result) {
                return success("TTS服务提供商切换成功");
            } else {
                return error("TTS服务提供商切换失败");
            }
        } catch (Exception e) {
            return error("切换TTS服务提供商失败: " + e.getMessage());
        }
    }

    @PostMapping("/system/test-connection")
    @Operation(summary = "测试TTS服务连接", description = "测试TTS服务连接")
    public ResponseEntity<?> testTtsConnection(
            @RequestParam String provider,
            @RequestBody(required = false) Map<String, Object> config) {
        try {
            Map<String, Object> result = audioService.testTtsConnection(provider, config);
            return success(result);
        } catch (Exception e) {
            return error("测试TTS服务连接失败: " + e.getMessage());
        }
    }

    @GetMapping("/system/configuration")
    @Operation(summary = "获取系统配置", description = "获取音频生成系统的配置信息")
    public ResponseEntity<?> getSystemConfiguration() {
        try {
            Map<String, Object> result = audioService.getSystemConfiguration();
            return success(result);
        } catch (Exception e) {
            return error("获取系统配置失败: " + e.getMessage());
        }
    }

    @PutMapping("/system/configuration")
    @Operation(summary = "更新系统配置", description = "更新音频生成系统的配置信息")
    public ResponseEntity<?> updateSystemConfiguration(
            @RequestBody Map<String, Object> config) {
        try {
            boolean result = audioService.updateSystemConfiguration(config);
            if (result) {
                return success("系统配置更新成功");
            } else {
                return error("系统配置更新失败");
            }
        } catch (Exception e) {
            return error("更新系统配置失败: " + e.getMessage());
        }
    }

    @PostMapping("/system/cleanup-cache")
    @Operation(summary = "清理过期缓存", description = "清理过期的音频缓存")
    public ResponseEntity<?> cleanupExpiredCache() {
        try {
            Map<String, Object> result = audioService.cleanupExpiredCache();
            return success(result);
        } catch (Exception e) {
            return error("清理过期缓存失败: " + e.getMessage());
        }
    }

    @PostMapping("/system/cleanup-temp")
    @Operation(summary = "清理临时文件", description = "清理临时音频文件")
    public ResponseEntity<?> cleanupTempFiles() {
        try {
            Map<String, Object> result = audioService.cleanupTempFiles();
            return success(result);
        } catch (Exception e) {
            return error("清理临时文件失败: " + e.getMessage());
        }
    }

    @PostMapping("/system/backup")
    @Operation(summary = "备份音频数据", description = "备份音频数据")
    public ResponseEntity<?> backupAudioData(
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            Map<String, Object> result = audioService.backupAudioData(params);
            return success(result);
        } catch (Exception e) {
            return error("备份音频数据失败: " + e.getMessage());
        }
    }

    @PostMapping("/system/restore")
    @Operation(summary = "恢复音频数据", description = "恢复音频数据")
    public ResponseEntity<?> restoreAudioData(
            @RequestBody Map<String, Object> params) {
        try {
            Map<String, Object> result = audioService.restoreAudioData(params);
            return success(result);
        } catch (Exception e) {
            return error("恢复音频数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/system/health")
    @Operation(summary = "系统健康检查", description = "检查音频生成系统的健康状态")
    public ResponseEntity<?> healthCheck() {
        try {
            Map<String, Object> result = audioService.healthCheck();
            return success(result);
        } catch (Exception e) {
            return error("系统健康检查失败: " + e.getMessage());
        }
    }
}