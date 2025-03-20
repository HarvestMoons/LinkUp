<template>
  <div class="navContainer">
    <nav>
      <router-link
        to="/user"
        class="userContainer"
        v-if="
          this.$store.getters.isAuthenticated && this.$route.meta.requiresAuth
        "
      >
        <img
          :src="this.$store.getters.getUserAvatar"
          alt="头像"
          class="userAvatar"
        />
        <span class="userNickname">{{ user.username }} (#{{ user.id }}) </span>
      </router-link>
      <router-link to="/">Home</router-link>
      |
      <router-link to="/friends">Friends</router-link>
      |
      <router-link to="/tasks">Tasks</router-link>
      |
      <router-link to="/groups">Groups</router-link>
      <button
        v-if="this.$route.meta.requiresAuth"
        @click="logout"
        class="logoutButton"
      >
        Logout
      </button>
    </nav>
  </div>
  <router-view />
</template>
<script>
import { fetchFriends } from "@/utils/friendService";

export default {
  data() {
    return {
      user: [],
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
        await fetchFriends(this.user.id);
      } catch (error) {
        console.error("获取用户数据失败:", error);
        // 捕获 401 错误（JWT 过期或无效）
        if (error.response && error.response.status === 401) {
          this.logout();
        }
      }
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
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

.navContainer {
  position: relative;
  z-index: 500;
  padding: 30px;
}

nav {
  height: 50px;
  display: flex;
  align-items: center; /* 确保头像和文字都在导航栏中垂直居中 */
  justify-content: center; /* 水平居中对齐 */
}

nav a {
  margin-right: 0.5%;
  margin-left: 0.5%;
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}

.logoutButton {
  margin-left: 1%;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 80px;
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
}
</style>
