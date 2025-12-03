package com.novel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.novel.constant.CacheConstant;
import com.novel.dto.BookChapterDto;
import com.novel.dto.BookStateVO;
import com.novel.dto.req.BookChapterAddReq;
import com.novel.dto.req.ChapterPageQueryReq;
import com.novel.po.book.BookInfo;
import com.novel.po.book.BookInfoTable;
import com.novel.repository.BookRepository;
import com.novel.result.PageResult;
import com.novel.service.BookService;
import com.novel.user.dto.book.*;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.util.Tuple;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author YYJY
 */
@Service
public class BookServiceImpl implements BookService {
	
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Override
	public List<BookCategoryView> listCategory(Integer id) {
		String jsonStr = redisTemplate.opsForValue().get(CacheConstant.CACHE_BOOK_CATEGORY + id);
		if (StrUtil.isNotBlank(jsonStr)) {
			return JSONUtil.toList(jsonStr, BookCategoryView.class);
		}
		List<BookCategoryView> bookCategoryViews = bookRepository.listCategory(id);
		redisTemplate.opsForValue().set(CacheConstant.CACHE_BOOK_CATEGORY + id, JSONUtil.toJsonStr(bookCategoryViews));
		return bookCategoryViews;
	}
	
	@Override
	public PageResult<AllBookView> listAll(AllBookQueryInput params, int pageNum, int pageSize) {
		return bookRepository.listAll(params, pageNum, pageSize);
	}
	
	@Override
	public BookInfoView getBookInfo(long id) {
		return bookRepository.findBookInfoById(id);
	}
	
	@Override
	public BookChapterDto listBookChapter(long id, Integer isAll, Integer orderBy) {
		return bookRepository.listBookChapter(id, isAll, orderBy);
	}
	
	@Override
	public List<BookInfo> listRandomBooks(long categoryId, long bookId) {
		return bookRepository.listRandomBooks(categoryId, bookId);
	}
	
	@Override
	public List<BookRankView> listRankBooks(Integer rankType) {
		Order order = null;
		switch(rankType) {
			case 1 -> order = BookInfoTable.$.visitCount().desc();
			case 2 -> order = BookInfoTable.$.createTime().desc();
			case 3 -> order = BookInfoTable.$.lastChapterUpdateTime().desc();
			case 4 -> order = BookInfoTable.$.commentCount().desc();
		}
		return bookRepository.listRankBooks(order);
	}
	
	@Transactional
	@Override
	public BookChapterContentView getBookContent(long bookId, long chapterId) {
		BookChapterContentView bookContent = bookRepository.getBookContent(bookId, chapterId);
		// 获取上下两个章节id
		Tuple<String, String> chapter = bookRepository.getChapter(bookId, bookContent.getChapterNum());
		bookContent.setNextChapterId(chapter._1());
		bookContent.setPrevChapterId(chapter._2());
		
		
		// 更新浏览记录
			try {
				long userId = StpUtil.getLoginIdAsLong();
				// 是否在书架
				boolean exists = bookRepository.updateBookshelf(bookId, userId, chapterId);
				bookContent.setIsInBookShelf(exists ? 0 : 1);
				
				CompletableFuture.runAsync(() -> {
					bookRepository.updateBookReadHistory(userId, bookId, chapterId);
				});
			} catch (Exception e) {
				// 忽略
			}
		
		return bookContent;
	}
	
	@Override
	public BookStateVO getBookState(long bookId) {
			boolean exists = bookRepository.isInBookshelf(StpUtil.getLoginIdAsLong(), bookId);
			return new BookStateVO(exists ? 1 : 0);
	}
	
	@Override
	public List<BookInfoSearchView> search(String name) {
		return bookRepository.search(name);
	}
	
	@Override
	public PageResult<BookChapterView> pageForAdmin(ChapterPageQueryReq req) {
		return bookRepository.pageForAdmin(req);
	}
	
	@Override
	public void addForAdmin(BookChapterAddReq req) {
		bookRepository.addForAdmin(req);
	}
	
	@Override
	public void updateForAdmin(BookChapterAddReq req) {
		bookRepository.updateForAdmin(req);
	}
	
	@Override
	public String processContent(String content) {
		// 步骤1：去除所有的HTML标签和&nbsp;
		String cleanText = content.replaceAll("&nbsp;", "");
		
		// 步骤2：按<br/><br/>分割段落
		String[] paragraphs = cleanText.trim().split("<br/><br/>");
		
		// 步骤3：用<p>标签包裹每个段落
		StringBuilder result = new StringBuilder();
		for (String paragraph : paragraphs) {
			if (!paragraph.isEmpty()) {
				result.append("<p>").append(paragraph).append("</p>");
			}
		}
		return result.toString();
	}
	
	
}
