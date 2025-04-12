<!-- FriendSelection.vue> -->
<template>
  <div class="friendsDropdown">
    <div v-if="friendListLoading">
      <MySpinner />
    </div>
    <div v-else-if="friends.length === 0" class="nothing-notice">
      {{ $t("friends.noFriends") }}
    </div>
    <div v-else>
      <ul class="friendsList">
        <li
          v-for="friend in friends"
          :key="friend.id"
          class="friendItem"
          :class="{
            selected: selectedFriends.includes(friend),
            unavailable: isUnavailable(friend),
          }"
          @click="toggleSelection(friend)"
        >
          <img
            :src="$store.getters.getAvatar(friend.avatarId)"
            :alt="$t('common.avatarAlt')"
            class="avatar"
          />
          <div class="nameAndActiveTimeContainer">
            <span class="nickname">
              {{ friend.username }} (#{{ friend.id }})
            </span>
            <div class="lastActiveTime">
              <span
                v-if="calcLastActiveString(friend.lastActiveTime) === '在线'"
                style="
                  display: inline-block;
                  width: 10px;
                  height: 10px;
                  background: green;
                  border-radius: 50%;
                  margin-right: 5px;
                "
              />
              <span>
                {{ calcLastActiveString(friend.lastActiveTime) }}
              </span>
            </div>
          </div>
          <span v-if="selectedFriends.includes(friend)" class="checkmark"
            >✔</span
          >
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { getFriendList, calcLastActiveString } from "@/utils/friendService";
import MySpinner from "@/components/common/MySpinner.vue";

export default {
  name: "FriendSelection",
  components: { MySpinner },
  props: {
    userId: Number,
    unavailableFriendIds: Array,
  },
  data() {
    return { friendListLoading: false, selectedFriends: [] };
  },
  computed: {
    // 计算属性：判断某个好友是否不可选
    isUnavailable() {
      return (friend) => this.unavailableFriendIds.includes(friend.id);
    },
    friends() {
      return this.$store.getters.getFriendList;
    },
  },
  mounted() {
    this.fetchFriends();
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

    calcLastActiveString(lastActiveTime) {
      return calcLastActiveString(lastActiveTime);
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
