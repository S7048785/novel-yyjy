import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import {
  Table,
  Button,
  Input,
  Space,
  Drawer,
  Form,
  Select,
  message,
  Popconfirm,
  Tag,
  Avatar,
} from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined, UserOutlined } from '@ant-design/icons'

// Mock 用户类型
interface User {
  id: number
  username: string
  nickName: string
  avatar: string
  email: string
  role: number
  status: number
  createTime: string
}

// Mock 数据
const mockUsers: User[] = Array.from({ length: 15 }, (_, i) => ({
  id: i + 1,
  username: `user${i + 1}`,
  nickName: `用户${i + 1}`,
  avatar: '',
  email: `user${i + 1}@example.com`,
  role: i === 0 ? 0 : 1,
  status: i % 4 === 0 ? 1 : 0,
  createTime: new Date(Date.now() - Math.random() * 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
}))

const roleOptions = [
  { label: '管理员', value: 0 },
  { label: '普通用户', value: 1 },
]

const statusOptions = [
  { label: '正常', value: 0 },
  { label: '禁用', value: 1 },
]

export function Users() {
  const [data, setData] = useState<User[]>(mockUsers)
  const [searchText, setSearchText] = useState('')
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [editingUser, setEditingUser] = useState<User | null>(null)
  const [form] = Form.useForm()

  // 过滤数据
  const filteredData = data.filter(
    (item) =>
      item.username.includes(searchText) || item.nickName.includes(searchText)
  )

  // 表格列定义
  const columns: ColumnsType<User> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 60,
    },
    {
      title: '头像',
      dataIndex: 'avatar',
      width: 70,
      render: () => <Avatar icon={<UserOutlined />} />,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      width: 120,
    },
    {
      title: '昵称',
      dataIndex: 'nickName',
      width: 120,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      width: 180,
    },
    {
      title: '角色',
      dataIndex: 'role',
      width: 80,
      render: (role: number) => (
        <Tag color={role === 0 ? 'red' : 'blue'}>
          {role === 0 ? '管理员' : '普通用户'}
        </Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (status: number) => {
        const colors = ['success', 'error']
        const labels = ['正常', '禁用']
        return <Tag color={colors[status]}>{labels[status]}</Tag>
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 120,
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除这个用户吗？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  // 处理编辑
  const handleEdit = (record: User) => {
    setEditingUser(record)
    form.setFieldsValue(record)
    setDrawerOpen(true)
  }

  // 处理删除
  const handleDelete = (id: number) => {
    setData(data.filter((item) => item.id !== id))
    message.success('删除成功')
  }

  // 处理批量删除
  const handleBatchDelete = () => {
    if (selectedRowKeys.length === 0) return
    setData(data.filter((item) => !selectedRowKeys.includes(item.id)))
    setSelectedRowKeys([])
    message.success(`已删除 ${selectedRowKeys.length} 条记录`)
  }

  // 处理新增
  const handleAdd = () => {
    setEditingUser(null)
    form.resetFields()
    setDrawerOpen(true)
  }

  // 处理保存
  const handleSave = async () => {
    const values = await form.validateFields()
    if (editingUser) {
      // 编辑
      setData(
        data.map((item) =>
          item.id === editingUser.id ? { ...item, ...values } : item
        )
      )
      message.success('更新成功')
    } else {
      // 新增
      const newUser: User = {
        id: Math.max(...data.map((d) => d.id)) + 1,
        ...values,
        avatar: '',
        createTime: new Date().toISOString().split('T')[0],
      }
      setData([newUser, ...data])
      message.success('添加成功')
    }
    setDrawerOpen(false)
    form.resetFields()
  }

  // 行选择配置
  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[]) => setSelectedRowKeys(keys),
  }

  return (
    <div>
      <h2 style={{ marginBottom: 16 }}>用户管理</h2>

      {/* 搜索和操作栏 */}
      <Space style={{ marginBottom: 16 }} wrap>
        <Input
          placeholder="搜索用户名或昵称"
          prefix={<SearchOutlined />}
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          style={{ width: 250 }}
        />
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增
        </Button>
        <Popconfirm
          title={`确定删除选中的 ${selectedRowKeys.length} 条记录吗？`}
          onConfirm={handleBatchDelete}
          disabled={selectedRowKeys.length === 0}
        >
          <Button danger disabled={selectedRowKeys.length === 0}>
            批量删除
          </Button>
        </Popconfirm>
      </Space>

      {/* 表格 */}
      <Table
        rowSelection={rowSelection}
        columns={columns}
        dataSource={filteredData}
        rowKey="id"
        pagination={{
          total: filteredData.length,
          pageSize: 10,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
        scroll={{ x: 1000 }}
      />

      {/* 抽屉表单 */}
      <Drawer
        title={editingUser ? '编辑用户' : '新增用户'}
        width={480}
        onClose={() => setDrawerOpen(false)}
        open={drawerOpen}
        extra={
          <Space>
            <Button onClick={() => setDrawerOpen(false)}>取消</Button>
            <Button type="primary" onClick={handleSave}>
              保存
            </Button>
          </Space>
        }
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input placeholder="请输入用户名" disabled={!!editingUser} />
          </Form.Item>
          <Form.Item
            name="nickName"
            label="昵称"
            rules={[{ required: true, message: '请输入昵称' }]}
          >
            <Input placeholder="请输入昵称" />
          </Form.Item>
          <Form.Item name="email" label="邮箱" rules={[{ type: 'email', message: '请输入有效的邮箱' }]}>
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          {!editingUser && (
            <Form.Item
              name="password"
              label="密码"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password placeholder="请输入密码" />
            </Form.Item>
          )}
          <Form.Item name="role" label="角色">
            <Select placeholder="请选择角色" options={roleOptions} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select placeholder="请选择状态" options={statusOptions} />
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  )
}

export const Route = createFileRoute('/app/users')({
  component: Users,
})
