<script lang="ts" setup="">
import logo from "@/assets/image/logo.png"
import emitter from "@/utils/emitter.ts"
import {useUserStore} from "@/stores/userStore.ts"
import {message} from "ant-design-vue";
import {getBookByNameAPI} from "@/api/book.ts";
const router = useRouter();
const userStore = useUserStore()
const route = useRoute()
const navList = [{
	name: "home",
	title: "首页",
	path: "/home"
}, {
	name: "all",
	title: "全部书籍",
	path: "/all"
}, {
	name: "rank",
	title: "排行榜",
	path: "/rank"
}]

const logout = () => {
	userStore.logout()
	message.success("退出成功")
	router.push('/')
}

const searchRef = ref()
const onSearch = async (value: string) => {
	// await getBookByNameAPI(value)
	router.push({
		path: '/search',
		query: {
			name: value
		}
	})
};
</script>

<template>
	<div class="bg-[#f2f2f2]  relative min-w-1020px">
		<header class="w-full bg-white h-22">
			<div class="w-260 h-full relative mx-auto flex items-center justify-between">
				<img class="w-50 h-12" :src="logo" alt="logo"></img>
				<div>
					<a-input-search
							v-model:value="searchRef"
							placeholder="书名 作者 关键字"
							class="w-100"
							size="large"
							@search="onSearch"
					>
					</a-input-search>
				</div>
				<div class="">
					<div v-if="userStore.user" class="mr-4">
						<a-dropdown>
							<div class="p-1 rounded-lg hover:bg-neutral-100">
								<a-avatar size="large" :src="userStore.user?.userPhoto"></a-avatar>
								<span class="ml-4">{{ userStore.user?.nickName }}</span>
							</div>

							<template #overlay>
								<a-menu>
									<a-menu-item class="">
											<router-link class="block text-center" to="/user/setting">个人信息</router-link>
									</a-menu-item>
									<a-menu-item class="">
											<router-link class="block text-center" to="/user/bookcase">我的书架</router-link>
									</a-menu-item>
									<a-menu-item class="">
										<div class="text-center">
											<a @click="logout" href="javascript:;" class="hover:text-red-5!">退出登录</a>
										</div>
									</a-menu-item>
								</a-menu>
							</template>
						</a-dropdown>
					</div>
					<a-button v-else @click="emitter.emit('open-login')" type="text">登录</a-button>
				</div>
			</div>
		</header>
		<nav class="w-full bg-[#93c5fd] mb-5">
			<div class="mx-auto w-260 flex h-13">
				<div class="flex">
					<router-link v-for="item in navList" :key="item.name"
											 class="text-white inline-flex justify-center items-center w-20 mx-4 text-center"
											  :to="{name: item.name}">
						<span :class="{'active': route.name === item.name}" class="text-center py-2">{{ item.title }}</span>
					</router-link>
				</div>
			</div>
		</nav>

		<main class="w-255 min-h-130 mx-auto mb-5">
			<router-view />
		</main>
		<footer class="w-full text-center bg-white py-4 text-12px text-gray-400">
			本站所有小说为转载作品，所有章节均由网友上传，转载至本站只是为了宣传本书让更多读者欣赏。
		</footer>

	</div>

</template>

<style scoped>
/*搜索栏优化*/
:deep(input.ant-input.ant-input-lg.css-dev-only-do-not-override-b92jn9) {
	height: 40px;
}

/* 搜索栏图标水平垂直居中*/
:deep(.ant-btn.ant-btn-default.ant-btn-lg.ant-input-search-button.ant-btn-icon-only) {
	display: inline-flex;
	align-items: center;
	justify-content: center;
}
/* 导航栏 */
nav {
	a {
		span {
			border-block: 1px solid #93c5fd;
			transition: border-bottom 0.2s;
			&.active {
				border-bottom: 1px solid;

			}
		}
		&:hover {
			span {
				border-bottom: 1px solid;
			}
		}
	}
}



</style>
