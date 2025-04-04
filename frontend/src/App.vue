<template>
  <div class="navContainer">
    <nav class="tabNav">
      <router-link
        to="/user"
        class="userContainer"
        v-if="this.$store.getters.isAuthenticated"
      >
        <img
          :src="this.$store.getters.getUserAvatar"
          alt="头像"
          class="userAvatar"
        />
        <span class="userNickname"
          >{{ this.$store.getters.getUser.username }} (#{{
            this.$store.getters.getUserId
          }})
        </span>
      </router-link>
      <router-link
        to="/home"
        class="tab"
        :class="{ active: isActive('/') || isActive('/home') }"
        >Home</router-link
      >

      <router-link
        to="/friends"
        class="tab"
        :class="{ active: isActive('/friends') }"
        >Friends</router-link
      >

      <router-link
        to="/tasks"
        class="tab"
        :class="{ active: isActive('/tasks') }"
        >Tasks</router-link
      >

      <router-link
        to="/groups"
        class="tab"
        :class="{ active: isActive('/groups') }"
        >Groups</router-link
      >

      <select @change="changeLanguage" class="languageSelect">
        <option value="en">English</option>
        <option value="zh-CN">中文</option>
      </select>

      <button
        v-if="this.$store.getters.isAuthenticated"
        @click="logout"
        class="button normalButton logoutButton"
      >
        Logout
      </button>
    </nav>
  </div>

  <router-view />

  <div class="footerContainer">
    <p>© 2025 Link Up</p>
    <p>|</p>
    <router-link to="/privacy">Privacy Policy</router-link>
  </div>
</template>

<script>
import { getFriendList } from "@/utils/friendService";
import { getTaskList } from "@/utils/taskService";
export default {
  data() {
    return {
      user: {},
    };
  },
  watch: {
    // 监听路由变化，判断是否需要调用 fetchUserData
    async $route(to) {
      if (to.meta.requiresAuth) {
        await this.fetchUserData();
      }
    },
  },
  methods: {
    logout() {
      this.$store.dispatch("logout");
      this.$router.push("/login"); // 跳转到登录页面
      this.user = {};
    },
    async fetchUserData() {
      try {
        // 检查 sessionStorage 中是否存在用户数据
        if (this.$store.getters.getUserId && this.$store.getters.getUser) {
          this.user = this.$store.getters.getUser;
        } else {
          const responseUser = await this.$axios.get(`/user/info`);
          this.user = responseUser.data;
          this.$store.dispatch("setUser", this.user);
        }

        getFriendList(this.$store.getters.getUserId);
        getTaskList(this.$store.getters.getUserId);
      } catch (error) {
        console.error("获取用户数据失败:", error);
        // 捕获 401 错误（JWT 过期或无效）
        if (error.response && error.response.status === 401) {
          this.logout();
        }
      }
    },
    isActive(route) {
      return this.$route.path === route;
    },
    // 处理语言选择的变化
    changeLanguage(event) {
      const selectedLanguage = event.target.value;
      this.$store.dispatch("setLanguage", selectedLanguage);
    },
  },
  created() {
    this.$store.dispatch("loadAvatars");
    if (this.$route.meta.requiresAuth) {
      this.fetchUserData();
    }
  },
};
</script>

<style>
html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #433f3e;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.navContainer {
  position: relative;
  z-index: 500;
  padding: 30px;
  padding-bottom: 0;
}

nav {
  height: 60px;
  display: flex;
  align-items: center; /* 确保头像和文字都在导航栏中垂直居中 */
  justify-content: center; /* 水平居中对齐 */
}

nav a {
  margin-right: 0.5%;
  margin-left: 0.5%;
  font-weight: bold;
  color: #433f3e;
}

nav a.router-link-exact-active {
  color: #938a8a;
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
  content: "";
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

.languageSelect {
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  width: 120px; /* 根据需要调整宽度 */
  background-color: #938a8a;
  color: white;
  font-weight: bold;
  height: 36px;
}

.languageSelect:hover {
  filter: brightness(90%);
}

.languageSelect option {
  background-color: #ddd; /* 默认选项背景色（灰色） */
  color: #333; /* 默认文字颜色 */
}

.languageSelect:focus {
  outline: none;
}
</style>
