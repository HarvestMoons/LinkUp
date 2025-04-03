<!-- TaskList.Vue -->
<template>
  <div class="taskCreateAndShowContainer">
    <div class="blockContainer">
      <transition name="taskFormTransition">
        <!-- 控制是否显示输入框 -->
        <TaskForm
          v-if="isCreating"
          :task="newTask"
          @cancel="cancelCreateTask"
          @submit="submitTask"
        />
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

    <div class="blockContainer">
      <div class="orderSelection">
        <h3 @click="changeOrderToStatus">按任务状态排序</h3>
        <span class="separator">|</span>
        <h3 @click="changeOrderToPriority">按任务优先级排序</h3>
      </div>
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
            <div class="previewBar" @click="toggleSection('high')">
              <h2>HIGH PRIORITY</h2>
            </div>
            <ul class="tasksList" v-if="expandedSections.high">
              <li
                v-for="task in highTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskForm
                  v-if="editingTasks[task.id]"
                  :task="task"
                  @cancel="cancelEditTask(task.id)"
                  @submit="updateTask(task.id, $event)"
                />
                <div v-else>
                  <!-- 任务块 -->
                  <TaskBlock
                    :task="task"
                    :showPriority="false"
                    :showStatus="true"
                  />

                  <!-- 右上角的 "三个点" -->
                  <div class="task-options">
                    <button @click.stop="toggleDropdown(task.id)">⋮</button>
                    <div
                      v-if="activeDropdown === task.id"
                      class="dropdown-menu"
                    >
                      <div @click="editTask(task.id)">✏️ 编辑任务</div>
                      <div
                        v-if="task.taskGroup && !isInGroupPage"
                        @click="enterGroupChat(task.taskGroup.id)"
                      >
                        💬 进入群聊
                      </div>
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </div>

          <div v-if="midTasks.length !== 0" class="blockContainer">
            <div class="previewBar" @click="toggleSection('medium')">
              <h2>MEDIUM PRIORITY</h2>
            </div>
            <ul class="tasksList" v-if="expandedSections.medium">
              <li
                v-for="task in midTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskForm
                  v-if="editingTasks[task.id]"
                  :task="task"
                  @cancel="cancelEditTask(task.id)"
                  @submit="updateTask(task.id, $event)"
                />
                <div v-else>
                  <TaskBlock
                    :task="task"
                    :showPriority="false"
                    :showStatus="true"
                  />

                  <!-- 右上角的 "三个点" -->
                  <div class="task-options">
                    <button @click.stop="toggleDropdown(task.id)">⋮</button>
                    <div
                      v-if="activeDropdown === task.id"
                      class="dropdown-menu"
                    >
                      <div @click="editTask(task.id)">✏️ 编辑任务</div>
                      <div
                        v-if="task.taskGroup && !isInGroupPage"
                        @click="enterGroupChat(task.taskGroup.id)"
                      >
                        💬 进入群聊
                      </div>
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </div>

          <div v-if="lowTasks.length !== 0" class="blockContainer">
            <div class="previewBar" @click="toggleSection('low')">
              <h2>LOW PRIORITY</h2>
            </div>
            <ul class="tasksList" v-if="expandedSections.low">
              <li
                v-for="task in lowTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskForm
                  v-if="editingTasks[task.id]"
                  :task="task"
                  @cancel="cancelEditTask(task.id)"
                  @submit="updateTask(task.id, $event)"
                />
                <div v-else>
                  <TaskBlock
                    :task="task"
                    :showPriority="false"
                    :showStatus="true"
                  />

                  <!-- 右上角的 "三个点" -->
                  <div class="task-options">
                    <button @click.stop="toggleDropdown(task.id)">⋮</button>
                    <div
                      v-if="activeDropdown === task.id"
                      class="dropdown-menu"
                    >
                      <div @click="editTask(task.id)">✏️ 编辑任务</div>
                      <div
                        v-if="task.taskGroup && !isInGroupPage"
                        @click="enterGroupChat(task.taskGroup.id)"
                      >
                        💬 进入群聊
                      </div>
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
        <div v-else-if="taskOrder === TaskOrder.Status">
          <div v-if="todoTasks.length !== 0" class="blockContainer">
            <div class="previewBar" @click="toggleSection('todo')">
              <h2>TODO</h2>
            </div>
            <ul class="tasksList" v-if="expandedSections.todo">
              <li
                v-for="task in todoTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskForm
                  v-if="editingTasks[task.id]"
                  :task="task"
                  @cancel="cancelEditTask(task.id)"
                  @submit="updateTask(task.id, $event)"
                />
                <div v-else>
                  <TaskBlock
                    :task="task"
                    :showPriority="true"
                    :showStatus="false"
                  />

                  <!-- 右上角的 "三个点" -->
                  <div class="task-options">
                    <button @click.stop="toggleDropdown(task.id)">⋮</button>
                    <div
                      v-if="activeDropdown === task.id"
                      class="dropdown-menu"
                    >
                      <div @click="editTask(task.id)">✏️ 编辑任务</div>
                      <div
                        v-if="task.taskGroup && !isInGroupPage"
                        @click="enterGroupChat(task.taskGroup.id)"
                      >
                        💬 进入群聊
                      </div>
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </div>

          <div v-if="inProgressTasks.length !== 0" class="blockContainer">
            <div class="previewBar" @click="toggleSection('inProgress')">
              <h2>IN PROGRESS</h2>
            </div>
            <ul class="tasksList" v-if="expandedSections.inProgress">
              <li
                v-for="task in inProgressTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskForm
                  v-if="editingTasks[task.id]"
                  :task="task"
                  @cancel="cancelEditTask(task.id)"
                  @submit="updateTask(task.id, $event)"
                />
                <div v-else>
                  <TaskBlock
                    :task="task"
                    :showPriority="true"
                    :showStatus="false"
                  />

                  <!-- 右上角的 "三个点" -->
                  <div class="task-options">
                    <button @click.stop="toggleDropdown(task.id)">⋮</button>
                    <div
                      v-if="activeDropdown === task.id"
                      class="dropdown-menu"
                    >
                      <div @click="editTask(task.id)">✏️ 编辑任务</div>
                      <div
                        v-if="task.taskGroup && !isInGroupPage"
                        @click="enterGroupChat(task.taskGroup.id)"
                      >
                        💬 进入群聊
                      </div>
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </div>

          <div v-if="completedTasks.length !== 0" class="blockContainer">
            <div class="previewBar" @click="toggleSection('completed')">
              <h2>COMPLETED</h2>
            </div>
            <ul class="tasksList" v-if="expandedSections.completed">
              <li
                v-for="task in completedTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskForm
                  v-if="editingTasks[task.id]"
                  :task="task"
                  @cancel="cancelEditTask(task.id)"
                  @submit="updateTask(task.id, $event)"
                />
                <div v-else>
                  <TaskBlock
                    :task="task"
                    :showPriority="true"
                    :showStatus="false"
                  />

                  <!-- 右上角的 "三个点" -->
                  <div class="task-options">
                    <button @click.stop="toggleDropdown(task.id)">⋮</button>
                    <div
                      v-if="activeDropdown === task.id"
                      class="dropdown-menu"
                    >
                      <div @click="editTask(task.id)">✏️ 编辑任务</div>
                      <div
                        v-if="task.taskGroup && !isInGroupPage"
                        @click="enterGroupChat(task.taskGroup.id)"
                      >
                        💬 进入群聊
                      </div>
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </div>

          <div v-if="archivedTasks.length !== 0" class="blockContainer">
            <div class="previewBar" @click="toggleSection('archived')">
              <h2>ARCHIVED</h2>
            </div>
            <ul class="tasksList" v-if="expandedSections.archived">
              <li
                v-for="task in archivedTasks"
                :key="task.id"
                class="taskItem blockContainer"
                @mouseover="hoverTask = task.id"
                @mouseleave="hoverTask = null"
              >
                <TaskForm
                  v-if="editingTasks[task.id]"
                  :task="task"
                  @cancel="cancelEditTask(task.id)"
                  @submit="updateTask(task.id, $event)"
                />
                <div v-else>
                  <TaskBlock
                    :task="task"
                    :showPriority="true"
                    :showStatus="false"
                  />

                  <!-- 右上角的 "三个点" -->
                  <div class="task-options">
                    <button @click.stop="toggleDropdown(task.id)">⋮</button>
                    <div
                      v-if="activeDropdown === task.id"
                      class="dropdown-menu"
                    >
                      <div @click="editTask(task.id)">✏️ 编辑任务</div>
                      <div
                        v-if="task.taskGroup && !isInGroupPage"
                        @click="enterGroupChat(task.taskGroup.id)"
                      >
                        💬 进入群聊
                      </div>
                    </div>
                  </div>
                </div>
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
import TaskForm from "@/components/tasks/TaskForm.vue";

