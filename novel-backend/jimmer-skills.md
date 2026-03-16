# Jimmer ORM AI Skills 教程文档

本文档旨在为 AI Agent 提供在项目中使用 Jimmer ORM 的标准规范和操作指南。Jimmer 是一种面向 Java/Kotlin 的关系型数据库 ORM 框架，支持强类型 SQL DSL 和 DTO 自动生成等高级特性。

## 1. 实体定义 (Entity Definition)

在 Jimmer 中，实体通过 `interface` 定义，并通过特定的注解来描述表结构和字段映射关系。

### 1.1 基本实体定义
创建实体接口并添加 `@Entity` 注解，通常建议实现一个包含通用字段（如创建时间、更新时间）的 `BaseEntity` 接口（如果项目中有提供）。

```java
import org.babyfish.jimmer.sql.*;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import java.time.LocalDateTime;

@Entity
@Table(name = "your_table_name") // 显式指定表名
public interface YourEntity {

    // 主键定义
    @Id
    @JsonConverter(LongToStringConverter.class) // 解决前端雪花算法精度丢失问题
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    long id();

    // 业务唯一键
    @Key
    String uniqueCode();

    // 普通字段定义（默认非空）
    String normalField();

    // 可空字段需要明确使用 @Nullable 或 @Null 注解
    @org.jetbrains.annotations.Nullable
    String nullableField();

    // 默认值和逻辑删除
    @Default("0")
    @LogicalDeleted("1")
    int delFlag();
}
```

### 1.2 关联关系定义
Jimmer 支持 `@OneToOne`, `@ManyToOne`, `@OneToMany`, `@ManyToMany`。

```java
@Entity
public interface ChildEntity {
    // 多对一关联
    @ManyToOne
    @JoinColumn(name = "parent_id")
    ParentEntity parent();
}

@Entity
public interface ParentEntity {
    // 一对多关联
    @OneToMany(mappedBy = "parent")
    List<ChildEntity> children();
}
```

---

## 2. DTO 定义 (DTO Definition)

Jimmer 提供了专属的 DTO 语言来自动生成 Java DTO 类，这些文件通常以 `.dto` 为后缀，放置在 `src/main/dto` 目录下的对应模块中。

### 2.1 DTO 文件语法
创建一个 `.dto` 文件，语法如下：

```text
# 导出目标实体
export com.your.package.po.YourEntity
# 指定生成的 DTO 类所在的包路径
    -> package com.your.package.dto

# 引入需要的 Java 依赖（如 Swagger 注解、Validation 注解等）
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

# 1. 定义 View 模型 (通常用于响应返回)
@Schema(description = "实体视图")
YourEntityView {
    # 包含的字段
    id
    @Schema(description = "唯一编码")
    uniqueCode
    normalField
    
    # 定义关联对象并指定其形状
    parent {
        id
        name
    }
}

# 2. 定义 Input 模型 (通常用于请求接收)
input YourEntityInput {
    @NotBlank(message = "编码不能为空")
    @Schema(description = "唯一编码")
    uniqueCode

    # 还可以定义实体中不存在的扩展字段
    captcha: String?
}
```
*提示：Jimmer 编译时会读取这些 `.dto` 文件，自动生成 `YourEntityView`、`YourEntityInput` 等 Java 类。*

---

## 3. JSqlClient 与 CRUD 操作

`JSqlClient` 是 Jimmer 操作数据库的核心入口，通常通过 Spring 的 `@Autowired` 注入。

### 3.1 数据查询 (Query)

基于强类型的 DSL 进行查询。Jimmer 会为每个实体生成一个以 `Table` 结尾的辅助类（例如 `YourEntityTable`）。

