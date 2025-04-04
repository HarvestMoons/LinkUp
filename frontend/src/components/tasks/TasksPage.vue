<!-- TasksPage.vue -->
<template>
  <div class="container">
    <TaskList
      :tasks="tasks"
      :taskListLoading="taskListLoading"
      :groupId="null"
      :isInGroupPage="false"
    />
  </div>
</template>

<script>
import TaskList from "@/components/tasks/TaskList.vue";

import { useToast } from "vue-toastification";
import { getTaskList } from "@/utils/taskService";

export default {
  name: "TasksPage",
  components: { TaskList },
  data() {
    return {
      taskListLoading: false,
      tasks: [],
    };
  },
  setup() {
    const toast = useToast();
    return { toast };
  },
  async mounted() {
    this.userId = this.$store.getters.getUserId; // 读取 userId
    if (!this.userId) {
      console.error("用户ID不存在，请重新登录");
      return;
    }
    this.taskListLoading = true;
    this.tasks = await getTaskList(this.userId);
    this.taskListLoading = false; // 加载完成，更新状态
  },
  methods: {},
};
</script>

