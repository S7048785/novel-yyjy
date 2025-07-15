<script setup lang="ts">

import {getBookRankAPI} from "@/api/book.ts";
import type {BookRankView} from "@/type/book.ts";


const rankList = [
	{
		id: 1,
		name: '点击榜'
	},
	{
		id: 2,
		name: '新书榜'
	},
	{
		id: 3,
		name: '更新榜'
	},
	{
		id: 4,
		name: '评论榜'
	}
]
const rankData = ref<BookRankView[]>();
const selectedIndex = ref(0);

const getRankBookData = async () => {
	const res = await getBookRankAPI(rankList[selectedIndex.value].id);
	rankData.value = res.data;
	rankData.value.forEach((item, index) => {
		item.index = index + 1;
	})
}

const changeRank = (index: number) => {
	selectedIndex.value = index;
	getRankBookData();
}

const loading = ref(true)
onMounted(() => {
	getRankBookData();
	loading.value = false
})
</script>

<template>
	<div v-if="loading" class="h-130 bg-white flex items-center justify-center rounded-lg p-5">
		<a-spin tip="加载中..." size="large"/>
	</div>
	<div v-else class="flex justify-between">
		<!-- 榜单-->
		<div class="p5 w190 bg-white rounded-lg">
			<p class="text-2xl font-bold">{{rankList[selectedIndex].name}}</p>
			<a-table
					class="mt-3"
					:pagination="false"
					:data-source="rankData"
					size="small">
				<a-table-column key="index" title="序号" align="center" :width="50" data-index="index">
					<template #default="{ text: index }">
						<div
								:class="{'bg-red-500': index === 1, 'bg-[#e67225]': index === 2, 'bg-[#e6bf25]': index === 3, 'bg-gray': index > 3}"
								class="text-white inline-block w-5 text-12px">
							{{ index }}
						</div>
					</template>
				</a-table-column>
				<a-table-column key="categoryName" title="分类" :width="80" data-index="categoryName">
					<template #default="{ text: categoryName}">
						<!--									TODO: 路由跳转：分类参数-->
						<RouterLink class="text-gray-400" :to="`/info/1`">
							{{ categoryName }}
						</RouterLink>
					</template>
				</a-table-column>
				<a-table-column key="bookName" title="书名" :width="180" data-index="bookName">
					<template #default="{ text: bookName, record: { id }}">
						<RouterLink :to="`/info/${id}`" class="text-hidden">
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
		<!--	选项-->
		<div class="py5 w60 h-fit bg-white  rounded-lg">
			<p class="font-bold text-xl px-4">排行榜</p>
			<ul class="mt-2">

				<li v-for="(item, index) in rankList" :key="item.id" class="mt-2 px-2">
					<a-button
							@click="changeRank(index)"
							type="text"
							:class="{'text-blue!': selectedIndex === index}"
							class="text-black p2 h-full w-full text-left text-16px">{{item.name}}</a-button>
				</li>

			</ul>
		</div>
	</div>

</template>

<style scoped>
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
</style>