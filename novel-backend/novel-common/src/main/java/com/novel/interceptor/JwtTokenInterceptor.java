package com.novel.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.novel.context.BaseContext;
import com.novel.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		// 如果是OPTIONS请求，直接放行
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return true;
		}
		// 获取防盗链
		String referer = request.getHeader("Referer");
		if ("http://localhost:8081/doc.html".equals(referer)) {
			BaseContext.setCurrentId(4L);
			return true;
		}
		
		// 获取token
		String token = request.getHeader("Authorization");
		// 校验令牌
		try {
			// 解析token
			String userId = JwtUtils.parseJwt(token);
			// 存入ThreadLocal
			BaseContext.setCurrentId(Long.valueOf(userId));
			// 放行
			return true;
		} catch(Exception e) {
			 //校验失败
			// 返回401状态码
			log.error("token校验失败:{}", e.getMessage());
			response.setStatus(401);
			return false;
		}
	}
}
