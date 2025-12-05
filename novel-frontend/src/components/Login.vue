<script setup lang="ts">
import emitter from "@/utils/emitter.ts";
import type { UserLoginInput, UserRegisterInput } from "@/type/user.ts";
import { loginAPI, registerAPI } from "@/api/user.ts";
import { message } from "ant-design-vue";
import { useUserStore } from "@/stores/userStore.ts";

const userStore = useUserStore();

const loginVisible = ref(false);

// false登录 true注册
const toggle = ref(false);

watch(toggle, () => {
  loginForm.value = {
    email: "",
    password: "",
    captcha: "",
  };
  registerForm.value = {
    email: "",
    password: "",
    nickName: "",
    captcha: "",
  };
});

const captchaUrl = ref(
  import.meta.env.VITE_APP_API_URL + "/captcha" + "?" + Date.now()
);
const setCaptchaRandom = () => {
  captchaUrl.value =
    import.meta.env.VITE_APP_API_URL + "/captcha" + "?" + Date.now();
};
const loginForm = ref<UserLoginInput>({
  email: "",
  password: "",
  captcha: "",
});

const loginDisabled = ref(false);
// 登录
const loginSubmit = async () => {
  loginDisabled.value = true;
  // 参数校验
  if (
    !loginForm.value.email ||
    !loginForm.value.password ||
    !loginForm.value.captcha
  ) {
    message.error("请填写完整信息");
    loginDisabled.value = false;
    return;
  }

  const res = await loginAPI(loginForm.value);
  if (res.code === 0) {
    message.error(res.msg);
    setCaptchaRandom();
    loginForm.value.captcha = "";
    return;
  }
  // 登录成功
  userStore.setUserInfo(res.data);
  localStorage.setItem("token", res.data.token);
  loginVisible.value = false;
  message.success("登录成功");
  loginDisabled.value = false;
};

const registerForm = ref<UserRegisterInput & { confirmPassword?: string }>({
  email: "",
  password: "",
  nickName: "",
  captcha: "",
  confirmPassword: "",
});

// 注册
const registerSubmit = async () => {
  // 数据校验
  if (
    !registerForm.value.email ||
    !registerForm.value.password ||
    !registerForm.value.nickName ||
    !registerForm.value.captcha
  ) {
    message.error("请填写完整信息");
    return;
  }
  const res = await registerAPI({
    email: registerForm.value.email,
    password: registerForm.value.password,
    nickName: registerForm.value.nickName,
    captcha: registerForm.value.captcha,
  });
  if (res.code === 1) {
    userStore.setUserInfo({
      id: res.data.id,
      email: res.data.email,
      nickName: res.data.nickName,
      userPhoto: res.data.userPhoto,
    });
    localStorage.setItem("token", res.data.token);
    message.success("注册成功");
    loginVisible.value = false;
  } else {
    setCaptchaRandom();
  }
};

const close = () => {
  toggle.value = false;
};

onMounted(() => {
  emitter.on("open-login", () => {
    loginVisible.value = true;
    isDark.value = document.documentElement.classList.value === "dark";
  });
});

const isDark = ref(document.documentElement.classList.value === "dark");

onUnmounted(() => {
  emitter.off("open-login");
  // emitter.off('close-login')
});
</script>

