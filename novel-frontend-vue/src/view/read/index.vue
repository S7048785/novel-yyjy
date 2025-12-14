<script setup lang="ts">
import Icon from "@ant-design/icons-vue";
import { addOrDelBookShelfAPI, getBookContentAPI } from "@/api/book.ts";
import type {
  BookChapterView,
  BookContentView,
  ChapterSummary,
} from "@/type/book.ts";
import dayjs from "dayjs";
import { getBookChapterAPI, getCommentSummaryList } from "@/api/comment.ts";
import emitter from "@/utils/emitter.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { message } from "ant-design-vue";
import ChapterComment from "./components/ChapterComment.vue";

const userStore = useUserStore();
const route = useRoute();
const router = useRouter();

const params = route.params as { bookId: string; chapterId: string };
// 夜间模式
const isDark = ref(document.documentElement.classList.value === "dark");
const catalogueModal = ref();

const isInBookShelf = ref(false);
// 侧边栏
const sidebarList = [
  {
    name: "目录",
    icon: "icon-menu",
    id: 0,
    d: "M177 336c35.346 0 64-28.654 64-64 0-35.346-28.654-64-64-64-35.346 0-64 28.654-64 64 0 35.346 28.654 64 64 64z m0 240c35.346 0 64-28.654 64-64 0-35.346-28.654-64-64-64-35.346 0-64 28.654-64 64 0 35.346 28.654 64 64 64z m0 240c35.346 0 64-28.654 64-64 0-35.346-28.654-64-64-64-35.346 0-64 28.654-64 64 0 35.346 28.654 64 64 64z m168-588h552c8.837 0 16 7.163 16 16v56c0 8.837-7.163 16-16 16H345c-8.837 0-16-7.163-16-16v-56c0-8.837 7.163-16 16-16z m0 240h552c8.837 0 16 7.163 16 16v56c0 8.837-7.163 16-16 16H345c-8.837 0-16-7.163-16-16v-56c0-8.837 7.163-16 16-16z m0 240h552c8.837 0 16 7.163 16 16v56c0 8.837-7.163 16-16 16H345c-8.837 0-16-7.163-16-16v-56c0-8.837 7.163-16 16-16z",
    onClick: () => {
      catalogueVisible.value = !catalogueVisible.value;
      getBookChapter();
    },
  },
  {
    name: "详情",
    icon: "icon-info",
    id: 1,
    d: "M800 64l-192 0c-52.48 0-98.56 25.6-128 65.28C450.56 89.6 404.48 64 352 64l-192 0C106.88 64 64 106.88 64 160l0 512C64 725.12 106.88 768 160 768l192 0C405.12 768 448 810.88 448 864 448 881.92 462.08 896 480 896 497.92 896 512 881.92 512 864 512 810.88 554.88 768 608 768l192 0c53.12 0 96-42.88 96-96l0-512C896 106.88 853.12 64 800 64zM448 736C421.12 716.16 387.84 704 352 704l-192 0C142.08 704 128 689.92 128 672l0-512C128 142.08 142.08 128 160 128l192 0C405.12 128 448 170.88 448 224L448 736zM832 672c0 17.92-14.08 32-32 32l-192 0c-35.84 0-69.12 12.16-96 32l0-512C512 170.88 554.88 128 608 128l192 0C817.92 128 832 142.08 832 160L832 672z",
    onClick: () => {
      router.push(`/info/${params.bookId}`);
    },
  },
  {
    name: isInBookShelf.value ? "已在书架" : "加入书架",
    icon: "icon-book",
    id: 2,
    d: "M778.624 213.312a53.312 53.312 0 0 0-53.312-53.312H298.624a53.312 53.312 0 0 0-53.312 53.312v620.416L493.44 656.64a32 32 0 0 1 37.184 0l248.064 177.088V213.312z m64 682.688a32 32 0 0 1-50.56 26.048L512 721.92l-280 200.128a32 32 0 0 1-50.56-26.048V213.312A117.312 117.312 0 0 1 298.56 96h426.688a117.312 117.312 0 0 1 117.312 117.312V896z",
    onClick: async () => {
      await addOrDelBookShelfAPI(isInBookShelf.value ? 1 : 0, params.bookId);
      isInBookShelf.value = !isInBookShelf.value;
      sidebarList[2].name = isInBookShelf.value ? "已在书架" : "加入书架";
    },
  },
  {
    name: "夜间",
    icon: "icon-book",
    id: 3,
    d: "M614.72 256.384q-10.88-10.88-45.44-22.08l-0.192-0.064c-2.752-0.896-2.688-4.928 0.128-5.76h0.064q32-9.088 45.44-22.528c8.832-8.832 16.256-23.68 22.272-44.544l0.192-0.64c0.768-2.688 4.672-2.752 5.44 0 6.08 21.248 13.568 36.288 22.464 45.184q13.312 13.312 44.8 22.4l1.152 0.32c2.56 0.64 2.56 4.288 0 4.992q-32.384 9.152-45.952 22.72c-8 8-15.488 23.296-22.4 45.824l-0.448 1.472c-0.64 2.368-3.968 2.304-4.672 0-7.04-23.36-14.592-39.104-22.848-47.36zM437.952 161.92A355.904 355.904 0 0 0 515.84 865.024a355.904 355.904 0 0 0 347.52-278.848 31.04 31.04 0 0 0-33.536-37.568l-4.864 0.896a293.696 293.696 0 0 1-349.76-350.592 31.04 31.04 0 0 0-37.12-36.992zM407.04 236.288l-0.64 10.688a355.84 355.84 0 0 0 355.456 371.392l15.68-0.32 11.392-0.768-1.92 4.928a293.696 293.696 0 1 1-384.768-384l4.8-1.92z m323.072 202.624q-14.72-14.72-61.824-30.08h-0.192c-3.776-1.28-3.712-6.656 0.128-7.808h0.064c29.056-8.32 49.664-18.496 61.824-30.72 12.032-11.968 22.08-32.192 30.336-60.544l0.256-0.896c1.024-3.712 6.336-3.712 7.36-0.064v0.064c8.32 28.864 18.496 49.344 30.592 61.44q18.112 18.112 60.928 30.464l1.472 0.448c3.456 0.896 3.52 5.824 0.128 6.784h-0.128c-29.312 8.32-50.112 18.624-62.4 30.912q-16.448 16.448-30.528 62.336l-0.64 2.112c-0.896 3.136-5.312 3.072-6.272 0v-0.128c-9.6-31.68-19.904-53.12-31.104-64.32z",
    onClick: () => {
      isDark.value = !isDark.value;
      document.documentElement.classList.toggle("dark", isDark.value);
      catalogueModal.value.querySelector(
        ".ant-modal-content"
      ).style.background = isDark.value ? "#262626" : "#ffffff";
    },
  },
];

