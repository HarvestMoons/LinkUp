<!-- HomePage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Home Page</h1>
    <p>Hello! You have successfully logged in.</p>
    <img src="../assets/images/icon.png" style="padding: 10px" alt="" />

    <div>
      <button @click="logout" class="logoutButton">Logout</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "HomePage",
  methods: {
    logout() {
      // 清除登录状态
      localStorage.removeItem("token");
      localStorage.removeItem("isLoggedIn");
      localStorage.removeItem("userId");
      this.$router.push("/login"); // 跳转到登录页面
    },
  },
};
</script>

<style scoped>
.container {
  text-align: center;
  margin-top: 30px;
}

.logoutButton {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 30%;
}
</style>
