package com.laoji.novelai.service.agent.creative;

import com.laoji.novelai.dto.agent.creative.*;
import com.laoji.novelai.entity.novel.Chapter;

import java.util.List;
import java.util.Map;

/**
 * 创作智能体服务接口
 */
public interface CreativeAgentService {

    // ========== 编剧智能体 ==========
    
    /**
     * 生成分镜脚本
     */
    Script generateScript(Chapter chapter, Map<String, Object> context);
    
    /**
     * 生成单个镜头
     */
    Shot generateShot(String sceneDescription, Map<String, Object> context);
    
    /**
     * 优化脚本
     */
    Script optimizeScript(Script script, Map<String, Object> params);
    
    // ========== 摄影师智能体 ==========
    
    /**
     * 为镜头生成图片参数
     */
    ImageGenerationParams generateImageParams(Shot shot, Map<String, Object> context);
    
    /**
     * 批量生成图片参数
     */
    List<ImageGenerationParams> batchGenerateImageParams(List<Shot> shots, Map<String, Object> context);
    
    /**
     * 优化图片参数
     */
    ImageGenerationParams optimizeImageParams(ImageGenerationParams params, Map<String, Object> feedback);
    
    // ========== 配音师智能体 ==========
    
    /**
     * 为脚本生成配音
     */
    VoiceOverResult generateVoiceOver(Script script, Map<String, Object> context);
    
    /**
     * 为单个对话生成配音
     */
    VoiceOverItem generateVoiceOverItem(Dialogue dialogue, Map<String, Object> context);
    
    /**
     * 匹配角色音色
     */
    Map<String, String> matchCharacterVoices(List<CharacterInfo> characters, Map<String, Object> context);
    
    // ========== 剪辑师智能体 ==========
    
    /**
     * 合成视频
     */
    VideoResult synthesizeVideo(VideoSynthesisRequest request);
    
    /**
     * 优化视频
     */
    VideoResult optimizeVideo(VideoResult video, Map<String, Object> params);
    
    /**
     * 生成视频剪辑建议
     */
    VideoEditingSuggestions generateEditingSuggestions(Script script, Map<String, Object> context);
    
    // ========== 智能体协作 ==========
    
    /**
     * 完整的视频生成流程
     */
    VideoProductionResult produceVideo(Chapter chapter, Map<String, Object> params);
    
    /**
     * 智能体协作完成任务
     */
    Map<String, Object> collaborate(List<String> agentTypes, String task, Map<String, Object> params);
}
