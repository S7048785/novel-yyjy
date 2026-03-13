# Spring Boot SSE 小说采集实战教程

本教程基于项目中的小说采集功能，深入分析 SSE 的完整实现流程。

---

## 1. SSE 概述

### 1.1 什么是 SSE？

Server-Sent Events (SSE) 是一种服务器推送技术，允许服务器通过 HTTP 协议向客户端**单方向**实时推送数据。

```
┌─────────────────────────────────────────────────────────────┐
│                     SSE 与 WebSocket 对比                    │
├─────────────────────────────────────────────────────────────┤
│  SSE                          │  WebSocket                  │
│  ─────────────────────────────┼─────────────────────────────│
│  单向通信 (服务器→客户端)       │  双向通信                   │
│  基于 HTTP                     │  独立协议 (WS/WSS)           │
│  自动重连                      │  需要手动重连               │
│  简单易用                      │  相对复杂                   │
│  适合服务端推送场景             │  适合实时交互场景           │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 本项目 SSE 适用场景

项目中用于**小说采集的实时进度推送**：
- 用户点击"开始采集"按钮
- 后端开始爬取小说章节（耗时操作）
- 前端需要实时显示：当前爬取到第几章、进度百分比、是否完成等

---

## 2. 完整流程分析

### 2.1 整体架构

```
┌──────────────────────────────────────────────────────────────────────────┐
│                              完整流程                                     │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   【步骤1】用户输入小说ID，点击"开始采集"                                  │
│                        │                                                 │
│                        ▼                                                 │
│   【步骤2】前端创建任务对象，显示"采集中"状态                               │
│                        │                                                 │
│                        ▼                                                 │
│   【步骤3】前端建立 SSE 连接 (EventSource)                                │
│                        │                                                 │
│                        ▼                                                 │
│   【步骤4】后端创建 SseEmitter，存入 ConcurrentHashMap                   │
│                        │                                                 │
│                        ▼                                                 │
│   【步骤5】前端调用采集 API (POST /admin/crawler)                         │
│                        │                                                 │
│                        ▼                                                 │
│   【步骤6】后端异步执行爬取，过程中多次调用 pushProgress()                  │
│                        │                                                 │
│                        ▼                                                 │
│   【步骤7】SseEmitter 推送 CrawlTaskStatus 对象给前端                    │
│                        │                                                 │
│                        ▼                                                 │
│   【步骤8】前端接收数据，更新任务列表的进度显示                             │
│                        │                                                 │
│                        ▼                                                 │
│   【步骤9】爬取完成，推送完成状态，前端显示"已完成"                         │
│                        │                                                 │
│                        ▼                                                 │
│   【步骤10】连接断开（SSE 自动或手动关闭）                                 │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 3. 后端实现详解

### 3.1 核心代码

**文件位置**: `novel-service/src/main/java/com/novel/controller/admin/CrawlerController.java`

