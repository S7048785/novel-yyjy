import { createFileRoute } from "@tanstack/react-router";
import { useState, useRef, useEffect } from "react";
import {
  Card,
  Form,
  InputNumber,
  Input,
  Button,
  List,
  Tag,
  Space,
  message,
  Progress,
  Alert,
} from "antd";
import {
  CloudDownloadOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
} from "@ant-design/icons";
import { api } from "../../../ApiInstance";
import type { CrawTaskStatus } from "../../../types/CrawlTaskStatus";

// 采集任务类型
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
  const [form] = Form.useForm();
  const [tasks, setTasks] = useState<CrawlerTask[]>([]);
  const [loading, setLoading] = useState(false);
  const sseRef = useRef<EventSource | null>(null);

  // 清理SSE连接
  useEffect(() => {
    return () => {
      if (sseRef.current) {
        sseRef.current.close();
      }
    };
  }, []);

  // 连接SSE获取进度
  const connectSSE = (bookId: string, taskId: number): Promise<void> => {
    return new Promise((resolve) => {
      // 如果已有连接，先关闭
      if (sseRef.current) {
        sseRef.current.close();
      }

      const BASE_URL =
        import.meta.env.VITE_API_BASE_URL || "http://localhost:9111";
      const eventSource = new EventSource(
        `${BASE_URL}/admin/crawler/stream?bookId=${bookId}`,
        {
          withCredentials: true,
        },
      );

      // 连接成功后再resolve
      eventSource.onopen = () => {
        console.log("SSE连接已建立");
        resolve();
      };

      // 使用 addEventListener 监听 named event
      eventSource.addEventListener("progress", (event: MessageEvent) => {
        try {
          const data: CrawTaskStatus = JSON.parse(event.data);
          console.log("收到进度更新:", data);
          console.log("当前任务列表:", tasks);

          // 根据消息类型处理 - 找到正在运行的任务进行更新
          setTasks((prev) => {
            const runningTaskIndex = prev.findIndex(
              (t) => t.status === "running",
            );
            if (runningTaskIndex === -1) return prev;

            const newTasks = [...prev];
            const task = newTasks[runningTaskIndex];

            if (data.status === "已完成" || data.status === "completed") {
              newTasks[runningTaskIndex] = {
                ...task,
                status: "completed",
                progress: 100,
                message: data.message,
                novelName: data.novelName || task.novelName,
              };
            } else if (data.status === "采集中" || data.status === "running") {
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
            return newTasks;
          });
        } catch (e) {
          console.error("解析SSE数据失败:", e);
        }
      });

      eventSource.onerror = (error) => {
        console.error("SSE错误:", error);
        // 即使出错也resolve，避免无限等待
        resolve();
      };

      sseRef.current = eventSource;
    });
  };

  // 处理采集
  const handleCrawl = async (values: {
    bookId: string;
    chapterCount?: number;
  }) => {
    setLoading(true);

    try {
      // 先创建任务并显示
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
      message.success("已添加采集任务");

      // 先建立SSE连接，等待连接真正建立后再发请求（最多等3秒）
      await Promise.race([
        connectSSE(values.bookId, newTask.id),
        new Promise((resolve) => setTimeout(resolve, 3000)),
      ]);

      // 调用采集API
      await api.crawlerController.addNovelById({
        bookId: values.bookId,
        chapterCount: values.chapterCount,
      });
    } catch (error: any) {
      message.error(error?.message || "采集失败");
    } finally {
      setLoading(false);
    }
  };

  // 删除任务
  const handleDelete = (id: number, bookId: string) => {
    setTasks(tasks.filter((task) => task.id !== id));
    // 如果删除的是当前SSE连接的任务，关闭连接
    const task = tasks.find((t) => t.id === id);
    if (task && sseRef.current) {
      sseRef.current.close();
      sseRef.current = null;
    }
    message.success("任务已删除");
  };

  // 获取状态标签
  const getStatusTag = (status: CrawlerTask["status"]) => {
    const config = {
      pending: { color: "default", text: "等待中" },
      running: { color: "processing", text: "采集中" },
      completed: { color: "success", text: "已完成" },
      failed: { color: "error", text: "失败" },
    };
    const { color, text } = config[status];
    return <Tag color={color}>{text}</Tag>;
  };

  return (
    <div>
      <h2 style={{ marginBottom: 16 }}>小说采集</h2>

      {/* 采集表单 */}
      <Card style={{ marginBottom: 24 }}>
        <Alert
          message="使用说明"
          description="请输入要采集的小说ID，选择要采集的章节数量（可选，默认全部），然后点击开始采集。"
          type="info"
          showIcon
          style={{ marginBottom: 16 }}
        />
        <Form
          form={form}
          layout="inline"
          onFinish={handleCrawl}
          style={{ flexWrap: "nowrap" }}
        >
          <Form.Item
            name="bookId"
            label="小说ID"
            rules={[{ required: true, message: "请输入小说ID" }]}
            style={{ marginRight: 16 }}
          >
            <Input placeholder="请输入小说ID" style={{ width: 200 }} />
          </Form.Item>
          <Form.Item
            name="chapterCount"
            label="章节数量"
            style={{ marginRight: 16 }}
          >
            <InputNumber
              min={1}
              placeholder="可选，默认全部"
              style={{ width: 150 }}
            />
          </Form.Item>
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              icon={<CloudDownloadOutlined />}
              loading={loading}
            >
              开始采集
            </Button>
          </Form.Item>
        </Form>
      </Card>

      {/* 任务列表 */}
      <Card title="采集任务">
        {tasks.length === 0 ? (
          <div style={{ textAlign: "center", padding: 40, color: "#999" }}>
            暂无采集任务
          </div>
        ) : (
          <List
            dataSource={tasks}
            renderItem={(task) => (
              <List.Item
                actions={[
                  task.status === "completed" ? (
                    <Button
                      type="link"
                      icon={<CheckCircleOutlined />}
                      style={{ color: "#52c41a" }}
                      disabled
                    >
                      完成
                    </Button>
                  ) : null,
                  <Button
                    type="link"
                    danger
                    icon={<DeleteOutlined />}
                    onClick={() => handleDelete(task.id, task.bookId)}
                  >
                    删除
                  </Button>,
                ]}
              >
                <List.Item.Meta
                  title={
                    <Space>
                      <span style={{ fontWeight: "bold" }}>
                        {task.novelName || "小说ID: " + task.bookId}
                      </span>
                      {getStatusTag(task.status)}
                      <span style={{ color: "#999", fontSize: 12 }}>
                        {task.createTime}
                      </span>
                    </Space>
                  }
                  description={
                    <div>
                      <div style={{ marginBottom: 8 }}>
                        {task.message}
                        {task.totalChapters &&
                          task.totalChapters > 0 &&
                          ` (第${task.currentChapter || 0}章/共${task.totalChapters}章)`}
                      </div>
                      {task.status === "running" && (
                        <Progress
                          percent={task.progress}
                          size="small"
                          status="active"
                        />
                      )}
                      {task.status === "completed" && (
                        <Progress percent={100} size="small" status="success" />
                      )}
                    </div>
                  }
                />
              </List.Item>
            )}
          />
        )}
      </Card>
    </div>
  );
}

export const Route = createFileRoute("/app/crawler/")({
  component: Crawler,
});
