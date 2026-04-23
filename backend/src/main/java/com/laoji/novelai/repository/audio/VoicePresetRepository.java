package com.laoji.novelai.repository.audio;

import com.laoji.novelai.entity.audio.VoicePreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 语音预设数据访问接口
 */
@Repository
public interface VoicePresetRepository extends JpaRepository<VoicePreset, Long> {

    /**
     * 根据小说ID查询语音预设列表
     */
    List<VoicePreset> findByNovelId(Long novelId);

    /**
     * 根据小说ID和是否启用查询语音预设列表
     */
    List<VoicePreset> findByNovelIdAndEnabled(Long novelId, Boolean enabled);

    /**
     * 根据小说ID和是否默认查询语音预设
     */
    VoicePreset findByNovelIdAndIsDefault(Long novelId, Boolean isDefault);

    /**
     * 根据提供商查询语音预设列表
     */
    List<VoicePreset> findByProvider(String provider);

    /**
     * 根据小说ID和标签查询语音预设列表
     */
    @Query(value = "SELECT * FROM audio_voice_preset WHERE novel_id = :novelId AND JSON_CONTAINS(tags, :tag)", nativeQuery = true)
    List<VoicePreset> findByNovelIdAndTag(@Param("novelId") Long novelId, @Param("tag") String tag);

    /**
     * 根据角色ID查询关联的语音预设
     */
    List<VoicePreset> findByCharacterId(Long characterId);

    /**
     * 根据小说ID和性别查询语音预设列表
     */
    List<VoicePreset> findByNovelIdAndGender(Long novelId, String gender);

    /**
     * 根据小说ID和年龄段查询语音预设列表
     */
    List<VoicePreset> findByNovelIdAndAgeGroup(Long novelId, String ageGroup);

    /**
     * 根据小说ID和情绪查询语音预设列表
     */
    List<VoicePreset> findByNovelIdAndEmotion(Long novelId, String emotion);

    /**
     * 根据小说ID和语言查询语音预设列表
     */
    List<VoicePreset> findByNovelIdAndLanguage(Long novelId, String language);

    /**
     * 根据克隆状态查询语音预设列表
     */
    List<VoicePreset> findByCloneStatus(String cloneStatus);

    /**
     * 根据小说ID、启用状态和默认状态查询语音预设列表
     */
    List<VoicePreset> findByNovelIdAndEnabledAndIsDefault(Long novelId, Boolean enabled, Boolean isDefault);

    /**
     * 统计指定小说中的语音预设数量
     */
    Long countByNovelId(Long novelId);

    /**
     * 根据小说ID和名称模糊查询语音预设列表
     */
    List<VoicePreset> findByNovelIdAndNameContaining(Long novelId, String name);

    /**
     * 根据小说ID和描述模糊查询语音预设列表
     */
    @Query("SELECT v FROM VoicePreset v WHERE v.novelId = :novelId AND LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<VoicePreset> findByNovelIdAndDescriptionContaining(@Param("novelId") Long novelId, @Param("keyword") String keyword);

    /**
     * 根据小说ID查询使用次数最多的语音预设（按使用次数降序）
     */
    List<VoicePreset> findByNovelIdOrderByUsageCountDesc(Long novelId);
}