const logout = () => {
  userStore.logout();
  message.success("退出成功");
  router.push("/");
};

//  内容
const bookContentView = ref<BookContentView>();

// 章节列表
const bookChapter = reactive<BookChapterView>({
  bookChapters: [],
  total: 0,
});

const bookContentRef = ref<HTMLDivElement>();
const getBookContent = async () => {
  bookContentView.value = (
    await getBookContentAPI(params.bookId, params.chapterId)
  ).data;
  isInBookShelf.value = bookContentView.value.isInBookShelf === 0;
  sidebarList[2].name = isInBookShelf.value ? "已在书架" : "加入书架";
};

// 获取小说章节
const getBookChapter = async (desc: boolean = false) => {
  if (bookChapter.total > 0) {
    return;
  }
  const id = route.params.bookId as string;
  const res = await getBookChapterAPI({ id, isAll: 1, orderBy: desc ? 1 : 0 });
  bookChapter.bookChapters = res.data.bookChapters;
  bookChapter.total = res.data.total;
  chapterOrderDesc.value = desc;
};

// 章节排序
const chapterOrderDesc = ref(false);
const changeOrder = () => {
  bookChapter.bookChapters.sort(() => -1);
  chapterOrderDesc.value = !chapterOrderDesc.value;
};
//  目录
const catalogueVisible = ref(false);
const loading = ref(true);

