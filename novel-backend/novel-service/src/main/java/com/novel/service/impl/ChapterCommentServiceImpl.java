package com.novel.service.impl;

import com.novel.dto.req.SegmentCommentPageQueryReq;
import com.novel.po.chapterSummary.ChapterSummary;
import com.novel.po.comment.BookComment;
import com.novel.po.comment.SegmentComment;
import com.novel.repository.ChapterSegmentRepository;
import com.novel.result.PageResult;
import com.novel.service.ChapterCommentService;
import com.novel.user.dto.chapterSummary.ChapterSummaryView;
import com.novel.user.dto.comment.AddChapterComment;
import com.novel.user.dto.comment.SegmentCommentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterCommentServiceImpl implements ChapterCommentService {
	
	@Autowired
	ChapterSegmentRepository repository;
	
	@Override
	public List<ChapterSummary> getCommentSummaryList(long bookId, long chapterId) {
		return repository.getCommentSummaryList(bookId, chapterId);
	}
	
	@Override
	public SegmentCommentView addChapterSummary(String ip, String address, AddChapterComment comment) {
		// 添加一条评论
		return repository.addChapterSummary(ip, address, comment);
	}
	
	@Override
	public PageResult<SegmentCommentView> listSegmentComment(SegmentCommentPageQueryReq query) {
		
		return repository.listSegmentComment(query);
	}
	
	@Override
	public void like(long commentId) {
		// TODO: 对段落评论点赞
	}
	
	@Override
	public void delete(long commentId) {
		// TODO: 删除段落评论
	}
}
