package com.laoji.novelai.controller.agent;

import com.laoji.novelai.dto.agent.creative.*;
import com.laoji.novelai.entity.novel.Chapter;
import com.laoji.novelai.service.agent.creative.CreativeAgentService;
import com.laoji.novelai.service.novel.OutlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 创作智能体控制器
 */
@RestController
@RequestMapping("/api/agent/creative")
@RequiredArgsConstructor
@Slf4j
public class CreativeAgentController {

    private final CreativeAgentService creativeAgentService;
    private final OutlineService outlineService;

    // ========== 编剧智能体 ==========

    /**
     * 生成分镜脚本
     */
    @PostMapping("/script/generate")
    public Script generateScript(@RequestBody Map<String, Object> request) {
        log.info("生成分镜脚本请求: {}", request);

        // 模拟章节数据
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitle("第一章 开始");
        chapter.setSummary("这是第一章的内容。\n\n主角走进了一个神秘的房间。\n\n他看到了一个奇怪的装置。");
        chapter.setChapterNumber(1);

        return creativeAgentService.generateScript(chapter, request);
    }

    /**
     * 生成单个镜头
     */
    @PostMapping("/shot/generate")
    public Shot generateShot(@RequestBody Map<String, Object> request) {
        log.info("生成单个镜头请求: {}", request);
        String sceneDescription = (String) request.get("sceneDescription");
        return creativeAgentService.generateShot(sceneDescription, request);
    }

    /**
     * 优化脚本
     */
    @PostMapping("/script/optimize")
    public Script optimizeScript(@RequestBody Map<String, Object> request) {
        log.info("优化脚本请求: {}", request);
        // 模拟脚本数据
        Script script = new Script();
        script.setId("script_1");
        script.setTitle("分镜脚本");
        return creativeAgentService.optimizeScript(script, request);
    }

    // ========== 摄影师智能体 ==========

    /**
     * 为镜头生成图片参数
     */
    @PostMapping("/image/params/generate")
    public ImageGenerationParams generateImageParams(@RequestBody Map<String, Object> request) {
        log.info("生成图片参数请求: {}", request);
        // 模拟镜头数据
        Shot shot = new Shot();
        shot.setId("shot_1");
        shot.setDescription("主角走进了一个神秘的房间");
        shot.setLighting("natural");
        return creativeAgentService.generateImageParams(shot, request);
    }

    /**
     * 批量生成图片参数
     */
    @PostMapping("/image/params/batch")
    public List<ImageGenerationParams> batchGenerateImageParams(@RequestBody Map<String, Object> request) {
        log.info("批量生成图片参数请求: {}", request);
        // 模拟镜头数据
        Shot shot1 = new Shot();
        shot1.setId("shot_1");
        shot1.setDescription("主角走进了一个神秘的房间");
        shot1.setLighting("natural");

        Shot shot2 = new Shot();
        shot2.setId("shot_2");
        shot2.setDescription("主角看到了一个奇怪的装置");
        shot2.setLighting("dim");

        return creativeAgentService.batchGenerateImageParams(List.of(shot1, shot2), request);
    }

    /**
     * 优化图片参数
     */
    @PostMapping("/image/params/optimize")
    public ImageGenerationParams optimizeImageParams(@RequestBody Map<String, Object> request) {
        log.info("优化图片参数请求: {}", request);
        // 模拟图片参数数据
        ImageGenerationParams params = new ImageGenerationParams();
        params.setId("image_params_1");
        params.setPrompt("高质量、详细的场景");
        return creativeAgentService.optimizeImageParams(params, request);
    }

    // ========== 配音师智能体 ==========

    /**
     * 为脚本生成配音
     */
    @PostMapping("/voice/generate")
    public VoiceOverResult generateVoiceOver(@RequestBody Map<String, Object> request) {
        log.info("生成配音请求: {}", request);
        // 模拟脚本数据
        Script script = new Script();
        script.setId("script_1");
        return creativeAgentService.generateVoiceOver(script, request);
    }

    /**
     * 为单个对话生成配音
     */
    @PostMapping("/voice/item/generate")
    public VoiceOverItem generateVoiceOverItem(@RequestBody Map<String, Object> request) {
        log.info("生成配音项目请求: {}", request);
        // 模拟对话数据
        Dialogue dialogue = new Dialogue();
        dialogue.setId("dialogue_1");
        dialogue.setCharacter("主角");
        dialogue.setText("这是一段对话内容");
        dialogue.setDuration("2s");
        return creativeAgentService.generateVoiceOverItem(dialogue, request);
    }

    /**
     * 匹配角色音色
     */
    @PostMapping("/voice/match")
    public Map<String, String> matchCharacterVoices(@RequestBody Map<String, Object> request) {
        log.info("匹配角色音色请求: {}", request);
        // 模拟角色数据
        CharacterInfo character1 = new CharacterInfo();
        character1.setName("主角");
        character1.setGender("男");

        CharacterInfo character2 = new CharacterInfo();
        character2.setName("女主角");
        character2.setGender("女");

        return creativeAgentService.matchCharacterVoices(List.of(character1, character2), request);
    }

    // ========== 剪辑师智能体 ==========

    /**
     * 合成视频
     */
    @PostMapping("/video/synthesize")
    public VideoResult synthesizeVideo(@RequestBody VideoSynthesisRequest request) {
        log.info("合成视频请求: {}", request);
        return creativeAgentService.synthesizeVideo(request);
    }

    /**
     * 优化视频
     */
    @PostMapping("/video/optimize")
    public VideoResult optimizeVideo(@RequestBody Map<String, Object> request) {
        log.info("优化视频请求: {}", request);
        // 模拟视频数据
        VideoResult video = new VideoResult();
        video.setId("video_1");
        video.setTitle("合成视频");
        return creativeAgentService.optimizeVideo(video, request);
    }

    /**
     * 生成视频剪辑建议
     */
    @PostMapping("/video/editing/suggestions")
    public VideoEditingSuggestions generateEditingSuggestions(@RequestBody Map<String, Object> request) {
        log.info("生成视频剪辑建议请求: {}", request);
        // 模拟脚本数据
        Script script = new Script();
        script.setId("script_1");
        return creativeAgentService.generateEditingSuggestions(script, request);
    }

    // ========== 智能体协作 ==========

    /**
     * 完整的视频生成流程
     */
    @PostMapping("/video/produce")
    public VideoProductionResult produceVideo(@RequestBody Map<String, Object> request) {
        log.info("完整的视频生成流程请求: {}", request);
        // 模拟章节数据
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitle("第一章 开始");
        chapter.setSummary("这是第一章的内容。\n\n主角走进了一个神秘的房间。\n\n他看到了一个奇怪的装置。");
        chapter.setChapterNumber(1);

        return creativeAgentService.produceVideo(chapter, request);
    }

    /**
     * 智能体协作完成任务
     */
    @PostMapping("/collaborate")
    public Map<String, Object> collaborate(@RequestBody Map<String, Object> request) {
        log.info("智能体协作请求: {}", request);
        List<String> agentTypes = (List<String>) request.get("agentTypes");
        String task = (String) request.get("task");
        return creativeAgentService.collaborate(agentTypes, task, request);
    }
}
