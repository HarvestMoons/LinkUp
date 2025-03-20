import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/styles/global.css'
import axios from 'axios'
import Toast from "vue-toastification"
import "vue-toastification/dist/index.css"
import { API_BASE_URL, PUBLIC_AUTH_API } from './config/constants'
import Cookies from 'js-cookie';  // 引入 js-cookie 库来操作 cookies
import store from '@/store';

const axiosInstance = axios.create({
    baseURL: API_BASE_URL, // 设置基本的 API URL
    withCredentials: true, // 允许跨域请求携带 Cookie
});

// 请求拦截器：在每个请求发出之前添加 Authorization Header
axiosInstance.interceptors.request.use(
    (config) => {
        const jwtToken = Cookies.get('JWT'); // 获取 token
        if (jwtToken && !config.url.startsWith(`/${PUBLIC_AUTH_API}`)) {
            config.headers.Authorization = `Bearer ${jwtToken}`; // 自动将 token 添加到请求头
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

app.config.globalProperties.$CONSTANT = { API_BASE_URL, PUBLIC_AUTH_API };

// 在挂载前注册所有插件
app
    .use(store) // 注册 Vuex
    .use(router)          // 先注册路由
    .use(Toast)           // 再注册 Toast
    .mount('#app')        // 最后挂载应用

