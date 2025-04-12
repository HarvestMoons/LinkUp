<!-- TaskForm.Vue -->
<template>
  <div class="taskFormContainer">
    <!-- 输入框区域 -->
    <div class="allInputFields">
      <div class="formWithLabelAndInput">
        <label for="taskTitle">{{ $t("task.form.titleLabel") }}:</label>
        <input
          id="taskTitle"
          v-model="localTask.title"
          type="text"
          :placeholder="$t('task.form.titlePlaceholder')"
        />
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskDescription"
          >{{ $t("task.form.descriptionLabel") }}:</label
        >
        <textarea
          id="taskDescription"
          v-model="localTask.description"
          :placeholder="$t('task.form.descriptionPlaceholder')"
        />
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskPriority">{{ $t("task.form.priorityLabel") }}:</label>
        <select id="taskPriority" v-model="localTask.priority">
          <option value="LOW">{{ $t("task.priority.low") }}</option>
          <option value="MEDIUM">{{ $t("task.priority.medium") }}</option>
          <option value="HIGH">{{ $t("task.priority.high") }}</option>
        </select>
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskStatus">{{ $t("task.form.statusLabel") }}:</label>
        <select id="taskStatus" v-model="localTask.status">
          <option value="TODO">{{ $t("task.status.todo") }}</option>
          <option value="IN_PROGRESS">
            {{ $t("task.status.inProgress") }}
          </option>
          <option value="COMPLETED">{{ $t("task.status.completed") }}</option>
          <option value="ARCHIVED">{{ $t("task.status.archived") }}</option>
        </select>
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskDueDate">{{ $t("task.form.dueDateLabel") }}:</label>
        <input
          id="taskDueDate"
          v-model="localTask.dueDate"
          type="datetime-local"
        />
      </div>
      <div class="formWithLabelAndInput" v-if="taskIsInGroup">
        <label for="selectUsers">{{ $t("task.form.selectUserLabel") }}:</label>
        <UserSelection
          :users="groupMembers"
          :unavailable-user-ids="[]"
          v-model="selectedUser"
          :loading="memberListLoading"
          :multiple="false"
        />
      </div>
    </div>
    <div class="doubleButtonContainer">
      <!-- 取消按钮 -->
      <button class="button warningButton" @click="$emit('cancel')">
        {{ $t("common.cancel") }}
      </button>

      <!-- 提交按钮 -->
      <button class="button normalButton" @click="handleSubmit">
        {{ $t("task.form.submitButton") }}
      </button>
    </div>
  </div>
</template>

<script>
import { fetchMembers } from "@/utils/groupService";
import UserSelection from "@/components/common/UserSelection.vue";

export default {
  name: "TaskForm",
  components: { UserSelection },
  props: {
    task: Object, // 传入的任务数据 若为空，则代表创建任务，否则代表修改任务
    taskIsInGroup: Boolean,
    groupId: Number,
  },
  data() {
    return {
      localTask: { ...this.task }, // 使用局部副本，避免修改原数据
      selectedUser: [this.task.assignee],
      memberListLoading: false,
      groupMembers: [],
    };
  },
  async mounted() {
    console.log(this.selectedUser, this.task);
    this.memberListLoading = true;
    if (this.taskIsInGroup) {
      this.groupMembers = [...(await fetchMembers(this.groupId))];
    }
    this.memberListLoading = false;
  },
  methods: {
    handleSubmit() {
      // 如果有选中用户（selectedUser 是一个数组），取出第一个作为 assignee
      if (this.selectedUser.length === 1) {
        this.localTask.assignee = this.selectedUser[0];
      } else {
        this.localTask.assignee = null;
      }

      // 触发事件传出 localTask
      this.$emit("submit", this.localTask);
    },
  },
};
</script>

<style scoped>
.taskFormContainer {
  display: flex;
  flex-direction: column;
  gap: 20px;
  justify-content: center;
  align-items: center;
}

.createTaskContainer {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  overflow: hidden;
  justify-content: center;
  align-items: center;
}
</style>
