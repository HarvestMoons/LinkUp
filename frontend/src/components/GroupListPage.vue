<!-- GroupListPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Groups Page</h1>
    <!-- 添加好友输入框和按钮 -->
    <div class="createGroupContainer">
      <transition name="groupFormTransition">
        <!-- 控制是否显示输入框 -->
        <div class="groupForm" v-if="isCreating">
          <!-- 输入框区域 -->
          <div class="newGroupInputFields">
            <div class="form-group">
              <label for="newGroupName">群组名称:</label>
              <input
                type="text"
                id="newGroupName"
                v-model="newGroup.name"
                placeholder="请输入群组名称"
              />
            </div>
            <div class="form-group">
              <label for="newGroupDescription">群组描述:</label>
              <textarea
                id="newGroupDescription"
                v-model="newGroup.description"
                placeholder="请输入群组描述"
              ></textarea>
            </div>

            <!-- 好友选择部分 -->
            <div class="form-group">
              <label for="selectFriends">选择好友:</label>
              <div class="friends-dropdown">
                <div v-if="friendListLoading" class="loading">加载中...</div>
                <div v-else-if="friends.length === 0" class="loading">
                  无好友
                </div>
                <div v-else>
                  <ul class="friendsList">
                    <li
                      v-for="friend in friends"
                      :key="friend.id"
                      class="friendItem"
                      @click="toggleSelection(friend)"
                      :class="{ selected: selectedFriends.includes(friend) }"
                    >
                      <img
                        :src="
                          friend.avatar || require('@/assets/images/icon.png')
                        "
                        alt="头像"
                        class="friendAvatar"
                      />
                      <span class="friendNickname"
                        >{{ friend.nickname }} (#{{ friend.id }})</span
                      >
                      <span
                        v-if="selectedFriends.includes(friend)"
                        class="checkmark"
                        >✔</span
                      >
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

          <div class="buttonContainer">
            <!-- 取消按钮 -->
            <button @click="cancelCreateGroup" class="cancelButton">
              取消
            </button>

            <!-- 提交按钮 -->
            <button @click="createGroup" class="submitButton">创建群聊</button>
          </div>
        </div>
      </transition>

      <!-- 默认的按钮 -->
      <transition name="createGroupButton">
        <button
          v-if="!isCreating"
          @click="startCreateGroup"
          class="createGroupButton"
        >
          创建群组
        </button>
      </transition>
    </div>
    <div class="groupListContainer">
      <div v-if="groupListLoading" class="loading">加载中...</div>
      <div v-else-if="groups.length === 0" class="loading">无群组</div>
      <!-- 显示群组列表 -->
      <div v-else>
        <ul class="groupsList">
          <li
            v-for="group in groups"
            :key="group.id"
            class="groupItem"
            @click="goToGroup(group.id)"
          >
            <img
              :src="group.avatar || require('@/assets/images/icon.png')"
              alt="头像"
              class="groupAvatar"
            />
            <span class="groupName">{{ group.name }} (#{{ group.id }})</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
import { Role } from "@/config/constants";
import { getFriendList } from "@/utils/friendService";

export default {
  name: "GroupListPage",
  data() {
    return {
      groups: [],
      groupListLoading: false, // 加载状态
      friendListLoading: false,
      isCreating: false,
      newGroup: [],
      selectedFriends: [],
      friends: [],
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  mounted() {
    this.userId = localStorage.getItem("userId"); // 读取 userId
    if (!this.userId) {
      console.error("用户ID不存在，请重新登录");
      return;
    }
    // 在组件挂载后获取群组数据
    this.fetchGroups();
  },
  methods: {
    async fetchGroups() {
      try {
        this.groupListLoading = true;
        this.groups = [];
        const response = await this.$axios.get(`/user/groups/${this.userId}`);
        this.groups = response.data.map((item) => item.taskGroup);
        console.log(this.groups);
      } catch (error) {
        console.error("获取群组数据失败:", error);
      } finally {
        this.groupListLoading = false; // 加载完成，更新状态
      }
    },

    async createGroup() {
      try {
        console.log(this.newGroup);
        // TODO: 创建群组和拉人分两步会不会不好回滚
        if (this.newGroup.name === "") {
          showToast(this.toast, "群组名不能为空", "error");
          return;
        }
        if (this.selectedFriends.length === 0) {
          showToast(this.toast, "至少需要选择一个好友", "error");
          return;
        }
        const responseNewGroup = await this.$axios.post(
          `/task-group/create`,
          // TODO:修改这个补丁
          {
            name: this.newGroup.name,
            description: this.newGroup.description,
          }
        );
        console.log(responseNewGroup.data);
        await this.$axios.post(
          `/groups/${responseNewGroup.data.id}/members/${this.userId}?role=${Role.Owner}`
        );
        for (const friend of this.selectedFriends) {
          await this.$axios.post(
            `/groups/${responseNewGroup.data.id}/members/${friend.id}?role=${Role.Admin}`
          );
        }
        showToast(this.toast, "群组创建成功", "success");
        // TODO: 群组创建完没有直接更新，得刷新
      } catch (error) {
        console.error("创建群组失败:", error);
        showToast(this.toast, "创建群组失败", "error");
      }
    },

    goToGroup(id) {
      this.$router.push(`/group/${id}`);
    },

    // 选择或取消选择好友
    toggleSelection(friend) {
      const index = this.selectedFriends.indexOf(friend);
      if (index === -1) {
        // 如果好友未被选中，添加到选中列表
        this.selectedFriends.push(friend);
      } else {
        // 如果好友已经选中，取消选择
        this.selectedFriends.splice(index, 1);
      }
    },

    // 切换到创建任务模式
    async startCreateGroup() {
      this.isCreating = true;
      this.friendListLoading = true;
      this.friends = await getFriendList(this.userId);
      this.friendListLoading = false;
    },

    // 取消创建任务
    cancelCreateGroup() {
      this.isCreating = false;
      this.resetForm(); // 重置表单
    },

    resetForm() {
      this.newGroup = {
        name: "",
        description: "",
      };
    },
  },
};
// TODO: 整理css
// TODO: 抽取复用好友列表
</script>

<style scoped>
.container {
  text-align: center;
  margin-top: 30px;
}

.groupListContainer,
.createGroupContainer {
  margin: 25px;
  padding: 25px;
  background-color: rgba(128, 128, 128, 0.1); /* 灰色，10%透明度 */
  border-radius: 10px; /* 圆角 */
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2); /* 阴影效果 */
  overflow-x: hidden;
}

.groupItem {
  list-style: none;
  display: flex;
  align-items: center; /* 使头像和昵称垂直居中 */
  margin-bottom: 15px; /* 每个列表项之间的间距 */
  transition: 0.2s ease;
  transform-origin: left;
}

.groupItem:hover {
  transform: scale(1.1);
}

.groupAvatar {
  width: 5%;
  height: 5%;
  border-radius: 50%;
  margin-right: 1.25%; /* 头像和昵称之间的间距 */
}

.loading {
  font-size: clamp(1rem, 2vw, 5rem);
  font-weight: bold;
}

.groupName {
  font-size: clamp(1rem, 2vw, 5rem);
  font-weight: bold;
  text-align: left; /* 确保昵称居中对齐 */
  flex-grow: 1; /* 如果有更多的空间，昵称会自动占用 */
}

.groupForm {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  overflow: hidden;
  justify-content: center;
  align-items: center;
}

.newGroupInputFields {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 70%;
}

.newGroupInputFields input,
.newGroupInputFields textarea,
.newGroupInputFields select {
  padding: 10px;
  margin-top: 5px;
  border-radius: 5px;
  border: 1px solid #ccc;
  width: 100%;
  box-sizing: border-box;
}

.submitButton,
.cancelButton,
.createGroupButton {
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;

  margin-left: 10px;
  color: white;
  width: 100px;
}

.submitButton {
  background-color: #007bff;
}

.cancelButton {
  background-color: #dc3545;
}

.createGroupButton {
  background-color: #28a745;
}

.buttonContainer {
  display: flex;
  align-items: center;
}

.friendsList {
  padding: 0;
}

.friendItem {
  list-style: none;
  display: flex;
  align-items: center; /* 使头像和昵称垂直居中 */
  margin-bottom: 15px; /* 每个列表项之间的间距 */
}

.friendAvatar {
  width: 5%;
  height: 5%;
  border-radius: 50%;
  margin-right: 1.25%; /* 头像和昵称之间的间距 */
}

.friendNickname {
  font-size: clamp(1rem, 2vw, 5rem);
  font-weight: bold;
  text-align: left; /* 确保昵称居中对齐 */
  flex-grow: 1; /* 如果有更多的空间，昵称会自动占用 */
}
</style>
