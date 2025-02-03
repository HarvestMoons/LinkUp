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
    async register() {
      // 验证两次密码是否一致
      if (this.password !== this.confirmPassword) {
        this.errorMessage = "Passwords do not match!";
        return;
      }

      try {
        // 发送 POST 请求到后端进行用户注册
        const response = await this.$axios.post("/register", {
          username: this.username,
          password: this.password,
        });

        // 假设后端注册成功后返回的是登录页面的重定向信息
        if (response.data.status === 200) {
          this.$router.push("/"); // 注册成功后跳转到登录页面
        } else {
          throw new Error(response.data.message);
        }
      } catch (error) {
        console.error("注册失败：", error);
        this.errorMessage =
          error.message || "Registration failed, please try again later.";
      }
    },
  },
};
</script>

<style scoped>
</style>
