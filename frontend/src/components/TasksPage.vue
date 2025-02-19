<!-- TasksPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Tasks Page</h1>
    <div class="tasksContainer">
      <ul class="tasksList">
        <li
          v-for="task in tasks"
          :key="task.id"
          class="taskItem"
          @mouseover="hoverTask = task.id"
          @mouseleave="hoverTask = null"
        >
          <!-- 任务块 -->
          <div class="taskContent">
            <div class="taskOverview">
              <span class="taskName">{{ task.name }}</span>
              <span
                class="taskStatus"
                :class="task.status ? 'completed' : 'pending'"
                >{{ task.status ? "Completed" : "Pending" }}</span
              >
              <span class="taskDueDate">{{ task.dueDate }}</span>
            </div>
            <!--<div v-if="hoverTask === task.id" class="taskDetails">-->
            <div class="taskDetails">
              <p class="taskDescription">{{ task.description }}</p>
              <p class="taskPriority">Priority: {{ task.priority }}</p>
              <p class="taskCreateTime">Created: {{ task.createdAt }}</p>
              <p class="taskUpdateTime">Updated: {{ task.updatedAt }}</p>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
//import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
export default {
  name: "TasksPage",
  data() {
    return {
      hoverTask: null, // 记录当前悬浮的任务ID
      tasks: [
        // TODO: 确认status具体内容
        // 示例任务数据
        {
          id: 1,
          name: "Task 1",
          status: true,
          dueDate: "2025-12-31",
          description: "This is a description of Task 1.",
          priority: "High",
          createdAt: "2025-02-01",
          updatedAt: "2025-02-15",
        },
        {
          id: 2,
          name: "Task 2",
          status: false,
          dueDate: "2025-05-10",
          description: "This is a description of Task 2.",
          priority: "Medium",
          createdAt: "2025-03-10",
          updatedAt: "2025-03-12",
        },
        // 更多任务...
      ],
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  mounted() {},
  methods: {},
};
</script>

<style scoped>
.container {
  text-align: center;
  margin-top: 30px;
  overflow-x: hidden;
}

.tasksContainer {
  width: 100%;
  display: flex;
  justify-content: center;
}

.tasksList {
  list-style: none;
  padding: 0;
  margin: 0;
  width: 80%;
}

.taskItem {
  margin: 25px;
  padding: 25px;
  background-color: rgba(128, 128, 128, 0.1);
  border-radius: 10px;
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2);
  overflow-x: hidden;
}

.taskContent {
  height: 150px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px;
  gap: 80px;
  transition: all 0.3s ease;
  transform: translateX(205px);
}

.taskOverview {
  width: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.taskName {
  font-size: 20px;
}

.taskStatus {
  font-weight: bold;
  color: green;
}

.taskStatus.pending {
  color: red;
}

.taskDueDate {
  font-size: 14px;
  color: #999;
}

.taskDetails {
  width: 300px;
  padding: 15px;
  background-color: #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  opacity: 0; /* 默认隐藏 */
  transition: all 0.3s ease; /* 过渡效果 */
}

.taskItem:hover .taskContent {
  transform: translateX(0); /* 向左滑动 */
}

.taskItem:hover .taskDetails {
  transform: translateX(0); /* 任务详情显示出来 */
  opacity: 1; /* 显示详情 */
}

.taskDescription,
.taskPriority,
.taskCreateTime,
.taskUpdateTime {
  margin: 10px 0;
  font-size: 14px;
  color: #333;
}

.taskDescription {
  font-style: italic;
}
</style>
