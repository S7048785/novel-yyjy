package com.novel.controller.front;

import com.novel.dto.comment.BookCommentInput;
import com.novel.dto.comment.BookCommentView;
import com.novel.dto.comment.BookSubCommentView;
import com.novel.po.comment.BookComment;
import com.novel.result.PageResult;
import com.novel.result.Result;
import com.novel.service.BookCommentService;
import com.novel.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论")
@RestController
@RequestMapping("/comment")
public class CommentController {
	
	@Autowired
	private BookCommentService bookCommentService;
	
	/**
	 * 小说评论查询接口 顶层评论
	 * @param bookId
	 * @return
	 */
	@Operation(summary = "小说根评论")
	@GetMapping("/top")
	public Result<PageResult<BookCommentView>> listBookComments(
			long bookId,
			@RequestParam(defaultValue = "1") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize) {
		PageResult<BookCommentView> pageResult = bookCommentService.listTopBookComments(bookId, pageNo, pageSize);
		return Result.ok(pageResult);
	}
	
	@Operation(summary = "小说子评论")
	@GetMapping("/child")
	public Result<List<BookSubCommentView>> listBookComments(
			long bookId,
			long rootId) {
		List<BookSubCommentView> list = bookCommentService.listChildBookComments(bookId, rootId);
		return Result.ok(list);
	}
	
	@Operation(summary = "发布评论")
	@PostMapping("/add")
	public Result<BookCommentView> addBookComment(HttpServletRequest request, @RequestBody BookCommentInput bookComment) {
		String ip = IpUtils.getIp(request);
		String address = IpUtils.getIpAddress(ip);
		BookCommentView bookCommentView = bookCommentService.addBookComment(ip, address, bookComment);
		return Result.ok(bookCommentView);
	}
	
	@Operation(summary = "删除评论")
	@DeleteMapping("{id}")
	public Result<String> deleteBookComment(@PathVariable Long id) {
		bookCommentService.deleteBookComment(id);
		return Result.ok();
	}
}
