package com.novel.po.home;

import com.novel.po.book.BookInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.babyfish.jimmer.sql.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 小说推荐
 */
@Table(name = "home_book")
@Entity
public interface HomeBook {
	
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	long id();
	
	/**
	 * 推荐类型;0-轮播图 1-顶部栏 2-本周强推 3-热门推荐 4-精品推荐
	 */
	String type();
	
	/**
	 * 推荐简介
	 * @return
	 */
	@Null
	String intro();
	
	/**
	 * 推荐标签
	 */
	@Null
	String tag();
	
	/**
	 * 推荐排序
	 */
	int sort();
	
	/**
	 * 推荐小说ID
	 */
	@OneToOne
	BookInfo book();
	
	@IdView
	long bookId();
	
	/**
	 * 创建时间
	 */
	@Null
	LocalDateTime createTime();
	
	/**
	 * 更新时间
	 */
	@Null
	LocalDateTime updateTime();
}

