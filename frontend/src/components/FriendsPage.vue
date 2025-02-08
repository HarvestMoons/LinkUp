<!-- FriendsPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Friends Page</h1>
    <div class="friendListContainer">
      <div v-if="loading" class="loading">加载中...</div>
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
      friends: [
        {
          id: 1,
          nickname: "测试用昵称1",
          avatar: require("@/assets/icon.png"),
        },
        {
          id: 2,
          nickname: "测试用昵称2",
          avatar: require("@/assets/logo.png"),
        },
      ], // 用来存储好友列表
      loading: true, // 加载状态
    };
  },
  mounted() {
    // 在组件挂载后获取好友数据
    this.fetchFriends();
  },
  methods: {
    async fetchFriends() {
      try {
        //const response = await this.$axios.get("/friends"); // 替换为你的实际接口
        //this.friends = response.data; // 将返回的好友数据存储到 friends 中
      } catch (error) {
        console.error("获取好友数据失败:", error);
      } finally {
        this.loading = false; // 加载完成，更新状态
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
