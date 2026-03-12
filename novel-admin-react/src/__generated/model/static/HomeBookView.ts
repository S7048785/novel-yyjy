/**
 * 小说推荐
 */
export interface HomeBookView {
    /**
     * 推荐类型;0-轮播图 1-顶部栏 2-本周强推 3-热门推荐 4-精品推荐
     */
    readonly type: string;
    /**
     * 推荐简介
     */
    readonly intro?: string | undefined;
    /**
     * 推荐标签
     */
    readonly tag?: string | undefined;
    readonly bookId: string;
    readonly picUrl: string;
    readonly bookName: string;
    readonly authorName: string;
    readonly bookDesc: string;
}
