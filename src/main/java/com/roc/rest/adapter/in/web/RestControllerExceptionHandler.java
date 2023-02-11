package com.roc.rest.adapter.in.web;

import com.roc.rest.exception.RestAPIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice("com.roc.rest.adapter.in.web")
public class RestControllerExceptionHandler {
    @ExceptionHandler(RestAPIException.class)
    @ResponseBody
    public ResponseEntity<RestResponseBody<?>> handleRestAPIException(RestAPIException e) {
        if (e.getCause() == null) {
            log.info("RestAPIException: {}: {}", e.getMessage(), e.getDetails());
        } else {
            log.error("RestAPIException: {}: {}, cause: {}", e.getMessage(), e.getDetails(), e.getCause());
        }
        RestResponseBody<Object> errorInfo = RestResponseBody.builder()
                .message(e.getMessage() + "详情: " + e.getDetails())
                .code(e.getHttpStatus().value())
                .build();
        return new ResponseEntity<>(errorInfo, e.getHttpStatus());
    }
}
