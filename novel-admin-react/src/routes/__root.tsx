import { createRootRoute, Outlet, useNavigate } from "@tanstack/react-router";
import { useEffect } from "react";
import { useUserStore } from "../lib/userStore";
import { api } from "../ApiInstance";
import { message } from "antd";
// 根路由组件
function RootComponent() {
  const navigate = useNavigate();
  const setUser = useUserStore((state) => state.setUser);
  const logout = useUserStore((state) => state.logout);
  // 初始化用户状态
  useEffect(() => {
    async function initializeUser() {
      try {
        const res = await api.adminController.me();
        if (res.code == 1) {
          setUser(res.data);
        } else {
          throw new Error();
        }
      } catch (error) {
        logout();
        message.error("获取用户信息失败");
        navigate({
          to: "/login",
        });
      }
    }
    initializeUser();
  }, []);
  return <Outlet />;
}

// 根路由
export const Route = createRootRoute({
  component: RootComponent,
});
