<template>
  <div class="groupEditorContainer">
    <div class="groupMembers">
      <h2>群成员</h2>
      <ul class="membersList">
        <li v-for="member in groupMembers" :key="member.id" class="memberItem">
          <img :src="member.avatar" alt="头像" class="memberAvatar" />
          <span class="memberName">{{ member.name }}</span>
        </li>
      </ul>
    </div>

    <div class="groupInfo">
      <h2>群组信息</h2>
      <div class="groupField" @click="editGroupName" v-if="isEditingName">
        <input
          v-model="editableGroupName"
          @blur="saveGroupName"
          @keyup.enter="saveGroupName"
        />
      </div>
      <div class="groupField" @click="startEditing('name')" v-else>
        <span>群组名称: </span>
        <span class="editableText">{{ groupName }}</span>
      </div>

      <div
        class="groupField"
        @click="editGroupDescription"
        v-if="isEditingDescription"
      >
        <textarea
          v-model="editableGroupDescription"
          @blur="saveGroupDescription"
          @keyup.enter="saveGroupDescription"
        ></textarea>
      </div>
      <div class="groupField" @click="startEditing('description')" v-else>
        <span>群组描述: </span>
        <span class="editableText">{{ groupDescription }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "GroupEditor",
  props: {
    groupMembers: Array,
    groupName: String,
    groupDescription: String,
    userRole: String, // 'owner', 'admin', 'member'
  },
  data() {
    return {
      isEditingName: false,
      isEditingDescription: false,
      editableGroupName: this.groupName,
      editableGroupDescription: this.groupDescription,
    };
  },
  methods: {
    startEditing(field) {
      if (this.userRole === "owner" || this.userRole === "admin") {
        if (field === "name") {
          this.isEditingName = true;
        } else if (field === "description") {
          this.isEditingDescription = true;
        }
      }
    },
    saveGroupName() {
      this.$emit("updateGroupName", this.editableGroupName);
      this.isEditingName = false;
    },
    saveGroupDescription() {
      this.$emit("updateGroupDescription", this.editableGroupDescription);
      this.isEditingDescription = false;
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
  align-items: center;
  margin: 10px;
  padding: 10px;
  background: white;
  border-radius: 8px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
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

.groupField {
  margin: 15px 0;
  padding: 10px;
  background: white;
  border-radius: 5px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.editableText {
  font-weight: bold;
  color: blue;
}

.groupField input,
.groupField textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
</style>
