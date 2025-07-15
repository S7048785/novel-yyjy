<script setup lang="ts">

import { useUserStore } from "@/stores/userStore.ts";
import {message} from "ant-design-vue";
import type {Data} from "@/utils/request.ts";
import type {UserInfoView} from "@/type/user.ts";
import {updateNickName} from "@/api/user.ts";
const userStore = useUserStore();

const fileList = ref([])

// 上传图片地址
const actionUrl = import.meta.env.VITE_APP_API_URL + "/user/upload"

const nickName = ref(userStore.user!.nickName)
const nameInput = ref()
// 修改昵称状态
const isNameChange = ref(false)

// 确认修改
const changeName = async () => {
	// 修改昵称
	const res = await updateNickName(nickName.value)
	if (res.code === 0) {
		message.error(res.msg);
		return;
	}
	userStore.setUserInfo({
		...userStore.user,
		nickName: nameInput.value.value
	} as UserInfoView);
	message.success('修改成功')
	isNameChange.value = false;
};

const enableNameChange = async () => {
	// 启用修改昵称
	isNameChange.value = true;
	await nextTick();
	nameInput.value.focus();
};

// 上传图像请求头
const headers = {
	authorization: localStorage.getItem("token") || '',
};

interface FileItem {
	uid: string;
	name?: string;
	status?: string;
	response?: string;
	url?: string;
	type?: string;
	size: number;
	originFileObj: any;
}

interface FileInfo {
	file: FileItem;
	fileList: FileItem[];
}

const uploadAvatar = (info: FileInfo) => {
	if (info.file.status === 'uploading') {
		// loading.value = true;
		return;
	}
	// 成功响应
	if (info.file.status === 'done') {
		// Get this url from response in real world.
		const result = (info.file.response) as Data<string> | undefined;
		if (result?.code === 1) {
			message.success('修改成功')
			const currentUser = userStore.user || {};
			userStore.setUserInfo({
				...currentUser,
				userPhoto: result.data
			} as UserInfoView);
		}
	}
	if (info.file.status === 'error') {
		// loading.value = false;
		message.error('upload error');
	}
};

</script>

<template>
	<div>
		<p class="border-b pb-2 mb-2">
			<span class="text-xl font-bold">个人信息</span>
		</p>
		<div class="mb-6">
			<span class="text-gray mr-4">头像</span>
			<a-avatar class="" :size="80" :src="userStore.user?.userPhoto"></a-avatar>
			<a-upload
					v-model:file-list="fileList"
					accept="image/*"
					name="file"
					:action="actionUrl"
					:headers="headers"
					@change="uploadAvatar"
					:showUploadList="false"
			>
				<a-button type="link">
					修改头像
				</a-button>
			</a-upload>
		</div>
		<div class="mb-6">
			<span class="text-gray mr-4">昵称</span>
			<input ref="nameInput" v-model="nickName" :disabled="!isNameChange" type="text" :class="{'rounded-md border-1 border-gray': isNameChange}" class="outline-none! focus:border-blue-200 focus:border-2 bg-transparent w-40 text-center"/>
			<a-button v-show="!isNameChange" @click="enableNameChange" type="link">修改昵称</a-button>
			<a-button v-show="isNameChange" @click="changeName" type="link">确定</a-button>
		</div>
		<div class="mb-6 flex items-center">
			<span class="text-gray mr-4">邮箱</span>
			<span class="">{{ userStore.user?.email }}</span>
		</div>
	</div>
</template>

<style scoped>

</style>