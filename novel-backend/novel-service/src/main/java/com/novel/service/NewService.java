package com.novel.service;

import com.novel.dto.news.LaetstNewsView;
import com.novel.result.Result;

import java.util.List;

public interface NewService {
	
	List<LaetstNewsView> latestList();
}
