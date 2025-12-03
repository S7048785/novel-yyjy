package com.novel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.novel.constant.UserConstant;
import com.novel.repository.CommentRepository;
import com.novel.repository.UserRepository;
import com.novel.result.PageResult;
import com.novel.service.BookCommentService;
import com.novel.user.dto.comment.BookCommentInput;
import com.novel.user.dto.comment.BookCommentView;
import com.novel.user.dto.comment.BookSubCommentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCommentServiceImpl implements BookCommentService {
	@Autowired
	private CommentRepository repository;
	@Autowired
	private UserRepository userRepository;
	
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
	public boolean deleteBookComment(Long id) {
		// 查询当前评论是否是自己的  是否是admin
		if (repository.findUserByCommentId(id) || StpUtil.hasRole(UserConstant.USER_ROLE_ADMIN)) {
			repository.remove(id);
			return true;
		}
		return false;
	}
}
