import { api } from "~/ApiInstance";
import type {
  HomeBookView,
  VisitRankBookView,
  HomeBookRankView,
  LastInsertBookView,
  LastUpdateBookView,
} from "~/__generated/model/static";

export interface HomeData {
  homeBooks: HomeBookView[];
  visitRank: VisitRankBookView[];
  newestRank: HomeBookRankView[];
  newestAdd: LastInsertBookView[];
  recentUpdate: LastUpdateBookView[];
}

export function useHome() {
  // 使用 useAsyncData 实现服务端渲染
  const { data, pending: loading, error } = useAsyncData(
    "home-data",
    async () => {
      const [
        homeRes,
        visitRankRes,
        newestRankRes,
        newestAddRes,
        recentUpdateRes,
      ] = await Promise.all([
        api.homeController.listHomeBooks(),
        api.homeController.listBookVisitRank(),
        api.homeController.listNewestRankBooks(),
        api.homeController.listNewestAddBooks(),
        api.homeController.listRecentUpdateBooks(),
      ]);

      return {
        homeBooks: homeRes.data || [],
        visitRank: visitRankRes.data || [],
        newestRank: newestRankRes.data || [],
        newestAdd: newestAddRes.data || [],
        recentUpdate: recentUpdateRes.data || [],
      };
    },
    {
      // 服务端渲染时等待数据
      server: true,
      // 客户端初始加载时重新获取
      lazy: false,
    },
  );

  // 从响应数据中提取
  const homeBooks = computed(() => data.value?.homeBooks || []);
  const visitRank = computed(() => data.value?.visitRank || []);
  const newestRank = computed(() => data.value?.newestRank || []);
  const newestAdd = computed(() => data.value?.newestAdd || []);
  const recentUpdate = computed(() => data.value?.recentUpdate || []);

  // 轮播图数据 (type=0)
  const bannerBooks = computed(() =>
    homeBooks.value.filter((b) => b.type === "0"),
  );

  // 本周强推 (type=1)
  const recommendBooks = computed(() =>
    homeBooks.value.filter((b) => b.type === "1"),
  );

  // 热门推荐 (type=2)
  const hotBooks = computed(() => homeBooks.value.filter((b) => b.type === "2"));

  // 精品推荐 (type=3)
  const boutiqueBooks = computed(() =>
    homeBooks.value.filter((b) => b.type === "3"),
  );

  // 格式化时间
  const formatTime = (time: string) => {
    const date = new Date(time);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / 60000);
    if (minutes < 60) return `${minutes}分钟前`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}小时前`;
    const days = Math.floor(hours / 24);
    return `${days}天前`;
  };

  return {
    // state
    homeBooks,
    visitRank,
    newestRank,
    newestAdd,
    recentUpdate,
    loading,
    error,
    // computed
    bannerBooks,
    recommendBooks,
    hotBooks,
    boutiqueBooks,
    // methods
    formatTime,
  };
}
