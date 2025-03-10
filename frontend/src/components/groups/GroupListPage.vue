<!-- GroupListPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Groups Page</h1>
    <!-- 添加好友输入框和按钮 -->
    <div class="blockContainer">
      <transition name="createGroupContainerTransition">
        <!-- 控制是否显示输入框 -->
        <div class="createGroupContainer" v-if="isCreating">
          <!-- 输入框区域 -->
          <div class="allInputFields">
            <div class="formWithLabelAndInput">
              <label for="newGroupName">群组名称:</label>
              <input
                type="text"
                id="newGroupName"
                v-model="newGroup.name"
                placeholder="请输入群组名称"
              />
            </div>
            <div class="formWithLabelAndInput">
              <label for="newGroupDescription">群组描述:</label>
              <textarea
                id="newGroupDescription"
                v-model="newGroup.description"
                placeholder="请输入群组描述"
              ></textarea>
            </div>
            <!-- 好友选择部分 -->
            <div class="formWithLabelAndInput">
              <label for="selectFriends">选择好友:</label>
              <FriendSelection
                v-model="selectedFriends"
                :userId="userId"
                :unavailableFriendIds="[]"
              />
            </div>
          </div>

          <div class="doubleButtonContainer">
            <!-- 取消按钮 -->
            <button @click="cancelCreateGroup" class="button warningButton">
              取消
            </button>

            <!-- 提交按钮 -->
            <button @click="createGroup" class="button normalButton">
              创建群聊
            </button>
          </div>
        </div>
      </transition>

      <!-- 默认的按钮 -->
      <transition name="createGroupButton">
        <button
          v-if="!isCreating"
          @click="startCreateGroup"
          class="button extendButton"
        >
          创建群组
        </button>
      </transition>
    </div>
    <div class="blockContainer">
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
              class="avatar"
            />
            <span class="nickname">{{ group.name }} (#{{ group.id }})</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import FriendSelection from "@/components/friends/FriendSelection.vue";

import { Role } from "@/config/constants";

import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";

export default {
  name: "GroupListPage",
  components: { FriendSelection },
  data() {
    return {
      groups: [],
      groupListLoading: false, // 加载状态
      friendListLoading: false,
      isCreating: false,
      newGroup: [],
      selectedFriends: [],
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
      // TODO: 按最新消息更新时间顺序显示
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
        const responseNewGroup = await this.$axios.post(`/task-group/create`, {
          name: this.newGroup.name,
          description: this.newGroup.description,
        });
        console.log(responseNewGroup.data);
        await this.$axios.post(
          `/groups/${responseNewGroup.data.id}/members/${this.userId}`,
          null,
          {
            params: { role: Role.Owner },
          }
        );
        for (const friend of this.selectedFriends) {
          await this.$axios.post(
            `/groups/${responseNewGroup.data.id}/members/${friend.id}`,
            null,
            {
              params: { role: Role.Member },
            }
          );
        }
        this.fetchGroups();
        this.cancelCreateGroup();
        showToast(this.toast, "群组创建成功", "success");
      } catch (error) {
        console.error("创建群组失败:", error);
        showToast(this.toast, "创建群组失败", "error");
      }
    },

    goToGroup(id) {
      this.$router.push(`/group/${id}`);
    },

    // 切换到创建任务模式
    async startCreateGroup() {
      this.isCreating = true;
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
</script>

<style scoped>
.groupItem {
  display: flex;
  align-items: center; /* 使头像和昵称垂直居中 */
  margin-bottom: 15px; /* 每个列表项之间的间距 */
  transition: 0.2s ease;
  transform-origin: left;
}

.groupItem:hover {
  transform: scale(1.1);
}

.createGroupContainer {
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
  justify-content: center;
  align-items: center;
}
</style>
