import { createFileRoute } from '@tanstack/react-router'
import { Card, Row, Col, Statistic } from 'antd'
import {
  BookOutlined,
  UserOutlined,
  ReadOutlined,
  CloudDownloadOutlined,
} from '@ant-design/icons'

// Mock 数据
const stats = [
  {
    title: '小说总数',
    value: 1234,
    icon: <BookOutlined />,
    color: '#1890ff',
  },
  {
    title: '章节总数',
    value: 56789,
    icon: <ReadOutlined />,
    color: '#52c41a',
  },
  {
    title: '用户总数',
    value: 5678,
    icon: <UserOutlined />,
    color: '#faad14',
  },
  {
    title: '采集任务',
    value: 12,
    icon: <CloudDownloadOutlined />,
    color: '#f5222d',
  },
]

export function Dashboard() {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>仪表盘</h2>
      <Row gutter={[16, 16]}>
        {stats.map((stat, index) => (
          <Col xs={24} sm={12} lg={6} key={index}>
            <Card>
              <Statistic
                title={stat.title}
                value={stat.value}
                prefix={stat.icon}
                valueStyle={{ color: stat.color }}
              />
            </Card>
          </Col>
        ))}
      </Row>
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={12}>
          <Card title="最近活动" style={{ height: '100%' }}>
            <p>系统运行正常</p>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="系统信息" style={{ height: '100%' }}>
            <p>版本: 1.0.0</p>
            <p>最后更新: 2024-01-01</p>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export const Route = createFileRoute('/app/dashboard')({
  component: Dashboard,
})
