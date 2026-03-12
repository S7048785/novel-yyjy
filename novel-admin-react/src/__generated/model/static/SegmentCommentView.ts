/**
 * 章节段落评论
 */
export interface SegmentCommentView {
    readonly id: number;
    /**
     * 评论内容
     */
    readonly content: string;
    /**
     * 楼层
     */
    readonly level: number;
    /**
     * 点赞数
     */
    readonly likeCount: number;
    readonly createTime: string;
    /**
     * ip属地
     */
    readonly ipAddress: string;
    readonly userId: string;
    readonly nickName: string;
    readonly avatar?: string | undefined;
}
