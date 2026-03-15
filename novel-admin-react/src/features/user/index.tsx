import { useState } from "react";
import { Table, Button, Space, Form, Popconfirm, Tag, Avatar } from "antd";
import type { ColumnsType } from "antd/es/table";
import { EditOutlined, DeleteOutlined, UserOutlined } from "@ant-design/icons";
import { useMutation, useQuery } from "@tanstack/react-query";

import type { Dynamic_UserInfo } from "@/__generated/model/dynamic";
import { queryClient } from "@/lib/queryClient";
import UserSearch from "./components/UserSearch";
import UserDrawer from "./components/UserDrawer";
import { queryUser, saveUser, updateUser } from "./api";

export function Users() {
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<Dynamic_UserInfo | null>(null);
  const [form] = Form.useForm();
  const [imageUrl, setImageUrl] = useState<string>("");
  const [imageLoading, setImageLoading] = useState(false);
  const [pagination, setPagination] = useState({ pageNum: 1, pageSize: 10 });
  const [searchText, setSearchText] = useState("");

  const mutationSave = useMutation({
    mutationFn: (newData: Dynamic_UserInfo) => saveUser(newData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["getUsers"] });
    },
  });
  const mutationUpdate = useMutation({
    mutationFn: (newData: Dynamic_UserInfo) => updateUser(newData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["getUsers"] });
    },
  });

  // 表格列定义
  const columns: ColumnsType<Dynamic_UserInfo> = [
    {
      title: "ID",
      dataIndex: "id",
      width: 40,
    },
    {
      title: "头像",
      dataIndex: "userPhoto",
      width: 60,
      render: (value) => (
        <Avatar src={value} className="size-15!" icon={<UserOutlined />} />
      ),
    },
    {
      title: "邮箱",
      dataIndex: "email",
      width: 120,
    },
    {
      title: "昵称",
      dataIndex: "nickName",
      width: 60,
    },
    {
      title: "角色",
      dataIndex: "role",
      width: 60,
      render: (role: string) => (
        <Tag color={role === "admin" ? "red" : "blue"}>
          {role === "admin" ? "管理员" : "普通用户"}
        </Tag>
      ),
    },
    {
      title: "状态",
      dataIndex: "status",
      width: 40,
      render: (status: number) => {
        const colors = ["success", "error"];
        const labels = ["正常", "禁用"];
        return <Tag color={colors[status]}>{labels[status]}</Tag>;
      },
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      width: 80,
      render: (value) => new Date(value).toLocaleString(),
    },
    {
      title: "操作",
      key: "action",
      width: 120,
      fixed: "right",
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => {
              handleEdit(record);
            }}
          >
            编辑
          </Button>
          <Popconfirm title="确定删除这个用户吗？" onConfirm={() => {}}>
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 处理编辑
  const handleEdit = (record: Dynamic_UserInfo) => {
    setEditingUser(record);
    setImageUrl(record.userPhoto || "");
    form.setFieldsValue(record);
    setDrawerOpen(true);
  };

  // 处理删除
  const handleDelete = (id: number) => {};

  // 处理批量删除
  // const handleBatchDelete = () => {
  //   if (selectedRowKeys.length === 0) return;
  //   setData(data.filter((item) => !selectedRowKeys.includes(item.id)));
  //   setSelectedRowKeys([]);
  //   message.success(`已删除 ${selectedRowKeys.length} 条记录`);
  // };

  // 处理新增
  const handleAdd = () => {
    console.log(123);
    setEditingUser(null);
    setImageUrl("");
    form.resetFields();
    setDrawerOpen(true);
  };

  // 处理保存
  const handleSave = async () => {
    const values = await form.validateFields();
    if (editingUser) {
      // 编辑
      mutationUpdate.mutate({
        id: editingUser.id,
        ...values,
        userPhoto: imageUrl,
      });
    } else {
      console.log("保存用户", values);
      mutationSave.mutate(values);
    }
    setDrawerOpen(false);
    form.resetFields();
  };

  // 行选择配置
  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[]) => setSelectedRowKeys(keys),
  };

  const { data, refetch } = useQuery({
    queryKey: ["getUsers", pagination],
    queryFn: async () =>
      queryUser({
        searchText,
        pagination,
      }),
  });
  return (
    <div>
      <h2 style={{ marginBottom: 16 }}>用户管理</h2>
      {/* 搜索和操作栏 */}
      <UserSearch
        handleAdd={handleAdd}
        searchText={searchText}
        setSearchText={setSearchText}
        handleSearch={() => refetch()}
        selectedRowKeys={selectedRowKeys}
      />
      {/* 表格 */}
      <Table
        rowSelection={rowSelection}
        columns={columns}
        dataSource={data?.list || []}
        rowKey="id"
        pagination={{
          total: data?.total || 0,
          pageSize: pagination.pageSize,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => {
            setPagination({ pageNum: page, pageSize });
          },
        }}
        scroll={{ x: 1000 }}
      />
      {/* 抽屉表单 */}
      <UserDrawer
        editingUser={editingUser}
        setDrawerOpen={setDrawerOpen}
        handleSave={handleSave}
        imageLoading={imageLoading}
        setImageLoading={setImageLoading}
        imageUrl={imageUrl}
        setImageUrl={setImageUrl}
        drawerOpen={drawerOpen}
        form={form}
      />
    </div>
  );
}
