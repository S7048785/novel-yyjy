import {createFileRoute} from "@tanstack/react-router";
import {useState} from "react";
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
	Avatar, type UploadProps, type GetProp, Upload,
} from "antd";
import type {ColumnsType} from "antd/es/table";
import {
	PlusOutlined,
	SearchOutlined,
	EditOutlined,
	DeleteOutlined,
	UserOutlined,
	LoadingOutlined
} from "@ant-design/icons";
import {useMutation, useQuery} from "@tanstack/react-query";
import {api} from "@/ApiInstance.ts";

const BASE_URL = import.meta.env.VITE_API_BASE_URL
const roleOptions = [
	{label: "user", value: "user"},
	{label: "admin", value: "admin"},
];

const statusOptions = [
	{label: "正常", value: 0},
	{label: "禁用", value: 1},
];
import type {Dynamic_UserInfo} from "@/__generated/model/dynamic";
import {queryClient} from "@/lib/queryClient";

async function updateUser(newUser: Dynamic_UserInfo) {
	await api.userManagementController.update({
		body: {
			id: newUser.id!,
			email: newUser.email!,
			nickName: newUser.nickName!,
			role: newUser.role!,
			status: newUser.status!,
			userPhoto: newUser.userPhoto!,
		}
	})
}

async function saveUser(user: Dynamic_UserInfo) {
	await api.userManagementController.add({
		body: {
			email: user.email!,
			password: user.password!,
			nickName: user.nickName!,
			role: user.role!,
			status: user.status!,
			userPhoto: user.userPhoto!,
		}
	})
}

type FileType = Parameters<GetProp<UploadProps, 'beforeUpload'>>[0];

const avatarBeforeUpload = (file: FileType) => {
	const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
	if (!isJpgOrPng) {
		message.error("仅支持上传 JPG/PNG 格式的图片");
	}
	const isLt1M = file.size / 1024 / 1024 < 1;
	if (!isLt1M) {
		message.error("图片大小必须小于 1MB!");
	}
	return isJpgOrPng && isLt1M;
};

