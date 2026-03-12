import { QueryClient } from "@tanstack/react-query";

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // 大多数人会改的常用默认值
      staleTime: 1000 * 60, // 1分钟内视为新鲜
      gcTime: 1000 * 60 * 5, // 5分钟后垃圾回收（以前叫 cacheTime）
      retry: 2, // 失败重试2次
      refetchOnWindowFocus: false, // 很多人喜欢关掉
    },
  },
});