const commentOpen = ref<boolean>(false);

// 段落评论
const summaryList = ref<ChapterSummary[]>([]);
// 当前段落
const segmentNum = ref<number | null>(null);

// 段落点击事件
const onSegmentClick = (e: MouseEvent) => {
  // 获取当前段落索引
  const index = Number((e.target as HTMLElement)?.getAttribute("data-index"));
  if (segmentNum.value) {
    (
      bookContentRef.value?.childNodes[segmentNum.value - 1] as HTMLElement
    )?.classList.remove("bg-gray-200");
  }

  // 判断是否是点击同一段落
  if (commentOpen.value && segmentNum.value == index) {
    commentOpen.value = false;
  } else {
    // 非同一段落，获取评论
    commentOpen.value = true;

    (e.target as HTMLElement).classList.add("bg-gray-200");
  }
  segmentNum.value = index;
};
// 获取章节段落列表
const getSummary = async () => {
  summaryList.value = (
    await getCommentSummaryList(params.bookId, params.chapterId)
  ).data;
  let sum = 0;
  bookContentRef.value?.childNodes.forEach((item: ChildNode, index: number) => {
    if (sum < summaryList.value.length) {
      const summary = summaryList.value[sum];
      console.log(index + 1, summary.segmentNum);
      if (index + 1 === summary.segmentNum) {
        const el = document.createElement("span");
        el.style = "position: relative; margin-left: 4px;top: 4px;";
        el.innerHTML = `<span class="comment-icon"></span><span class="absolute top-0 mx-auto text-xs ml-3px text-center w-20px">${summary.commentNum}</span>`;
        item.appendChild(el);
        sum++;
      }
    }
    (item as HTMLElement).setAttribute("data-index", String(index + 1)); // 设置属性值
    // 对段落添加点击事件，点击时打开评论区
    item.addEventListener("click", onSegmentClick as EventListener);
  });
};

onMounted(async () => {
  await getBookContent();
  loading.value = false;

  catalogueModal.value.querySelector(".ant-modal-content").style.background =
    isDark.value ? "#262626" : "#ffffff";

  // 等待小说内容DOM 更新完成
  nextTick(() => {
    console.log(bookContentRef.value);
    getSummary();
  });
});
</script>

