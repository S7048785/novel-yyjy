package com.novel.exception;

/**
 * 密码错误异常
 */
public class PasswordErrorException extends BaseException{
	public PasswordErrorException(String username, String message) {
		super(username + " " +message);
	}
	
	public PasswordErrorException() {
	}
}
