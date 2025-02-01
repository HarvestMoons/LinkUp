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
      /*// 模拟登录逻辑，实际情况可能是发请求到后台
      if (this.username === 'admin' && this.password === '1234') {
        this.$router.push('/');  // 登录成功后跳转到主页
      } else {
        alert('Invalid credentials');
      }*/
    },
  },
};
</script>

<style scoped>
</style>
