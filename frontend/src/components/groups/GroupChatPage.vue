<!-- GroupChatPage.vue -->
<template>
  <div class="container">
    <div class="chatContainer">
      <!-- 顶部群组名称 -->
      <header class="groupHeader">
        <h1>{{ groupData.name }} (#{{ groupId }}) ({{ groupMembers.length }})</h1>
        <p>{{ groupData.description }}</p>
        <div class="doubleButtonContainer">
          <button class="button sideBarButton" @click="toggleTaskSidebar">
            📋 {{ $t('groups.tasksButton') }}
          </button>
          <button class="button sideBarButton" @click="toggleGroupSidebar">
            {{ $t('groups.groupInfoButton') }}
          </button>
        </div>
      </header>

      <!-- 聊天记录区域 -->
      <div class="chatArea">
        <div v-if="messageLoading">
          <MySpinner />
        </div>
        <div v-else>
          <ul class="messageList">
            <li
              v-for="message in messageList"
              :key="message.id"
              ref="messageItemRef"
              class="messageItem"
              :class="{
                alignRight: isSentByCurrentUser(message),
                alignLeft: !isSentByCurrentUser(message),
              }"
            >
              <img
                v-if="!isSentByCurrentUser(message)"
                :src="$store.getters.getAvatar(message.sender.avatarId)"
                :alt="$t('common.avatarAlt')"
                class="messageAvatar leftAvatar"
              />
              <div class="messageContainer">
                <div>{{ message.sender.username }}</div>
                <div class="messageContent">
                  {{ message.content }}
                </div>
              </div>
              <img
                v-if="isSentByCurrentUser(message)"
                :src="$store.getters.getAvatar(message.sender.avatarId)"
                :alt="$t('common.avatarAlt')"
                class="messageAvatar rightAvatar"
              />
            </li>
          </ul>
        </div>
      </div>

      <!-- 底部输入框 -->
      <footer class="chatInputArea">
        <input
          v-model="newMessage"
          type="text"
          :placeholder="$t('groups.messagePlaceholder')"
          class="chatInput"
          @keyup.enter="sendMessage"
        />
        <button class="button normalButton" @click="sendMessage">
          {{ $t('groups.sendButton') }}
        </button>
      </footer>
    </div>

    <!-- 右侧任务侧边栏 -->
    <SideBar
      :title="$t('groups.sidebarTitles.tasks')"
      :is-visible="showTaskSidebar"
      :content-component="TaskList"
      :content-props="{
        tasks: groupTasks,
        taskListLoading: taskListLoading,
        groupId: groupId,
        isInGroupPage: true,
        refreshTaskList: fetchGroupTasks,
      }"
      @close="toggleTaskSidebar"
    />

    <!-- 右侧群组信息侧边栏 -->
    <SideBar
      :title="$t('groups.sidebarTitles.groupInfo')"
      :is-visible="showGroupSidebar"
      :content-component="GroupEditor"
      :content-props="{
        groupId: groupId,
        groupName: groupData.name,
        groupDescription: groupData.description,
        groupMembers: groupMembers,
        userRole: userRole,
        userId: userId,
        refreshGroupData: fetchGroup,
      }"
      @close="toggleGroupSidebar"
    />
  </div>
</template>

<script>
import { markRaw } from 'vue'

import SideBar from '@/components/common/SideBar.vue'
import TaskList from '@/components/tasks/TaskList.vue'
import GroupEditor from '@/components/groups/GroupEditor.vue'

import { Role } from '@/config/constants'

import { showToast } from '@/utils/toast'
import { useToast } from 'vue-toastification'

import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'
import MySpinner from '@/components/common/MySpinner.vue'

import { fetchMembers, fetchUserRole } from '@/utils/groupService'

