import type {UserInfoView, UserLoginInput, UserLoginView, UserRegisterInput} from "@/type/user.ts";
import {request, type Result} from "@/utils/request.ts";


export const registerAPI = (data: UserRegisterInput): Result<UserLoginView> => {
	return request({
		url: '/user/register',
		method: 'post',
		data
	})
}

export const loginAPI = (data: UserLoginInput): Result<UserLoginView> => {
	return request({
		url: '/user/login',
		method: 'post',
		data
	})
}

export const getUserInfoAPI = (): Result<UserInfoView> => {
	return request({
		url: '/user/info',
		method: 'get'
	})
}

export const updateNickName = (nickName: string): Result<void> => {
	return request({
		url: '/user/nickName',
		method: 'put',
		params: {
			nickName
		}
	})
}