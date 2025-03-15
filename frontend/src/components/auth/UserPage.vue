<!-- UserPage.vue -->
<template>
  <div class="container">
    <h1>个人信息</h1>
    <div class="blockContainer">
      <div class="basicInfo">
        <div class="profileSection">
          <input
            type="file"
            @change="handleFileUpload"
            accept="image/*"
            hidden
            ref="fileInput"
          />
          <img
            :src="previewAvatar || user.avatar || defaultAvatar"
            alt="头像"
            class="avatar"
            @click="$refs.fileInput.click()"
          />
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
import { MAX_STRING_LENGTH } from "@/config/constants";

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
    this.user = JSON.parse(localStorage.getItem("user"));
  },
  methods: {
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.previewAvatar = e.target.result;
        };
        reader.readAsDataURL(file);
      }
      // TODO: 存储头像逻辑
    },

    // 密码强度校验（至少6位，包含字母和数字）
    validatePassword(password) {
      const minLength = 6;
      const hasLetter = /[a-zA-Z]/.test(password);
      const hasNumber = /\d/.test(password);
      return password.length >= minLength && hasLetter && hasNumber;
    },
    // 用户名合法性校验（仅允许字母、数字和下划线）
    validateUsername(username) {
      const regex = /^\w+$/;
      return regex.test(username);
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
      var isUserNameLegal = true;
      if (this.editableUserName === "") {
        isUserNameLegal = false;
        showToast(this.toast, "用户名称不能为空!", "error");
      } else if (this.editableUserName.length > MAX_STRING_LENGTH) {
        isUserNameLegal = false;
        showToast(this.toast, "用户名过长！", "error");
      } else if (!this.validateUsername(this.editableUserName)) {
        isUserNameLegal = false;
        showToast(this.toast, "用户名只能包含字母、数字和下划线！", "error");
      }
      if (!isUserNameLegal) {
        this.editableUserName = this.user.username;
        this.isEditingName = false;
        return;
      }
      try {
        this.isEditingName = false;
        this.user.username = this.editableUserName;
        await this.$axios.put(`/user/update-username/${this.user.id}`, null, {
          params: { newUsername: this.editableUserName },
        });
        showToast(this.toast, `群用户名称更改成功`, "success");
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
      if (this.newPassword.length > MAX_STRING_LENGTH) {
        showToast(this.toast, "密码用户名过长！", "error");
        return;
      }
      if (!this.validatePassword(this.newPassword)) {
        showToast(this.toast, "密码至少6位，且需包含字母和数字！", "error");
        return;
      }
      try {
        // TODO: 提取密码检查
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
};
</script>

<style scoped>
.editPasswordContainer,
.basicInfo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.avatar {
  margin: 0;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  cursor: pointer;
  object-fit: cover;
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
