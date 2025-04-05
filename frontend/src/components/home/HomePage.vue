<!-- HomePage.vue -->
<template>
  <div class="container">
    <div class="home-container">
      <!-- 左侧信息面板 -->
      <div class="info-panel">
        <h2 class="greeting">
          {{ $t('dashboard.greeting', { name: this.$store.getters.getUser.username }) }}
        </h2>

        <div class="blockContainer">
          <h3>{{ $t('dashboard.todoTitle') }}</h3>
          <p>{{ $t('dashboard.pendingTasks', todoTasks.length, { count: todoTasks.length }) }}</p>
        </div>

        <div class="blockContainer">
          <h3>{{ $t('dashboard.completionRate') }}</h3>
          <p>{{ $t('dashboard.weeklyRate', { rate: weeklyCompletionRate }) }}</p>
          <p>{{ $t('dashboard.monthlyRate', { rate: monthlyCompletionRate }) }}</p>
        </div>

        <div class="blockContainer" v-if="upcomingDeadlines.length !== 0">
          <h3>{{ $t('dashboard.upcomingDeadlines') }}</h3>
          <ul>
            <li v-for="task in upcomingDeadlines" :key="task.id">
              {{ $t('dashboard.deadlineItem', {
              title: task.title,
              date: formatDate(task.dueDate)
            }) }}
            </li>
          </ul>
        </div>
      </div>

      <!-- 右侧日历 -->
      <div class="calendar-view">
        <TaskCalendar :tasks="tasks" />
      </div>
    </div>
  </div>
</template>

<script>
import { getTaskList } from "@/utils/taskService";
import TaskCalendar from "@/components/tasks/TaskCalendar.vue";
import { TaskStatus } from "@/config/constants";

export default {
  name: "HomePage",
  components: { TaskCalendar },
  data() {
    return {
      user: null,
      userId: null,
      tasks: [],
      todoTasks: [],
    };
  },
  async mounted() {
    this.user = this.$store.getters.getUser;
    this.userId = this.$store.getters.getUserId; // 读取 userId
    if (!this.userId) {
      return;
    }
    this.tasks = await getTaskList(this.userId);
    this.todoTasks = this.tasks.filter(
      (task) => task.status === TaskStatus.Todo
    );
    console.log(this.tasks);
  },
  computed: {
    weeklyCompletionRate() {
      const range = this.getThisWeekRange();
      return this.calculateCompletionRate(range);
    },
    monthlyCompletionRate() {
      const range = this.getThisMonthRange();
      return this.calculateCompletionRate(range);
    },
    upcomingDeadlines() {
      const now = new Date();
      const soon = new Date();
      soon.setDate(now.getDate() + 3);
      return this.tasks
        .filter(
          (t) =>
            new Date(t.dueDate) > now &&
            new Date(t.dueDate) <= soon &&
            t.status !== TaskStatus.Completed
        )
        .slice(0, 5);
    },
  },
  methods: {
    getThisWeekRange() {
      const today = new Date();
      const dayOfWeek = today.getDay(); // 0 = Sunday
      const start = new Date(today);
      start.setDate(today.getDate() - dayOfWeek);
      const end = new Date(start);
      end.setDate(start.getDate() + 6);
      return { start, end };
    },
    getThisMonthRange() {
      const now = new Date();
      const start = new Date(now.getFullYear(), now.getMonth(), 1);
      const end = new Date(now.getFullYear(), now.getMonth() + 1, 0);
      return { start, end };
    },
    calculateCompletionRate(range) {
      const filtered = this.tasks.filter((task) => {
        const date = new Date(task.dueDate);
        return date >= range.start && date <= range.end;
      });
      if (filtered.length === 0) return 0;
      const completed = filtered.filter(
        (t) => t.status === TaskStatus.Completed
      ).length;
      return ((completed / filtered.length) * 100).toFixed(1);
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString("zh-CN");
    },
  },
};
</script>

<style scoped>
.home-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 20px;

  align-items: center; /* 确保头像和文字都在导航栏中垂直居中 */
  justify-content: center; /* 水平居中对齐 */
}

.info-panel {
  flex: 1;
  max-width: 400px;
  min-width: 300px;
  gap: 10px;
}

h3,
p {
  margin: 0;
}

.blockContainer {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.calendar-view {
  flex: 2;
  max-width: 600px;
  min-width: 400px;
}
</style>