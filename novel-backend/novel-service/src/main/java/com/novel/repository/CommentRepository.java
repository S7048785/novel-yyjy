package com.novel.repository;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.novel.constant.MessageConstant;
import com.novel.exception.BaseException;
import com.novel.po.book.BookInfoTable;
import com.novel.po.comment.BookComment;
import com.novel.po.comment.BookCommentDraft;
import com.novel.po.comment.BookCommentProps;
import com.novel.po.comment.BookCommentTable;
import com.novel.result.PageResult;
import com.novel.user.dto.comment.BookCommentInput;
import com.novel.user.dto.comment.BookCommentView;
import com.novel.user.dto.comment.BookSubCommentView;
import org.babyfish.jimmer.sql.DissociateAction;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
public class CommentRepository {
	@Autowired
	private JSqlClient sqlClient;
	
	private final BookCommentTable bookCommentTb = BookCommentTable.$;
	
	public PageResult<BookCommentView> listTopBookComments(long bookId, Integer pageNo, Integer pageSize) {
		var execute = sqlClient.createQuery(bookCommentTb)
				              .where(bookCommentTb.bookId().eq(bookId))
				              .where(
						              bookCommentTb.rootParentId().isNull()
				              )
				              .orderBy(bookCommentTb.createTime().desc())
				              .select(bookCommentTb.fetch(BookCommentView.class))
				              .fetchPage(pageNo - 1, pageSize);
		List<BookCommentView> rows = execute.getRows();
		rows.forEach(item -> {
				item.setChildren(Collections.emptyList());
		});
		return new PageResult<>(pageNo, pageSize, execute.getTotalRowCount(), rows);
	}
	
	public List<BookSubCommentView> listChildBookComments(long bookId, long rootId) {
		return sqlClient.createQuery(bookCommentTb)
				       .where(bookCommentTb.bookId().eq(bookId))
				       .where(bookCommentTb.rootParentId().eq(rootId))
				       .select(bookCommentTb.fetch(BookSubCommentView.class))
				       .execute();
	}
	
	@Transactional
	public BookCommentView addBookComment(String ip, String address, BookCommentInput bookComment) {
		long currentId = StpUtil.getLoginIdAsLong();
		BookComment modifiedEntity = sqlClient.save(BookCommentDraft.$.produce(draft -> {
			draft.setBookId(bookComment.getBookId())
					.setUserId(currentId)
					.setContent(bookComment.getContent())
					.setIp(ip)
					.setAddress(address)
					.setCreateTime(LocalDateTime.now())
					.setChildren(Collections.emptyList());
			var parentId = bookComment.getParentId() != null ? bookComment.getParentId() : 0;
			var rootParentId = bookComment.getRootParentId() != null ? bookComment.getRootParentId() : 0;
			
			draft.setParentId(parentId);
			draft.setRootParentId(rootParentId);
			
			if (bookComment.getReplyNickName() != null) {
				draft.setReplyNickName(bookComment.getReplyNickName());
			}
		}), SaveMode.INSERT_ONLY).getModifiedEntity();
		
		// 更新书评数量
		sqlClient.createUpdate(BookInfoTable.$)
				.where(BookInfoTable.$.id().eq(bookComment.getBookId()))
				.set(BookInfoTable.$.commentCount(), BookInfoTable.$.commentCount().plus(1L))
				.execute();
		
		return new BookCommentView(modifiedEntity);
	}
	
	/**
	 * 删除评论
	 * 非根评论不做级联删除
	 * @param id
	 */
	@Transactional
	public void remove(Long id) {
		// 查找评论
		List<BookComment> execute = sqlClient.createQuery(bookCommentTb)
				                            .where(bookCommentTb.id().eq(id))
				                            .select(bookCommentTb)
				                            .execute();
		if (execute.isEmpty()) {
			throw new BaseException(MessageConstant.COMMENT_NOT_FOUND);
		}
		BookComment bookComment = execute.get(0);
		// 非根评论 直接删除
		if (bookComment.rootParent().id() != 0) {
			sqlClient.deleteById(BookComment.class, id);
		} else {
			
			/**
			 * 级联删除根评论
			 */
			sqlClient
					.getEntities()
					.deleteCommand(BookComment.class, id)
					.setDissociateAction(BookCommentProps.ROOT_PARENT, DissociateAction.DELETE)
					.execute();
		}
		
	}
	
	public boolean findUserByCommentId(Long id) {
		// 获取当前用户ID
		long userId = StpUtil.getLoginIdAsLong();
		List<Long> execute = sqlClient.createQuery(BookCommentTable.$)
				                     .where(BookCommentTable.$.id().eq(id))
				                     .select(bookCommentTb.userId())
				                     .execute();
		Long first = CollectionUtil.getFirst(execute);
		// 评论不存在或评论用户ID和当前用户ID不一致
		return first == null || first == userId;
	}
}
