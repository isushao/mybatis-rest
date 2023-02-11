## 开始使用

#### 依赖和配置

pom添加依赖如下：

```xml
<dependency>
    <groupId>com.roc</groupId>
    <artifactId>rest</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

创建实体并添加注解：@RestEntity 或配置类添加 @RestEntityScan("com.roc.xxx.domain")

例如：

```java
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@TableName("user")
@RestEntity
public class User implements Serializable {
    private Long id;

    private String name;

    private String password;

    private String email;
}
```

组件dao层仍使用mybatis-plus，所以还需要添加相关依赖，并配置mapper扫描包等。

例如：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.2</version>
</dependency>

```
```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```
或配置@MapperScan("com.roc.xxx.dao")

自此即实现了通用的添加、详情、全列表、查询列表、分页列表、count等接口。



| **配置项**             | **描述**      | **备注**             |
|---------------------| ------------- | -------------------- |
| roc.rest.context-path | 通用api根路径 | 默认: /rest/entities |
|                     |               |                      |

#### 调用示例如下：

##### 详情

```shell
curl --location --request GET 'localhost:8080/rest/entities/user/1606181956003495938'
```

正确返回：

```json
{
    "message": null,
    "code": 200,
    "data": {
        "id": 1606181956003495938,
        "name": "roc1",
        "password": "1wrqrqwr",
        "email": "345@email.com"
    }
}
```

##### 所有列表

```shell
curl --location --request GET 'localhost:8080/rest/entities/user'
```

正确返回：

```json
{
    "message": null,
    "code": 200,
    "data": [
        {
            "id": 1606181956003495938,
            "name": "roc1",
            "password": "1wrqrqwr",
            "email": "345@email.com"
        },
        {
            "id": 1606184667637800961,
            "name": "roc2",
            "password": "q2e3",
            "email": "3tqt@email.com"
        },
        {
            "id": 1606184884063887361,
            "name": "roc3",
            "password": "dfagag",
            "email": "zdfzdsfzg@email.com"
        },
        {
            "id": 1607260542630248449,
            "name": "roc4",
            "password": "dfafafe",
            "email": "asfdaea@email.com"
        },
        {
            "id": 1607314583842410498,
            "name": "roc5",
            "password": "asfafa",
            "email": "adsfasf@email.com"
        },
        {
            "id": 1607328445153083394,
            "name": "roc6",
            "password": "ndfgn",
            "email": "dfng@email.com"
        }
    ]
}
```

##### 分页列表

- 默认设置了禁止使用count()查询，以免查询性能降低，total和pages使用count接口进行查询计算
- current、size默认为 1，10
- Requestbody可添加equal参数，暂未实现（like、between等）

```shell
curl --location --request POST 'localhost:8080/rest/entities/user/page' \
--header 'Content-Type: application/json' \
--data '{
"name": "roc6"
}'
```

正确返回：

```json
{
    "message": null,
    "code": 200,
    "data": {
        "records": [
            {
                "id": 1607328445153083394,
                "name": "roc6",
                "password": "ndfgn",
                "email": "dfng@email.com"
            }
        ],
        "total": 0,
        "size": 10,
        "current": 1,
        "orders": [],
        "optimizeCountSql": true,
        "searchCount": false,
        "countId": null,
        "maxLimit": null,
        "pages": 0
    }
}
```

##### 搜索

- Requestbody可添加equal参数，暂未实现（like、between等）

```shell
curl --location --request POST 'localhost:8080/rest/entities/user/search' \
--header 'Content-Type: application/json' \
--data '{
}'
```

正确返回：

```json
{
  "message": null,
  "code": 200,
  "data": [
    {
      "id": 1606181956003495938,
      "name": "roc1",
      "password": "1wrqrqwr",
      "email": "345@email.com"
    },
    {
      "id": 1606184667637800961,
      "name": "roc2",
      "password": "q2e3",
      "email": "3tqt@email.com"
    },
    {
      "id": 1606184884063887361,
      "name": "roc3",
      "password": "dfagag",
      "email": "zdfzdsfzg@email.com"
    },
    {
      "id": 1607260542630248449,
      "name": "roc4",
      "password": "dfafafe",
      "email": "asfdaea@email.com"
    },
    {
      "id": 1607314583842410498,
      "name": "roc5",
      "password": "asfafa",
      "email": "adsfasf@email.com"
    },
    {
      "id": 1607328445153083394,
      "name": "roc6",
      "password": "ndfgn",
      "email": "dfng@email.com"
    }
  ]
}
```

##### 搜索count

- Requestbody可添加equal参数，暂未实现（like、between等）

```shell
curl --location --request POST 'localhost:8080/rest/entities/user/search/count' \
--header 'Content-Type: application/json' \
--data '{
}'
```

正确返回：

```shell
{
    "message": null,
    "code": 200,
    "data": 6
}
```

##### 创建

```shell
curl --location --request POST 'localhost:8080/rest/entities/user' \
--header 'Content-Type: application/json' \
--data '{
    "name": "roc8",
    "email": "adfa@email.com"
}'
```

正确返回：

```json
{
  "message": null,
  "code": 200,
  "data": "创建成功!"
}
```

错误返回：

```json
{
    "message": "创建失败！",
    "code": 500,
    "data": null
}
```

##### 错误

错误示例：

```json
{
    "message": "未找到实体",
    "code": 500,
    "data": null
}
{
    "message": "未找到Mapper",
    "code": 500,
    "data": null
}
```

#### 扩展

##### date类型format

可通过如下配置，定义date类型字段的串行化格式。

```properties
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=GMT+8
```

**效果如下**：

配置前：

```json
{
    "message": null,
    "code": 200,
    "data": {
        "id": 1607942782154518530,
        "name": "test2",
        "password": "asfas",
        "email": "afafewaf@163.com",
        "createTime": "2022-12-28T03:00:00.000+00:00"
    }
}
```

配置后：

```json
{
    "message": null,
    "code": 200,
    "data": {
        "id": 1607942782154518530,
        "name": "test2",
        "password": "asfas",
        "email": "afafewaf@163.com",
        "createTime": "2022-12-28 11:00:00"
    }
}
```