<!-- FriendsPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Friends Page</h1>
    <!-- 添加好友输入框和按钮 -->
    <div class="blockContainer">
      <div class="addFriendContainer">
        <input
          v-model="friendId"
          type="text"
          placeholder="输入用户Id"
          class="addFriendInput"
        />
        <button @click="sendFriendRequest" class="button normalButton">
          添加好友
        </button>
      </div>
    </div>
    <!-- 好友申请列表 -->
    <div v-if="pendingRequests.length > 0" class="blockContainer">
      <div v-if="friendRequestLoading" class="loading">加载中...</div>
      <div v-else>
        <ul class="friendRequestList">
          <li
            v-for="request in pendingRequests"
            :key="request.id"
            class="friendRequestItem"
          >
            <img
              :src="
                request.sender.avatar || require('@/assets/images/icon.png')
              "
              alt="头像"
              class="avatar"
            />
            <span class="nickname">
              {{ request.sender.username }} (#{{ request.sender.id }})
              想加你为好友
            </span>
            <div class="doubleButtonContainer">
              <button
                class="button normalButton"
                @click="acceptRequest(request.id)"
              >
                接收
              </button>
              <button
                class="button normalButton"
                @click="rejectRequest(request.id)"
              >
                拒绝
              </button>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <div class="blockContainer">
      <div v-if="friendListLoading" class="loading">加载中...</div>
      <div v-else-if="friends.length === 0" class="loading">无好友</div>
      <!-- 显示好友列表 -->
      <div v-else>
        <ul class="friendsList">
          <li v-for="friend in friends" :key="friend.id" class="friendItem">
            <img
              :src="friend.avatar || require('@/assets/images/icon.png')"
              alt="头像"
              class="avatar"
            />
            <span class="nickname"
              >{{ friend.username }} (#{{ friend.id }})</span
            >
            <button
              class="button normalButton"
              @click="deleteFriend(friend.friend)"
            >
              删除好友
            </button>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { fetchFriends, getFriendList } from "@/utils/friendService";

import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";

export default {
  name: "FriendsPage",
  data() {
    return {
      friendId: "",
      friends: [], // 用来存储好友列表
      pendingRequests: [], // 存储待处理的好友申请列表
      friendListLoading: false, // 加载状态
      friendRequestLoading: false,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  async mounted() {
    this.userId = localStorage.getItem("userId"); // 读取 userId
    if (!this.userId) {
      console.error("用户ID不存在，请重新登录");
      return;
    }
    // 在组件挂载后获取好友数据
    // 调用公共方法

    this.friendListLoading = true;
    this.friends = await getFriendList(this.userId);
    this.friendListLoading = false;
    await this.fetchPendingRequests();
  },
  methods: {
    async fetchPendingRequests() {
      try {
        this.friendRequestLoading = true;
        const response = await this.$axios.get(
          `/friend-requests/receiver/${this.userId}/status/pending`
        );
        this.pendingRequests = response.data; // 获取待处理的好友申请
      } catch (error) {
        console.error("获取好友申请数据失败:", error);
      } finally {
        this.friendRequestLoading = false; // 加载完成，更新状态
      }
    },

    // 发送添加好友请求
    async sendFriendRequest() {
      if (!this.friendId.trim()) {
        showToast(this.toast, "请输入用户id！", "error"); // 提示错误信息
        return;
      }

      try {
        await this.$axios.post(
          "/friend-requests/send", // 后端的接口
          {
            senderId: Number(this.userId), // 将字符串转换为数字
            receiverId: Number(this.friendId), // 将字符串转换为数字
          }
        );

        // 处理成功的响应
        showToast(this.toast, "好友请求已发送！", "success");
      } catch (error) {
        console.error("发送好友请求失败:", error);
        if (error.response.data.message) {
          showToast(this.toast, error.response.data.message, "error");
        } else {
          showToast(this.toast, "发送好友请求失败，请稍后再试！", "error");
        }
      }
    },

    async acceptRequest(requestId) {
      try {
        // 接受好友申请
        console.log(localStorage.getItem("userId"));
        await this.$axios.post(`/friend-requests/accept/${requestId}`);

        // 更新待处理申请和好友列表
        await this.fetchPendingRequests();
        this.friends = await fetchFriends(this.userId);

        // 提示用户已成功接收好友请求
        showToast(this.toast, "已成功接收好友申请", "success");
      } catch (error) {
        console.error("接受好友申请失败:", error);
        showToast(this.toast, "接受好友申请失败", "error");
      }
    },

    async rejectRequest(requestId) {
      try {
        // 拒绝好友申请
        await this.$axios.post(`/friend-requests/reject/${requestId}`);

        // 更新待处理申请
        await this.fetchPendingRequests();

        // 提示用户已成功拒绝好友请求
        showToast(this.toast, "已成功拒绝好友申请", "success");
      } catch (error) {
        console.error("拒绝好友申请失败:", error);
        showToast(this.toast, "拒绝好友申请失败", "error");
      }
    },

    async deleteFriend(friend) {
      try {
        const responseUser = await this.$axios.get(`user/info`);
        await this.$axios.delete(`/friendships/remove`, {
          data: {
            user: responseUser.data,
            friend: friend,
          },
        });
        // 更新好友列表
        this.friends = await fetchFriends(this.userId);
        showToast(this.toast, "已成功删除好友", "success");
      } catch (error) {
        console.error("删除好友失败:", error);
        showToast(this.toast, "删除好友失败", "error");
      }
    },
  },
};
</script>

<style scoped>
.addFriendContainer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.friendRequestItem {
  display: flex;
  align-items: center; /* 使头像和昵称垂直居中 */
}

.addFriendInput {
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
  width: 70%;
  box-sizing: border-box;
}

.friendRequestList {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
</style>
