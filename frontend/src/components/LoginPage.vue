<!-- LoginPage.vue -->
<template>
  <div class="container">
    <h1>Login Page</h1>
    <form @submit.prevent="login">
      <label for="username">Username:</label>
      <input type="text" id="username" v-model="username" required />

      <label for="password">Password:</label>
      <input type="password" id="password" v-model="password" required />

      <button type="submit">Login</button>
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
.container {
  text-align: center;
  margin-top: 50px;
}

input,
button {
  display: block;
  margin: 10px auto;
}

button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
