package com.novel.po.chapterSummary;

import com.novel.po.book.BookChapter;
import com.novel.po.book.BookInfo;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.babyfish.jimmer.sql.*;

/**
 * 章节段落信息表
 */
@Entity
@Table(name = "chapter_summary")
public interface ChapterSummary {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	@JsonConverter(LongToStringConverter.class)
	long id();
	
	/**
	 * 评论小说ID
	 */
	@Key
	@ManyToOne
	@JoinColumn(name = "book_id")
	BookInfo book();
	
	@IdView
	long bookId();
	
	@Key
	@ManyToOne
	@JoinColumn(name = "chapter_id")
	BookChapter chapter();
	
	@IdView
	long chapterId();
	
	/**
	 * 段落号
	 */
	int segmentNum();
	
	/**
	 * 评论量
	 */
	long commentNum();
}

