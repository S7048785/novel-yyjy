package com.novel.po.comment;

import com.novel.po.book.BookChapter;
import com.novel.po.user.UserInfo;
import org.babyfish.jimmer.sql.*;


import java.time.LocalDateTime;

/**
 * 章节段落评论
 */
@Table(name = "segment_comment")
@Entity
public interface SegmentComment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	long id();
	
	/**
	 * 用户
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	UserInfo user();
	
	/**
	 * 章节id
	 */
	@Key
	@ManyToOne
	@JoinColumn(name = "chapter_id")
	BookChapter chapter();
	
	/**
	 * 段落号
	 */
	@Key
	int segmentNum();
	
	/**
	 * 楼层
	 */
	int level();
	
	/**
	 * 评论内容
	 */
	String content();
	
	/**
	 * 点赞数
	 * @return
	 */
	int likeCount();
	
	String ip();
	
	/**
	 * ip属地
	 */
	String ipAddress();
	
	LocalDateTime createTime();
	
	@Default("0")
	@LogicalDeleted("1")
	int delFlag();
}

