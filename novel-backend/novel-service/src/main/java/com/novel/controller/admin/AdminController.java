package com.novel.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.novel.dto.req.AdminLoginReq;
import com.novel.po.book.BookChapterTable;
import com.novel.po.book.BookInfoTable;
import com.novel.po.user.UserInfoTable;
import com.novel.result.Result;
import com.novel.service.UserService;
import com.novel.user.dto.user.UserInfoView;
import com.novel.user.dto.user.UserLoginView;
import com.novel.vo.DashboardStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.client.meta.Api;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nyxcirea
 * date 2026/3/11
 */
@Slf4j
@Api
@Tag(name = "后台模块")
@RequestMapping("/admin")
@RestController
public class AdminController {
	private final UserService userService;
	private final JSqlClient sqlClient;
	public AdminController(UserService userService, JSqlClient sqlClient) {
		this.userService = userService;
		this.sqlClient = sqlClient;
	}
	
	@Api
	@Operation(summary = "管理员登录")
	@PostMapping("/login")
	public Result<Void> login(HttpServletRequest request, @Validated @RequestBody AdminLoginReq loginReq) {
		String captcha = (String) request.getSession().getAttribute("captcha");
		String captcha1 = loginReq.getCaptcha();
		log.info("captcha: {}, captcha1: {}", captcha, captcha1);
		
		if (captcha == null || !captcha.equals(captcha1)) {
			return Result.fail("验证码错误");
		}
		UserLoginView user = userService.loginForAdmin(loginReq);
		
		StpUtil.login(user.getId());
		return Result.ok();
	}
	
	@Api
	@SaCheckRole("admin")
	@Operation(summary = "获取管理员登录信息")
	@GetMapping("/me")
	public Result<UserInfoView> me() {
		UserInfoView user = userService.getUserInfo();
		return Result.ok(user);
	}
	
	@Api
	@SaCheckRole("admin")
	@Operation(summary = "退出登录")
	@PostMapping("/logout")
	public Result<Void> logout() {
		StpUtil.logout();
		return Result.ok();
	}
	
	@Api
	@SaCheckRole("admin")
	@Cacheable(cacheNames = "dashboardCache")
	@Operation(summary = "仪表盘数据")
	@GetMapping("/dashboard")
	public Result<DashboardStatisticsVO> dashboard() {
		//new DashboardStatistics()
		var result = new DashboardStatisticsVO(
				sqlClient
						.createQuery(BookInfoTable.$)
						.select(
								BookInfoTable.$.asTableEx().id().count()
						).fetchOne(),
		sqlClient.createQuery(BookChapterTable.$)
				.select(
						BookChapterTable.$.asTableEx().id().count()
				).fetchOne(),
		sqlClient.createQuery(UserInfoTable.$)
				.select(
						UserInfoTable.$.asTableEx().id().count()
				).fetchOne()
		);
		
		return Result.ok(result);
	}
}
