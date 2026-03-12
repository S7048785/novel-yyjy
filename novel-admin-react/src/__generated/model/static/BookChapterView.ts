/**
 * 小说章节
 */
export interface BookChapterView {
    readonly id: string;
    /**
     * 章节名
     */
    readonly chapterName: string;
    readonly createTime?: string | undefined;
    readonly updateTime?: string | undefined;
    /**
     * 章节字数
     */
    readonly wordCount: number;
}
