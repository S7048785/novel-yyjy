/**
 * 小说信息
 */
export interface HomeBookRankView {
    /**
     * 主键
     */
    readonly id: string;
    /**
     * 小说封面地址
     */
    readonly picUrl: string;
    /**
     * 小说名
     */
    readonly bookName: string;
    /**
     * 书籍描述
     */
    readonly bookDesc: string;
    /**
     * 类别名
     */
    readonly categoryName?: string | undefined;
    /**
     * 作家名
     */
    readonly authorName: string;
}
