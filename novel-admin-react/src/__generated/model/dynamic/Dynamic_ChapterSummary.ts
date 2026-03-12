import type {Dynamic_BookChapter, Dynamic_BookInfo} from './';

export interface Dynamic_ChapterSummary {
    readonly id?: string;
    /**
     * 评论小说ID
     */
    readonly book?: Dynamic_BookInfo;
    readonly bookId?: number;
    readonly chapter?: Dynamic_BookChapter;
    readonly chapterId?: number;
    /**
     * 段落号
     */
    readonly segmentNum?: number;
    /**
     * 评论量
     */
    readonly commentNum?: number;
}
