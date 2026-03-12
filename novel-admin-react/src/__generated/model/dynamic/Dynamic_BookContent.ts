import type {Dynamic_BookChapter} from './';

export interface Dynamic_BookContent {
    readonly createTime?: string | undefined;
    readonly updateTime?: string | undefined;
    readonly id?: string;
    /**
     * 章节ID
     */
    readonly chapter?: Dynamic_BookChapter;
    /**
     * 小说章节内容
     */
    readonly content?: string;
    readonly delFlag?: number;
}
