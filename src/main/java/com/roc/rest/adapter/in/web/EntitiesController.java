package com.roc.rest.adapter.in.web;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roc.rest.application.in.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Controller that performs CRUD entity operations
 */
@Slf4j
@RestController("rest_EntitiesController")
@RequestMapping(value = "${roc.rest.context-path:/rest/entities}")
public class EntitiesController {

    private final RestService restService;

    public EntitiesController(RestService restService) {
        this.restService = restService;
    }

    @GetMapping("/{entityName}/{entityId}")
    public ResponseEntity<Object> loadEntity(@PathVariable String entityName,
                                             @PathVariable String entityId) {
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.OK);
        RestResponseBody<Object> body = RestResponseBody.builder()
                .code(HttpStatus.OK.value())
                .data(restService.selectById(entityName, entityId))
                .build();
        return responseBuilder.body(body);
    }

    @GetMapping("/{entityName}")
    public ResponseEntity<Object> loadEntitiesList(@PathVariable String entityName) {
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.OK);
        RestResponseBody<Object> body = RestResponseBody.builder()
                .code(HttpStatus.OK.value())
                .data(restService.listAll(entityName))
                .build();
        return responseBuilder.body(body);
    }

    @PostMapping("/{entityName}/page")
    public ResponseEntity<Object> pageEntitiesListPost(@PathVariable String entityName,
                                                       @RequestBody String requestBodyJson,
                                                       @RequestParam(required = false) Integer current,
                                                       @RequestParam(required = false) Integer size,
                                                       @RequestParam(required = false) String sorts) {
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.OK);

        if (current == null) {
            current = 1;
        }
        if (size == null) {
            size = 10;
        }
        Page page = new Page(current, size);
        page.setSearchCount(false);

        setOrderItem(sorts, page);
        RestResponseBody<Object> body = RestResponseBody.builder()
                .code(HttpStatus.OK.value())
                .data(restService.pageEntitiesListPost(page, entityName, requestBodyJson))
                .build();
        return responseBuilder.body(body);
    }

    @PostMapping("/{entityName}/search")
    public ResponseEntity<Object> searchEntitiesListPost(@PathVariable String entityName,
                                                         @RequestBody String requestBodyJson) {
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.OK);
        RestResponseBody<Object> body = RestResponseBody.builder()
                .code(HttpStatus.OK.value())
                .data(restService.searchEntityListPost(entityName, requestBodyJson))
                .build();
        return responseBuilder.body(body);
    }

    @PostMapping("/{entityName}/search/count")
    public ResponseEntity<Object> countSearchEntitiesListPost(@PathVariable String entityName,
                                                              @RequestBody String requestBodyJson) {
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.OK);
        RestResponseBody<Object> body = RestResponseBody.builder()
                .code(HttpStatus.OK.value())
                .data(restService.count(entityName, requestBodyJson))
                .build();
        return responseBuilder.body(body);
    }

    @PostMapping("/{entityName}")
    public ResponseEntity<Object> createEntity(@RequestBody String entityJson,
                                               @PathVariable String entityName) {
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.OK);
        RestResponseBody<Object> body = RestResponseBody.builder()
                .code(HttpStatus.OK.value())
                .data(restService.createEntity(entityName, entityJson) ? "创建成功！" : "创建失败！")
                .build();
        return responseBuilder.body(body);
    }

    private void setOrderItem(String sorts, Page page) {
        if (StringUtils.isNotBlank(sorts) && !SqlInjectionUtils.check(sorts)) {

            String[] split = sorts.split(",");
            List<OrderItem> orders = new ArrayList<>(split.length);
            OrderItem orderItem;
            for (String sort : split) {
                String order = "";
                if (sort.startsWith("-") || sort.startsWith("+")) {
                    order = sort.substring(0, 1);
                    sort = sort.substring(1);
                }
                switch (order) {
                    case "-":
                        orderItem = new OrderItem(sort, false);
                        break;
                    case "+":
                    default:
                        orderItem = new OrderItem(sort, true);
                        break;
                }
                orders.add(orderItem);
            }
            page.setOrders(orders);

        }
    }
}
