import { createFileRoute, redirect, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { Form, Input, Button, Card, message, Space, theme } from "antd";
import { LockOutlined, UserOutlined, SafetyOutlined } from "@ant-design/icons";
import { api } from "../ApiInstance";
import { ThemeToggle } from "../components/ThemeToggle.tsx";
import { useUserStore } from "../lib/userStore";
const BASE_URL = import.meta.env.VITE_API_BASE_URL;
// 登录函数
const login = async (credentials: {
  username: string;
  password: string;
  captcha: string;
}) => {
  const result = await api.adminController.login({
    body: {
      username: credentials.username,
      password: credentials.password,
      captcha: credentials.captcha,
    },
  });
  return result;
};

function LoginPage() {
  const navigate = useNavigate();
  const setUser = useUserStore((state) => state.setUser);
  const [form] = Form.useForm();
  const [captchaUrl, setCaptchaUrl] = useState<string>(
    `${BASE_URL}/captcha?${Date.now()}`,
  );

  // 登录 mutation
  const loginMutation = useMutation({
    mutationFn: login,
    onSuccess: (data) => {
      if (data.code === 1) {
        message.success("登录成功");
        // 使用 userStore 保存用户信息
        if (data.data) {
          setUser(data.data);
        }
        // 跳转到仪表盘
        navigate({ to: "/app/dashboard" });
      } else {
        message.error(data.msg || "登录失败");
        // 登录失败后刷新验证码
        setCaptchaUrl(`${BASE_URL}/captcha?${Date.now()}`);
        form.setFieldValue("captcha", "");
      }
    },
    onError: (error: any) => {
      message.error(error?.message || "登录失败，请稍后重试");
      setCaptchaUrl(`${BASE_URL}/captcha?${Date.now()}`);

      form.setFieldValue("captcha", "");
    },
  });

  // 表单提交
  const handleSubmit = (values: {
    username: string;
    password: string;
    captcha: string;
  }) => {
    loginMutation.mutate(values);
  };

  // 验证码点击刷新
  const handleCaptchaClick = () => {
    if (captchaUrl) {
      URL.revokeObjectURL(captchaUrl);
    }
    setCaptchaUrl(`${BASE_URL}/captcha?${Date.now()}`);
  };
  const { token } = theme.useToken();
  return (
    <div
      style={{ backgroundColor: token.colorBgContainer }}
      className={"min-h-screen flex justify-center items-center relative"}
    >
      <div className="absolute top-10 right-10">
        <ThemeToggle />
      </div>
      <Card
        style={{
          width: 420,
          boxShadow: "0 8px 24px rgba(0, 0, 0, 0.12)",
          borderRadius: 8,
        }}
      >
        <div style={{ textAlign: "center", marginBottom: 32 }}>
          <h1 style={{ fontSize: 24, fontWeight: "bold", marginBottom: 8 }}>
            小说管理系统
          </h1>
          <p style={{ color: "#666" }}>后台管理登录</p>
        </div>

        <Form
          form={form}
          name="login"
          onFinish={handleSubmit}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: "请输入用户名" },
              { type: "string", message: "请输入有效的用户名" },
            ]}
          >
            <Input prefix={<UserOutlined />} placeholder="用户名" />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: "请输入密码" },
              { min: 6, message: "密码至少6位" },
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="密码" />
          </Form.Item>

          <Form.Item
            name="captcha"
            rules={[{ required: true, message: "请输入验证码" }]}
          >
            <Space>
              <Input
                prefix={<SafetyOutlined />}
                placeholder="验证码"
                style={{ flex: 1 }}
                maxLength={4}
              />
              <div
                onClick={handleCaptchaClick}
                style={{
                  cursor: "pointer",
                  height: 40,
                  lineHeight: "40px",
                  border: "1px solid #d9d9d9",
                  borderRadius: 6,
                  padding: "0 8px",
                  background: "#f5f5f5",
                  minWidth: 100,
                  textAlign: "center",
                }}
              >
                {captchaUrl ? (
                  <img
                    src={captchaUrl}
                    alt="验证码"
                    className="w-25 h-10 rounded-md mr-2 border-black border-1"
                  />
                ) : (
                  <span>点击刷新</span>
                )}
              </div>
            </Space>
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loginMutation.isPending}
              block
              style={{ height: 44, fontSize: 16 }}
            >
              {loginMutation.isPending ? "登录中..." : "登 录"}
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

export const Route = createFileRoute("/login")({
  component: LoginPage,
  beforeLoad: async () => {
    const isAuthenticated = useUserStore.getState().isAuthenticated;
    console.log("Login route beforeLoad, isAuthenticated:", isAuthenticated);
    if (isAuthenticated) {
      throw redirect({
        to: "/app/dashboard",
      });
    }
  },
});
