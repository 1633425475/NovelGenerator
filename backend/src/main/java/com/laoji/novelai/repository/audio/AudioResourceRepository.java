package com.laoji.novelai.repository.audio;

import com.laoji.novelai.entity.audio.AudioResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 音频资源数据访问接口
 */
@Repository
public interface AudioResourceRepository extends JpaRepository<AudioResource, Long> {

    /**
     * 根据小说ID查询音频资源列表
     */
    List<AudioResource> findByNovelId(Long novelId);

    /**
     * 根据小说ID和状态查询音频资源列表
     */
    List<AudioResource> findByNovelIdAndStatus(Long novelId, String status);

    /**
     * 根据语音预设ID查询音频资源列表
     */
    List<AudioResource> findByVoicePresetId(Long voicePresetId);

    /**
     * 根据角色ID查询音频资源列表
     */
    List<AudioResource> findByCharacterId(Long characterId);

    /**
     * 根据章节ID查询音频资源列表
     */
    List<AudioResource> findByChapterId(Long chapterId);

    /**
     * 根据事件ID查询音频资源列表
     */
    List<AudioResource> findByEventId(Long eventId);

    /**
     * 根据提供商查询音频资源列表
     */
    List<AudioResource> findByProvider(String provider);

    /**
     * 根据审核状态查询音频资源列表
     */
    List<AudioResource> findByReviewStatus(String reviewStatus);

    /**
     * 根据后处理状态查询音频资源列表
     */
    List<AudioResource> findByPostProcessingStatus(String postProcessingStatus);

    /**
     * 根据生成时间范围查询音频资源列表
     */
    List<AudioResource> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据小说ID和标签查询音频资源列表
     */
    @Query(value = "SELECT * FROM audio_resource WHERE novel_id = :novelId AND JSON_CONTAINS(tags, :tag)", nativeQuery = true)
    List<AudioResource> findByNovelIdAndTag(@Param("novelId") Long novelId, @Param("tag") String tag);

    /**
     * 根据小说ID和是否公开查询音频资源列表
     */
    List<AudioResource> findByNovelIdAndIsPublic(Long novelId, Boolean isPublic);

    /**
     * 根据小说ID查询音频资源并按播放次数降序排序
     */
    List<AudioResource> findByNovelIdOrderByPlayCountDesc(Long novelId);

    /**
     * 根据小说ID查询音频资源并按创建时间降序排序
     */
    List<AudioResource> findByNovelIdOrderByCreatedAtDesc(Long novelId);

    /**
     * 根据小说ID和质量评分查询音频资源列表
     */
    List<AudioResource> findByNovelIdAndQualityScoreGreaterThanEqual(Long novelId, Integer minScore);

    /**
     * 根据小说ID和音频格式查询音频资源列表
     */
    List<AudioResource> findByNovelIdAndFormat(Long novelId, String format);

    /**
     * 根据文本内容模糊查询音频资源列表
     */
    @Query("SELECT a FROM AudioResource a WHERE a.novelId = :novelId AND LOWER(a.originalText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AudioResource> findByNovelIdAndTextContaining(@Param("novelId") Long novelId, @Param("keyword") String keyword);

    /**
     * 根据文件名模糊查询音频资源列表
     */
    List<AudioResource> findByFileNameContaining(String keyword);

    /**
     * 统计指定小说中的音频资源数量
     */
    Long countByNovelId(Long novelId);

    /**
     * 统计指定小说中不同状态的音频资源数量
     */
    @Query("SELECT a.status, COUNT(a) FROM AudioResource a WHERE a.novelId = :novelId GROUP BY a.status")
    List<Object[]> countByNovelIdGroupByStatus(@Param("novelId") Long novelId);

    /**
     * 统计指定小说中不同提供商的音频资源数量
     */
    @Query("SELECT a.provider, COUNT(a) FROM AudioResource a WHERE a.novelId = :novelId GROUP BY a.provider")
    List<Object[]> countByNovelIdGroupByProvider(@Param("novelId") Long novelId);

    /**
     * 统计指定小说中的音频总时长
     */
    @Query("SELECT COALESCE(SUM(a.duration), 0) FROM AudioResource a WHERE a.novelId = :novelId AND a.status = 'COMPLETED'")
    Double sumDurationByNovelId(@Param("novelId") Long novelId);

    /**
     * 统计指定小说中的音频总文件大小
     */
    @Query("SELECT COALESCE(SUM(a.fileSize), 0) FROM AudioResource a WHERE a.novelId = :novelId AND a.status = 'COMPLETED'")
    Long sumFileSizeByNovelId(@Param("novelId") Long novelId);

    /**
     * 根据小说ID和生成时间降序查询最新的音频资源
     */
    List<AudioResource> findTop10ByNovelIdOrderByCreatedAtDesc(Long novelId);

    /**
     * 根据小说ID和播放次数降序查询最受欢迎的音频资源
     */
    List<AudioResource> findTop10ByNovelIdOrderByPlayCountDesc(Long novelId);

    /**
     * 根据小说ID查询失败的音频资源列表
     */
    @Query("SELECT a FROM AudioResource a WHERE a.novelId = :novelId AND a.status = 'FAILED'")
    List<AudioResource> findFailedAudiosByNovelId(@Param("novelId") Long novelId);
}