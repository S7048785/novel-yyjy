<script lang="ts" setup>
import { useHome } from "~/composables/useHome";

const {
  loading,
  bannerBooks,
  recommendBooks,
  hotBooks,
  boutiqueBooks,
  visitRank,
  newestAdd,
  recentUpdate,
  formatTime,
} = useHome();

// 跳转到书籍详情
const goToBook = (bookId: string) => {
  navigateTo(`/book/${bookId}`);
};
</script>

<template>
  <div class="min-h-screen">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <UIcon
        name="i-lucide-loader-2"
        class="w-8 h-8 animate-spin text-orange-500"
      />
    </div>

    <div v-else class="max-w-7xl mx-auto px-4 py-6 space-y-6">
      <!-- 第一行：轮播图 + 新闻公告 -->
      <div class="grid grid-cols-3 gap-6">
        <!-- 轮播图 -->
        <div class="col-span-2">
          <HomeBanner :books="bannerBooks" @click="goToBook" />
        </div>
        <!-- 新闻公告 -->
        <HomeNotice />
      </div>

      <!-- 第二行：本周强推（独占一行） -->
      <HomeRecommend :books="recommendBooks" @click="goToBook" />

      <!-- 第三行：热门推荐 + 点击榜单 -->
      <div class="grid grid-cols-4 gap-6">
        <!-- 热门推荐 -->
        <div class="col-span-3">
          <HomeHot :books="hotBooks" @click="goToBook" />
        </div>
        <!-- 点击榜单 -->
        <HomeVisitRank :books="visitRank" @click="goToBook" />
      </div>

      <!-- 第四行：精品推荐 + 新书榜单 -->
      <div class="grid grid-cols-4 gap-6">
        <!-- 精品推荐 -->
        <div class="col-span-3">
          <HomeBoutique :books="boutiqueBooks" @click="goToBook" />
        </div>
        <!-- 新书榜单 -->
        <HomeNewBook :books="newestAdd" @click="goToBook" />
      </div>

      <!-- 第五行：最近更新（独占一行） -->
      <HomeRecentUpdate
        :books="recentUpdate"
        :format-time="formatTime"
        @click="goToBook"
      />
    </div>
  </div>
</template>
