package com.novel.po.book;

import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.babyfish.jimmer.sql.*;


import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 小说信息
 * @author YYJY
 */
@Table(name = "book_info")
@Entity
public interface BookInfo {
	
	/**
	 * 主键
	 */
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	long id();
	
	/**
	 * 作品方向;0-男频 1-女频
	 */
	@Null
	Integer workDirection();
	
	/**
	 * 类别ID
	 */
	@Null
	@ManyToOne
	BookCategory category();
	
	@Null
	@IdView
	Long categoryId();
	
	/**
	 * 类别名
	 */
	@Null
	String categoryName();
	
	/**
	 * 小说封面地址
	 */
	String picUrl();
	
	/**
	 * 小说名
	 */
	@Key
	String bookName();
	
	/**
	 * 作家id
	 */
	BigInteger authorId();
	
	/**
	 * 作家名
	 */
	@Key
	String authorName();
	
	/**
	 * 书籍描述
	 */
	String bookDesc();
	
	/**
	 * 评分;总分:10 ，真实评分 = score/10
	 */
	int score();
	
	/**
	 * 书籍状态;0-连载中 1-已完结
	 */
	int bookStatus();
	
	/**
	 * 点击量
	 */
	long visitCount();
	
	/**
	 * 总字数
	 */
	long wordCount();
	
	/**
	 * 评论数
	 */
	long commentCount();
	
	/**
	 * 最新章节ID
	 */
	@Null
	BigInteger lastChapterId();
	
	/**
	 * 最新章节名
	 */
	@Null
	String lastChapterName();
	
	/**
	 * 最新章节更新时间
	 */
	@Null
	LocalDateTime lastChapterUpdateTime();
	
	/**
	 * 是否收费;1-收费 0-免费
	 */
	@Column(name = "is_vip")
	int vipState();
	
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
	
	@Default("0")
	@LogicalDeleted("1")
	int delFlag();
}

