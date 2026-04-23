package com.laoji.novelai.repository.resource;

import com.laoji.novelai.entity.resource.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 人物资源仓库接口
 */
@Repository
public interface CharacterRepository extends JpaRepository<Character, Long>, JpaSpecificationExecutor<Character> {

    /**
     * 根据小说ID查找人物列表
     */
    List<Character> findByNovelIdAndDeletedFalseOrderByName(Long novelId);

    /**
     * 根据用户ID查找人物列表
     */
    List<Character> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 根据名称模糊搜索
     */
    List<Character> findByNameContainingAndDeletedFalse(String name);

    /**
     * 根据标签查找人物
     */
    @Query(value = "SELECT * FROM resource_character WHERE JSON_CONTAINS(tags, ?1) AND is_deleted = false", nativeQuery = true)
    List<Character> findByTag(String tag);

    /**
     * 统计小说中的人物数量
     */
    Long countByNovelIdAndDeletedFalse(Long novelId);
}