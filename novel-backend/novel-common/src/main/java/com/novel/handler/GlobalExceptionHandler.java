package com.novel.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.novel.exception.BaseException;
import com.novel.result.Result;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	//@ExceptionHandler
	//public Result handleException(Exception e) {
	//	log.error("全局异常处理器，拦截到异常：{}", e.getMessage());
	//	return Result.error("服务器异常，请联系管理员");
	//}
	/**
	 * 捕获业务异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	public Result handleBaseException(BaseException e) {
		//log.error("异常信息: {}", e.getMessage());
		return Result.fail(e.getMessage());
	}
	
	/**
	 * 捕获参数验证失败的异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		
		ArrayList<String> errorMsg = new ArrayList<>();
		List<FieldError> errors = bindingResult.getFieldErrors();
		errors.forEach(error -> {
			errorMsg.add(error.getDefaultMessage());
		});
		log.error("参数验证失败：{}", errorMsg);
		return Result.fail(String.join(";", errorMsg));
	}
	
	@ExceptionHandler
	public ResponseEntity<String> handleNotLoginException(NotLoginException e) {
		// 返回 401 状态码和错误信息
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}
}
