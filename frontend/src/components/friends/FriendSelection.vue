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
            :src="this.$store.getters.getAvatar(friend.avatarId)"
            alt="头像"
            class="avatar"
          />
          <span class="nickname">{{ friend.username }} (#{{ friend.id }})</span>
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
  padding-left: 5%;
  padding-right: 5%;
  width: 90%;
}

.unavailable {
  opacity: 0.5;
  pointer-events: none; /* 禁用点击 */
}
</style>
