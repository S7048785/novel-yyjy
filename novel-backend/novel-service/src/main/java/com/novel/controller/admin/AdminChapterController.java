package com.novel.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.novel.constant.UserConstant;
import com.novel.dto.req.BookChapterAddReq;
import com.novel.dto.req.ChapterPageQueryReq;
import com.novel.result.PageResult;
import com.novel.result.Result;
import com.novel.service.BookService;
import com.novel.user.dto.book.BookChapterView;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@SaCheckRole(UserConstant.USER_ROLE_ADMIN)
@RequestMapping("admin/chapter")
@RestController
public class AdminChapterController {
	@Autowired
	BookService service;
	
	@Operation(summary = "分页查询章节列表")
	@GetMapping("list")
	public PageResult<BookChapterView> list(ChapterPageQueryReq req) {
		return service.pageForAdmin(req);
	}
	
	@Operation(summary = "增加章节")
	@PostMapping("add")
	public Result<Void> add(@RequestBody BookChapterAddReq req) {
		service.addForAdmin(req);
		return Result.ok();
	}
	
	@Operation(summary = "更新章节")
	@PutMapping("update")
	public Result<Void> update(@RequestBody BookChapterAddReq req) {
		service.updateForAdmin(req);
		return Result.ok();
	}
}
