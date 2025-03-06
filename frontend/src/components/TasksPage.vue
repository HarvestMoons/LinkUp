<!-- TasksPage.vue -->
<template>
  <div class="container">
    <h1>Welcome to the Tasks Page</h1>

    <TaskList
        :tasks="tasks"
        :taskListLoading="taskListLoading"
        :groupId="null"
        :fetchTasks="fetchTasks"
    />
  </div>
</template>

<script>
import TaskList from "@/components/TaskList.vue";
import {useToast} from "vue-toastification";

export default {
  name: "TasksPage",
  components: {TaskList},
  data() {
    return {
      taskListLoading: false,
      tasks: [],
    };
  },
  setup() {
    const toast = useToast();
    return {toast};
  },
  mounted() {
    this.userId = localStorage.getItem("userId"); // 读取 userId
    if (!this.userId) {
      console.error("用户ID不存在，请重新登录");
      return;
    }
    this.fetchTasks();
  },
  methods: {
    async fetchTasks() {
      try {
        this.taskListLoading = true;
        this.tasks = [];
        const responsePersonalTasks = await this.$axios.get(
            `/tasks/user/${this.userId}/personal-tasks`
        );
        const responseGroupTasks = await this.$axios.get(
            `/tasks/user/${this.userId}/group-tasks`
        );
        this.tasks = responsePersonalTasks.data.concat(responseGroupTasks.data);
      } catch (error) {
        console.error("获取任务数据失败:", error);
      } finally {
        this.taskListLoading = false; // 加载完成，更新状态
      }
    },
  },
};
</script>

<style scoped>
.container {
  text-align: center;
  margin-top: 30px;
}
</style>