```java
@RestController
@RequestMapping("/admin/crawler")
@Slf4j
public class CrawlerController {

    // ========== 关键1: 使用 ConcurrentHashMap 存储所有活跃的 SSE 连接 ==========
    // 为什么用 Map？支持多个用户同时采集不同的小说
    // 为什么用 ConcurrentHashMap？线程安全，支持高并发
    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * ========== SSE 接口：建立长连接，实时推送进度 ==========
     *
     * 调用方式: GET /admin/crawler/stream?bookId=xxx
     *
     * produces = MediaType.TEXT_EVENT_STREAM_VALUE
     *   告诉浏览器这个接口返回的是 SSE 数据流，而不是普通的 JSON
     *   会自动设置响应头: Content-Type: text/event-stream
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String bookId) {
        // ========== 关键2: 创建 SseEmitter，设置超时时间 ==========
        // 参数是超时时间（毫秒），Long.MAX_VALUE 表示无限时间
        // 实际生产环境可以设置具体时间，如 30分钟 = 30 * 60 * 1000
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // ========== 关键3: 将连接存入 Map，bookId 作为 key ==========
        // 这样后续可以通过 bookId 找到对应的连接并推送数据
        emitters.put(bookId, emitter);

        // ========== 关键4: 处理连接断开事件 ==========
        // 三种方式都会导致连接断开，都需要清理 Map
        emitter.onCompletion(() -> {
            // 连接正常完成
            emitters.remove(bookId);
            log.info("SSE 连接完成: {}", bookId);
        });

        emitter.onTimeout(() -> {
            // 连接超时
            emitters.remove(bookId);
            log.info("SSE 连接超时: {}", bookId);
        });

        emitter.onError(e -> {
            // 连接出错（网络中断、服务器异常等）
            emitters.remove(bookId);
            log.error("SSE 连接错误: {}", bookId, e);
        });

        // ========== 关键5: 发送初始连接消息 ==========
        // 作用：告诉前端连接已建立，可以开始发送请求了
        try {
            emitter.send(SseEmitter.event()
                .name("connect")           // 事件名称（可选）
                .data("connected")         // 数据内容
                .build());
        } catch (IOException e) {
            // 发送失败说明连接已经出问题，清理掉
            emitters.remove(bookId);
            log.error("发送初始连接消息失败: {}", bookId, e);
        }

        return emitter;
    }

    /**
     * ========== 推送采集进度到前端 ==========
     *
     * 这是一个静态方法，方便在其他 Service 中调用
     * 调用位置: NovelScraperServiceImpl.scrapeChapters()
     *
     * 推送时机:
     *   1. 开始采集时推送初始状态
     *   2. 每采集一章后推送进度
     *   3. 采集完成后推送完成状态
     *   4. 采集失败时推送错误状态
     */
    public static void pushProgress(String bookId, CrawlTaskStatus status) {
        // 从 Map 中获取对应的 SSE 连接
        SseEmitter emitter = emitters.get(bookId);

        if (emitter != null) {
            try {
                // 关键6: 发送事件
                // name: 事件名称，前端通过这个名称监听
                // data: 发送的数据对象，会自动序列化为 JSON
                emitter.send(SseEmitter.event()
                    .name("progress")       // 事件名称
                    .data(status)            // 数据对象（会被转为 JSON）
                    .build());
            } catch (IOException e) {
                // 发送失败，清理连接
                emitters.remove(bookId);
                log.error("推送进度失败: {}", bookId, e);
            }
        }
    }

    /**
     * ========== 触发采集任务 ==========
     *
     * 调用方式: POST /admin/crawler/
     * 参数: bookId (小说ID), chapterCount (要采集的章节数，可选)
     */
    @PostMapping("/")
    public Result<Void> addNovelById(String bookId, Integer chapterCount) {
        // 保存小说基本信息到数据库
        long dbBookId = novelScraperService.addNovelById(bookId);

        // 异步执行爬取任务，不阻塞主请求
        // 这样可以让 SSE 实时推送进度
        try {
            // 这个方法内部会多次调用 CrawlerController.pushProgress()
            novelScraperService.scrapeChapters(bookId, dbBookId, chapterCount, bookId);
        } catch (Exception e) {
            log.error("爬取小说章节失败", e);

            // 推送错误状态
            CrawlTaskStatus errorStatus = new CrawlTaskStatus(bookId, String.valueOf(dbBookId), 0);
            errorStatus.setMessage("采集失败: " + e.getMessage());
            errorStatus.setStatus("失败");
            pushProgress(bookId, errorStatus);
        }

        return Result.ok();
    }
}
```

### 3.2 进度推送详解

**文件位置**: `novel-service/src/main/java/com/novel/service/impl/NovelScraperServiceImpl.java`

