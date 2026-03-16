<script lang="ts" setup>
import type { HomeBookView } from "~/__generated/model/static";

defineProps<{
  books: HomeBookView[];
}>();

const emit = defineEmits<{
  click: [bookId: string];
}>();

const goToBook = (bookId: string) => {
  emit("click", bookId);
};
</script>

<template>
  <div class="rounded-xl p-4 h-full">
    <div class="flex items-center gap-2 mb-4">
      <div class="w-1 h-8 rounded-full bg-yellow-500"></div>

      <UIcon name="i-lucide-star" class="text-yellow-500" />
      <h3 class="font-semibold">热门推荐</h3>
    </div>
    <div class="grid grid-cols-2 gap-4">
      <div
        v-for="book in books.slice(0, 2)"
        :key="book.bookId"
        class="flex gap-4 p-6 rounded-2xl bg-secondary cursor-pointer group"
        @click="goToBook(book.bookId)"
      >
        <!-- 左侧封面 -->
        <div class="w-40 shrink-0 h-full">
          <div class="rounded-lg overflow-hidden shadow-md h-full">
            <img
              :src="book.picUrl"
              :alt="book.bookName"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            />
          </div>
        </div>
        <!-- 右侧信息 -->
        <div class="flex flex-col flex-1 min-w-0">
          <p
            class="text-lg font-bold truncate group-hover:text-orange-600 transition-colors"
          >
            {{ book.bookName }}
          </p>
          <p class="text-sm text-gray-500 mt-1 line-clamp-6">
            {{ book.intro || book.bookDesc || "暂无描述" }}
          </p>
          <span
            v-if="book.tag"
            class="inline-block mt-auto px-4 py-1 bg-orange-100 text-orange-600 text-sm rounded-lg w-fit"
          >
            {{ book.tag }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
