package com.novel.po.news;

import java.time.LocalDateTime;

import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.babyfish.jimmer.sql.*;


/**
 * <p>
 * 新闻信息
 *
 * </p>
 *
 * @author YYJY
 * @date 2025-06-29
 */
@Entity
@Table(name = "news_info")
public interface NewsInfo {
	
	/**
	 * 主键
	 */
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id();
	
	@OneToOne(mappedBy = "newsInfo")
	@Nullable
	NewsContent newsContent();
	
	/**
	 * 类别ID
	 */
	@IdView
	long categoryId();
	
	/**
	 * 类别名称
	 */
	String categoryName();
	
	/**
	 * 类别
	 */
	@ManyToOne
	NewsCategory category();
	
	/**
	 * 新闻来源
	 */
	@Column(name = "source_name")
	String sourceName();
	
	/**
	 * 新闻标题
	 */
	String title();
	
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
