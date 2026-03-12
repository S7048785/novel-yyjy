/**
 * 小说信息
 */
export interface AllBookQueryInput {
    readonly bookName?: string | undefined;
    readonly channelId?: number | undefined;
    readonly categoryId?: number | undefined;
    readonly overState?: number | undefined;
    readonly wordCountState?: number | undefined;
    readonly updateDay?: number | undefined;
    readonly sortState?: number | undefined;
}
