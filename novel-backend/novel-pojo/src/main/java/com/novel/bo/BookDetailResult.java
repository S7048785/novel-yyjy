package com.novel.bo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class BookDetailResult {
    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("pic_url")
    private String picUrl;

    @JsonProperty("book_name")
    private String bookName;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("book_desc")
    private String bookDesc;
    
    @JsonProperty("chapter_count")
    private int ChapterCount;
}