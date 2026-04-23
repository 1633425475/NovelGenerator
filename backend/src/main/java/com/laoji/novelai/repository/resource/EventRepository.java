package com.laoji.novelai.repository.resource;

import com.laoji.novelai.entity.resource.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 事件资源仓库接口
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    /**
     * 根据小说ID查找事件列表
     */
    List<Event> findByNovelIdAndDeletedFalseOrderByTimePoint(Long novelId);

    /**
     * 根据用户ID查找事件列表
     */
    List<Event> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 根据时间线ID查找事件列表
     */
    List<Event> findByTimelineIdAndDeletedFalseOrderByTimePoint(Long timelineId);

    /**
     * 根据标题模糊搜索
     */
    List<Event> findByTitleContainingAndDeletedFalse(String title);

    /**
     * 根据类型查找事件
     */
    List<Event> findByTypeAndDeletedFalse(String type);

    /**
     * 查找关键转折点事件
     */
    List<Event> findByIsTurningPointTrueAndDeletedFalse();

    /**
     * 根据标签查找事件
     */
    @Query(value = "SELECT * FROM resource_event WHERE JSON_CONTAINS(tags, ?1) AND is_deleted = false", nativeQuery = true)
    List<Event> findByTag(String tag);

    /**
     * 根据参与人物查找事件
     */
    @Query(value = "SELECT * FROM resource_event WHERE JSON_CONTAINS(participants, ?1) AND is_deleted = false", nativeQuery = true)
    List<Event> findByParticipant(String participantJson);
}