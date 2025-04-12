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
          overdue: isTaskOverdue,
        }"
      >
        {{
          task.priority === TaskPriority.High
            ? $t("task.priority.high") +
              (isTaskOverdue ? ` (${$t("task.overdue")})` : "")
            : task.priority === TaskPriority.Medium
            ? $t("task.priority.medium") +
              (isTaskOverdue ? ` (${$t("task.overdue")})` : "")
            : task.priority === TaskPriority.Low
            ? $t("task.priority.low")
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
          overdue: isTaskOverdue,
        }"
      >
        {{
          task.status === TaskStatus.Todo
            ? $t("task.status.todo") +
              (isTaskOverdue ? ` (${$t("task.overdue")})` : "")
            : task.status === TaskStatus.InProgress
            ? $t("task.status.inProgress") +
              (isTaskOverdue ? ` (${$t("task.overdue")})` : "")
            : task.status === TaskStatus.Completed
            ? $t("task.status.completed")
            : task.status === TaskStatus.Archived
            ? $t("task.status.archived")
            : "Unknown"
        }}
      </span>
      <span class="taskDueDate">{{ formatDate(task.dueDate) }}</span>
      <span class="taskAssignee" v-if="task.assignee">
        {{ $t("task.assignee") }}: {{ task.assignee?.username }} (#{{
          task.assignee?.id
        }})
      </span>
    </div>
    <div class="taskDetails">
      <p class="taskDescription">
        {{ task.description || $t("task.noDescription") }}
      </p>
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
  computed: {
    isTaskOverdue() {
      const now = new Date();
      const dueDate = new Date(this.task.dueDate);
      return (
        dueDate < now &&
        this.task.status !== "COMPLETED" &&
        this.task.status !== "ARCHIVED"
      );
    },
    TaskStatus() {
      return TaskStatus;
    },
    TaskPriority() {
      return TaskPriority;
    },
  },
  methods: {
    formatDate(date) {
      if (date == null) return;
      return date.replace("T", " ").slice(0, 16);
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
  word-break: break-word;
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

.taskAssignee,
.taskDueDate {
  font-size: 14px;
  color: #999;
}

.taskDetails {
  word-break: break-word;
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

@media screen and (max-width: 768px) {
  .taskTitle {
    font-size: 15px;
  }
  .taskDueDate {
    font-size: 10px;
  }
  .taskStatus {
    font-size: 10px;
  }
  .taskContent {
    height: 100px;
  }
}
</style>
