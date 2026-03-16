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
  <div class="rounded-xl p-4">
    <div class="flex items-center gap-2 mb-4">
      <div class="w-1 h-8 rounded-full bg-purple-500"></div>
      <UIcon name="i-lucide-gem" class="text-purple-500" />
      <h3 class="font-semibold">精品推荐</h3>
    </div>
    <div class="grid grid-cols-5 gap-4">
      <div
        v-for="book in books.slice(0, 5)"
        :key="book.bookId"
        class="cursor-pointer group"
        @click="goToBook(book.bookId)"
      >
        <div class="rounded-lg overflow-hidden mb-2 shadow-md">
          <img
            :src="book.picUrl"
            :alt="book.bookName"
            class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          />
        </div>
        <p
          class="font-md truncate group-hover:text-purple-600 transition-colors"
        >
          {{ book.bookName }}
        </p>
        <p class="text-xs text-gray-500 truncate">
          {{ book.authorName }}
        </p>
      </div>
    </div>
  </div>
</template>
