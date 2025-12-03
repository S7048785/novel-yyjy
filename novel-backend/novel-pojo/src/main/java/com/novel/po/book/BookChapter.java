package com.novel.po.book;

import com.novel.po.BaseEntity;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.babyfish.jimmer.sql.*;


import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 小说章节
 */
@Table(name = "book_chapter")
@Entity
public interface BookChapter extends BaseEntity {
	
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	long id();
	
	/**
	 * 小说ID
	 */
	@Key
	@ManyToOne
	BookInfo book();
	
	@Null
	@OneToOne(mappedBy = "chapter")
	BookContent content();
	
	/**
	 * 章节号
	 */
	@Key
	int chapterNum();
	
	/**
	 * 章节名
	 */
	String chapterName();
	
	/**
	 * 章节字数
	 */
	int wordCount();
	
	/**
	 * 是否收费;1-收费 0-免费
	 */
	@Column(name = "is_vip")
	int vipState();
	
	@Default("0")
	@LogicalDeleted("1")
	int delFlag();
}

