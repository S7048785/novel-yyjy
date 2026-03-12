package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author Nyxcirea
 * date 2026/3/12
 * description: TODO
 */
@Data
public class UserUpdateReq {
	@NotBlank
	String id;
	@NotBlank
	String email;
	@NotBlank
	String nickName;
	String userPhoto;
	@NotBlank
	@Pattern(regexp = "^(admin|user)$", message = "role字段只能是admin或user")
	String role;
	int status;
}
