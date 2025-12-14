import type {Result} from "@/utils/request.ts";
import type {LatestNews} from "@/type/news.ts";
import {request} from "@/utils/request.ts";

export const getLatestNewsAPI = (): Result<LatestNews[]> => {
	return request({
		url: '/news/latest',
		method: 'get'
	})
}