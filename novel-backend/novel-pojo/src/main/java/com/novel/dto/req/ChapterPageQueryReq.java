package com.novel.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChapterPageQueryReq {
	/** 用户ID */
	Long chapterId;
	/** 用户名 */
	String chapterName;
	/** 书名 */
	String bookName;
	/** 创建时间 */
	LocalDateTime startTime;
	LocalDateTime endTime = LocalDateTime.now();
	/** 当前页码 */
	int pageNum = 1;
	/** 每页记录数 */
	int pageSize = 10;
}
