<!-- TasksPage.vue -->
<template>
  <div class="container">
    <TaskList
      :tasks="tasks"
      :task-list-loading="taskListLoading"
      :group-id="null"
      :is-in-group-page="false"
      :refresh-task-list="fetchAllTasks"
    />
  </div>
</template>

<script>
import TaskList from '@/components/tasks/TaskList.vue'

import { useToast } from 'vue-toastification'
import { fetchAllTasks, getTaskList } from '@/utils/taskService'

export default {
  name: 'TasksPage',
  components: { TaskList },
  setup() {
    const toast = useToast()
    return { toast }
  },
  data() {
    return {
      taskListLoading: false,
    }
  },
  computed: {
    tasks() {
      return this.$store.getters.getTaskList || this.tasks
    },
  },
  async mounted() {
    this.userId = this.$store.getters.getUserId // 读取 userId
    if (!this.userId) {
      return
    }
    this.taskListLoading = true
    this.tasks = await getTaskList(this.userId)
    this.taskListLoading = false // 加载完成，更新状态
  },
  methods: {
    fetchAllTasks() {
      fetchAllTasks(this.userId)
    },
  },
}
</script>
