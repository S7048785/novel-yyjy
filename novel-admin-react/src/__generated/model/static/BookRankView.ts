/**
 * 小说信息
 */
export interface BookRankView {
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
     * 最新章节名
     */
    readonly lastChapterName?: string | undefined;
    /**
     * 最新章节ID
     */
    readonly lastChapterId?: number | undefined;
    /**
     * 作家名
     */
    readonly authorName: string;
    /**
     * 总字数
     */
    readonly wordCount: number;
    /**
     * 小说封面地址
     */
    readonly picUrl: string;
    /**
     * 最新章节更新时间
     */
    readonly lastChapterUpdateTime?: string | undefined;
    /**
     * 书籍描述
     */
    readonly bookDesc: string;
}
