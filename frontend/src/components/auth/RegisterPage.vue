<!-- RegisterPage.vue -->
<template>
  <div class="container">
    <div class="loginContainer">
      <h1>Register</h1>
      <form @submit.prevent="register">
        <div class="form-group">
          <label for="username">Username:</label>
          <input
            type="text"
            id="usernameInput"
            v-model="username"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Password:</label>
          <input
            type="password"
            id="passwordInput"
            v-model="password"
            required
          />
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

        <button type="submit" class="longButton normalButton">Register</button>
      </form>

      <p>
        Already have an account?
        <router-link to="/login">Login here</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import { validateInput} from "@/utils/userService";
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
import {useI18n} from "vue-i18n";

export default {
  name: "RegisterPage",
  data() {
    return {
      username: "",
      password: "",
      confirmPassword: "",
      errorMessage: "",
    };
  },
  setup() {
    const toast = useToast();
    const t = useI18n();
    return {toast ,t};
  },
  methods: {
    async register() {
      this.errorMessage = "";

      // 校验用户名
      this.errorMessage = validateInput(this.$constants.NAME_VALIDATION, this.username,this.$t);
      if (this.errorMessage) return;

      // 校验密码
      this.errorMessage = validateInput(this.$constants.PW_VALIDATION, this.password,this.$t);
      if (this.errorMessage) return;

      // 确认密码
      if (this.password !== this.confirmPassword) {
        this.errorMessage = "两次输入的密码不一致！";
        return;
      }

      try {
        await this.$axios.post(`${this.$constants.PUBLIC_AUTH_API}/register`, {
          username: this.username,
          password: this.password,
        });

        showToast(this.toast, "注册成功！即将跳转到登录页面...", "success");
        setTimeout(() => {
          this.$router.push("/login"); // 明确跳转到登录页
        }, 3000);
      } catch (error) {
        if (error.response) {
          // 后端返回了错误响应（HTTP 4xx 或 5xx）
          this.errorMessage =
            error.response.data.message || "服务器异常，请稍后再试。";
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