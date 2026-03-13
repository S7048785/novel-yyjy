import {
  createFileRoute,
  Link,
  Outlet,
  useLocation,
  useNavigate,
} from "@tanstack/react-router";
import { Button, Layout, Menu, Avatar, Dropdown, Space } from "antd";
import {
  DashboardOutlined,
  BookOutlined,
  ReadOutlined,
  UserOutlined,
  CloudDownloadOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  LogoutOutlined,
} from "@ant-design/icons";
import type { MenuProps } from "antd";
import { ProgressBar } from "../components/ProgressBar.tsx";
import { ThemeToggle } from "../components/ThemeToggle.tsx";
import { useState } from "react";
import { useUserStore } from "../lib/userStore";

const { Header, Sider, Content } = Layout;

// 菜单项
const menuItems: MenuProps["items"] = [
  {
    key: "/app/dashboard",
    icon: <DashboardOutlined />,
    label: <Link to="/app/dashboard">仪表盘</Link>,
  },
  {
    key: "/app/book",
    icon: <BookOutlined />,
    label: <Link to="/app/book">小说管理</Link>,
  },
  {
    key: "/app/chapter",
    icon: <ReadOutlined />,
    label: <Link to="/app/chapter">章节管理</Link>,
  },
  {
    key: "/app/users",
    icon: <UserOutlined />,
    label: <Link to="/app/users">用户管理</Link>,
  },
  {
    key: "/app/crawler",
    icon: <CloudDownloadOutlined />,
    label: <Link to="/app/crawler">小说采集</Link>,
  },
];

// 布局组件
export function AppLayout() {
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const { userInfo, logout } = useUserStore();
  const location = useLocation();
  // 退出登录
  const handleLogout = () => {
    logout();
    navigate({ to: "/login" });
  };

  // 用户下拉菜单
  const userMenuItems = [
    {
      key: "logout",
      icon: <LogoutOutlined />,
      label: "退出登录",
      onClick: handleLogout,
    },
  ];

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <ProgressBar />
      <Sider collapsible collapsed={collapsed} trigger={null} width={220}>
        <div
          style={{
            height: 64,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: 18,
            fontWeight: "bold",
            color: "#1677ff",
          }}
        >
          {!collapsed ? "小说管理系统" : ""}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          defaultSelectedKeys={[location.pathname]}
          items={menuItems}
          style={{ borderRight: 0 }}
        />
      </Sider>

      <Layout>
        <Header
          className="flex items-center justify-between gap-4"
          style={{ padding: "0" }}
        >
          <div>
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              style={{
                fontSize: "16px",
                width: 64,
                height: 64,
              }}
            />
          </div>
          <div className="mr-10 flex items-center gap-4">
            <ThemeToggle />
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Space style={{ cursor: "pointer" }}>
                <Avatar
                  src={userInfo?.userPhoto}
                  icon={!userInfo?.userPhoto && <UserOutlined />}
                  style={{ backgroundColor: "#1677ff" }}
                />
                <span style={{ color: "#666" }}>
                  {userInfo?.nickName || userInfo?.email || "管理员"}
                </span>
              </Space>
            </Dropdown>
          </div>
        </Header>
        <Content
          style={{
            padding: 24,
            minHeight: "calc(100vh - 64px)",
          }}
        >
          <div
            className="content-wrapper"
            style={{
              borderRadius: 8,
              minHeight: "100%",
            }}
          >
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
}

// App 布局路由
export const Route = createFileRoute("/app")({
  component: AppLayout,
});
