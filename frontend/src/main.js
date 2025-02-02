import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/styles/global.css'
import axios from 'axios'

const app = createApp(App)

app.config.globalProperties.$axios = axios
axios.defaults.baseURL = '/api'

app.use(router).mount('#app')
