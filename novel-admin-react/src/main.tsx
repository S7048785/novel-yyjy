import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {createRouter, RouterProvider} from '@tanstack/react-router'
import './index.css'
import {queryClient} from './lib/queryClient.ts'
import {QueryClientProvider} from '@tanstack/react-query'
import {routeTree} from './routeTree.gen'
import {ConfigProvider, theme} from 'antd'
import {useThemeStore} from './lib/themeStore'

export const router = createRouter({
	routeTree,
	defaultPreload: 'intent',
	defaultViewTransition: true,
})

declare module '@tanstack/react-router' {
	interface Register {
		router: typeof router
	}
}

// 根布局组件
function RootLayout({children}: {children: React.ReactNode}) {
	const isDarkMode = useThemeStore((state) => state.isDarkMode)

	return (
		<ConfigProvider
			theme={{
				algorithm: isDarkMode ? theme.darkAlgorithm : theme.defaultAlgorithm,
			}}
		>
			<QueryClientProvider client={queryClient}>
				{children}
			</QueryClientProvider>
		</ConfigProvider>
	)
}

createRoot(document.getElementById('root')!).render(
	<StrictMode>
		<RootLayout>
			<RouterProvider router={router} />
		</RootLayout>
	</StrictMode>,
)
