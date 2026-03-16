export interface PageResult<T> {
    readonly pages: number;
    readonly pageNum: number;
    readonly pageSize: number;
    readonly total: number;
    readonly list: ReadonlyArray<T>;
}
