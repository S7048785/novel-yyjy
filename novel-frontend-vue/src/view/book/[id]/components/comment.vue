<script setup lang="ts">

/**
 * 评论区
 */
import {addBookCommentAPI, deleteBookCommentAPI, getBookCommentAPI, getBookSubCommentAPI} from "@/api/comment.ts";
import type {BookCommentView} from "@/type/book.ts";
import dayjs from "dayjs";
import SubComment from "@/view/book/[id]/components/sub-comment.vue";
import subComment from "@/view/book/[id]/components/sub-comment.vue";
import {message} from "ant-design-vue";
import {useUserStore} from "@/stores/userStore.ts";
import emitter from "@/utils/emitter.ts";
import type {PageData} from "@/utils/request.ts";

const userStore = useUserStore()
const {bookId} = defineProps<{ bookId: string }>()
// 评论内容表单
const bookCommentValue = ref<string>('')

const bookComment: BookCommentView[] = reactive([])
const commentCount = ref(0)

// 删除评论 by评论ID
const deleteComment = async (id: string) => {
	const res = await deleteBookCommentAPI(id);

	res.code === 1 ? message.success('删除成功') : message.error(res.msg);
	// 刷新评论区
	const res1 = await getBookCommentAPI(bookId, pageNo, pageSize)
	bookComment.splice(0, bookComment.length,...res1.data.list)
	commentCount.value = res1.data.total
	hasMore.value = false;
}

/**
 * 删除子评论
 * @param id 评论id
 * @param index 根评论索引
 * @param rootParentId 根评论ID
 */
const deleteSubComment = async (id: string, index: number, rootParentId: string) => {
	const res = await deleteBookCommentAPI(id);
	res.code === 1 ? message.success('删除成功') : message.error(res.msg);
	getSubComment(index,rootParentId);
	bookComment[index].childrenCount--;
}

const sendComment = async (rootParentId?: string, parentId?: string) => {
	if (userStore.user == null) {
		message.warn('请先登录')
		emitter.emit('open-login')
		return;
	}

	if (bookCommentValue.value.trim() === '') {
		message.warn('请输入内容')
		return;
	}
	const res = await addBookCommentAPI({
		content: bookCommentValue.value,
		bookId: bookId,
		rootParentId,
		parentId,
	})
	message.success('发送成功')
	bookComment.unshift({
		id: res.data.id,
		content: res.data.content,
		childrenCount: 0,
		children: null,
		userId: userStore.user!.id,
		nickName: userStore.user!.nickName,
		avatar: userStore.user!.userPhoto,
		createTime: dayjs(res.data.createTime).format('YYYY-MM-DD HH:mm:ss')
	})
	commentCount.value++;
	bookCommentValue.value = '';
}

const sendSubComment = async (index: number) => {
	if (userStore.user == null) {
		message.warn('请先登录')
		emitter.emit('open-login')
		return;
	}
	if (reply.value.trim() === '') {
		message.warn('请输入内容')
		return;
	}
	await addBookCommentAPI({
		content: reply.value,
		bookId: bookId,
		replyNickName: reply.isShowReplyId === reply.parentId ? void 0 : reply.nickName,
		rootParentId: reply.isShowReplyId,
		parentId: reply.parentId,
	})
	message.success('发送成功')
	await getSubComment(index, bookComment[index].id)
	reply.value = '';
	reply.isShowReplyId = '';
}

let pageNo = 1;
let pageSize = 20;
const hasMore = ref(true);

const replyInput = ref()
// 回复
const reply = reactive({
	value: '',
	isShowReplyId: '',
	nickName: '',
	rootParentId: '',
	parentId: '',
})
const replyClick = (id: string, replayNickName2: string, parentId: string) => {
	if (id === reply.isShowReplyId && replayNickName2 === void 0) {
		reply.isShowReplyId = '';
	} else {
		reply.isShowReplyId = id;
		reply.nickName = replayNickName2;
		reply.parentId = parentId;
	}
	nextTick(() => {
		replyInput.value[0].focus()
	})
}

// 获取所有根评论数据
const getCommentData = async () => {
	if (!hasMore.value) return;
	const res = await getBookCommentAPI(bookId, pageNo, pageSize,)
	bookComment.push(...res.data.list)
	commentCount.value = res.data.total
	hasMore.value = false;
}

// 获取子评论数据
const getSubComment = async (index: number, rootId: string) => {
	const res = await getBookSubCommentAPI(bookId, rootId)
	bookComment[index].children = res.data
}

