package com.novel.utils;

/**
 * @author Nyxcirea
 * date 2026/3/13
 * description: TODO
 */
public interface ProgressCallback {
	/**
	 * 当进度更新时调用此方法
	 *
	 * @param currentChapter 当前章节
	 * @param totalChapters  总共章节数
	 * @param progress       进度百分比 (0.0 - 1.0)
	 */
	void onProgressUpdate(int currentChapter, int totalChapters, double progress);
}