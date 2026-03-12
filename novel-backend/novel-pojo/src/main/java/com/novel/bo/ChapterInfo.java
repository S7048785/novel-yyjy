package com.novel.bo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ChapterInfo {
    @JsonProperty("chapter_name")
    private String chapterName;

    @JsonProperty("word_count")
    private Integer wordCount;

    @JsonProperty("chapter_num")
    private Integer chapterNum;
}