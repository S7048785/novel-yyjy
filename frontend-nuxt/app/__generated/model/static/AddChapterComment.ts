/**
 * 章节段落评论
 */
export interface AddChapterComment {
    /**
     * 评论内容
     */
    readonly content: string;
    /**
     * 段落号
     */
    readonly segmentNum: number;
    readonly bookId: number;
    readonly chapterId: number;
}
