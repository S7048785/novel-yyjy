package com.novel.po.user;

import com.novel.po.book.BookChapter;
import com.novel.po.book.BookInfo;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.babyfish.jimmer.sql.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 用户书架
 */
@Table(name = "user_bookshelf")
@Entity
public interface UserBookshelf {
	
	/**
	 * 主键
	 */
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	long id();
	
	/**
	 * 用户ID
	 */
	@Key
	long userId();
	
	/**
	 * 小说ID
	 */
	@Key
	@OneToOne
	BookInfo book();
	
	/**
	 * 上一次阅读的章节内容表ID
	 */
	@Null
	@OneToOne
	@JoinColumn(name = "pre_content_id")
	BookChapter preContent();
	
	int state();
	
	/**
	 * 创建时间;
	 */
	LocalDateTime createTime();
	
	/**
	 * 更新时间;
	 */
	LocalDateTime updateTime();
}

