package com.novel.controller.front;

import com.novel.dto.BookChapterDto;
import com.novel.dto.BookInfoDoc;
import com.novel.dto.BookStateVO;
import com.novel.po.book.BookInfo;
import com.novel.result.PageResult;
import com.novel.result.Result;
import com.novel.service.BookService;
import com.novel.service.SearchService;
import com.novel.user.dto.book.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小说")
@RestController
@RequestMapping("/book")
public class BookController {

	@Autowired
	private BookService bookService;
	
	@Autowired
	private SearchService searchService;
	
	@Operation(summary = "小说分类查询接口")
	@GetMapping("category")
	public Result<List<BookCategoryView>> listCategory(@Parameter(description = "作品方向 0-男频 1-女频") Integer id) {
		return Result.ok(bookService.listCategory(id));
	}
	
	@Operation(summary = "全部小说查询接口")
	@GetMapping("all")
	public Result<PageResult<BookInfoDoc>> listAllCategory(
			AllBookQueryInput params,
			@RequestParam(defaultValue = "1") int pageNum,
			@RequestParam(defaultValue = "20") int pageSize
			) {
		//return Result.ok(bookService.listAll(params, pageNum, pageSize));
		PageResult<BookInfoDoc> searchResult = searchService.conditionSearchBooks(params, pageNum, pageSize);
		return Result.ok(searchResult);
	}
	
	@Operation(summary = "小说详情查询接口")
	@GetMapping("info/{id}")
	public Result<BookInfoView> getBookInfo(@PathVariable("id") long id) {
		return Result.ok(bookService.getBookInfo(id));
	}
	
	@Operation(summary = "小说目录查询接口")
	@GetMapping("chapter")
	public Result<BookChapterDto> listBookChapter(
			long id,
			@RequestParam(defaultValue = "0") Integer isAll,
			@RequestParam(defaultValue = "1") Integer orderBy) {
		return Result.ok(bookService.listBookChapter(id, isAll, orderBy));
	}
	
	@Operation(summary = "随机推荐同类小说")
	@GetMapping("random")
	public Result<List<BookInfo>> listRandomBooks(long categoryId, long bookId) {
		return Result.ok(bookService.listRandomBooks(categoryId, bookId));
	}
	
	@Operation(summary = "排行榜单", description = "1-总点击 2-最新入库 3-最新更新 4-评论数")
	@GetMapping("rank")
	public Result<List<BookRankView>> listRankBooks(Integer rankType) {
		return Result.ok(bookService.listRankBooks(rankType));
	}
	
	@Operation(summary = "获取章节信息")
	@GetMapping("content")
	public Result<Object> getBookContent(long bookId, long chapterId) {
		BookChapterContentView bookContent = bookService.getBookContent(bookId, chapterId);
		return Result.ok(bookContent);
	}
	
	@Operation(summary = "获取小说状态")
	@GetMapping("state")
	public Result<BookStateVO> getBookState(long bookId) {
		return Result.ok(bookService.getBookState(bookId));
	}
	
	@Operation(summary = "小说搜索")
	@GetMapping("search")
	public Result<List<BookInfoSearchView>> search(String name) {
		List<BookInfoSearchView> searchResult = searchService.searchBooksByName(name);
		return Result.ok(searchResult);
	}
}
