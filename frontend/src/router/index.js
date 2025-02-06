import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/components/HomePage'
import LoginPage from '@/components/LoginPage'
import RegisterPage from '@/components/RegisterPage'

const routes = [
  {
    path: '/',
    name: 'home1',
    component: HomePage,
    meta: { requiresAuth: true } // 标记需要认证的路由
  },
  {
    path: '/home',
    name: 'home2',
    component: HomePage,
    meta: { requiresAuth: true } // 标记需要认证的路由
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterPage,
    meta: { guestOnly: true } // 标记仅允许未登录用户访问
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { guestOnly: true } // 标记仅允许未登录用户访问
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true' // 验证登录状态
  const hasValidToken = !!localStorage.getItem('token') // 验证 JWT 是否存在

  // 双重验证：本地存储状态 + 实际 token 存在性
  const isAuthenticated = isLoggedIn && hasValidToken

  // 处理需要认证的路由
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login' })
    return
  }

  // 处理仅允许未登录用户访问的路由
  if (to.meta.guestOnly && isAuthenticated) {
    next({ name: 'home1' }) // 已登录用户访问登录/注册页时重定向到首页
    return
  }

  next()
})

export default router