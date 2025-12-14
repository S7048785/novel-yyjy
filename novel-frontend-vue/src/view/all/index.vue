<script setup lang="ts">
import type {AllBookView, BookCategoryView} from "@/type/book.ts";
import {getAllBookAPI, getBookCategoryAPI} from "@/api/book.ts";
import type {PageData} from "@/utils/request.ts";

const route = useRoute()

type StateType = {
	channel: { id: number, text: string }[],
	category: BookCategoryView[],
	over: { id: number, text: string }[]
	wordCount: { id: number, text: string }[],
	updateDay: { id: number, text: string }[],
	sort: { id: number, text: string }[]
}
const stateList = reactive<StateType>({
	channel: [{id: 0, text: '全部'}, {id: 1, text: '男频'}, {id: 2, text: '女频'}],
	category: [{id: 0, name: '全部'}],
	over: [{id: 0, text: '全部'}, {id: 1, text: '连载中'}, {id: 2, text: '已完结'}],
	wordCount: [{id: 0, text: '全部'}, {id: 1, text: '30万字以下'}, {id: 2, text: '30万-50万字'}, {id: 3, text: '50万-100万字'}, {id: 4, text: '100万字以上'}],
	updateDay: [{id: 0, text: '全部'}, {id: 7, text: '七日内'}, {id: 30, text: '一月内'}, {id: 180, text: '半年内'}, {id: 365, text: '一年内'}],
	sort: [{id: 0, text: '全部'}, {id: 10, text: '更新时间'}, {id: 2, text: '总字数'}, {id: 3, text: '点击量'}]
})
// 选择状态
const selectedState = reactive({
	channel: 0,
	category: 0,
	over: 0,
	wordCount: 0,
	updateDay: 0,
	sort: 0
})
// 状态变化
const stateChange = {
	channelChange: (index: number) => {
		selectedState.channel = index;
		selectedState.category = 0;
		getCategoryData();
		pageChange(1);
	},
	categoryChange: (index: number) => {
		selectedState.category = index;
		pageChange(1)
	},
	overChange: (index: number) => {
		selectedState.over = index;
		pageChange(1)
	},
	wordCountChange: (index: number) => {
		selectedState.wordCount = index;
		pageChange(1)
	},
	updateDayChange: (index: number) => {
		selectedState.updateDay = index;
		pageChange(1)
	},
	sortChange: (index: number) => {
		selectedState.sort = index;
		pageChange(1)
	}
}
// 小说分页数据
const bookData = ref<PageData<AllBookView>>({
	pageNum: 1,
	pageSize: 10,
	total: 0,
	list: [],
	pages: 1
})

// 获取分类数据
const getCategoryData = async () => {
	// 获取分类数据
	const res = await getBookCategoryAPI(selectedState.channel === 0 ? null : selectedState.channel - 1);
	stateList.category.splice(1, stateList.category.length, ...res.data)
}
// 获取全部数据
const getAllData = async (data?: any) => {
	// 获取推荐数据
	const res = await getAllBookAPI(data);
	bookData.value = res.data
	bookData.value.list.forEach((item, index) => {
		item.index = index + 1;
	})
}

const pageNo = ref(1)
const scrollTopRef = ref<HTMLElement>()
// 点击页码时获取数据
const pageChange = async (page: number) => {

	bookData.value.pageNum = page;
	// 手动修改表格页码
	pageNo.value = page;

	if (page !== 1) {
		// 跳转到顶部
		scrollTopRef.value?.scrollIntoView({
			behavior: 'smooth', // 平滑滚动，可选 'auto' 直接跳转
			block: 'start',    // 对齐方式：'start'、'center'、'end'
		});
	}

	loading.value = true
	await getAllData({
		pageNum: page,
		channelId: selectedState.channel === 0 ? null : selectedState.channel - 1,
		categoryId: selectedState.category === 0 ? null : selectedState.category,
		overState: selectedState.over === 0 ? null : selectedState.over - 1,
		wordCountState: selectedState.wordCount === 0 ? null : selectedState.wordCount - 1,
		updateDay: selectedState.updateDay === 0 ? null : stateList.updateDay[selectedState.updateDay].id,
		sortState: selectedState.sort === 0 ? null : selectedState.sort - 1
	})
	loading.value = false
}

const jumpToCategory = () => {
	const queryElement = route.query['c'];
	for (let i of stateList.category) {
		if (queryElement === i.id.toString()) {
			stateChange.categoryChange(Number(queryElement));
			return;
		}
	}
}

// 加载状态
const loading = ref(true)
onMounted(async () => {
	await Promise.all([
		getCategoryData(),
		getAllData()
	])
	loading.value = false

	jumpToCategory()
})
</script>

