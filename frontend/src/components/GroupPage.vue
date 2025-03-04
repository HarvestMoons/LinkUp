<!-- GroupPage.vue -->
<template>
  <div class="container">
    <!-- 顶部群组名称 -->
    <header class="groupHeader">
      <h1>
        {{ groupData.name }} (#{{ groupId }}) ({{ groupData.members.length }})
      </h1>
      <p>{{ groupData.description }}</p>
    </header>

    <!-- 聊天记录区域 -->
    <div class="chatArea">
      <p>这里是聊天内容区域...</p>
    </div>

    <!-- 底部输入框 -->
    <footer class="chatInputArea">
      <input
        type="text"
        v-model="message"
        placeholder="输入消息..."
        class="chatInput"
      />
      <button @click="sendMessage" class="sendButton">发送</button>
    </footer>
  </div>
</template>

<script>
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
import { useRoute } from "vue-router";

export default {
  name: "GroupPage",
  data() {
    return {
      groupId: null,
      groupData: {
        id: 1,
        name: "群组名称",
        description: "群组描述",
        members: [{ id: 111 }, { id: 12 }],
      },
      isMember: false,
      loading: true,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  async mounted() {
    this.groupId = useRoute().params.id;
    //await this.checkMembership();
    //if (this.isMember) {
    //await this.fetchGroup();
    //}
  },
  methods: {
    async checkMembership() {
      try {
        const response = await this.$axios.get(
          `/groups/${this.groupId}/members/check-member`
        );
        this.isMember = response.data;
        if (!this.isMember) {
          showToast(this.toast, "你不是该群组的成员，无法访问！", "error");
          this.$router.push("/"); // 重定向到首页
        }
      } catch (error) {
        console.error("检查群组权限失败", error);
        showToast(this.toast, "权限检查失败，请重试！", "error");
        this.$router.push("/");
      } finally {
        this.loading = false;
      }
    },
    async fetchGroup() {
      try {
        const response = await this.$axios.get(`/task-group/${this.groupId}`);
        this.groupData.value = response.data;
      } catch (error) {
        console.error("加载群组失败", error);
      }
    },
  },
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  text-align: center;
  margin-top: 30px;
  min-height: 100vh;
}

.groupHeader {
  background: #4caf50;
  color: white;
  text-align: center;
  padding: 15px;
  font-size: 18px;
  font-weight: bold;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
}
/* 聊天区域 */
.chatArea {
  flex-grow: 1;
  padding: 20px;
  background: white;
  overflow-y: auto;
  border-top: 1px solid #ddd;
  border-bottom: 1px solid #ddd;
}

/* 底部输入框 */
.chatInputArea {
  display: flex;
  align-items: center;
  padding: 10px;
  background: white;
  box-shadow: 0px -2px 5px rgba(0, 0, 0, 0.1);
}

/* 输入框 */
.chatInput {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  outline: none;
}

.sendButton {
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  margin-left: 10px;
  color: white;
  width: 100px;
  background-color: #007bff;
}
</style>