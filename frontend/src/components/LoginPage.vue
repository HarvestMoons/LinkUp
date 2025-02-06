<!-- LoginPage.vue -->
<template>
  <div class="loginContainer">
    <h1>Login</h1>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="username">Username:</label>
        <input
          type="text"
          id="usernameInput"
          v-model="username"
          @input="validateUsernameInput"
          required
        />
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="passwordInput" v-model="password" required />
      </div>
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
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
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";

export default {
  name: "LoginPage",
  data() {
    return {
      username: "",
      password: "",
      errorMessage: "",
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  methods: {
    // 用户名合法性校验（仅允许字母、数字和下划线）
    validateUsername(username) {
      const regex = /^[a-zA-Z0-9_]+$/;
      return regex.test(username);
    },
    // 如果输入不合法，恢复输入前的值，避免非法字符输入
    validateUsernameInput() {
      if (this.validateUsername(this.username) || this.username.length === 0) {
        this.errorMessage = "";
      } else {
        this.username = this.username.slice(0, -1);
        this.errorMessage = "用户名只能包含字母、数字和下划线！";
      }
    },
    async login() {
      try {
        // 发送 POST 请求到后端进行用户登录
        const response = await this.$axios.post(
          `${this.$CONSTANT.PUBLIC_AUTH_API}/login`,
          {
            username: this.username,
            password: this.password,
          }
        );

        // 判断后端返回的状态码是否为 200 (表示登录成功)
        if (response.data.status === 200) {
          // 存储 JWT 和登录状态
          localStorage.setItem('token', response.data.token);
          localStorage.setItem('isLoggedIn', 'true');

          showToast(this.toast, "登录成功！即将跳转到主页面...", "success");
          // 登录成功后跳转到主页
          setTimeout(() => {
            this.$router.push("/");
          }, 3000);
        } else {
          // 登录失败
          throw new Error(response.data.message);
        }
      } catch (error) {
        this.errorMessage = error.message || "登录失败，请检查网络或稍后重试。";
        console.error("登录失败：", error);
      }
    },
  },
};
</script>

<style scoped>
</style>
