/**
 * 用户书架
 */
export interface UserBookShelfView {
    /**
     * 主键
     */
    readonly id: string;
    readonly chapterId?: string | undefined;
    readonly chapterName?: string | undefined;
    readonly bookId: string;
    readonly bookName: string;
    readonly categoryName?: string | undefined;
    readonly authorName: string;
}
