package com.novel.bo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CrawlTaskStatus {
    private String taskId;
    private String novelId;
    private String novelName;  // 小说名称
    private String message;
    private String status; // "采集中", "已完成", "采集失败"
    private LocalDateTime startTime;
    private int currentChapter;
    private int totalChapters;
    private double progress; // 0.0 - 100.0

    public CrawlTaskStatus(String taskId, String novelId, int totalChapters) {
        this.taskId = taskId;
        this.novelId = novelId;
        this.status = "采集中";
        this.message = "正在采集第 1 章";
        this.startTime = LocalDateTime.now();
        this.totalChapters = totalChapters;
        this.currentChapter = 0;
        this.progress = 0.0;
    }
}