<template>
  <div v-if="isMobile" class="hamburgerContainer">
    <button class="hamburger" @click="sidebarVisible = true">☰</button>
  </div>
  <div v-if="!isMobile" class="navContainer">
    <nav class="tabNav">
      <router-link v-if="$store.getters.isAuthenticated" to="/user" class="userContainer">
        <img :src="$store.getters.getUserAvatar" :alt="$t('common.avatarAlt')" class="userAvatar" />
        <span class="userNickname"
          >{{ $store.getters.getUser?.username || '' }} (#{{ $store.getters.getUserId }})
        </span>
      </router-link>
      <router-link to="/home" class="tab" :class="{ active: isActive('/') || isActive('/home') }">{{
        $t('dashboard.home')
      }}</router-link>

      <p v-if="isMobile">|</p>
      <router-link to="/friends" class="tab" :class="{ active: isActive('/friends') }">{{
        $t('dashboard.friends')
      }}</router-link>

      <p v-if="isMobile">|</p>
      <router-link to="/tasks" class="tab" :class="{ active: isActive('/tasks') }">{{
        $t('dashboard.tasks')
      }}</router-link>

      <p v-if="isMobile">|</p>
      <router-link to="/groups" class="tab" :class="{ active: isActive('/groups') }">{{
        $t('dashboard.groups')
      }}</router-link>

      <select v-model="$i18n.locale" class="languageSelect" @change="changeLanguage">
        <option value="en">English</option>
        <option value="zh-CN">中文</option>
        <option value="es">Español</option>
        <option value="ja">日本語</option>
      </select>

      <button
        v-if="$store.getters.isAuthenticated"
        class="button normalButton logoutButton"
        @click="logout"
      >
        {{ $t('dashboard.logout') }}
      </button>
    </nav>
  </div>

  <SideBar
    :title="$t('dashboard.menu')"
    :is-visible="sidebarVisible"
    :content-component="SidebarMenu"
    @close="sidebarVisible = false"
  />

  <router-view />

  <div class="footerContainer">
    <p>© 2025 Link Up</p>
    <p>|</p>
    <router-link to="/privacy">{{ $t('dashboard.privacyPolicy') }}</router-link>
  </div>
</template>

<script>
import { markRaw } from 'vue'
import { getFriendList } from '@/utils/friendService'
import { getTaskList } from '@/utils/taskService'
import { getGroupList } from '@/utils/groupService'
import { useIsMobile } from '@/utils/useIsMobile'
import { useOnlinePing } from '@/utils/useOnlinePing'
import SideBar from '@/components/common/SideBar.vue'
import SidebarMenu from '@/components/common/SidebarMenu.vue'

export default {
  components: { SideBar, SidebarMenu },
  setup() {
    const { isMobile } = useIsMobile()
    useOnlinePing() // 页面挂载时执行，内部自动监听 token
    return { isMobile }
  },
  data() {
    return {
      sidebarVisible: false,
      SidebarMenu: markRaw(SidebarMenu),
      user: {},
    }
  },
  watch: {
    // 监听路由变化，判断是否需要调用 fetchUserData
    async $route(to) {
      if (to.meta.requiresAuth) {
        await this.fetchUserData()
      }
    },
  },
  created() {
    this.$store.dispatch('loadAvatars')
    if (this.$route.meta.requiresAuth) {
      this.fetchUserData()
    }
  },
  methods: {
    logout() {
      this.$store.dispatch('logout')
      this.$router.push('/login') // 跳转到登录页面
      this.user = {}
    },
    async fetchUserData() {
      try {
        // 检查 sessionStorage 中是否存在用户数据
        if (this.$store.getters.getUserId && this.$store.getters.getUser) {
          this.user = this.$store.getters.getUser
        } else {
          const responseUser = await this.$axios.get(`/user/info`)
          this.user = responseUser.data
          this.$store.dispatch('setUser', this.user)
        }

        getFriendList(this.$store.getters.getUserId)
        getTaskList(this.$store.getters.getUserId)
        getGroupList(this.$store.getters.getUserId)
      } catch (error) {
        console.error('获取用户数据失败:', error)
        // 捕获 401 错误（JWT 过期或无效）
        if (error.response && error.response.status === 401) {
          this.logout()
        }
      }
    },
    isActive(route) {
      return this.$route.path === route
    },
    changeLanguage(event) {
      const lang = event.target.value
      this.$store.commit('setLanguage', lang)
      localStorage.setItem('userLanguage', lang)
      this.$i18n.locale = lang
    },
  },
}
</script>

<style scoped>
.hamburger {
  font-size: 30px;
  background: none;
  border: none;
  cursor: pointer;
  margin-right: auto;
  color: #433f3e;
}

.navContainer {
  position: relative;
  z-index: 500;
  padding: 30px 30px 0;
}

.tabNav {
  height: 60px;
  display: flex;
  align-items: center; /* 确保头像和文字都在导航栏中垂直居中 */
  justify-content: center; /* 水平居中对齐 */
}

.tabNav a {
  margin-right: 0.5%;
  margin-left: 0.5%;
  font-weight: bold;
  color: #433f3e;
}

.tabNav a.router-link-exact-active {
  color: #938a8a;
}

.hamburgerContainer {
  text-align: right;
  padding: 10px;
}

.hamburger {
  right: 20px;
}

.logoutButton,
.languageSelect {
  margin-left: 1%;
}

.userAvatar {
  height: 50px;
  border-radius: 50%;
  margin-right: 1.25%; /* 头像和昵称之间的间距 */
}

.userContainer {
  display: flex;
  align-items: center; /* 确保头像和文字都在导航栏中垂直居中 */
  justify-content: center; /* 水平居中对齐 */
  margin-right: 1%;
}

.tab {
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  padding: 12px 20px;
  background: #ddd;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  text-decoration: none;
  color: #433f3e;
  font-weight: bold;
  transition: 0.3s;
  position: relative;
  width: 250px;
  height: 100%;
  border: 3px solid #938a8a;
  border-bottom: none;
  box-sizing: border-box;
}

.tab.active {
  background: white;
  width: 300px;
  z-index: 2;
  color: #938a8a;
}

.tab.active::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 3px; /* 和 container 的边框厚度一致 */
  background: white; /* 盖住 container 的边框 */
  bottom: -3px; /* 让它刚好覆盖 container 上边框 */
}

.footerContainer {
  width: 100%;
  height: 60px;
  background: #2a2727;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  gap: 10px;
}

.footerContainer a {
  color: #d1c8c3;
  text-decoration: none;
}

@media screen and (max-width: 768px) {
  .tab {
    padding: 6px 5px;
    background: #fff;
    border-top-left-radius: 0px;
    border-top-right-radius: 0px;
    text-decoration: none;
    transition: 0.3s;
    position: relative;
    width: auto;
    height: 100%;
    border: none;
  }
  .userAvatar {
    height: 30px;
  }
  .tab.active {
    width: auto;
  }
  .tab.active::after {
    width: 100%;
    bottom: 0px; /* 让它刚好覆盖 container 上边框 */
  }
}
</style>
