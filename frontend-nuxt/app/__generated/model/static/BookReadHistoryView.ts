/**
 * 用户阅读历史
 */
export interface BookReadHistoryView {
    /**
     * 主键
     */
    readonly id: string;
    /**
     * 更新时间;
     */
    readonly updateTime?: string | undefined;
    readonly chapterId: string;
    readonly chapterName: string;
    readonly bookId: string;
    readonly bookName: string;
    readonly categoryName?: string | undefined;
    readonly authorName: string;
}
