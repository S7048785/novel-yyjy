package com.novel.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.novel.exception.BaseException;
import com.novel.result.Result;
import com.novel.service.NovelScraperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.babyfish.jimmer.client.meta.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nyxcirea
 * @date 2026/3/10
 * @description: TODO
 */
@Api
@Tag(name = "爬虫控制器")
@SaCheckRole("admin")
@RequestMapping("/crawler")
@RestController
public class CrawlerController {
	
	@Autowired
	private NovelScraperService novelScraperService;
	
	@Api
	@CacheEvict(cacheNames = "dashboardCache")
	@Operation(summary = "根据小说ID新增小说")
	@PostMapping("/")
	public Result<Void> addNovelById(String bookId, Integer chapterCount) {
		long dbBookId = novelScraperService.addNovelById(bookId);
		
		try {
			novelScraperService.scrapeChapters(bookId, dbBookId, chapterCount);
		} catch (Exception e) {
			throw new BaseException("爬取小说章节失败");
		}
		return Result.ok();
	}
}
