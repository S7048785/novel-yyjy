import {request, type Result} from "@/utils/request.ts";
import type {HomeBookRankView, HomeBookView, HomeLatestUpdateBookView, HomeLatestInsertBookView} from "@/type/home.ts";

/**
 * 获取首页推荐列表
 */
export const getHomeBookListAPI = (): Result<HomeBookView[]> => {
	return request({
		url: '/home',
		method: 'get'
	})
}

/**
 * 获取首页书籍访问量排行
 */
export const getHomeBookVisitRankAPI = (): Result<HomeBookRankView[]> => {
	return request({
		url: '/home/visit-rank',
		method: 'get'
	})
}

/**
 * 获取首页新书榜
 */
export const getHomeBookNewestAPI = (): Result<HomeBookRankView[]> => {
	return request({
		url: '/home/newest',
		method: 'get'
	})
}

/**
 * 获取首页最近更新
 */
export const getHomeBookUpdateAPI = (): Result<HomeLatestUpdateBookView[]> => {
	return request({
		url: '/home/recent-update',
		method: 'get'
	})
}

/**
 * 获取首页最新入库
 */
export const getHomeBookNewAPI = (): Result<HomeLatestInsertBookView[]> => {
	return request({
		url: '/home/newest-add',
		method: 'get'
	})
}