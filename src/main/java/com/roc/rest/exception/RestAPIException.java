package com.roc.rest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


/**
 * rest api 异常类
 */
@Getter
public class RestAPIException extends RuntimeException {

    protected HttpStatus httpStatus;

    protected String details;

    protected RestError restError;

    public RestAPIException(RestError restError, HttpStatus httpStatus) {
        this(restError.getMessage(), restError.getDetail(), httpStatus);
    }

    public RestAPIException(RestError restError, String details, HttpStatus httpStatus) {
        this(restError.getMessage(), details, httpStatus);
    }

    public RestAPIException(String message, String details, HttpStatus httpStatus) {
        this(message, details, httpStatus, null);
    }

    public RestAPIException(String message, String details, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.details = details;
        this.httpStatus = httpStatus;
    }
}