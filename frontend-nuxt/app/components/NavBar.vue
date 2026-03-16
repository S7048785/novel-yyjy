<script lang="ts" setup>
import { useRoute } from "vue-router";
import type { NavigationMenuItem } from "@nuxt/ui";

const navList = [
  { name: "首页", path: "/", icon: "material-symbols:house" },
  {
    name: "分类",
    path: "/category",
    icon: "material-symbols:grid-view-outline-rounded",
  },
  {
    name: "排行榜",
    path: "/rank",
    icon: "material-symbols:leaderboard-outline",
  },
  {
    name: "作者专区",
    path: "/author",
    icon: "material-symbols:person-edit-outline",
  },
  { name: "书架", path: "/shelf", icon: "material-symbols:book-outline" },
  {
    name: "个人中心",
    path: "/profile",
    icon: "material-symbols:account-circle-outline",
  },
];

const route = useRoute();
const isActive = (path: string) => {
  // 精确匹配或前缀匹配（例如 /category/xxx 时也认为 /category 是高亮）
  return route.path === path || route.path.startsWith(path + "/");
};
</script>
<template>
  <div
    class="px-10 py-4 border-b border-gray-200 dark:border-gray-700 grid grid-cols-[60%_40%] items-center"
  >
    <div class="flex items-center overflow-visible">
      <p class="font-bold text-lg mr-8">小说港</p>
      <ul class="flex gap-6 overflow-visible">
        <li v-for="item in navList" :key="item.path">
          <NuxtLink
            :class="[
              'flex items-center transform transition-transform duration-200 hover:scale-110 origin-center will-change-transform p-2 rounded-md',
              isActive(item.path)
                ? 'text-cyan-600 font-semibold bg-cyan-50 dark:bg-cyan-900/20'
                : 'hover:text-cyan-500 hover:bg-gray-100 dark:hover:bg-gray-700',
            ]"
            :to="item.path"
            aria-current="page"
          >
            <UIcon :name="item.icon" class="size-5 mr-2" />
            <span :class="isActive(item.path) ? 'pointer-events-none' : ''">{{
              item.name
            }}</span>
          </NuxtLink>
        </li>
      </ul>
    </div>
    <div class="flex gap-4 justify-end items-center">
      <UInput
        icon="i-lucide-search"
        size="xl"
        variant="outline"
        placeholder="搜索小说s、作者..."
        color="primary"
        :ui="{
          base: 'rounded-full',
        }"
      />
      <UColorModeButton size="xl" />
      <UAvatar src="https://github.com/benjamincanac.png" />
    </div>
  </div>
</template>
