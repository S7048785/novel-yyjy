package com.novel.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.novel.bo.CrawlTaskStatus;
import com.novel.exception.BaseException;
import com.novel.result.Result;
import com.novel.service.NovelScraperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.babyfish.jimmer.client.meta.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nyxcirea
 * date 2026/3/10
 */
@Api
@Tag(name = "爬虫控制器")
@SaCheckRole("admin")
@RequestMapping("/admin/crawler")
@RestController
public class CrawlerController {

	@Autowired
	private NovelScraperService novelScraperService;

	// 存储所有活跃的SSE连接
	private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	@Api
	@CacheEvict(cacheNames = "dashboardCache")
	@Operation(summary = "根据小说ID新增小说和章节")
	@PostMapping("/")
	public Result<Void> addNovelById(String bookId, Integer chapterCount) {
		long dbBookId = novelScraperService.addNovelById(bookId);

		try {
			// 传入SSE emitter用于推送进度
			novelScraperService.scrapeChapters(bookId, dbBookId, chapterCount, bookId);
		} catch (Exception e) {
			//throw new Exception("爬取小说章节失败" + e.getMessage());
			throw new BaseException("爬取小说章节失败" + e.getMessage());
		}
		return Result.ok();
	}

	/**
	 * SSE接口：实时推送采集进度
	 */
	@Api
	@Operation(summary = "实时推送采集进度")
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter stream(@RequestParam String bookId) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.put(bookId, emitter);

		// 客户端断开连接时移除
		emitter.onCompletion(() -> emitters.remove(bookId));
		emitter.onTimeout(() -> emitters.remove(bookId));
		emitter.onError(e -> emitters.remove(bookId));

		// 初始连接发送空消息保持连接
		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("connected")
				.build());
		} catch (IOException e) {
			emitters.remove(bookId);
		}

		return emitter;
	}

	/**
	 * 推送采集进度到前端
	 */
	public static void pushProgress(String bookId, CrawlTaskStatus status) {
		SseEmitter emitter = emitters.get(bookId);
		if (emitter != null) {
			try {
				emitter.send(SseEmitter.event()
					.name("progress")
					.data(status)
					.build());
			} catch (IOException e) {
				emitters.remove(bookId);
			}
		}
	}

}
