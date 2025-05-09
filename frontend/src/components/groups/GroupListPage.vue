<!-- GroupListPage.vue -->
<template>
  <div class="container">
    <!-- 添加好友输入框和按钮 -->
    <div class="blockContainer">
      <HelpTooltip>{{ $t('help.createGroup') }}</HelpTooltip>
      <transition name="createGroupContainerTransition">
        <div v-if="isCreating" class="createGroupContainer">
          <div class="allInputFields">
            <div class="formWithLabelAndInput">
              <label for="newGroupName">{{ $t('groups.create.nameLabel') }}:</label>
              <input
                id="newGroupName"
                v-model="newGroup.name"
                type="text"
                :placeholder="$t('groups.create.namePlaceholder')"
                required
              />
            </div>
            <div class="formWithLabelAndInput">
              <label for="newGroupDescription">{{ $t('groups.create.descLabel') }}:</label>
              <textarea
                id="newGroupDescription"
                v-model="newGroup.description"
                :placeholder="$t('groups.create.descPlaceholder')"
              />
            </div>
            <div class="formWithLabelAndInput">
              <label for="selectFriends">{{ $t('groups.create.selectFriendsLabel') }}:</label>
              <UserSelection
                v-model="selectedFriends"
                :users="friends"
                :unavailable-user-ids="[]"
                :loading="friendListLoading"
              />
            </div>
          </div>

          <div class="doubleButtonContainer">
            <button class="button warningButton" @click="cancelCreateGroup">
              {{ $t('common.cancel') }}
            </button>
            <button class="button normalButton" @click="createGroup">
              {{ $t('groups.create.submitButton') }}
            </button>
          </div>
        </div>
      </transition>

      <transition name="createGroupButton">
        <button v-if="!isCreating" class="button extendButton" @click="startCreateGroup">
          {{ $t('groups.createButton') }}
        </button>
      </transition>
    </div>
    <div class="blockContainer">
      <div v-if="groups == null || groupListLoading">
        <MySpinner />
      </div>
      <div v-else-if="groups.length === 0" class="nothing-notice">
        {{ $t('groups.noGroups') }}
      </div>
      <div v-else>
        <ul class="groupsList">
          <li
            v-for="group in groups"
            :key="group.id"
            class="groupItem"
            @click="goToGroup(group.id)"
          >
            <div class="groupAvatar">
              <img
                v-for="(avatar, index) in group.avatars"
                :key="index"
                :src="avatar"
                class="avatarInGroupAvatar"
                :class="`avatar-${group.avatars.length}`"
                :alt="$t('groups.groupAvatarAlt')"
              />
            </div>
            <span class="nickname">{{ group.name }} (#{{ group.id }})</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import UserSelection from '@/components/common/UserSelection.vue'
import HelpTooltip from '@/components/common/HelpTooltip.vue'

import { showToast } from '@/utils/toast'
import { useToast } from 'vue-toastification'
import { Role } from '@/config/constants'
import { validateInput } from '@/utils/validationUtils'
import { getFriendList } from '@/utils/friendService'
import { fetchGroups, getGroupList } from '@/utils/groupService'
import MySpinner from '@/components/common/MySpinner.vue'

export default {
  name: 'GroupListPage',
  components: { MySpinner, UserSelection, HelpTooltip },
  setup() {
    const toast = useToast()
    return { toast }
  },
  data() {
    return {
      groupListLoading: false, // 加载状态
      friendListLoading: false,
      isCreating: false,
      newGroup: [],
      selectedFriends: [],
      userId: null,
    }
  },
  computed: {
    groups() {
      return this.$store.getters.getGroupList || this.groups
    },
    friends() {
      return this.$store.getters.getFriendList
    },
  },
  async mounted() {
    this.userId = this.$store.getters.getUserId // 读取 userId
    if (!this.userId) {
      return
    }
    // 在组件挂载后获取群组数据
    this.groupListLoading = true
    this.groups = await getGroupList(this.userId)
    this.groupListLoading = false // 加载完成，更新状态
    this.friendListLoading = true
    this.friends = await getFriendList(this.userId)
    this.friendListLoading = false
  },
  methods: {
    async createGroup() {
      const errorMessage = validateInput(this.$constants.GROUP_NAME_VALIDATION, this.newGroup.name)
      if (errorMessage) {
        showToast(this.toast, errorMessage, 'error')
        return
      }
      try {
        // TODO: 创建群组和拉人分两步会不会不好回滚(目前可以考虑使用定时清理数据库的方法解决)
        if (this.selectedFriends.length === 0) {
          showToast(this.toast, this.$t('groups.errors.selectAtLeastOne'), 'warning')
          return
        }
        const responseNewGroup = await this.$axios.post(`/task-group/create`, {
          name: this.newGroup.name,
          description: this.newGroup.description,
        })
        await this.$axios.post(`/groups/${responseNewGroup.data.id}/members/${this.userId}`, null, {
          params: { role: Role.Owner },
        })
        for (const friend of this.selectedFriends) {
          await this.$axios.post(`/groups/${responseNewGroup.data.id}/members/${friend.id}`, null, {
            params: { role: Role.Member },
          })
        }
        this.groups = await fetchGroups(this.userId)
        this.cancelCreateGroup()
        showToast(this.toast, this.$t('groups.groupManagement.createSuccess'), 'success')
      } catch (error) {
        showToast(this.toast, this.$t('groups.errors.createGroupFailed'), 'error')
      }
    },

    goToGroup(id) {
      this.$router.push(`/group/${id}`)
    },

    // 切换到创建任务模式
    async startCreateGroup() {
      this.isCreating = true
    },

    // 取消创建任务
    cancelCreateGroup() {
      this.isCreating = false
      this.resetForm() // 重置表单
    },

    resetForm() {
      this.newGroup = {
        name: '',
        description: '',
      }
    },
  },
}
</script>

<style scoped>
.groupsList {
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.groupItem {
  display: flex;
  align-items: center; /* 使头像和昵称垂直居中 */
  transition: 0.2s ease;
  transform-origin: left;
}

.groupItem:hover {
  transform: scale(1.1);
}

.createGroupContainer {
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
  justify-content: center;
  align-items: center;
}

.groupAvatar {
  position: relative;
  width: clamp(3rem, 4vw, 5rem);
  height: clamp(3rem, 4vw, 5rem);
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  margin-right: 1.25%;
}

.avatarInGroupAvatar {
  position: absolute;
  width: 50%;
  height: 50%;
  border-radius: 50%;
  border: 2px solid white;
  object-fit: cover;
}

.avatar-1 {
  width: 100%;
  height: 100%;
}

.avatar-2:nth-child(1) {
  top: 25%;
  left: 0;
}
.avatar-2:nth-child(2) {
  top: 25%;
  right: 0;
}

.avatar-3:nth-child(1) {
  top: 0;
  left: 50%;
  transform: translateX(-50%);
}
.avatar-3:nth-child(2) {
  bottom: 0;
  left: 0;
}
.avatar-3:nth-child(3) {
  bottom: 0;
  right: 0;
}

.avatar-4:nth-child(1) {
  top: 0;
  left: 0;
}
.avatar-4:nth-child(2) {
  top: 0;
  right: 0;
}
.avatar-4:nth-child(3) {
  bottom: 0;
  left: 0;
}
.avatar-4:nth-child(4) {
  bottom: 0;
  right: 0;
}
</style>
