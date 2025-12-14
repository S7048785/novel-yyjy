<script setup lang="ts">
import type {HomeBookRankView, HomeBookView, HomeLatestUpdateBookView, HomeLatestInsertBookView} from "@/type/home.ts";
import {
	getHomeBookListAPI,
	getHomeBookNewAPI,
	getHomeBookNewestAPI,
	getHomeBookUpdateAPI,
	getHomeBookVisitRankAPI
} from "@/api/home.ts";
import Week from "./components/Week.vue"
import Hot from "./components/Hot.vue"
import VisitRank from "./components/VisitRank.vue"
import {createReusableTemplate} from '@vueuse/core'
import {getLatestNewsAPI} from "@/api/news.ts";
import type {LatestNews} from "@/type/news.ts";
import dayjs from "dayjs";

const [DefineTemplate, ReuseTemplate] = createReusableTemplate()

type HomeBookDataType = {
	sliderContent: HomeBookView[],
	topBooks: HomeBookView[],
	weekCommend: HomeBookView[],
	hotRecommend: HomeBookView[],
	goodRecommend: HomeBookView[]
	latestNews: LatestNews[],
	visitRank: HomeBookRankView[],
	newestRank: HomeBookRankView[],
	latestUpdate: HomeLatestUpdateBookView[],
	latestInsert: HomeLatestInsertBookView[]
}
const homeBookData = reactive<HomeBookDataType>({
	// 轮播图
	sliderContent: [],
	// 顶部栏
	topBooks: [],
	//本周强推
	weekCommend: [],
	// 热门推荐
	hotRecommend: [],
	// 精品推荐
	goodRecommend: [],
	// 最新资讯
	latestNews: [],
	// 点击榜单
	visitRank: [],
	// 新书榜单
	newestRank: [],
	// 最近更新
	latestUpdate: [],
	// 最新入库
	latestInsert: []
})

// 获取小说推荐
const getCommendData = async () => {
	// 获取推荐数据
	const res = await getHomeBookListAPI();

	res.data.forEach(item => {
		switch (item.type) {
			case '0':
				homeBookData.sliderContent.push(item);
				break;
			case '1':
				homeBookData.topBooks.push(item);
				break;
			case '2':
				homeBookData.weekCommend.push(item);
				break;
			case '3':
				homeBookData.hotRecommend.push(item);
				break;
			case '4':
				homeBookData.goodRecommend.push(item);
		}
	})
}
// 获取资讯数据
const getLatestNewsData = async () => {
	// 获取资讯数据
	const news = await getLatestNewsAPI();
	homeBookData.latestNews = news.data
}
// 获取点击榜单
const getVisitRankData = async () => {
	// 获取推荐数据
	const res = await getHomeBookVisitRankAPI();
	homeBookData.visitRank = res.data
}
// 获取新书榜单
const getNewestRankData = async () => {
	// 获取推荐数据
	const res = await getHomeBookNewestAPI();
	homeBookData.newestRank = res.data

}
// 获取最近更新
const getLatestUpdateData = async () => {
	// 获取最近更新数据
	const res = await getHomeBookUpdateAPI();
	homeBookData.latestUpdate = res.data
	// 时间格式化
	homeBookData.latestUpdate.forEach(item => {
		item.lastChapterUpdateTime = dayjs(item.lastChapterUpdateTime).format('MM-DD')
	})
}
// 获取最新入库
const getLatestInsertData = async () => {
	// 获取最新入库数据
	const res = await getHomeBookNewAPI();
	homeBookData.latestInsert = res.data
}

// 轮播图悬浮索引
const sliderContentIndex = ref(0)
let sliderContentIsHover: boolean = false
// 鼠标移入轮播图时
const hoverSliderContentIn = (index: number) => {
	sliderContentIndex.value = index
	sliderContentIsHover = true
}
// 鼠标移出轮播图
const hoverSliderContentOut = () => {
	sliderContentIsHover = false
}

