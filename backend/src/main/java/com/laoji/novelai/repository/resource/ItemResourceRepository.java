package com.laoji.novelai.repository.resource;

import com.laoji.novelai.entity.resource.ItemResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 物品资源仓库接口
 */
@Repository
public interface ItemResourceRepository extends JpaRepository<ItemResource, Long>, JpaSpecificationExecutor<ItemResource> {

    /**
     * 根据小说ID查找物品列表
     */
    List<ItemResource> findByNovelIdAndDeletedFalseOrderByName(Long novelId);

    /**
     * 根据用户ID查找物品列表
     */
    List<ItemResource> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 根据名称模糊搜索
     */
    List<ItemResource> findByNameContainingAndDeletedFalse(String name);

    /**
     * 根据类型查找物品
     */
    List<ItemResource> findByTypeAndDeletedFalse(String type);

    /**
     * 根据稀有度查找物品
     */
    List<ItemResource> findByRarityAndDeletedFalse(String rarity);

    /**
     * 根据当前持有者查找物品
     */
    List<ItemResource> findByCurrentHolderIdAndDeletedFalse(Long holderId);

    /**
     * 根据标签查找物品
     */
    @Query(value = "SELECT * FROM resource_item WHERE JSON_CONTAINS(tags, ?1) AND is_deleted = false", nativeQuery = true)
    List<ItemResource> findByTag(String tag);
}