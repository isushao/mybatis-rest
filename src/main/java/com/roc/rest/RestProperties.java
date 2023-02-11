package com.roc.rest;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "roc.rest")
public class RestProperties {
    private String contextPath;

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
