package com.laoji.novelai.entity.resource;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人物资源实体（详细版）
 */
@Entity
@Table(name = "resource_character")
@Data
@EqualsAndHashCode(callSuper = true)
public class Character extends BaseEntity {

    /**
     * 人物名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 别名/称号
     */
    private String alias;

    /**
     * 性别：MALE-男, FEMALE-女, OTHER-其他
     */
    private String gender;

    /**
     * 年龄
     */
    private String age;

    /**
     * 种族/物种
     */
    private String race;

    /**
     * 身份/职业
     */
    private String identity;

    /**
     * 阵营/势力
     */
    private String faction;

    /**
     * 性格描述
     */
    @Column(columnDefinition = "TEXT")
    private String personality;

    /**
     * 外貌特征
     */
    @Column(columnDefinition = "TEXT")
    private String appearance;

    /**
     * 背景故事
     */
    @Column(columnDefinition = "TEXT")
    private String background;

    /**
     * 能力/技能
     */
    @Column(columnDefinition = "TEXT")
    private String abilities;

    /**
     * 弱点/缺点
     */
    @Column(columnDefinition = "TEXT")
    private String weaknesses;

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
     * 与其他角色的关系（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String relationships;

    /**
     * 重要语录
     */
    @Column(columnDefinition = "TEXT")
    private String quotes;

    /**
     * 关联的小说ID
     */
    private Long novelId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 状态：DRAFT-草稿, COMPLETED-已完成
     */
    @Column(nullable = false)
    private String status = "DRAFT";

    /**
     * 标签（JSON数组格式）
     */
    @Column(columnDefinition = "JSON")
    private String tags;
}