```java
public void scrapeChapters(String bookId, long dbBookId, Integer count, String taskId) {
    // ... 获取小说信息和章节列表 ...

    // ========== 第1次推送：开始采集 ==========
    CrawlTaskStatus startStatus = new CrawlTaskStatus(taskId, String.valueOf(dbBookId), crawlCount);
    startStatus.setMessage("开始采集: " + novelName);
    startStatus.setNovelId(dbBookId.toString());
    // 调用静态方法推送
    CrawlerController.pushProgress(taskId, startStatus);

    // 开始爬取章节（这里使用回调函数实时推送进度）
    var chapterRes = NovelScraperUtil.scrapeChapters(
        bookId,
        startIndex,
        crawlCount,
        // 回调函数：每爬取一章调用一次
        (int currentChapter, int totalChapters, double progress) -> {

            CrawlTaskStatus status = new CrawlTaskStatus(taskId, String.valueOf(dbBookId), crawlCount);
            status.setNovelId(dbBookId.toString());
            status.setNovelName(bookDetail.getBookName());
            status.setCurrentChapter(currentChapter);     // 当前第几章
            status.setTotalChapters(totalChapters);        // 总章节数
            status.setProgress(progress);                  // 进度百分比 0-100
            status.setMessage("正在采集第 " + currentChapter + " 章 / 共 " + totalChapters + " 章");
            status.setStatus("采集中");

            // ========== 第2~N次推送：实时进度 ==========
            CrawlerController.pushProgress(taskId, status);
        }
    );

    // ... 保存章节到数据库 ...

    // ========== 最后1次推送：完成 ==========
    CrawlTaskStatus completeStatus = new CrawlTaskStatus(taskId, String.valueOf(dbBookId), totalChapters);
    completeStatus.setNovelId(dbBookId.toString());
    completeStatus.setNovelName(bookDetail.getBookName());
    completeStatus.setCurrentChapter(totalChapters);
    completeStatus.setTotalChapters(totalChapters);
    completeStatus.setProgress(100.0);
    completeStatus.setMessage("采集完成！共 " + totalChapters + " 章");
    completeStatus.setStatus("已完成");

    CrawlerController.pushProgress(taskId, completeStatus);
}
```

### 3.3 进度数据对象

**文件位置**: `novel-pojo/src/main/java/com/novel/bo/CrawlTaskStatus.java`

```java
@Data
public class CrawlTaskStatus {
    private String taskId;           // 任务ID（也是bookId）
    private String novelId;          // 数据库中的小说ID
    private String novelName;        // 小说名称
    private String message;          // 显示的消息
    private String status;           // 状态: "采集中" / "已完成" / "失败"
    private LocalDateTime startTime;  // 开始时间
    private int currentChapter;      // 当前采集到第几章
    private int totalChapters;       // 总章节数
    private double progress;         // 进度 0.0 - 100.0
}
```

---

## 4. 前端实现详解

### 4.1 核心代码

**文件位置**: `novel-admin-react/src/routes/app/crawler/index.tsx`

