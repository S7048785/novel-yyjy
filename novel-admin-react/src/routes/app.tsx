import {createFileRoute, Link, Outlet} from '@tanstack/react-router'
import {Button, Layout, Menu} from 'antd'
import {
	DashboardOutlined,
	BookOutlined,
	ReadOutlined,
	UserOutlined,
	CloudDownloadOutlined,MenuFoldOutlined,
	MenuUnfoldOutlined,

} from '@ant-design/icons'
import type {MenuProps} from 'antd'
import {ProgressBar} from "../components/ProgressBar.tsx";
import {ThemeToggle} from "../components/ThemeToggle.tsx";
import {useState} from "react";

const {Header, Sider, Content} = Layout

// 菜单项
const menuItems: MenuProps['items'] = [
	{
		key: '/app/dashboard',
		icon: <DashboardOutlined/>,
		label: <Link to="/app/dashboard">仪表盘</Link>,
	},
	{
		key: '/app/books',
		icon: <BookOutlined/>,
		label: <Link to="/app/books">小说管理</Link>,
	},
	{
		key: '/app/chapters',
		icon: <ReadOutlined/>,
		label: <Link to="/app/chapters">章节管理</Link>,
	},
	{
		key: '/app/users',
		icon: <UserOutlined/>,
		label: <Link to="/app/users">用户管理</Link>,
	},
	{
		key: '/app/crawler',
		icon: <CloudDownloadOutlined/>,
		label: <Link to="/app/crawler">小说采集</Link>,
	},
]

// 布局组件
export function AppLayout() {
	const [collapsed, setCollapsed] = useState(false);
	return (
			<Layout style={{minHeight: '100vh'}} >
				<ProgressBar />
				<Sider
						collapsible collapsed={collapsed} trigger={null}
						width={220}
				>
					<div
							style={{
								height: 64,
								display: 'flex',
								alignItems: 'center',
								justifyContent: 'center',
								fontSize: 18,
								fontWeight: 'bold',
								color: '#1677ff',
							}}
					>
						{!collapsed ? '小说管理系统' : ''}
					</div>
					<Menu
							theme="dark"
							mode="inline"
							defaultSelectedKeys={['/app/dashboard']}
							items={menuItems}
							style={{borderRight: 0}}
					/>

				</Sider>

				<Layout>
					<Header
							className="flex items-center justify-between gap-4"
							style={{padding: "0"}}
					>
						<div>
							<Button
									type="text"
									icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
									onClick={() => setCollapsed(!collapsed)}
									style={{
										fontSize: '16px',
										width: 64,
										height: 64,
									}}
							/>
						</div>
						<div className="mr-10">
							<ThemeToggle />
							<span style={{color: '#666'}}>管理员</span>
						</div>
					</Header>
					<Content
							style={{
								padding: 24,
								minHeight: 'calc(100vh - 64px)',
							}}
					>
						<div
								className="content-wrapper"
								style={{
									borderRadius: 8,
									minHeight: '100%',
								}}
						>
							<Outlet />
						</div>
					</Content>
				</Layout>
			</Layout>
	)
}

// App 布局路由
export const Route = createFileRoute('/app')({
	component: AppLayout,
})
