<script setup lang="ts">
import { getBookRankAPI } from "@/api/book.ts";
import type { BookRankView } from "@/type/book.ts";
import router from "@/router";

const rankList = [
  { id: 1, name: '点击榜', icon: '🔥' },
  { id: 2, name: '新书榜', icon: '📚' },
  { id: 3, name: '更新榜', icon: '✨' },
  { id: 4, name: '评论榜', icon: '💬' }
]

const rankData = ref<BookRankView[]>([]);
const selectedIndex = ref(0);
const loading = ref(true);
const hoverIndex = ref<number | null>(null);

const getRankBookData = async () => {
  loading.value = true;
  try {
    const res = await getBookRankAPI(rankList[selectedIndex.value].id);
    rankData.value = res.data;
    rankData.value.forEach((item, index) => {
      item.index = index + 1;
    });
  } catch (error) {
    console.error('获取排行榜数据失败:', error);
  } finally {
    loading.value = false;
  }
}

const changeRank = (index: number) => {
  selectedIndex.value = index;
  getRankBookData();
}

const goToBook = (id: string) => {
  router.push(`/info/${id}`);
}

const goToChapter = (bookId: string, chapterId: string) => {
  router.push(`/read/${bookId}/${chapterId}`);
}

onMounted(() => {
  getRankBookData();
});

// 格式化字数
const formatWordCount = (count: number) => {
  if (count >= 10000) {
    return `${(count / 10000).toFixed(1)}万字`;
  }
  return `${count}字`;
};

// 获取排名徽章样式
const getRankBadgeClass = (rank: number) => {
  if (rank === 1) return 'bg-gradient-to-br from-yellow-400 to-orange-500';
  if (rank === 2) return 'bg-gradient-to-br from-gray-300 to-gray-500';
  if (rank === 3) return 'bg-gradient-to-br from-orange-300 to-orange-600';
  return 'bg-gray-400';
};

// 获取排名徽章图标
const getRankBadgeIcon = (rank: number) => {
  if (rank === 1) return '👑';
  if (rank === 2) return '🥈';
  if (rank === 3) return '🥉';
  return '';
};
</script>

