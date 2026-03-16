<script setup lang="ts">
import dayjs from "dayjs";
import { LikeFilled, LikeOutlined } from "@ant-design/icons-vue";
import { ref } from "vue";
import type { SegmentCommentView } from "@/type/book.ts";
import type { PageData } from "@/utils/request.ts";
import { useUserStore } from "@/stores/userStore";
import { addChapterSummary, listSegmentComment } from "@/api/comment";
import { message } from "ant-design-vue";

const { segmentNum } = defineProps<{
  segmentNum: number;
}>();

const emit = defineEmits<{
  getComment: [segmentNum: number];
  close: [void];
}>();

const params = useRoute().params as {
  chapterId: string;
  bookId: string;
};

// 当前段落评论区
const comment = reactive<PageData<SegmentCommentView>>({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  list: [],
  pages: 0,
});
let hasMore = true;
// 获取段落评论列表
const getComment = async (segmentNum: number) => {
  if (!hasMore) {
    return;
  }
  const res = await listSegmentComment(
    params.chapterId,
    segmentNum,
    comment.pageNum,
    comment.pageSize
  );
  comment.total = res.data.total;
  comment.pages = res.data.pages;
  comment.list = [...comment.list, ...res.data.list];
  comment.pageNum++;
  if (comment.list.length >= res.data.total) {
    hasMore = false;
  }
};
// 获取新段落评论
const getNewComment = async (segmentNum: number) => {
  const res = await listSegmentComment(
    params.chapterId,
    segmentNum,
    1,
    comment.pageSize
  );
  comment.total = res.data.total;
  comment.pages = res.data.pages;
  comment.list = res.data.list;
  if (comment.list.length >= res.data.total) {
    hasMore = false;
  } else {
    hasMore = true;
    comment.pageNum = 2;
  }
};

// 监听段落标记变化 ，重置评论列表
watch(() => segmentNum, getNewComment);

const userStore = useUserStore();

const likes = ref<number>(0);
const action = ref<string>();
const inputValue = ref<string>("");

const commentButtonIsShow = ref(false);
// 检查登录状态
const checkLogin = () => {
  // 校验登录状态
  if (!userStore.user) {
    message.warning("请先登录才能评论");
    return;
  }
  commentButtonIsShow.value = true;
  adjustTextareaHeight();
};
// 评论按钮禁用
const commentDisabled = ref(false);
const sendComment = async () => {
  commentDisabled.value = true;
  await new Promise((resolve) => setTimeout(resolve, 1000));
  if (inputValue.value.trim() === "") {
    message.warning("评论内容不可为空");
    commentDisabled.value = false;
    return;
  }
  const res = await addChapterSummary({
    bookId: params.bookId,
    chapterId: params.chapterId,
    segmentNum: segmentNum,
    content: inputValue.value,
  });
  if (res.code === 0) {
    message.error(res.msg || "评论失败", 1);
    commentDisabled.value = false;
    return;
  }
  comment.list.unshift(res.data);
  comment.total++;
  inputValue.value = "";
  commentDisabled.value = false;
};

const like = () => {
  // TODO: 实现点赞功能
};

const textareaRef = ref();
// 关闭输入框后调整高度
const onCloseTestArea = () => {
  commentButtonIsShow.value = false;
  textareaRef.value.style.height = "20px";
};
// 每次输入内容自适应输入框高度
function adjustTextareaHeight() {
  const textarea = textareaRef.value as HTMLTextAreaElement;
  textarea.style.height = "auto"; // 重置高度
  textarea.style.height = textarea.scrollHeight + "px"; // 设置高度为内容高度
}

onMounted(() => {
  getComment(segmentNum);

  // 监听内容变化
  textareaRef.value.addEventListener("input", adjustTextareaHeight);
});
</script>

