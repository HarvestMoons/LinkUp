<!-- RegisterPage.vue -->
<template>
  <div class="container">
    <div class="loginContainer">
      <h1>{{ $t("auth.registration.title") }}</h1>
      <form @submit.prevent="register">
        <div class="form-group">
          <label for="username">{{ $t("common.username") }}:</label>
          <input id="usernameInput" v-model="username" type="text" required />
        </div>

        <div class="form-group">
          <label for="password">{{ $t("common.password") }}:</label>
          <input
            id="passwordInput"
            v-model="password"
            type="password"
            required
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword">{{ $t("auth.confirmPassword") }}:</label>
          <input
            id="confirmPasswordInput"
            v-model="confirmPassword"
            type="password"
            required
          />
        </div>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <button type="submit" class="longButton normalButton">
          {{ $t("auth.registration.title") }}
        </button>
      </form>

      <p>
        {{ $t("auth.registration.existing_account") }}
        <router-link to="/login">{{
          $t("auth.registration.login_link")
        }}</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import { validateInput } from "@/utils/validationUtils";
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";

export default {
  name: "RegisterPage",
  setup() {
    const toast = useToast();
    const t = useI18n();
    return { toast, t };
  },
  data() {
    return {
      username: "",
      password: "",
      confirmPassword: "",
      errorMessage: "",
    };
  },
  methods: {
    async register() {
      this.errorMessage = "";

      // 校验用户名
      this.errorMessage = validateInput(
        this.$constants.USER_NAME_VALIDATION,
        this.username
      );
      if (this.errorMessage) return;

      // 校验密码
      this.errorMessage = validateInput(
        this.$constants.PW_VALIDATION,
        this.password
      );
      if (this.errorMessage) return;

      // 确认密码
      if (this.password !== this.confirmPassword) {
        this.errorMessage = this.$t("error.validation.password_mismatch");
        return;
      }

      try {
        await this.$axios.post(`${this.$constants.PUBLIC_AUTH_API}/register`, {
          username: this.username,
          password: this.password,
        });

        showToast(this.toast, this.$t("auth.registration.success"), "success");
        setTimeout(() => {
          this.$router.push("/login"); // 明确跳转到登录页
        }, 3000);
      } catch (error) {
        if (error.response) {
          // 后端返回了错误响应（HTTP 4xx 或 5xx）
          this.errorMessage =
            error.response.data.message || this.$t("error.server.default");
        } else if (error.request) {
          //  请求已发送，但服务器无响应（网络错误或服务器崩溃）
          this.errorMessage = this.$t("error.server.network");
        } else {
          // 未知错误
          this.errorMessage = this.$t("error.server.unknown");
        }
      }
    },
  },
};
</script>
