<script setup lang="ts">
import type {HomeBookView} from "@/type/home.ts";

const {pictures, data} = defineProps<{
	pictures: string[];
	data: HomeBookView[]
}>()

type SliderView = {
	book: HomeBookView;
	pictures: string;

}
const newData = reactive<SliderView[]>(pictures.map((item, index) => {
	return {
		book: data[index],
		pictures: item,
	}
}))
</script>

<template>
	<a-carousel arrows>
		<template #prevArrow>
			<div class="custom-slick-arrow" style="left: 15px;">
						<ArrowLeftOutlined />
			</div>
		</template>
		<template #nextArrow>
			<div class="custom-slick-arrow" style="right: 15px;">
					<ArrowRightOutlined />
			</div>
		</template>
			<div v-for="item in newData" class="relative">
				<img :src="item.pictures" alt="">
				<div class="bg-gradient-to-r from-black/40 to-transparent absolute left-0 top-0 h-full inset-0">
as
				</div>
				<div class="absolute bottom-5 left-5 text-left ">
					<a-tag color="#2db7f5" class="rounded-full px-3 py-0.5 mb-1">{{item.book.tag}}</a-tag>
					<p class="text-white font-bold text-2xl">{{item.book.bookName}}</p>
					<p class="text-[#eee] mb-2">{{item.book.intro}}</p>
					<a-button type="primary">立即阅读</a-button>
				</div>
			</div>
	</a-carousel>
</template>

<style scoped>
:deep(.slick-slider) {
	text-align: center;
	position: relative;
	overflow: hidden;
}

:deep(.slick-arrow.custom-slick-arrow) {
	width: 45px;
	height: 80px;
	margin-top: -30px;
	font-size: 25px;
	color: #fcfcfc;
	transition: ease all 0.3s;
	z-index: 1;
	background-color: #33333399;
	display: flex !important;
	align-items: center !important;
	justify-content: center;
	border-radius: 99999px;
}
:deep(.slick-arrow.custom-slick-arrow:before) {
	display: none;
}
:deep(.slick-arrow.custom-slick-arrow:hover) {
	color: #fcfcfc;
	opacity: 0.5;
}

:deep(.slick-slide h3) {
	color: #fff;
}
</style>