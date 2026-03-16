export interface SegmentCommentPageQueryReq {
    readonly chapterId?: number | undefined;
    readonly segmentNum?: number | undefined;
    readonly pageNum: number;
    readonly pageSize: number;
}
