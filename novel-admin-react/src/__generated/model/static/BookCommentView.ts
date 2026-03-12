/**
 * 小说评论
 */
export interface BookCommentView {
    /**
     * 主键
     */
    readonly id: string;
    /**
     * 评价内容
     */
    readonly content: string;
    readonly childrenCount: number;
    /**
     * 创建时间
     */
    readonly createTime: string;
    readonly userId: string;
    readonly nickName?: string | undefined;
    readonly avatar?: string | undefined;
}
