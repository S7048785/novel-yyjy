package com.novel.service;

import com.novel.user.dto.book.HomeBookRankView;
import com.novel.user.dto.book.LastInsertBookView;
import com.novel.user.dto.book.LastUpdateBookView;
import com.novel.user.dto.book.VisitRankBookView;
import com.novel.user.dto.home.HomeBookView;

import java.util.List;

public interface HomeService {
	List<HomeBookView> listHomeBooks();
	
	List<VisitRankBookView> listBookVisitRank();
	
	List<HomeBookRankView> listNewestRankBooks();
	
	List<LastUpdateBookView> listRecentUpdateBook();
	
	List<LastInsertBookView> listNewestAddBooks();
}
