package com.novel.dto;

import com.novel.po.book.BookCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;
import org.babyfish.jimmer.sql.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
public class BookInfoDoc {
	String id;
	
	/**
	 * 作品方向;0-男频 1-女频
	 */
	Integer workDirection;
	
	/**
	 * 类别ID
	 */
	BookCategory category;
	
	Long categoryId;
	
	/**
	 * 类别名
	 */
	String categoryName;
	
	/**
	 * 小说封面地址
	 */
	String picUrl;
	
	/**
	 * 小说名
	 */
	String bookName;
	
	/**
	 * 作家id
	 */
	BigInteger authorId;
	
	/**
	 * 作家名
	 */
	String authorName;
	
	/**
	 * 书籍描述
	 */
	String bookDesc;
	
	/**
	 * 评分;总分:10 ，真实评分 = score/10
	 */
	int score;
	
	/**
	 * 书籍状态;0-连载中 1-已完结
	 */
	int bookStatus;
	
	/**
	 * 点击量
	 */
	long visitCount;
	
	/**
	 * 总字数
	 */
	long wordCount;
	
	/**
	 * 最新章节ID
	 */
	String lastChapterId;
	
	/**
	 * 最新章节名
	 */
	String lastChapterName;
	
	/**
	 * 最新章节更新时间
	 */
	long lastChapterUpdateTime;
	
}
