# Spring Boot SSE 使用教程

本教程基于项目中的小说采集功能，展示如何实现服务器向客户端推送实时进度。

## 1. 什么是 SSE？

**Server-Sent Events (SSE)** 是一种服务器推送技术，允许服务器通过 HTTP 协议向客户端单方向推送数据。适用于：
- 实时进度推送
- 消息通知
- 股票行情更新
- 长时任务状态同步

**优点**：简单易用、自动重连、基于 HTTP、无需额外协议

---

## 2. 后端实现

### 2.1 创建进度数据对象

```java
// CrawlTaskStatus.java
@Data
public class CrawlTaskStatus {
    private String taskId;
    private String novelId;
    private String novelName;
    private String message;
    private String status;      // "采集中" / "已完成" / "采集失败"
    private int currentChapter;  // 当前章节
    private int totalChapters;  // 总章节数
    private double progress;    // 0.0 - 100.0
}
```

### 2.2 编写 SSE 控制器

```java
@RestController
@RequestMapping("/admin/crawler")
@Slf4j
public class CrawlerController {

    // 存储所有活跃的SSE连接 (使用ConcurrentHashMap保证线程安全)
    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * SSE接口：建立长连接，实时推送进度
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String bookId) {
        // 创建SSE连接，超时时间设为Long.MAX_VALUE
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // 将连接存入Map，使用bookId作为key
        emitters.put(bookId, emitter);

        // 处理连接断开（三种方式都会触发）
        emitter.onCompletion(() -> emitters.remove(bookId));
        emitter.onTimeout(() -> emitters.remove(bookId));
        emitter.onError(e -> emitters.remove(bookId));

        // 发送初始连接消息
        try {
            emitter.send(SseEmitter.event()
                .name("connect")           // 事件名称
                .data("connected")         // 发送的数据
                .build());
        } catch (IOException e) {
            emitters.remove(bookId);
        }

        return emitter;
    }

    /**
     * 推送采集进度到前端
     * 使用静态方法，便于在其他Service中调用
     */
    public static void pushProgress(String bookId, CrawlTaskStatus status) {
        SseEmitter emitter = emitters.get(bookId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("progress")       // 事件名称：前端通过此名称监听
                    .data(status)            // 发送对象，会自动序列化为JSON
                    .build());
            } catch (IOException e) {
                emitters.remove(bookId);
            }
        }
    }

    /**
     * 触发采集任务（异步执行，不阻塞主线程）
     */
    @PostMapping("/")
    public Result<Void> addNovelById(String bookId, Integer chapterCount) {
        long dbBookId = novelScraperService.addNovelById(bookId);

        // 异步执行爬取任务，在爬取过程中调用pushProgress推送进度
        try {
            novelScraperService.scrapeChapters(bookId, dbBookId, chapterCount, bookId);
        } catch (Exception e) {
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

### 2.3 关键点说明

| 要点 | 说明 |
|------|------|
| `produces = MediaType.TEXT_EVENT_STREAM_VALUE` | 告诉浏览器这是SSE响应 |
| `SseEmitter(Long.MAX_VALUE)` | 设置超时时间，Long.MAX_VALUE表示无限 |
| `ConcurrentHashMap` | 存储多连接，线程安全 |
| `onCompletion/onTimeout/onError` | 三种断开连接的处理 |
| `.name("progress")` | 事件名称，前端按此名称监听 |

---

## 3. 前端实现

### 3.1 使用 EventSource 连接 SSE

```tsx
// 定义任务类型
interface CrawlerTask {
  id: number;
  bookId: string;
  novelName: string;
  status: "pending" | "running" | "completed" | "failed";
  progress: number;
  message: string;
}

