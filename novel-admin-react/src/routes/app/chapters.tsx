import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import {
  Table,
  Button,
  Input,
  Space,
  Drawer,
  Form,
  InputNumber,
  Select,
  message,
  Popconfirm,
  Tag,
} from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'

// Mock 章节类型
interface Chapter {
  id: number
  chapterNum: number
  chapterName: string
  bookName: string
  wordCount: number
  isVip: number
  status: number
  createTime: string
}

// Mock 数据
const mockChapters: Chapter[] = Array.from({ length: 30 }, (_, i) => ({
  id: i + 1,
  chapterNum: i + 1,
  chapterName: `第${i + 1}章 ${['入门', '修炼', '突破', '历练', '危机'][i % 5]}`,
  bookName: `小说${(i % 5) + 1}`,
  wordCount: Math.floor(Math.random() * 5000) + 1000,
  isVip: i % 3 === 0 ? 1 : 0,
  status: 0,
  createTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
}))

const bookOptions = [
  { label: '小说1', value: '小说1' },
  { label: '小说2', value: '小说2' },
  { label: '小说3', value: '小说3' },
  { label: '小说4', value: '小说4' },
  { label: '小说5', value: '小说5' },
]

const isVipOptions = [
  { label: '免费', value: 0 },
  { label: 'VIP', value: 1 },
]

const statusOptions = [
  { label: '已发布', value: 0 },
  { label: '草稿', value: 1 },
]

export function Chapters() {
  const [data, setData] = useState<Chapter[]>(mockChapters)
  const [searchText, setSearchText] = useState('')
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [editingChapter, setEditingChapter] = useState<Chapter | null>(null)
  const [form] = Form.useForm()

  // 过滤数据
  const filteredData = data.filter(
    (item) =>
      item.chapterName.includes(searchText) || item.bookName.includes(searchText)
  )

  // 表格列定义
  const columns: ColumnsType<Chapter> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 60,
    },
    {
      title: '章节号',
      dataIndex: 'chapterNum',
      width: 80,
    },
    {
      title: '章节名',
      dataIndex: 'chapterName',
      width: 200,
    },
    {
      title: '所属小说',
      dataIndex: 'bookName',
      width: 120,
    },
    {
      title: '字数',
      dataIndex: 'wordCount',
      width: 80,
      render: (count: number) => count.toLocaleString(),
    },
    {
      title: 'VIP',
      dataIndex: 'isVip',
      width: 60,
      render: (isVip: number) => (
        <Tag color={isVip ? 'gold' : 'default'}>
          {isVip ? 'VIP' : '免费'}
        </Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (status: number) => {
        const colors = ['success', 'default']
        const labels = ['已发布', '草稿']
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
            title="确定删除这个章节吗？"
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
  const handleEdit = (record: Chapter) => {
    setEditingChapter(record)
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
    setEditingChapter(null)
    form.resetFields()
    setDrawerOpen(true)
  }

  // 处理保存
  const handleSave = async () => {
    const values = await form.validateFields()
    if (editingChapter) {
      // 编辑
      setData(
        data.map((item) =>
          item.id === editingChapter.id ? { ...item, ...values } : item
        )
      )
      message.success('更新成功')
    } else {
      // 新增
      const newChapter: Chapter = {
        id: Math.max(...data.map((d) => d.id)) + 1,
        ...values,
        createTime: new Date().toISOString().split('T')[0],
      }
      setData([newChapter, ...data])
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
      <h2 style={{ marginBottom: 16 }}>章节管理</h2>

      {/* 搜索和操作栏 */}
      <Space style={{ marginBottom: 16 }} wrap>
        <Input
          placeholder="搜索章节名或小说名"
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
        scroll={{ x: 1100 }}
      />

      {/* 抽屉表单 */}
      <Drawer
        title={editingChapter ? '编辑章节' : '新增章节'}
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
            name="chapterName"
            label="章节名称"
            rules={[{ required: true, message: '请输入章节名称' }]}
          >
            <Input placeholder="请输入章节名称" />
          </Form.Item>
          <Form.Item
            name="bookName"
            label="所属小说"
            rules={[{ required: true, message: '请选择小说' }]}
          >
            <Select placeholder="请选择小说" options={bookOptions} />
          </Form.Item>
          <Form.Item name="wordCount" label="字数">
            <InputNumber
              min={0}
              style={{ width: '100%' }}
              placeholder="请输入字数"
            />
          </Form.Item>
          <Form.Item name="isVip" label="是否VIP">
            <Select placeholder="请选择" options={isVipOptions} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select placeholder="请选择状态" options={statusOptions} />
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  )
}

export const Route = createFileRoute('/app/chapters')({
  component: Chapters,
})
