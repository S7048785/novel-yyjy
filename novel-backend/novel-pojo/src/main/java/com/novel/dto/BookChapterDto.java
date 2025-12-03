package com.novel.dto;

import com.novel.user.dto.book.BookChapterView;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookChapterDto {
	List<BookChapterView> bookChapters;
	Long total;
}
