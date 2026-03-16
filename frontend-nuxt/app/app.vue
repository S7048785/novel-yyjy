<script lang="ts" setup>
import { api } from "./ApiInstance";

// import Button from "primevue/button";
const homeData = ref();
const getHomeData = async () => {
  const res = await api.homeController.listHomeBooks();
  homeData.value = res.data;
};

onMounted(() => {
  getHomeData();
});
</script>
<template>
  <div>
    <!-- <NuxtRouteAnnouncer />
    <NuxtWelcome /> -->
    <UApp>
      <NuxtLayout>
        <!-- <transition name="page-fade" mode="out-in"> -->
        <NuxtPage />
        <!-- </transition> -->
      </NuxtLayout>
    </UApp>

    <!-- <p class="bg-green text-blue">{{ JSON.stringify(homeData) }}</p> -->
  </div>
</template>

<style>
.page-enter-active,
.page-leave-active {
  transition: all 0.4s;
}
.page-enter-from,
.page-leave-to {
  opacity: 0;
  transform: translateY(20px);
  /* filter: blur(1rem); */
}
</style>
