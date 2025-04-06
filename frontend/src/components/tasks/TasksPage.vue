<!-- TasksPage.vue -->
<template>
  <div class="container">
    <TaskList
      :tasks="tasks"
      :taskListLoading="taskListLoading"
      :groupId="null"
      :isInGroupPage="false"
      :refreshTaskList="fetchAllTasks"
    />
  </div>
</template>

<script>
import TaskList from "@/components/tasks/TaskList.vue";

import { useToast } from "vue-toastification";
import { fetchAllTasks, getTaskList } from "@/utils/taskService";

export default {
  name: "TasksPage",
  components: { TaskList },
  data() {
    return {
      taskListLoading: true,
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  computed: {
    tasks() {
      return this.$store.getters.getTaskList;
    },
  },
  async mounted() {
    this.userId = this.$store.getters.getUserId; // 读取 userId
    if (!this.userId) {
      return;
    }
    this.taskListLoading = true;
    this.tasks = await getTaskList(this.userId);
    this.taskListLoading = false; // 加载完成，更新状态
  },
  methods: {
    fetchAllTasks() {
      fetchAllTasks(this.userId);
    },
  },
};
</script>

