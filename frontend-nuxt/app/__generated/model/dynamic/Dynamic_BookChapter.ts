import type {Dynamic_BookContent, Dynamic_BookInfo} from './';

export interface Dynamic_BookChapter {
    readonly createTime?: string | undefined;
    readonly updateTime?: string | undefined;
    readonly id?: string;
    /**
     * 小说ID
     */
    readonly book?: Dynamic_BookInfo;
    readonly content?: Dynamic_BookContent | undefined;
    /**
     * 章节号
     */
    readonly chapterNum?: number;
    /**
     * 章节名
     */
    readonly chapterName?: string;
    /**
     * 章节字数
     */
    readonly wordCount?: number;
    /**
     * 是否收费;1-收费 0-免费
     */
    readonly vipState?: number;
    readonly delFlag?: number;
}
