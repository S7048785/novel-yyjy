import {createRootRoute,  Outlet} from '@tanstack/react-router'
// 根路由组件
function RootComponent() {
  return <Outlet />
}

// 根路由
export const Route = createRootRoute({
  component: RootComponent,

})

