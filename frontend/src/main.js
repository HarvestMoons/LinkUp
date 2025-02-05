import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/styles/global.css'
import axios from 'axios'
import Toast from "vue-toastification"
import "vue-toastification/dist/index.css"

const app = createApp(App)

// 配置 axios
app.config.globalProperties.$axios = axios
axios.defaults.baseURL = '/api'

// 配置 Toast
app.config.globalProperties.$toast = Toast

// 在挂载前注册所有插件
app
    .use(router)          // 先注册路由
    .use(Toast)           // 再注册 Toast
    .mount('#app')        // 最后挂载应用