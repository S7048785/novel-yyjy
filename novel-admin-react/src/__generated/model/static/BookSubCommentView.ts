/**
 * 小说评论
 */
export interface BookSubCommentView {
    /**
     * 主键
     */
    readonly id: string;
    /**
     * 回复的昵称
     */
    readonly replyNickName?: string | undefined;
    /**
     * 评价内容
     */
    readonly content: string;
    /**
     * 创建时间
     */
    readonly createTime: string;
    readonly userId: string;
    readonly nickName: string;
    readonly avatar?: string | undefined;
}
