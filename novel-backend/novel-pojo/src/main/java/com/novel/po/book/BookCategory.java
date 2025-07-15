package com.novel.po.book;

import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.babyfish.jimmer.sql.*;
import org.springframework.boot.context.properties.bind.DefaultValue;


import java.time.LocalDateTime;

/**
 * 小说类别
 */
@Table(name = "book_category")
@Entity
public interface BookCategory {
	
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	long id();
	
	/**
	 * 作品方向;0-男频 1-女频
	 */
	
	int workDirection();
	
	/**
	 * 类别名
	 */
	String name();
	
	/**
	 * 排序
	 */
	
	int sort();
	
	/**
	 * 创建时间
	 */
	LocalDateTime createTime();
	
	/**
	 * 更新时间
	 */
	LocalDateTime updateTime();
	
	
	@Default("0")
	@LogicalDeleted("1")
	int delFlag();
}

