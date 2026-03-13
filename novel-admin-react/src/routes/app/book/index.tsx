import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import {
  Table,
  Button,
  Input,
  Space,
  Drawer,
  InputNumber,
  Select,
  message,
  Popconfirm,
  Tag,
  Avatar,
  Card,
  Form,
  Row,
  Col,
  Image,
} from "antd";
import type { ColumnsType } from "antd/es/table";
import {
  PlusOutlined,
  SearchOutlined,
  EditOutlined,
  DeleteOutlined,
  BookOutlined,
  LoadingOutlined,
  ReloadOutlined,
} from "@ant-design/icons";
import { useMutation, useQuery } from "@tanstack/react-query";
import { api } from "@/ApiInstance";
import type { BookUpdateReq } from "@/__generated/model/static/BookUpdateReq";
import { queryClient } from "@/lib/queryClient";
import type { BookInfoDto } from "@/__generated/model/dto";

const statusOptions = [
  { label: "连载中", value: 0 },
  { label: "已完结", value: 1 },
];

const workDirectionOptions = [
  { label: "男频", value: 0 },
  { label: "女频", value: 1 },
];

async function saveBook(book: BookUpdateReq) {
  await api.bookManagementController.add({ body: book });
}

async function updateBook(book: BookUpdateReq) {
  await api.bookManagementController.update({ body: book });
}

async function deleteBook(id: string) {
  await api.bookManagementController.delete({ id });
}

