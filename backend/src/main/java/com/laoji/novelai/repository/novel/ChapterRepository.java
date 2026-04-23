package com.laoji.novelai.repository.novel;

import com.laoji.novelai.entity.novel.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 章节仓库接口
 */
@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    /**
     * 根据大纲ID查找章节列表
     */
    List<Chapter> findByOutlineIdAndDeletedFalseOrderByChapterNumber(Long outlineId);
}