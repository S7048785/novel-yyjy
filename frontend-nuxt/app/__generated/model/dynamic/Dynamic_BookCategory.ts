export interface Dynamic_BookCategory {
    readonly id?: string;
    /**
     * 作品方向;0-男频 1-女频
     */
    readonly workDirection?: number;
    /**
     * 类别名
     */
    readonly name?: string;
    /**
     * 排序
     */
    readonly sort?: number;
    /**
     * 创建时间
     */
    readonly createTime?: string;
    /**
     * 更新时间
     */
    readonly updateTime?: string;
    readonly delFlag?: number;
}
