<!-- UserPage.vue -->
<template>
  <ConfirmDialog
    v-model:isVisible="isConfirmDialogVisible"
    :message="confirmMessage"
    @confirm="handleConfirm"
  />

  <div v-if="showAvatarDropdown" class="overlay" @click="cancelDropdown" />

  <div class="container">
    <h1>{{ $t('profile.title') }}</h1>
    <div class="blockContainer">
      <HelpTooltip>{{ $t('help.editProfile') }}</HelpTooltip>
      <div class="basicInfo">
        <div class="profileSection">
          <img
            :src="previewAvatar || $store.getters.getUserAvatar"
            :alt="$t('common.avatarAlt')"
            class="avatar"
            @click="toggleAvatarDropdown"
          />
          <!-- 头像选择下拉框 -->
          <div v-if="showAvatarDropdown" class="avatarDropdown">
            <div class="avatarOptions">
              <div
                v-for="avatarId in avatarList"
                :key="avatarId"
                class="avatarOption"
                :class="{ selected: selectedAvatarId === avatarId }"
                @click="selectAvatar(avatarId)"
              >
                <img :src="getAvatarById(avatarId)" alt="可选头像" />
                <span v-if="selectedAvatarId === avatarId" class="checkmark">✔</span>
              </div>
            </div>
            <div class="doubleButtonContainer">
              <button class="button warningButton" @click="cancelDropdown">
                {{ $t('common.cancel') }}
              </button>
              <button class="button normalButton" @click="confirmAvatarChange">
                {{ $t('common.confirm') }}
              </button>
            </div>
          </div>
        </div>
        <div v-if="!isEditingName" class="nameAndIdContainer" @click="startEditingName">
          <span class="nameInfo">{{ user.username }}</span>
          <span class="idInfo">(#{{ user.id }})</span>
        </div>
        <div v-else class="userField">
          <input
            ref="userNameInput"
            v-model="editableUserName"
            @blur="saveUserName"
            @keyup.enter="saveUserName"
          />
        </div>
      </div>
    </div>
    <div class="blockContainer">
      <div class="changeInfoContainer">
        <div class="doubleButtonContainer">
          <button v-if="!isEditingPassword" class="button normalButton" @click="editPassword">
            {{ $t('profile.buttons.change_password') }}
          </button>
          <button
            v-if="!isEditingPassword"
            class="button warningButton"
            @click="showDeactivateAccountConfirm"
          >
            {{ $t('profile.buttons.deactivate_account') }}
          </button>
        </div>
        <div v-if="isEditingPassword" class="editPasswordContainer">
          <div class="allInputFields">
            <div class="formWithLabelAndInput">
              <label for="oldPassword"> {{ $t('profile.password.current_label') }}</label>
              <input
                id="oldPassword"
                v-model="oldPassword"
                type="password"
                :placeholder="$t('profile.password.placeholder_current')"
              />
            </div>

            <div class="formWithLabelAndInput">
              <label for="newPassword">{{ $t('profile.password.new_label') }}</label>
              <input
                id="newPassword"
                v-model="newPassword"
                type="password"
                :placeholder="$t('profile.password.placeholder_new')"
              />
            </div>
          </div>

          <div class="doubleButtonContainer">
            <button class="button warningButton" @click="cancelEditPassword">
              {{ $t('common.cancel') }}
            </button>
            <button class="button normalButton" @click="saveChanges">
              {{ $t('common.save') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import HelpTooltip from '@/components/common/HelpTooltip.vue'

import { validateInput } from '@/utils/validationUtils'

import { showToast } from '@/utils/toast'
import { useToast } from 'vue-toastification'
import { useI18n } from 'vue-i18n'

export default {
  name: 'UserPage',
  components: { ConfirmDialog, HelpTooltip },
  setup() {
    const toast = useToast()
    const t = useI18n()
    return { toast, t }
  },
  data() {
    return {
      user: {},
      avatarMap: {},
      selectedAvatarId: null, // 选中的头像
      showAvatarDropdown: false, // 控制下拉框显示
      editableUserName: '',
      isEditingName: false,
      oldPassword: '',
      newPassword: '',
      isEditingPassword: false,
      previewAvatar: '', // 本地预览头像
      isConfirmDialogVisible: false,
      confirmMessage: this.$t('profile.account.delete_confirm'),
    }
  },
  computed: {
    avatarList() {
      return Object.keys(this.$store.getters.getAvatarMap).map(Number) // 获取所有头像 ID
    },
  },
  mounted() {
    this.user = this.$store.getters.getUser
  },
  methods: {
    cancelDropdown() {
      this.showAvatarDropdown = false
      this.selectedAvatarId = null
    },
    getAvatarById(avatarId) {
      return this.$store.getters.getAvatar(avatarId)
    },
    toggleAvatarDropdown() {
      this.showAvatarDropdown = !this.showAvatarDropdown
      this.selectedAvatarId = null
    },
    selectAvatar(avatarId) {
      this.selectedAvatarId = avatarId
    },
    async confirmAvatarChange() {
      try {
        if (!this.selectedAvatarId) {
          showToast(this.toast, this.$t('profile.avatar.not_selected'), 'warning')
          return
        }

        await this.$axios.put(`/user/update-avatar/${this.user.id}`, null, {
          params: {
            avatarId: this.selectedAvatarId,
          },
        })
        this.user.avatarId = this.selectedAvatarId

        this.$store.dispatch('setUser', this.user)

        showToast(this.toast, this.$t('profile.avatar.update_success'), 'success')
        this.showAvatarDropdown = false
      } catch (error) {
        showToast(this.toast, this.$t('profile.avatar.update_error'), 'error')
      }
    },

    startEditingName() {
      this.editableUserName = this.user.username
      this.isEditingName = true
      setTimeout(() => {
        this.$refs.userNameInput.focus()
      }, 0)
    },
    async saveUserName() {
      this.isEditingName = false

      // 若未改变用户名则直接退出函数
      if (this.editableUserName === this.user.username) {
        return
      }

      // 用户名合法性检验
      const errorMessage = validateInput(
        this.$constants.USER_NAME_VALIDATION,
        this.editableUserName
      )
      if (errorMessage) {
        this.editableUserName = this.user.username
        showToast(this.toast, errorMessage, 'error')
        return
      }

      try {
        this.user.username = this.editableUserName
        await this.$axios.put(`/user/update-username/${this.user.id}`, null, {
          params: { newUsername: this.editableUserName },
        })

        this.$store.dispatch('logout')
        this.$router.push('/login')
        showToast(this.toast, this.$t('profile.username.update_success'), 'success')
      } catch (error) {
        showToast(this.toast, this.$t('profile.username.update_fail'), 'error')
      }
    },

    resetChanges() {
      this.oldPassword = ''
      this.newPassword = ''
    },
    async saveChanges() {
      const updatedData = {
        oldPassword: this.oldPassword,
        newPassword: this.newPassword,
      }
      const errorMessage = validateInput(this.$constants.PW_VALIDATION, this.newPassword)
      if (errorMessage) {
        showToast(this.toast, errorMessage, 'error')
        return
      }

      try {
        await this.$axios.put(`/user/update-password/${this.user.id}`, null, {
          params: updatedData,
        })
        showToast(this.toast, this.$t('profile.password.update_success'), 'success')
        this.isEditingPassword = false
      } catch (error) {
        if (error.response) {
          showToast(this.toast, error.response.message, 'error')
        } else {
          showToast(this.toast, this.$t('profile.password.update_fail'), 'error')
        }
      }
    },
    editPassword() {
      this.isEditingPassword = true
    },
    cancelEditPassword() {
      this.isEditingPassword = false
      this.resetChanges()
    },

    showDeactivateAccountConfirm() {
      this.isConfirmDialogVisible = true
    },
    handleConfirm() {
      this.deactivateAccount()
    },
    async deactivateAccount() {
      try {
        // TODO: 处理用户为群主时的注销逻辑
        await this.$axios.delete(`/user/close-account/${this.user.id}`)

        this.$store.dispatch('logout')
        this.$router.push('/login')

        showToast(
          this.toast,
          this.$t('profile.account.delete_success', {
            username: this.user.username,
            id: this.user.id,
          }),
          'success'
        )
      } catch (error) {
        showToast(this.toast, this.$t('profile.account.delete_fail'), 'error')
      }
    },
  },
}
</script>

<style scoped>
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0; /* 确保遮罩层在侧边栏之下 */
}

.editPasswordContainer,
.basicInfo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.avatar {
  position: relative;
  margin: 0;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  cursor: pointer;
  object-fit: cover;
  z-index: 1;
}

.avatarDropdown {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  margin-top: 10px;
  z-index: 10;
  width: 80%;
  max-width: 700px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: center;
  justify-content: center;
}

.avatarOptions {
  display: grid;
  grid-gap: 10px;
  grid-template-rows: 1fr 1fr 1fr;
  width: 100%;
  overflow-x: scroll;
  grid-auto-flow: column;
}

.avatarOption {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
}

.avatarOption img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatarOption.selected {
  border-color: #4caf50;
  opacity: 0.7;
}

.checkmark {
  position: absolute;
  bottom: 5px;
  right: 5px;
  background: #4caf50;
  color: white;
  font-size: 12px;
  border-radius: 50%;
  width: 16px;
  height: 16px;
}

.nameAndIdContainer {
  display: flex;
  align-items: baseline;
  gap: 5px;
  cursor: pointer;
}

.nameInfo {
  font-size: 25px;
  font-weight: bold;
}

.idInfo {
  font-size: 15px;
}

.changeInfoContainer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.userField {
  cursor: pointer;
}

.userField input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 10px;
  width: 100%;
  box-sizing: border-box;
}
</style>
