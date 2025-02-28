<template>
  <div class="navContainer">
    <nav>
      <div class="userContainer" v-if="isAuthPage">
        <img
          :src="user.avatar || require('@/assets/images/icon.png')"
          alt="头像"
          class="friendAvatar"
        />
        <span class="friendNickname"
          >{{ user.username }} (#{{ user.id }})
        </span>
      </div>
      <router-link to="/">Home</router-link>
      |
      <router-link to="/friends">Friends</router-link>
      |
      <router-link to="/tasks">Tasks</router-link>
      <button v-if="isAuthPage" @click="logout" class="logoutButton">
        Logout
      </button>
    </nav>
  </div>
  <router-view />
</template>

<script>
export default {
  data() {
    return {
      user: [],
    };
  },
  computed: {
    // 判断当前路由是否为 /login 或 /register
    isAuthPage() {
      if (this.$route.meta.requiresAuth) {
        this.fetchUserData();
      }
      return this.$route.meta.requiresAuth;
    },
  },
  methods: {
    logout() {
      // 清除登录状态
      localStorage.removeItem("token");
      localStorage.removeItem("isLoggedIn");
      this.$router.push("/login"); // 跳转到登录页面
    },
    async fetchUserData() {
      try {
        const responseUserId = await this.$axios.get(
          `user/info`
        );
        this.user = responseUserId.data;
        console.log(this.user);
      } catch (error) {
        console.error("获取用户数据失败:", error);
        // 捕获 401 错误（JWT 过期或无效）
        if (error.response && error.response.status === 401) {
          this.logout();
        }
      }
    },
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

.friendAvatar {
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
