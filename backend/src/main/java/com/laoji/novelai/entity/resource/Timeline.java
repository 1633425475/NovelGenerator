package com.laoji.novelai.entity.resource;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 时间线资源实体
 */
@Entity
@Table(name = "resource_timeline")
@Data
@EqualsAndHashCode(callSuper = true)
public class Timeline extends BaseEntity {

    /**
     * 时间线名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 时间单位：YEAR-年, MONTH-月, DAY-日, CUSTOM-自定义
     */
    private String timeUnit;

    /**
     * 起始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 时间点列表（JSON数组格式）
     * 格式: [{"time": "时间点", "title": "事件标题", "description": "事件描述", "relatedEntities": [{"type": "CHARACTER", "id": 1}]}]
     */
    @Column(columnDefinition = "JSON")
    private String timePoints;

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
     * 是否为主时间线
     */
    private Boolean isMain = false;

    /**
     * 标签（JSON数组格式）
     */
    @Column(columnDefinition = "JSON")
    private String tags;
}