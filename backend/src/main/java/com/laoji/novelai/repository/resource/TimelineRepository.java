package com.laoji.novelai.repository.resource;

import com.laoji.novelai.entity.resource.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 时间线资源仓库接口
 */
@Repository
public interface TimelineRepository extends JpaRepository<Timeline, Long>, JpaSpecificationExecutor<Timeline> {

    /**
     * 根据小说ID查找时间线列表
     */
    List<Timeline> findByNovelIdAndDeletedFalseOrderByIsMainDescCreatedAtDesc(Long novelId);

    /**
     * 根据用户ID查找时间线列表
     */
    List<Timeline> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 查找主时间线
     */
    Timeline findByNovelIdAndIsMainTrueAndDeletedFalse(Long novelId);

    /**
     * 根据名称模糊搜索
     */
    List<Timeline> findByNameContainingAndDeletedFalse(String name);
}