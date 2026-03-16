# AI Skills: Jimmer Entity Generation from SQL DDL

本文档旨在指导 AI 助手如何根据 SQL DDL 语句生成符合当前项目规范的 Jimmer 实体接口（Java）。

## 1. 基础规范

- **文件类型**：Jimmer 实体是 Java `interface`，而非 `class`。
- **文件位置**：所有实体文件位于 `novel-pojo` 模块的 `src/main/java/com/novel/po` 目录下。
- **包名规则**：根据表名前缀划分子包。
  - `book_*` -> `com.novel.po.book`
  - `user_*` -> `com.novel.po.user`
  - `news_*` -> `com.novel.po.news`
  - 其他 -> `com.novel.po.common` 或直接在 `com.novel.po` 下。

## 2. 类型映射规则 (Type Mapping)

| SQL Type | Java Type | 备注 |
| :--- | :--- | :--- |
| `bigint` | `long` | 主键通常使用 `long` |
| `tinyint` | `int` | 状态、标志位等 |
| `int` | `int` | |
| `varchar`, `char`, `text` | `String` | |
| `datetime`, `timestamp` | `LocalDateTime` | 需导入 `java.time.LocalDateTime` |
| `decimal` | `BigDecimal` | |

## 3. 注解与字段规范

### 3.1 类级别注解
- **@Entity**：必须添加。
- **@Table(name = "...")**：显式指定表名。
- **Javadoc**：必须将 SQL 中的 `comment` 转换为类的 Javadoc。

### 3.2 主键 (Primary Key)
- 必须添加 `@Id`。
- 必须添加 `@GeneratedValue(strategy = GenerationType.IDENTITY)` (如果是自增)。
- 必须添加 `@JsonConverter(LongToStringConverter.class)` 以防止前端精度丢失。

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@JsonConverter(LongToStringConverter.class)
long id();
```

### 3.3 唯一约束 (Unique Constraints)
- 如果字段在 DDL 中被包含在 `unique` 约束或索引中，必须添加 `@Key` 注解。

### 3.4 关联关系 (Associations)
- **多对一 (@ManyToOne)**：
  - 当检测到字段名为 `xxx_id` 时，通常代表外键。
  - 生成一个关联对象字段：`@ManyToOne Xxx xxx();`
  - (可选) 生成一个视图字段：`@IdView Long xxxId();` (注意类型通常为包装类 `Long` 以支持 null，除非明确 not null)。
  - **示例**：`category_id` ->
    ```java
    @ManyToOne
    BookCategory category();
    
    @IdView
    Long categoryId();
    ```

### 3.5 字段属性
- **非空性**：
  - SQL `NOT NULL` -> 默认（无注解）。
  - SQL `NULL` -> 添加 `@org.jetbrains.annotations.Nullable`。
- **逻辑删除**：
  - 字段 `del_flag` ->
    ```java
    @Default("0")
    @LogicalDeleted("1")
    int delFlag();
    ```
- **特殊命名**：
  - 如果 SQL 字段名包含 `is_` 前缀（如 `is_vip`），Java 字段建议去除 `is` 前缀并使用 `@Column` 映射（如 `vipState`），或者保持 `isVip`。当前项目倾向于使用 `int` 类型表示布尔状态。
  - 示例：`is_vip` -> `@Column(name = "is_vip") int vipState();`

### 3.6 通用字段 (BaseEntity)
- 如果表中包含 `create_time` 和 `update_time`，可以考虑让接口继承 `BaseEntity`（如果项目中有定义且字段类型一致）。
- 否则，显式定义这两个字段：
  ```java
  @Nullable
  LocalDateTime createTime();
  
  @Nullable
  LocalDateTime updateTime();
  ```

## 4. 示例

**输入 (SQL DDL):**
```sql
create table book_info (
    id bigint unsigned auto_increment comment '主键' primary key,
    category_id bigint unsigned null comment '类别ID',
    book_name varchar(50) not null comment '小说名',
    author_name varchar(50) not null comment '作家名',
    is_vip tinyint unsigned default '0' not null comment '是否收费;1-收费 0-免费',
    create_time datetime default(now()) null comment '创建时间',
    del_flag tinyint default 0 not null,
    constraint uk_bookName_authorName unique (book_name, author_name)
) comment '小说信息';
```

**输出 (Jimmer Entity):**
```java
package com.novel.po.book;

import com.novel.po.BaseEntity; // 如果选择继承
import org.babyfish.jimmer.sql.*;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;

/**
 * 小说信息
 */
@Entity
@Table(name = "book_info")
public interface BookInfo {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonConverter(LongToStringConverter.class)
    long id();

    /**
     * 类别ID
     */
    @IdView
    Long categoryId();

    /**
     * 类别关联
     * (推断出 category_id 指向 book_category 表，对应 BookCategory 实体)
     */
    @ManyToOne
    @Nullable
    BookCategory category();

    /**
     * 小说名
     */
    @Key
    String bookName();

    /**
     * 作家名
     */
    @Key
    String authorName();

    /**
     * 是否收费;1-收费 0-免费
     */
    @Column(name = "is_vip")
    int vipState();

    /**
     * 创建时间
     */
    @Nullable
    LocalDateTime createTime();

    @Default("0")
    @LogicalDeleted("1")
    int delFlag();
}
```

## 5. 常见依赖导入

```java
import org.babyfish.jimmer.sql.*;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.math.BigDecimal;
```
