package com.novel.service;

import com.novel.dto.BookChapterDto;
import com.novel.dto.BookStateVO;
import com.novel.dto.req.BookChapterAddReq;
import com.novel.dto.req.BookPageQueryReq;
import com.novel.dto.req.BookUpdateReq;
import com.novel.dto.req.ChapterPageQueryReq;
import com.novel.po.book.BookInfo;
import com.novel.result.PageResult;
import com.novel.user.dto.book.*;
import org.babyfish.jimmer.sql.fetcher.Fetcher;

import java.util.List;

public interface BookService {
	/**
	 * 分页查询小说列表
	 */
	PageResult<BookInfo> page(BookPageQueryReq req, Fetcher<BookInfo> bookViewOfAdmin);

	/**
	 * 根据ID获取小说详情
	 */
	BookInfo getById(long id, Fetcher<BookInfo> fetcher);

	/**
	 * 新增小说
	 */
	void addBook(BookUpdateReq req);

	/**
	 * 修改小说
	 */
	void updateBook(BookUpdateReq req);

	/**
	 * 删除小说
	 */
	void deleteBook(String id);

	List<BookCategoryView> listCategory(Integer id);
	
	PageResult<AllBookView> listAll(AllBookQueryInput params, int pageNum, int pageSize);
	
	BookInfoView getBookInfo(long id);
	
	BookChapterDto listBookChapter(long id, Integer isAll, Integer orderBy);
	
	List<BookInfo> listRandomBooks(long categoryId, long bookId);
	
	List<BookRankView> listRankBooks(Integer rankType);
	
	BookChapterContentView getBookContent(long bookId, long chapterId);
	
	BookStateVO getBookState(long bookId);
	
	List<BookInfoSearchView> search(String name);
	
	PageResult<BookChapterView> pageForAdmin(ChapterPageQueryReq req);
	
	void addForAdmin(BookChapterAddReq req);
	
	void updateForAdmin(BookChapterAddReq req);
	
	// 内容处理相关
	String processContent(String content);
}
