# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

novel-frontend-nuxt 是小说阅读平台的前端应用，基于 Nuxt 3 + Vue 3 开发。与 novel-backend (Spring Boot) 配合使用，提供小说推荐、排行榜、阅读、评论、历史阅读、个人书架等功能。

## 技术栈

- **框架**: Nuxt 3 (compatibilityDate: "2025-07-15")
- **UI 组件库**: @nuxt/ui v4
- **全局状态管理**: Pinia
- **样式**: Tailwind CSS v4
- **字体**: @fontsource/google-sans, @fontsource/noto-serif-sc, @fontsource-variable/victor-mono
- **包管理**: Bun

## 项目结构

```
novel-frontend-nuxt/
├── app/
│   ├── __generated/          # 自动生成的 API 客户端 (从后端 ts.zip 导入)
│   │   ├── services/         # API 控制器服务
│   │   └── model/            # DTO/VO 类型定义
│   ├── pages/                # 页面路由
│   │   ├── index.vue         # 首页
│   │   ├── book/             # 书籍详情
│   │   ├── read/             # 阅读页
│   │   ├── rank/             # 排行榜
│   │   ├── category/         # 分类页
│   │   ├── search/           # 搜索页
│   │   ├── shelf/            # 书架
│   │   ├── author/           # 作者页
│   │   └── profile/          # 个人中心
│   ├── components/           # Vue 组件
│   ├── layouts/              # 布局文件
│   ├── assets/css/           # 样式文件
│   ├── ApiInstance.ts        # API 实例配置
│   └── app.vue               # 根组件
├── scripts/
│   └── generate-api.js        # API 生成脚本
└── public/                   # 静态资源
```

## 常用命令

```bash
# 安装依赖
bun install

# 启动开发服务器 (端口 3000)
bun run dev

# 构建生产版本
bun run build

# 预览生产构建
bun run preview

# 生成 API 客户端 (从后端下载 ts.zip)
bun run api
```

## API 集成

- API 客户端通过 `bun run api` 命令从后端自动生成
- 后端需运行在 `http://localhost:9111`
- API 类型定义位于 `app/__generated/`
- 使用方式: `import { api } from "~/ApiInstance"; api.homeController.listHomeBooks()`

## 认证机制

- 后端使用 Sa-Token 管理登录状态
- Token 名称为 `token`，通过请求头传递

## 后端关联

- 后端地址: `http://localhost:9111`
- 后端项目: `novel-backend` (Spring Boot 3.4.5)
- API 文档: `/openapi.html` (Knife4j)
- TypeScript 客户端下载: `/ts.zip`
