import { defineConfig } from 'unocss'

export default defineConfig({
	// ...UnoCSS options
	theme: {
		extend: {
			transitionProperty: {
				'transform': 'transform',
			},
			duration: {
				'300': '300ms',
			},
			timingFunction: {
				'ease': 'ease',
			}
		}
	},
	shortcuts: {
		'transition-transform-300': 'transition-transform duration-300 ease-in-out',
	}
})