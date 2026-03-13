export interface BookPageQueryReq {
    readonly bookName: string;
    readonly authorName: string;
    readonly categoryId?: number | undefined;
    readonly bookStatus?: number | undefined;
    readonly pageNum: number;
    readonly pageSize: number;
}
