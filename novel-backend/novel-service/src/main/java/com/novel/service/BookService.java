package com.novel.service;

import com.novel.dto.BookChapterDto;
import com.novel.dto.BookStateVO;
import com.novel.dto.book.*;
import com.novel.po.book.BookInfo;
import com.novel.result.PageResult;

import java.util.List;

public interface BookService {
	List<BookCategoryView> listCategory(Integer id);
	
	PageResult<AllBookView> listAll(AllBookQueryInput params, int pageNum, int pageSize);
	
	BookInfoView getBookInfo(long id);
	
	BookChapterDto listBookChapter(long id, Integer isAll, Integer orderBy);
	
	List<BookInfo> listRandomBooks(long categoryId, long bookId);
	
	List<BookRankView> listRankBooks(Integer rankType);
	
	BookChapterContentView getBookContent(String token, long bookId, long chapterId);
	
	BookStateVO getBookState(long bookId);
}
