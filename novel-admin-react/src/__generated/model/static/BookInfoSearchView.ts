/**
 * 小说信息
 */
export interface BookInfoSearchView {
    /**
     * 主键
     */
    readonly id: string;
    /**
     * 类别名
     */
    readonly categoryName?: string | undefined;
    /**
     * 小说名
     */
    readonly bookName: string;
    /**
     * 作家名
     */
    readonly authorName: string;
    /**
     * 小说封面地址
     */
    readonly picUrl: string;
    /**
     * 书籍描述
     */
    readonly bookDesc: string;
    /**
     * 最新章节ID
     */
    readonly lastChapterId?: number | undefined;
    /**
     * 最新章节名
     */
    readonly lastChapterName?: string | undefined;
}