export function Books() {
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [editingBook, setEditingBook] = useState<
    BookInfoDto["BookManagementController/BOOK_VIEW_OF_ADMIN"] | null
  >(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [imageUrl, setImageUrl] = useState<string>("");
  const [pagination, setPagination] = useState({ pageNum: 1, pageSize: 10 });
  const [searchParams, setSearchParams] = useState({
    bookName: "",
    authorName: "",
    categoryId: undefined as string | undefined,
    bookStatus: undefined as number | undefined,
    workDirection: undefined as number | undefined,
  });

  // 获取分类列表
  const { data: categories } = useQuery({
    queryKey: ["getCategories"],
    queryFn: async () => {
      const res = await api.bookController.listCategory({});
      return res.data;
    },
  });

  const categoryOptions = categories?.map((c) => ({
    label: c.name,
    value: c.id,
  }));

  // 获取书籍列表
  const { data, isLoading } = useQuery({
    queryKey: ["getBooks", pagination, searchParams],
    queryFn: async () => {
      const res = await api.bookManagementController.page({
        req: {
          authorName: searchParams.authorName,
          bookName: searchParams.bookName,
          bookStatus: searchParams.bookStatus,
          categoryId: undefined,
          pageNum: pagination.pageNum,
          pageSize: pagination.pageSize,
        },
      });
      return res.data;
    },
  });

  const mutationSave = useMutation({
    mutationFn: (newData: BookUpdateReq) => saveBook(newData),
    onSuccess: () => {
      message.success("新增成功");
      queryClient.invalidateQueries({ queryKey: ["getBooks"] });
      setDrawerOpen(false);
    },
  });

  const mutationUpdate = useMutation({
    mutationFn: (newData: BookUpdateReq) => updateBook(newData),
    onSuccess: () => {
      message.success("更新成功");
      queryClient.invalidateQueries({ queryKey: ["getBooks"] });
      setDrawerOpen(false);
    },
  });

  const mutationDelete = useMutation({
    mutationFn: (id: string) => deleteBook(id),
    onSuccess: () => {
      message.success("删除成功");
      queryClient.invalidateQueries({ queryKey: ["getBooks"] });
    },
  });

  // 表格列定义
  const columns: ColumnsType<
    BookInfoDto["BookManagementController/BOOK_VIEW_OF_ADMIN"]
  > = [
    {
      title: "ID",
      dataIndex: "id",
      width: 80,
    },
    {
      title: "封面",
      dataIndex: "picUrl",
      width: 80,
      render: (value) => (
        <Image
          width={60}
          alt="basic"
          src={value}
          fallback={
            "data:image/png;base64,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"
          }
        />
      ),
    },
    {
      title: "书名",
      dataIndex: "bookName",
      width: 150,
    },
    {
      title: "分类",
      dataIndex: "categoryName",
      width: 100,
      render: (name: string) => <Tag color="blue">{name}</Tag>,
    },

    {
      title: "字数",
      dataIndex: "wordCount",
      width: 80,
    },

    {
      title: "点击量",
      dataIndex: "visitCount",
      width: 80,
    },
    {
      title: "评分",
      dataIndex: "score",
      width: 80,
      render: (score: number) => (score / 10).toFixed(1),
    },
    {
      title: "作者",
      dataIndex: "authorName",
      width: 100,
    },
    {
      title: "状态",
      dataIndex: "bookStatus",
      width: 80,
      render: (status: number) => {
        const colors = ["processing", "success"];
        const labels = ["连载中", "已完结"];
        return <Tag color={colors[status]}>{labels[status]}</Tag>;
      },
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      width: 150,
      render: (value) => (value ? new Date(value).toLocaleString() : "-"),
    },
    {
      title: "操作",
      key: "action",
      width: 150,
      fixed: "right",
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
            onConfirm={() => {
              console.log("确定删除这本书吗？", record.id);
              mutationDelete.mutate(record.id);
            }}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 处理编辑
  const handleEdit = (
    record: BookInfoDto["BookManagementController/BOOK_VIEW_OF_ADMIN"],
  ) => {
    setEditingBook(record);
    setImageUrl(record.picUrl || "");
    form.setFieldsValue({
      ...record,
      // 如果后端返回的 id 是 string，但 update 需要 number
      id: Number(record.id),
    });
    setDrawerOpen(true);
  };

  // 处理新增
  const handleAdd = () => {
    setEditingBook(null);
    setImageUrl("");
    form.resetFields();
    setDrawerOpen(true);
  };

  // 处理保存
  const handleSave = async () => {
    const values = await form.validateFields();
    const bookData: BookUpdateReq = {
      ...values,
      picUrl: imageUrl,
    };

    if (editingBook) {
      mutationUpdate.mutate({
        ...bookData,
        id: editingBook.id,
      });
    } else {
      mutationSave.mutate(bookData);
    }
  };

  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    console.log(values);
    setSearchParams({
      bookName: values.bookName || "",
      authorName: values.authorName || "",
      categoryId: values.categoryId || undefined,
      bookStatus: values.bookStatus || undefined,
      workDirection: values.workDirection || undefined,
    });
    setPagination({ ...pagination, pageNum: 1 });
  };

  const handleReset = () => {
    searchForm.resetFields();
    setSearchParams({
      bookName: "",
      authorName: "",
      categoryId: undefined,
      bookStatus: undefined,
      workDirection: undefined,
    });
    setPagination({ ...pagination, pageNum: 1 });
  };

  return (
    <div>
      <h2 style={{ marginBottom: 16 }}>小说管理</h2>

      {/* 多条件查询表单 */}
      <Card style={{ marginBottom: 16 }}>
        <Form form={searchForm} layout="inline" onFinish={handleSearch}>
          <Row gutter={[16, 16]} style={{ width: "100%" }}>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="bookName" label="书名">
                <Input placeholder="请输入书名" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="authorName" label="作者">
                <Input placeholder="请输入作者名" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="categoryId" label="分类">
                <Select
                  placeholder="请选择分类"
                  options={categoryOptions}
                  allowClear
                  style={{ width: "100%" }}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="bookStatus" label="状态">
                <Select
                  placeholder="请选择状态"
                  options={statusOptions}
                  allowClear
                  style={{ width: "100%" }}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="workDirection" label="频道">
                <Select
                  placeholder="请选择频道"
                  options={workDirectionOptions}
                  allowClear
                  style={{ width: "100%" }}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={24} md={24} lg={18} style={{ textAlign: "right" }}>
              <Space>
                <Button
                  type="primary"
                  icon={<SearchOutlined />}
                  onClick={handleSearch}
                >
                  查询
                </Button>
                <Button icon={<ReloadOutlined />} onClick={handleReset}>
                  重置
                </Button>
                <Button
                  type="primary"
                  icon={<PlusOutlined />}
                  onClick={handleAdd}
                >
                  新增
                </Button>
              </Space>
            </Col>
          </Row>
        </Form>
      </Card>

      {/* 表格 */}
      <Table
        loading={isLoading}
        columns={columns}
        dataSource={data?.list || []}
        rowKey="id"
        pagination={{
          current: pagination.pageNum,
          pageSize: pagination.pageSize,
          total: data?.total || 0,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => {
            setPagination({ pageNum: page, pageSize });
          },
        }}
        scroll={{ x: 1000 }}
      />

      {/* 抽屉表单 */}
      <Drawer
        title={editingBook ? "编辑小说" : "新增小说"}
        width={560}
        onClose={() => setDrawerOpen(false)}
        open={drawerOpen}
        extra={
          <Space>
            <Button onClick={() => setDrawerOpen(false)}>取消</Button>
            <Button
              type="primary"
              onClick={handleSave}
              loading={mutationSave.isPending || mutationUpdate.isPending}
            >
              保存
            </Button>
          </Space>
        }
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="bookName"
            label="书名"
            rules={[{ required: true, message: "请输入书名" }]}
          >
            <Input placeholder="请输入书名" />
          </Form.Item>
          <Form.Item
            name="authorName"
            label="作者"
            rules={[{ required: true, message: "请输入作者" }]}
          >
            <Input placeholder="请输入作者" />
          </Form.Item>
          <Form.Item
            name="categoryId"
            label="分类"
            rules={[{ required: true, message: "请选择分类" }]}
          >
            <Select placeholder="请选择分类" options={categoryOptions} />
          </Form.Item>
          <Form.Item
            name="bookStatus"
            label="状态"
            rules={[{ required: true, message: "请选择状态" }]}
          >
            <Select placeholder="请选择状态" options={statusOptions} />
          </Form.Item>
          <Form.Item
            name="bookDesc"
            label="简介"
            rules={[{ required: true, message: "请输入简介" }]}
          >
            <Input.TextArea rows={4} placeholder="请输入简介" />
          </Form.Item>
          <Form.Item label="封面" name="picUrl">
            <Input placeholder="请输入封面url" />
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  );
}

export const Route = createFileRoute("/app/book/")({
  component: Books,
});