export default {
  components: { TaskBlock, TaskForm },
  name: "TaskList",
  props: {
    tasks: Array, // 任务数据
    taskListLoading: Boolean, // 是否在加载
    groupId: Number,
    fetchTasks: {
      type: Function,
      required: true, // 确保调用 TaskList 时必须提供
    },
    isInGroupPage: Boolean,
  },
  data() {
    return {
      showedTasks: [],
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
      expandedSections: {
        high: true,
        medium: true,
        low: true,
        todo: true,
        inProgress: true,
        completed: true,
        archived: true,
      },
      activeDropdown: null,
      editingTasks: {},
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  mounted() {
    this.resetForm();
    this.showedTasks = this.tasks;
  },
  watch: {
    // 监听 tasks 的变化，确保数据更新后再处理
    tasks: {
      handler(tasks) {
        this.$nextTick(() => {
          this.showedTasks = tasks;
        });
      },
      deep: true,
    },
    showedTasks: {
      handler(showedTasks) {
        if (showedTasks.length > 0) {
          showedTasks.sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate));
          this.divideTasksByPriority(showedTasks);
          this.divideTasksByStatus(showedTasks);
        }
      },
      deep: true, // 确保监听数组内部变化
      immediate: true, // 组件初始化时也执行一次
    },
  },
  methods: {
    divideTasksByPriority(tasks) {
      const newHighTasks = [];
      const newMidTasks = [];
      const newLowTasks = [];
      tasks.forEach((task) => {
        if (task.priority === TaskPriority.High) {
          newHighTasks.push(task);
        } else if (task.priority === TaskPriority.Medium) {
          newMidTasks.push(task);
        } else if (task.priority === TaskPriority.Low) {
          newLowTasks.push(task);
        }
      });
      this.highTasks = newHighTasks;
      this.midTasks = newMidTasks;
      this.lowTasks = newLowTasks;
    },
    divideTasksByStatus(tasks) {
      const newTodoTasks = [];
      const newInProgressTasks = [];
      const newCompletedTasks = [];
      const newArchivedTasks = [];
      tasks.forEach((task) => {
        if (task.status === TaskStatus.Todo) {
          newTodoTasks.push(task);
        } else if (task.status === TaskStatus.InProgress) {
          newInProgressTasks.push(task);
        } else if (task.status === TaskStatus.Completed) {
          newCompletedTasks.push(task);
        } else if (task.status === TaskStatus.Archived) {
          newArchivedTasks.push(task);
        }
      });
      this.todoTasks = newTodoTasks;
      this.inProgressTasks = newInProgressTasks;
      this.completedTasks = newCompletedTasks;
      this.archivedTasks = newArchivedTasks;
    },

    changeOrderToPriority() {
      this.taskOrder = TaskOrder.Priority;
    },
    changeOrderToStatus() {
      this.taskOrder = TaskOrder.Status;
    },

    isTaskLegal(task) {
      if (task.title === "") {
        showToast(this.toast, "任务名不能为空", "error");
        return false;
      }
      if (task.dueDate === "") {
        showToast(this.toast, "任务截止日期不能为空", "error");
        return false;
      }
      return true;
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
    async submitTask(newTask) {
      try {
        // TODO: 更多输入限制（如日期范围）
        // 调用后端API创建任务
        if (!this.isTaskLegal(newTask)) {
          return;
        }
        newTask.creator = this.$store.getters.getUser;
        newTask.taskGroupId = this.groupId;
        console.log(newTask);
        await this.$axios.post("/tasks/create", newTask);
        showToast(this.toast, "任务创建成功", "success");

        // 提交成功后重置状态和表单
        this.isCreating = false;
        this.resetForm();
        this.showedTasks.push(newTask);
        //await this.fetchTasks();
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

    toggleSection(section) {
      this.expandedSections[section] = !this.expandedSections[section];
    },

    toggleDropdown(taskId) {
      this.activeDropdown = this.activeDropdown === taskId ? null : taskId;
    },
    cancelEditTask(taskId) {
      this.editingTasks[taskId] = false;
    },
    editTask(taskId) {
      this.editingTasks[taskId] = true;
    },
    async updateTask(taskId, updatedTask) {
      try {
        if (!this.isTaskLegal(updatedTask)) {
          return;
        }
        const taskIndex = this.showedTasks.findIndex(
          (task) => task.id === taskId
        );
        if (taskIndex !== -1) {
          this.showedTasks[taskIndex] = updatedTask;
        }
        await this.$axios.put(`/tasks/update/${taskId}`, updatedTask);
        this.editingTasks[taskId] = false;
        showToast(this.toast, "更新任务成功", "success");
      } catch (error) {
        console.error("更新任务失败", error);
        showToast(this.toast, "更新任务失败", "error");
      }
    },

    enterGroupChat(groupId) {
      console.log("进入群聊", groupId);
      this.$router.push(`/group/${groupId}`);
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

.orderSelection {
  display: flex;
  justify-content: center;
  align-items: center;
}

.orderSelection h3 {
  margin: 0;
  flex: 1; /* 让 h3 平均占据左右空间 */
  text-align: center; /* 让 h3 内部文字居中 */
  cursor: pointer;
}

.separator {
  font-size: 1.2rem; /* 可以调整大小 */
  font-weight: bold;
}

.previewBar {
  cursor: pointer;
}

.tasksList {
  display: flex;
  flex-wrap: wrap; /* 允许换行 */
  justify-content: center; /* 居中显示任务项 */
  width: 100%;
}

.taskItem {
  position: relative;
  width: min(80%, 600px);
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

.task-options {
  position: absolute;
  top: 8px;
  right: 8px;
}

.task-options button {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.dropdown-menu {
  position: absolute;
  top: 24px;
  right: 0;
  background: white;
  border: 1px solid #ddd;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border-radius: 5px;
  width: 120px;
  z-index: 100;
}

.dropdown-menu div {
  padding: 8px 12px;
  cursor: pointer;
}

.dropdown-menu div:hover {
  background: #f5f5f5;
}
</style>
