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
      <div class="w-1 h-8 rounded-full bg-orange-500"></div>
      <UIcon name="i-lucide-flame" class="text-orange-500" />
      <h3 class="font-semibold">本周强推</h3>
    </div>
    <div class="grid grid-cols-6 gap-4">
      <div
        v-for="book in books.slice(0, 6)"
        :key="book.bookId"
        class="cursor-pointer group"
        @click="goToBook(book.bookId)"
      >
        <div class="rounded-2xl overflow-hidden mb-2 shadow-md">
          <img
            :src="book.picUrl"
            :alt="book.bookName"
            class="w-full h-[80%] object-cover rounded-2xl group-hover:scale-105 transition-transform duration-300"
          />
        </div>
        <p
          class="text-base font-bold truncate group-hover:text-primary transition-colors mb-1"
        >
          {{ book.bookName }}
        </p>
        <p class="text-xs truncate">
          {{ book.authorName }}
        </p>
      </div>
    </div>
  </div>
</template>
