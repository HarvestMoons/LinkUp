<!-- GroupChatPage.vue -->
<template>
  <div class="container">
    <div class="chatContainer">
      <!-- 顶部群组名称 -->
      <header class="groupHeader">
        <h1>
          {{ groupData.name }} (#{{ groupId }}) ({{ groupMembers.length }})
        </h1>
        <p>{{ groupData.description }}</p>
        <div class="sideBarButtonContainer">
          <button @click="toggleTaskSidebar" class="sideBarButton">
            📋 任务
          </button>
          <button @click="toggleGroupSidebar" class="sideBarButton">
            群组信息
          </button>
        </div>
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
              <div>
                <div>{{ message.sender.username }}</div>
                <div
                  class="messageContent"
                  :class="{
                    rightContent: isSentByCurrentUser(message),
                    leftContent: !isSentByCurrentUser(message),
                  }"
                >
                  {{ message.content }}
                </div>
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

    <!-- 右侧任务侧边栏 -->
    <SideBar
      title="群组任务"
      :isVisible="showTaskSidebar"
      :contentComponent="TaskList"
      :contentProps="{
        tasks: groupTasks,
        taskListLoading: taskListLoading,
        groupId: groupId,
        fetchTasks: fetchGroupTasks,
      }"
      @close="toggleTaskSidebar"
    />

    <!-- 右侧群组信息侧边栏 -->
    <SideBar
      title="群组信息"
      :isVisible="showGroupSidebar"
      :contentComponent="GroupEditor"
      :contentProps="{
        groupId: groupId,
        groupName: groupData.name,
        groupDescription: groupData.description,
        groupMembers: groupMembers,
        userRole: 'admin',
        userId: userId,
      }"
      @close="toggleGroupSidebar"
    />
  </div>
</template>

<script>
// TODO: 任务显示、群组信息编辑组件
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
import SockJS from "sockjs-client"; // 新增
import { Client } from "@stomp/stompjs"; // 新增
import SideBar from "@/components/SideBar.vue";
import TaskList from "@/components/TaskList.vue";
import GroupEditor from "@/components/GroupEditor.vue";

export default {
  components: { SideBar },
  name: "GroupChatPage",
  data() {
    return {
      groupId: null,
      groupData: {
        id: 1,
        name: "群组名称加载中",
        description: "群组描述加载中",
      },
      groupMembers: [],
      isMember: false,
      messageLoading: true,
      newMessage: "",
      messageList: [],
      showTaskSidebar: false,
      showGroupSidebar: false,
      groupTasks: [],
      stompClient: null, // 修改为STOMP客户端
      TaskList,
      GroupEditor,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  async mounted() {
    this.user = JSON.parse(localStorage.getItem("user"));
    if (!this.user) {
      console.error("用户不存在，请重新登录");
      return;
    }
    this.userId = this.user.id;

    this.groupId = this.$route.params.id;
    this.connectWebSocket(); // 修改后的连接方法

    this.checkMembership().then(() => {
      if (this.isMember) {
        this.fetchGroup();
        this.fetchMembers();
        this.fetchMessages();
      }
    });
  },
  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.deactivate(); // 安全断开连接
    }
  },
  methods: {
    async checkMembership() {
      try {
        const response = await this.$axios.get(
          `/groups/${this.groupId}/members/${this.userId}/is-member`
        );
        this.isMember = response.data;
        if (!this.isMember) {
          showToast(this.toast, "你不是该群组的成员，无法访问！", "error");
          this.$router.push("/");
        }
      } catch (error) {
        console.error("检查群组权限失败", error);
        if (error.response.data.message) {
          showToast(this.toast, error.response.data.message, "error");
        } else {
          showToast(this.toast, "权限检查失败，请重试！", "error");
        }
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
    async fetchMembers() {
      try {
        const response = await this.$axios.get(
          `/groups/${this.groupId}/members`
        );
        this.groupMembers = response.data.map((item) => {
          return {
            role: item.role,
            ...item.user,
          };
        });
        console.log(this.groupMembers);
      } catch (error) {
        console.error("加载群组成员失败", error);
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
    connectWebSocket() {
      //todo:接收到新消息时，滚轮自动划到最下面
      const serverUrl = `http://localhost:8099/chatroom`;
      const socket = new SockJS(serverUrl);

      this.stompClient = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log("STOMP连接成功");

          // 订阅群组主题（匹配后端@SendTo配置）
          // 保存subscription以便取消订阅
          this.subscription = this.stompClient.subscribe(
            `/topic/group/${this.groupId}`
          );
        },
        onStompError: (frame) => {
          console.error("STOMP协议错误:", frame.headers.message);
        },
        onWebSocketClose: () => {
          console.log("连接关闭，尝试重连...");
        },
      });

      this.stompClient.activate();
    },

    sendMessage() {
      if (!this.newMessage.trim()) {
        showToast(this.toast, "发送的内容不能为空", "error");
        return;
      }

      // 消息结构需要匹配后端的ChatMessage对象
      const message = {
        content: this.newMessage,
        sender: this.user,
        taskGroup: { id: this.groupId }, // 关键字段，用于后端路由
      };

      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.publish({
          destination: `/chat/sendMessage`, // 匹配@MessageMapping
          body: JSON.stringify(message),
        });
        this.messageList.push(message);
        this.newMessage = "";
      } else {
        console.error("STOMP连接未就绪");
        showToast(this.toast, "连接尚未建立，请稍后重试", "error");
      }
    },

    toggleTaskSidebar() {
      this.showTaskSidebar = !this.showTaskSidebar;
      this.showGroupSidebar = false;
      if (this.showTaskSidebar) {
        this.fetchGroupTasks();
      }
    },

    async fetchGroupTasks() {
      this.taskListLoading = true;
      try {
        const response = await this.$axios.get(`/tasks/group/${this.groupId}`);
        this.groupTasks = response.data;
      } catch (error) {
        console.error("获取任务失败", error);
      } finally {
        this.taskListLoading = false;
      }
    },

    toggleGroupSidebar() {
      this.showGroupSidebar = !this.showGroupSidebar;
      this.showTaskSidebar = false;
    },
  },
};
</script>

<style scoped>
.container {
  margin-top: 30px;
}

.chatContainer {
  display: flex;
  flex-direction: column;
  text-align: center;
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

.sideBarButtonContainer {
  display: flex;
  gap: 10px;

  justify-content: center;
  align-items: center;
}

.sideBarButton {
  background: lightgreen;
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  width: 100px;
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
  text-align: left;
  align-self: flex-start; /* 左对齐 */
}

.align-right {
  text-align: right;
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