export default {
  name: 'GroupChatPage',
  components: { MySpinner, SideBar },
  setup() {
    const toast = useToast()
    return { toast }
  },
  data() {
    return {
      groupId: null,
      groupData: {
        id: 1,
        name: this.$t('groups.groupNameLoading'),
        description: this.$t('groups.groupDescLoading'),
      },
      groupMembers: [],
      isMember: false,
      messageLoading: true,
      newMessage: '',
      messageList: [],
      showTaskSidebar: false,
      showGroupSidebar: false,
      groupTasks: [],
      stompClient: null, // 修改为STOMP客户端
      userId: null,
      userRole: Role.Member,
      taskListLoading: false,
      TaskList: markRaw(TaskList),
      GroupEditor: markRaw(GroupEditor),
    }
  },

  watch: {
    messageList: {
      handler() {
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      },
      deep: true, // 深度监听，确保数组更新时触发
    },
  },
  async mounted() {
    this.user = this.$store.getters.getUser
    if (!this.user) {
      return
    }
    this.userId = this.user.id

    this.groupId = parseInt(this.$route.params.id)
    this.connectWebSocket() // 修改后的连接方法

    this.checkMembership().then(() => {
      if (this.isMember) {
        this.fetchGroup()
        this.fetchUserRole()
        this.fetchMembers()
        this.fetchMessages()
        this.taskListLoading = true
        this.fetchGroupTasks()
      }
    })
  },
  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.deactivate() // 安全断开连接
    }
  },
  methods: {
    async checkMembership() {
      try {
        const response = await this.$axios.get(
          `/groups/${this.groupId}/members/${this.userId}/is-member`
        )
        this.isMember = response.data
        if (!this.isMember) {
          showToast(this.toast, this.$t('groups.errors.notMember'), 'error')
          this.$router.push('/')
        }
      } catch (error) {
        console.error('检查群组权限失败', error)
        if (error.response.data.message) {
          showToast(this.toast, error.response.data.message, 'error')
        } else {
          showToast(this.toast, this.$t('groups.errors.checkPermissionDefault'), 'error')
        }
        this.$router.push('/')
      }
    },

    async fetchGroup() {
      try {
        const response = await this.$axios.get(`/task-group/${this.groupId}`)
        this.groupData = response.data
      } catch (error) {
        console.error('加载群组失败', error)
      }
    },

    async fetchUserRole() {
      this.userRole = await fetchUserRole(this.groupId, this.userId)
    },

    async fetchMembers() {
      this.groupMembers = await fetchMembers(this.groupId)
    },

    async fetchMessages() {
      try {
        const response = await this.$axios.get(`/chat-message/group/${this.groupId}`)
        this.messageList = response.data
      } catch (error) {
        console.error('加载聊天记录失败', error)
      } finally {
        this.messageLoading = false
      }
    },

    isSentByCurrentUser(message) {
      return this.userId === message.sender.id
    },

    connectWebSocket() {
      const socket = new SockJS('/chatroom')
      this.stompClient = Stomp.over(socket)

      this.stompClient.connect(
        {},
        (frame) => {
          console.log('STOMP连接成功:', frame)

          if (!this.groupId) {
            console.error('groupId 未定义，无法订阅 WebSocket 主题')
            return
          }

          this.subscription = this.stompClient.subscribe(
            `/topic/group/${this.groupId}`,
            (message) => {
              const receivedMessage = JSON.parse(message.body)
              if (receivedMessage.sender.id !== this.userId) {
                this.messageList.push(receivedMessage)
              }
            }
          )
        },
        (error) => {
          console.error('STOMP 连接失败:', error)
        }
      )
    },

    sendMessage() {
      if (!this.newMessage.trim()) {
        showToast(this.toast, this.$t('groups.errors.emptyMessage'), 'error')
        return
      }

      const message = {
        content: this.newMessage,
        sender: this.user,
        taskGroup: { id: this.groupId },
      }

      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.publish({
          destination: `/chat/sendMessage`,
          body: JSON.stringify(message),
        })
        this.messageList.push(message)
        this.newMessage = ''
      } else {
        console.error('STOMP连接未就绪')
        showToast(this.toast, this.$t('groups.errors.connectionNotReady'), 'error')
      }
    },

    scrollToBottom() {
      const container = this.$refs.messageItemRef
      if (container) {
        if (container.length > 0) {
          container[container.length - 1].scrollIntoView({
            behavior: 'smooth',
          })
        }
      }
    },

    toggleTaskSidebar() {
      this.showTaskSidebar = !this.showTaskSidebar
      this.showGroupSidebar = false
      if (this.showTaskSidebar) {
        this.fetchGroupTasks()
      }
    },

    async fetchGroupTasks() {
      try {
        const response = await this.$axios.get(`/tasks/group/${this.groupId}`)
        this.groupTasks = response.data
        this.groupTasks.forEach(async (task) => {
          task.userRole = this.userRole
        })
      } catch (error) {
        console.error('获取任务失败', error)
      } finally {
        this.taskListLoading = false
      }
    },

    toggleGroupSidebar() {
      this.showGroupSidebar = !this.showGroupSidebar
      this.showTaskSidebar = false
    },
  },
}
</script>

<style scoped>
.chatContainer {
  display: flex;
  flex-direction: column;
  text-align: center;
  height: calc(100vh - 186px);
}

.groupHeader {
  background: #686160;
  color: white;
  text-align: center;
  padding: 10px;
  font-size: 18px;
  font-weight: bold;
  border-radius: 10px;
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 10px;
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
}

.messageItem {
  display: flex;
}

.alignLeft {
  text-align: left;
  align-self: flex-start; /* 左对齐 */
}

.alignRight {
  text-align: right;
  align-self: flex-end; /* 右对齐 */
}

.messageContainer {
  display: flex;
  flex-direction: column;
}

.alignLeft .messageContainer {
  align-items: flex-start;
}

.alignRight .messageContainer {
  align-items: flex-end;
}

.messageContent {
  display: inline-block;
  width: fit-content;
  max-width: 600px; /* 限制消息宽度 */
  padding: 10px;
  border-radius: 8px;
  word-wrap: break-word;
  text-align: left;
}

.alignLeft .messageContent {
  background-color: #f0f0f0;
}

.alignRight .messageContent {
  background-color: #686160;
  color: white;
}

/* 底部输入框 */
.chatInputArea {
  display: flex;
  align-items: center;
  gap: 10px;
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
