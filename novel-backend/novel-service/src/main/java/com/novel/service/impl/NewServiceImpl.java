package com.novel.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.novel.constant.CacheConstant;
import com.novel.dto.news.LaetstNewsView;
import com.novel.po.news.NewsInfoTable;
import com.novel.result.Result;
import com.novel.service.NewService;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author YYJY
 */
@Service
public class NewServiceImpl implements NewService {
	
	@Autowired
	private JSqlClient sqlClient;
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Override
	public List<LaetstNewsView> latestList() {
		String s = redisTemplate.opsForValue().get(CacheConstant.CACHE_NEWS_LATEST);
		if (StrUtil.isNotBlank(s)) {
			return JSONUtil.toList(s, LaetstNewsView.class);
		}
		List<LaetstNewsView> execute = sqlClient.createQuery(NewsInfoTable.$)
				                               .select(NewsInfoTable.$.fetch(LaetstNewsView.class))
				                               .limit(2)
				                               .execute();
		redisTemplate.opsForValue().set(CacheConstant.CACHE_NEWS_LATEST, JSONUtil.toJsonStr(execute));
		return execute;
	}
}