// 本周强推悬浮索引
const weekCommendHoverIndex = ref(0)
// 点击榜悬浮索引
const visitRankHoverIndex = ref(0)
// 新书榜悬浮索引
const newestRankHoverIndex = ref(0)

const loading = ref(true)
onMounted(async () => {

	await Promise.all([
		getCommendData(),
		getLatestNewsData(),
		getVisitRankData(),
		getNewestRankData(),
		getLatestUpdateData(),
		getLatestInsertData()
	])
	loading.value = false
	// 轮播图定时器
	setInterval(() => {
		if (!sliderContentIsHover) {
			sliderContentIndex.value = (sliderContentIndex.value + 1) % homeBookData.sliderContent.length
		}
	}, 3000)
})
</script>

<template>

	<div v-if="loading" class="h-228 bg-white  flex items-center justify-center p-5 mx-auto">
		<a-spin tip="加载中..." size="large"/>
	</div>

	<div v-else>
		<div>
			<div class="grid grid-cols-3 my-4">
				<!--				轮播图-->
				<div class="col-span-2">
					<Carousel :data="homeBookData.sliderContent"
										:pictures="['/image/carousel1.png', '/image/carousel2.png','/image/carousel3.png']"></Carousel>
				</div>
				<!--				本周推荐-->
				<Week :data="homeBookData.weekCommend"></Week>
			</div>

			<div class="flex gap-4">
<!--				热门推荐-->
				<Hot :data="homeBookData.hotRecommend" class="flex-1"></Hot>
