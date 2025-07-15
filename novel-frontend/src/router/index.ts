import {createRouter, createWebHistory} from 'vue-router'
import Home from "@/view/home/index.vue"
import DefaultLayout from "@/layouts/default.vue"
import {message} from "ant-design-vue";
import emitter from "@/utils/emitter.ts";
import {useUserStore} from "@/stores/userStore.ts";
import NotFound from "@/view/not-found.vue";

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: '/',
			redirect: '/home',
			component: DefaultLayout,
			children: [
				{
					path: '/home',
					name: 'home',
					component: Home
				},
				{
					path: '/all',
					name: 'all',
					component: () => import('@/view/all/index.vue')
				},
				{
					path: '/rank',
					name: 'rank',
					component: () => import('@/view/rank/index.vue')
				},
				{
					path: '/info',
					component: () => import("@/view/book/index.vue"),
					children: [
						{
							path: ':id',
							name: 'info',
							component: () => import('@/view/book/[id]/index.vue')
						}
					]
				},
				{
					path: '/user',
					name: 'user',
					meta: { requiresAuth: true },
					component: () => import('@/view/user/index.vue'),
					children: [
						{
							path: 'setting',
							component: () => import('@/view/user/setting/index.vue')
						},
						{
							path: 'bookcase',
							component: () => import('@/view/user/bookcase/index.vue')
						},
						{
							path: 'history',
							component: () => import('@/view/user/history/index.vue')
						}
					]
				}
			]
		},
		{
			path: '/read',
			name: 'read',
			component: () => import('@/layouts/content.vue'),
			children: [
				{
					path: ':bookId/:chapterId',
					component: () => import('@/view/read/index.vue')
				}
			]
		},
			{
				path: '/news',
				name: 'news',
				component: () => import('@/view/news/index.vue')
			},
		{
			path: '/:pathMatch(.*)*', // 捕获所有未匹配的路由
			name: 'NotFound',
			component: NotFound,
		},
	],
})

router.beforeEach((to, from, next) => {

	if (to.meta.requiresAuth) {
		if (!localStorage.getItem('token')) {
			next('/')
			emitter.emit('open-login')
			useUserStore().logout()
			message.warn("请先登录");
			return;
		}
	}
	next()
})

router.afterEach(() => {
	// 进入新页面时 滚动条移动到顶部
	window.scrollTo(0, 0);
});

export default router
