# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

novel-admin-react 是小说阅读项目 (novel-yyjy) 的后台管理系统前端，基于 React + TypeScript + Vite 构建，使用 TanStack 系列库进行路由和数据管理。

## 技术栈

- **核心框架**: React 19.2.0
- **语言**: TypeScript ~5.9.3
- **构建工具**: Vite 7.3.1
- **UI框架**: Ant Design 6.3.2
- **CSS框架**: Tailwind CSS 4.2.1
- **状态管理**: TanStack React Query 5.90.21
- **全局状态管理**: Zustand 5.0.11
- **路由**: TanStack React Router 1.166.6
- **包管理器**: Bun

## 常用命令

```bash
# 安装依赖
bun install

# 启动开发服务器
bun dev

# 构建生产版本
bun build

# 代码检查
bun lint

# 预览生产构建
bun preview

# 从后端生成API客户端代码
bun api
```

## API集成

项目使用 OpenAPI 规范自动生成 TypeScript API 客户端：

- API生成脚本: `scripts/generate-api.js`
- 生成代码目录: `src/__generated/`
- 后端API地址: `http://localhost:9111` (通过 `/ts.zip` 下载OpenAPI规范)

## 项目结构

```
novel-admin-react/
├── src/
│   ├── __generated/          # 自动生成的API客户端代码
│   │   ├── model/            # 数据模型类型
│   │   └── services/         # API服务方法
│   ├── lib/                  # 工具库
│   │   ├── request.ts        # Axios实例配置
│   │   └── queryClient.ts    # React Query客户端配置
│   ├── assets/               # 静态资源
│   └── main.tsx              # React入口文件
├── scripts/
│   └── generate-api.js       # API生成脚本
├── public/                   # 静态资源
├── index.html                # HTML入口
├── vite.config.ts            # Vite配置
├── tailwind.config.js        # Tailwind CSS配置
├── tsconfig.json             # TypeScript配置
└── eslint.config.js          # ESLint配置
```

## 注意事项

- 后端服务运行在 `localhost:9111` 端口
- 前台用户端为 novel-frontend-vue (Vue 3)
- src/\_\_generated 下的所有代码都不能修改
