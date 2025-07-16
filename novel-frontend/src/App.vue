<script setup lang="ts">
import Login from "@/components/Login.vue";
import {getUserInfoAPI} from "@/api/user.ts";
import {useUserStore} from "@/stores/userStore.ts";
const userStore = useUserStore();
const router = useRouter()
const getUserInfo = async () => {
	const res = await getUserInfoAPI()
	if (res.code === 0) {
		userStore.logout();
		router.push('/search')
		return;
	}
	userStore.setUserInfo(res.data)
}

onMounted(() => {
	getUserInfo()
});
</script>

<template>
	<router-view :key="$route.fullPath"/>
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
