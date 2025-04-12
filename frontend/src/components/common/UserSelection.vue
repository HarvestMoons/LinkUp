<!-- UserSelection.vue -->
<template>
  <div class="userDropdown">
    <div v-if="loading">
      <MySpinner />
    </div>
    <div v-else-if="users.length === 0" class="nothing-notice">
      {{ $t("users.noUsers") }}
    </div>
    <div v-else>
      <ul class="userList">
        <li
          v-for="user in users"
          :key="user.id"
          class="userItem"
          :class="{
            selected: selectedUsers.includes(user),
            unavailable: isUnavailable(user),
          }"
          @click="toggleSelection(user)"
        >
          <img
            :src="$store.getters.getAvatar(user.avatarId)"
            :alt="$t('common.avatarAlt')"
            class="avatar"
          />
          <div class="nameAndActiveTimeContainer">
            <span class="nickname"> {{ user.username }} (#{{ user.id }}) </span>
            <div class="lastActiveTime">
              <span
                v-if="calcLastActiveString(user.lastActiveTime) === '在线'"
                style="
                  display: inline-block;
                  width: 10px;
                  height: 10px;
                  background: green;
                  border-radius: 50%;
                  margin-right: 5px;
                "
              />
              <span>{{ calcLastActiveString(user.lastActiveTime) }}</span>
            </div>
          </div>
          <span v-if="selectedUsers.includes(user)" class="checkmark">✔</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { calcLastActiveString } from "@/utils/friendService";
import MySpinner from "@/components/common/MySpinner.vue";

export default {
  name: "UserSelection",
  components: { MySpinner },
  props: {
    users: {
      type: Array,
      required: true,
    },
    unavailableUserIds: {
      type: Array,
      default: () => [],
    },
    loading: {
      type: Boolean,
      default: false,
    },
    modelValue: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    selectedUsers: {
      get() {
        return this.modelValue;
      },
      set(val) {
        this.$emit("update:modelValue", val);
      },
    },
    isUnavailable() {
      return (user) => this.unavailableUserIds.includes(user.id);
    },
  },
  methods: {
    toggleSelection(user) {
      if (this.isUnavailable(user)) return;

      const index = this.selectedUsers.findIndex((u) => u.id === user.id);
      if (index === -1) {
        this.selectedUsers = [...this.selectedUsers, user];
      } else {
        this.selectedUsers = this.selectedUsers.filter((u) => u.id !== user.id);
      }
    },
    calcLastActiveString(lastActiveTime) {
      return calcLastActiveString(lastActiveTime);
    },
  },
};
</script>

<style scoped>
.userDropdown {
  max-height: 120px;
  overflow-y: auto;
  padding-left: 5%;
  padding-right: 5%;
  width: 90%;
}
.unavailable {
  opacity: 0.5;
  pointer-events: none;
}
</style>
