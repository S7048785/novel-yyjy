import axios from 'axios'
import {message} from "ant-design-vue";
import {useUserStore} from "@/stores/userStore.ts";
import router from "@/router";

export type Data<T> = {
	code: number;
	msg: string;
	data: T;
};

export type PageData<T> = {
	pageNum: number;
	pageSize: number;
	total: number;
	list: T[];
	pages: number;
};

export type Result<T> = Promise<Data<T>>;

const baseURL = import.meta.env.VITE_APP_API_URL;

export const request = axios.create({
	baseURL,
	timeout: 3000,
	headers: {
		'Content-Type': 'application/json'
	}
});

request.defaults.withCredentials = true;

request.interceptors.request.use(config => {
	config.headers['Authorization'] = localStorage.getItem('token') || '';
	return config;
}, error => {

	return Promise.reject(error);
});

request.interceptors.response.use((response) => {

	return response.data;
}, error => {

	if (error.status === 401) {
		message.error('登录已过期，请重新登录');
		useUserStore().logout();
		router.push('/');
	}
	return Promise.reject(error)
});