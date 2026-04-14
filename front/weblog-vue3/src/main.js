import '@/assets/main.css'
import 'animate.css';
import 'nprogress/nprogress.css'
import 'ant-design-vue/es/message/style/index.js'
import 'virtual:svg-icons-register'

import { createApp } from 'vue'
// 引入全局状态管理 Pinia
import pinia from '@/stores'
import { setActivePinia } from 'pinia'
import App from '@/App.vue'
// 导入路由
import router from '@/router'
// 导入全局路由守卫
import '@/permission'
import { useAiThemeStore } from '@/stores/ai-robot/theme'
// 导入 Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 图片点击放大
import 'viewerjs/dist/viewer.css'
import VueViewer from 'v-viewer'

const app = createApp(App)

// 应用路由
app.use(router)
// 应用 Pinia
app.use(pinia)
// 确保在组件外使用 store 前激活 Pinia
setActivePinia(pinia)

// 激活主题 store，统一读取/应用之前的深浅色偏好
const aiThemeStore = useAiThemeStore()
const savedTheme = localStorage.getItem('ai-theme') || localStorage.getItem('vueuse-color-scheme')
if (savedTheme === 'dark' || savedTheme === 'light') {
  aiThemeStore.theme = savedTheme
}

// 引入图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(VueViewer)

app.mount('#app')
