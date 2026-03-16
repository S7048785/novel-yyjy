import type {Dynamic_BookCategory} from '../dynamic/';

export interface BookInfoDoc {
    readonly id: string;
    readonly workDirection?: number | undefined;
    readonly category: Dynamic_BookCategory;
    readonly categoryId?: number | undefined;
    readonly categoryName: string;
    readonly picUrl: string;
    readonly bookName: string;
    readonly authorId: number;
    readonly authorName: string;
    readonly bookDesc: string;
    readonly score: number;
    readonly bookStatus: number;
    readonly visitCount: number;
    readonly wordCount: number;
    readonly lastChapterId: string;
    readonly lastChapterName: string;
    readonly lastChapterUpdateTime: number;
}
