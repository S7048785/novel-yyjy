<script setup lang="ts">

import router from "@/router";
</script>

<!-- src/views/404.vue -->
<template>
	<div class="bg-[#f2f2f2] size-screen relative">
			<div class="text-center absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 ">
				<p class="text-5xl select-none text-blue">
					Page Not Found
				</p>
				<!-- From Uiverse.io by AKAspidey01 -->
				<button
						@click="router.push('/')"
						class="bg-white mt-8 text-center w-68 rounded-2xl h-14 relative text-black text-xl font-semibold group"
						type="button"
				>
					<div
							class="bg-blue-400 rounded-xl h-12 w-1/4 flex items-center justify-center absolute left-1 top-[4px] group-hover:w-97% z-10 duration-300"
					>
						<svg
								xmlns="http://www.w3.org/2000/svg"
								viewBox="0 0 1024 1024"
								height="25px"
								width="25px"
						>
							<path
									d="M224 480h640a32 32 0 1 1 0 64H224a32 32 0 0 1 0-64z"
									fill="#000000"
							></path>
							<path
									d="m237.248 512 265.408 265.344a32 32 0 0 1-45.312 45.312l-288-288a32 32 0 0 1 0-45.312l288-288a32 32 0 1 1 45.312 45.312L237.248 512z"
									fill="#000000"
							></path>
						</svg>
					</div>
					<p class="translate-x-2">Go Home</p>
				</button>

			</div>
	</div>
</template>
<style scoped>
p:first-child {
	text-shadow: rgba(0, 123, 255, 0.5) 0 0 10px;
}
</style>