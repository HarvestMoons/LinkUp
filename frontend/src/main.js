import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/styles/global.css'
import axios from 'axios'
import Toast from "vue-toastification"
import "vue-toastification/dist/index.css"
import * as constants from './config/constants'
import Cookies from 'js-cookie';  // 引入 js-cookie 库来操作 cookies
import i18n from '@/i18n'
import store from '@/store';
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";

const axiosInstance = axios.create({
    baseURL: constants.API_BASE_URL, // 设置基本的 API URL
    withCredentials: true, // 允许跨域请求携带 Cookie
});

// 请求拦截器：在每个请求发出之前添加 Authorization Header
axiosInstance.interceptors.request.use(
    (config) => {
        const jwtToken = Cookies.get('JWT'); // 获取 token
        if (jwtToken && !config.url.startsWith(`/${constants.PUBLIC_AUTH_API}`)) {
            config.headers.Authorization = `Bearer ${jwtToken}`; // 自动将 token 添加到请求头
        }
        return config;
    },
    (error) => {
        console.log("hello")
        if (error.response && error.response.status === 401) {
            const toast = useToast();
            showToast(toast, "检测到401错误，登出账号", "warning");
            store.dispatch("logout");
            router.push("/login");
        }
        return Promise.reject(error);
    }
);

const app = createApp(App)

// 配置 axios
app.config.globalProperties.$axios = axiosInstance
axios.defaults.baseURL = constants.API_BASE_URL

// 配置 Toast
app.config.globalProperties.$toast = Toast
// 注册全局常量
app.config.globalProperties.$constants = constants

// 在挂载前注册所有插件
app
    .use(i18n) // 使用独立的i18n配置
    .use(store) // 注册 Vuex
    .use(router)          // 先注册路由
    .use(Toast)           // 再注册 Toast
    .mount('#app')        // 最后挂载应用

