<!-- TaskList.Vue -->
<template>
  <div class="taskCreateAndShowContainer">
    <div class="blockContainer">
      <transition name="taskFormTransition">
        <!-- 控制是否显示输入框 -->
        <div class="createTaskContainer" v-if="isCreating">
          <!-- 输入框区域 -->
          <div class="allInputFields">
            <div class="formWithLabelAndInput">
              <label for="newTaskTitle">任务标题:</label>
              <input
                type="text"
                id="newTaskTitle"
                v-model="newTask.title"
                placeholder="请输入任务标题"
              />
            </div>
            <div class="formWithLabelAndInput">
              <label for="newTaskDescription">任务描述:</label>
              <textarea
                id="newTaskDescription"
                v-model="newTask.description"
                placeholder="请输入任务描述"
              ></textarea>
            </div>
            <div class="formWithLabelAndInput">
              <label for="newTaskPriority">任务优先级:</label>
              <select v-model="newTask.priority" id="newTaskPriority">
                <option value="LOW">低</option>
                <option value="MEDIUM">中</option>
                <option value="HIGH">高</option>
              </select>
            </div>
            <div class="formWithLabelAndInput">
              <label for="newTaskStatus">任务状态:</label>
              <select v-model="newTask.status" id="newTaskStatus">
                <option value="TODO">待办</option>
                <option value="IN_PROGRESS">进行中</option>
                <option value="COMPLETED">已完成</option>
                <option value="ARCHIVED">已存档</option>
              </select>
            </div>
            <div class="formWithLabelAndInput">
              <label for="newTaskDueDate">截止日期:</label>
              <input
                type="datetime-local"
                v-model="newTask.dueDate"
                id="newTaskDueDate"
              />
            </div>
          </div>
          <div class="doubleButtonContainer">
            <!-- 取消按钮 -->
            <button @click="cancelCreateTask" class="button warningButton">
              取消
            </button>

            <!-- 提交按钮 -->
            <button @click="submitTask" class="button normalButton">
              提交任务
            </button>
          </div>
        </div>
      </transition>

      <!-- 默认的按钮 -->
      <transition name="createButtonTransition">
        <button
          v-if="!isCreating"
          @click="startCreateTask"
          class="button extendButton"
        >
          创建任务
        </button>
      </transition>
    </div>

    <div class="tasksContainer">
      <div v-if="taskListLoading" class="blockContainer loading">加载中...</div>
      <div v-else-if="tasks.length === 0" class="blockContainer loading">
        无任务
      </div>
      <!-- 显示任务列表 -->
      <div v-else>
        <div v-if="taskOrder === TaskOrder.Priority">
          <div v-if="highTasks.length !== 0" class="blockContainer">
            <ul class="tasksList">
              <h2>HIGH PRIORITY</h2>
              <li
                v-for="task in highTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <!-- 任务块 -->
                <TaskBlock
                  :task="task"
                  :showPriority="false"
                  :showStatus="true"
                />
              </li>
            </ul>
          </div>

          <div v-if="midTasks.length !== 0" class="blockContainer">
            <ul class="tasksList">
              <h2>MEDIUM PRIORITY</h2>
              <li
                v-for="task in midTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskBlock
                  :task="task"
                  :showPriority="false"
                  :showStatus="true"
                />
              </li>
            </ul>
          </div>

          <div v-if="lowTasks.length !== 0" class="blockContainer">
            <ul class="tasksList">
              <h2>LOW PRIORITY</h2>
              <li
                v-for="task in lowTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskBlock
                  :task="task"
                  :showPriority="false"
                  :showStatus="true"
                />
              </li>
            </ul>
          </div>
        </div>
        <div v-else-if="taskOrder === TaskOrder.Status">
          <div v-if="todoTasks.length !== 0" class="blockContainer">
            <ul class="tasksList">
              <h2>TODO</h2>
              <li
                v-for="task in todoTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskBlock
                  :task="task"
                  :showPriority="true"
                  :showStatus="false"
                />
              </li>
            </ul>
          </div>

          <div v-if="inProgressTasks.length !== 0" class="blockContainer">
            <ul class="tasksList">
              <h2>IN PROGRESS</h2>
              <li
                v-for="task in inProgressTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskBlock
                  :task="task"
                  :showPriority="true"
                  :showStatus="false"
                />
              </li>
            </ul>
          </div>

          <div v-if="completedTasks.length !== 0" class="blockContainer">
            <ul class="tasksList">
              <h2>COMPLETED</h2>
              <li
                v-for="task in completedTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskBlock
                  :task="task"
                  :showPriority="true"
                  :showStatus="false"
                />
              </li>
            </ul>
          </div>

          <div v-if="archivedTasks.length !== 0" class="blockContainer">
            <ul class="tasksList">
              <h2>ARCHIVED</h2>
              <li
                v-for="task in archivedTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskBlock
                  :task="task"
                  :showPriority="true"
                  :showStatus="false"
                />
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { showToast } from "@/utils/toast";
import { useToast } from "vue-toastification";
import { TaskOrder, TaskPriority, TaskStatus } from "@/config/constants.js";
import TaskBlock from "@/components/tasks/TaskBlock.vue";

