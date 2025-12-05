<script setup lang="ts">
/**
 * 热门推荐
 */
import type {HomeBookView} from "@/type/home.ts";
import {message} from "ant-design-vue";
 const {data} = defineProps<{
	 data: HomeBookView[]
 }>()

const onStar = () => {
	 // 发送收藏请求
}

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
	<div class="">
		<p class="inline-flex items-center gap-2"><FireFilled style="color:#ff6600; font-size: 18px"/><span class="text-[#0082dd] text-lg font-bold">热门推荐</span></p>
		<div class="grid grid-cols-3 gap-y-4 mt-2 bg-white p-4 shadow ">
			<div v-for="item in data" class="overflow-hidden duration-240 justify-self-center rounded-lg w-58 hover:-translate-y-2 shadow hover:shadow-lg">
				<div class="w-full h-50 rounded-t-lg overflow-hidden relative">
					<img :src="item.picUrl" alt="" class="object-cover size-full transition duration-240 hover:scale-110">
					<div class="absolute right-2 top-2 ">
						<StarFilled @click="" class="text-gray bg-white/80 rounded-full size-8 text-lg inline-flex items-center pl-1.8 hover:bg-white mr-2 hover:text-amber"/>
						<ShareAltOutlined @click="onShare(item.bookId)" class="text-gray bg-white/80 rounded-full size-8 text-lg inline-flex items-center pl-1.8 hover:bg-white hover:text-blue"/>
					</div>
				</div>
				<RouterLink :to="`/info/${item.bookId}`" class="block py-2 px-4 bg-[#efefef] h-29 overflow-hidden">
					<p class="font-bold mb-1">{{item.bookName}}</p>
					<p class="text-gray-500 text-xs">作者：{{item.authorName}}</p>
					<p class="text-gray-500 overflow-hidden text-xs">{{item.bookDesc}}</p>
				</RouterLink>
				<div style="width: 100%;height: 10px;background-color: #efefef"></div>
			</div>
		</div>
	</div>
</template>

<style scoped>
</style>