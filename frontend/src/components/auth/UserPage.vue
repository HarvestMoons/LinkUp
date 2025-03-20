<!-- UserPage.vue -->
<template>
  <div v-if="showAvatarDropdown" class="overlay" @click="cancelDropdown"></div>
  <div class="container">
    <h1>个人信息</h1>
    <div class="blockContainer">
      <div class="basicInfo">
        <div class="profileSection">
          <img
            :src="previewAvatar || user.avatar || defaultAvatar"
            alt="头像"
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
                <span v-if="selectedAvatarId === avatarId" class="checkmark"
                  >✔</span
                >
              </div>
            </div>
            <div class="doubleButtonContainer">
              <button class="button warningButton" @click="cancelDropdown">
                取消
              </button>
              <button class="button normalButton" @click="confirmAvatarChange">
                确认
              </button>
            </div>
          </div>
        </div>
        <div
          class="nameAndIdContainer"
          @click="startEditingName"
          v-if="!isEditingName"
        >
          <span class="nameInfo">{{ user.username }}</span>
          <span class="idInfo">(#{{ user.id }})</span>
        </div>
        <div class="userField" v-else>
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
        <button
          class="button normalButton"
          @click="editPassword"
          v-if="!isEditingPassword"
        >
          修改密码
        </button>
        <div class="editPasswordContainer" v-else>
          <div class="allInputFields">
            <div class="formWithLabelAndInput">
              <label for="oldPassword">原密码:</label>
              <input
                type="password"
                id="oldPassword"
                v-model="oldPassword"
                placeholder="输入原密码以修改"
              />
            </div>

            <div class="formWithLabelAndInput">
              <label for="newPassword">新密码:</label>
              <input
                type="password"
                id="newPassword"
                v-model="newPassword"
                placeholder="输入新密码"
              />
            </div>
          </div>

          <div class="doubleButtonContainer">
            <button class="button warningButton" @click="cancelEditPassword">
              取消
            </button>
            <button class="button normalButton" @click="saveChanges">
              保存
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { validateInput } from "@/utils/userService";

import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";

export default {
  name: "UserPage",
  data() {
    return {
      user: {
        avatar: "", // 服务器上的头像 URL
        username: "",
      },
      avatarMap: {},
      selectedAvatarId: null, // 选中的头像
      showAvatarDropdown: false, // 控制下拉框显示
      editableUserName: "",
      isEditingName: false,
      oldPassword: "",
      newPassword: "",
      isEditingPassword: false,
      previewAvatar: "", // 本地预览头像
      defaultAvatar: require("@/assets/images/icon.png"),
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  mounted() {
    this.user = this.$store.getters.getUser;

    // 预加载所有头像
    this.avatarMap = {};
    const images = require.context("@/assets/images/avatars", false, /\.png$/);
    images.keys().forEach((fileName) => {
      const id = fileName.match(/(\d+)\.png$/)[1]; // 提取头像 ID
      this.avatarMap[id] = images(fileName);
    });
  },
  methods: {
    cancelDropdown() {
      this.showAvatarDropdown = false;
      this.selectedAvatarId = null;
    },
    getAvatarById(avatarId) {
      return this.avatarMap[avatarId] || this.defaultAvatar;
    },
    toggleAvatarDropdown() {
      this.showAvatarDropdown = !this.showAvatarDropdown;
      this.selectedAvatarId = null;
    },
    selectAvatar(avatarId) {
      this.selectedAvatarId = avatarId;
    },
    async confirmAvatarChange() {
      try {
        if (!this.selectedAvatarId) {
          showToast(this.toast, "请先选择一个头像", "warning");
          return;
        }

        await this.$axios.put(`/user/update-avatar/${this.user.id}`, null, {
          params: {
            avatarId: this.selectedAvatarId,
          },
        });

        this.user.avatar = this.getAvatarById(this.selectedAvatarId);
        this.$store.dispatch("setUser", this.user);

        showToast(this.toast, "头像更新成功", "success");
        this.showAvatarDropdown = false;
      } catch (error) {
        console.error("头像更新失败", error);
        showToast(this.toast, "头像更新失败", "error");
      }
    },

    startEditingName() {
      this.editableUserName = this.user.username;
      this.isEditingName = true;
      setTimeout(() => {
        this.$refs.userNameInput.focus();
      }, 0);
    },
    async saveUserName() {
      // TODO: 修改完名字会出现401错误，需解决
      this.isEditingName = false;
      const errorMessage = validateInput("用户名", this.editableUserName);
      if (errorMessage) {
        this.editableUserName = this.user.username;
        showToast(this.toast, errorMessage, "error");
        return;
      }

      try {
        this.user.username = this.editableUserName;
        await this.$axios.put(`/user/update-username/${this.user.id}`, null, {
          params: { newUsername: this.editableUserName },
        });
        showToast(this.toast, `用户名称更改成功`, "success");
      } catch (error) {
        console.error("更改用户名称失败", error);
        showToast(this.toast, "更改用户名称失败", "error");
      }
    },

    resetChanges() {
      this.oldPassword = "";
      this.newPassword = "";
    },
    async saveChanges() {
      const updatedData = {
        oldPassword: this.oldPassword,
        newPassword: this.newPassword,
      };
      const errorMessage = validateInput("密码", this.newPassword);
      if (errorMessage) {
        showToast(this.toast, errorMessage, "error");
        return;
      }

      try {
        await this.$axios.put(`/user/update-password/${this.user.id}`, null, {
          params: updatedData,
        });
        showToast(this.toast, "密码修改成功", "success");
        this.isEditingPassword = false;
      } catch (error) {
        console.error("密码修改失败", error);
        if (error.response) {
          showToast(this.toast, error.response.message, "error");
        } else {
          showToast(this.toast, "密码修改失败", "error");
        }
      }
    },
    editPassword() {
      this.isEditingPassword = true;
    },
    cancelEditPassword() {
      this.isEditingPassword = false;
      this.resetChanges();
    },
  },
  computed: {
    avatarList() {
      return Object.keys(this.avatarMap).map(Number); // 获取所有头像 ID
    },
  },
};
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