<template>
  <a-modal
    closable
    style="top: 4em"
    :body-style="{
      height: toggle ? '530px' : '410px',
      'transition-duration': '0.3s',
      overflow: 'hidden',
      position: 'relative',
    }"
    :footer="false"
    :maskStyle="{
      'backdrop-filter': 'blur(3px)',
      'background-color': `${
        isDark ? 'rgba(0, 0, 0, 0.3)' : 'rgba(255, 255, 255, 0.3)'
      }`,
    }"
    @cancel="close"
    v-model:open="loginVisible"
  >
    <template #closeIcon> </template>
    <transition name="sign-in">
      <div v-if="!toggle" class="absolute w-90% m-6 bg-white">
        <h2 class="text-2xl font-bold text-gray-900 mb-8">登录</h2>
        <a-form @submit="loginSubmit" :model="loginForm" class="flex flex-col">
          <a-form-item name="email">
            <a-input
              v-model:value="loginForm.email"
              type="email"
              class="bg-gray-100 text-gray-900 border-0! rounded-md p-2 focus:bg-gray-200 focus:outline-none focus:ring-1 focus:ring-blue-500 transition ease-in-out duration-150"
              placeholder="邮箱"
            />
          </a-form-item>

          <a-form-item name="password">
            <a-input
              v-model:value="loginForm.password"
              autocomplete="off"
              type="password"
              class="bg-gray-100 text-gray-900 rounded-md p-2 mt-4 border-0! focus:bg-gray-200 focus:outline-none focus:ring-1 focus:ring-blue-500 transition ease-in-out duration-150"
              placeholder="密码"
            />
          </a-form-item>
          <!--				TODO: 验证玛-->
          <div class="inline-flex items-center mb-4 justify-end">
            <img
              class="w-25 h-10 rounded-md mr-2 border-black border-1"
              @click="setCaptchaRandom"
              :src="captchaUrl"
              alt=""
            />
            <a-form-item name="captcha" class="mb-0">
              <a-input
                v-model:value="loginForm.captcha"
                type="text"
                class="w-30 text-center bg-gray-100 text-gray-900 border-0! rounded-md p-2 focus:bg-gray-200 focus:outline-none focus:ring-1 focus:ring-blue-500 transition ease-in-out duration-150"
                placeholder="输入验证码"
              />
            </a-form-item>
          </div>
          <div class="inline-flex items-center justify-between my-4">
            <p>
              <a href="#" class="text-blue-500 hover:underline">忘记密码?</a>
            </p>
            <p class="text-gray-900">
              还没有账号?
              <a
                @click="toggle = true"
                href="#"
                class="text-blue-500 mx-2 hover:underline"
                >注册</a
              >
            </p>
          </div>
          <a-button
            :disabled="loginDisabled"
            htmlType="submit"
            class="bg-gradient-to-r! from-indigo-500 to-blue-500 text-white block h-10 rounded-md mt-4 hover:bg-indigo-600 hover:to-blue-600 transition ease-in-out duration-150"
          >
            登录
          </a-button>
        </a-form>
      </div>
    </transition>
    <transition name="sign-up">
      <div v-if="toggle" class="m-6 absolute w-90%">
        <h2 class="text-2xl font-bold text-gray-900 mb-8">注册</h2>
        <a-form
          @submit="registerSubmit"
          :model="registerForm"
          class="flex flex-col"
        >
          <a-input
            v-model:value="registerForm.nickName"
            type="text"
            class="bg-gray-100 text-gray-900 border-0! rounded-md p-2 mb-8 focus:bg-gray-200 focus:outline-none focus:ring-1 focus:ring-blue-500 transition ease-in-out duration-150"
            placeholder="昵称"
          />
          <a-input
            v-model:value="registerForm.email"
            type="email"
            class="bg-gray-100 text-gray-900 border-0! rounded-md p-2 mb-8 focus:bg-gray-200 focus:outline-none focus:ring-1 focus:ring-blue-500 transition ease-in-out duration-150"
            placeholder="邮箱"
          />
          <a-input
            v-model:value="registerForm.password"
            type="password"
            autocomplete="off"
            class="bg-gray-100 text-gray-900 border-0! rounded-md p-2 mb-8 focus:bg-gray-200 focus:outline-none focus:ring-1 focus:ring-blue-500 transition ease-in-out duration-150"
            placeholder="密码"
          />
          <a-input
            v-model:value="registerForm.confirmPassword"
            type="password"
            autocomplete="off"
            class="bg-gray-100 text-gray-900 border-0! rounded-md p-2 mb-4 focus:bg-gray-200 focus:outline-none focus:ring-1 focus:ring-blue-500 transition ease-in-out duration-150"
            placeholder="确认密码"
          />
          <!--				TODO: 验证玛-->
          <div class="inline-flex items-center mb-4 justify-end">
            <a-input
              v-model:value="registerForm.captcha"
              type="text"
              class="w-30 text-center bg-gray-100 text-gray-900 border-0! rounded-md p-2 mr-2 focus:bg-gray-200 focus:outline-none focus:ring-1 focus:ring-blue-500 transition ease-in-out duration-150"
              placeholder="输入验证码"
            />
            <img
              class="w-25 h-10 rounded-md mr-2 border-black border-1"
              @click="setCaptchaRandom"
              :src="captchaUrl"
              alt=""
            />
          </div>
          <p class="text-gray-900 text-center my-4">
            已有账号?
            <a
              @click="toggle = false"
              href="#"
              class="text-blue-500 mx-2 hover:underline"
              >登录</a
            >
          </p>
          <button
            type="submit"
            class="bg-gradient-to-r! from-indigo-500 to-blue-500 text-white font-bold py-2 px-4 rounded-md mt-4 hover:bg-indigo-600 hover:to-blue-600 transition ease-in-out duration-150"
          >
            注册
          </button>
        </a-form>
      </div>
    </transition>
    <!--			</div>-->
  </a-modal>
</template>

<style scoped lang="less">
.sign-in-enter-active,
.sign-in-leave-active,
.sign-up-enter-active,
.sign-up-leave-active {
  transition: opacity 0.2s ease, transform 0.3s ease;
}

/* 在进入前 延迟一段时间*/
.sign-in-enter-active,
.sign-up-enter-active {
  transition-delay: 0.2s;
}

/* 向左进入 */
.sign-in-enter-from,
.sign-up-enter-from {
  opacity: 0;
  transform: translateX(-300px);
}

/* 向右离开 */
.sign-up-leave-to,
.sign-in-leave-to {
  opacity: 0;
  transform: translateX(300px);
}
</style>
