package com.novel.controller.front;

import com.novel.result.Result;
import com.novel.service.NewService;
import com.novel.user.dto.news.LaetstNewsView;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "新闻模块")
@RequestMapping("/news")
@RestController
public class NewsController {
	
	@Autowired
	private NewService newService;
	@GetMapping("latest")
	public Result<List<LaetstNewsView>> latestList() {
		var list = newService.latestList();
		return Result.ok(list);
	}
}
