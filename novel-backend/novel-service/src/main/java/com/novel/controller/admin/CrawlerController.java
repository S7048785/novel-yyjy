package com.novel.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.novel.bo.CrawlTaskStatus;
import com.novel.result.Result;
import com.novel.service.NovelScraperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.client.meta.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nyxcirea
 * date 2026/3/10
 */
@Api
@Tag(name = "爬虫控制器")
@RequestMapping("/admin/crawler")
@RestController
@Slf4j
public class CrawlerController {

	@Autowired
	private NovelScraperService novelScraperService;

	// 存储所有活跃的SSE连接
	private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	@Api
	@CacheEvict(cacheNames = "dashboardCache", allEntries = true)
	@Operation(summary = "根据小说ID新增小说和章节")
	@PostMapping("/")
	public Result<Void> addNovelById(String bookId, Integer chapterCount) {
		StpUtil.checkRole("admin");
		CompletableFuture.runAsync(() -> {
			// 业务逻辑
			long dbBookId = novelScraperService.addNovelById(bookId);
			
			
			// 异步执行爬取任务，不阻塞主请求，让SSE能实时推送进度
			try {
				novelScraperService.scrapeChapters(bookId, dbBookId, chapterCount, bookId);
			} catch (Exception e) {
				log.error("爬取小说章节失败", e);
				CrawlTaskStatus errorStatus = new CrawlTaskStatus(bookId, String.valueOf(dbBookId), 0);
				errorStatus.setMessage("采集失败: " + e.getMessage());
				errorStatus.setStatus("失败");
				pushProgress(bookId, errorStatus);
			}
		});
		
		return Result.ok();
	}

	/**
	 * SSE接口：实时推送采集进度
	 */
	@Api
	@SaCheckRole("admin")
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
