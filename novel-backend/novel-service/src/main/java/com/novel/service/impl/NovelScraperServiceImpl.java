package com.novel.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.novel.bo.CrawlTaskStatus;
import com.novel.controller.admin.CrawlerController;
import com.novel.po.book.*;
import com.novel.service.NovelScraperService;
import com.novel.utils.NovelScraperUtil;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author Nyxcirea
 * date 2026/3/10
 * description: TODO
 */
@Slf4j
@Service
public class NovelScraperServiceImpl implements NovelScraperService {

	@Autowired
	private JSqlClient sqlClient;

	/**
	 * 新增小说
	 *
	 * @return 新增小说ID
	 */
	@Override
	public long addNovelById(String bookId) {
		var res = NovelScraperUtil.scrapeBookDetail(bookId);
		var book = BookInfoDraft.$.produce((draft) -> {
			var category = res.getCategoryName();
			draft.setWorkDirection(category.equals("女生频道") ? 1 : 0);
			draft.setCategoryId(
					switch (category) {
						case "玄幻奇幻" -> 1L;
						case "武侠仙侠" -> 2L;
						case "都市言情" -> 3L;
						case "历史军事" -> 4L;
						case "科幻灵异" -> 5L;
						case "网游竞技" -> 6L;
						default -> 7L;
					}
			);
			draft.setCategoryName(category)
					.setPicUrl("http://117.72.165.13:8888" + res.getPicUrl())
					.setBookName(res.getBookName())
					.setAuthorId(BigInteger.valueOf(0L))
					.setAuthorName(res.getAuthorName())
					.setBookDesc(res.getBookDesc())
					.setScore(0)
					.setBookStatus(0)
					.setVisitCount(0)
					.setWordCount(0)
					.setCommentCount(0)
					.setVipState(0)
					.setDelFlag(0);
		});
		var dbBookId = sqlClient.save(book).getModifiedEntity().id();

		log.info("新增小说：{}", res.getBookName());
		return dbBookId;
	}

	/**
	 * 爬取小说章节
	 */
	@Transactional
	@Override
	public void scrapeChapters(String bookId, Long dbBookId, Integer count, String taskId) throws Exception {

		Long chapterCount = sqlClient.createQuery(BookChapterTable.$)
				                    .where(BookChapterTable.$.bookId().eq(dbBookId))
				                    .selectCount().execute().get(0);

		// 获取小说详情用于显示
		var bookDetail = NovelScraperUtil.scrapeBookDetail(bookId);
		String novelName = bookDetail.getBookName();
		
		int crawlCount;
		if (count == null || count < 1) {
			crawlCount = bookDetail.getChapterCount();
		} else {
			crawlCount = count;
		}
		
		// 首次推送：开始采集
		CrawlTaskStatus startStatus = new CrawlTaskStatus(taskId, String.valueOf(dbBookId), crawlCount);
		startStatus.setMessage("开始采集: " + novelName);
		startStatus.setNovelId(dbBookId.toString());
		CrawlerController.pushProgress(taskId, startStatus);

		int startIndex = chapterCount > 0 ? chapterCount.intValue() : 0;
		var chapterRes = NovelScraperUtil.scrapeChapters(bookId, startIndex, crawlCount, (int currentChapter, int totalChapters, double progress) -> {
			
			CrawlTaskStatus status = new CrawlTaskStatus(taskId, String.valueOf(dbBookId), crawlCount);
			status.setNovelId(dbBookId.toString());
			status.setNovelName(bookDetail.getBookName());
			status.setCurrentChapter(currentChapter);
			status.setTotalChapters(totalChapters);
			status.setProgress(progress);
			status.setMessage("正在采集第 " + currentChapter + " 章 / 共 " + totalChapters + " 章");
			status.setStatus("采集中");
			CrawlerController.pushProgress(taskId, status);
		});

		int totalChapters = chapterRes.size();

		// 分批保存并推送进度
		int batchSize = 10;
		int total = chapterRes.size();
		int savedCount = 0;
		long totalWords = 0;
		BookChapter lastChapter = null;

		for (int i = 0; i < total; i += batchSize) {
			int end = Math.min(i + batchSize, total);
			var batchRes = chapterRes.subList(i, end);

			// 构建实体
			var batchEntities = CollUtil.map(batchRes, (item) -> BookContentDraft.$.produce((draft) -> {
				draft.setContent(item.getBookContent().getContent())
						.setChapter(BookChapterDraft.$.produce((chapterDraft) -> {
							chapterDraft.setBookId(dbBookId)
									.setChapterNum(item.getBookChapter().getChapterNum() - 1)
									.setChapterName(item.getBookChapter().getChapterName())
									.setWordCount(item.getBookChapter().getWordCount())
									.setVipState(0);
						}));
			}), true);

			// 保存批次
			var items = sqlClient.saveEntities(batchEntities).getItems();
			savedCount += items.size();
			totalWords += items.stream().mapToLong(item -> item.getModifiedEntity().chapter().wordCount()).sum();
			lastChapter = items.get(items.size() - 1).getModifiedEntity().chapter();
		}

		log.info("{} 章节写入成功", savedCount);

		// 更新小说信息
		var bookTable = BookInfoTable.$;
		sqlClient.createUpdate(bookTable)
				.where(bookTable.id().eq(dbBookId))
				.set(bookTable.wordCount(), totalWords)
				.set(bookTable.lastChapterId(), BigInteger.valueOf(lastChapter.id()))
				.set(bookTable.lastChapterName(), lastChapter.chapterName())
				.set(bookTable.lastChapterUpdateTime(), LocalDateTime.now()).execute();

		// 推送完成状态
		CrawlTaskStatus completeStatus = new CrawlTaskStatus(taskId, String.valueOf(dbBookId), totalChapters);
		completeStatus.setNovelId(dbBookId.toString());
		completeStatus.setNovelName(bookDetail.getBookName());
		completeStatus.setCurrentChapter(totalChapters);
		completeStatus.setTotalChapters(totalChapters);
		completeStatus.setProgress(100.0);
		completeStatus.setMessage("采集完成！共 " + totalChapters + " 章");
		completeStatus.setStatus("已完成");
		CrawlerController.pushProgress(taskId, completeStatus);
	}

}
