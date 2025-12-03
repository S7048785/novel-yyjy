package com.novel.controller.front;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.novel.dto.req.SegmentCommentPageQueryReq;
import com.novel.po.chapterSummary.ChapterSummary;
import com.novel.po.comment.BookComment;
import com.novel.po.comment.SegmentComment;
import com.novel.result.PageResult;
import com.novel.result.Result;
import com.novel.service.ChapterCommentService;
import com.novel.user.dto.comment.AddChapterComment;
import com.novel.user.dto.comment.SegmentCommentView;
import com.novel.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 章节段落控制器
 * @author YYJY
 */
@Tag(name = "章节评论")
@RequestMapping("/chapter-comment")
@RestController
public class ChapterCommentController {

	@Autowired
	private ChapterCommentService service;
	
	@Operation(summary = "获取章节的概要列表信息")
	@GetMapping("summary")
	public Result<List<ChapterSummary>> getCommentSummaryList(long bookId, long chapterId) {
		List<ChapterSummary> list = service.getCommentSummaryList(bookId, chapterId);
		return Result.ok(list);
	}
	
	@SaCheckLogin
	@Operation(summary = "添加章节段落评论")
	@PostMapping("add")
	public Result<SegmentCommentView> addChapterSummary(@RequestBody AddChapterComment comment, HttpServletRequest request) {
		String ip = IpUtils.getIp(request);
		String address = IpUtils.getIpAddress(ip);
		return Result.ok(service.addChapterSummary(ip, address,comment));
	}
	
	@Operation(summary = "获取段落评论")
	@GetMapping("list")
	public Result<PageResult<SegmentCommentView>> listSegmentComment(SegmentCommentPageQueryReq query) {
		return Result.ok(service.listSegmentComment(query));
	}
	
	@Operation(summary = "点赞 - 未完成")
	@PostMapping("like")
	public Result<Void> like(long commentId) {
		service.like(commentId);
		return Result.ok();
	}
	
	@Operation(summary = "删除评论")
	@DeleteMapping("delete")
	public Result<Void> delete(long commentId) {
		service.delete(commentId);
		return Result.ok();
	}
	
}