```tsx
import { useState, useRef, useEffect } from "react";

// 定义采集任务类型
interface CrawlerTask {
  id: number;
  bookId: string;
  novelName: string;
  chapterCount: number;
  status: "pending" | "running" | "completed" | "failed";
  progress: number;
  message: string;
  createTime: string;
  startTime?: string;
  currentChapter?: number;
  totalChapters?: number;
}

export function Crawler() {
  const [tasks, setTasks] = useState<CrawlerTask[]>([]);
  const [loading, setLoading] = useState(false);

  // 使用 ref 存储 EventSource，组件销毁时需要清理
  const sseRef = useRef<EventSource | null>(null);

  // ========== 关键1: 组件卸载时清理 SSE 连接 ==========
  // 防止内存泄漏和无效连接
  useEffect(() => {
    return () => {
      if (sseRef.current) {
        sseRef.current.close();  // 关闭连接
        sseRef.current = null;
      }
    };
  }, []);

  // ========== 建立 SSE 连接 ==========
  const connectSSE = (bookId: string): Promise<void> => {
    return new Promise((resolve) => {
      // 如果已有连接，先关闭（避免重复连接）
      if (sseRef.current) {
        sseRef.current.close();
      }

      // 获取后端地址
      const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:9111";

      // ========== 关键2: 创建 EventSource 连接 ==========
      // 参数1: SSE 接口地址，包含 bookId 作为查询参数
      // 参数2: 选项对象，withCredentials: true 表示携带 cookie（用于认证）
      const eventSource = new EventSource(
        `${BASE_URL}/admin/crawler/stream?bookId=${bookId}`,
        { withCredentials: true }
      );

      // ========== 关键3: 连接打开时的回调 ==========
      // 当后端发送初始连接消息时触发
      eventSource.onopen = () => {
        console.log("SSE 连接已建立");
        resolve();  // 通知调用者连接已建立
      };

      // ========== 关键4: 监听 named event - progress ==========
      // 这是项目中使用的方式：通过 addEventListener 监听特定事件
      // 后端发送的事件 name 是 "progress"
      eventSource.addEventListener("progress", (event: MessageEvent) => {
        try {
          // event.data 是后端发送的 JSON 字符串
          const data = CrawTaskStatus = JSON.parse(event.data);
          console.log("收到进度更新:", data);

          // 根据状态更新任务列表
          setTasks((prev) => {
            // 找到正在运行的任务
            const runningTaskIndex = prev.findIndex((t) => t.status === "running");
            if (runningTaskIndex === -1) return prev;

            const newTasks = [...prev];
            const task = newTasks[runningTaskIndex];

            // 处理完成状态
            if (data.status === "已完成" || data.status === "completed") {
              newTasks[runningTaskIndex] = {
                ...task,
                status: "completed",
                progress: 100,
                message: data.message,
                novelName: data.novelName || task.novelName,
              };
            }
            // 处理采集中状态
            else if (data.status === "采集中" || data.status === "running") {
              newTasks[runningTaskIndex] = {
                ...task,
                status: "running",
                progress: Math.round(data.progress),
                message: data.message,
                novelName: data.novelName || task.novelName,
                currentChapter: data.currentChapter,
                totalChapters: data.totalChapters,
              };
            }
            // 处理失败状态
            else if (data.status === "失败" || data.status === "failed") {
              newTasks[runningTaskIndex] = {
                ...task,
                status: "failed",
                message: data.message,
              };
            }

            return newTasks;
          });
        } catch (e) {
          console.error("解析 SSE 数据失败:", e);
        }
      });

      // ========== 关键5: 监听连接错误 ==========
      // SSE 自动重连机制：onerror 后会自动尝试重新连接
      // 如果不想自动重连，可以调用 eventSource.close()
      eventSource.onerror = (error) => {
        console.error("SSE 错误:", error);
        // 即使出错也 resolve，避免调用者无限等待
        resolve();
      };

      // 保存引用
      sseRef.current = eventSource;
    });
  };

  // ========== 处理采集操作 ==========
  const handleCrawl = async (values: { bookId: string; chapterCount?: number }) => {
    setLoading(true);

    try {
      // ========== 步骤1: 创建任务并显示 ==========
      const newTask: CrawlerTask = {
        id: Date.now(),
        bookId: values.bookId,
        novelName: "获取中...",
        chapterCount: values.chapterCount || 0,
        status: "running",
        progress: 0,
        message: "正在开始采集...",
        createTime: new Date().toLocaleString(),
        startTime: new Date().toLocaleString(),
      };

      setTasks([newTask, ...tasks]);

      // ========== 步骤2: 建立 SSE 连接 ==========
      // 重要：先建立连接，再发请求！确保连接已建立后再发送请求
      // Promise.race: 3秒超时，防止连接一直不建立
      await Promise.race([
        connectSSE(values.bookId),
        new Promise((resolve) => setTimeout(resolve, 3000)),
      ]);

      // ========== 步骤3: 调用采集 API ==========
      // 这是一个普通的 POST 请求，不会阻塞
      await api.crawlerController.addNovelById({
        bookId: values.bookId,
        chapterCount: values.chapterCount,
      });

    } catch (error: any) {
      console.error("采集失败:", error);
    } finally {
      setLoading(false);
    }
  };

  // ... 其他代码（删除任务、渲染UI等）...
}
```

---

## 5. 连接生命周期

### 5.1 建立连接

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          建立连接流程                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  前端                                    后端                           │
│  ────────────────────────────────────────────────────────────────────   │
│                                                                         │
│  new EventSource(url)    ──►  GET /stream?bookId=xxx                  │
│                                    │                                   │
│                                    ▼                                   │
│                             创建 SseEmitter                              │
│                                    │                                   │
│                                    ▼                                   │
│                             存入 ConcurrentHashMap                       │
│                                    │                                   │
│                                    ▼                                   │
│                             返回 SseEmitter                             │
│       ◄──────────────────────────│                                   │
│                                                                         │
│  onopen 回调触发            ──►  发送初始消息                            │
│       │                            │                                   │
│       │                            ▼                                   │
│       │                     emitter.send(event()                      │
│       │                        .name("connect")                        │
│       │                        .data("connected"))                     │
│       ◄──────────────────────────│                                   │
│                                                                         │
│  连接建立完成                                                      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.2 推送数据

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          推送数据流程                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  后端 Service                      SseEmitter          前端             │
│  ────────────────────────────────────────────────────────────────────   │
│                                                                         │
│  爬取第1章完成后:                                                        │
│  ─────────────────                                                      │
│  CrawlerController.     ──►  emitters.get(bookId)                      │
│  pushProgress()             │                                           │
│       │                     ▼                                           │
│       │              emitter.send(event()                               │
│       │                  .name("progress")                              │
│       │                  .data(status))                                │
│       │                     │                                           │
│       └───────────────────►│◄───────────────────────────────────►      │
│                              │              addEventListener           │
│                              │                  ("progress",            │
│                              │                   callback)             │
│                              │                      │                   │
│                              │                      ▼                   │
│                              │              解析 JSON                   │
│                              │                      │                   │
│                              │                      ▼                   │
│                              │              更新 UI                      │
│                                                                         │
│  爬取第2章完成后:                                                        │
│  ─────────────────  (重复上述流程)                                       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.3 关闭连接

