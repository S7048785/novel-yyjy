/**
 * 小说评论
 */
export interface BookCommentInput {
    /**
     * 评价内容
     */
    readonly content: string;
    /**
     * 回复的昵称
     */
    readonly replyNickName?: string | undefined;
    readonly bookId: number;
    readonly parentId?: number | undefined;
    readonly rootParentId?: number | undefined;
}
