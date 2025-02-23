<!-- FriendsPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Friends Page</h1>
    <!-- 添加好友输入框和按钮 -->
    <div class="addFriendContainer">
      <input
        v-model="friendId"
        type="text"
        placeholder="输入用户Id"
        class="addFriendInput"
      />
      <button @click="sendFriendRequest" class="addFriendButton">
        添加好友
      </button>
    </div>
    <!-- 好友申请列表 -->
    <div v-if="pendingRequests.length > 0" class="friendRequestContainer">
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
              class="friendAvatar"
            />
            <span class="friendNickname">
              {{ request.sender.username }} (#{{ request.sender.id }})
              想加你为好友
            </span>
            <button
              class="acceptFriendButton"
              @click="acceptRequest(request.id)"
            >
              接收
            </button>
            <button
              class="rejectFriendButton"
              @click="rejectRequest(request.id)"
            >
              拒绝
            </button>
          </li>
        </ul>
      </div>
    </div>
    <div class="friendListContainer">
      <div v-if="friendListLoading" class="loading">加载中...</div>
      <div v-else-if="friends.length === 0" class="loading">无好友</div>
      <!-- 显示好友列表 -->
      <div v-else>
        <ul class="friendsList">
          <li v-for="friend in friends" :key="friend.id" class="friendItem">
            <img
              :src="friend.avatar || require('@/assets/images/icon.png')"
              alt="头像"
              class="friendAvatar"
            />
            <span class="friendNickname"
              >{{ friend.nickname }} (#{{ friend.id }})</span
            >
            <button
              class="deleteFriendButton"
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
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
export default {
  name: "FriendsPage",
  data() {
    return {
      friendId: "",
      friends: [], // 用来存储好友列表
      pendingRequests: [], // 存储待处理的好友申请列表
      friendListLoading: true, // 加载状态
      friendRequestLoading: true,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  mounted() {
    // 在组件挂载后获取好友数据
    this.fetchFriends();
    this.fetchPendingRequests();
    this.fetchPendingRequests();
  },
  methods: {
    async fetchFriends() {
      try {
        this.friends = [];
        const responseUserId = await this.$axios.get(
          `${this.$CONSTANT.PUBLIC_AUTH_API}/info`
        );
        const response = await this.$axios.get(
          `/friendships/find/${responseUserId.data.id}`
        );
        response.data.forEach((friendship) => {
          if (friendship.user.id === responseUserId.data.id) {
            this.friends.push({
              nickname: friendship.friend.username,
              id: friendship.friend.id,
              avatar: friendship.friend.avatar,
              friend: friendship.friend,
            });
          } else {
            this.friends.push({
              nickname: friendship.user.username,
              id: friendship.user.id,
              avatar: friendship.user.avatar,
              friend: friendship.user,
            });
          }
        });
        return this.friends;
      } catch (error) {
        console.error("获取好友数据失败:", error);
      } finally {
        this.friendListLoading = false; // 加载完成，更新状态
      }
    },

    async fetchPendingRequests() {
      try {
        const responseUserId = await this.$axios.get(
          `${this.$CONSTANT.PUBLIC_AUTH_API}/info`
        );
        const response = await this.$axios.get(
          `/friend-requests/receiver/${responseUserId.data.id}/status/pending`
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
        const responseUserId = await this.$axios.get(
          `${this.$CONSTANT.PUBLIC_AUTH_API}/info`
        );
        await this.$axios.post(
          "/friend-requests/send", // 后端的接口
          { senderId: responseUserId.data.id, receiverId: this.friendId }
        );

        // 处理成功的响应
        showToast(this.toast, "好友请求已发送！", "success");
        this.friendUsername = ""; // 清空输入框
      } catch (error) {
        console.error("发送好友请求失败:", error);
        showToast(this.toast, "发送好友请求失败，请稍后再试！", "error");
      }
    },

    async acceptRequest(requestId) {
      try {
        // 接受好友申请
        await this.$axios.post(`/friend-requests/accept/${requestId}`);

        // 更新待处理申请和好友列表
        await this.fetchPendingRequests();
        await this.fetchFriends();

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
        const responseUserId = await this.$axios.get(
          `${this.$CONSTANT.PUBLIC_AUTH_API}/info`
        );
        await this.$axios.delete(`/friendships/remove`, {
          data: {
            user: responseUserId.data,
            friend: friend,
          },
        });

        // 更新好友列表
        this.fetchFriends();

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
.container {
  text-align: center;
  margin-top: 30px;
}

.friendListContainer,
.friendRequestContainer,
.addFriendContainer {
  margin: 25px;
  padding: 25px;
  background-color: rgba(128, 128, 128, 0.1); /* 灰色，10%透明度 */
  border-radius: 10px; /* 圆角 */
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2); /* 阴影效果 */
}

.friendRequestItem,
.friendRequestItem,
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

.loading {
  font-size: clamp(1rem, 2vw, 5rem);
  font-weight: bold;
}

.loading {
  font-size: clamp(1rem, 2vw, 5rem);
  font-weight: bold;
}

.friendNickname {
  font-size: clamp(1rem, 2vw, 5rem);
  font-weight: bold;
  text-align: left; /* 确保昵称居中对齐 */
  flex-grow: 1; /* 如果有更多的空间，昵称会自动占用 */
}

.addFriendButton,
.acceptFriendButton,
.rejectFriendButton,
.deleteFriendButton {
  margin-left: 1%;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 100px;
}

.addFriendInput {
  padding: 10px;
  margin-top: 5px;
  border-radius: 5px;
  border: 1px solid #ccc;
  width: 70%;
  box-sizing: border-box;
}
</style>