export function Users() {
	const [searchText, setSearchText] = useState("");
	const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
	const [drawerOpen, setDrawerOpen] = useState(false);
	const [editingUser, setEditingUser] = useState<Dynamic_UserInfo | null>(null);
	const [form] = Form.useForm();
	const [imageUrl, setImageUrl] = useState<string>("")
	const [imageLoading, setImageLoading] = useState(false);

	const mutationSave = useMutation({
		mutationFn: (newData: Dynamic_UserInfo) => saveUser(newData),
		onSuccess: () => {
			queryClient.invalidateQueries({queryKey: ["getUsers"]});
		},
	});
	const mutationUpdate = useMutation({
		mutationFn: (newData: Dynamic_UserInfo) => updateUser(newData),
		onSuccess: () => {
			queryClient.invalidateQueries({queryKey: ["getUsers"]});
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
					<Avatar src={value} className="size-15!" icon={<UserOutlined/>}/>
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
								icon={<EditOutlined/>}
								onClick={() => {
									handleEdit(record);
								}}
						>
							编辑
						</Button>
						<Popconfirm title="确定删除这个用户吗？" onConfirm={() => {
						}}>
							<Button type="link" danger icon={<DeleteOutlined/>}>
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
    setImageUrl(record.userPhoto || "")
		form.setFieldsValue(record);
		setDrawerOpen(true);
	};

	// 处理删除
	// const handleDelete = (id: number) => {
	//   setData(data.filter((item) => item.id !== id));
	//   message.success("删除成功");
	// };

	// 处理批量删除
	// const handleBatchDelete = () => {
	//   if (selectedRowKeys.length === 0) return;
	//   setData(data.filter((item) => !selectedRowKeys.includes(item.id)));
	//   setSelectedRowKeys([]);
	//   message.success(`已删除 ${selectedRowKeys.length} 条记录`);
	// };

	// 处理新增
	const handleAdd = () => {
		setEditingUser(null);
		setImageUrl("")
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
			})

		} else {
			console.log("保存用户", values)
			mutationSave.mutate(values)
			// 新增
			// const newUser: User = {
			//   id: Math.max(...data.map((d) => d.id)) + 1,
			//   ...values,
			//   avatar: "",
			//   createTime: new Date().toISOString().split("T")[0],
			// };
			// // setData([newUser, ...data]);
			// message.success("添加成功");
		}
		setDrawerOpen(false);
		form.resetFields();
	};

	const handleAvatarChange: UploadProps['onChange'] = (info) => {
		if (info.file.status === 'uploading') {
			setImageLoading(true);
			return;
		}
		if (info.file.status === 'done') {
			setImageLoading(false);
			setImageUrl(info.file.response.data)
		}
	};

	const uploadButton = (
			<button style={{border: 0, background: 'none'}} type="button">
				{imageLoading ? <LoadingOutlined/> : <PlusOutlined/>}
				<div style={{marginTop: 8}}>Upload</div>
			</button>
	);
	// 行选择配置
	const rowSelection = {
		selectedRowKeys,
		onChange: (keys: React.Key[]) => setSelectedRowKeys(keys),
	};

	console.log("@@@@@@@@@@@@@@@@@")

	const {data} = useQuery({
		queryKey: ["getUsers"],
		queryFn: async () => {
			// 这里可以替换成实际的 API 请求
			const res = await api.userManagementController.page({
				req: {
					email: "",
					pageSize: 10,
					pageNum: 1,
					sort: 0,
					startTime: "",
					endTime: "",
					sortField: "",
				},
			});
			return res.data;
		},
	});
	return (
			<div>
				<h2 style={{marginBottom: 16}}>用户管理</h2>

				{/* 搜索和操作栏 */}
				<Space style={{marginBottom: 16}} wrap>
					<Input
							placeholder="搜索用户名或昵称"
							prefix={<SearchOutlined/>}
							value={searchText}
							onChange={(e) => setSearchText(e.target.value)}
							style={{width: 250}}
					/>
					<Button type="primary" icon={<PlusOutlined/>} onClick={handleAdd}>
						新增
					</Button>
					<Popconfirm
							title={`确定删除选中的 ${selectedRowKeys.length} 条记录吗？`}
							onConfirm={() => {
							}}
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
						dataSource={data?.list || []}
						rowKey="id"
						pagination={{
							total: data?.total || 0,
							pageSize: 10,
							showSizeChanger: true,
							showTotal: (total) => `共 ${total} 条`,
						}}
						scroll={{x: 1000}}
				/>

				{/* 抽屉表单 */}
				<Drawer
						title={editingUser ? "编辑用户" : "新增用户"}
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
								name="nickName"
								label="昵称"
								rules={[{required: true, message: "请输入昵称"}]}
						>
							<Input placeholder="请输入昵称"/>
						</Form.Item>
						<Form.Item
								name="email"
								label="邮箱"
								rules={[
									{required: true, type: "email", message: "请输入有效的邮箱"},
								]}
						>
							<Input placeholder="请输入邮箱"/>
						</Form.Item>
						{!editingUser && (
								<Form.Item
										name="password"
										label="密码"
										rules={[{required: true, message: "请输入密码"}]}
								>
									<Input.Password placeholder="请输入密码"/>
								</Form.Item>
						)}
						<Form.Item name="role" label="角色" rules={[{required: true}]}>
							<Select placeholder="请选择角色" options={roleOptions}/>
						</Form.Item>
						<Form.Item name="status" label="状态" rules={[{required: true}]}>
							<Select placeholder="请选择状态" options={statusOptions}/>
						</Form.Item>
						<Form.Item name="userPhoto" label="头像" rules={[{required: false}]}>
							<Upload
									name="file"
									listType="picture-card"
									className="avatar-uploader"
									showUploadList={false}
									action={BASE_URL + "/admin/user/upload"}
									withCredentials={true}
									beforeUpload={avatarBeforeUpload}
									onChange={handleAvatarChange}
							>
								{imageUrl ? (
										<img draggable={false} src={imageUrl} alt="avatar" style={{width: '100%'}}/>
								) : (
										uploadButton
								)}
							</Upload>

						</Form.Item>
					</Form>
				</Drawer>
			</div>
	);
}

export const Route = createFileRoute("/app/users/")({
	component: Users,
});
