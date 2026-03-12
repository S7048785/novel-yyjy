import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { UserLoginView } from "../__generated/model/static";

interface UserState {
  // 用户信息
  userInfo: UserLoginView | null;
  // Token
  token: string | null;
  // 是否已登录
  isAuthenticated: boolean;
  // 设置用户信息（登录时调用）
  setUser: (user: UserLoginView) => void;
  // 清除用户信息（登出时调用）
  logout: () => void;
}

export const useUserStore = create<UserState>()(
  persist(
    (set) => ({
      userInfo: null,
      token: null,
      isAuthenticated: false,

      setUser: (user: UserLoginView) => {
        set({
          userInfo: user,
          token: user.token || null,
          isAuthenticated: true,
        });
      },

      logout: () => {
        set({
          userInfo: null,
          token: null,
          isAuthenticated: false,
        });
      },
    }),
    {
      name: "user-storage",
      partialize: (state) => ({
        userInfo: state.userInfo,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
    },
  ),
);
