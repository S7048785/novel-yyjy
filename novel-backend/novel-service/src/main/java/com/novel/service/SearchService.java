package com.novel.service;

import com.novel.result.PageResult;
import com.novel.user.dto.book.AllBookQueryInput;
import com.novel.user.dto.book.AllBookView;
import com.novel.user.dto.book.BookInfoSearchView;

import java.util.List;

public interface SearchService {
	
	/**
	 * 小说搜索
	 *
	 * @param condition 搜索条件
	 * @param pageNum   页码
	 * @param pageSize  每页数量
	 * @return 搜索结果
	 */
	PageResult<AllBookView> conditionSearchBooks(AllBookQueryInput condition, int pageNum, int pageSize);
	
	List<BookInfoSearchView> searchBooksByName(String name);
}
