<script setup lang="ts">
/**
 * 本周推荐
 */
import type {HomeBookView} from "@/type/home.ts";

const {data} = defineProps<{
	data: HomeBookView[]
}>()
</script>

<template>
	<div class="flex-1 bg-black p-4">
		<div class="flex justify-between items-center">
			<a class="text-blue  inline-flex items-center">
				<CrownFilled class="color-amber-300 mr-2"/>
				<span class="font-bold">本周推荐</span>
			</a>
			<a class="text-blue-300 ">更多推荐 ></a>
		</div>
		<div class="text-white">
			<router-link :to="`/info/${item.bookId}`" v-for="(item, index) in data"
									 class="flex items-center px-4 py-2 mt-4 rounded-lg hover:bg-gray-800">
				<TrophyFilled class="mr-4"
											:class="{'text-amber': index === 0,'text-gray-300': index === 1,'text-orange-900': index === 2, 'opacity-0': index > 2}"/>
				<div class="">
					<p class="text-sm font-bold">{{ item.bookName }}</p>
					<div class="text-sm text-gray w-full grid">
						<p>作者：{{ item.authorName }}</p>
						<p class="overflow-hidden w-full whitespace-nowrap text-ellipsis;">{{ item.intro }}</p>
					</div>
				</div>
				<RightOutlined class="text-blue ml-auto"/>
			</router-link>
		</div>
	</div>
</template>

<style scoped>

</style>