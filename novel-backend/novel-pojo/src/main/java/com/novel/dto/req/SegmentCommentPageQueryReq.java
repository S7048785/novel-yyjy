package com.novel.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SegmentCommentPageQueryReq {
	@NotNull
	Long chapterId;
	@NotNull
	Integer segmentNum;
	/** 当前页码 */
	int pageNum = 1;
	/** 每页记录数 */
	int pageSize = 10;
}
