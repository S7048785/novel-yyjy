package com.novel.controller.front;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.novel.result.PageResult;
import com.novel.result.Result;
import com.novel.service.BookCommentService;
import com.novel.user.dto.comment.BookCommentInput;
import com.novel.user.dto.comment.BookCommentView;
import com.novel.user.dto.comment.BookSubCommentView;
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
	
	@SaCheckLogin
	@Operation(summary = "发布评论")
	@PostMapping("/add")
	public Result<BookCommentView> addBookComment(HttpServletRequest request, @RequestBody BookCommentInput bookComment) {
		String ip = IpUtils.getIp(request);
		String address = IpUtils.getIpAddress(ip);
		BookCommentView bookCommentView = bookCommentService.addBookComment(ip, address, bookComment);
		return Result.ok(bookCommentView);
	}
	
	@SaCheckLogin
	@Operation(summary = "删除评论")
	@DeleteMapping("{id}")
	public Result<String> deleteBookComment(@PathVariable Long id) {
		boolean isDeleted = bookCommentService.deleteBookComment(id);
		return isDeleted ? Result.ok() : Result.fail("删除失败：当前用户无操作权限");
	}
}
