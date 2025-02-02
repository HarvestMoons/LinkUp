<!-- LoginPage.vue -->
<template>
  <div class="loginContainer">
    <h1>Login Page</h1>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="usernameInput" v-model="username" required />
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="passwordInput" v-model="password" required />
      </div>
      <button type="submit" class="submitButton">Login</button>
    </form>

    <p>
      Didn't have an account?
      <router-link to="/register">Register here</router-link>
    </p>
  </div>
</template>

<script>
export default {
  name: "LoginPage",
  data() {
    return {
      username: "",
      password: "",
    };
  },
  methods: {
    login() {
      // TODO: 登录逻辑
      this.$router.push("/"); // 登录成功后跳转到主页
    },
  },
};
</script>

<style scoped>
</style>
