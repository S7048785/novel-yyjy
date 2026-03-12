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

// Mock 书籍类型
interface Book {
  id: number
  bookName: string
  author: string
  category: string
  wordCount: number
  status: number
  createTime: string
}

// Mock 数据
const mockBooks: Book[] = Array.from({ length: 20 }, (_, i) => ({
  id: i + 1,
  bookName: `小说${i + 1}`,
  author: `作者${String.fromCharCode(65 + (i % 26))}`,
  category: ['玄幻', '仙侠', '都市', '历史', '科幻'][i % 5],
  wordCount: Math.floor(Math.random() * 1000000) + 100000,
  status: i % 3,
  createTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
}))

const categories = [
  { label: '玄幻', value: '玄幻' },
  { label: '仙侠', value: '仙侠' },
  { label: '都市', value: '都市' },
  { label: '历史', value: '历史' },
  { label: '科幻', value: '科幻' },
]

const statusOptions = [
  { label: '连载中', value: 0 },
  { label: '已完结', value: 1 },
  { label: '暂停', value: 2 },
]

export function Books() {
  const [data, setData] = useState<Book[]>(mockBooks)
  const [searchText, setSearchText] = useState('')
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [editingBook, setEditingBook] = useState<Book | null>(null)
  const [form] = Form.useForm()

  // 过滤数据
  const filteredData = data.filter((item) =>
    item.bookName.includes(searchText) || item.author.includes(searchText)
  )

  // 表格列定义
  const columns: ColumnsType<Book> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 60,
    },
    {
      title: '书名',
      dataIndex: 'bookName',
      width: 150,
    },
    {
      title: '作者',
      dataIndex: 'author',
      width: 100,
    },
    {
      title: '分类',
      dataIndex: 'category',
      width: 80,
      render: (category: string) => <Tag color="blue">{category}</Tag>,
    },
    {
      title: '字数',
      dataIndex: 'wordCount',
      width: 100,
      render: (count: number) => count.toLocaleString(),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (status: number) => {
        const colors = ['processing', 'success', 'default']
        const labels = ['连载中', '已完结', '暂停']
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
            title="确定删除这本书吗？"
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
  const handleEdit = (record: Book) => {
    setEditingBook(record)
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
    setEditingBook(null)
    form.resetFields()
    setDrawerOpen(true)
  }

  // 处理保存
  const handleSave = async () => {
    const values = await form.validateFields()
    if (editingBook) {
      // 编辑
      setData(
        data.map((item) =>
          item.id === editingBook.id ? { ...item, ...values } : item
        )
      )
      message.success('更新成功')
    } else {
      // 新增
      const newBook: Book = {
        id: Math.max(...data.map((d) => d.id)) + 1,
        ...values,
        createTime: new Date().toISOString().split('T')[0],
      }
      setData([newBook, ...data])
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
      <h2 style={{ marginBottom: 16 }}>小说管理</h2>

      {/* 搜索和操作栏 */}
      <Space style={{ marginBottom: 16 }} wrap>
        <Input
          placeholder="搜索书名或作者"
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
        title={editingBook ? '编辑小说' : '新增小说'}
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
            name="bookName"
            label="书名"
            rules={[{ required: true, message: '请输入书名' }]}
          >
            <Input placeholder="请输入书名" />
          </Form.Item>
          <Form.Item
            name="author"
            label="作者"
            rules={[{ required: true, message: '请输入作者' }]}
          >
            <Input placeholder="请输入作者" />
          </Form.Item>
          <Form.Item
            name="category"
            label="分类"
            rules={[{ required: true, message: '请选择分类' }]}
          >
            <Select placeholder="请选择分类" options={categories} />
          </Form.Item>
          <Form.Item name="wordCount" label="字数">
            <InputNumber
              min={0}
              style={{ width: '100%' }}
              placeholder="请输入字数"
            />
          </Form.Item>
          <Form.Item
            name="status"
            label="状态"
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select placeholder="请选择状态" options={statusOptions} />
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  )
}

export const Route = createFileRoute('/app/books')({
  component: Books,
})
