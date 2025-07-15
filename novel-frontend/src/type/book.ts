export type BookCategoryView = {
	id: number;
	name: string;
}

export type AllBookView = {
	index?: number;
	id: number;
	categoryName: string;
	bookName: string;
	lastChapterName: string;
	authorName: string;
	wordCount: number;
}

export type BookInfoView = {
	id: string;
	bookName: string;
	authorName: string;
	bookDesc: string;
	bookStatus: number;
	picUrl: string;
	categoryId: number;
	categoryName: string;
	lastChapterUpdateTime: string;
	lastChapterId: number;
	lastChapterName: string;
	visitCount: number;
	wordCount: number;
	commentCount: number;
}

export type BookChapterList = {
	id: string;
	chapterName: string;
	updateTime: string;
	wordCount: number;
}

export type BookChapterView = {
	bookChapters: BookChapterList[];
	total: number;
}

export type BookCommentView = {
	id: string;
	content: string;
	children: BookSubCommentView[] | null;
	childrenCount: number;
	userId: string;
	nickName: string;
	avatar: string | null;
	createTime: string;
}

export type BookSubCommentView = {
	id: string;
	content: string;
	userId: string;
	replyNickName: string | null;
	nickName: string;
	avatar: string | null;
	createTime: string;
}

export type BookRandomView = {
	id: string;
	bookName: string;
	bookDesc: string;
	picUrl: string;
}

export type BookRankView = {
	index?: number;
	id: string;
	categoryName: string;
	bookName: string;
	lastChapterName: string;
	authorName: string;
	wordCount: number;
}

export type BookContentView = {
	id: string;
	chapterNum: number;
	chapterName: string;
	wordCount: number;
	createTime: string;
	categoryName: string;
	categoryId: number;
	bookName: string;
	bookId: string;
	authorName: string;
	content: string;
	prevChapterId: string | null;
	nextChapterId: string | null;
	isInBookShelf: number;
}

export type BookCommentInput = {
	bookId: string;
	content: string;
	replyNickName?: string;
	rootParentId?: string;
	parentId?: string;
}

export type BookHistoryView = {
	id: string;
	bookId: string;
	bookName: string;
	chapterId: string;
	chapterName: string;
	updateTime: string;
	categoryName: string;
	authorName: string;
}

export type BookShelfView = {
	id: string;
	chapterId: string;
	chapterName: string;
	bookId: string;
	bookName: string;
	categoryName: string;
	authorName: string;
}

/**
 * 获取书架状态
 */
export type BookStateView = {
	/**
	 * 是否在书架中 0-不在 1-在
	 */
	isInBookShelf: number;
}