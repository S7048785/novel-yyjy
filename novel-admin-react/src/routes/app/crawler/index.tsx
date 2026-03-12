import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
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
  PlayCircleOutlined,
  CheckCircleOutlined,
  SyncOutlined,
} from "@ant-design/icons";

// 采集任务类型
interface CrawlerTask {
  id: number;
  bookId: string;
  chapterCount: number;
  status: "pending" | "running" | "completed" | "failed";
  progress: number;
  message: string;
  createTime: string;
}

// Mock 任务数据
const mockTasks: CrawlerTask[] = [
  {
    id: 1,
    bookId: "123456",
    chapterCount: 100,
    status: "completed",
    progress: 100,
    message: "采集完成",
    createTime: "2024-01-15 10:30:00",
  },
  {
    id: 2,
    bookId: "789012",
    chapterCount: 50,
    status: "running",
    progress: 65,
    message: "正在采集第65章",
    createTime: "2024-01-15 11:00:00",
  },
  {
    id: 3,
    bookId: "345678",
    chapterCount: 200,
    status: "pending",
    progress: 0,
    message: "等待中",
    createTime: "2024-01-15 11:05:00",
  },
];

export function Crawler() {
  const [form] = Form.useForm();
  const [tasks, setTasks] = useState<CrawlerTask[]>(mockTasks);
  const [loading, setLoading] = useState(false);

  // 处理采集
  const handleCrawl = async (values: {
    bookId: string;
    chapterCount?: number;
  }) => {
    setLoading(true);

    // 模拟API调用
    const newTask: CrawlerTask = {
      id: Math.max(...tasks.map((t) => t.id), 0) + 1,
      bookId: values.bookId,
      chapterCount: values.chapterCount || 0,
      status: "running",
      progress: 0,
      message: "开始采集...",
      createTime: new Date().toLocaleString(),
    };

    setTasks([newTask, ...tasks]);
    message.success("已添加采集任务");

    // 模拟采集进度
    let progress = 0;
    const interval = setInterval(() => {
      progress += Math.random() * 20;
      if (progress >= 100) {
        progress = 100;
        clearInterval(interval);
        setTasks((prev) =>
          prev.map((task) =>
            task.id === newTask.id
              ? {
                  ...task,
                  status: "completed" as const,
                  progress: 100,
                  message: "采集完成",
                }
              : task,
          ),
        );
        setLoading(false);
      } else {
        setTasks((prev) =>
          prev.map((task) =>
            task.id === newTask.id
              ? {
                  ...task,
                  progress: Math.round(progress),
                  message: `正在采集第${Math.round(progress)}章`,
                }
              : task,
          ),
        );
      }
    }, 500);
  };

  // 删除任务
  const handleDelete = (id: number) => {
    setTasks(tasks.filter((task) => task.id !== id));
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
        <List
          dataSource={tasks}
          renderItem={(task) => (
            <List.Item
              actions={[
                task.status === "running" ? (
                  <Button type="link" icon={<SyncOutlined spin />} disabled>
                    采集中
                  </Button>
                ) : task.status === "completed" ? (
                  <Button
                    type="link"
                    icon={<CheckCircleOutlined />}
                    style={{ color: "#52c41a" }}
                  >
                    完成
                  </Button>
                ) : (
                  <Button
                    type="link"
                    icon={<PlayCircleOutlined />}
                    onClick={() => {
                      setTasks((prev) =>
                        prev.map((t) =>
                          t.id === task.id
                            ? { ...t, status: "running" as const, progress: 0 }
                            : t,
                        ),
                      );
                    }}
                  >
                    开始
                  </Button>
                ),
                <Button
                  type="link"
                  danger
                  icon={<DeleteOutlined />}
                  onClick={() => handleDelete(task.id)}
                >
                  删除
                </Button>,
              ]}
            >
              <List.Item.Meta
                title={
                  <Space>
                    <span>小说ID: {task.bookId}</span>
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
                      {task.chapterCount > 0 && ` (共${task.chapterCount}章)`}
                    </div>
                    {task.status === "running" && (
                      <Progress
                        percent={task.progress}
                        size="small"
                        status="active"
                      />
                    )}
                  </div>
                }
              />
            </List.Item>
          )}
        />
      </Card>
    </div>
  );
}

export const Route = createFileRoute("/app/crawler/")({
  component: Crawler,
});
