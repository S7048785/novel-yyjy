/**
 * 小说信息
 */
export interface LastUpdateBookView {
    /**
     * 主键
     */
    readonly id: string;
    readonly categoryId?: string | undefined;
    /**
     * 类别名
     */
    readonly categoryName?: string | undefined;
    /**
     * 小说名
     */
    readonly bookName: string;
    /**
     * 最新章节名
     */
    readonly lastChapterName?: string | undefined;
    /**
     * 作家名
     */
    readonly authorName: string;
    /**
     * 最新章节更新时间
     */
    readonly lastChapterUpdateTime?: string | undefined;
}
