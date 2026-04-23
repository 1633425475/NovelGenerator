package com.laoji.novelai.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础控制器类，提供通用响应方法
 */
public class BaseController {

    /**
     * 成功响应
     */
    protected ResponseEntity<Map<String, Object>> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    /**
     * 成功响应（无数据）
     */
    protected ResponseEntity<Map<String, Object>> success() {
        return success(null);
    }

    /**
     * 错误响应
     */
    protected ResponseEntity<Map<String, Object>> error(String message, int code) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        return ResponseEntity.status(code).body(result);
    }

    /**
     * 错误响应（默认500）
     */
    protected ResponseEntity<Map<String, Object>> error(String message) {
        return error(message, 500);
    }

    /**
     * 错误响应（带数据）
     */
    protected ResponseEntity<Map<String, Object>> error(String message, int code, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", data);
        return ResponseEntity.status(code).body(result);
    }

    /**
     * 未授权响应
     */
    protected ResponseEntity<Map<String, Object>> unauthorized(String message) {
        return error(message, 401);
    }

    /**
     * 禁止访问响应
     */
    protected ResponseEntity<Map<String, Object>> forbidden(String message) {
        return error(message, 403);
    }

    /**
     * 资源不存在响应
     */
    protected ResponseEntity<Map<String, Object>> notFound(String message) {
        return error(message, 404);
    }

    /**
     * 请求参数错误响应
     */
    protected ResponseEntity<Map<String, Object>> badRequest(String message) {
        return error(message, 400);
    }
}