package com.novel.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPageQueryReq {
	
	/** 邮箱 */
	String email;
	/** 排序 0-降序 1-升序 */
	int sort = 0;
	/** 排序字段 0-用户id 1-邮箱 2-注册时间 */
	String sortField;
	/** 是否封禁 0-正常 1-封禁 */
	Integer status;
	/** 注册开始时间 */
	LocalDateTime startTime;
	LocalDateTime endTime = LocalDateTime.now();
	/** 当前页码 */
	int pageNum = 1;
	/** 每页记录数 */
	int pageSize = 10;
}
