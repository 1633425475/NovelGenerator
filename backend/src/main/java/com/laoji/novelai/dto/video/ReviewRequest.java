package com.laoji.novelai.dto.video;

import java.util.Map;

/**
 * 审核请求
 */
public class ReviewRequest {

    private String result;
    private String comment;
    private Integer score;
    private Map<String, Object> metadata;

    // Getters and setters
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
