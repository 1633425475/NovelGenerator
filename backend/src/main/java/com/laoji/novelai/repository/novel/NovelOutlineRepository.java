package com.laoji.novelai.repository.novel;

import com.laoji.novelai.entity.novel.NovelOutline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 小说大纲仓库接口
 */
@Repository
public interface NovelOutlineRepository extends JpaRepository<NovelOutline, Long>, JpaSpecificationExecutor<NovelOutline> {

    /**
     * 根据用户ID查找大纲列表
     */
    List<NovelOutline> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 根据参数哈希查找已生成的大纲（用于缓存）
     */
    Optional<NovelOutline> findByParamsHashAndDeletedFalse(String paramsHash);

    /**
     * 查找特定版本的大纲
     */
    @Query("SELECT o FROM NovelOutline o WHERE o.parentId = :parentId AND o.deleted = false ORDER BY o.createdAt DESC")
    List<NovelOutline> findVersionsByParentId(Long parentId);

    /**
     * 查找最新版本的大纲
     */
    @Query("SELECT o FROM NovelOutline o WHERE o.parentId = :parentId AND o.deleted = false ORDER BY o.createdAt DESC LIMIT 1")
    Optional<NovelOutline> findLatestVersionByParentId(Long parentId);
}