package com.novel.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Nyxcirea
 * @date 2026/3/12
 * @description: TODO
 */
@Data
@AllArgsConstructor
public class DashboardStatistics {
	private Long novelCount;
	private Long chapterCount;
	private Long userCount;
}
