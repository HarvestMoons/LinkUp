<!-- TaskBlock.vue -->
<template>
  <div class="taskContent">
    <div class="taskOverview">
      <span class="taskTitle">{{ task.title }}</span>
      <span
        v-if="showPriority"
        class="taskPriority"
        :class="{
          high: task.priority === TaskPriority.High,
          medium: task.priority === TaskPriority.Medium,
          low: task.priority === TaskPriority.Low,
          overdue: isOverdue(task), // 判断是否逾期
        }"
      >
        {{
          task.priority === TaskPriority.High
            ? "High" + (isOverdue(task) ? " (Overdue)" : "")
            : task.priority === TaskPriority.Medium
            ? "Medium" + (isOverdue(task) ? " (Overdue)" : "")
            : task.priority === TaskPriority.Low
            ? "Low"
            : "Unknown"
        }}
      </span>
      <span
        v-if="showStatus"
        class="taskStatus"
        :class="{
          todo: task.status === TaskStatus.Todo,
          inProgress: task.status === TaskStatus.InProgress,
          completed: task.status === TaskStatus.Completed,
          archived: task.status === TaskStatus.Archived,
          overdue: isOverdue(task), // 判断是否逾期
        }"
      >
        {{
          task.status === TaskStatus.Todo
            ? "To Do" + (isOverdue(task) ? " (Overdue)" : "")
            : task.status === TaskStatus.InProgress
            ? "In Progress" + (isOverdue(task) ? " (Overdue)" : "")
            : task.status === TaskStatus.Completed
            ? "Completed"
            : task.status === TaskStatus.Archived
            ? "Archived"
            : "Unknown"
        }}
      </span>
      <span class="taskDueDate">{{ formatDate(task.dueDate) }}</span>
    </div>
    <!--<div v-if="hoverTask === task.id" class="taskDetails">-->
    <div class="taskDetails">
      <p class="taskDescription">{{ task.description }}</p>
    </div>
  </div>
</template>

<script>
import { TaskStatus, TaskPriority } from "@/config/constants.js";

export default {
  name: "TaskBlock",
  props: {
    task: Object,
    showPriority: Boolean,
    showStatus: Boolean,
  },
  methods: {
    formatDate(date) {
      if (date == null) {
        return;
      }
      return date.replace("T", " ").slice(0, 16);
    },

    isOverdue(task) {
      const now = new Date();
      const dueDate = new Date(task.dueDate);
      // 判断任务是否逾期且未完成或未归档
      return (
        dueDate < now &&
        task.status !== "COMPLETED" &&
        task.status !== "ARCHIVED"
      );
    },
  },
  computed: {
    TaskStatus() {
      return TaskStatus; // 让模板能访问 TaskStatus
    },
    TaskPriority() {
      return TaskPriority; // 让模板能访问 TaskPriority
    },
  },
};
</script>

<style scoped>
.taskContent {
  height: 150px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 5%;
  gap: 10%;
  transition: all 0.3s ease;
  transform: translateX(25%);
}

.taskOverview {
  width: 100%;
  padding: 15px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.taskTitle {
  font-size: 20px;
}

.taskPriority,
.taskStatus {
  font-weight: bold;
  color: black;
}

.taskPriority.high,
.taskStatus.todo {
  color: blue;
}

.taskStatus.inProgress {
  color: orange;
}

.taskPriority.medium,
.taskStatus.completed {
  color: green;
}

.taskPriority.low,
.taskStatus.archived {
  color: gray;
}

.taskPriority.overdue,
.taskStatus.overdue {
  color: red;
}

.taskDueDate {
  font-size: 14px;
  color: #999;
}

.taskDetails {
  width: 100%;
  padding: 15px;
  background-color: #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  opacity: 0; /* 默认隐藏 */
  transition: all 0.3s ease; /* 过渡效果 */
}

.taskContent:hover {
  transform: translateX(0); /* 向左滑动 */
}

.taskContent:hover .taskDetails {
  opacity: 1; /* 显示详情 */
}

.taskDescription {
  margin: 10px 0;
  font-size: 14px;
  color: #333;
  font-style: italic;
}
</style>