<!-- RegisterPage.vue -->
<template>
  <div class="loginContainer">
    <h1>Register</h1>
    <form @submit.prevent="register">
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
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";

export default {
  name: "RegisterPage",
  data() {
    return {
      username: "",
      password: "",
      confirmPassword: "",
      errorMessage: "",
      isLoading: false, // 加载状态
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  methods: {
    // 密码强度校验（至少6位，包含字母和数字）
    validatePassword(password) {
      const minLength = 6;
      const hasLetter = /[a-zA-Z]/.test(password);
      const hasNumber = /\d/.test(password);
      return password.length >= minLength && hasLetter && hasNumber;
    },
    // 用户名合法性校验（仅允许字母、数字和下划线）
    validateUsername(username) {
      const regex = /^[a-zA-Z0-9_]+$/;
      return regex.test(username);
    },
    // 如果输入不合法，恢复输入前的值，避免非法字符输入
    validateUsernameInput() {
      if (this.validateUsername(this.username) || this.username.length == 0) {
        this.errorMessage = "";
      } else {
        this.username = this.username.slice(0, -1);
        this.errorMessage = "用户名只能包含字母、数字和下划线！";
      }
    },
    async register() {
      this.errorMessage = "";
      this.isLoading = true;

      // 输入校验
      if (!this.username.trim()) {
        this.errorMessage = "用户名不能为空！";
        this.isLoading = false;
        return;
      }
      if (!this.validateUsername(this.username)) {
        this.errorMessage = "用户名只能包含字母、数字和下划线！";
        this.isLoading = false;
        return;
      }
      if (this.password !== this.confirmPassword) {
        this.errorMessage = "两次输入的密码不一致！";
        this.isLoading = false;
        return;
      }
      if (!this.validatePassword(this.password)) {
        this.errorMessage = "密码至少6位，且需包含字母和数字！";
        this.isLoading = false;
        return;
      }

      try {
        const response = await this.$axios.post(
          `${this.$CONSTANT.PUBLIC_AUTH_API}/register`,
          {
            username: this.username,
            password: this.password,
          }
        );

        // 假设后端返回 HTTP 状态码 200 表示成功
        if (response.status === 200) {
          showToast(this.toast, "注册成功！即将跳转到登录页面...", "success");
          setTimeout(() => {
            this.$router.push("/login"); // 明确跳转到登录页
          }, 3000);
        } else {
          // 注册失败
          throw new Error(response.data.message);
        }
      } catch (error) {
        // 提取后端返回的具体错误信息
        const backendError = error.response?.data?.message;
        this.errorMessage = backendError || "注册失败，请检查网络或稍后重试。";
        console.error("注册失败：", error);
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>

<style scoped></style>
