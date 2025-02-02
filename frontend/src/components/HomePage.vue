<!-- HomePage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Home Page</h1>
    <p>Hello! You have successfully logged in.</p>

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
      // TODO: 清除存储的用户信息
      this.$router.push("/login"); // 跳转到登录页面
    },
  },
};
</script>

<style scoped>
.container {
  text-align: center;
  margin-top: 50px;
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
