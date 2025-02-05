<!-- LoginPage.vue -->
<template>
  <div class="loginContainer">
    <h1>Login</h1>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="usernameInput" v-model="username" required />
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
        console.log(response.data.status);
        // 判断后端返回的状态码是否为 200 (表示登录成功)
        if (response.data.status === 200) {
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
        console.error("登录失败：", error);
        this.errorMessage =
          error.message || "Login failed, please try again later.";
      }
    },
  },
};
</script>

<style scoped>
</style>
