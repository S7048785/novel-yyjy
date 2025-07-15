package com.novel.service.impl;

import com.novel.dto.comment.BookCommentInput;
import com.novel.dto.comment.BookCommentView;
import com.novel.dto.comment.BookSubCommentView;
import com.novel.repository.CommentRepository;
import com.novel.result.PageResult;
import com.novel.service.BookCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCommentServiceImpl implements BookCommentService {
	@Autowired
	private CommentRepository repository;
	
	
	@Override
	public PageResult<BookCommentView> listTopBookComments(long bookId, Integer pageNo, Integer pageSize) {
		return repository.listTopBookComments(bookId, pageNo, pageSize);
	}
	
	@Override
	public List<BookSubCommentView> listChildBookComments(long bookId, long rootId) {
		List<BookSubCommentView> list = repository.listChildBookComments(bookId, rootId);
		return list;
	}
	
	@Override
	public BookCommentView addBookComment(String ip, String address, BookCommentInput bookComment) {
		BookCommentView bookCommentView = repository.addBookComment(ip, address, bookComment);
		
		return bookCommentView;
	}
	
	@Override
	public void deleteBookComment(Long id) {
		repository.remove(id);
	}
}
