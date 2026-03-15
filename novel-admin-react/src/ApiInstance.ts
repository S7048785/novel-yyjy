// src/ApiInstance.ts
import { Api } from "./__generated";
import { message } from "antd";
import { useUserStore } from "@/lib/userStore";
import { router } from "@/main";

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:9111";

// 导出全局变量`api`
export const api = new Api(async ({ uri, method, headers, body }) => {
  // --- 请求拦截逻辑 ---
  const tenant = (window as any).__tenant as string | undefined;

  // 准备请求头
  const requestHeaders: Record<string, string> = {
    "content-type": "application/json;charset=UTF-8",
    ...headers,
  };

  // 添加租户信息
  if (tenant) {
    requestHeaders.tenant = tenant;
  }

  // 如果需要 Token，可以从 store 中获取并添加到 Header
  // const token = useUserStore.getState().token;
  // if (token) {
  //   requestHeaders.Authorization = `Bearer ${token}`;
  // }

  try {
    const response = await fetch(`${BASE_URL}${uri}`, {
      method,
      credentials: "include", // 允许跨域请求携带 Cookie
      body: body !== undefined ? JSON.stringify(body) : undefined,
      headers: requestHeaders,
    });

    // --- 响应拦截逻辑 ---

    // 1. 处理 HTTP 状态码错误
    if (!response.ok) {
      if (response.status === 401) {
        // 未授权，清除登录状态并跳转到登录页
        useUserStore.getState().logout();
        message.error("会话已过期，请重新登录");
        router.navigate({ to: "/login" });
      } else {
        message.error(`请求失败: ${response.status} ${response.statusText}`);
      }

      // 抛出错误以中断后续处理
      const errorData = await response.json().catch(() => ({}));
      throw errorData;
    }

    const text = await response.text();
    if (text.length === 0) {
      return null;
    }

    const result = JSON.parse(text);

    // 2. 处理业务错误码 (假设 code !== 1 表示业务失败)
    // 注意：这里的具体判断逻辑取决于你的后端接口规范
    if (result && typeof result.code === "number" && result.code !== 1) {
      // 如果是特定的业务错误，可以在这里全局提示
      // message.error(result.msg || "业务处理失败");
    }

    return result;
  } catch (error: any) {
    // 处理网络错误或其他异常
    if (error instanceof TypeError && error.message === "Failed to fetch") {
      message.error("网络连接失败，请检查您的网络设置");
    }
    throw error;
  }
});
