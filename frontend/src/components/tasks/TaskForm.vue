<!-- TaskForm.Vue -->
<template>
  <div class="taskFormContainer">
    <!-- 输入框区域 -->
    <div class="allInputFields">
      <div class="formWithLabelAndInput">
        <label for="taskTitle">任务标题:</label>
        <input
          type="text"
          id="taskTitle"
          v-model="localTask.title"
          placeholder="请输入任务标题"
        />
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskDescription">任务描述:</label>
        <textarea
          id="taskDescription"
          v-model="localTask.description"
          placeholder="请输入任务描述"
        ></textarea>
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskPriority">任务优先级:</label>
        <select v-model="localTask.priority" id="taskPriority">
          <option value="LOW">低</option>
          <option value="MEDIUM">中</option>
          <option value="HIGH">高</option>
        </select>
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskStatus">任务状态:</label>
        <select v-model="localTask.status" id="taskStatus">
          <option value="TODO">待办</option>
          <option value="IN_PROGRESS">进行中</option>
          <option value="COMPLETED">已完成</option>
          <option value="ARCHIVED">已存档</option>
        </select>
      </div>
      <div class="formWithLabelAndInput">
        <label for="taskDueDate">截止日期:</label>
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
        取消
      </button>

      <!-- 提交按钮 -->
      <button @click="$emit('submit', localTask)" class="button normalButton">
        提交任务
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
      handler(localTask) {
        this.localTask = { ...localTask };
      },
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