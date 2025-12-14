<script setup lang="ts">

import {getBookHistoryAPI} from "@/api/book.ts";
import type {BookHistoryView} from "@/type/book.ts";
const history = ref<BookHistoryView[]>([])

const getBookHistory = async () => {
	const res = await getBookHistoryAPI()
	history.value = res.data
}
onMounted(() => {
	getBookHistory()
})
</script>

<template>
	<div>
		<p class="border-b pb-2 mb-2">
			<span class="text-xl font-bold mr-4">历史浏览</span>
			<span class="text-sm text-gray-500">最多显示 20 本书</span>
		</p>
		<a-table
				class="mt-3"
				:pagination="false"
				:data-source="history"
				size="small">
			<a-table-column key="categoryName" title="分类" :width="80" data-index="categoryName">
				<template #default="{ text: categoryName}">
					<!--									TODO: 路由跳转：分类参数-->
					<span class="text-gray-400">
						{{ categoryName }}
					</span>
				</template>
			</a-table-column>
			<a-table-column key="bookName" title="书名" :width="180" data-index="bookName">
				<template #default="{ text: bookName, record: { bookId }}">
					<RouterLink :to="`/info/${bookId}`" class="text-hidden">
						{{ bookName }}
					</RouterLink>
				</template>
			</a-table-column>
			<!--									TODO: 路由跳转：章节参数-->
			<a-table-column key="chapterName" title="阅读章节" ellipsis="true" data-index="chapterName">
				<template #default="{ text , record: { chapterId, bookId}}">
					<RouterLink class="text-12px text-blue" :to="`/read/${bookId}/${chapterId}`">
						{{ text }}
					</RouterLink>
				</template>
			</a-table-column>
			<a-table-column key="authorName" title="作者" :width="200" ellipsis="true" data-index="authorName">
				<template #default="{ text: authorName }">
									<span class="text-gray-400 text-12px">
											{{ authorName }}
									</span>
				</template>
			</a-table-column>
		</a-table>
	</div>
</template>

<style scoped>

</style>