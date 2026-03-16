export interface UserPageQueryReq {
    readonly email: string;
    readonly sort: number;
    readonly sortField: string;
    readonly status?: number | undefined;
    readonly startTime: string;
    readonly endTime: string;
    readonly pageNum: number;
    readonly pageSize: number;
}
