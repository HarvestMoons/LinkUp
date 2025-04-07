<!-- TaskForm.Vue -->
<template>
  <div class="taskFormContainer">
    <!-- 输入框区域 -->
    <div class="allInputFields">
      <div class="formWithLabelAndInput">
        <label for="taskTitle">{{ $t("task.form.titleLabel") }}:</label>
        <input
          type="text"
          id="taskTitle"
          v-model="localTask.title"
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
        ></textarea>
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskPriority">{{ $t("task.form.priorityLabel") }}:</label>
        <select v-model="localTask.priority" id="taskPriority">
          <option value="LOW">{{ $t("task.priority.low") }}</option>
          <option value="MEDIUM">{{ $t("task.priority.medium") }}</option>
          <option value="HIGH">{{ $t("task.priority.high") }}</option>
        </select>
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskStatus">{{ $t("task.form.statusLabel") }}:</label>
        <select v-model="localTask.status" id="taskStatus">
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
          type="datetime-local"
          v-model="localTask.dueDate"
          id="taskDueDate"
        />
      </div>
    </div>
    <div class="doubleButtonContainer">
      <!-- 取消按钮 -->
      <button @click="$emit('cancel')" class="button warningButton">
        {{ $t("common.cancel") }}
      </button>

      <!-- 提交按钮 -->
      <button @click="$emit('submit', localTask)" class="button normalButton">
        {{ $t("task.form.submitButton") }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "TaskForm",
  props: {
    task: Object, // 传入的任务数据 若为空，则代表创建任务，否则代表修改任务
  },
  data() {
    return {
      localTask: { ...this.task }, // 使用局部副本，避免修改原数据
    };
  },
  watch: {
    task: {
      deep: true,
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