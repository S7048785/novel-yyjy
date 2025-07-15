package com.novel.controller.front;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Tag(name = "验证码控制器")
@Controller
@RequestMapping("/captcha")
public class CaptchaController {

	@Autowired
	private DefaultKaptcha defaultKaptcha;
	
	/**
	 * 获取验证码图片
	 * 此方法通过HTTP GET请求生成并返回一个验证码图片，同时设置响应头以避免缓存
	 * 验证码文本被存储在HttpSession中，供后续验证使用
	 *
	 * @param request 用于获取当前会话或创建新会话的HttpServletRequest对象
	 * @param response 用于设置响应头和输出流的HttpServletResponse对象
	 * @throws IOException 如果在写入图片到输出流时发生错误
	 */
	@GetMapping
	public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 禁止缓存此响应，确保每次请求都生成新的验证码
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		
		// 设置响应内容类型为JPEG图片
		response.setContentType("image/jpeg");
		
		// 生成验证码文本
		String capText = defaultKaptcha.createText();
		// 将验证码文本存储在会话中，供后续验证使用
		request.getSession().setAttribute("captcha", capText);
		
		// 根据验证码文本生成验证码图片
		BufferedImage bi = defaultKaptcha.createImage(capText);
		// 将生成的验证码图片写入响应的输出流
		ImageIO.write(bi, "jpg", response.getOutputStream());
	}
	
}
