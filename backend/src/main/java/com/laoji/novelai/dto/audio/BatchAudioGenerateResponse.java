package com.laoji.novelai.dto.audio;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 批量音频生成响应数据传输对象
 */
@Data
public class BatchAudioGenerateResponse {
    
    private Boolean success;
    
    private String message;
    
    private String batchId;
    
    private String taskId;
    
    private String status;
    
    private Double overallProgress;
    
    private Long estimatedTotalTimeRemaining;
    
    private Long startTime;
    
    private Long endTime;
    
    private Long totalDuration;
    
    private List<BatchItemResult> itemResults;
    
    private BatchSummary summary;
    
    private String mergedAudioUrl;
    
    private String mergedSubtitleUrl;
    
    private String downloadUrl;
    
    private String compressDownloadUrl;
    
    private String errorCode;
    
    private String errorDetails;
    
    private Map<String, Object> statistics;
    
    private String[] warnings;
    
    private String[] suggestedActions;
    
    private String debugInfo;
    
    private String version;
    
    private Long timestamp;
    
    private String requestId;
    
    @Data
    public static class BatchItemResult {
        private Integer index;
        private ScriptItemDTO scriptItem;
        private Boolean success;
        private String audioUrl;
        private String subtitleUrl;
        private String errorMessage;
        private Long generationTime;
        private Double qualityScore;
        private String status;
        private Double progress;
        private String taskId;
        private Long audioId;
        private String provider;
        private Double cost;
        private String currency;
        private Boolean isCached;
        private String cacheKey;
        private String[] warnings;
        private Map<String, Object> metadata;
    }
    
    @Data
    public static class BatchSummary {
        private Integer totalItems;
        private Integer completedItems;
        private Integer failedItems;
        private Integer pendingItems;
        private Integer processingItems;
        private Double successRate;
        private Double averageQualityScore;
        private Long totalGenerationTime;
        private Long totalFileSize;
        private Double totalDuration;
        private Double totalCost;
        private String currency;
        private Integer cacheHits;
        private Integer cacheMisses;
        private Double cacheHitRate;
        private String mostUsedProvider;
        private String mostUsedVoice;
        private Map<String, Integer> statusDistribution;
        private Map<String, Integer> providerDistribution;
        private Map<String, Double> qualityDistribution;
        private Long estimatedCompletionTime;
        private Boolean isComplete;
        private Boolean hasErrors;
        private Boolean hasWarnings;
        private String[] errorTypes;
        private String[] warningTypes;
        private String overallStatus;
        private String recommendation;
    }
}