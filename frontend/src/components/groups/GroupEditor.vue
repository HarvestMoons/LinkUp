<!-- GroupEditor.vue -->
<template>
  <!-- 自定义右键菜单 -->
  <div
    v-if="isMenuVisible"
    :style="{ top: menuPosition.top + 'px', left: menuPosition.left + 'px' }"
    class="contextMenu"
  >
    <ul>
      <li
        v-if="
          (isUserGroupAdmin() || isUserGroupOwner()) &&
          !isMemberGroupOwner(clickOnMember) &&
          !isUser(clickOnMember)
        "
        @click="deleteMember(clickOnMember)"
      >
        {{ $t("groups.contextMenu.deleteMember") }}
      </li>
      <li
        v-if="
          isUserGroupOwner() &&
          !(
            isMemberGroupAdmin(clickOnMember) ||
            isMemberGroupOwner(clickOnMember)
          )
        "
        @click="setMemberAdmin(clickOnMember)"
      >
        {{ $t("groups.contextMenu.setAdmin") }}
      </li>
      <li
        v-if="isUserGroupOwner() && isMemberGroupAdmin(clickOnMember)"
        @click="setMemberNotAdmin(clickOnMember)"
      >
        {{ $t("groups.contextMenu.removeAdmin") }}
      </li>
      <li
        v-if="
          !isUser(clickOnMember) && friends != null && !isFriend(clickOnMember)
        "
        @click="addMemberAsFriend(clickOnMember)"
      >
        {{ $t("groups.contextMenu.addFriend") }}
      </li>
    </ul>
  </div>

  <!-- 引入自定义确认框组件 -->
  <ConfirmDialog
    v-model:isVisible="isConfirmDialogVisible"
    :message="confirmMessage"
    @confirm="handleConfirm"
  />

  <div>
    <div class="blockContainer">
      <h2>{{ $t("groups.memberListTitle") }}</h2>
      <ul class="membersList">
        <div v-if="showedGroupMembers.length === 0">
          <MySpinner />
        </div>
        <li
          v-else
          v-for="member in showedGroupMembers"
          :key="member.id"
          class="memberItem"
          :class="{
            owner: isMemberGroupOwner(member),
            admin: isMemberGroupAdmin(member),
          }"
          @contextmenu="showMenu($event, member)"
        >
          <img
            :src="this.$store.getters.getAvatar(member.avatarId)"
            :alt="$t('common.avatarAlt')"
            class="memberAvatar"
          />
          <span class="memberNickname"
            >{{ member.username }} (#{{ member.id }})
          </span>
        </li>
        <div
          class="memberItem"
          @click="startAddingGroupMember"
          v-if="!isSelectingFriend"
        >
          +
        </div>
      </ul>
      <div v-if="isSelectingFriend" class="addGroupMemberContainer">
        <FriendSelection
          v-model="selectedFriends"
          :userId="userId"
          :unavailableFriendIds="getGroupMemberIdArray(groupMembers)"
        />

        <div class="doubleButtonContainer">
          <button @click="cancelAddingGroupMember" class="button warningButton">
            {{ $t("common.cancel") }}
          </button>
          <button @click="addGroupMember" class="button normalButton">
            {{ $t("groups.inviteButton") }}
          </button>
        </div>
      </div>
    </div>

    <div class="blockContainer">
      <h2>{{ $t("groups.groupInfoTitle") }}</h2>
      <div class="groupField" v-if="isEditingName">
        <input
          ref="groupNameInput"
          v-model="editableGroupName"
          @blur="saveGroupName"
          @keyup.enter="saveGroupName"
          class=""
        />
      </div>
      <div class="groupField" @click="startEditing('name')" v-else>
        <span>{{ $t("groups.groupNameLabel") }} </span>
        <span class="editableText">{{ showedGroupName }}</span>
      </div>

      <div class="groupField" v-if="isEditingDescription">
        <textarea
          ref="groupDescriptionInput"
          v-model="editableGroupDescription"
          @blur="saveGroupDescription"
          @keyup.enter="saveGroupDescription"
        ></textarea>
      </div>
      <div class="groupField" @click="startEditing('description')" v-else>
        <span>{{ $t("groups.groupDescLabel") }} </span>
        <span class="editableText">{{
          showedGroupDescription
            ? showedGroupDescription
            : $t("groups.noDescription")
        }}</span>
      </div>
    </div>

    <button
      class="button warningButton"
      @click="disbandGroup"
      v-if="isUserGroupOwner()"
    >
      {{ $t("groups.disbandButton") }}
    </button>
    <button
      class="button warningButton"
      @click="leaveGroup"
      v-if="!isUserGroupOwner()"
    >
      {{ $t("groups.leaveButton") }}
    </button>
  </div>
</template>

<script>
import FriendSelection from "@/components/friends/FriendSelection.vue";
import ConfirmDialog from "@/components/common/ConfirmDialog.vue";

import { Role } from "@/config/constants";

import { getFriendList } from "@/utils/friendService";
import { fetchGroups } from "@/utils/groupService";

import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
import { validateInput } from "@/utils/validationUtils";
import MySpinner from "@/components/common/MySpinner.vue";

export default {
  name: "GroupEditor",
  components: {MySpinner, FriendSelection, ConfirmDialog },
  props: {
    groupId: Number,
    groupName: String,
    groupDescription: String,
    groupMembers: Array,
    userRole: Role,
    userId: Number,
    refreshGroupData: Function,
  },
  data() {
    return {
      isMenuVisible: false,
      menuPosition: { top: 0, left: 0 },
      clickOnMember: null,
      isConfirmDialogVisible: false, // 控制确认框的显示和隐藏
      confirmMessage: "", // 确认框显示的提示信息
      isEditingName: false,
      isEditingDescription: false,
      editableGroupName: this.groupName,
      editableGroupDescription: this.groupDescription,
      showedGroupName: this.groupName,
      showedGroupDescription: this.groupDescription,
      isSelectingFriend: false,
      selectedFriends: [],
      showedGroupMembers: [],
      friends: null,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  async mounted() {
    this.showedGroupMembers = this.sortGroupMember(this.groupMembers);
    this.friends = await getFriendList(this.userId);
    window.addEventListener("click", this.closeMenu);
    window.addEventListener("wheel", this.closeMenu);
  },
  methods: {
    isUserGroupOwner() {
      return this.userRole === Role.Owner;
    },
    isUserGroupAdmin() {
      return this.userRole === Role.Admin;
    },
    isMemberGroupOwner(member) {
      return member.role === Role.Owner;
    },
    isMemberGroupAdmin(member) {
      return member.role === Role.Admin;
    },
    isUser(member) {
      return member.id === this.userId;
    },

    showMenu(event, member) {
      event.preventDefault(); // 阻止浏览器默认的右键菜单
      this.isMenuVisible = true; // 显示自定义菜单

      // 计算相对于组件容器的坐标
      const top = event.clientY;
      const left = event.clientX;

      // 设置右键菜单的位置
      this.menuPosition = { top, left };
      this.clickOnMember = member;
    },
    closeMenu() {
      this.isMenuVisible = false;
    },

    handleConfirm() {
      this.disbandGroup(); // 用户确认解散群聊
    },

    async deleteMember(member) {
      try {
        if (this.showedGroupMembers.length === 2) {
          this.confirmMessage = this.$t("groups.confirm.disbandWhenRemove", {
            username: member.username,
            id: member.id,
          });
          this.isConfirmDialogVisible = true;
        } else {
          await this.$axios.delete(
            `/groups/${this.groupId}/members/${member.id}`
          );
          this.closeMenu();
          const index = this.groupMembers.findIndex(
            (tempMember) => tempMember.id === member.id
          );
          this.showedGroupMembers.splice(index, 1);
          showToast(
            this.toast,
            this.$t("groups.memberManagement.removeMemberSuccess", {
              username: member.username,
              id: member.id,
            }),
            "success"
          );
        }
      } catch (error) {
        showToast(
          this.toast,
          this.$t("groups.errors.removeMemberFailed"),
          "error"
        );
      }
    },

    async setMemberAdmin(member) {
      try {
        await this.$axios.put(
          `/groups/${this.groupId}/members/${Number(member.id)}/update-role`,
          { newRole: Role.Admin }
        );
        this.closeMenu();
        member.role = Role.Admin;
        showToast(
          this.toast,
          this.$t("groups.memberManagement.setAdminSuccess", {
            username: member.username,
            id: member.id,
          }),
          "success"
        );
      } catch (error) {
        showToast(this.toast, this.$t("groups.errors.setAdminFailed"), "error");
      }
    },

    async setMemberNotAdmin(member) {
      try {
        await this.$axios.put(
          `/groups/${this.groupId}/members/${member.id}/update-role`,
          { newRole: Role.Member }
        );
        this.closeMenu();
        member.role = Role.Member;
        showToast(
          this.toast,
          this.$t("groups.memberManagement.removeAdminSuccess", {
            username: member.username,
            id: member.id,
          }),
          "success"
        );
      } catch (error) {
        showToast(
          this.toast,
          this.$t("groups.errors.removeAdminFailed"),
          "error"
        );
      }
    },

    async addMemberAsFriend(member) {
      try {
        await this.$axios.post("/friend-requests/send", {
          senderId: Number(this.userId),
          receiverId: Number(member.id),
        });
        showToast(
          this.toast,
          this.$t("friends.success.requestSent"),
          "success"
        );
        this.closeMenu();
      } catch (error) {
        showToast(
          this.toast,
          this.$t("friends.errors.sendRequestFailed"),
          "error"
        );
      }
    },
    isFriend(member) {
      return this.friends.some((friend) => friend.id === member.id);
    },
    startEditing(field) {
      if (this.isUserGroupOwner() || this.isUserGroupAdmin()) {
        if (field === "name") {
          this.editableGroupName = this.showedGroupName;
          this.isEditingName = true;
          setTimeout(() => {
            this.$refs.groupNameInput.focus();
          }, 0);
        } else if (field === "description") {
          this.editableGroupDescription = this.showedGroupDescription;
          this.isEditingDescription = true;
          setTimeout(() => {
            this.$refs.groupDescriptionInput.focus();
          }, 0);
        }
      } else {
        showToast(this.toast, this.$t("groups.permissionDenied.edit"), "error");
      }
    },

    updateHeader() {
      this.refreshGroupData();
    },
    async saveGroupName() {
      this.isEditingName = false;
      if (this.showedGroupName === this.editableGroupName) {
        return;
      }
      this.errorMessage = validateInput(
        this.$constants.GROUP_NAME_VALIDATION,
        this.editableGroupName
      );
      if (this.errorMessage) {
        showToast(this.toast, this.errorMessage, "error");
        return;
      }
      try {
        this.showedGroupName = this.editableGroupName;
        await this.$axios.put(`/task-group/${this.groupId}/update-name`, null, {
          params: { name: this.editableGroupName },
        });
        showToast(
          this.toast,
          this.$t("groups.groupManagement.nameUpdateSuccess"),
          "success"
        );
        this.updateHeader();
      } catch (error) {
        showToast(
          this.toast,
          this.$t("groups.errors.updateNameFailed"),
          "error"
        );
      }
    },
    async saveGroupDescription() {
      this.isEditingDescription = false;
      if (this.showedGroupDescription === this.editableGroupDescription) {
        return;
      }
      if (this.editableGroupDescription == null) {
        this.editableGroupDescription = "";
      }

      try {
        this.showedGroupDescription = this.editableGroupDescription;
        await this.$axios.put(
          `/task-group/${this.groupId}/update-description`,
          null,
          {
            params: { description: this.editableGroupDescription },
          }
        );
        showToast(
          this.toast,
          this.$t("groups.groupManagement.descUpdateSuccess"),
          "success"
        );
        this.updateHeader();
      } catch (error) {
        showToast(
          this.toast,
          this.$t("groups.errors.updateDescFailed"),
          "error"
        );
      }
    },
    async disbandGroup() {
      try {
        await this.$axios.delete(`/task-group/${this.groupId}`);
        showToast(
          this.toast,
          this.$t("groups.groupManagement.disbandSuccess"),
          "success"
        );
        fetchGroups(this.$store.getters.getUserId);
        this.$router.push("/groups");
      } catch (error) {
        showToast(this.toast, this.$t("groups.errors.disbandFailed"), "error");
      }
    },
    async leaveGroup() {
      try {
        if (this.groupMembers.length === 2) {
          await this.$axios.delete(`/task-group/${this.groupId}`);
        } else {
          await this.$axios.delete(
            `/groups/${this.groupId}/members/${this.userId}`
          );
        }
        showToast(
          this.toast,
          this.$t("groups.groupManagement.leaveSuccess"),
          "success"
        );
        fetchGroups(this.$store.getters.getUserId);
        this.$router.push("/groups");
      } catch (error) {
        showToast(this.toast, this.$t("groups.errors.leaveFailed"), "error");
      }
    },

    startAddingGroupMember() {
      this.isSelectingFriend = true;
    },
    cancelAddingGroupMember() {
      this.isSelectingFriend = false;
    },
    async addGroupMember() {
      try {
        if (this.selectedFriends.length === 0) {
          console.log(this.$t("groups.errors.selectAtLeastOne"));
          showToast(
            this.toast,
            this.$t("groups.errors.selectAtLeastOne"),
            "warning"
          );
          return;
        }
        const promises = [];

        // 收集所有的异步请求
        for (const friend of this.selectedFriends) {
          const promise = this.$axios
            .post(`/groups/${this.groupId}/members/${friend.id}`, null, {
              params: { role: Role.Member },
            })
            .then(() => {
              this.showedGroupMembers.push({ role: Role.Member, ...friend });
            });

          // 不使用 await，这样不会阻塞循环
          promises.push(promise);
        }
        // 等待所有请求完成
        await Promise.all(promises);
        this.isSelectingFriend = false;
        showToast(
          this.toast,
          this.$t("groups.memberManagement.inviteSuccess"),
          "success"
        );
      } catch (error) {
        showToast(this.toast, this.$t("groups.errors.inviteFailed"), "error");
      }
    },
    getGroupMemberIdArray(groupMembers) {
      return groupMembers.map((item) => item.id);
    },

    sortGroupMember(groupMembers) {
      const rolePriority = {
        [Role.Owner]: 1,
        [Role.Admin]: 2,
        [Role.Member]: 3,
      };
      let result = groupMembers;
      result.sort((a, b) => rolePriority[a.role] - rolePriority[b.role]);
      return result;
    },
  },
  beforeUnmount() {
    window.removeEventListener("click", this.closeMenu);
    window.removeEventListener("wheel", this.closeMenu);
  },
  watch: {
    groupMembers: {
      handler() {
        this.$nextTick(() => {
          this.showedGroupMembers = this.sortGroupMember(this.groupMembers);
        });
      },
      deep: true, // 深度监听，确保数组更新时触发
    },
    groupName: {
      handler() {
        this.$nextTick(() => {
          this.showedGroupName = this.groupName;
        });
      },
    },
    groupDescription: {
      handler() {
        this.$nextTick(() => {
          this.showedGroupDescription = this.groupDescription;
        });
      },
    },
  },
};
</script>

<style scoped>
.contextMenu {
  position: fixed;
  background-color: white;
  border: 1px solid #ccc;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 1001;
  cursor: pointer;
}

.contextMenu li {
  padding: 10px 15px;
  border-bottom: 1px solid #eee;
}

.contextMenu li:hover {
  background-color: #f0f0f0;
}

.blockContainer {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.blockContainer h2 {
  margin: 0;
}

.membersList {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
}

.memberItem {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 10px;
  padding: 10px;
  background-color: rgba(128, 128, 128, 0.1);
  border-radius: 10px;
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2);
  border: 2px solid rgba(0, 0, 0, 0);
}
.memberItem.owner {
  border: 2px solid #686160;
}
.memberItem.admin {
  border: 2px solid #c0b5b5;
}

.memberAvatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.memberName {
  font-size: 16px;
  font-weight: bold;
}

.addGroupMemberContainer {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 15px;
  gap: 15px;
}

.groupField {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  min-height: 35px;
  padding: 10px;
  background-color: rgba(128, 128, 128, 0.1);
  border-radius: 10px;
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2);
  cursor: pointer;
}

.editableText {
  font-weight: bold;
  color: #5e4239;
}

.groupField input,
.groupField textarea {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 10px;
  width: 100%;
  box-sizing: border-box;
}
</style>
