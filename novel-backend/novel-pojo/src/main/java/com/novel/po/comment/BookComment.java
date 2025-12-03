package com.novel.po.comment;

import com.novel.po.book.BookChapter;
import com.novel.po.book.BookInfo;
import com.novel.po.chapterSummary.ChapterSummary;
import com.novel.po.user.UserInfo;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.Formula;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.babyfish.jimmer.sql.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小说评论
 */
@Entity
@Table(name = "book_comment")
public interface BookComment {
	
	/**
	 * 主键
	 */
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	long id();
	
	/**
	 * 评论小说ID
	 */
	@ManyToOne
	@JoinColumn(name = "book_id")
	BookInfo book();
	
	/**
	 * 评论用户ID
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	UserInfo user();
	
	/**
	 * 评价内容
	 */
	String content();
	
	String ip();
	
	/**
	 * ip属地
	 */
	String address();
	
	/**
	 * 回复的昵称
	 */
	@Null
	String replyNickName();
	
	/**
	 * 父评论
	 */
	@ManyToOne
	@Null
	@OnDissociate(DissociateAction.SET_NULL)
	BookComment parent();
	
	/**
	 * 根评论
	 */
	@ManyToOne
	@Null
	@OnDissociate(DissociateAction.DELETE)
	BookComment rootParent();
	
	@OneToMany(mappedBy = "parent")
	List<BookComment> children();
	@Formula(dependencies = {"children"})
	default int childrenCount() {
		return children().size();
	}
	
	/**
	 * 创建时间
	 */
	LocalDateTime createTime();
	
	@Default("0")
	@LogicalDeleted("1")
	int delFlag();
}

