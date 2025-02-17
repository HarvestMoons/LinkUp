<!-- FriendsPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Friends Page</h1>
    <!-- 好友申请列表 -->
    <div v-if="pendingRequests.length > 0" class="friendRequestContainer">
      <h2>好友申请</h2>

      <div v-if="friendRequestLoading" class="loading">加载中...</div>
      <div v-else>
        <ul class="friendRequestList">
          <li
            v-for="request in pendingRequests"
            :key="request.id"
            class="friendRequestItem"
          >
            <span>{{ request.nickname }} 想加你为好友</span>
            <button @click="acceptRequest(request.id)">接收</button>
            <button @click="rejectRequest(request.id)">拒绝</button>
          </li>
        </ul>
      </div>
    </div>
    <div class="friendListContainer">
      <div v-if="friendListLoading" class="loading">加载中...</div>
      <div v-else-if="friends.length == 0" class="loading">无好友</div>
      <!-- 显示好友列表 -->
      <div v-else>
        <ul class="friendsList">
          <li v-for="friend in friends" :key="friend.id" class="friendItem">
            <img :src="friend.avatar" alt="头像" class="friendAvatar" />
            <span class="friendNickname">{{ friend.nickname }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FriendsPage",
  data() {
    return {
      friends: [], // 用来存储好友列表
      pendingRequests: [], // 存储待处理的好友申请列表
      friendListLoading: true, // 加载状态
      friendRequestLoading: true,
    };
  },
  mounted() {
    // 在组件挂载后获取好友数据
    this.fetchFriends();
    this.fetchPendingRequests();
  },
  methods: {
    async fetchFriends() {
      try {
        const responseUserId = await this.$axios.get(
          `${this.$CONSTANT.PUBLIC_AUTH_API}/info`
        );
        const response = await this.$axios.get(
          `/friendships/find/${responseUserId.data.id}`
        ); // 假设从 store 获取当前用户 ID
        console.log(response.data);
        this.friends = response.data; // 获取好友列表
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

    async acceptRequest(requestId) {
      try {
        // 接受好友申请
        await this.$axios.post(`/friend-requests/accept/${requestId}`);

        // 更新待处理申请和好友列表
        this.fetchPendingRequests();
        this.fetchFriends();

        // 提示用户已成功接收好友请求
        this.$toast.success("已成功接收好友申请");
      } catch (error) {
        console.error("接受好友申请失败:", error);
        this.$toast.error("接受好友申请失败");
      }
    },

    async rejectRequest(requestId) {
      try {
        // 拒绝好友申请
        await this.$axios.post(`/friend-requests/reject/${requestId}`);

        // 更新待处理申请
        this.fetchPendingRequests();

        // 提示用户已成功拒绝好友请求
        this.$toast.success("已成功拒绝好友申请");
      } catch (error) {
        console.error("拒绝好友申请失败:", error);
        this.$toast.error("拒绝好友申请失败");
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

.friendListContainer {
  margin: 50px;
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
