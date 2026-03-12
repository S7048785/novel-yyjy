/**
 * 小说信息
 */
export interface BookInfoView {
    /**
     * 主键
     */
    readonly id: string;
    /**
     * 小说名
     */
    readonly bookName: string;
    /**
     * 作家名
     */
    readonly authorName: string;
    /**
     * 书籍描述
     */
    readonly bookDesc: string;
    /**
     * 书籍状态;0-连载中 1-已完结
     */
    readonly bookStatus: number;
    /**
     * 小说封面地址
     */
    readonly picUrl: string;
    readonly categoryId?: string | undefined;
    /**
     * 类别名
     */
    readonly categoryName?: string | undefined;
    /**
     * 最新章节更新时间
     */
    readonly lastChapterUpdateTime?: string | undefined;
    /**
     * 最新章节ID
     */
    readonly lastChapterId?: number | undefined;
    /**
     * 最新章节名
     */
    readonly lastChapterName?: string | undefined;
    /**
     * 点击量
     */
    readonly visitCount: number;
    /**
     * 总字数
     */
    readonly wordCount: number;
    /**
     * 评论数
     */
    readonly commentCount: number;
}
