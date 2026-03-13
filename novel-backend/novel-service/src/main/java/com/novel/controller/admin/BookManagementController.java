package com.novel.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.novel.dto.req.BookPageQueryReq;
import com.novel.dto.req.BookUpdateReq;
import com.novel.po.book.BookInfo;
import com.novel.po.book.BookInfoFetcher;
import com.novel.result.PageResult;
import com.novel.result.Result;
import com.novel.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.Api;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 后台小说管理控制器
 */
@Api
@SaCheckRole("admin")
@Tag(name = "后台小说管理")
@RequestMapping("/admin/book")
@RestController
public class BookManagementController {
	
	private final BookService bookService;
	
	public BookManagementController(BookService bookService) {
		this.bookService = bookService;
	}
	
	@Api
	@Operation(summary = "分页查询小说列表")
	@GetMapping("/page")
	public Result<PageResult<@FetchBy("BOOK_VIEW_OF_ADMIN") BookInfo>> page(@Validated BookPageQueryReq req) {
		var pageResult = bookService.page(req, BOOK_VIEW_OF_ADMIN);
		return Result.ok(pageResult);
	}
	
	@Api
	@Operation(summary = "根据ID获取小说详情")
	@GetMapping("/{id}")
	public Result<@FetchBy("BOOK_VIEW_OF_ADMIN") BookInfo> getById(@PathVariable long id) {
		BookInfo bookVO = bookService.getById(id, BOOK_VIEW_OF_ADMIN);
		return Result.ok(bookVO);
	}
	
	@Api
	@CacheEvict(cacheNames = "dashboardCache")
	@Operation(summary = "新增小说")
	@PostMapping
	public Result<Void> add(@Validated @RequestBody BookUpdateReq req) {
		bookService.addBook(req);
		return Result.ok();
	}
	
	@Api
	@Operation(summary = "修改小说")
	@PutMapping
	public Result<Void> update(@Validated @RequestBody BookUpdateReq req) {
		bookService.updateBook(req);
		return Result.ok();
	}
	
	@Api
	@CacheEvict(cacheNames = "dashboardCache")
	@Operation(summary = "删除小说")
	@DeleteMapping("/{id}")
	public Result<Void> delete(@PathVariable String id) {
		bookService.deleteBook(id);
		return Result.ok();
	}
	
	private static final Fetcher<BookInfo> BOOK_VIEW_OF_ADMIN = BookInfoFetcher.$
			                                                            .allScalarFields()
			                                                            .vipState(false)
			                                                            .authorId(false)
			                                                            .lastChapterId(false)
			                                                            .lastChapterName(false)
			                                                            .lastChapterUpdateTime(false);
	
	
}
