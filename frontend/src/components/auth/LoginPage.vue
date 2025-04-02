<!-- LoginPage.vue -->
<template>
  <div class="container">
    <div class="loginContainer">
      <h1>Login</h1>
      <form @submit.prevent="login">
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
  </div>
</template>

<script>
import {showToast} from "@/utils/toast";
import {useToast} from "vue-toastification";
import {validateInput} from "@/utils/userService";
import {useI18n} from "vue-i18n";

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
    const t = useI18n();
    return {toast, t};
  },
  methods: {
    async login() {

      // 校验用户名
      this.errorMessage = validateInput(this.$constants.NAME_VALIDATION, this.username, this.$t);
      if (this.errorMessage) {
        this.username = "";
        return;
      }

      // 校验密码
      this.errorMessage = validateInput(this.$constants.PW_VALIDATION, this.password, this.$t);
      if (this.errorMessage) {
        this.password = "";
        return;
      }


      try {
        // 发送 POST 请求到后端进行用户登录
        const response = await this.$axios.post(
            `${this.$constants.PUBLIC_AUTH_API}/login`,
            {
              username: this.username,
              password: this.password,
            }
        );

        console.log(response);
        this.$store.dispatch("login", response.data.token);

        showToast(this.toast, "登录成功！即将跳转到主页面...", "success");
        // 登录成功后跳转到主页
        setTimeout(() => {
          this.$router.push("/");
        }, 3000);
      } catch (error) {
        console.error(error);
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
