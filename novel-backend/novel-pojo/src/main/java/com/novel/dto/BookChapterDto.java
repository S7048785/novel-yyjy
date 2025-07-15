package com.novel.dto;

import com.novel.dto.book.BookChapterView;
import com.novel.po.book.BookChapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.babyfish.jimmer.sql.Default;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Data
@AllArgsConstructor
public class BookChapterDto {
	List<BookChapterView> bookChapters;
	Long total;
}
