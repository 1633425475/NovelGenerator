package com.laoji.novelai.entity.novel;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 章节实体
 */
@Entity
@Table(name = "novel_chapter")
@Data
@EqualsAndHashCode(callSuper = true)
public class Chapter extends BaseEntity {

    /**
     * 章节序号
     */
    @Column(nullable = false)
    private Integer chapterNumber;

    /**
     * 章节标题
     */
    @Column(nullable = false)
    private String title;

    /**
     * 章节概要
     */
    @Column(columnDefinition = "TEXT")
    private String summary;

    /**
     * 预计字数
     */
    private Integer estimatedWordCount;

    /**
     * 关键事件
     */
    @Column(columnDefinition = "TEXT")
    private String keyEvents;

    /**
     * 出场人物（JSON格式存储人物ID列表）
     */
    @Column(columnDefinition = "JSON")
    private String characters;

    /**
     * 重要场景
     */
    @Column(columnDefinition = "TEXT")
    private String importantScenes;

    /**
     * 大纲ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outline_id", nullable = false)
    private NovelOutline outline;
}