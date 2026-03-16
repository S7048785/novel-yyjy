<script lang="ts" setup>
import type { VisitRankBookView } from "~/__generated/model/static";

defineProps<{
  books: readonly VisitRankBookView[];
}>();

const emit = defineEmits<{
  click: [bookId: string];
}>();

const goToBook = (bookId: string) => {
  emit("click", bookId);
};
</script>

<template>
  <div class="bg-secondary rounded-xl shadow-sm p-4">
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center gap-2">
        <UIcon name="i-lucide-trophy" class="text-yellow-500" />
        <h3 class="font-semibold">点击榜单</h3>
      </div>
      <NuxtLink to="/rank" class="text-sm hover:text-orange-500">更多</NuxtLink>
    </div>
    <div class="space-y-3">
      <div
        v-for="(book, index) in books.slice(0, 6)"
        :key="book.id"
        class="flex items-center gap-3 cursor-pointer group"
        @click="goToBook(book.id)"
      >
        <span
          :class="[
            'w-6 h-6 rounded flex items-center justify-center text-sm font-bold',
            index < 3
              ? 'bg-orange-500 text-white'
              : 'bg-gray-200 text-gray-600',
          ]"
        >
          {{ index + 1 }}
        </span>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium truncate group-hover:text-orange-600">
            {{ book.bookName }}
          </p>
          <p class="text-xs">{{ book.authorName }}</p>
        </div>
      </div>
    </div>
  </div>
</template>
