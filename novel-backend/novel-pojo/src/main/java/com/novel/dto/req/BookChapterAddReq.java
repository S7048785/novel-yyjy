package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookChapterAddReq {
	@NotNull
	Long bookId;
	@NotBlank
	String chapterName;
	@NotBlank
	String content;
}
