<script setup lang="ts">
import {UserOutlined, HistoryOutlined, ReadOutlined, CommentOutlined} from "@ant-design/icons-vue"
const menuState = reactive([
	{
		icon: UserOutlined,
		label: '个人信息',
		to: '/user/setting'
	},
	{
		icon: HistoryOutlined,
		label: '阅读历史',
		to: '/user/history'
	},
	{
		icon: ReadOutlined,
		label: '我的书架',
		to: '/user/bookcase'
	}
])

const route = useRoute()

watch(() => route.fullPath, (newVal) => {
	selectKey.value[0] = newVal
})

const selectKey = ref([route.fullPath])

const handleClick = (e) => {
	selectKey.value[0] = e.key
}
</script>

<template>
	<div class="min-h-120 bg-white p-5 rounded-lg flex">
			<a-menu
					:selectedKeys="selectKey"
					style="width: 256px; padding-right: 20px"
					class="h-full mr-10"
					mode="vertical"
					@select="handleClick"
			>
				<a-menu-item v-for="item in menuState" :key="item.to">
					<template #icon>
						<component :is="item.icon" />
					</template>
					<router-link :to="item.to">{{ item.label }}</router-link>
				</a-menu-item>
			</a-menu>
			<router-view class="w-full" />
	</div>
</template>

<style scoped>

</style>