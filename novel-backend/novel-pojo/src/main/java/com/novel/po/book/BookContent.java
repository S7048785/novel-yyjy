package com.novel.po.book;

import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.babyfish.jimmer.sql.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 小说内容
 */
@Table(name = "book_content")
@Entity
public interface BookContent {
	
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	long id();
	
	/**
	 * 章节ID
	 */
	@Key
	@OneToOne
	@JoinColumn(name = "chapter_id")
	BookChapter chapter();
	
	/**
	 * 小说章节内容
	 */
	String content();
	
	@Null
	LocalDateTime createTime();
	
	@Null
	LocalDateTime updateTime();
	
	@Default("0")
	@LogicalDeleted("1")
	int delFlag();
}

