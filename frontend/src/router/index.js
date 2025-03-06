import {createRouter, createWebHistory} from 'vue-router'
import HomePage from '@/components/HomePage'
import LoginPage from '@/components/LoginPage'
import RegisterPage from '@/components/RegisterPage'
import FriendsPage from '@/components/FriendsPage'
import TasksPage from '@/components/TasksPage'
import GroupListPage from '@/components/GroupListPage'
import GroupChatPage from '@/components/GroupChatPage'
import {useToast} from 'vue-toastification';

const routes = [
    {
        path: '/',
        name: 'home1',
        component: HomePage,
        meta: {title: '首页', requiresAuth: true} // 标记需要认证的路由
    },
    {
        path: '/home',
        name: 'home2',
        component: HomePage,
        meta: {title: '首页', requiresAuth: true} // 标记需要认证的路由
    },
    {
        path: '/register',
        name: 'register',
        component: RegisterPage,
        meta: {title: '注册', guestOnly: true} // 标记仅允许未登录用户访问
    },
    {
        path: '/login',
        name: 'login',
        component: LoginPage,
        meta: {title: '登录', guestOnly: true} // 标记仅允许未登录用户访问
    },
    {
        path: '/friends',
        name: 'friends',
        component: FriendsPage,
        meta: {title: '好友', requiresAuth: true} // 标记仅允许未登录用户访问
    },
    {
        path: '/tasks',
        name: 'tasks',
        component: TasksPage,
        meta: {title: '任务', requiresAuth: true} // 标记仅允许未登录用户访问
    },
    {
        path: '/groups',
        name: 'groups',
        component: GroupListPage,
        meta: {title: '群组', requiresAuth: true} // 需要登录才能访问
    },
    {
        path: '/group/:id',
        name: 'group',
        component: GroupChatPage,
        meta: {title: '群组', requiresAuth: true} // 需要登录才能访问
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

    // 获取toast实例
    const toast = useToast();

    // 处理需要认证的路由
    if (to.meta.requiresAuth && !isAuthenticated) {
        toast.error('请先登录后再访问此页面');
        next({name: 'login'})
        return
    }

    // 处理仅允许未登录用户访问的路由
    if (to.meta.guestOnly && isAuthenticated) {
        toast.error('请先登出后再进行登录或注册');
        next({name: 'home1'}) // 已登录用户访问登录/注册页时重定向到首页
        return
    }

    document.title = `Link Up - ${typeof to.meta.title === 'string' ? to.meta.title : '默认标题'}`;
    next();
});

export default router