<script setup lang="ts">
import Login from "@/components/Login.vue";
import {getUserInfoAPI} from "@/api/user.ts";
import {useUserStore} from "@/stores/userStore.ts";
const userStore = useUserStore();

const getUserInfo = async () => {
	const res = await getUserInfoAPI()
	if (res.code === 0) {
		userStore.logout();
		return;
	}
	userStore.setUserInfo(res.data)
}

onMounted(() => {
	getUserInfo()
});
</script>

<template>
	<router-view />
	<login />

</template>

<style>
* {
	margin: 0;
	padding: 0;
}
ul,li {
	list-style: none;
}
</style>
