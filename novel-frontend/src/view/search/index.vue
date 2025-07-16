<script setup lang="ts">

import {getBookByNameAPI} from "@/api/book.ts";
import type { BookInfoSearchView} from "@/type/book.ts";
const route = useRoute()
const router = useRouter()

const bookData = ref<BookInfoSearchView[]>([])

const searchBook = async () => {
	const { name } = route.query
	if (typeof name === 'string') {
		const res = await getBookByNameAPI(name);
		if (res.data.length === 0) {
			return;
		}
		bookData.value = res.data;
	}
}
const loading = ref(true);
onMounted(async () => {
	await searchBook()
	loading.value = false;
})
</script>

<template>
	<div class="w-255 min-h-100 bg-white p-5 rounded-lg flex gap-4 flex-wrap">
		<div v-if="loading"></div>
		<div v-else-if="bookData.length === 0" class="left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 absolute">
			<a-empty description="没有找到任何书籍"/>
		</div>
		<div v-else v-for="item in bookData" class="flex w-120">
				<router-link :to="`/info/${item.id}`">
					<img class="w-30 mr-4" :src="item.picUrl" onerror="this.src = '/public/image/default.png'" alt="">
				</router-link>
				<div class="flex flex-col w-2/3">
					<p>
						<router-link :to="`/info/${item.id}`" class="mr-4 font-bold">
							{{item.bookName}}
						</router-link>
						<span class="text-gray text-sm">
							{{item.authorName}}
						</span>
					</p>
					<p class="text-sm my-2">
						{{item.categoryName}}
						<router-link class="ml-4 text-blue" :to="`/read/${item.id}/${item.lastChapterId}`">
							{{item.lastChapterName}}
						</router-link>
					</p>
					<p v-html="item.bookDesc" class="overflow-hidden text-gray-500 text-sm text-ellipsis h-20">
					</p>
				</div>
		</div>
	</div>
</template>

<style scoped>

</style>