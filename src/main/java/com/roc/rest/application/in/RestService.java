package com.roc.rest.application.in;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface RestService {


    Object selectById(String entityName, String entityId);

    boolean createEntity(String entityName, String entityJson);

    List<?> listAll(String entityName);

    Object count(String entityName, String requestBodyJson);

    List<?> searchEntityListPost(String entityName, String requestBodyJson);

    IPage<?> pageEntitiesListPost(Page page, String entityName, String requestBodyJson);
}
