package com.novel.service;

public interface NovelScraperService {

	long addNovelById(String bookId);


	void scrapeChapters(String bookId, Long dbBookId, Integer count, String taskId) throws Exception;

}