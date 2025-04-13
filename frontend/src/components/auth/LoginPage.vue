<!-- LoginPage.vue -->
<template>
  <div class="container">
    <div class="loginContainer">
      <h1>{{ $t('auth.login.title') }}</h1>
      <form @submit.prevent="login">
        <div class="form-group">
          <label for="username">{{ $t('common.username') }}:</label>
          <input id="usernameInput" v-model="username" type="text" required />
        </div>
        <div class="form-group">
          <label for="password">{{ $t('common.password') }}:</label>
          <input id="passwordInput" v-model="password" type="password" required />
        </div>
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        <button type="submit" class="longButton normalButton">
          {{ $t('auth.login.title') }}
        </button>
      </form>

      <p>
        {{ $t('auth.login.no_account_prompt') }}
        <router-link to="/register">{{ $t('auth.login.register_link') }}</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import { showToast } from '@/utils/toast'
import { useToast } from 'vue-toastification'
import { validateInput } from '@/utils/validationUtils'
import { useI18n } from 'vue-i18n'
import { getFriendList } from '@/utils/friendService'
import { getTaskList } from '@/utils/taskService'
import { getGroupList } from '@/utils/groupService'

export default {
  name: 'LoginPage',
  setup() {
    const toast = useToast()
    const t = useI18n()
    return { toast, t }
  },
  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',
    }
  },
  methods: {
    async login() {
      // 校验用户名
      this.errorMessage = validateInput(this.$constants.USER_NAME_VALIDATION, this.username)
      if (this.errorMessage) {
        this.username = ''
        return
      }

      // 校验密码
      this.errorMessage = validateInput(this.$constants.PW_VALIDATION, this.password)
      if (this.errorMessage) {
        this.password = ''
        return
      }

      try {
        // 发送 POST 请求到后端进行用户登录
        const response = await this.$axios.post(`${this.$constants.PUBLIC_AUTH_API}/login`, {
          username: this.username,
          password: this.password,
        })

        const responseUser = await this.$axios.get(`/user/info`)
        this.user = responseUser.data
        this.$store.dispatch('setUser', this.user)

        showToast(this.toast, this.$t('auth.login.success'), 'success')
        // 登录成功后跳转到主页
        this.$store.dispatch('login', response.data.token)
        getFriendList(this.user.id)
        getTaskList(this.user.id)
        getGroupList(this.user.id)
        setTimeout(() => {
          this.$router.push('/')
          this.$store.dispatch('setLogined')
        }, 3000)
      } catch (error) {
        if (error.response) {
          // 后端返回了错误响应（HTTP 4xx 或 5xx）
          this.errorMessage = error.response.data.message || this.$t('error.server.default')
        } else if (error.request) {
          //  请求已发送，但服务器无响应（网络错误或服务器崩溃）
          this.errorMessage = this.$t('error.server.network')
        } else {
          // 未知错误
          this.errorMessage = this.$t('error.server.unknown')
        }
      }
    },
  },
}
</script>
