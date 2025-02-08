<template>
  <div class="navContainer">
    <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/friends">Friends</router-link>
      <button v-if="isAuthPage" @click="logout" class="logoutButton">
        Logout
      </button>
    </nav>
  </div>
  <router-view />
</template>

<script>
export default {
  computed: {
    // 判断当前路由是否为 /login 或 /register
    isAuthPage() {
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
  display: flex;
  align-items: center;
  height: 30px;
  padding: 30px;
}

nav {
  width: 100%;
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
</style>
