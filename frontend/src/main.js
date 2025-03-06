import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import './assets/styles/global.css'
import axios from 'axios'
import Toast from "vue-toastification"
import "vue-toastification/dist/index.css"
import {API_BASE_URL, PUBLIC_AUTH_API} from './config/constants'

const axiosInstance = axios.create({
    baseURL: API_BASE_URL, // 设置基本的 API URL
});

// 请求拦截器：在每个请求发出之前添加 Authorization Header
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token'); // 获取 token
        if (token && !config.url.startsWith(`/${PUBLIC_AUTH_API}`)) {
            config.headers.Authorization = `Bearer ${token}`; // 自动将 token 添加到请求头
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

const app = createApp(App)

// 配置 axios
app.config.globalProperties.$axios = axiosInstance
axios.defaults.baseURL = API_BASE_URL

// 配置 Toast
app.config.globalProperties.$toast = Toast

app.config.globalProperties.$CONSTANT = {API_BASE_URL, PUBLIC_AUTH_API};

// 在挂载前注册所有插件
app
    .use(router)          // 先注册路由
    .use(Toast)           // 再注册 Toast
    .mount('#app')        // 最后挂载应用

