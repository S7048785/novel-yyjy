/**
 * <p>
 * 新闻信息
 * 
 * </p>
 */
export interface LaetstNewsView {
    /**
     * 主键
     */
    readonly id: string;
    /**
     * 类别名称
     */
    readonly categoryName: string;
    /**
     * 新闻标题
     */
    readonly title: string;
    /**
     * 更新时间
     */
    readonly updateTime?: string | undefined;
}
