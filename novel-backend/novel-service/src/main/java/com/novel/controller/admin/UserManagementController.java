package com.novel.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.novel.admin.dto.user.UserAddInput;
import com.novel.dto.req.UserPageQueryReq;
import com.novel.dto.req.UserUpdateReq;
import com.novel.exception.BaseException;
import com.novel.po.user.UserInfo;
import com.novel.repository.UserRepository;
import com.novel.result.PageResult;
import com.novel.result.Result;
import com.novel.service.UserService;
import com.novel.utils.AliyunOSSUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.babyfish.jimmer.client.meta.Api;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * @author Nyxcirea
 * date 2026/3/12
 * description: 后台用户管理控制器
 */
@Api
@SaCheckRole("admin")
@Tag(name = "后台用户管理")
@RequestMapping("/admin/user")
@RestController
public class UserManagementController {
	private final UserService userService;
	private final UserRepository userRepository;
	private final AliyunOSSUtils aliyunOSSUtils;
	
	public UserManagementController(UserService userService, UserRepository userRepository, AliyunOSSUtils aliyunOSSUtils) {
		this.userService = userService;
		this.userRepository = userRepository;
		this.aliyunOSSUtils = aliyunOSSUtils;
	}

	@Api
	@Operation(summary = "分页查询用户列表")
	@GetMapping("/page")
	public Result<PageResult<UserInfo>> page(@Validated UserPageQueryReq req) {
		var pageResult =userService.page(req);
		return Result.ok(pageResult);
	}

	@Api
	@Operation(summary = "根据ID获取用户详情")
	@GetMapping("/{id}")
	public Result<UserInfo> getById(@PathVariable long id) {
		UserInfo userInfo = userRepository.getById(id);
		return Result.ok(userInfo);
	}

	@Api
	@CacheEvict(cacheNames = "dashboardCache")
	@Operation(summary = "添加用户")
	@PostMapping
	public Result<Void> add(@Validated @RequestBody UserAddInput user) {
		userService.addUser(user);
		return Result.ok();
	}

	@Api
	@Operation(summary = "修改用户")
	@PutMapping
	public Result<Void> update(@Validated @RequestBody UserUpdateReq user) {
		userService.updateUser(user);
		return Result.ok();
	}

	@Api
	@CacheEvict(cacheNames = "dashboardCache")
	@Operation(summary = "删除用户")
	@DeleteMapping("/{id}")
	public Result<Void> delete(@PathVariable String id) {
		userService.delete(id);
		return Result.ok();
	}
	
	@Api
	@SaCheckRole
	@Operation(summary = "上传头像")
	@PostMapping("/upload")
	public Result<String> upload(@RequestPart MultipartFile file) throws Exception {
		
		if (!aliyunOSSUtils.isImage(file)) {
			throw new BaseException("请上传正确格式的图片");
		}
		
		String url = aliyunOSSUtils.upload(file.getBytes(), Objects.requireNonNull(file.getOriginalFilename()));
		
		return Result.ok(url);
	}
}