<template>
  <div class="bg-neutral-200 dark:bg-[#202020FF] overflow-hidden">
    <div class="bg-neutral-200 dark:bg-[#202020FF] flex justify-center z-10">
      <!-- 侧边栏 -->
      <div class="relative -left-20" :class="{ '-left-50': commentOpen }">
        <div class="flex fixed flex-col z-20 ml-[-20px] h-screen top-40">
          <span
            v-for="item in sidebarList"
            @click="item.onClick"
            class="inline-flex flex-col items-center py2 px-2 cursor-pointer rounded-lg text-sm text-center mb4 bg-gray-100 hover:bg-white dark:text-neutral dark:hover:bg-neutral-700 dark:bg-neutral-800"
          >
            <Icon>
              <template #component>
                <svg
                  width="2rem"
                  height="2em"
                  viewBox="0 0 1024 1024"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    :d="item.d"
                    :fill="`${isDark ? '#A3A3A3' : '#263140'}`"
                  ></path>
                </svg>
              </template>
            </Icon>
            {{ item.name }}
          </span>
        </div>
      </div>
      <div :class="{ '-translate-x-30': commentOpen }" class="z-10">
        <div
          v-if="loading"
          class="h-228 w240 bg-white dark:bg-neutral-800 flex items-center justify-center p-5 mx-auto"
        >
          <a-spin tip="加载中..." size="large" />
        </div>
        <div
          v-else
          class="w-200 relative h-full pt-4 bg-neutral-100 dark:bg-neutral-800"
        >
          <div class="flex justify-between border-b dark:border-gray-7">
            <div class="pb4 pl6">
              <router-link
                class="font-bold text-gray-600 hover:text-gray-800 dark:text-gray-500 dark:hover:text-gray text-2xl"
                to="/"
              >
                小说精品屋
              </router-link>
            </div>
            <div class="pr6">
              <a-dropdown v-if="userStore.user">
                <div
                  class="p-1 rounded-lg hover:bg-neutral-100 dark:hover:bg-neutral-800"
                >
                  <a-avatar
                    size="large"
                    :src="userStore.user?.userPhoto"
                  ></a-avatar>
                  <span class="ml-4 dark:text-gray">{{
                    userStore.user?.nickName
                  }}</span>
                </div>

                <template #overlay>
                  <a-menu class="dark:bg-neutral-700">
                    <a-menu-item class="">
                      <div class="text-center">
                        <router-link
                          to="/user/setting"
                          class="dark:text-gray-400!"
                          >个人信息</router-link
                        >
                      </div>
                    </a-menu-item>
                    <a-menu-item class="">
                      <div class="text-center">
                        <router-link
                          to="/user/bookcase"
                          class="dark:text-gray-400!"
                          >我的书架</router-link
                        >
                      </div>
                    </a-menu-item>
                    <a-menu-item class="">
                      <div class="text-center">
                        <a
                          @click="logout"
                          href="javascript:;"
                          class="dark:text-gray-400! hover:text-red-5!"
                          >退出登录</a
                        >
                      </div>
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
              <a-button
                v-else
                @click="emitter.emit('open-login')"
                class="text-gray p-0 hover:text-blue!"
                type="text"
              >
                登录
              </a-button>
            </div>
          </div>
          <!--			面包屑-->
          <div class="py6 border-b dark:border-gray-7 pl6">
            <a-breadcrumb>
              <template #separator
                ><span class="dark:text-neutral-500"> -> </span></template
              >
              <a-breadcrumb-item>
                <router-link
                  to="/"
                  class="dark:text-neutral-500 dark:hover:text-neutral-400"
                >
                  <span class="text-neutral-500 dark:text-neutral-500"
                    >首页</span
                  >
                </router-link>
              </a-breadcrumb-item>
              <a-breadcrumb-item>
                <router-link
                  :to="{
                    name: 'all',
                    query: {
                      c: bookContentView?.categoryId,
                    },
                  }"
                  class="dark:text-neutral-500 dark:hover:text-neutral-400"
                >
                  <span class="text-neutral-500 dark:text-neutral-500">{{
                    bookContentView?.categoryName
                  }}</span>
                </router-link>
              </a-breadcrumb-item>
              <a-breadcrumb-item>
                <router-link :to="`/info/${bookContentView?.bookId}`">
                  <span
                    class="text-neutral-700 dark:text-neutral-400 dark:hover:text-neutral-400"
                    >{{ bookContentView?.bookName }}</span
                  >
                </router-link>
              </a-breadcrumb-item>
            </a-breadcrumb>
          </div>
          <!-- 正文 -->
          <div class="pb6 mt6">
            <!--				头部-->
            <div class="text-center">
              <p class="text-2xl mb4 dark:text-neutral-400">
                {{ bookContentView?.chapterName }}
              </p>
              <p
                class="text-sm inline-flex gap-6 text-gray-500 dark:text-neutral-400"
              >
                <span class="inline-flex items-center">
                  <Icon class="mr-1">
                    <template #component>
                      <svg
                        t="1752059332317"
                        class="size-4"
                        viewBox="0 0 1024 1024"
                        version="1.1"
                        xmlns="http://www.w3.org/2000/svg"
                        p-id="6958"
                      >
                        <path
                          d="M895.887 166.804c-15.842 0-28.655-12.813-28.655-28.667 0-15.854 12.813-28.667 28.655-28.667 15.854 0 28.667-12.814 28.667-28.667s-12.813-28.667-28.667-28.667H207.953c-47.471 0-85.991 38.485-85.991 86.001v773.925c0 31.662 25.639 57.334 57.323 57.334h687.946c31.685 0 57.322-25.672 57.322-57.334V195.471c0.001-15.854-12.813-28.667-28.666-28.667z m0.619 57.334v706.435H153.309V199.614c4.2 5.765 28.825 24.523 54.644 24.523h109.174v-28.825H207.953c-15.786 0-54.644-11.665-54.644-57.177 0-47.088 38.857-58.123 55.33-58.123h691.92c-12.025 7.026-53.776 11.395-53.776 58.123 0 34.703 42.179 50.601 49.723 57.177H499.68v28.825h396.826z"
                          :fill="`${isDark ? '#A3A3A3' : '#263140'}`"
                          p-id="6959"
                        ></path>
                        <path
                          d="M293.943 52.135v458.63l114.658-114.646 114.657 114.646V52.135H293.943z m203.339 395.621l-88.681-87.916-87.983 87.916V78.798h176.665v368.958z"
                          :fill="`${isDark ? '#A3A3A3' : '#263140'}`"
                          p-id="6960"
                        ></path>
                      </svg>
                    </template>
                  </Icon>
                  {{ bookContentView?.bookName }}
                </span>
                <span class="inline-flex items-center">
                  <Icon class="mr-1">
                    <template #component>
                      <svg
                        t="1752058993733"
                        class="size-4"
                        viewBox="0 0 1024 1024"
                        version="1.1"
                        xmlns="http://www.w3.org/2000/svg"
                        p-id="5493"
                      >
                        <path
                          :fill="`${isDark ? '#A3A3A3' : '#263140'}`"
                          d="M896 864a32 32 0 0 1 0 64H128a32 32 0 0 1 0-64z m-60.16-733.621333l15.093333 15.093333c45.866667 45.834667 45.866667 120.16 0 166.005333L418.794667 743.338667a160.192 160.192 0 0 1-78.122667 42.986666l-152.245333 34.197334c-23.84 5.365333-44.661333-16.853333-37.749334-40.288l43.338667-146.88a160.042667 160.042667 0 0 1 40.373333-67.925334l435.328-435.050666c45.866667-45.834667 120.245333-45.834667 166.112 0zM636.32 254.304L279.68 610.709333a96.021333 96.021333 0 0 0-24.213333 40.746667l-27.946667 94.656 99.093333-22.261333a96.117333 96.117333 0 0 0 46.869334-25.781334L726.933333 344.842667l-90.602666-90.538667z m78.698667-78.656l-33.397334 33.386667 90.602667 90.538666 33.386667-33.376a53.333333 53.333333 0 0 0 0-75.456l-15.093334-15.093333a53.408 53.408 0 0 0-75.498666 0z"
                          p-id="5494"
                        ></path>
                      </svg>
                    </template>
                  </Icon>
                  {{ bookContentView?.authorName }}
                </span>
                <span class="inline-flex items-center">
                  <Icon class="mr-1">
                    <template #component>
                      <svg
                        t="1752059552103"
                        class="size-4"
                        viewBox="0 0 1024 1024"
                        version="1.1"
                        xmlns="http://www.w3.org/2000/svg"
                        p-id="14893"
                      >
                        <path
                          :fill="`${isDark ? '#A3A3A3' : '#263140'}`"
                          d="M512 101.376a410.624 410.624 0 1 1 0 821.248 410.624 410.624 0 0 1 0-821.248z m0 74.666667a336.042667 336.042667 0 1 0 0 672 336.042667 336.042667 0 0 0 0-672.085334z m149.333333 149.333333a37.290667 37.290667 0 0 1 6.741334 73.984l-6.741334 0.682667H549.290667v298.666666a37.290667 37.290667 0 0 1-73.984 6.656l-0.682667-6.741333v-298.666667H362.666667a37.290667 37.290667 0 0 1-6.741334-73.984l6.741334-0.597333h298.666666z"
                          p-id="14894"
                        ></path>
                      </svg>
                    </template>
                  </Icon>
                  {{ bookContentView?.wordCount }}
                  字</span
                >
                <span class="inline-flex items-center">
                  <Icon class="mr-1">
                    <template #component>
                      <svg
                        t="1752059800853"
                        class="size-4"
                        viewBox="0 0 1024 1024"
                        version="1.1"
                        xmlns="http://www.w3.org/2000/svg"
                        p-id="3525"
                      >
                        <path
                          d="M512 0C229.225814 0 0 229.248624 0 512s229.225814 512 512 512 512-229.225814 512-512S794.774186 0 512 0z m0 937.524225v0.980865c-235.555813 0-426.493685-190.937872-426.493685-426.50509s190.926466-426.562117 426.493685-426.562117 426.493685 190.994899 426.493685 426.562117-215.037491 425.51282-426.493685 425.51282z m213.281059-425.524225h-191.952953V255.994297c0-17.906485-18.453944-31.935132-36.497294-31.935131-26.049943 0-48.826535 14.051458-48.826535 31.935131v298.67332c0 17.906485 24.624268 42.667617 42.667617 42.667617h234.609165c18.043349 0 26.722862-20.005079 26.722862-37.922969 0.057027-17.929295-8.622486-47.412265-26.722862-47.412265z"
                          :fill="`${isDark ? '#A3A3A3' : '#263140'}`"
                          p-id="3526"
                        ></path>
                      </svg>
                    </template>
                  </Icon>
                  {{
                    dayjs(bookContentView?.createTime).format(
                      "YYYY年MM月DD日 HH:mm"
                    )
                  }}
                </span>
              </p>
            </div>
            <!--				内容-->
            <div class="mt-10 w-10/13 mx-auto text-lg dark:text-neutral-400">
              <div
                v-html="bookContentView?.content"
                ref="bookContentRef"
                class="flex flex-col gap-8 content"
              ></div>
            </div>
            <!--				底部-->
            <div class="px6 pt-10 mt4 flex gap-10 items-center justify-center">
              <router-link
                v-if="bookContentView?.prevChapterId"
                :to="`/read/${params.bookId}/${bookContentView?.prevChapterId}`"
                type="text"
                class="rounded-full bg-white px30 py4 hover:bg-neutral-200 transition-duration-300 content-center"
              >
                上一章
              </router-link>
              <router-link
                v-if="bookContentView?.nextChapterId"
                :to="`/read/${params.bookId}/${bookContentView?.nextChapterId}`"
                type="text"
                class="rounded-full bg-white hover:bg-neutral-200 dark:bg-neutral-600 dark:hover:bg-neutral-700 dark:text-white transition-duration-300 px30 py4 content-center"
              >
                下一章
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <div
        :class="{ '-translate-x-30': !commentOpen }"
        style="left: calc(0.496354 * 100vw + 278.4px)"
        class="fixed top-0 overflow-hidden z-1"
      >
        <transition name="slide-right">
          <div
            v-if="commentOpen"
            class="w-100 h-screen bg-neutral-100 dark:bg-neutral-800 dark:border-gray-7 border-l"
          >
            <ChapterComment
								class="w-90%"
              :segmentNum="segmentNum!"
              :commentOpen="commentOpen"
              @close="
                () => {
                  commentOpen = false;
                  (bookContentRef?.childNodes[segmentNum! - 1] as HTMLElement)?.classList.remove(
                    'bg-gray-200'
                  );
                  segmentNum = null;
                }
              "
            />
          </div>
        </transition>
      </div>
    </div>

    <!--		目录模态框-->
    <div ref="catalogueModal">
      <a-modal
        v-model:open="catalogueVisible"
        :get-container="() => $refs.catalogueModal as HTMLElement"
        closable
        :forceRender="true"
        style="top: 20px"
        :width="880"
        :footer="false"
        :mask="false"
      >
        <template #title>
          <div class="dark:bg-neutral-800 flex justify-between">
            <div>
              <span class="text-2xl mr-4 dark:text-gray-500">目录</span>
              <span class="text-gray dark:text-neutral-500">
                连载中 · 共 {{ bookChapter.total }} 章</span
              >
            </div>
            <div class="inline-flex mr-10">
              <a-button
                @click="changeOrder"
                type="text"
                class="inline-flex items-center px-1 dark:hover:bg-neutral-700"
              >
                <Icon>
                  <template #component>
                    <svg
                      t="1752056997879"
                      class="size-4 p0"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="3699"
                    >
                      <path
                        :fill="`${isDark ? '#A3A3A3' : '#263140'}`"
                        d="M420.559974 72.98601l-54.855 0 0 774.336c-52.455014-69.163008-121.619046-123.762995-201.120051-157.052006l0 61.968c85.838029 41.401958 156.537958 111.337984 201.120051 198.221005l0 0.208 54.855 0 0-13.047c0.005018-0.00297 0.010035-0.005018 0.01495-0.007987-0.005018-0.010035-0.010035-0.019968-0.01495-0.030003L420.559974 72.986zM658.264986 73.385984l0-0.4L603.41 72.985984l0 877.68 54.855 0L658.265 176.524c52.457984 69.178982 121.632051 123.790029 201.149952 157.078016l0-61.961C773.560013 230.238003 702.853018 160.287027 658.264986 73.385984z"
                        p-id="3700"
                      ></path>
                    </svg>
                  </template>
                </Icon>
                <span class="dark:text-neutral">{{
                  chapterOrderDesc ? "正序" : "倒序"
                }}</span>
              </a-button>
            </div>
          </div>
        </template>
        <template #closeIcon>
          <span>✖</span>
        </template>
        <div
          id="catalogue"
          class="mt-4 flex px-6 flex-wrap h-150 overflow-y-scroll"
        >
          <div
            v-for="item in bookChapter.bookChapters"
            class="mr-2 w-48% mx-auto relative after:border-b dark:after:border-neutral-600 after:content-[''] after:absolute after:right-1 after:h-1px after:bottom-0 after:w-98%"
          >
            <router-link
              :to="`/read/${params.bookId}/${item.id}`"
              class="block py4 px2 rounded-lg hover:bg-gray-200 hover:text-black dark:text-neutral-400 dark:hover:text-neutral-400 dark:hover:bg-neutral-700"
            >
              {{ item.chapterName }}
            </router-link>
          </div>
        </div>
      </a-modal>
    </div>
  </div>
