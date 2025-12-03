package com.novel.service;


import com.novel.user.dto.news.LaetstNewsView;

import java.util.List;

public interface NewService {
	
	List<LaetstNewsView> latestList();
}