let observer: IntersectionObserver;
// 初始化观察器
const initIntersectionObserver = () => {
	observer = new IntersectionObserver(async (entries) => {
		if (entries[0].isIntersecting) {
			await getCommentData()
			loading.value = false
		}
	}, {threshold: 0.1}); // 当元素10%进入视口时触发

	if (target.value) observer.observe(target.value);
}
// 哨兵元素
const target = ref();

const loading = ref(true)
onMounted(() => {
	// getCommentData()
	initIntersectionObserver()
})
// 清理观察器
onUnmounted(() => {
	if (observer) observer.disconnect();
});
</script>

<template>
	<div class="p-5 mt-5 w-full rounded-lg bg-white">
		<!--		头部-->
		<div>
			<div class="mb-5">
				<div class="float-left">
					<span class="text-xl font-bold mr-4 mb-4">书评区</span>
					<span class="text-sm text-gray-500">共 {{ commentCount }} 条书评</span>
				</div>
				<div class="clear-both"></div>
			</div>
			<div>
				<a-textarea
						:autosize="{
									minRows: 4,
									maxRows: 6
								}"
						:maxlength="500"
						v-model:value="bookCommentValue"
						placeholder="快来吐槽这本书吧，注意文明用语噢(～￣▽￣)～"/>
				<div class="ml-auto w-fit mt-4">
					<span class="text-sm mr-4 text-gray">{{ bookCommentValue?.length }}/500</span>
					<a-button @click="() => sendComment()"
										class="px-10 h-10 border-none bg-sky hover:bg-sky-300 text-white hover:text-white!">发表
					</a-button>
				</div>
			</div>
		</div>
		<!--		评论区-->
		<div v-if="!loading" class="">
			<!--			评论列表-->
			<div
					v-slide-in
					v-for="(item, index) in bookComment" :key="item?.id"
					class="mb-4"
			>
				<img :src="item.avatar || `/public/image/default_avatar.jpg`" class="w-12 absolute rounded-full" alt="">
				<div class="pl-16 pt-2 ">
					<div class="border-neutral-300 border-b pb-4">
						<div class="text-sm text-neutral-600 mb-2">{{ item.nickName }}</div>
						<div class="">{{ item.content }}</div>
						<div class="text-13px text-neutral-500">
							<span class="mr-4">{{ dayjs(item.createTime).format("YYYY-MM-DD HH:mm") }}</span>
							<a-button @click="() => replyClick(item.id,item.nickName, item.id)" type="link" class="text-neutral p-0">
								回复
							</a-button>
							<a-popconfirm
									title="是否删除当前评论？"
									ok-text="确定"
									cancel-text="No"
									:showCancel="false"
									@confirm="deleteComment(item.id)"
							>
								<a-button v-if="item.userId === userStore.user?.id" type="link" class="text-red-500 hover:text-red! pl-2">删除</a-button>
							</a-popconfirm>
							<div v-if="item.childrenCount > 0 && item.children!.length < item.childrenCount" class="text-sm">
								共 {{ item.childrenCount }} 条回复
								<a-button @click="getSubComment(index, item.id)" type="link" class="p-0 m-0 h-auto text-neutral-500">
									点击查看
								</a-button>
							</div>
						</div>
						<!--						子评论-->
						<div>
							<sub-comment
									@replyClick="replyClick"
									:root-parent-id="item.id"
									:sub-comment="item.children"
									:userId="userStore.user?.id || ''"
									:index="index"
									@deleteSubComment="deleteSubComment"
							/>
						</div>
						<div v-if="reply.isShowReplyId === item.id" class="">
							<div class="flex mt-2">
								<a-avatar class="mr-4" :size="55" :src="userStore.user?.userPhoto"/>
								<a-textarea
										ref="replyInput"
										:autosize="{
											minRows: 1,
											maxRows: 4
										}"
										v-model:value="reply.value"
										class="pt-3 min-h-12!"
										:placeholder="`回复 @${reply.nickName}`"/>
							</div>
							<a-button @click="() => sendSubComment(index)"
												class="ml-auto w-24 h-10 bg-sky hover:bg-sky-300 border-0 hover:text-white! text-white block mt-4">
								发布
							</a-button>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div v-if="hasMore" ref="target" class="text-center">
			<a-spin tip="加载中..." size="large"/>
		</div>
	</div>
</template>

<style scoped>
.list-enter-active,
.list-leave-active {
	transition: all 1s ease;
}

.list-enter-from,
.list-leave-to {
	opacity: 0;
	transform: translateY(30px);
}
</style>