</template>

<style scoped>
div {
  transition: all 0.2s ease-in-out;
}

#catalogue {
  &::-webkit-scrollbar {
    width: 6px;
    background-color: transparent;
    height: 1px;
  }

  &::-webkit-scrollbar-thumb {
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
    border-radius: 10px;
    height: 20px;
  }
}

#catalogue::-webkit-scrollbar-thumb {
  background-color: #ccc !important; /* 默认亮色模式 */
}

.dark #catalogue::-webkit-scrollbar-thumb {
  background-color: #5a5a5a !important; /* 暗色模式 */
}

:deep(.ant-drawer-right > .ant-drawer-content-wrapper) {
  box-shadow: none;
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.3s ease;
}

.slide-right-enter-from,
.slide-right-leave-to {
  transform: translateX(-100%);
}

.slide-right-enter-to,
.slide-right-leave-from {
  transform: translateX(0);
}

:deep(.comment-icon) {
  display: inline-block;
  -webkit-mask-image: url("/image/comment.svg");
  -webkit-mask-position: center center;
  -webkit-mask-repeat: no-repeat;
  -webkit-mask-size: 96%;
  background: #00000052;
  width: 24px;
  height: 16px;
  position: absolute;
  left: 0;
  top: 0;
}

:deep(.content) {
  p {
    cursor: pointer;
		text-indent: 1.5em;
		/* 行距 */
    line-height: 2;
		span {
			text-indent:0;

		}
    /*background-color: #cccccc99;*/
  }
}
</style>
