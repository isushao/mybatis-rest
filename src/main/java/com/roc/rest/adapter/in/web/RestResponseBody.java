package com.roc.rest.adapter.in.web;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RestResponseBody<T> implements Serializable {

    private final String message;
    private final Integer code;

    private final T data;
}
