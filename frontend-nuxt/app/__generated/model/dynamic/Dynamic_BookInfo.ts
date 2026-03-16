import type {Dynamic_BookCategory} from './';

export interface Dynamic_BookInfo {
    /**
     * 主键
     */
    readonly id?: string;
    /**
     * 作品方向;0-男频 1-女频
     */
    readonly workDirection?: number | undefined;
    /**
     * 类别ID
     */
    readonly category?: Dynamic_BookCategory | undefined;
    readonly categoryId?: number | undefined;
    /**
     * 类别名
     */
    readonly categoryName?: string | undefined;
    /**
     * 小说封面地址
     */
    readonly picUrl?: string;
    /**
     * 小说名
     */
    readonly bookName?: string;
    /**
     * 作家id
     */
    readonly authorId?: number;
    /**
     * 作家名
     */
    readonly authorName?: string;
    /**
     * 书籍描述
     */
    readonly bookDesc?: string;
    /**
     * 评分;总分:10 ，真实评分 = score/10
     */
    readonly score?: number;
    /**
     * 书籍状态;0-连载中 1-已完结
     */
    readonly bookStatus?: number;
    /**
     * 点击量
     */
    readonly visitCount?: number;
    /**
     * 总字数
     */
    readonly wordCount?: number;
    /**
     * 评论数
     */
    readonly commentCount?: number;
    /**
     * 最新章节ID
     */
    readonly lastChapterId?: number | undefined;
    /**
     * 最新章节名
     */
    readonly lastChapterName?: string | undefined;
    /**
     * 最新章节更新时间
     */
    readonly lastChapterUpdateTime?: string | undefined;
    /**
     * 是否收费;1-收费 0-免费
     */
    readonly vipState?: number;
    /**
     * 创建时间
     */
    readonly createTime?: string | undefined;
    /**
     * 更新时间
     */
    readonly updateTime?: string | undefined;
    readonly delFlag?: number;
}
