package com.novel.po.user;

import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.jackson.JsonConverter;
import org.babyfish.jimmer.jackson.LongToStringConverter;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Key;

import org.babyfish.jimmer.sql.GenerationType;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 用户信息
 */
@Entity
public interface UserInfo {
	
	@Id
	@JsonConverter(LongToStringConverter.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY
	)
	long id();
	
	/**
	 * 登录名
	 */
	@Key
	String email();
	
	/**
	 * 登录密码
	 */
	String password();
	
	/**
	 * 昵称
	 */
	String nickName();
	
	/**
	 * 用户头像
	 */
	@Null
	String userPhoto();
	
	/**
	 * 用户状态;0-正常
	 */
	int status();
	
	/**
	 * 创建时间
	 */
	@Null
	LocalDateTime createTime();
	
	/**
	 * 更新时间
	 */
	@Null
	LocalDateTime updateTime();
}

