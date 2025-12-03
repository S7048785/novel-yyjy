package com.novel.controller.front;

import com.novel.result.Result;
import com.novel.service.HomeService;
import com.novel.user.dto.book.HomeBookRankView;
import com.novel.user.dto.book.LastInsertBookView;
import com.novel.user.dto.book.LastUpdateBookView;
import com.novel.user.dto.book.VisitRankBookView;
import com.novel.user.dto.home.HomeBookView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "首页")
@RestController
@RequestMapping("/home")
public class HomeController {
	@Autowired
	private HomeService homeService;
	@Operation(summary = "首页小说推荐查询接口")
	@GetMapping
	public Result<List<HomeBookView>> listHomeBooks() {
		return Result.ok(homeService.listHomeBooks());
	}

	@Operation(summary = "首页小说点击排行")
	@GetMapping("visit-rank")
	public Result<List<VisitRankBookView>> listBookVisitRank() {
		return Result.ok(homeService.listBookVisitRank());
	}
	
	@Operation(summary = "小说新书榜查询接口")
	@GetMapping("newest")
	public Result<List<HomeBookRankView>> listNewestRankBooks() {
		return Result.ok(homeService.listNewestRankBooks());
	}
	
	@Operation(summary = "最近更新小说查询接口")
	@GetMapping("recent-update")
	public Result<List<LastUpdateBookView>> listRecentUpdateBooks() {
		List<LastUpdateBookView> list = homeService.listRecentUpdateBook();
		return Result.ok(list);
	}
	
	@Operation(summary = "最新入库小说查询接口")
	@GetMapping("newest-add")
	public Result<List<LastInsertBookView>> listNewestAddBooks() {
		List<LastInsertBookView> list = homeService.listNewestAddBooks();
		return Result.ok(list);
	}
}
