package com.novel.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Nyxcirea
 * date 2026/3/12
 * description: TODO
 */
@Data
public class AdminLoginReq {
	@NotBlank(message = "用户名不能为空")
	@Schema(description = "用户名")
	private String username;
	@NotBlank(message = "密码不能为空")
	@Schema(description = "密码")
	private String password;
	@NotBlank(message = "验证码不能为空")
	@Schema(description = "验证码")
	private String captcha;
}
