package com.novel.repository;

import cn.hutool.core.collection.CollUtil;
import com.novel.context.BaseContext;
import com.novel.dto.comment.BookCommentInput;
import com.novel.dto.comment.BookCommentView;
import com.novel.dto.comment.BookSubCommentView;
import com.novel.po.book.BookInfoTable;
import com.novel.po.comment.BookComment;
import com.novel.po.comment.BookCommentDraft;
import com.novel.po.comment.BookCommentTable;
import com.novel.result.PageResult;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.DeleteMode;
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
						              bookCommentTb.parentId().eq(0L),
						              bookCommentTb.rootParentId().eq(0L)
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
		Long currentId = BaseContext.getCurrentId();
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
	
	@Transactional
	public void remove(Long id) {
		sqlClient.deleteById(BookComment.class, id);
		
		// 更新数量
		sqlClient.createUpdate(BookInfoTable.$)
				.where(BookInfoTable.$.id().eq(id))
				.set(BookInfoTable.$.commentCount(), BookInfoTable.$.commentCount().minus(1L))
				.execute();
	}
}
