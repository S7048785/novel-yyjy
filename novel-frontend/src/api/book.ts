import {type PageData, request, type Result} from "@/utils/request.ts";
import type {
	AllBookView,
	BookCategoryView,
	BookContentView, BookHistoryView,
	BookInfoView,
	BookRandomView,
	BookRankView, BookShelfView, BookStateView
} from "@/type/book.ts";

/**
 * 获取小说分类
 * @param channel 0-男频 1-女频
 */
export const getBookCategoryAPI = (channel: number | null): Result<BookCategoryView[]> => {
	return request({
		url: '/book/category',
		method: 'get',
		params: {id: channel}
	})
}

/**
 * 获取所有小说
 * @param data
 */
export const getAllBookAPI = (data?: any): Result<PageData<AllBookView>> => {
	return request({
		url: '/book/all',
		method: 'get',
		params: data
	})
}

/**
 * 获取小说信息
 * @param id
 */
export const getBookInfoAPI = (id: string): Result<BookInfoView> => {
	return request({
		url: `/book/info/${id}`,
		method: 'get'
	})
}

export const getBookRandomAPI = (categoryId: number | string, bookId: string): Result<BookRandomView[]> => {
	return request({
		url: "/book/random",
		method: 'get',
		params: {
			categoryId,
			bookId
		}
	})
}

export const getBookRankAPI = (rankType: number | string): Result<BookRankView[]> => {
	return request({
		url: "/book/rank",
		method: 'get',
		params: {
			rankType
		}
	})
}

export const getBookContentAPI = (bookId: string, chapterId: string ): Result<BookContentView> => {
	return request({
		url: "/book/content",
		method: 'get',
		params: {
			bookId,
			chapterId
		}
	})
}

export const getBookHistoryAPI = (): Result<BookHistoryView[]> => {
	return request({
		url: "/user/history",
		method: 'get'
	})
}

// 加入/移除书架
export const addOrDelBookShelfAPI = (optType: number, bookId: string): Result<BookInfoView> => {
	return request({
		url: "/user/bookshelf",
		method: 'put',
		params: {
			optType,
			bookId
		}
	})
}

export const getBookShelfAPI = (): Result<BookShelfView[]> => {
	return request({
		url: "/user/bookshelf",
		method: 'get'
	})
}

export const getBookStateAPI = (bookId: string): Result<BookStateView> => {
	return request({
		url: "/book/state",
		method: 'get',
		params: {
			bookId
		}
	})
}