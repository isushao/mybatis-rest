package com.roc.rest.exception;

import lombok.Getter;

@Getter
public enum RestError {
    LOAD_ENTITY_ERROR(100001, "未找到实体", "请检查@RestEntity注解"),
    LOAD_MAPPER_ERROR(100002, "未找到Mapper", "请检查@Mapper注解"),
    CREAT_ERROR(200001, "创建失败！", ""),
    REQUEST_ERROR(300001, "请求异常！", "");
    private final int code;
    private final String message;

    private final String detail;

    RestError(int code, String message, String detail) {
        this.code = code;
        this.message = message;
        this.detail = detail;
    }
}
