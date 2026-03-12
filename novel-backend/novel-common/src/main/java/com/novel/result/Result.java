package com.novel.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {
	private Integer code;
	private String msg;
	private T data;
	
	public static <T> Result<T> ok() {
		Result<T> result = new Result<>();
		result.setCode(1);
		result.setMsg("ok");
		return result;
	}
	public static <T> Result<T> ok(T data) {
		Result<T> result = new Result<>();
		result.setCode(1);
		result.setMsg("ok");
		result.setData(data);
		return result;
	}
	public static <T> Result<T> fail(String msg) {
		Result<T> result = new Result<>();
		result.setCode(0);
		result.setMsg(msg);
		return result;
	}
	
	public static <T> Result<T> fail(int code, String msg) {
		Result<T> result = new Result<>();
		result.setCode(code);
		result.setMsg(msg);
		return result;
	}
}
