<script setup lang="ts">
import {
  addOrDelBookShelfAPI,
  getBookInfoAPI,
  getBookRandomAPI,
  getBookStateAPI,
} from "@/api/book.ts";
import type {
  BookInfoView,
  BookRandomView,
  BookStateView,
} from "@/type/book.ts";
import dayjs from "dayjs";
import Comment from "./components/comment.vue";
import Catalogue from "./components/catalogue.vue";
import emitter from "@/utils/emitter.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { message } from "ant-design-vue";

const userStore = useUserStore();
const route = useRoute();
const bookId = route.params.id as string;
// 小说信息
const bookInfo = ref<BookInfoView>();
const bookState = ref<BookStateView>();

// 获取小说信息
const getBookInfo = async () => {
  const id = route.params.id as string;
  const res = await getBookInfoAPI(id);
  bookInfo.value = res.data;
};

// 获取小说状态
const getBookState = async () => {
  const res = await getBookStateAPI(bookId);
  bookState.value = res.data;
};

// 添加/移除书架
const toggleBookState = async () => {
  if (userStore.user == undefined) {
    message.warn("请先登录");
    emitter.emit("open-login");
    return;
  }
  await addOrDelBookShelfAPI(bookState.value!.isInBookShelf, bookId);
  bookState.value!.isInBookShelf = 1 - bookState.value!.isInBookShelf;
  message.success(
    bookState.value!.isInBookShelf === 1 ? "已加入书架" : "已移出书架"
  );
};

const randomBooks = ref<BookRandomView[]>();
// 获取同类推荐
const getBookRandom = async () => {
  const res = await getBookRandomAPI(bookInfo.value!.categoryId, bookId);
  randomBooks.value = res.data;
};

const loading = ref(true);
onMounted(async () => {
  await getBookInfo();
  Promise.all([getBookRandom(), getBookState()]);
  loading.value = false;
});
</script>

<template>
  <div>
    <!--	面包屑-->
    <a-breadcrumb>
      <a-breadcrumb-item>
        <router-link to="/"> 首页 </router-link>
      </a-breadcrumb-item>
      <a-breadcrumb-item>
        <router-link
          :to="{
            name: 'all',
            query: {
              c: bookInfo?.categoryId,
            },
          }"
        >
          {{ bookInfo?.categoryName }}
        </router-link>
      </a-breadcrumb-item>
      <a-breadcrumb-item>
        <router-link class="text-black!" :to="`/info/${bookInfo?.id}`">
          {{ bookInfo?.bookName }}
        </router-link>
      </a-breadcrumb-item>
    </a-breadcrumb>

    <!--	骨架屏-->
    <div
      v-if="loading"
      class="h-200 mt-5 bg-white flex items-center justify-center p-5 mx-auto"
    >
      <a-spin tip="加载中..." size="large" />
    </div>
    <div v-else>
      <div>
        <div class="flex justify-between">
          <div class="flex p-5 mt-5 flex-1 rounded-lg bg-white">
            <!--		封面-->
            <div class="w-50 h-266px mr-8 img-shadow">
              <img
                class="size-full"
                :src="bookInfo?.picUrl"
                onerror="this.src = '/public/image/default.png'"
                alt=""
              />
            </div>
            <!--		介绍-->
            <div class="flex-col flex justify-between">
              <div>
                <div class="mb-5">
                  <span class="font-bold mr-4 text-xl">{{
                    bookInfo?.bookName
                  }}</span>
                </div>
                <div class="mb-2">作者: {{ bookInfo?.authorName }}</div>
                <div class="text-gray-500 mb-2">
                  分类: {{ bookInfo?.categoryName }}
                </div>
                <div class="text-gray-500 text-xs">
                  <p>最新章节: {{ bookInfo?.lastChapterName }}</p>
                  <p>
                    更新时间:
                    {{
                      dayjs(bookInfo?.lastChapterUpdateTime).format(
                        "YYYY-MM-DD · HH:mm:ss"
                      )
                    }}
                  </p>
                </div>
              </div>
              <div>
                <div class="text-sm text-gray-500">
                  <span
                    >总字数:
                    <i class="text-lg text-red-500 mr-4 font-bold"
                      >{{
                        ((bookInfo?.wordCount || 0) / 10000).toFixed(2)
                      }}万</i
                    ></span
                  >
                  <span
                    >总点击:
                    <i class="text-lg text-red-500 mr-4 font-bold">{{
                      bookInfo?.visitCount
                    }}</i></span
                  >
                  <span
                    >总评论:
                    <i class="text-lg text-red-500 mr-4 font-bold">{{
                      bookInfo?.commentCount
                    }}</i></span
                  >
                </div>
                <div class="mt-4 h-10">
                  <a-button
                    @click="emitter.emit('read')"
                    type="primary"
                    class="mr-8"
                    >点击阅读</a-button
                  >
                  <a-button @click="toggleBookState">{{
                    bookState?.isInBookShelf === 1 ? "已在书架" : "加入书架"
                  }}</a-button>
                </div>
              </div>
            </div>
          </div>
          <!--						作者信息-->
          <div
            class="px-5 h-77 mt-5 rounded-lg flex-col justify-center flex w-60 bg-white"
          >
            <a
              class="block text-center w-full cursor-default pb-2"
              href="javascript:void(0)"
            >
              <a-avatar :size="64" src="/public/image/author_head.png" />
            </a>
            <p class="text-center font-bold border-b pb-2">
              {{ bookInfo?.authorName }}
            </p>
            <div class="text-xs mt-2">
              <p class="mb-2">作者留言</p>
              <p>
                亲亲们，你们的支持是我最大的动力！求点击、求推荐、求书评哦！
              </p>
            </div>
          </div>
        </div>
        <!--				简介-->
        <div class="p-5 mt-5 rounded-lg bg-white">
          <p class="text-xl font-bold mb-4">作品简介</p>
          <p
            id="Introduction"
            v-html="bookInfo?.bookDesc"
            class="text-sm text-gray-700 max-h-50 overflow-y-auto"
          ></p>
        </div>
        <div class="flex gap-4 justify-between">
          <div class="w-2/3">
            <!--				目录-->
            <Catalogue :book-id="bookId" />
            <!--				书评区-->
            <Comment :book-id="bookId" />
          </div>
          <!--				同类小说推荐-->
          <div class="py-5 mt-5 flex-1 rounded-lg bg-white">
            <p class="pl-5 text-lg font-bold">同类推荐</p>
            <div>
              <router-link
                :to="`/info/${item.id}`"
                v-for="item in randomBooks"
                :key="item.id"
                class="mt-2 py-2 px-2 mx-2 flex rounded-lg transition hover:bg-neutral-100"
              >
                <img
                  class="w-16 mr-2"
                  :src="item.picUrl"
                  onerror="this.src = '/image/default.png'"
                  alt=""
                />
                <div class="">
                  <p class="text-sm font-bold">{{ item.bookName }}</p>
                  <p
                    v-html="item.bookDesc"
                    class="h-15 line-height-[180%] text-ellipsis overflow-hidden text-xs"
                  ></p>
                </div>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#Introduction {
  &::-webkit-scrollbar {
    width: 5px;
    background-color: transparent;
  }

  &::-webkit-scrollbar-thumb {
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
    background-color: rgba(205, 205, 205, 0.63);
    border-radius: 10px;
  }
}

.img-shadow {
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.3);
}

/* 文本超出隐藏 */
.text-hidden {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
</style>
