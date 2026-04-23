package com.laoji.novelai.entity.resource;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物品资源实体
 */
@Entity
@Table(name = "resource_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemResource extends BaseEntity {

    /**
     * 物品名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 别名
     */
    private String alias;

    /**
     * 物品类型：WEAPON-武器, ARMOR-防具, ARTIFACT-神器, MEDICINE-丹药, SKILL-功法, MATERIAL-材料等
     */
    private String type;

    /**
     * 稀有度：COMMON-普通, RARE-稀有, EPIC-史诗, LEGENDARY-传说, MYTHIC-神话
     */
    private String rarity;

    /**
     * 物品描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 外观特征
     */
    @Column(columnDefinition = "TEXT")
    private String appearance;

    /**
     * 功能/能力
     */
    @Column(columnDefinition = "TEXT")
    private String abilities;

    /**
     * 使用方法
     */
    @Column(name = "`usage`", columnDefinition = "TEXT")
    private String usage;

    /**
     * 获取方式
     */
    @Column(columnDefinition = "TEXT")
    private String acquisition;

    /**
     * 历史背景
     */
    @Column(columnDefinition = "TEXT")
    private String history;

    /**
     * 当前持有者（关联人物ID）
     */
    private Long currentHolderId;

    /**
     * 前持有者列表（JSON数组格式）
     */
    @Column(columnDefinition = "JSON")
    private String previousHolders;

    /**
     * 关联地点（物品所在位置）
     */
    private Long locationId;

    /**
     * 关联事件（物品出现的事件）
     */
    private Long eventId;

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