export type BookInfoDto = {
    'BookManagementController/BOOK_VIEW_OF_ADMIN': {
        /**
         * 主键
         */
        readonly id: string;
        /**
         * 作品方向;0-男频 1-女频
         */
        readonly workDirection?: number | undefined;
        /**
         * 类别名
         */
        readonly categoryName?: string | undefined;
        /**
         * 小说封面地址
         */
        readonly picUrl: string;
        /**
         * 小说名
         */
        readonly bookName: string;
        /**
         * 作家名
         */
        readonly authorName: string;
        /**
         * 书籍描述
         */
        readonly bookDesc: string;
        /**
         * 评分;总分:10 ，真实评分 = score/10
         */
        readonly score: number;
        /**
         * 书籍状态;0-连载中 1-已完结
         */
        readonly bookStatus: number;
        /**
         * 点击量
         */
        readonly visitCount: number;
        /**
         * 总字数
         */
        readonly wordCount: number;
        /**
         * 评论数
         */
        readonly commentCount: number;
        /**
         * 创建时间
         */
        readonly createTime?: string | undefined;
        /**
         * 更新时间
         */
        readonly updateTime?: string | undefined;
    }
}
