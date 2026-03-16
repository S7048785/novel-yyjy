# Nuxt SSR 配置指南

## 问题背景

项目使用 Nuxt 3 开发，首页数据通过 API 获取。最初实现时，首页在服务端渲染的 HTML 中没有实际数据，客户端需要等待 JavaScript 加载完成后才能发起 API 请求并渲染数据，导致：
- 首次加载时页面内容为空或加载状态
- SEO 不友好
- 用户体验不佳

## 解决方案

### 1. 使用 useAsyncData 实现服务端数据获取

**问题原因**：使用 `onMounted` + 手动调用 API 方式，数据获取只在客户端执行。

**解决方案**：使用 Nuxt 提供的 `useAsyncData` 钩子，它会在服务端执行数据获取，并将数据序列化到 HTML 中发送给客户端。

**修改文件**：`app/composables/useHome.ts`

```typescript
// 修改前：只在客户端获取数据
export function useHome() {
  const homeBooks = ref<HomeBookView[]>([]);
  const fetchHomeData = async () => {
    const res = await api.homeController.listHomeBooks();
    homeBooks.value = res.data || [];
  };
  return { homeBooks, fetchHomeData };
}

// 修改后：使用 useAsyncData
export function useHome() {
  const { data, pending: loading } = useAsyncData(
    "home-data",
    async () => {
      const res = await api.homeController.listHomeBooks();
      return res.data || [];
    },
    { server: true }
  );

  const homeBooks = computed(() => data.value || []);
  return { homeBooks, loading };
}
```

**页面调用**（`app/pages/index.vue`）：

```vue
<script setup>
const { loading, bannerBooks, recommendBooks, ... } = useHome();
// 无需 onMounted，useAsyncData 自动在服务端执行
</script>
```

### 2. 修复 API 实例的 SSR 兼容性问题

**问题原因**：`ApiInstance.ts` 中使用了 `window` 对象来判断客户端环境，但在 SSR 环境下 `window` 不存在，导致报错。

**解决方案**：添加环境判断，只在客户端访问 `window` 对象。

**修改文件**：`app/ApiInstance.ts`

```typescript
// 修改前
const tenant = (window as any).__tenant;

// 修改后
const isClient = typeof window !== "undefined";
const tenant = isClient ? (window as any).__tenant : undefined;
```

## 验证 SSR 是否生效

### 方法1：查看页面源代码

访问首页，执行以下命令检查：

```bash
curl -s http://localhost:3000 | grep "serverRendered"
```

如果返回 `"serverRendered":true`，说明 SSR 已生效。

### 方法2：检查 __NUXT_DATA__

```bash
curl -s http://localhost:3000 | grep '"home-data"'
```

如果能看到具体的书籍数据（如 `"bookName":"xxx"`），说明数据已在服务端获取并渲染。

### 方法3：检查 HTML 中是否有实际内容

```bash
curl -s http://localhost:3000 | grep "本周强推"
```

如果能看到完整的模块结构而非空状态，说明 SSR 正常工作。

## 关键点总结

1. **数据获取必须在服务端执行**：使用 `useAsyncData` 而非 `onMounted`
2. **避免使用浏览器 API**：在 SSR 环境下，`window`、`document`、`localStorage` 等不可用
3. **数据序列化**：`useAsyncData` 会自动将数据序列化到 HTML 的 `__NUXT_DATA__` 中
4. **组件自动导入**：Nuxt 会自动导入 `components/` 和 `composables/` 目录下的文件

## 相关文件

- `app/composables/useHome.ts` - 首页数据Composable
- `app/pages/index.vue` - 首页面
- `app/ApiInstance.ts` - API实例配置
- `app/components/home/*.vue` - 首页各模块组件
