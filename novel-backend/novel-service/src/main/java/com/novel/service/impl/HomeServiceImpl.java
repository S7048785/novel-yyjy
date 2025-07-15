package com.novel.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.novel.constant.CacheConstant;
import com.novel.dto.book.HomeBookRankView;
import com.novel.dto.book.LastInsertBookView;
import com.novel.dto.book.LastUpdateBookView;
import com.novel.dto.home.HomeBookView;
import com.novel.po.home.HomeBookDraft;
import com.novel.po.home.HomeBookTable;
import com.novel.repository.BookRepository;
import com.novel.service.HomeService;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.sql.Serialized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author YYJY
 */
@Service
public class HomeServiceImpl implements HomeService {
	
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Override
	public List<HomeBookView> listHomeBooks() {
		String jsonStr = redisTemplate.opsForValue().get(CacheConstant.CACHE_HOME_BOOKS);
		if (StrUtil.isNotBlank(jsonStr)) {
			JSONArray objects = JSONUtil.parseArray(jsonStr);
			return JSONUtil.toList(objects, HomeBookView.class);
		}
		List<HomeBookView> homeBookViews = bookRepository.listHomeBooks();
		redisTemplate.opsForValue().set(CacheConstant.CACHE_HOME_BOOKS, JSONUtil.toJsonStr(homeBookViews));
		return homeBookViews;
	}
	
	@Override
	public List<HomeBookRankView> listBookVisitRank() {
		return bookRepository.listBookVisitRank(10);
	}
	
	@Override
	public List<HomeBookRankView> listNewestRankBooks() {
		/**
		 * SELECT book_name, score, visit_count
		 * FROM book_info
		 * ORDER BY score DESC, visit_count DESC, create_time
		 * LIMIT 50;
		 */
		
		return bookRepository.listNewestRankBooks();
	}
	
	@Override
	public List<LastUpdateBookView> listRecentUpdateBook() {
		return bookRepository.listRecentUpdateBook();
	}
	
	@Override
	public List<LastInsertBookView> listNewestAddBooks() {
		
		return bookRepository.listNewestAddBooks();
	}
	
	
}
