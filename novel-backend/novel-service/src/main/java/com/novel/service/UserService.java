package com.novel.service;

import com.novel.admin.dto.user.UserAddInput;
import com.novel.admin.dto.user.UserUpdateInput;
import com.novel.dto.req.UserPageQueryReq;
import com.novel.result.PageResult;
import com.novel.user.dto.user.*;

import java.util.List;

public interface UserService {
	UserLoginView login(UserLoginInput userLoginInput);
	
	UserLoginView register(UserRegisterInput userRegisterInput);
	
	void updateAvatar(String url);
	
	UserInfoView getUserInfo();
	
	void updateNickName(String nickName);
	
	List<BookReadHistoryView> listHistory();
	
	List<UserBookShelfView> listBookshelf();
	
	// --------------- Admin ------------------
	void updateBookshelf(int optType, long bookId);
	
	void addUser(UserAddInput user);
	
	void updateUser(UserUpdateInput user);
	
	void delete(Long id);
	
	PageResult<com.novel.admin.dto.user.UserInfoView> page(UserPageQueryReq req);
}
