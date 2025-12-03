package com.novel.service;


import com.novel.result.PageResult;
import com.novel.user.dto.comment.BookCommentInput;
import com.novel.user.dto.comment.BookCommentView;
import com.novel.user.dto.comment.BookSubCommentView;

import java.util.List;

public interface BookCommentService {
	PageResult<BookCommentView> listTopBookComments(long bookId, Integer pageNo, Integer pageSize);
	
	List<BookSubCommentView> listChildBookComments(long bookId, long rootId);
	
	BookCommentView addBookComment(String ip, String address, BookCommentInput bookComment);
	
	boolean deleteBookComment(Long id);
}
