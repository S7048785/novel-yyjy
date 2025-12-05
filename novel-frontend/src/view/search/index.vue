<script setup lang="ts">

import {getBookByNameAPI} from "@/api/book.ts";
import type { BookInfoSearchView} from "@/type/book.ts";
const route = useRoute()

const bookData = ref<BookInfoSearchView[]>([])

const searchBook = async () => {
	const { name } = route.query
	if (typeof name === 'string') {
		loading.value = true
		try {
			const res = await getBookByNameAPI(name);
			bookData.value = res.data;
		} catch (error) {
			console.error('搜索失败:', error)
			bookData.value = []
		} finally {
			loading.value = false
		}
	}
}

const loading = ref(true);
onMounted(async () => {
	await searchBook()
})
</script>

<template>
	<div class="w-full max-w-4xl mx-auto min-h-screen bg-white p-4 md:p-6">
		<!-- 加载状态 -->
		<div v-if="loading" class="flex justify-center items-center h-64">
			<a-spin tip="搜索中..." size="large">
				<div class="content" />
			</a-spin>
		</div>

		<!-- 空结果状态 -->
		<div v-else-if="bookData.length === 0" class="flex flex-col items-center justify-center py-16">
			<a-empty description="没有找到相关书籍" />
		</div>

		<!-- 搜索结果 - 单列布局 -->
		<div v-else>
			<div class="space-y-5">
				<div
					v-for="item in bookData"
					:key="item.id"
					class="border border-gray-200 rounded-xl p-5 hover:shadow-lg transition-all duration-300 bg-white"
				>
					<div class="flex flex-col sm:flex-row gap-5">
						<!-- 封面图片 -->
						<div class="flex-shrink-0">
							<router-link :to="`/info/${item.id}`" class="block">
								<img
									class="w-24 h-30 object-cover rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300"
									:src="item.picUrl"
									onerror="this.src = '/public/image/default.png'"
									:alt="item.bookName"
								/>
							</router-link>
						</div>

						<!-- 书籍信息 -->
						<div class="flex-1 min-w-0">
							<!-- 标题和分类 -->
							<div class="flex flex-wrap items-start justify-between gap-2 mb-3">
								<h3 class="text-lg font-bold text-gray-800 hover:text-blue-600 transition-colors">
									<router-link
										:to="`/info/${item.id}`"
										v-html="item.bookName"
										class="block"
									></router-link>
								</h3>
								<a-tag color="blue" class="text-xs flex-shrink-0">{{ item.categoryName }}</a-tag>
							</div>

							<!-- 作者信息 -->
							<div class="text-sm text-gray-600 mb-3">
								<span class="font-medium">作者：</span>
								<span>{{ item.authorName }}</span>
							</div>

							<!-- 书籍描述 - 只显示两行 -->
							<div class="mb-4">
								<p
									v-html="item.bookDesc"
									class="text-gray-700 text-sm leading-relaxed line-clamp-2"
								></p>
							</div>

							<!-- 最新章节和操作按钮 -->
							<div class="flex flex-wrap items-center justify-between gap-3">
								<!-- 最新章节 -->
								<div class="text-sm min-w-0 flex-1" v-if="item.lastChapterId && item.lastChapterName">
									<span class="text-gray-500 mr-2">最新：</span>
									<router-link
										:to="`/read/${item.id}/${item.lastChapterId}`"
										class="text-blue-600 hover:text-blue-800 transition-colors truncate inline-block align-middle"
										:title="item.lastChapterName"
									>
										{{ item.lastChapterName }}
									</router-link>
								</div>

								<!-- 操作按钮 -->
								<div class="flex-shrink-0">
									<router-link :to="`/info/${item.id}`">
										<a-button size="small" type="primary">查看详情</a-button>
									</router-link>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
:deep(em) {
	font-style: normal;
	background-color: #fff3cd; /* 柔和的浅黄色（类似 Bootstrap 的 warning 色） */
	color: #856404;            /* 深一点的棕黄文字，提高对比度 */
	padding: 0.15em 0.3em;     /* 内边距，让文字呼吸 */
	border-radius: 0.3em;      /* 圆角，更柔和 */
	font-weight: 600;          /* 稍微加粗，突出但不突兀 */
	box-shadow: 0 0 0 1px #ffeaa7 inset; /* 内发光边框，增强层次感 */
	transition: background-color 0.2s ease; /* 平滑过渡（用于动态高亮） */
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .flex-col.sm\:flex-row {
    flex-direction: column !important;
  }

  .gap-5 {
    gap: 1.25rem !important;
  }

  .w-24 {
    width: 8rem !important;
    height: auto !important;
  }

  .p-5 {
    padding: 1rem !important;
  }

  h3.text-lg {
    font-size: 1.125rem !important;
  }
}

/* 超小屏幕适配 */
@media (max-width: 480px) {
  .p-4.md\:p-6 {
    padding: 0.75rem !important;
  }

  .w-24 {
    width: 6rem !important;
  }

  .space-y-5 {
    gap: 1rem !important;
  }
}
</style>