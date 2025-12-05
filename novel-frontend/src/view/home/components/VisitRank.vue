<script setup lang="ts">
/**
 * 点击榜单
 */
import type {VisitRankBookView} from "@/type/home.ts";

const {data} = defineProps<{
	data: VisitRankBookView[]
}>()
const segmenteds = reactive(['周榜', '总榜']);
const value = ref(segmenteds[0]);

</script>

<template>
	<div class="w-30%">
		<div class="flex justify-between">
			<p class="text-xl font-bold pb-2 border-b">点击榜单</p>
			<a-segmented class="bg-gray-200" v-model:value="value" :options="segmenteds" />
		</div>
		<ul class="border bg-white px-4">
			<li v-for="(item, index) in data" class="my-3">
				<div v-if="index === 0" class="">
					<a-tag color="#f50" style="font-weight: bold;margin-bottom: 8px;">NO.1</a-tag>
					<div class="grid grid-cols-3 gap-2">
						<div class="">
							<img :src="item.picUrl" alt="" class="size-full">
						</div>
						<div class="inline-flex flex-col gap-2 col-span-2">
							<RouterLink :to="`/info/${item.id}`" class="hover:text-blue">{{ item.bookName }}</RouterLink>
							<p class="text-sm text-[#3f9bcd] font-bold">{{ item.authorName }}</p>
							<p class="text-red font-bold">{{ (item.visitCount / 1000) }}K<span class="text-sm font-400">点击</span>
							</p>
						</div>
					</div>
				</div>

				<!--					标题-->
				<div v-else class="text-hidden text-sm clear-both flex items-center w-full hover:text-blue pt-3 border-t">
					<a-tag>
						<span class="relative bottom-0.5 font-bold">{{ index + 1 }}</span>
					</a-tag>
					<span class="text-gray mr-1 ">[{{ item.categoryName }}]</span>
					<RouterLink
							:to="`/info/${item.id}`"
							class="overflow-hidden w-40 text-ellipsis overflow-hidden">{{ item.bookName }}
					</RouterLink>
					<span class="text-amber font-bold ml-auto">{{ (item.visitCount / 1000) }}K</span>

				</div>
			</li>
		</ul>
		<a-button class="w-full">
			<!--				TODO: 查看更多-->
			<span style="font-size: 12px">查看更多</span>
		</a-button>
	</div>
</template>

<style scoped>

</style>