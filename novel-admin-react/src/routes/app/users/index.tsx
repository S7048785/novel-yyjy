import {createFileRoute} from "@tanstack/react-router";
import {useState} from "react";
import {Table, Button, Input, Space, Popconfirm, message} from "antd";
import {PlusOutlined, SearchOutlined} from "@ant-design/icons";
import {useMutation, useQuery} from "@tanstack/react-query";
import {queryClient} from "@/lib/queryClient";
import {useUserColumns} from "./__components/columns.tsx";
import {UserDrawer} from "./__components/UserDrawer.tsx";
import {fetchUsers, saveUser, updateUser, deleteUser} from "./__api/api.ts";
import type {Dynamic_UserInfo} from "@/__generated/model/dynamic";

export function Users() {
	const [searchText, setSearchText] = useState("");
	const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
	const [drawerOpen, setDrawerOpen] = useState(false);
	const [editingUser, setEditingUser] = useState<Dynamic_UserInfo | null>(null);

	// 查询用户列表
	const {data, isLoading} = useQuery({
		queryKey: ["getUsers", searchText],
		queryFn: () => fetchUsers({
			email: searchText, endTime: "", sortField: "", startTime: "",
			pageSize: 10,
			pageNum: 1,
			sort: 0
		}),
	});

	// 保存/新增 mutation
	const saveMutation = useMutation({
		mutationFn: (user: Dynamic_UserInfo) =>
				user.id ? updateUser(user) : saveUser(user),
		onSuccess: () => {
			queryClient.invalidateQueries({queryKey: ["getUsers"]});
			message.success(editingUser ? "更新成功" : "添加成功");
			setDrawerOpen(false);
			setEditingUser(null);
		},
		onError: () => {
			message.error("操作失败");
		},
	});

	// 删除 mutation
	const deleteMutation = useMutation({
		mutationFn: (id: number) => deleteUser(id),
		onSuccess: () => {
			queryClient.invalidateQueries({queryKey: ["getUsers"]});
			message.success("删除成功");
		},
		onError: () => {
			message.error("删除失败");
		},
	});

	// 处理编辑
	const handleEdit = (record: Dynamic_UserInfo) => {
		setEditingUser(record);
		setDrawerOpen(true);
	};

	// 处理删除
	const handleDelete = (id: number) => {
		deleteMutation.mutate(id);
	};

	// 处理新增
	const handleAdd = () => {
		setEditingUser(null);
		setDrawerOpen(true);
	};

	// 处理保存
	const handleSave = (values: Dynamic_UserInfo) => {
		saveMutation.mutate({
			...editingUser,
			...values,
		} as Dynamic_UserInfo);
	};

	// 表格列
	const columns = useUserColumns({onEdit: handleEdit, onDelete: handleDelete});

	return (
			<div>
				<h2 style={{marginBottom: 16}}>用户管理</h2>

				{/* 搜索和操作栏 */}
				<Space style={{marginBottom: 16}} wrap>
					<Input
							placeholder="搜索邮箱"
							prefix={<SearchOutlined/>}
							value={searchText}
							onChange={(e) => setSearchText(e.target.value)}
							style={{width: 250}}
							allowClear
					/>
					<Button type="primary" icon={<PlusOutlined/>} onClick={handleAdd}>
						新增
					</Button>
					<Popconfirm
							title={`确定删除选中的 ${selectedRowKeys.length} 条记录吗？`}
							disabled={selectedRowKeys.length === 0}
					>
						<Button danger disabled={selectedRowKeys.length === 0}>
							批量删除
						</Button>
					</Popconfirm>
				</Space>

				{/* 表格 */}
				<Table
						rowSelection={{
							selectedRowKeys,
							onChange: setSelectedRowKeys,
						}}
						columns={columns}
						dataSource={data?.list || []}
						rowKey="id"
						loading={isLoading}
						pagination={{
							total: data?.total || 0,
							pageSize: 10,
							showSizeChanger: true,
							showTotal: (total) => `共 ${total} 条`,
						}}
						scroll={{x: 1000}}
				/>

				{/* 用户表单抽屉 */}
				<UserDrawer
						open={drawerOpen}
						onClose={() => {
							setDrawerOpen(false);
							setEditingUser(null);
						}}
						onSave={handleSave}
						editingUser={editingUser}
						loading={saveMutation.isPending}
				/>
			</div>
	);
}

export const Route = createFileRoute("/app/users/")({
	component: Users,
});
