package com.novel.service;

import com.novel.dto.book.HomeBookRankView;
import com.novel.dto.book.LastInsertBookView;
import com.novel.dto.book.LastUpdateBookView;
import com.novel.dto.home.HomeBookView;
import com.novel.result.Result;

import java.util.List;

public interface HomeService {
	List<HomeBookView> listHomeBooks();
	
	List<HomeBookRankView> listBookVisitRank();
	
	List<HomeBookRankView> listNewestRankBooks();
	
	List<LastUpdateBookView> listRecentUpdateBook();
	
	List<LastInsertBookView> listNewestAddBooks();
}
