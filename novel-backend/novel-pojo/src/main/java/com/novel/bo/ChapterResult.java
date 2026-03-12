package com.novel.bo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ChapterResult {
    @JsonProperty("book_chapter")
    private ChapterInfo bookChapter;

    @JsonProperty("book_content")
    private ContentInfo bookContent;
}