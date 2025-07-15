package com.novel.service;

import com.novel.dto.comment.BookCommentInput;
import com.novel.dto.comment.BookCommentView;
import com.novel.dto.comment.BookSubCommentView;
import com.novel.result.PageResult;

import java.util.List;

public interface BookCommentService {
	PageResult<BookCommentView> listTopBookComments(long bookId, Integer pageNo, Integer pageSize);
	
	List<BookSubCommentView> listChildBookComments(long bookId, long rootId);
	
	BookCommentView addBookComment(String ip, String address, BookCommentInput bookComment);
	
	void deleteBookComment(Long id);
}
