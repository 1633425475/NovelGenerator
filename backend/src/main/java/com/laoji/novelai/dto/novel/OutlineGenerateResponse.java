package com.laoji.novelai.dto.novel;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 大纲生成响应DTO
 */
@Data
public class OutlineGenerateResponse {

    /**
     * 大纲ID
     */
    private Long id;

    /**
     * 大纲标题
     */
    private String title;

    /**
     * 小说创意描述
     */
    private String idea;

    /**
     * 小说风格
     */
    private String style;

    /**
     * 目标字数
     */
    private Integer targetWordCount;

    /**
     * 章节数量
     */
    private Integer chapterCount;

    /**
     * 世界观设定
     */
    private String worldBuilding;

    /**
     * 故事主线描述
     */
    private String mainStory;

    /**
     * 状态
     */
    private String status;

    /**
     * 版本号
     */
    private String version;

    /**
     * 章节列表
     */
    private List<ChapterDTO> chapters;

    /**
     * 主要人物列表
     */
    private List<MainCharacterDTO> mainCharacters;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 是否异步任务
     */
    private Boolean async = false;

    /**
     * 任务ID（如果是异步任务）
     */
    private String taskId;

    /**
     * 章节DTO
     */
    @Data
    public static class ChapterDTO {
        private Long id;
        private Integer chapterNumber;
        private String title;
        private String summary;
        private Integer estimatedWordCount;
    }

    /**
     * 主要人物DTO
     */
    @Data
    public static class MainCharacterDTO {
        private Long id;
        private String name;
        private String gender;
        private String age;
        private String identity;
        private String personality;
    }
}