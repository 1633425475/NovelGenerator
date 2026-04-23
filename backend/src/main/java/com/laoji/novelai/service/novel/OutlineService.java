package com.laoji.novelai.service.novel;

import com.laoji.novelai.dto.novel.OutlineGenerateRequest;
import com.laoji.novelai.dto.novel.OutlineGenerateResponse;

/**
 * 小说大纲服务接口
 */
public interface OutlineService {

    /**
     * 生成小说大纲
     */
    OutlineGenerateResponse generateOutline(OutlineGenerateRequest request);

    /**
     * 异步生成小说大纲
     */
    String generateOutlineAsync(OutlineGenerateRequest request);

    /**
     * 获取异步任务结果
     */
    OutlineGenerateResponse getAsyncResult(String taskId);

    /**
     * 获取大纲详情
     */
    OutlineGenerateResponse getOutlineDetail(Long outlineId);

    /**
     * 获取用户的大纲列表
     */
    Object getUserOutlines(Long userId);

    /**
     * 删除大纲
     */
    void deleteOutline(Long outlineId);

    /**
     * 更新大纲（创建新版本）
     */
    OutlineGenerateResponse updateOutline(Long outlineId, OutlineGenerateRequest request);

    /**
     * 获取大纲版本历史
     */
    Object getOutlineVersions(Long outlineId);
}