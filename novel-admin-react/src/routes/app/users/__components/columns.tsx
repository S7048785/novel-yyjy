import {Button, Space, Tag, Avatar} from "antd";
import type {ColumnsType} from "antd/es/table";
import {EditOutlined, DeleteOutlined, UserOutlined} from "@ant-design/icons";
import type {Dynamic_UserInfo} from "@/__generated/model/dynamic";

interface UserColumnsProps {
	onEdit: (record: Dynamic_UserInfo) => void;
	onDelete: (id: number) => void;
}

export function useUserColumns({onEdit, onDelete}: UserColumnsProps): ColumnsType<Dynamic_UserInfo> {
	return [
		{
			title: "ID",
			dataIndex: "id",
			width: 40,
		},
		{
			title: "头像",
			dataIndex: "userPhoto",
			width: 70,
			render: (value: string) => (
				<Avatar src={value} size={50} icon={<UserOutlined/>}/>
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
			width: 100,
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
			width: 60,
			render: (status: number) => {
				const config = [
					{color: "success", label: "正常"},
					{color: "error", label: "禁用"},
				];
				const item = config[status] || config[0];
				return <Tag color={item.color}>{item.label}</Tag>;
			},
		},
		{
			title: "创建时间",
			dataIndex: "createTime",
			width: 160,
			render: (value: string) => new Date(value).toLocaleString(),
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
						size="small"
						icon={<EditOutlined/>}
						onClick={() => onEdit(record)}
					>
						编辑
					</Button>
					<Button
						type="link"
						size="small"
						danger
						icon={<DeleteOutlined/>}
						onClick={() => onDelete(Number(record.id))}
					>
						删除
					</Button>
				</Space>
			),
		},
	];
}
