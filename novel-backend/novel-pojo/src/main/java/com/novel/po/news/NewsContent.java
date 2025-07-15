package com.novel.po.news;

import java.time.LocalDateTime;

import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.babyfish.jimmer.sql.*;


/**
 * <p>
 * 新闻内容
 *
 * </p>
 *
 * @author YYJY
 * @date 2025-06-29
 */
@Entity
@Table(name = "news_content")
public interface NewsContent {
	
	/**
	 * 主键
	 */
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id();
	
	/**
	 * 新闻
	 */
	@IdView
	long newsInfoId();
	
	@Key
	@OneToOne
	@JoinColumn(name = "news_id")
	NewsInfo newsInfo();
	
	/**
	 * 新闻内容
	 */
	String content();
	
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
