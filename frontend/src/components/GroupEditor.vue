<template>
  <div class="groupEditorContainer">
    <div class="groupMembers">
      <h2>群成员</h2>
      <ul class="membersList">
        <div v-if="groupMembers.length === 0" class="loading">加载中...</div>
        <li
          v-else
          v-for="member in groupMembers"
          :key="member.id"
          class="memberItem"
        >
          <img
            :src="member.avatar || require('@/assets/images/icon.png')"
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

        <div class="buttonContainer">
          <!-- 取消按钮 -->
          <button @click="cancelAddingGroupMember" class="cancelButton">
            取消
          </button>

          <!-- 提交按钮 -->
          <button @click="addGroupMember" class="submitButton">邀请</button>
        </div>
      </div>
    </div>

    <div class="groupInfo">
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
        <span class="editableText">{{ groupName }}</span>
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
          groupDescription ? groupDescription : "无"
        }}</span>
      </div>
    </div>

    <button class="disbandButton" @click="disbandGroup" v-if="isGroupOwner()">
      解散群聊
    </button>
    <button class="leaveButton" @click="leaveGroup" v-if="!isGroupOwner()">
      退出群聊
    </button>
  </div>
</template>

<script>
// TODO: 右键踢出群聊或添加好友
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
import FriendSelection from "@/components/FriendSelection.vue";
import { Role } from "@/config/constants";

export default {
  name: "GroupEditor",
  components: { FriendSelection },
  props: {
    groupId: Number,
    groupName: String,
    groupDescription: String,
    groupMembers: Array,
    userRole: Role,
    userId: Number,
  },
  data() {
    return {
      isEditingName: false,
      isEditingDescription: false,
      editableGroupName: this.groupName,
      editableGroupDescription: this.groupDescription,
      isSelectingFriend: false,
      selectedFriends: [],
      shoedGroupMembers: this.groupMembers,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  methods: {
    isGroupOwner() {
      return this.userRolGroupOwnere === Role.Owner;
    },
    startEditing(field) {
      if (this.userRole === Role.Owner || this.userRole === Role.Admin) {
        if (field === "name") {
          this.editableGroupName = this.groupName;
          this.isEditingName = true;
          setTimeout(() => {
            this.$refs.groupNameInput.focus();
          }, 0);
        } else if (field === "description") {
          this.editableGroupDescription = this.groupDescription;
          this.isEditingDescription = true;
          setTimeout(() => {
            this.$refs.groupDescriptionInput.focus();
          }, 0);
        }
      } else {
        showToast(this.toast, "你不是该群组的管理员，无权修改！", "error");
      }
    },
    saveGroupName() {
      // TODO: 调用后端保存
      this.isEditingName = false;
    },
    saveGroupDescription() {
      // TODO: 调用后端保存
      this.isEditingDescription = false;
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
      console.log("shoed members", this.shoedGroupMembers);
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
            .post(
              `/groups/${this.groupId}/members/${friend.id}?role=${Role.Member}`
            )
            .then(() => {
              this.shoedGroupMembers.push({ role: Role.Member, ...friend });
            });

          // 不使用 await，这样不会阻塞循环
          promises.push(promise);
        }
        // 等待所有请求完成
        await Promise.all(promises);
        console.log("shoed members", this.shoedGroupMembers);
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
  },
};
</script>

<style scoped>
.groupEditorContainer {
  text-align: center;
}

.groupMembers,
.groupInfo {
  display: flex;
  flex-direction: column;
  margin: 25px;
  padding: 25px;
  background-color: rgba(128, 128, 128, 0.1);
  border-radius: 10px;
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2);
}

.membersList {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  list-style: none;
  padding: 0;
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
  gap: 5px;
}

.submitButton,
.cancelButton {
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  color: white;
  width: 100px;
}

.submitButton {
  background-color: #007bff;
  margin-left: 10px;
}

.cancelButton {
  background-color: #dc3545;
}

.buttonContainer {
  display: flex;
  align-items: center;
}

.groupField {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  min-height: 35px;
  margin: 15px;
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

.disbandButton,
.leaveButton {
  background: red;
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  color: white;
  width: 100px;
}
</style>
