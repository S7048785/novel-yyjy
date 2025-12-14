import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import UnoCSS from 'unocss/vite'
import vueDevTools from 'vite-plugin-vue-devtools'

import Components from 'unplugin-vue-components/vite'
import AutoImport from 'unplugin-auto-import/vite'
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers';
// 引入node内置模块path：可以获取绝对路径（找不到模块“path”或其相应的类型声明。ts(2307))
import path from 'path'
// https://vite.dev/config/
export default defineConfig({
	plugins: [vueDevTools(
			// 禁用插件生成的客户端 CSS 文件
			// clientCss: false // 关键配置，若不存在可尝试其他相关参数（如 `injectCss: false`）
	),vue(), UnoCSS(), vueDevTools(),
		AutoImport({
			imports: [
				'vue',
				'vue-router',
				'pinia'
			],
			dts: 'src/auto-imports.d.ts', // 生成类型声明文件
		}),
		Components({
			resolvers: [
				AntDesignVueResolver({
					importStyle: 'less', // 或 'css'，推荐 'less' 以支持主题定制
					resolveIcons: true, // 自动解析图标
				}),
			],
			dts: "src/components.d.ts"
		}),],
	resolve: {
		alias: {
			//@ 表示 src
			"@":path.resolve(__dirname,'src')
		},
	},
	// css: {
	// 	// css预处理器
	// 	preprocessorOptions: {
	// 		less: {
	// 			charset: false,
	// 			additionalData: '@import "./src/assets/style/global.less";',
	// 		},
	// 	},
	// },
})
