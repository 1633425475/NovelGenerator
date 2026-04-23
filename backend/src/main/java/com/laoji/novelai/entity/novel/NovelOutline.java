package com.laoji.novelai.entity.novel;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 小说大纲实体
 */
@Entity
@Table(name = "novel_outline")
@Data
@EqualsAndHashCode(callSuper = true)
public class NovelOutline extends BaseEntity {

    /**
     * 大纲标题
     */
    @Column(nullable = false)
    private String title;

    /**
     * 小说创意描述
     */
    @Column(columnDefinition = "TEXT")
    private String idea;

    /**
     * 小说风格（玄幻、都市、科幻等）
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
     * 世界观设定（JSON格式存储）
     */
    @Column(columnDefinition = "JSON")
    private String worldBuilding;

    /**
     * 故事主线描述
     */
    @Column(columnDefinition = "TEXT")
    private String mainStory;

    /**
     * 状态：DRAFT-草稿, GENERATED-已生成, PUBLISHED-已发布
     */
    @Column(nullable = false)
    private String status = "DRAFT";

    /**
     * 版本号，格式：v1.0
     */
    private String version;

    /**
     * 父版本ID，用于版本管理
     */
    private Long parentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 章节列表
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "outline")
    private List<Chapter> chapters;

    /**
     * 主要人物列表
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "outline")
    private List<MainCharacter> mainCharacters;

    /**
     * 生成参数哈希，用于缓存
     */
    private String paramsHash;
}