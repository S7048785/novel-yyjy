package com.novel.service.impl;

import cn.hutool.core.collection.CollUtil;
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
 * @date 2026/3/10
 * @description: TODO
 */
@Slf4j
@Service
public class NovelScraperServiceImpl implements NovelScraperService {
	
	@Autowired
	private JSqlClient sqlClient;
	
	/**
	 * 新增小说
	 *
	 * @return
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
			LocalDateTime now = LocalDateTime.now();
			draft.setCategoryName(category)
					.setPicUrl(res.getPicUrl())
					.setBookName(res.getBookName())
					.setAuthorId(BigInteger.valueOf(0L))
					.setAuthorName(res.getAuthorName())
					.setBookDesc(res.getBookDesc())
					.setScore(5)
					.setBookStatus(0)
					.setVisitCount(0)
					.setWordCount(0)
					.setCommentCount(0)
					.setVipState(0)
					.setCreateTime(now)
					.setUpdateTime(now)
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
	public void scrapeChapters(String bookId, Long dbBookId, int count) throws Exception {
		
		Long chapterCount = sqlClient.createQuery(BookChapterTable.$)
				                    .where(BookChapterTable.$.bookId().eq(dbBookId))
				                    .selectCount().execute().get(0);
		
		var chapterRes = NovelScraperUtil.scrapeChapters(bookId, chapterCount > 0 ? chapterCount.intValue() : 0, count);
		var entities = CollUtil.map(chapterRes, (item) -> {
			return BookContentDraft.$.produce((draft) -> {
				draft.setContent(item.getBookContent().getContent())
						.setChapter(BookChapterDraft.$.produce((chapterDraft) -> {
							chapterDraft.setBookId(dbBookId)
									.setChapterNum(item.getBookChapter().getChapterNum() - 1)
									.setChapterName(item.getBookChapter().getChapterName())
									.setWordCount(item.getBookChapter().getWordCount())
									.setVipState(0);
						}));
			});
		}, true);
		var items = sqlClient.saveEntities(entities).getItems();
		log.info("{} 章节写入成功", items.size());
		var lastChapter = items.get(items.size() - 1).getModifiedEntity().chapter();
		var bookTable = BookInfoTable.$;
		int updatedCount = sqlClient.createUpdate(bookTable)
				                   .where(bookTable.id().eq(dbBookId))
				                   .set(bookTable.wordCount(), entities.stream().mapToLong(item -> item.chapter().wordCount()).sum())
				                   .set(bookTable.lastChapterId(), BigInteger.valueOf(lastChapter.id()))
				                   .set(bookTable.lastChapterName(), lastChapter.chapterName())
				                   .set(bookTable.lastChapterUpdateTime(), LocalDateTime.now()).execute();
	}
}