export default {
  components: { TaskBlock },
  name: "TaskList",
  props: {
    tasks: Array, // 任务数据
    taskListLoading: Boolean, // 是否在加载
    groupId: Number,
    fetchTasks: {
      type: Function,
      required: true, // 确保调用 TaskList 时必须提供
    },
  },
  data() {
    return {
      hoverTask: null, // 记录当前悬浮的任务ID
      isCreating: false,
      highTasks: [],
      midTasks: [],
      lowTasks: [],
      todoTasks: [],
      inProgressTasks: [],
      completedTasks: [],
      archivedTasks: [],
      newTask: [],
      taskOrder: TaskOrder.Status,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  mounted() {
    this.resetForm();
  },
  watch: {
    // 监听 tasks 的变化，确保数据更新后再处理
    tasks: {
      handler(newTasks) {
        if (newTasks.length > 0) {
          this.divideTasksByPriority();
          this.divideTasksByStatus();
        }
      },
      deep: true, // 确保监听数组内部变化
      immediate: true, // 组件初始化时也执行一次
    },
  },
  methods: {
    // TODO: 用户自定义排序（按优先级、按ddl）
    // TODO: 任务编辑功能
    divideTasksByPriority() {
      this.highTasks = [];
      this.midTasks = [];
      this.lowTasks = [];
      this.tasks.forEach((task) => {
        if (task.status === TaskPriority.High) {
          this.highTasks.push(task);
        } else if (task.priority === TaskPriority.Medium) {
          this.midTasks.push(task);
        } else if (task.priority === TaskPriority.Low) {
          this.lowTasks.push(task);
        }
      });
    },
    divideTasksByStatus() {
      this.todoTasks = [];
      this.inProgressTasks = [];
      this.completedTasks = [];
      this.archivedTasks = [];
      this.tasks.forEach((task) => {
        if (task.status === TaskStatus.Todo) {
          this.todoTasks.push(task);
        } else if (task.status === TaskStatus.InProgress) {
          this.inProgressTasks.push(task);
        } else if (task.status === TaskStatus.Completed) {
          this.completedTasks.push(task);
        } else if (task.status === TaskStatus.Archived) {
          this.archivedTasks.push(task);
        }
      });
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
        // TODO: 更多输入限制（如日期范围）
        // 调用后端API创建任务
        this.newTask.creator = JSON.parse(localStorage.getItem("user"));
        this.newTask.taskGroupId = this.groupId;
        if (this.newTask.title === "") {
          showToast(this.toast, "任务名不能为空", "error");
          return;
        }
        if (this.newTask.dueDate === "") {
          showToast(this.toast, "任务截止日期不能为空", "error");
          return;
        }
        console.log(this.newTask);
        await this.$axios.post("/tasks/create", this.newTask);
        showToast(this.toast, "任务创建成功", "success");

        // 提交成功后重置状态和表单
        this.isCreating = false;
        this.resetForm();
        await this.fetchTasks();
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
  },
  computed: {
    TaskOrder() {
      return TaskOrder; // 让模板能访问 TaskOrder
    },
  },
};
</script>

<style scoped>
.tasksContainer {
  width: 100%;
}

.tasksList {
  display: flex;
  flex-direction: column; /* 让 .taskItem 垂直排列 */
  align-items: center; /* 让 .taskItem 水平居中 */
  width: 100%;
}

.taskItem {
  width: min(80%, 600px);
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

.createButtonTransition-enter-active,
.taskFormTransition-enter-active {
  transition: opacity 0.5s ease, max-height 0.5s ease, padding 0.5s ease;
  transition-delay: 0.25s;
}

.createButtonTransition-leave-active,
.taskFormTransition-leave-active {
  transition: opacity 0.5s ease, max-height 0.5s ease, padding 0.5s ease;
}

.createButtonTransition-enter-from,
.createButtonTransition-leave-to,
.taskFormTransition-enter-from,
.taskFormTransition-leave-to {
  opacity: 0;
  max-height: 0;
  padding: 0;
}

.createButtonTransition-leave-from,
.createButtonTransition-enter-to,
.taskFormTransition-enter-to,
.taskFormTransition-leave-from {
  opacity: 1;
  max-height: 700px;
  padding: 20px;
}
</style>
