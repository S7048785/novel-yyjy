package com.novel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookStateVO {
	
	@Schema(description = "是否在书架中 0-不在 1-在")
	private int isInBookShelf;
}