<template>
		<!--	头部选择列表-->
	<div class="bg-white p-5 mb-5 mx-auto">
			<ul class="px-5">
				<li class="mb-6">
					<span class="mr-10 text-gray">频道:</span>
					<span
							v-for="(item, index) in stateList.channel"
							:class="{'text-blue': selectedState.channel === index}"
							@click="stateChange.channelChange(index)"
							class="mr-8 cursor-pointer">
					{{ item.text }}
				</span>
				</li>
				<li class="mb-6">
					<span class="mr-10 text-gray">分类:</span>
					<span
							v-for="(item, index) in stateList.category"
							:class="{'text-blue': selectedState.category === index}"
							@click="stateChange.categoryChange(index)"
							class="mr-8 cursor-pointer">
					{{ item.name }}
				</span>
				</li>
				<li class="mb-6">
					<span class="mr-10 text-gray">状态:</span>
					<span
							v-for="(item, index) in stateList.over"
							:class="{'text-blue': selectedState.over === index}"
							@click="stateChange.overChange(index)"
							class="mr-8 cursor-pointer">
					{{ item.text }}
				</span>
				</li>
				<li class="mb-6">
					<span class="mr-10 text-gray">字数:</span>
					<span
							v-for="(item, index) in stateList.wordCount"
							:class="{'text-blue': selectedState.wordCount === index}"
							@click="stateChange.wordCountChange(index)"
							class="mr-8 cursor-pointer">
					{{ item.text }}
				</span>
				</li>
				<li class="mb-6">
					<span class="mr-10 text-gray">更新时间:</span>
					<span
							v-for="(item, index) in stateList.updateDay"
							:class="{'text-blue': selectedState.updateDay === index}"
							@click="stateChange.updateDayChange(index)"
							class="mr-8 cursor-pointer">
					{{ item.text }}
				</span>
				</li>
				<li class="">
					<span class="mr-10 text-gray">排序方式:</span>
					<span
							v-for="(item, index) in stateList.sort"
							:class="{'text-blue': selectedState.sort === index}"
							@click="stateChange.sortChange(index)"
							class="mr-8 cursor-pointer">
					{{ item.text }}
				</span>
				</li>
			</ul>
		</div>

	<div v-if="loading" class="h-228 bg-white flex items-center justify-center p-5 mx-auto">
		<a-spin tip="加载中..." size="large"/>
	</div>
		<!--	小说列表-->
		<div v-else class="bg-white p-5 pt-2 mx-auto">
			<a ref="scrollTopRef"></a>
			<a-table
					class="mt-3"
					:pagination="{
					pagination: 'center',
					defaultPageSize: 20,
					current: pageNo,
					onChange: pageChange,
					total: bookData.total,showSizeChanger: false,
					hideOnSinglePage: true,
				}"
					:data-source="bookData.list"
					size="small">
				<a-table-column key="index" title="序号" align="center" :width="50" data-index="index">
					<template #default="{ text: index }">
						<div class="text-white inline-block bg-gray w-5 text-12px">
							{{ index }}
						</div>
					</template>
				</a-table-column>
				<a-table-column key="categoryName" title="分类" :width="100" data-index="categoryName">
					<template #default="{ text: categoryName}">
						<!--									TODO: 路由跳转：分类参数-->
						<RouterLink class="text-gray-400" :to="`/info/1`">
							{{ categoryName }}
						</RouterLink>
					</template>
				</a-table-column>
				<a-table-column key="bookName" title="书名" ellipsis="true" :width="250" data-index="bookName">
					<template #default="{ text: bookName, record: { id }}">
						<RouterLink :to="`/info/${id}`">
							{{ bookName }}
						</RouterLink>
					</template>
				</a-table-column>
				<!--									TODO: 路由跳转：章节参数-->
				<a-table-column key="lastChapterName" title="最新章节" ellipsis="true" data-index="lastChapterName">
					<template #default="{ text }">
						<RouterLink class="text-12px hidden-over-color" :to="`/info/1`">
						<span class="">
						{{ text }}
						</span>
						</RouterLink>
					</template>
				</a-table-column>
				<a-table-column key="authorName" title="作者" :width="120" ellipsis="true" data-index="authorName">
					<template #default="{ text: authorName }">
									<span class="text-gray-400 text-12px">
											{{ authorName }}
									</span>
					</template>
				</a-table-column>
				<a-table-column key="wordCount" align="center" title="字数" :width="90"
												data-index="wordCount">
					<template #default="{ text: wordCount }">
									<span class="text-gray-400 text-12px">
										{{ (wordCount / 10000).toFixed(2) }}万
									</span>
					</template>
				</a-table-column>
			</a-table>
		</div>

</template>

<style scoped>

/* 覆盖antd表头样式 */
:deep(.ant-table-thead) {
	.ant-table-cell {
		background: #f8f8f8;
		color: gray;
		font-weight: 400;
	}
}

/* 覆盖antda表格内标签悬浮后的颜色 */
a.hidden-over-color {
	&:hover {
		color: inherit;
	}
}

/* 隐藏antd表头分割线 */
:deep(:where(.css-dev-only-do-not-override-b92jn9).ant-table-wrapper .ant-table-thead >tr>th:not(:last-child):not(.ant-table-selection-column):not(.ant-table-row-expand-icon-cell):not([colspan])::before, :where(.css-dev-only-do-not-override-b92jn9).ant-table-wrapper .ant-table-thead >tr>td:not(:last-child):not(.ant-table-selection-column):not(.ant-table-row-expand-icon-cell):not([colspan])::before) {
	display: none;
}

/* 覆盖antd分页样式 */
:deep(ul.ant-pagination.ant-pagination-mini.ant-table-pagination) {
	justify-content: center;

	li {
		margin-right: 15px;
		border-radius: 0;
		min-width: 30px;
		height: 30px;
		line-height: 28px;

		&.ant-pagination-jump-next.ant-pagination-jump-next-custom-icon {
			a div span {
				&.anticon-double-right.ant-pagination-item-link-icon {
					top: 10px;
					right: 10px;
					position: absolute;
				}
			}
		}
	}

	button.ant-pagination-item-link {
		display: flex;
		align-items: center;
		justify-content: center;
	}
}

</style>
