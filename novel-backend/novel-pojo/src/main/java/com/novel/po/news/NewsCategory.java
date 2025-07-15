package com.novel.po.news;

import java.time.LocalDateTime;

import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.jetbrains.annotations.Nullable;
import org.babyfish.jimmer.sql.*;


/**
 * <p>
 * 新闻类别
 * </p>
 *
 * @author YYJY
 * @date 2025-06-29
 */
@Entity
@Table(name = "news_category")
public interface NewsCategory {
	
	/**
	 * id
	 */
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id();
	
	/**
	 * 类别名
	 */
	String name();
	
	/**
	 * 排序
	 */
	long sort();
	
	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	@Nullable
	LocalDateTime createTime();
	
	/**
	 * 更新时间
	 */
	@Column(name = "update_time")
	@Nullable
	LocalDateTime updateTime();
	
}
