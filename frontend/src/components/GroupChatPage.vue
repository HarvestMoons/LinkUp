<!-- GroupChatPage.vue -->
<template>
  <div class="container">
    <!-- 顶部群组名称 -->
    <header class="groupHeader">
      <h1>{{ groupData.name }} (#{{ groupId }})</h1>
      <p>{{ groupData.description }}</p>
    </header>

    <!-- 聊天记录区域 -->
    <div class="chatArea">
      <div v-if="messageLoading" class="loading">加载中...</div>
      <div v-else>
        <ul class="messageList">
          <li
              v-for="message in messageList"
              :key="message.id"
              class="messageItem"
              :class="{
              'align-right': isSentByCurrentUser(message),
              'align-left': !isSentByCurrentUser(message),
            }"
          >
            <img
                v-if="!isSentByCurrentUser(message)"
                :src="
                message.sender.avatar || require('@/assets/images/icon.png')
              "
                alt="头像"
                class="messageAvatar leftAvatar"
            />
            <div
                class="messageContent"
                :class="{
                rightContent: isSentByCurrentUser(message),
                leftContent: !isSentByCurrentUser(message),
              }"
            >
              {{ message.content }}
            </div>
            <img
                v-if="isSentByCurrentUser(message)"
                :src="
                message.sender.avatar || require('@/assets/images/icon.png')
              "
                alt="头像"
                class="messageAvatar rightAvatar"
            />
          </li>
        </ul>
      </div>
    </div>

    <!-- 底部输入框 -->
    <footer class="chatInputArea">
      <input
          type="text"
          v-model="newMessage"
          placeholder="输入消息..."
          class="chatInput"
      />
      <button @click="sendMessage" class="sendButton">发送</button>
    </footer>
  </div>
</template>

<script>
// TODO: 任务显示、群组信息编辑组件
import {showToast} from "@/utils/toast";
import {useToast} from "vue-toastification";
import {useRoute} from "vue-router";

export default {
  name: "GroupChatPage",
  data() {
    return {
      groupId: null,
      groupData: {
        id: 1,
        name: "群组名称",
        description: "群组描述",
        members: [{id: 111}, {id: 12}],
      },
      isMember: false,
      messageLoading: true,
      newMessage: "",
      messageList: [],
      socket: null, // WebSocket 实例
    };
  },
  setup() {
    const toast = useToast();
    return {toast};
  },
  async mounted() {
    this.userId = localStorage.getItem("userId"); // 读取 userId
    if (!this.userId) {
      console.error("用户ID不存在，请重新登录");
      return;
    }
    this.groupId = useRoute().params.id;
    await this.checkMembership();
    if (this.isMember) {
      await this.fetchGroup();
      await this.fetchMessages();
      this.initWebSocket(); // 启动 WebSocket 连接
    }
  },
  beforeUnmount() {
    if (this.socket) {
      this.socket.close(); // 组件卸载时关闭 WebSocket 连接
    }
  },
  methods: {
    async checkMembership() {
      try {
        const response = await this.$axios.get(
            `/groups/${this.groupId}/members/is-member/${this.userId}`
        );
        this.isMember = response.data;
        if (!this.isMember) {
          showToast(this.toast, "你不是该群组的成员，无法访问！", "error");
          this.$router.push("/");
        }
      } catch (error) {
        console.error("检查群组权限失败", error);
        showToast(this.toast, "权限检查失败，请重试！", "error");
        this.$router.push("/");
      }
    },
    async fetchGroup() {
      try {
        const response = await this.$axios.get(`/task-group/${this.groupId}`);
        this.groupData = response.data;
      } catch (error) {
        console.error("加载群组失败", error);
      }
    },
    async fetchMessages() {
      try {
        const response = await this.$axios.get(
            `/chat-message/group/${this.groupId}`
        );
        this.messageList = response.data;
      } catch (error) {
        console.error("加载聊天记录失败", error);
      } finally {
        this.messageLoading = false;
      }
    },
    isSentByCurrentUser(message) {
      return this.userId === message.sender.id;
    },
    async sendMessage() {
      try {
        if (this.newMessage === "") {
          showToast(this.toast, "发送的内容不能为空", "error");
          return;
        }
        await this.$axios.post("/chat-message/send", null, {
          params: {
            groupId: this.groupId,
            senderId: this.userId,
            content: this.newMessage,
          },
        });
        this.newMessage = "";
        // 这里不再调用 `fetchMessages()`，因为 WebSocket 会自动更新
      } catch (error) {
        console.error("发送信息失败", error);
      }
    },
    initWebSocket() {
      this.socket = new WebSocket(`ws://8.155.47.138/ws/chat/${this.groupId}`);

      this.socket.onopen = () => {
        console.log("WebSocket 连接已建立");
      };

      this.socket.onmessage = (event) => {
        try {
          const newMessage = JSON.parse(event.data);
          this.messageList.push(newMessage); // 直接更新消息列表
        } catch (error) {
          console.error("解析 WebSocket 消息失败", error);
        }
      };

      this.socket.onerror = (error) => {
        console.error("WebSocket 发生错误", error);
      };

      this.socket.onclose = () => {
        console.log("WebSocket 连接已关闭");
      };
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
  height: calc(100vh - 157px);
}

.groupHeader {
  background: #4caf50;
  color: white;
  text-align: center;
  padding: 10px;
  font-size: 18px;
  font-weight: bold;
  border-radius: 10px;
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2);
}

/* 聊天区域 */
.chatArea {
  flex-grow: 1;
  padding: 20px;
  overflow-y: auto;
}

.messageList {
  display: flex;
  flex-direction: column; /* 纵向排列 */
  gap: 10px; /* 每个消息之间的间距 */
  list-style: none;
  padding: 0;
}

.messageItem {
  display: flex;
}

.messageContent {
  max-width: 600px; /* 限制消息宽度 */
  padding: 10px;
  border-radius: 8px;
  word-wrap: break-word;
  text-align: left;
}

.align-left {
  align-self: flex-start; /* 左对齐 */
}

.align-right {
  align-self: flex-end; /* 右对齐 */
}

.leftContent {
  background-color: #f0f0f0;
}

.rightContent {
  background-color: #007bff;
  color: white;
}

/* 底部输入框 */
.chatInputArea {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: rgba(128, 128, 128, 0.1);
  border-radius: 10px;
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2);
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

.messageAvatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  margin: 0 8px;
}

.leftAvatar {
  margin-right: 8px; /* 让头像和文字有间距 */
}

.rightAvatar {
  margin-left: 8px;
}
</style>