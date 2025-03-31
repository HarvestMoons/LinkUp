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
        删除该成员
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
        设置该成员为管理员
      </li>
      <li
        v-if="isUserGroupOwner() && isMemberGroupAdmin(clickOnMember)"
        @click="setMemberNotAdmin(clickOnMember)"
      >
        设置该成员为非管理员
      </li>
      <li
        v-if="
          !isUser(clickOnMember) && friends != null && !isFriend(clickOnMember)
        "
        @click="addMemberAsFriend(clickOnMember)"
      >
        添加好友
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
      <h2>群成员</h2>
      <ul class="membersList">
        <div v-if="showedGroupMembers.length === 0" class="loading">
          加载中...
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
            alt="头像"
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
          <!-- 取消按钮 -->
          <button @click="cancelAddingGroupMember" class="button warningButton">
            取消
          </button>

          <!-- 提交按钮 -->
          <button @click="addGroupMember" class="button normalButton">
            邀请
          </button>
        </div>
      </div>
    </div>

    <div class="blockContainer">
      <h2>群组信息</h2>
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
        <span>群组名称: </span>
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
        <span>群组描述: </span>
        <span class="editableText">{{
          showedGroupDescription ? showedGroupDescription : "无"
        }}</span>
      </div>
    </div>

    <button
      class="button warningButton"
      @click="disbandGroup"
      v-if="isUserGroupOwner()"
    >
      解散群聊
    </button>
    <button
      class="button warningButton"
      @click="leaveGroup"
      v-if="!isUserGroupOwner()"
    >
      退出群聊
    </button>
  </div>
</template>

<script>
import FriendSelection from "@/components/friends/FriendSelection.vue";
import ConfirmDialog from "@/components/common/ConfirmDialog.vue";

import { Role } from "@/config/constants";

import { getFriendList } from "@/utils/friendService";

import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";

export default {
  name: "GroupEditor",
  components: { FriendSelection, ConfirmDialog },
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
      console.log("delete", member);
      try {
        if (this.showedGroupMembers.length === 2) {
          this.confirmMessage = `当前群聊只有两人，踢出该成员会直接解散群聊，是否确认踢出用户 ${member.username}(#${member.id})?`;
          this.isConfirmDialogVisible = true; // 显示确认框
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
            `已将用户 ${member.username}(#${member.id}) 踢出群组`,
            "success"
          );
        }
      } catch (error) {
        console.error("删除群成员失败", error);
        showToast(this.toast, "删除群成员失败", "error");
      }
    },
    async setMemberAdmin(member) {
      console.log("set admin", member);
      try {
        await this.$axios.put(
          `/groups/${this.groupId}/members/${Number(member.id)}/update-role`,
          { newRole: Role.Admin }
        );
        this.closeMenu();
        member.role = Role.Admin;
        showToast(
          this.toast,
          `已设置用户 ${member.username}(#${member.id}) 为管理员`,
          "success"
        );
      } catch (error) {
        console.error("设置管理员失败", error);
        showToast(this.toast, "设置管理员失败", "error");
      }
    },
    async setMemberNotAdmin(member) {
      console.log("set not admin", this.groupId, member.id);
      try {
        await this.$axios.put(
          `/groups/${this.groupId}/members/${member.id}/update-role`,
          { newRole: Role.Member }
        );
        this.closeMenu();
        member.role = Role.Member;
        showToast(
          this.toast,
          `已设置用户 ${member.username}(#${member.id}) 为非管理员`,
          "success"
        );
      } catch (error) {
        console.error("设置非管理员失败", error);
        showToast(this.toast, "设置非管理员失败", "error");
      }
    },
    async addMemberAsFriend(member) {
      console.log("add friend with", member);
      try {
        await this.$axios.post(
          "/friend-requests/send", // 后端的接口
          {
            senderId: Number(this.userId), // 将字符串转换为数字
            receiverId: Number(member.id), // 将字符串转换为数字
          }
        );

        // 处理成功的响应
        showToast(this.toast, "好友请求已发送！", "success");
        this.closeMenu();
      } catch (error) {
        console.error("添加好友失败", error);
        showToast(this.toast, "添加好友失败", "error");
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
        showToast(this.toast, "你不是该群组的管理员，无权修改！", "error");
      }
    },

    updateHeader() {
      try {
        this.refreshGroupData();
      } catch (error) {
        console.error("更新群组抬头信息失败", error);
      }
    },
    async saveGroupName() {
      this.isEditingName = false;
      try {
        if (this.editableGroupName === "") {
          this.editableGroupName = this.showedGroupName;
          showToast(this.toast, "群组名称不能为空", "error");
        }
        this.showedGroupName = this.editableGroupName;
        await this.$axios.put(`/task-group/${this.groupId}/update-name`, null, {
          params: { name: this.editableGroupName },
        });
        showToast(this.toast, `群组名称更改成功`, "success");
        this.updateHeader();
      } catch (error) {
        console.error("更改群组名称失败", error);
        showToast(this.toast, "更改群组名称失败", "error");
      }
    },
    async saveGroupDescription() {
      this.isEditingDescription = false;
      try {
        this.showedGroupDescription = this.editableGroupDescription;
        await this.$axios.put(
          `/task-group/${this.groupId}/update-description`,
          null,
          {
            params: { description: this.editableGroupDescription },
          }
        );
        showToast(this.toast, `群组描述更改成功`, "success");
        this.updateHeader();
      } catch (error) {
        console.error("更改群组描述失败", error);
        showToast(this.toast, "更改群组描述失败", "error");
      }
    },

    async disbandGroup() {
      try {
        await this.$axios.delete(`/task-group/${this.groupId}`);
        showToast(this.toast, "群组解散成功", "success");
        this.$router.push("/groups");
      } catch (error) {
        console.error("群组解散失败", error);
        showToast(this.toast, "群组解散失败，请稍后重试", "error");
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
        showToast(this.toast, "群组退出成功", "success");
        this.$router.push("/groups");
      } catch (error) {
        console.error("退出群组失败", error);
        showToast(this.toast, "退出群组失败，请稍后重试", "error");
      }
    },

    startAddingGroupMember() {
      this.isSelectingFriend = true;
      console.log("shoed members", this.groupMembers, this.showedGroupMembers);
    },
    cancelAddingGroupMember() {
      this.isSelectingFriend = false;
    },
    async addGroupMember() {
      try {
        if (this.selectedFriends.length === 0) {
          showToast(this.toast, "至少需要选择一个好友", "error");
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
        console.log("shoed members", this.showedGroupMembers);
        this.isSelectingFriend = false;
        showToast(this.toast, "好友加入群组成功", "success");
      } catch (error) {
        console.error("邀请好友加入群组失败", error);
        showToast(this.toast, "邀请好友加入群组失败，请重试", "error");
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
  border: 2px solid rgba(0, 100, 0, 0.5);
}
.memberItem.admin {
  border: 2px solid rgba(0, 0, 100, 0.5);
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
  color: blue;
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
