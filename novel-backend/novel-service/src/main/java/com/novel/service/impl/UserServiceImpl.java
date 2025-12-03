package com.novel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.novel.admin.dto.user.UserAddInput;
import com.novel.admin.dto.user.UserUpdateInput;
import com.novel.constant.MessageConstant;
import com.novel.dto.req.UserPageQueryReq;
import com.novel.exception.BaseException;
import com.novel.po.user.UserInfo;
import com.novel.po.user.UserInfoDraft;
import com.novel.repository.UserRepository;
import com.novel.result.PageResult;
import com.novel.service.UserService;
import com.novel.user.dto.user.*;
import com.novel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Override
	public UserLoginView login(UserLoginInput userLoginInput) {
		// 查询用户信息
		UserInfo userInfo = userRepository.getUserInfoByEmail(userLoginInput.getEmail());
		
		// 校验用户密码
		if (userInfo.password().equals(userLoginInput.getPassword())) {
			return new UserLoginView(userInfo);
		}
		
		throw new BaseException( MessageConstant.ACCOUNT_OR_PASSWORD_NOT_FOUND);
	}
	
	@Transactional
	@Override
	public UserLoginView register(UserRegisterInput userRegisterInput) {
		// 查询邮箱和昵称是否存在
		userRepository.isExist(userRegisterInput.getEmail(), userRegisterInput.getNickName());
		
		// 保存用户信息
		UserInfo produce = UserInfoDraft.$.produce(draft -> {
			draft.setEmail(userRegisterInput.getEmail());
			draft.setPassword(userRegisterInput.getPassword());
			draft.setNickName(userRegisterInput.getNickName());
			draft.setStatus(0);
			draft.setCreateTime(LocalDateTime.now());
			draft.setUpdateTime(LocalDateTime.now());
			// 默认头像
			draft.setUserPhoto("/public/image/default_avatar.jpg");
		});
		UserInfo info = userRepository.save(produce);
		UserLoginView userLoginView = new UserLoginView(info);
		
		// 生成token
		String token = JwtUtils.createJwt(String.valueOf(info.id()));
		userLoginView.setToken(token);
		return userLoginView;
	}
	
	@Override
	public void updateAvatar(String url) {
		userRepository.updateAvatar(url);
	}
	
	@Override
	public UserInfoView getUserInfo() {
		long userId = StpUtil.getLoginIdAsLong();
		
		UserInfo userInfo = userRepository.getUserInfo(userId);
		return new UserInfoView(userInfo);
	}
	
	@Override
	public void updateNickName(String nickName) {
		Long userId = StpUtil.getLoginIdAsLong();
		userRepository.updateNickName(userId, nickName);
	}
	
	@Override
	public List<BookReadHistoryView> listHistory() {
		return userRepository.listHistory();
	}
	
	@Override
	public List<UserBookShelfView> listBookshelf() {
		return userRepository.listBookshelf();
	}
	
	@Override
	public void updateBookshelf(int optType, long bookId) {
		Long userId = StpUtil.getLoginIdAsLong();
		userRepository.updateBookshelf(optType, userId, bookId);
	}
	
	@Override
	public void addUser(UserAddInput user) {
		userRepository.save(UserInfoDraft.$.produce(user.toEntity(),draft -> draft.setUpdateTime(LocalDateTime.now())));
	}
	
	@Override
	public void updateUser(UserUpdateInput user) {
		userRepository.updateUser(user);
	}
	
	@Override
	public void delete(Long id) {
		userRepository.delete(id);
	}
	
	@Override
	public PageResult<com.novel.admin.dto.user.UserInfoView> page(UserPageQueryReq req) {
		return userRepository.page(req);
	}
}
