<script lang="ts" setup>
import type { LastUpdateBookView } from "~/__generated/model/static";
import type { TableColumn } from "@nuxt/ui";

const props = defineProps<{
  books: readonly LastUpdateBookView[];
  formatTime: (time: string) => string;
}>();

const emit = defineEmits<{
  click: [bookId: string];
}>();

const goToBook = (bookId: string) => {
  emit("click", bookId);
};

const columns: TableColumn<LastUpdateBookView>[] = [
  {
    accessorKey: "categoryName",
    header: "类别",
    cell: ({ row }) =>
      h(
        "span",
        { class: "px-2 py-1 text-sm rounded" },
        row.original.categoryName,
      ),
  },
  {
    accessorKey: "bookName",
    header: "书名",
    cell: ({ row }) =>
      h(
        "span",
        {
          class:
            "font-medium hover:text-blue-600 cursor-pointer transition-colors",
        },
        row.original.bookName,
      ),
  },
  {
    accessorKey: "lastChapterName",
    header: "最新章节",
    cell: ({ row }) =>
      h(
        "span",
        { class: "text-sm truncate max-w-[200px] block" },
        row.original.lastChapterName,
      ),
  },
  {
    accessorKey: "authorName",
    header: "作者",
    cell: ({ row }) => h("span", { class: "text-sm" }, row.original.authorName),
  },
  {
    accessorKey: "lastChapterUpdateTime",
    header: "更新时间",
    cell: ({ row }) =>
      h(
        "span",
        { class: "text-sm" },
        row.original.lastChapterUpdateTime
          ? props.formatTime(row.original.lastChapterUpdateTime)
          : "",
      ),
  },
];

const displayBooks = computed(() => props.books.slice(0, 8));
</script>

<template>
  <div class="bg-secondary rounded-xl shadow-sm p-4">
    <div class="flex items-center gap-2 mb-4">
      <div class="w-1 h-8 rounded-full bg-blue-500"></div>
      <UIcon name="i-lucide-clock" class="text-blue-500" />
      <h3 class="font-semibold">最近更新</h3>
    </div>
    <UTable
      :data="displayBooks"
      :columns="columns"
      @row-click="(row: { id: string }) => goToBook(row.id)"
    />
  </div>
</template>
