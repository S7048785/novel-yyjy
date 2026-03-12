<script setup lang="ts">
/**
 * 热门推荐
 */
import type {HomeBookView} from "@/type/home.ts";
import {message} from "ant-design-vue";
const {data} = defineProps<{
	 data: HomeBookView[]
 }>()

const onShare = async (bookId: string) => {
	try {
		await navigator.clipboard.writeText(`${window.location.host}/info/${bookId}`)
		await nextTick() // 等待 DOM 更新完成
			message.success('已复制小说详情链接!')
	} catch (err) {
		// 处理错误
		console.error('复制失败', err)
	}
}
</script>

<template>
	<div class="h-full">
		<p class="inline-flex items-center gap-2">
			<FireFilled style="color:#ff6600; font-size: 18px"/>
			<span class="text-[#0082dd] text-lg font-bold">热门推荐</span>
		</p>
		<div class="grid grid-cols-2 gap-3 mt-2 bg-white p-3 shadow h-[calc(100%-32px)]">
			<div v-for="item in data" class="overflow-hidden duration-240 rounded-lg hover:-translate-y-1 shadow-sm hover:shadow-md transition-all">
				<div class="w-full h-40 overflow-hidden relative">
					<img
						:src="item.picUrl"
						alt=""
						class="object-cover w-full h-full transition-transform duration-300 hover:scale-105"
						onerror="this.src='/image/default.png'"
					/>
					<div class="absolute right-2 top-2 flex gap-1">
						<ShareAltOutlined @click="onShare(item.bookId)" class="text-gray-500 bg-white/80 rounded-full w-7 h-7 text-base flex items-center justify-center hover:bg-white hover:text-blue-500 transition-colors cursor-pointer"/>
					</div>
					<!-- 分类标签 -->
					<span class="absolute top-2 left-2 px-2 py-0.5 bg-blue-500 text-white text-xs rounded-full">
						{{ item.categoryName }}
					</span>
				</div>
				<div class="p-3 bg-[#f9f9f9]">
					<RouterLink :to="`/info/${item.bookId}`" class="block">
						<p class="font-bold text-sm mb-1 hover:text-blue-500 transition-colors truncate">{{item.bookName}}</p>
						<p class="text-gray-500 text-xs mb-1">作者：{{item.authorName}}</p>
						<p class="text-gray-400 text-xs line-clamp-2" v-html="item.bookDesc"></p>
					</RouterLink>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
/* 文字截断 */
.line-clamp-2 {
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}
</style>