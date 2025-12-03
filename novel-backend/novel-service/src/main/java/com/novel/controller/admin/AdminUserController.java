package com.novel.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.novel.admin.dto.user.UserAddInput;
import com.novel.admin.dto.user.UserInfoView;
import com.novel.admin.dto.user.UserUpdateInput;
import com.novel.dto.req.UserPageQueryReq;
import com.novel.result.PageResult;
import com.novel.result.Result;
import com.novel.service.UserService;
import com.novel.user.dto.user.UserLoginInput;
import com.novel.user.dto.user.UserLoginView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin用户管理")
@RequestMapping("admin/user")
@RestController
public class AdminUserController {
	@Autowired
	private UserService userService;
	@Operation(summary = "登录")
	@PostMapping("login")
	public Result<UserLoginView> login(UserLoginInput user) {
		UserLoginView login = userService.login(user);
		StpUtil.login(login.getId());
		return Result.ok(login);
	}
	
	@Operation(summary = "添加用户")
	@PostMapping("add")
	public Result<Void> add(UserAddInput user) {
		userService.addUser(user);
		return Result.ok();
	}
	
	@Operation(summary = "编辑用户")
	@PutMapping("update")
	public Result<Void> update(UserUpdateInput user) {
		userService.updateUser(user);
		return Result.ok();
	}
	
	@Operation(summary = "删除用户")
	@DeleteMapping("delete/{id}")
	public Result<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return Result.ok();
	}
	
	
	@Operation(summary = "分页查询用户列表")
	@GetMapping("list")
	public PageResult<UserInfoView> list(UserPageQueryReq req) {
		return userService.page(req);
	}
}