连接关闭有多种情况：

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          连接关闭情况                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  1. 正常完成                                                            │
│     ───────────                                                        │
│     后端推送 status = "已完成"                                         │
│     前端显示完成                                                        │
│     EventSource 自动关闭 / 前端手动调用 close()                         │
│                                                                         │
│  2. 客户端主动关闭                                                      │
│     ─────────────                                                      │
│     用户刷新页面 / 组件卸载                                             │
│     useEffect 清理函数中调用 eventSource.close()                       │
│                                                                         │
│  3. 连接超时                                                            │
│     ─────────                                                          │
│     SseEmitter 设置了超时时间                                           │
│     超时后 onTimeout 回调触发                                           │
│     从 Map 中移除                                                       │
│                                                                         │
│  4. 服务器错误                                                          │
│     ───────────                                                        │
│     服务器宕机 / 网络中断                                               │
│     onError 回调触发                                                    │
│     SSE 自动尝试重连（默认行为）                                         │
│                                                                         │
│  5. 手动删除任务                                                        │
│     ─────────────                                                      │
│     前端调用 handleDelete()                                             │
│     关闭 EventSource                                                    │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 6. 关键代码解析

### 6.1 为什么用 ConcurrentHashMap？

```java
// 场景：多个用户同时采集不同的小说
// user1 采集 bookId=001
// user2 采集 bookId=002
// user3 采集 bookId=003

// 每个用户有独立的 SSE 连接
emitters.put("001", emitter1);  // user1
emitters.put("002", emitter2);  // user2
emitters.put("003", emitter3);  // user3

// 推送时只发给对应 bookId 的用户
emitters.get("001").send(...);  // 只发给 user1
```

**ConcurrentHashMap 的优势**：
- 线程安全：高并发下不会出问题
- 高性能：比 synchronizedMap 性能更好

### 6.2 为什么先建立 SSE 再发请求？

```typescript
// ❌ 错误顺序
await api.crawlerController.addNovelById(...);  // 先发请求
await connectSSE(bookId);                        // 再建立连接
// 问题：请求已经完成，连接还没建立，错过进度推送

// ✅ 正确顺序
await connectSSE(bookId);                        // 先建立连接
await api.crawlerController.addNovelById(...);  // 再发请求
// 好处：连接已建立，可以实时接收进度
```

### 6.3 withCredentials: true 的作用

```typescript
const eventSource = new EventSource(url, {
  withCredentials: true  // 携带 Cookie，用于身份认证
});
```

因为 SSE 请求会发送到后端，需要携带认证信息（Token/Cookie），否则会报 401/403 错误。

---

## 7. 注意事项

### 7.1 后端注意事项

| 事项 | 说明 |
|------|------|
| 线程安全 | 使用 `ConcurrentHashMap` 存储连接 |
| 超时设置 | 生产环境建议设置具体超时时间 |
| 异常处理 | 推送失败时要清理连接 |
| 序列化 | 发送的对象需要能被 JSON 序列化 |

### 7.2 前端注意事项

| 事项 | 说明 |
|------|------|
| 清理连接 | 组件卸载时调用 `close()` |
| 认证 | 设置 `withCredentials: true` |
| 连接顺序 | 先建立连接，再发请求 |
| 错误处理 | `onerror` 中也要 resolve |
| 防止重复 | 建立新连接前先关闭旧连接 |

### 7.3 生产环境建议

1. **连接超时**：不要用 `Long.MAX_VALUE`，设置合理超时
2. **重连机制**：SSE 默认会自动重连，可配置
3. **限流**：防止恶意请求创建大量连接
4. **监控**：记录连接数和推送成功率
5. **负载均衡**：SSE 场景下注意 session 亲和性
