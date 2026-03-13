export interface BookUpdateReq {
    readonly id: string;
    readonly workDirection: number;
    readonly categoryId?: number | undefined;
    readonly picUrl: string;
    readonly bookName: string;
    readonly authorName: string;
    readonly bookDesc: string;
    readonly bookStatus: number;
}