export function Crawler() {
  const [tasks, setTasks] = useState<CrawlerTask[]>([]);
  const sseRef = useRef<EventSource | null>(null);

  // 组件卸载时清理连接
  useEffect(() => {
    return () => {
      if (sseRef.current) {
        sseRef.current.close();
      }
    };
  }, []);

  // 连接SSE获取进度
  const connectSSE = (bookId: string): Promise<void> => {
    return new Promise((resolve) => {
      // 先关闭已有连接
      if (sseRef.current) {
        sseRef.current.close();
      }

      const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:9111";

      // 创建EventSource连接
      const eventSource = new EventSource(
        `${BASE_URL}/admin/crawler/stream?bookId=${bookId}`,
        { withCredentials: true }  // 携带cookie用于认证
      );

      // 连接成功
      eventSource.onopen = () => {
        console.log("SSE连接已建立");
        resolve();
      };

      // 监听 progress 事件（与后端 .name("progress") 对应）
      eventSource.addEventListener("progress", (event: MessageEvent) => {
        try {
          const data = JSON.parse(event.data);

          // 更新任务状态
          setTasks((prev) => {
            const runningTaskIndex = prev.findIndex((t) => t.status === "running");
            if (runningTaskIndex === -1) return prev;

            const newTasks = [...prev];
            const task = newTasks[runningTaskIndex];

            if (data.status === "已完成" || data.status === "completed") {
              newTasks[runningTaskIndex] = {
                ...task,
                status: "completed",
                progress: 100,
                message: data.message,
              };
            } else if (data.status === "采集中" || data.status === "running") {
              newTasks[runningTaskIndex] = {
                ...task,
                status: "running",
                progress: Math.round(data.progress),
                message: data.message,
              };
            }
            return newTasks;
          });
        } catch (e) {
          console.error("解析SSE数据失败:", e);
        }
      });

      // 连接错误
      eventSource.onerror = (error) => {
        console.error("SSE错误:", error);
        resolve();  // 出错时也resolve，避免无限等待
      };

      sseRef.current = eventSource;
    });
  };

  // 发起采集任务
  const handleCrawl = async (values: { bookId: string; chapterCount?: number }) => {
    // 1. 先创建任务并显示
    const newTask: CrawlerTask = {
      id: Date.now(),
      bookId: values.bookId,
      novelName: "获取中...",
      status: "running",
      progress: 0,
      message: "正在开始采集...",
    };
    setTasks([newTask, ...tasks]);

    // 2. 建立SSE连接，等待连接建立后再发请求
    await Promise.race([
      connectSSE(values.bookId),
      new Promise((resolve) => setTimeout(resolve, 3000)),  // 超时3秒
    ]);

    // 3. 调用采集API
    await api.crawlerController.addNovelById({
      bookId: values.bookId,
      chapterCount: values.chapterCount,
    });
  };
}
```

---

## 4. SSE 完整流程图

```
┌─────────────┐                              ┌─────────────┐
│   前端      │                              │   后端      │
└──────┬──────┘                              └──────┬──────┘
       │                                            │
       │  1. POST /admin/crawler (创建任务)          │
       │───────────────────────────────────────────►│
       │                                            │
       │  2. GET /admin/crawler/stream?bookId=xxx   │
       │◄──────────────────────────────────────────│
       │     (建立SSE长连接)                         │
       │                                            │
       │              ◄──── 推送进度 ────►           │
       │         (多次推送 progress 事件)            │
       │                                            │
       │  3. 任务完成，连接自动断开                  │
       │                                            │
```

---

## 5. 注意事项

1. **认证问题**：SSE 需要携带认证信息，使用 `withCredentials: true`

2. **连接管理**：
   - 使用 `ConcurrentHashMap` 存储多个连接
   - 正确处理 `onCompletion`、`onTimeout`、`onError` 事件

3. **前端连接时机**：
   - 先建立 SSE 连接，再调用触发任务的 API
   - 使用 `Promise.race` 防止连接超时

4. **数据格式**：
   - 后端 `SseEmitter.event().name("xxx").data(obj)` 发送
   - 前端 `eventSource.addEventListener("xxx", handler)` 监听

5. **CORS 配置**：如果跨域，需要在后端配置 CORS，允许 SSE 路径
