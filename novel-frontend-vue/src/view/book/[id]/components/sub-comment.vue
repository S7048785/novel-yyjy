<script setup lang="ts">
import dayjs from "dayjs";
import type { BookSubCommentView} from "@/type/book.ts";

const {subComment, rootParentId, userId, index} = defineProps<{
	subComment: BookSubCommentView[] | null,
	rootParentId: string,
	userId: string,
	index: number
}>()

const emit = defineEmits<{
	replyClick: [id: string, replayNickName2: string, parentId: string]
	deleteSubComment: [id: string, index: number, rootParentId: string]
}>()

</script>

<template>
	<div
			v-for="item in subComment" :key="item?.id"
			class="relative mt-4"
	>
		<img :src="item.avatar || `/public/image/default_avatar.jpg`" class="w-12 absolute rounded-full" alt="">
		<div class="pl-16 pt-2">
			<div class="">
				<div class="text-sm text-neutral-600">
					<span>{{item.nickName}}</span>
					<span v-if="item.replyNickName" class="ml-4">回复 {{item.replyNickName}}: </span>
					<span class="ml-4 text-black">{{item.content}}</span>
				</div>
				<div class="text-13px text-neutral-500">
					<span class="mr-4">{{dayjs(item.createTime).format("YYYY-MM-DD HH:mm")}}</span>
					<a-button @click="() => emit('replyClick', rootParentId, item.nickName, item.id )" type="link" class="text-neutral p-0">回复</a-button>
					<a-popconfirm
							title="是否删除当前评论？"
							ok-text="确定"
							cancel-text="No"
							:showCancel="false"
							@confirm="emit('deleteSubComment', item.id, index, rootParentId)"
					>
						<a-button v-if="item.userId === userId" type="link" class="text-red-500 hover:text-red! pl-2">删除</a-button>
					</a-popconfirm>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>

</style>