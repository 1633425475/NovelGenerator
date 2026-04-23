package com.laoji.novelai.entity.novel;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 主要人物实体
 */
@Entity
@Table(name = "novel_main_character")
@Data
@EqualsAndHashCode(callSuper = true)
public class MainCharacter extends BaseEntity {

    /**
     * 人物名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 性别：MALE-男, FEMALE-女, OTHER-其他
     */
    private String gender;

    /**
     * 年龄
     */
    private String age;

    /**
     * 身份/职业
     */
    private String identity;

    /**
     * 性格描述
     */
    @Column(columnDefinition = "TEXT")
    private String personality;

    /**
     * 背景故事
     */
    @Column(columnDefinition = "TEXT")
    private String background;

    /**
     * 目标与动机
     */
    @Column(columnDefinition = "TEXT")
    private String goals;

    /**
     * 成长弧线
     */
    @Column(columnDefinition = "TEXT")
    private String growthArc;

    /**
     * 关系描述（JSON格式存储与其他角色的关系）
     */
    @Column(columnDefinition = "JSON")
    private String relationships;

    /**
     * 大纲ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outline_id", nullable = false)
    private NovelOutline outline;
}