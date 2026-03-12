import {Button, Tooltip} from 'antd'
import {SunOutlined, MoonOutlined} from '@ant-design/icons'
import {useThemeStore} from '../lib/themeStore'

export function ThemeToggle() {
  const {isDarkMode, toggleTheme} = useThemeStore()

  return (
    <Tooltip title={isDarkMode ? '切换到浅色模式' : '切换到深色模式'}>
      <Button
        type="text"
        children={isDarkMode ? '浅色模式' : '深色模式'}
        icon={isDarkMode ? <SunOutlined /> : <MoonOutlined />}
        onClick={toggleTheme}
      />
    </Tooltip>
  )
}