<!--				点击榜-->
				<VisitRank :data="homeBookData.visitRank" class=""></VisitRank>
			</div>
		</div>


		<!--	精品推荐 新书榜单-->
		<div class="p-5 mb-5 rounded-lg bg-white mx-auto">
			<div class="w-178 float-left">
				<p class="border-b pb-2 text-xl font-bold">精品推荐</p>
				<div v-for="(item) in homeBookData.goodRecommend" class="float-left my-4 w-89 h-33">
					<RouterLink class="mr-4 w-25 float-left overflow-hidden " :to="`/info/${item.bookId}`">
						<img class="object-cover transition duration-300 hover:scale-110" :src="item.picUrl"
								 onerror="this.src='/image/default.png'" alt="">
					</RouterLink>
					<div class="h-29 float-left w-220px line-height-[180%] overflow-hidden ">
						<RouterLink :to="`/info/${item.bookId}`" class="mb-2 hover:text-blue">{{ item.bookName }}</RouterLink>
						<div class="text-gray-400">
							<p class="text-xs mb-2">{{ item.authorName }}</p>
							<p v-html="item.bookDesc" class="text-xs"></p>
						</div>
					</div>
					<div class="clear-both"></div>
				</div>
			</div>
			<div class="w-60 float-right">
				<p class="text-xl font-bold pb-2 border-b">点击榜单</p>
				<ul>
					<li v-for="(item, index) in homeBookData.newestRank.slice(0, 10)"
							@mouseenter="() => {newestRankHoverIndex = index}" :key="item.id" class="my-3">
						<!--					标题-->
						<RouterLink :to="`/info/${item.id}`"
												class="text-hidden text-sm clear-both inline-flex items-center w-full hover:text-blue">
							<div class="size-4 text-white not-italic text-center bg-blue-400 mr-2"><span
									class="relative bottom-0.5">{{ index + 1 }}</span></div>
							<span class="">{{ item.bookName }}</span>
						</RouterLink>
						<!--					图片及描述-->
						<RouterLink :to="`/info/${item.id}`" v-show="index === newestRankHoverIndex"
												class="w-full block cursor-default bg-[#f2f2f2] border-1 h-25 p-2 mt-1">
							<div class="block float-left mr-4">
								<img class="h-20 cursor-pointer" :src="item.picUrl" onerror="this.src='/image/default.png'" alt=""/>
							</div>
							<a v-html="homeBookData.newestRank[newestRankHoverIndex]?.bookDesc"
								 class="h-full cursor-pointer hover:text-blue block text-xs line-height-[180%] text-ellipsis overflow-hidden text-gray-500"/>
						</RouterLink>
					</li>
				</ul>
				<a-button class="w-full bg-[#f2f2f2]">
					<!--				TODO: 查看更多-->
					<span style="font-size: 12px">查看更多</span>
				</a-button>
			</div>
			<div class="clear-both"></div>
		</div>

		<!--	最近更新 最新入库-->
		<div class="p-5 mb-5 rounded-lg bg-white mx-auto">
			<div class="w-170 float-left">
				<p class="border-b pb-2 text-xl font-bold">最近更新</p>
				<a-table
						class="mt-3"
						:pagination="false"
						:data-source="homeBookData.latestUpdate"
						size="small">
					<a-table-column key="categoryName" title="分类" :width="80" data-index="categoryName">
						<template #default="{ text: categoryName, record: {categoryId}}">
							<!--									TODO: 路由跳转：分类参数-->
							<RouterLink class="text-gray-400" :to="`/category?c=${categoryId}`">
								{{ categoryName }}
							</RouterLink>
						</template>
					</a-table-column>
					<a-table-column key="bookName" title="书名" :width="180" data-index="bookName">
						<template #default="{ text: bookName, record: { id }}">
							<RouterLink :to="`/info/${id}`">
								{{ bookName }}
							</RouterLink>
						</template>
					</a-table-column>
					<!--									TODO: 路由跳转：章节参数-->
					<a-table-column key="lastChapterName" title="最新章节" ellipsis="true" data-index="lastChapterName">
						<template #default="{ text }">
							<RouterLink class="text-12px" :to="`/info/1`">
								{{ text }}
							</RouterLink>
						</template>
					</a-table-column>
					<a-table-column key="authorName" title="作者" :width="80" ellipsis="true" data-index="authorName">
						<template #default="{ text: authorName }">
									<span class="text-gray-400 text-12px">
											{{ authorName }}
									</span>
						</template>
					</a-table-column>
					<a-table-column key="lastChapterUpdateTime" align="center" title="更新时间" :width="90"
													data-index="lastChapterUpdateTime">
						<template #default="{ text: lastChapterUpdateTime }">
									<span class="text-gray-400 text-12px">
										{{ lastChapterUpdateTime }}
									</span>
						</template>
					</a-table-column>
				</a-table>
			</div>
			<div class="w-70 float-right">
				<p class="text-xl font-bold pb-2 border-b">最新入库</p>
				<ul class="mt-3">
					<li v-for="(item) in homeBookData.latestInsert.slice(0, 20)" class="text-12px border-b py-2">
						<span class="mr-6">[{{ item.categoryName }}]</span>
						<RouterLink :to="`/info/${item.id}`" class="text-hidden clear-both items-center w-full text-[#5865A6FF]">
							<span>{{ item.bookName }}</span>
						</RouterLink>
						<span class="float-right">{{ item.authorName }}</span>
					</li>
				</ul>
			</div>
			<div class="clear-both"></div>
		</div>
	</div>

</template>

<style scoped lang="less">
/* 居中 */
.container {
	position: absolute;
	left: 50%;
	transform: translateX(-50%);
}

/* 文本超出隐藏 */
.text-hidden {
	white-space: nowrap;
	text-overflow: ellipsis;
	overflow: hidden;
}

dd a, dt {
	.text-hidden()
}

/* 覆盖antd表头样式 */
:deep(.ant-table-thead) {
	.ant-table-cell {
		background: white;
		color: gray;
		font-weight: 400;
	}
}

/* 隐藏antd表头分割线 */
:deep(:where(.css-dev-only-do-not-override-b92jn9).ant-table-wrapper .ant-table-thead >tr>th:not(:last-child):not(.ant-table-selection-column):not(.ant-table-row-expand-icon-cell):not([colspan])::before, :where(.css-dev-only-do-not-override-b92jn9).ant-table-wrapper .ant-table-thead >tr>td:not(:last-child):not(.ant-table-selection-column):not(.ant-table-row-expand-icon-cell):not([colspan])::before) {
	display: none;
}

</style>
