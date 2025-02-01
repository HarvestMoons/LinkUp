<!-- RegisterPage.vue -->
<template>
  <div class="loginContainer">
    <h1>Register</h1>
    <form @submit.prevent="register">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="usernameInput" v-model="username" required />
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="passwordInput" v-model="password" required />
      </div>

      <div class="form-group">
        <label for="confirmPassword">Confirm Password:</label>
        <input
          type="password"
          id="confirmPasswordInput"
          v-model="confirmPassword"
          required
        />
      </div>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <button type="submit" class="submitButton">Register</button>
    </form>

    <p>
      Already have an account? <router-link to="/login">Login here</router-link>
    </p>
  </div>
</template>

<script>
export default {
  name: "RegisterPage",
  data() {
    return {
      username: "",
      password: "",
      confirmPassword: "",
      errorMessage: "", // 用于存储错误信息
    };
  },
  methods: {
    register() {
      // 验证两次密码是否一致
      if (this.password !== this.confirmPassword) {
        this.errorMessage = "Passwords do not match!";
        return;
      }

      // 这里你可以添加更多的验证逻辑，比如检查用户名是否已经存在
      // 模拟注册成功后跳转到登录页面
      alert("Registration successful!");
      this.$router.push("/login"); // 注册成功后跳转到登录页面
    },
  },
};
</script>

<style scoped>
</style>
