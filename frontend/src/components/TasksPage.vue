<!-- TasksPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Tasks Page</h1>

    <transition name="taskCreator" class="taskCreator">
      <div class="taskCreator">
        <!-- 控制是否显示输入框 -->
        <div
          class="taskForm"
          v-if="isCreating"
          @transitionend="onTransitionEnd"
        >
          <!-- 输入框区域 -->
          <div class="newTaskInputFields">
            <div class="form-group">
              <label for="newTaskTitle">任务标题:</label>
              <input
                type="text"
                id="newTaskTitle"
                v-model="newTask.title"
                placeholder="请输入任务标题"
              />
            </div>
            <div class="form-group">
              <label for="newTaskDescription">任务描述:</label>
              <textarea
                id="newTaskDescription"
                v-model="newTask.description"
                placeholder="请输入任务描述"
              ></textarea>
            </div>
            <div class="form-group">
              <label for="newTaskPriority">任务优先级:</label>
              <select v-model="newTask.priority" id="newTaskPriority">
                <option value="LOW">低</option>
                <option value="MEDIUM">中</option>
                <option value="HIGH">高</option>
              </select>
            </div>
            <div class="form-group">
              <label for="newTaskStatus">任务状态:</label>
              <select v-model="newTask.status" id="newTaskStatus">
                <option value="TODO">待办</option>
                <option value="IN_PROGRESS">进行中</option>
                <option value="COMPLETED">已完成</option>
                <option value="ARCHIVED">已存档</option>
              </select>
            </div>
            <div class="form-group">
              <label for="newTaskDueDate">截止日期:</label>
              <input
                type="datetime-local"
                v-model="newTask.dueDate"
                id="newTaskDueDate"
              />
            </div>
          </div>
          <div class="buttonContainer">
            <!-- 取消按钮 -->
            <button @click="cancelCreateTask" class="cancelButton">取消</button>

            <!-- 提交按钮 -->
            <button @click="submitTask" class="submitButton">提交任务</button>
          </div>
        </div>

        <!-- 默认的按钮 -->
        <button v-else @click="startCreateTask" class="createButton">
          创建任务
        </button>
      </div>
    </transition>

    <div class="tasksContainer">
      <div v-if="taskListLoading" class="loading">加载中...</div>
      <div v-else-if="tasks.length === 0" class="loading">无任务</div>
      <!-- 显示任务列表 -->
      <div v-else>
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
                <span class="taskTitle">{{ task.title }}</span>
                <span
                  class="taskStatus"
                  :class="{
                    todo: task.status === 'TODO',
                    inProgress: task.status === 'IN_PROGRESS',
                    completed: task.status === 'COMPLETED',
                    archived: task.status === 'ARCHIVED',
                    overdue: isOverdue(task), // 判断是否逾期
                  }"
                >
                  {{
                    task.status === "TODO"
                      ? "To Do" + (isOverdue(task) ? " (Overdue)" : "")
                      : task.status === "IN_PROGRESS"
                      ? "In Progress" + (isOverdue(task) ? " (Overdue)" : "")
                      : task.status === "COMPLETED"
                      ? "Completed"
                      : task.status === "ARCHIVED"
                      ? "Archived"
                      : "Unknown"
                  }}
                </span>
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
  </div>
</template>

<script>
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
export default {
  name: "TasksPage",
  data() {
    return {
      taskListLoading: true,
      hoverTask: null, // 记录当前悬浮的任务ID
      tasks: [],
      isCreating: false,
      newTask: [],
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  mounted() {
    this.resetForm();
    this.fetchTasks();
  },
  methods: {
    async fetchTasks() {
      try {
        this.tasks = [];
        const response = await this.$axios.get(`/tasks/all`);
        this.tasks = response.data;
      } catch (error) {
        console.error("获取任务数据失败:", error);
      } finally {
        this.taskListLoading = false; // 加载完成，更新状态
      }
    },

    // 切换到创建任务模式
    startCreateTask() {
      this.isCreating = true;
    },

    // 取消创建任务
    cancelCreateTask() {
      this.isCreating = false;
      this.resetForm(); // 重置表单
    },

    // 提交任务到后端
    async submitTask() {
      try {
        // TODO: 处理任务标题为空、日期为空等情况 以及更多输入限制（如日期范围）
        // TODO: 日期的自动补全
        // 调用后端API创建任务
        // const newTaskDueDate = new Date(this.newTask.dueDate);
        //this.newTask.dueDate = newTaskDueDate.toISOString();
        console.log(this.newTask);
        await this.$axios.post("/tasks/create", this.newTask);
        showToast(this.toast, "任务创建成功", "success");

        // 提交成功后重置状态和表单
        this.isCreating = false;
        this.resetForm();
        this.fetchTasks();
      } catch (error) {
        console.error("创建任务失败:", error);
        showToast(this.toast, "创建任务失败", "error");
      }
    },

    // 重置任务表单
    resetForm() {
      this.newTask = {
        title: "",
        description: "",
        priority: "MEDIUM",
        status: "TODO",
        dueDate: "",
      };
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

    // 过渡动画开始前
    beforeEnter(el) {
      el.style.height = "0";
      el.style.opacity = "0";
    },

    // 过渡动画进行时
    enter(el, done) {
      el.offsetHeight; // trigger reflow
      el.style.transition = "height 0.5s ease, opacity 0.5s ease";
      el.style.height = "auto"; // Set height to auto to expand
      el.style.opacity = "1";
      done();
    },

    // 过渡动画离开时
    leave(el, done) {
      el.style.transition = "height 0.5s ease, opacity 0.5s ease";
      el.style.height = "0";
      el.style.opacity = "0";
      done();
    },
  },
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
}

.loading,
.taskItem {
  width: 600px;
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

.taskTitle {
  font-size: 20px;
}

.taskStatus {
  font-weight: bold;
  color: black;
}

.taskStatus.todo {
  color: blue;
}

.taskStatus.inProgress {
  color: orange;
}

.taskStatus.completed {
  color: green;
}

.taskStatus.archived {
  color: gray;
}

.taskStatus.overdue {
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

.taskCreator {
  margin: 25px;
  padding: 25px;
  background-color: rgba(128, 128, 128, 0.1); /* 灰色，10%透明度 */
  border-radius: 10px; /* 圆角 */
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2); /* 阴影效果 */
}

.taskForm {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  overflow: hidden;
  justify-content: center;
  align-items: center;
}

.newTaskInputFields {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 70%;
}

.newTaskInputFields input,
.newTaskInputFields textarea,
.newTaskInputFields select {
  padding: 10px;
  margin-top: 5px;
  border-radius: 5px;
  border: 1px solid #ccc;
  width: 100%;
  box-sizing: border-box;
}

.submitButton,
.cancelButton,
.createButton {
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;

  margin-left: 10px;
  color: white;
  width: 100px;
}

.submitButton {
  background-color: #007bff;
}

.cancelButton {
  background-color: #dc3545;
}

.createButton {
  background-color: #28a745;
}

.buttonContainer {
  display: flex;
  align-items: center;
}

.taskCreator-enter-active,
.taskCreator-leave-active {
  transition: all 0.5s ease-in-out;
}

.taskCreator-enter,
.taskCreator-leave-to {
  height: 0;
  opacity: 0;
  visibility: hidden;
}
</style>
