package com.novel.exception;

/**
 * 用户不存在异常
 */
public class UserAlreadyExistsException extends BaseException{
	public UserAlreadyExistsException(String username, String message) {
		super(username + " " + message);
	}
}
