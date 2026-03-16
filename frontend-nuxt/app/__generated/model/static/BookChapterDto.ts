import type {BookChapterView} from './';

export interface BookChapterDto {
    readonly bookChapters: ReadonlyArray<BookChapterView>;
    readonly total?: number | undefined;
}
