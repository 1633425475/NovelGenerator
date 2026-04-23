package com.laoji.novelai.repository.resource;

import com.laoji.novelai.entity.resource.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 地点资源仓库接口
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {

    /**
     * 根据小说ID查找地点列表
     */
    List<Location> findByNovelIdAndDeletedFalseOrderByName(Long novelId);

    /**
     * 根据用户ID查找地点列表
     */
    List<Location> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 根据名称模糊搜索
     */
    List<Location> findByNameContainingAndDeletedFalse(String name);

    /**
     * 根据类型查找地点
     */
    List<Location> findByTypeAndDeletedFalse(String type);

    /**
     * 根据标签查找地点
     */
    @Query(value = "SELECT * FROM resource_location WHERE JSON_CONTAINS(tags, ?1) AND is_deleted = false", nativeQuery = true)
    List<Location> findByTag(String tag);

    /**
     * 统计小说中的地点数量
     */
    Long countByNovelIdAndDeletedFalse(Long novelId);
}