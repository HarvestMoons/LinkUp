<!-- FriendSelection.vue> -->
<template>
  <div class="friendsDropdown">
    <div v-if="friendListLoading" class="loading">加载中...</div>
    <div v-else-if="friends.length === 0" class="loading">无好友</div>
    <div v-else>
      <ul class="friendsList">
        <li
          v-for="friend in friends"
          :key="friend.id"
          class="friendItem"
          @click="toggleSelection(friend)"
          :class="{
            selected: selectedFriends.includes(friend),
            unavailable: isUnavailable(friend),
          }"
        >
          <img
            :src="friend.avatar || require('@/assets/images/icon.png')"
            alt="头像"
            class="friendAvatar"
          />
          <span class="friendNickname"
            >{{ friend.username }} (#{{ friend.id }})</span
          >
          <span v-if="selectedFriends.includes(friend)" class="checkmark"
            >✔</span
          >
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { getFriendList } from "@/utils/friendService";

export default {
  name: "FriendSelection",
  props: {
    userId: Number,
    unavailableFriendIds: Array,
  },
  data() {
    return { friendListLoading: false, friends: [], selectedFriends: [] };
  },
  mounted() {
    this.fetchFriends();
  },
  computed: {
    // 计算属性：判断某个好友是否不可选
    isUnavailable() {
      return (friend) => this.unavailableFriendIds.includes(friend.id);
    },
  },
  methods: {
    async fetchFriends() {
      this.friendListLoading = true;
      this.friends = await getFriendList(this.userId);
      this.friendListLoading = false;
    },
    // 选择或取消选择好友
    toggleSelection(friend) {
      if (this.isUnavailable(friend)) {
        return;
      }
      const index = this.selectedFriends.indexOf(friend);
      if (index === -1) {
        // 如果好友未被选中，添加到选中列表
        this.selectedFriends.push(friend);
      } else {
        // 如果好友已经选中，取消选择
        this.selectedFriends.splice(index, 1);
      }
      this.$emit("update:modelValue", this.selectedFriends);
    },
  },
};
</script>

<style scoped>
.friendsDropdown {
  max-height: 120px;
  overflow-y: auto;
  width: 90%;
}

.friendsList {
  padding: 0;
}

.friendItem {
  list-style: none;
  display: flex;
  align-items: center; /* 使头像和昵称垂直居中 */
  margin-bottom: 15px; /* 每个列表项之间的间距 */
}

.unavailable {
  opacity: 0.5;
  pointer-events: none; /* 禁用点击 */
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
