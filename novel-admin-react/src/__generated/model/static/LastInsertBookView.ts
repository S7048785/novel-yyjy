/**
 * 小说信息
 */
export interface LastInsertBookView {
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
     * 创建时间
     */
    readonly createTime?: string | undefined;
}
