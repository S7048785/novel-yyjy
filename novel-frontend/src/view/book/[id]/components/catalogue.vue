<script setup lang="ts">
import {getBookChapterAPI} from "@/api/comment.ts";
import type {BookChapterView} from "@/type/book.ts";
import {DownOutlined, UpOutlined} from "@ant-design/icons-vue"
import emitter from "@/utils/emitter.ts"
const route = useRoute();
const router = useRouter();
const {bookId} = defineProps<{ bookId?: string }>();

// 章节列表
const bookChapter = reactive<BookChapterView>({
	bookChapters: [],
	total: 0
})
// 获取小说章节
const getBookChapter = async (isAll?: number, desc: boolean = false) => {
	if (bookChapter.total !== 0 && bookChapter.total == bookChapter.bookChapters.length) {
	bookChapter.bookChapters.sort(() => -1)
		chapterOrderDesc.value = desc;
		return;
	}
	const id = route.params.id as string;
	const res = await getBookChapterAPI({id, isAll, orderBy: desc ? 1 : 0});
	bookChapter.bookChapters = res.data.bookChapters;
	bookChapter.total = res.data.total;
	chapterOrderDesc.value = desc;
	if (isAll === 1) {
		isShow.value = true;
	}
}
// 章节排序
const chapterOrderDesc = ref(false)

// 章节展示
const isShow = ref<boolean | null>(null)

onMounted(async () => {
	await getBookChapter();
	const firstChapter = bookChapter.bookChapters[0].id;
	emitter.on('read', () => {
		router.push(`/read/${bookId}/${firstChapter}`)
	});
})

onUnmounted(() => {
	emitter.off('read')
})
</script>

<template>
	<div class="p-5 mt-5 w-full rounded-lg bg-white">
		<!--					头部-->
		<div>
			<div class="float-left">
				<span class="text-xl font-bold mr-4 mb-4">目录</span>
				<span class="text-sm text-gray-500">连载共 {{ bookChapter.total }} 章节</span>
			</div>
			<div class="float-right">
				<a-button v-if="isShow !== null" @click="isShow = !isShow" shape="circle" type="text" class="inline-flex items-center justify-center mr-4 border-neutral-300">
					<template #icon>
						<DownOutlined v-if="!isShow"/>
						<UpOutlined v-else />
					</template>
				</a-button>
				<!--				<a-button shape="circle" type="text" class="mr-4 border-neutral-300">-->
				<!--				</a-button>-->
				<a-button @click="getBookChapter(1, !chapterOrderDesc)" type="text" class="mr-4 px-6 border-neutral-300">
					{{ chapterOrderDesc ? '正' : '倒' }}序
				</a-button>
			</div>
			<div class="clear-both"></div>
		</div>
		<!--					章节-->
		<div v-if="isShow || isShow == null">
			<div v-for="item in bookChapter.bookChapters" :key="item.id" class="float-left p-2 w-1/3 text-hidden">
				<span class="text-sm text-gray-500 hover:text-blue">
					<router-link :to="`/read/${bookId}/${item.id}`">
						{{ item.chapterName }}
					</router-link>
				</span>
			</div>
			<div class="clear-both"></div>
			<!--				查看更多章节-->
			<div v-if="bookChapter.bookChapters.length < bookChapter.total" class="mt-5">
				<a-button type="text" class="border-neutral-300 inline-flex items-center justify-center"
									@click="getBookChapter(1)" block>
					查看更多
				</a-button>
			</div>
		</div>

	</div>
</template>

<style scoped>

</style>