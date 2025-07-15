package com.novel.context;

import org.springframework.stereotype.Component;


public class BaseContext {

	private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
	
	public static Long getCurrentId() {
		return threadLocal.get();
	}
	
	public static void setCurrentId(Long val) {
		threadLocal.set(val);
	}
	
	public static void removeCurrentId() {
		threadLocal.remove();
	}
}
