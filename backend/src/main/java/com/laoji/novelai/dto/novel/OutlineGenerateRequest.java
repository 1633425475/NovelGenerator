package com.laoji.novelai.dto.novel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 大纲生成请求DTO
 */
@Data
public class OutlineGenerateRequest {

    /**
     * 创意描述
     */
    @NotBlank(message = "创意描述不能为空")
    private String idea;

    /**
     * 小说风格
     */
    @NotBlank(message = "小说风格不能为空")
    private String style;

    /**
     * 目标字数
     */
    @NotNull(message = "目标字数不能为空")
    private Integer targetWordCount;

    /**
     * 章节数量
     */
    @NotNull(message = "章节数量不能为空")
    private Integer chapterCount;

    /**
     * 参考作品（可选）
     */
    private String referenceWorks;

    /**
     * 是否异步生成
     */
    private Boolean async = false;
}