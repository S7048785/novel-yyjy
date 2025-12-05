<script setup lang="ts">
import {getBookCategoryAPI} from "@/api/book.ts";
import type {BookCategoryView} from "@/type/book.ts";
import emitter from "@/utils/emitter.js";
import {useUserStore} from "@/stores/userStore.ts";

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const navList = [{
	name: "home",
	title: "首页",
	path: "/home"
}, {
	name: "rank",
	title: "排行榜",
	path: "/rank"
}, {
	name: "all",
	title: "全部小说",
	path: "/all"
}]

const inputRef = ref()

const onSearch = () => {
	if (inputRef.value) {
		router.push({name: "search", query: {name: inputRef.value}})
	}
}

const onLogin = () => {
	emitter.emit('open-login')
}

const categoryList = ref<BookCategoryView[]>([])

const getCategory = async () => {
	const res = await getBookCategoryAPI(0)
	categoryList.value = res.data
}

onMounted(() => {
	getCategory()
})
</script>

<template>
	<div class="flex justify-between border-b shadow bg-white px-20">
		<!--		left-->
		<div class="flex items-center h-20 w-20 w-full flex-1">
			<router-link to="/home" class="inline-flex items-center mr-4">
				<img src="/image/novel_logo.jpg" alt="" class="w-15">
				<p class="text-xl font-bold text-[#0078D7]">小说精品屋</p>
				</router-link>
			<div class="flex gap-4">
				<div v-for="(item, index) in navList">
					<router-link :to="{name: item.name}" class="hover:text-blue">
						<a :class="{'text-blue font-bold': route.name === item.name}">{{ item.title }}</a>
					</router-link>
				</div>
				<a-dropdown class="lg:hidden block">
					<a class="ant-dropdown-link inline-flex items-center" @click.prevent>
						分类
						<DownOutlined class="ml-1"/>
					</a>
					<template #overlay>
						<a-menu>
							<a-menu-item v-for="item in categoryList">
								<router-link
										:to="`/all?c=${item.id}`"
										class="p-2 block"
								>
									{{item.name}}</router-link>
							</a-menu-item>
						</a-menu>
					</template>
				</a-dropdown>
			</div>
			<div class="flex-1 items-center gap-4 mx-4 overflow-x-scroll whitespace-nowrap scroll-container hidden lg:flex">
				|
				<router-link
						v-for="item in categoryList"
						:to="`/all?c=${item.id}`"
						class="hover:text-blue text-sm"
				>
					{{item.name}}
				</router-link>
			</div>

		</div>
		<!--		right-->
		<div class="flex items-center gap-4">
			<div class="inline-flex w-70 relative">
				<input
						v-model="inputRef"
						@keydown.enter="onSearch"
						class="border px-4 py-2 rounded-full w-full focus-visible:outline-0 pr-15"
						type="text"
						placeholder="搜索书名/作者/关键字">
				<button @click="onSearch" class="bg-[#FF6600] px-4 py-2 rounded-r-full absolute right-0">
					<SearchOutlined style="background-color: #FF6600; color: white; font-size: 18px;"/>
				</button>
			</div>

			<div>
				<a-button v-if="!userStore.user" type="text" @click="onLogin">登录</a-button>
				<a-dropdown v-else class="">
					<a-button type="text" class="flex items-center gap-2 " @click.prevent>
<!--						<a-avatar :src="userStore.user.userPhoto" size="large"/>-->
						<img :src="userStore.user.userPhoto" class="rounded-full " alt="" size="32" height="32" width="32">
						{{userStore.user.nickName}}
					</a-button>
					<template #overlay>
						<a-menu>
							<a-menu-item>
								<a href="javascript:;" @click="router.push('/user/setting')">个人信息</a>
							</a-menu-item>
							<a-menu-item>
								<a href="javascript:;" @click="router.push('/user/bookcase')">我的书架</a>
							</a-menu-item>
							<a-menu-item>
								<a href="javascript:;" @click="userStore.logout">登出</a>
							</a-menu-item>
						</a-menu>
					</template>
				</a-dropdown>
			</div>
		</div>
	</div>
</template>

<style scoped>


/* 隐藏默认滚动条 - Chrome, Safari, Opera */
.scroll-container::-webkit-scrollbar {
	height: 0px;
}

/* 滚动条轨道 */
.scroll-container::-webkit-scrollbar-track {
	background: #f1f1f1;
	border-radius: 10px;

}

/* 滚动条滑块 */
.scroll-container::-webkit-scrollbar-thumb {
	background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
	border-radius: 10px;
	border: 3px solid #f1f1f1;

}

/* 滚动条滑块悬停效果 */
.scroll-container::-webkit-scrollbar-thumb:hover {
	background: linear-gradient(90deg, #3a7bd5 0%, #00d2ff 100%);
}

</style>