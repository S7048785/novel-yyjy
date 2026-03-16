# AI Skills: 后端 API 使用指南

本文档为 AI 助手提供了一份通用指南，说明如何在本项目中正确地调用后端 API。该项目使用了自动生成的 TypeScript 客户端代码，因此请务必遵循以下规范。

## 1. 核心架构概览

项目采用前后端分离架构，前端通过一个统一配置的 API 实例与后端进行通信。所有的 API 请求方法、数据模型（Model）均由工具自动生成，存放在 `src/__generated` 目录下。

### 关键文件与目录说明

- **`src/ApiInstance.ts`**
  - **作用**：API 的核心入口文件。
  - **功能**：配置了基础 URL（Base URL）、请求拦截器（如添加 Token、Tenant 租户信息）、响应拦截器（如统一错误处理、登录失效跳转）。
  - **重要导出**：导出了全局唯一的单例对象 `api`。**所有 API 调用都必须使用此对象**。

- **`src/__generated/`**
  - **作用**：存放所有自动生成的 API 代码。**请勿手动修改此目录下的任何文件**。
  - **结构**：
    - `Api.ts`: 定义了主 `Api` 类，它聚合了所有的业务控制器（Controller）。
    - `services/`: 存放具体的控制器类（Controller）。每个文件对应后端的一个 Controller，包含该模块下的所有接口方法。
    - `model/`: 存放数据模型定义。
      - `static/`: 包含请求参数（Request DTO）和响应数据（Response View Object）的 TypeScript 类型定义。

## 2. 如何调用 API

### 2.1 引入 API 实例

在任何组件或逻辑文件中，必须从 `@/ApiInstance` 导入 `api` 实例，而不是自己去实例化 `Api` 类或 `Controller` 类。

```typescript
import { api } from "@/ApiInstance";
```

### 2.2 调用模式

`api` 对象下挂载了所有的控制器（Controller）。调用方式通常遵循以下模式：

`api.控制器名.方法名(参数对象)`

**示例模式**：

```typescript
// 假设有一个名为 someController 的控制器，其中有一个名为 someMethod 的方法
const response = await api.someController.someMethod({
  // 参数通常是一个对象，具体结构请参考 IDE 的类型提示或生成的类型定义文件

  // 情况 A：路径参数 (Path Variable)
  // 如果接口路径上有参数（如 /api/resource/{id}），通常直接作为属性传入
  id: "123",

  // 情况 B：查询参数 (Query Params)
  // 通常封装在 params 对象中，或者直接作为属性展开（视生成配置而定）
  params: {
    keyword: "search_text",
    page: 1,
    pageSize: 10,
  },

  // 情况 C：请求体 (Request Body)
  // POST/PUT 请求的数据通常封装在 body 对象中
  body: {
    field1: "value1",
    field2: "value2",
  },
});
```

## 3. 响应处理规范

### 3.1 响应结构

大多数接口返回的数据都封装在一个通用的 `Result<T>` 结构中。

```typescript
// 典型结构参考（以实际代码为准）
interface Result<T> {
  readonly code?: number; // 业务状态码
  readonly msg: string; // 提示信息
  readonly data: T; // 实际业务数据
}
```

### 3.2 错误处理与判断

1.  **HTTP 状态码**：`ApiInstance` 内部已经拦截了非 2xx 的 HTTP 错误（如 401, 500），并进行了统一提示或跳转处理。
2.  **业务状态码**：即使 HTTP 状态码为 200，业务逻辑也可能失败。
    - **必须检查 `code`**：通常约定 `code === 1` 表示业务成功（具体值请参考 `Result` 定义或项目约定）。
    - 如果 `code !== 1`，则视为业务失败，应当根据 `msg` 字段进行提示或处理。

```typescript
const res = await api.someController.action({...});

if (res.code === 1) {
  // 业务成功，处理 res.data
  console.log(res.data);
} else {
  // 业务失败，提示错误信息
  console.error(res.msg);
}
```

## 4. 类型定义参考

在编写代码时，请充分利用 TypeScript 的类型提示。

- **请求参数类型**：通常以 `*Req` 或 `*Input` 结尾。
- **响应数据类型**：通常以 `*View`、`*Dto` 或 `*Vo` 结尾。
- **位置**：这些类型定义都位于 `src/__generated/model/static/` 目录下。

## 5. 总结

1.  **统一入口**：始终使用 `import { api } from "@/ApiInstance"`。
2.  **查阅生成代码**：如果不确定参数结构，请查看 `src/__generated/services/` 下对应的 Controller 定义。
3.  **遵循规范**：检查 `code` 字段以确认业务是否成功。
