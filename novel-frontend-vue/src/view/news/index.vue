<script setup lang="ts">

</script>

<template>
	<div class="bg-[#f3f3f3]">
		<NavBar></NavBar>
		<div class="w-320 min-h-130 mx-auto mb-5">
			<div class="flex mt-4">
				<div class="w-[900px]">
					<Carousel :pictures="['/image/carousel1.png', '/image/carousel2.png','/image/carousel3.png']"></Carousel>
				</div>
				<div class="flex-1 bg-black p-4">
					<div class="flex justify-between items-center">
						<a class="text-blue text-lg inline-flex items-center">
							<CrownFilled class="color-amber-300 mr-2"/>
							<span class="font-bold">本周推荐</span>
						</a>
						<a class="text-blue-300 text-sm">更多推荐 ></a>
					</div>
					<div class="text-white mt-4">
						<div>
							<div class="flex items-center px-4 py-2 rounded-lg hover:bg-gray-800">
								<TrophyFilled class="text-amber mr-4"/>
								<div>
									<p class="text-lg font-bold">北国谍影</p>
									<div class="text-sm text-gray">
										<p>作者：小王</p>
										<p>谍战+悬疑，看民国特工如何逆转局势</p>
									</div>
								</div>
								<RightOutlined class="text-blue ml-auto"/>
							</div>
						</div>
					</div>

				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>

</style>