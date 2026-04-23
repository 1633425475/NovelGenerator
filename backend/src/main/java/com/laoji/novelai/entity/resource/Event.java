package com.laoji.novelai.entity.resource;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 事件资源实体
 */
@Entity
@Table(name = "resource_event")
@Data
@EqualsAndHashCode(callSuper = true)
public class Event extends BaseEntity {

    /**
     * 事件名称
     */
    @Column(nullable = false)
    private String title;

    /**
     * 事件类型：BATTLE-战斗, MEETING-会面, DISCOVERY-发现, BETRAYAL-背叛, ROMANCE-爱情等
     */
    private String type;

    /**
     * 事件描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 发生时间（时间点）
     */
    private String timePoint;

    /**
     * 持续时间
     */
    private String duration;

    /**
     * 发生地点（关联地点ID）
     */
    private Long locationId;

    /**
     * 参与人物（JSON数组格式，包含人物ID和角色）
     * 格式: [{"characterId": 1, "role": "主角"}, {"characterId": 2, "role": "反派"}]
     */
    @Column(columnDefinition = "JSON")
    private String participants;

    /**
     * 前因
     */
    @Column(columnDefinition = "TEXT")
    private String cause;

    /**
     * 过程
     */
    @Column(columnDefinition = "TEXT")
    private String process;

    /**
     * 结果
     */
    @Column(columnDefinition = "TEXT")
    private String result;

    /**
     * 影响
     */
    @Column(columnDefinition = "TEXT")
    private String impact;

    /**
     * 关键转折点
     */
    private Boolean isTurningPoint = false;

    /**
     * 关联的时间线ID
     */
    private Long timelineId;

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