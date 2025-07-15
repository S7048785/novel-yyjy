package com.novel.service;

import com.novel.dto.user.*;
import com.novel.po.user.UserBookshelfTable;
import com.novel.po.user.UserInfo;
import com.novel.result.PageResult;

import java.util.List;

public interface UserService {
	UserLoginView login(UserLoginInput userLoginInput);
	
	UserLoginView register(UserRegisterInput userRegisterInput);
	
	void updateAvatar(String url);
	
	UserInfoView getUserInfo(String token);
	
	void updateNickName(String nickName);
	
	List<BookReadHistoryView> listHistory();
	
	List<UserBookShelfView> listBookshelf();
	
	void updateBookshelf(int optType, long bookId);
}
