package com.roc.rest.application.in.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.roc.rest.annotation.RestEntity;
import com.roc.rest.application.in.RestService;
import com.roc.rest.exception.RestAPIException;
import com.roc.rest.exception.RestError;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class RestServiceImpl implements RestService {

    private final ApplicationContext applicationContext;

    public RestServiceImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Optional<Class> loadEntityClass(String entityName) {
        Object entity;
        try {
            entity = applicationContext.getBean(entityName);
        } catch (BeansException e) {
            log.error("load entity: {} class is error: {}", entityName, e);
            throw new RestAPIException(RestError.LOAD_ENTITY_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Class<?> entityClass = entity.getClass();
        RestEntity annotation = entityClass.getAnnotation(RestEntity.class);
        if (annotation != null) {
            return Optional.of(entityClass);
        }
        return Optional.empty();
    }

    public Optional<BaseMapper<?>> loadEntityMapper(Optional<Class> entityClass) {
        if (entityClass.isPresent()) {
            BaseMapper<?> mapper;
            try {
                SqlSession sqlSession = SqlHelper.sqlSession(entityClass.get());
                mapper = SqlHelper.getMapper(entityClass.get(), sqlSession);
            } catch (Exception e) {
                log.error("load entity: {} mapper class is error: {}", entityClass.get().getSimpleName(), e);
                throw new RestAPIException(RestError.LOAD_MAPPER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return Optional.of(mapper);
        }
        return Optional.empty();
    }

    @Override
    public Object selectById(String entityName, String entityId) {
        Optional<BaseMapper<?>> mapperOptional = loadEntityMapper(loadEntityClass(entityName));
        if (mapperOptional.isPresent()) {
            BaseMapper<?> mapper = mapperOptional.get();
            return mapper.selectById(entityId);
        }
        return Strings.EMPTY;
    }

    @Override
    public boolean createEntity(String entityName, String entityJson) {
        Optional<Class> entityOption = loadEntityClass(entityName);
        Optional<BaseMapper<?>> mapperOptional = loadEntityMapper(entityOption);
        if (mapperOptional.isPresent() && entityOption.isPresent()) {
            try {
                BaseMapper<?> mapper = mapperOptional.get();
                return mapper.insert(createEntityFromJson(entityJson, entityOption.get())) > 0;
            } catch (RestAPIException e) {
                throw e;
            } catch (Exception e) {
                log.error("create entity: {}  is error: {}", entityName, e);
                throw new RestAPIException(RestError.CREAT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return false;
    }

    protected <T> T createEntityFromJson(String json, Class entityClass) {
        Object obj = JSONObject.parseObject(json, entityClass);
        // 实体验证
        Set<ConstraintViolation<@Valid Object>> validateSet = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(obj);
        if (!CollectionUtils.isEmpty(validateSet)) {
            String messages = validateSet.stream()
                    .map(ConstraintViolation::getMessage)
                    // 归约实现字符串合并
                    .reduce((m1, m2) -> m1 + ";" + m2)
                    .orElse("参数输入有误！");
            throw new RestAPIException(RestError.REQUEST_ERROR, messages, HttpStatus.BAD_REQUEST);
        }
        return (T) obj;
    }


    @Override
    public List<?> listAll(String entityName) {
        Optional<BaseMapper<?>> mapperOptional = loadEntityMapper(loadEntityClass(entityName));
        if (mapperOptional.isPresent()) {
            BaseMapper<?> mapper = mapperOptional.get();
            return mapper.selectList(Wrappers.emptyWrapper());
        }
        return Collections.emptyList();
    }

    @Override
    public List<?> searchEntityListPost(String entityName, String requestBodyJson) {
        Optional<Class> entityOption = loadEntityClass(entityName);
        Optional<BaseMapper<?>> mapperOptional = loadEntityMapper(entityOption);
        if (mapperOptional.isPresent() && entityOption.isPresent()) {
            return mapperOptional.get().selectList(
                    Wrappers.query(JSONObject.parseObject(requestBodyJson, (Type) entityOption.get()))
            );
        }
        return Collections.emptyList();
    }

    @Override
    public IPage<?> pageEntitiesListPost(Page page, String entityName, String requestBodyJson) {
        Optional<Class> entityOption = loadEntityClass(entityName);
        Optional<BaseMapper<?>> mapperOptional = loadEntityMapper(entityOption);
        if (mapperOptional.isPresent() && entityOption.isPresent()) {
            return mapperOptional.get().selectPage(page,
                    Wrappers.query(JSONObject.parseObject(requestBodyJson, (Type) entityOption.get()))
            );

        }
        return null;
    }

    @Override
    public Object count(String entityName, String requestBodyJson) {
        Optional<Class> entityOption = loadEntityClass(entityName);
        Optional<BaseMapper<?>> mapperOptional = loadEntityMapper(entityOption);
        if (mapperOptional.isPresent() && entityOption.isPresent()) {
            return mapperOptional.get().selectCount(
                    Wrappers.query(JSONObject.parseObject(requestBodyJson, (Type) entityOption.get()))
            );
        }
        return Strings.EMPTY;
    }
}