<template>
  <div class="relative h-full flex flex-col">
    <!-- 头部 -->
    <div
      class="flex items-center justify-between px-5 pt-6 pb-4 border-b dark:border-gray-7"
    >
      <div class="dark:text-white">
        <span class="text-xl font-bold mr-2">评论</span>
        <span>{{ comment.total }} 条</span>
      </div>
      <CloseOutlined
        @click="emit('close')"
        class="bg-[#e0e0e0] rounded-full p-2 text-18px text-gray-500"
      />
    </div>
    <!--		评论内容-->
    <div class="px-4 overflow-y-scroll flex-1 pb-8" id="comment-list">
      <a-empty
        class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-gray"
        v-if="comment.list.length === 0"
        description="没有找到任何评论"
      />
      <a-comment v-for="item in comment.list">
        <div class="flex items-center gap-4 text-gray-400">
          <span
            key="comment-basic-like"
            @click="like"
            class="inline-flex items-center"
          >
            <a-tooltip title="Like">
              <template v-if="action === 'liked'">
                <LikeFilled />
              </template>
              <template v-else>
                <LikeOutlined />
              </template>
            </a-tooltip>
            <span style="padding-left: 4px; cursor: pointer">
              {{ item.likeCount }}
            </span>
          </span>
          <span class="text-xs">IP: {{ item.ipAddress }}</span>
          <span class="text-xs">{{ item.level }} 楼</span>
          <button
            v-if="item.userId === userStore.user?.id"
            @click=""
            class="ml-auto text-red dark:bg-neutral-800"
          >
            删除
          </button>
        </div>

        <template #author
          ><a class="text-14px dark:text-white">{{
            item.nickName
          }}</a></template
        >
        <template #avatar>
          <a-avatar
            :src="item.avatar || '/image/default_avatar.jpg'"
            :alt="item.nickName"
            class=""
          />
        </template>
        <template #content>
          <p class="text-14px dark:text-neutral-400">
            {{ item.content }}
          </p>
        </template>
        <template #datetime>
          <a-tooltip :title="dayjs().format('YYYY-MM-DD HH:mm:ss')">
            <span class="dark:text-neutral-400">{{
              dayjs(item.createTime).format("YYYY-MM-DD · HH:mm:ss")
            }}</span>
          </a-tooltip>
        </template>
      </a-comment>
    </div>
    <div
      class="w-full px-4 pt-4 pb-8 flex items-center justify-between gap-4 bg-[#f4f4f4] dark:bg-neutral-800 shadow-[0_-5px_40px_-10px_#cccccc] border-gray-7 dark:border-t dark:shadow-none"
    >
      <div>
        <a-avatar
          :src="userStore.user?.userPhoto || '/image/default_avatar.jpg'"
          :alt="userStore.user?.nickName || '用户'"
          class=""
        />
      </div>
      <div
        class="bg-white dark:bg-neutral-700 dark:text-white flex-1 rounded-lg px-4 py-2"
        :class="{ 'shadow-[0_0_5px_#ff660055]': commentButtonIsShow }"
      >
        <textarea
          ref="textareaRef"
          type="text"
          class="outline-none w-full placeholder:text-[#cccccc] h-5 overflow-hidden dark:bg-neutral-700 dark:text-white"
          :class="{ 'h-auto max-h-40 overflow-auto!': commentButtonIsShow }"
          placeholder="请输入评论内容"
          v-model="inputValue"
          maxLength="200"
          @focus="checkLogin"
        />
        <div
          v-if="commentButtonIsShow"
          class="ml-auto w-fit flex gap-2 items-center"
        >
          <span class="text-gray"> {{ inputValue.length }}/200 </span>
          <a-button @click="onCloseTestArea">取消</a-button>
          <a-button
            class="bg-[#ff6600] text-white!"
            @click="sendComment"
            :loading="commentDisabled"
            >评论</a-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="less">
:deep(.ant-comment .ant-comment-inner) {
  padding-bottom: 5px;
}
:deep(.ant-avatar) {
  width: 45px;
  height: 45px;
}
:deep(.ant-comment .ant-comment-avatar img) {
  width: auto;
  height: auto;
}
:deep(.ant-comment-nested) {
  margin-left: 58px;
}
textarea {
  resize: none; /* 禁止调整大小 */
}
:deep(.ant-btn-default:not(:disabled):hover) {
  color: #ff6600;
  border-color: #ff6600;
}

#comment-list {
  &::-webkit-scrollbar {
    width: 6px;
    background-color: transparent;
    height: 1px;
  }
  &::-webkit-scrollbar-thumb {
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0);
    border-radius: 10px;
    height: 20px;
  }
}
</style>