```java
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class YourRepository {

    @Autowired
    private JSqlClient sqlClient;
    
    private final YourEntityTable table = YourEntityTable.$;

    // 1. 根据 ID 查询实体
    public YourEntity getById(long id) {
        return sqlClient.findById(YourEntity.class, id);
    }

    // 2. 列表查询与条件过滤
    public List<YourEntity> listByCondition(String keyword) {
        return sqlClient.createQuery(table)
                // likeIf/eqIf 等方法在参数为空时会自动忽略该条件
                .where(table.normalField().likeIf(keyword))
                .orderBy(table.createTime().desc())
                .select(table)
                .execute();
    }

    // 3. 分页查询
    public PageResult<YourEntityView> pageQuery(int pageNum, int pageSize) {
        var page = sqlClient.createQuery(table)
                .where(table.delFlag().eq(0))
                .select(table.fetch(YourEntityView.class)) // 结合 Fetcher 使用 DTO 视图
                .fetchPage(pageNum - 1, pageSize); // Jimmer 的页码从 0 开始
                
        return new PageResult<>(pageNum, pageSize, page.getTotalRowCount(), page.getRows());
    }
}
```

### 3.2 Fetcher (对象抓取器) 的使用
Fetcher 用于精准控制查询时需要加载的关联数据及字段，避免 N+1 问题。

通常，在查询中配合 DTO 生成的类一起使用：
```java
// 直接使用 DTO 对应的 Class 作为 Fetcher
List<YourEntityView> list = sqlClient.createQuery(table)
        .select(table.fetch(YourEntityView.class))
        .execute();
```

或者使用自动生成的 Fetcher 辅助类（如 `YourEntityFetcher`）自定义查询形状：
```java
import org.babyfish.jimmer.sql.fetcher.Fetcher;

Fetcher<YourEntity> fetcher = YourEntityFetcher.$
        .allScalarFields()
        .parent(
            ParentEntityFetcher.$.name()
        );
        
List<YourEntity> list = sqlClient.createQuery(table)
        .select(table.fetch(fetcher))
        .execute();
```

### 3.3 数据新增与保存 (Save/Insert)

Jimmer 提倡使用 `Draft` API 构建对象，结合 `sqlClient.save()` 实现保存或更新（UPSERT）。

```java
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;

// 使用自动生成的 Draft 接口来构建实体
YourEntity entityToSave = YourEntityDraft.$.produce(draft -> {
    draft.setUniqueCode("CODE-001")
         .setNormalField("Hello Jimmer");
});

// 保存实体
// SaveMode.UPSERT (默认): 存在则更新，不存在则插入
// SaveMode.INSERT_ONLY: 仅插入
// SaveMode.UPDATE_ONLY: 仅更新
sqlClient.save(entityToSave, SaveMode.UPSERT);
```

### 3.4 数据更新 (Update)

除了使用 `save` 进行整体覆盖更新外，也可以使用 DSL 编写精确的 `Update` 语句。

```java
public void updateStatus(long id, String newStatus) {
    sqlClient.createUpdate(table)
            .set(table.normalField(), newStatus)
            .where(table.id().eq(id))
            .execute();
}
```

### 3.5 数据删除 (Delete)

```java
// 根据 ID 直接删除
sqlClient.deleteById(YourEntity.class, id);

// 使用 DSL 批量/条件删除
sqlClient.createDelete(table)
        .where(table.normalField().eq("Invalid"))
        .execute();
```

## 4. 总结

在 AI 辅助开发此项目时：
1. **优先使用 DTO 语言** (`.dto` 文件) 定义入参和出参，避免手动编写大量的请求体和响应体类。
2. **所有数据库交互** 均应通过注入的 `JSqlClient` 完成。
3. **构建实体对象** 必须使用对应实体生成的 `Draft.$.produce()` 闭包方法。
4. **条件查询** 善用强类型的 `Table` 辅助类（如 `YourEntityTable.$`）以及 `.eqIf()`, `.likeIf()` 等动态条件判断方法。
5. **查询结果映射** 尽量使用 `.select(table.fetch(YourDTOView.class))` 的方式直接获得 DTO 列表或分页结果。
