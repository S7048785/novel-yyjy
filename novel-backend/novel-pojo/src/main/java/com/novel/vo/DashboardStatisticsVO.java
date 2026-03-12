package com.novel.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Nyxcirea
 * date 2026/3/12
 * description: 仪表盘数据
 */
@Data
@AllArgsConstructor
public class DashboardStatisticsVO {
	private Long novelCount;
	private Long chapterCount;
	private Long userCount;
}
