import { createApp } from 'vue'
import './style.css'
import 'virtual:uno.css'
import '@unocss/reset/tailwind-compat.css'
import App from './App.vue'
import {registerPlugins} from "./plugins";
import vSlideIn from "@/directive/vSlideIn.ts"
import './plugins/day.ts'

const app = createApp(App);
registerPlugins(app)

app.directive('slide-in', vSlideIn);
app.mount('#app')