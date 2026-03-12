import { createFileRoute } from "@tanstack/react-router";
import { Card, Row, Col, Statistic, Button, type StatisticProps } from "antd";
import {
  BookOutlined,
  UserOutlined,
  ReadOutlined,
  CloudDownloadOutlined,
} from "@ant-design/icons";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../ApiInstance";
import { useEffect, useMemo, useState } from "react";
import CountUp from "react-countup";

const formatter: StatisticProps["formatter"] = (value) => (
  <CountUp end={value as number} separator="," />
);
export function Dashboard() {
  const { data, isLoading, refetch } = useQuery({
    queryKey: ["dashboardStats"],
    queryFn: async () => {
      const res = await api.adminController.dashboard();
      return res.data;
    },
  });
  const [stats, setStats] = useState([
    {
      title: "小说总数",
      value: data?.novelCount || 0,
      icon: <BookOutlined />,
      color: "#1890ff",
    },
    {
      title: "章节总数",
      value: data?.chapterCount || 0,
      icon: <ReadOutlined />,
      color: "#52c41a",
    },
    {
      title: "用户总数",
      value: data?.userCount || 0,
      icon: <UserOutlined />,
      color: "#faad14",
    },
    {
      title: "采集任务",
      value: 12,
      icon: <CloudDownloadOutlined />,
      color: "#f5222d",
    },
  ]);

  async function doRefetch() {
    const result = await refetch();
    // refetch 返回的最新数据在 result.data
    setStats([
      {
        title: "小说总数",
        value: result.data!.novelCount! + 1 || 0,
        icon: <BookOutlined />,
        color: "#1890ff",
      },
      {
        title: "章节总数",
        value: result.data?.chapterCount || 0,
        icon: <ReadOutlined />,
        color: "#52c41a",
      },
      {
        title: "用户总数",
        value: result.data?.userCount || 0,
        icon: <UserOutlined />,
        color: "#faad14",
      },
      {
        title: "采集任务",
        value: 12,
        icon: <CloudDownloadOutlined />,
        color: "#f5222d",
      },
    ]);
  }
  return (
    <div>
      <div className="flex mb-6">
        <h2>仪表盘</h2>
        <div className="ml-auto">
          <Button onClick={() => doRefetch()}>刷新</Button>
        </div>
      </div>
      <Row gutter={[16, 16]}>
        {stats.map((stat, index) => (
          <Col xs={24} sm={12} lg={6} key={index}>
            <Card>
              <Statistic
                title={stat.title}
                value={stat.value}
                prefix={stat.icon}
                valueStyle={{ color: stat.color }}
                formatter={formatter}
              />
            </Card>
          </Col>
        ))}
      </Row>
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={12}>
          <Card title="最近活动" style={{ height: "100%" }}>
            <p>系统运行正常</p>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="系统信息" style={{ height: "100%" }}>
            <p>版本: 1.0.0</p>
            <p>最后更新: 2026-03-12</p>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export const Route = createFileRoute("/app/dashboard")({
  component: Dashboard,
});
