package com.laoji.novelai.repository.novel;

import com.laoji.novelai.entity.novel.MainCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 主要人物仓库接口
 */
@Repository
public interface MainCharacterRepository extends JpaRepository<MainCharacter, Long> {

    /**
     * 根据大纲ID查找人物列表
     */
    List<MainCharacter> findByOutlineIdAndDeletedFalse(Long outlineId);
}