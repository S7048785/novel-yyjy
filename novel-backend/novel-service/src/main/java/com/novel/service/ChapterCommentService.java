package com.novel.service;

import com.novel.dto.req.SegmentCommentPageQueryReq;
import com.novel.po.chapterSummary.ChapterSummary;
import com.novel.po.comment.BookComment;
import com.novel.po.comment.SegmentComment;
import com.novel.result.PageResult;
import com.novel.user.dto.chapterSummary.ChapterSummaryView;
import com.novel.user.dto.comment.AddChapterComment;
import com.novel.user.dto.comment.SegmentCommentView;

import java.util.List;

public interface ChapterCommentService {
	List<ChapterSummary> getCommentSummaryList(long bookId, long chapterId);
	
	SegmentCommentView addChapterSummary(String ip, String address, AddChapterComment comment);
	
	PageResult<SegmentCommentView> listSegmentComment(SegmentCommentPageQueryReq query);
	
	void like(long commentId);
	
	void delete(long commentId);
}
