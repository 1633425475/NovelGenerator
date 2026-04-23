package com.laoji.novelai.entity.resource;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 地点资源实体
 */
@Entity
@Table(name = "resource_location")
@Data
@EqualsAndHashCode(callSuper = true)
public class Location extends BaseEntity {

    /**
     * 地点名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 别名
     */
    private String alias;

    /**
     * 地点类型：COUNTRY-国家, CITY-城市, MOUNTAIN-山脉, FOREST-森林, BUILDING-建筑等
     */
    private String type;

    /**
     * 地理位置描述
     */
    @Column(columnDefinition = "TEXT")
    private String geography;

    /**
     * 气候环境
     */
    @Column(columnDefinition = "TEXT")
    private String climate;

    /**
     * 建筑风格
     */
    @Column(columnDefinition = "TEXT")
    private String architecture;

    /**
     * 人文风貌
     */
    @Column(columnDefinition = "TEXT")
    private String culture;

    /**
     * 历史背景
     */
    @Column(columnDefinition = "TEXT")
    private String history;

    /**
     * 重要场所
     */
    @Column(columnDefinition = "TEXT")
    private String importantPlaces;

    /**
     * 政治状况
     */
    @Column(columnDefinition = "TEXT")
    private String politics;

    /**
     * 经济状况
     */
    @Column(columnDefinition = "TEXT")
    private String economy;

    /**
     * 与其他地点的关系（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String connections;

    /**
     * 地图坐标（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String coordinates;

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