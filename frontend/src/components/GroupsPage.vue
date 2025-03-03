<!-- GroupsPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Groups Page</h1>
    <!-- 添加好友输入框和按钮 -->
    <div class="createGroupContainer">
      <button @click="createGroup" class="createGroupButton">创建群聊</button>
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
//import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
export default {
  name: "GroupsPage",
  data() {
    return {
      groups: [
        { id: 1, name: "群组1", description: "1111111111111" },
        { id: 2, name: "群组2", description: "2111111111111" },
      ],
      groupListLoading: false, // 加载状态
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  mounted() {
    // 在组件挂载后获取群组数据
    //this.fetchGroups();
  },
  methods: {
    async fetchGroups() {
      try {
        this.groups = [];
        //const responseUserId = await this.$axios.get(`user/info`);
        const response = await this.$axios.get(`/task-group`);
        this.groups = response.data;
      } catch (error) {
        console.error("获取群组数据失败:", error);
      } finally {
        this.groupListLoading = false; // 加载完成，更新状态
      }
    },
    async createGroup() {
      try {
        // TODO: 在下一行代码中需要传入TaskGroup(name和description)
        await this.$axios.post(`/task-group`);
      } catch (error) {
        console.error("创建群组失败:", error);
      }
    },
    goToGroup(id) {
      this.$router.push(`/group/${id}`);
    },
  },
};
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

.createGroupButton {
  margin-left: 1%;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 100px;
}
</style>