<template>
  <div class="min-h-screen bg-gray-50 py-3">
    <div class="max-w-5xl mx-auto px-3">
      <!-- 页面标题 -->
      <div class="mb-4 text-center">
        <h1 class="text-2xl font-bold text-gray-800 mb-1">小说排行榜</h1>
        <p class="text-gray-600 text-xs">发现最受欢迎的精彩作品</p>
      </div>

      <!-- 榜单分类选择 -->
      <div class="flex justify-center mb-4">
        <div class="bg-white rounded-md shadow-sm p-0.5 flex gap-0.5">
          <button
            v-for="(item, index) in rankList"
            :key="item.id"
            @click="changeRank(index)"
            :class="[
              'px-4 py-1.5 rounded-sm font-medium transition-all duration-300 flex items-center gap-1.5 text-xs',
              selectedIndex === index
                ? 'bg-blue-500 text-white shadow-sm transform scale-105'
                : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100'
            ]"
          >
            <span class="text-sm">{{ item.icon }}</span>
            <span>{{ item.name }}</span>
          </button>
        </div>
      </div>

      <!-- 排行榜内容 -->
      <div v-if="loading" class="flex justify-center items-center h-64">
        <a-spin size="large" tip="加载中..." />
      </div>

      <div v-else>
        <!-- 列表项 -->
        <TransitionGroup name="list" tag="div" class="space-y-3">
          <div
            v-for="(book, index) in rankData"
            :key="`${book.id}-${selectedIndex}`"
            @mouseenter="hoverIndex = index"
            @mouseleave="hoverIndex = null"
            @click="goToBook(book.id)"
            :class="[
              'bg-white rounded-lg shadow-sm hover:shadow-lg transition-all duration-300 cursor-pointer overflow-hidden',
              'transform hover:-translate-y-0.5',
              index === 0 ? 'ring-2 ring-yellow-400 ring-opacity-50' : ''
            ]"
          >
          <div class="p-2">
            <div class="flex gap-3">
              <!-- 排名徽章 -->
              <div class="flex-shrink-0">
                <div
                  :class="[
                    'w-10 h-10 rounded-full flex items-center justify-center text-white font-bold text-base shadow-md',
                    getRankBadgeClass(book.index!)
                  ]"
                >
                  {{ getRankBadgeIcon(book.index!) || book.index }}
                </div>
              </div>

              <!-- 书籍信息 -->
              <div class="flex-1 grid grid-cols-[112px_1fr] gap-3">
                <!-- 左侧：书籍图片 -->
                <div class="">
                  <div class="relative overflow-hidden rounded-md shadow-sm w-[112px] h-[156px] group">
                    <img
                      :src="book.picUrl || '/image/default.png'"
                      :alt="book.bookName"
											onerror="this.src = '/image/default.png'"
                      class="w-full h-full object-cover transform transition-all duration-300 ease-in-out group-hover:scale-110"
                    />
                    <div class="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent opacity-0 group-hover:opacity-100 transition-all duration-300"></div>
                    <!-- 分类标签 -->
                    <span class="absolute top-1 left-1 px-1.5 py-0.5 bg-blue-500 text-white text-xs rounded-full z-10">
                      {{ book.categoryName }}
                    </span>
                  </div>
                </div>

                <!-- 右侧：详细信息 -->
                <div class=" flex flex-col justify-between">
                  <!-- 标题和作者 -->
                  <div>
                    <h3 class="text-base font-bold text-gray-800 mb-0.5 hover:text-blue-500 transition-colors">
                      {{ book.bookName }}
                    </h3>
                    <p class="text-gray-600 mb-1 text-xs">
                      <span class="mr-2">作者：{{ book.authorName }}</span>
                      <span>{{ formatWordCount(book.wordCount) }}</span>
                    </p>
                  </div>

                  <!-- 简介 -->
                  <div v-if="book.bookDesc" v-html="book.bookDesc" class="text-gray-700 mb-1 text-xs line-clamp-2">
                  </div>
                  <div v-else class="text-gray-500 mb-1 italic text-xs">
                    暂无简介
                  </div>

                  <!-- 最新章节 -->
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-1.5 text-xs">
                      <span class="text-gray-500">最新章节：</span>
                      <a
                        href="javascript:void(0)"
                        @click.stop="goToChapter(book.id, book.lastChapterId || '')"
                        class="text-blue-500 hover:text-blue-700 hover:underline truncate max-w-xs"
                      >
                        {{ book.lastChapterName }}
                      </a>
                    </div>

                    <!-- 操作按钮 -->
                    <div class="flex gap-1.5">
                      <button
                        @click.stop="goToBook(book.id)"
                        class="px-2 py-1 bg-blue-500 text-white rounded-sm hover:bg-blue-600 transition-colors text-xs"
                      >
                        开始阅读
                      </button>
                      <button
                        class="px-2 py-1 border border-gray-300 text-gray-700 rounded-sm hover:bg-gray-50 transition-colors text-xs"
                      >
                        加入书架
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 动画线条 -->
            <div
              v-if="hoverIndex === index"
              class="absolute bottom-0 left-0 h-1 bg-gradient-to-r from-blue-400 to-blue-600 animate-pulse"
              :style="{ animationDuration: '2s' }"
            ></div>
          </div>
        </div>
        </TransitionGroup>
      </div>

      <!-- 底部提示 -->
      <div v-if="!loading && rankData.length === 0" class="text-center py-16">
        <p class="text-gray-500 text-lg">暂无数据</p>
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

/* 列表动画 */
.list-enter-active {
  transition: all 0.5s ease-out;
}

.list-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.list-move {
  transition: transform 0.5s ease;
}
</style>