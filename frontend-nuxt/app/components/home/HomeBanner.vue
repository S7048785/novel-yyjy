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
  <div class="relative rounded-xl overflow-hidden shadow-sm">
    <UCarousel
      v-slot="{ index }"
      :items="books.slice(0, 5)"
      dots
      :autoplay="true"
    >
      <div class="aspect-2/1 relative">
        <img
          :src="books[index]?.picUrl"
          :alt="books[index]?.bookName"
          class="object-fill w-full h-full"
        />
        <div
          class="absolute inset-0 from-black/60 via-transparent to-transparent"
        />
        <div class="absolute bottom-0 left-0 right-0 p-6">
          <div class="flex items-center gap-2 mb-2">
            <span class="px-2 py-0.5 bg-orange-500 text-xs rounded">{{
              books[index]?.tag || "推荐"
            }}</span>
          </div>
          <h3 class="text-2xl font-bold mb-1">
            {{ books[index]?.bookName }}
          </h3>
          <p class="text-sm mb-3 line-clamp-2">
            {{ books[index]?.intro }}
          </p>
          <UButton
            color="primary"
            variant="solid"
            @click="goToBook(books[index]!.bookId)"
          >
            立即阅读
          </UButton>
        </div>
      </div>
    </UCarousel>
  </div>
</template>
