import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/components/home/HomePage'
import LoginPage from '@/components/auth/LoginPage'
import RegisterPage from '@/components/auth/RegisterPage'
import FriendsPage from '@/components/friends/FriendsPage'
import TasksPage from '@/components/tasks/TasksPage'
import GroupListPage from '@/components/groups/GroupListPage'
import GroupChatPage from '@/components/groups/GroupChatPage'
import UserPage from '@/components/auth/UserPage'
import PrivacyPage from '@/components/auth/PrivacyPage'
import { useToast } from 'vue-toastification'
import store from '@/store'
import { t } from '@/i18n'

const routes = [
  {
    path: '/',
    name: 'home1',
    component: HomePage,
    meta: { title: '首页', requiresAuth: true }, // 标记需要认证的路由
  },
  {
    path: '/home',
    name: 'home2',
    component: HomePage,
    meta: { title: '首页', requiresAuth: true }, // 标记需要认证的路由
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterPage,
    meta: { title: '注册', guestOnly: true, requiresAuth: false }, // 标记仅允许未登录用户访问
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { title: '登录', guestOnly: true, requiresAuth: false }, // 标记仅允许未登录用户访问
  },
  {
    path: '/friends',
    name: 'friends',
    component: FriendsPage,
    meta: { title: '好友', requiresAuth: true }, // 标记仅允许未登录用户访问
  },
  {
    path: '/tasks',
    name: 'tasks',
    component: TasksPage,
    meta: { title: '任务', requiresAuth: true }, // 标记仅允许未登录用户访问
  },
  {
    path: '/groups',
    name: 'groups',
    component: GroupListPage,
    meta: { title: '群组', requiresAuth: true }, // 需要登录才能访问
  },
  {
    path: '/group/:id',
    name: 'group',
    component: GroupChatPage,
    meta: { title: '群组', requiresAuth: true }, // 需要登录才能访问
  },
  {
    path: '/user',
    name: 'user',
    component: UserPage,
    meta: { title: '个人信息', requiresAuth: true }, // 需要登录才能访问
  },
  {
    path: '/privacy',
    name: 'privacy',
    component: PrivacyPage,
    meta: { title: '隐私政策' },
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = store.getters.isAuthenticated // 使用 Vuex 获取登录状态

  // 获取toast实例
  const toast = useToast()

  if (to.path === '/' && !isAuthenticated) {
    next({ name: 'login' })
    return
  }

  // 处理需要认证的路由
  if (to.meta.requiresAuth && !isAuthenticated) {
    toast.error(t('auth.login_required'))
    next({ name: 'login' })
    return
  }

  // 处理仅允许未登录用户访问的路由
  if (to.meta.guestOnly && isAuthenticated) {
    toast.error(t('auth.logout_required'))
    next({ name: 'home1' }) // 已登录用户访问登录/注册页时重定向到首页
    return
  }
  document.title = `Link Up - ${typeof to.meta.title === 'string' ? to.meta.title : '默认标题'}`
  next()
})

export default router
