/**
 * 首页小说推荐
 */
export type HomeBookView = {
	type: '0' | '1' | '2' | '3' | '4';
	bookId: string;
	picUrl: string;
	bookName: string;
	authorName: string;
	bookDesc: string;
}

/**
 * 首页小说排行
 */
export type HomeBookRankView = Pick<HomeBookView,'picUrl' | 'bookName' | 'bookDesc'> & {id: string}

/**
 * 首页最新更新小说
 */
export type HomeLatestUpdateBookView = {
	id: string;
	categoryName: string;
	bookName: string;
	lastChapterName: string;
	authorName: string;
	lastChapterUpdateTime: Date | string;
}

export type HomeLatestInsertBookView = {
	id: string;
	categoryName: string;
	bookName: string;
	authorName: string;
	createTime: Date;
}