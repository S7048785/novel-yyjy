import {type PageData, request, type Result} from "@/utils/request.ts";
import type {BookChapterView, BookCommentInput, BookCommentView, BookSubCommentView} from "@/type/book.ts";

/**
 * 获取小说章节
 * @param params
 */
export const getBookChapterAPI = (params: { id: string, isAll?: number, orderBy?: number }): Result<BookChapterView> => {
	return request({
		url: "/book/chapter",
		method: 'get',
		params
	})
}

export const getBookCommentAPI = (bookId: string, pageNo: number, pageSize: number): Result<PageData<BookCommentView>> => {
	return request({
		url: "/comment/top",
		method: 'get',
		params: {
			bookId,
			pageNo,
			pageSize
		}
	})
}

export const getBookSubCommentAPI = (bookId: string, rootId: string): Result<BookSubCommentView[]> => {
	return request({
		url: "/comment/child",
		method: 'get',
		params: {
			bookId,
			rootId
		}
	})
}

export const addBookCommentAPI = (data: BookCommentInput): Result<BookCommentView> => {
	return request({
		url: "/comment/add",
		method: 'post',
		data
	})
}