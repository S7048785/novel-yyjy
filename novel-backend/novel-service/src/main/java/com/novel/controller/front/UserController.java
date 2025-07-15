package com.novel.controller.front;

import com.novel.dto.user.*;
import com.novel.exception.BaseException;
import com.novel.po.user.UserBookshelfTable;
import com.novel.result.PageResult;
import com.novel.result.Result;
import com.novel.service.UserService;
import com.novel.utils.AliyunOSSUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

/**
 * @author YYJY
 */
@Slf4j
@Tag(name = "用户")
@RestController
@RequestMapping("user")
public class UserController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private AliyunOSSUtils aliyunOSSUtils;
	
	@Operation(summary = "登录")
	@PostMapping("/login")
	public Result<UserLoginView> login(HttpServletRequest request, @Validated @RequestBody UserLoginInput userLoginInput) {
		String captcha = (String) request.getSession().getAttribute("captcha");
		String captcha1 = userLoginInput.getCaptcha();
		log.info("captcha: {}, captcha1: {}", captcha, captcha1);
		if (captcha == null || !captcha.equals(captcha1)) {
			return Result.fail("验证码错误");
		}
		UserLoginView user = userService.login(userLoginInput);
		return Result.ok(user);
	}
	
	@Operation(summary = "注册")
	@PostMapping("/register")
	public Result<UserLoginView> register(HttpServletRequest request, @Validated @RequestBody UserRegisterInput userRegisterInput) {
		
		String captcha = (String) request.getSession().getAttribute("captcha");
		String captcha1 = userRegisterInput.getCaptcha();
		if (captcha == null || !captcha.equals(captcha1)) {
			return Result.fail("验证码错误");
		}
		
		UserLoginView user = userService.register(userRegisterInput);
		return Result.ok(user);
	}
	
	@Operation(summary = "上传头像")
	@PostMapping("/upload")
	public Result<String> upload(@RequestPart MultipartFile file) throws Exception {
		
		if (!isImage(file)) {
			throw new BaseException("请上传正确格式的图片");
		}
		
		String url = aliyunOSSUtils.upload(file.getBytes(), file.getOriginalFilename());

		userService.updateAvatar(url);
		
		return Result.ok(url);
	}
	
	@Operation(summary = "获取用户信息")
	@GetMapping("/info")
	public Result<UserInfoView> getUserInfo(@RequestHeader("Authorization") String token) {
		UserInfoView user  = userService.getUserInfo(token);
		return Result.ok(user);
	}
	
	@Operation(summary = "修改昵称")
	@PutMapping("/nickName")
	public Result<String> updateNickName(@Validated @Size(min = 2, max = 10, message = "昵称长度必须在2-10之间") String nickName) {
		userService.updateNickName(nickName);
		return Result.ok();
	}
	
	@Operation(summary = "历史浏览")
	@GetMapping("/history")
	public Result<List<BookReadHistoryView>> listHistory() {
		return Result.ok(userService.listHistory());
	}
	
	@Operation(summary = "书架")
	@GetMapping("/bookshelf")
	public Result<List<UserBookShelfView>> listBookshelf() {
		return Result.ok(userService.listBookshelf());
	}
	
	@Operation(summary = "添加/移除书架")
	@PutMapping("/bookshelf")
	public Result<Void> updateBookshelf(int optType, long bookId) {
		userService.updateBookshelf(optType, bookId);
		return Result.ok();
	}

	/**
	 * 通过读取文件并获取其width及height的方式，来判断判断当前文件是否图片，这是一种非常简单的方式。
	 */
	public boolean isImage(MultipartFile imageFile) {
		if (imageFile == null || imageFile.isEmpty()) {
			return false;
		}
		
		try (InputStream inputStream = new ByteArrayInputStream(imageFile.getBytes())) {
			BufferedImage image = ImageIO.read(inputStream);
			return image != null && image.getWidth() > 0 && image.getHeight() > 0;
		} catch (IOException e) {
			return false;
		}
	}
}
