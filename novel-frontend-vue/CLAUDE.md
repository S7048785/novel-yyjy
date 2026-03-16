# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于 Vue3 + TypeScript + Vite 开发的小说阅读前端应用，支持小��推荐、排行榜、阅读、评论、历史阅读、个人书架、个人信息等功能。

## 技术栈

- **核心框架**: Vue 3.5.13 (Composition API)
- **开发语言**: TypeScript 5.8
- **构建工具**: Vite 6.2.4
- **UI框架**: Ant Design Vue 4.0.0-rc.6
- **状态管理**: Pinia 3.0.1 + pinia-plugin-persistedstate (持久化)
- **路由**: Vue Router 4.5.0
- **HTTP客户端**: Axios 1.9.0
- **CSS框架**: UnoCSS 66.3.1 (原子化CSS)
- **工具库**:
  - dayjs (日期处理)
  - @vueuse/core (Vue组合式工具)
  - mitt (事件总线)

## 项目结构

```
src/
├── api/           # API接口定义 (按功能模块划分)
├── components/    # 公共组件
├── view/         # 页面视图组件
│   ├── home/     # 首页及相关组件
│   ├── book/     # 书籍详情页
│   ├── read/     # 阅读页面
│   ├── user/     # 用户中心
│   └── ...
├── layouts/      # 布局组件
├── router/       # 路由配置
├── stores/       # Pinia状态管理
├── utils/        # 工具函数
├── type/         # TypeScript类型定义
├── directive/    # 自定义指令
└── plugins/      # 插件配置
```

## 常用开发命令

```bash
# 安装依赖
pnpm install

# 启动开发服务器 (端口自动分配)
pnpm dev

# 构建生产版本
pnpm build

# 预览生产构建
pnpm preview
```

## 重要配置

### 环境变量
- 开发环境配置文件：`.env.development`
- API基础URL：`VITE_APP_API_URL='http://localhost:8081'`

### 路径别名
- `@` 指向 `src` 目录 (在 vite.config.ts 中配置)

### 自动化配置
- **组件自动导入**: unplugin-vue-components 自动导入 Ant Design Vue 组件
- **API自动导入**: unplugin-auto-import 自动导入 Vue、Vue Router、Pinia 的组合式 API
- **类型声明**: 自动生成 `src/auto-imports.d.ts` 和 `src/components.d.ts`

### 请求配置
- Axios 实例位于 `src/utils/request.ts`
- 自动携带 Cookie (`withCredentials: true`)
- 401 错误自动跳转登录页
- 响应拦截器自动返回 `response.data`

## 开发注意事项

1. **组件开发**:
   - Ant Design Vue 组件会自动导入，无需手动引入
   - 使用 UnoCSS 原子化类名进行样式编写
   - 组件名使用 PascalCase 命名

2. **状态管理**:
   - 使用 Pinia 进行状态管理
   - 重要状态（如用户信息）通过 persist 插件持久化到 localStorage
   - 示例：`useUserStore()` 管理用户登录状态

3. **路由管理**:
   - 使用嵌套路由，DefaultLayout 作为主布局
   - 需要认证的路由设置 `meta: { requiresAuth: true }`
   - 路由守卫自动处理登录验证和页面滚动重置

4. **API调用**:
   - 所有 API 返回 Promise<Data<T>> 类型
   - 统一错误处理在 request.ts 中实现
   - API 接口按模块划分在 `src/api/` 目录

5. **类型定义**:
   - TypeScript 类型定义在 `src/type/` 目录
   - 使用泛型 `Data<T>` 和 `PageData<T>` 处理响应数据

6. **自定义指令**:
   - `v-slide-in`: 页面元素滑入动画效果

## 特色功能实现

- **全局事件总线**: 使用 mitt 在组件间通信（如登录弹窗触发）
- **登录状态持久化**: 通过 Pinia persist 插件实现
- **响应式布局**: 最小宽度 1020px，适配大屏设备
- **路由懒加载**: 除首页外使用动态 import 实现代码分割