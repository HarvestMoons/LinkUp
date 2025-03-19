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
      <button type="submit" class="longButton normalButton">Login</button>
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
      const regex = /^\w+$/;
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
        localStorage.removeItem("token");
        localStorage.removeItem("isLoggedIn");
        localStorage.removeItem("userId");
        // 发送 POST 请求到后端进行用户登录
        const response = await this.$axios.post(
          `${this.$CONSTANT.PUBLIC_AUTH_API}/login`,
          {
            username: this.username,
            password: this.password,
          }
        );

        // 存储 JWT 和登录状态
        localStorage.setItem("token", response.data.token);
        localStorage.setItem("isLoggedIn", "true");

        showToast(this.toast, "登录成功！即将跳转到主页面...", "success");
        // 登录成功后跳转到主页
        setTimeout(() => {
          this.$router.push("/");
        }, 3000);
      } catch (error) {
        if (error.response) {
          alert(error)
          //todo:详细的错误提示
          // 后端返回了错误响应（HTTP 4xx 或 5xx）
          this.errorMessage = "服务器异常，请稍后再试。";
          localStorage.removeItem("token");
          localStorage.removeItem("isLoggedIn");
          localStorage.removeItem("userId");
        } else if (error.request) {
          // 请求已发送，但服务器无响应（网络错误或服务器崩溃）
          this.errorMessage = "无法连接到服务器，请检查网络。";
        } else {
          // 其他未知错误
          this.errorMessage = "发生未知错误，请稍后再试。";
        }
      }
    },
  },
};
</script>
