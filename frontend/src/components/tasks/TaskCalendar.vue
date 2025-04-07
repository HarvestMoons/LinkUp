<template>
  <FullCalendar :options="calendarOptions" />
</template>

<script>
import FullCalendar from "@fullcalendar/vue3"
import dayGridPlugin from "@fullcalendar/daygrid"
import allLocales from '@fullcalendar/core/locales-all'
import { TaskStatusColorMap } from "@/config/constants"

export default {
  components: { FullCalendar },
  props: { tasks: Array },
  setup(props) {
    // 直接从 localStorage 获取语言，默认 'en'
    const userLanguage = localStorage.getItem('userLanguage') || 'en'

    // 静态配置（无需响应式）
    const calendarOptions = {
      plugins: [dayGridPlugin],
      initialView: "dayGridMonth",
      locale: userLanguage, // 直接使用 localStorage 的值
      locales: allLocales,
      events: props.tasks.map(task => ({
        start: task.dueDate,
        end: task.dueDate.split("T")[0] + "T23:59:59",
        color: isOverdue(task) ? "red" : TaskStatusColorMap[task.status],
        display: 'background'
      }))
    }

    // 逾期判断函数
    const isOverdue = (task) => {
      const now = new Date()
      const dueDate = new Date(task.dueDate)
      return (
          dueDate < now &&
          task.status !== "COMPLETED" &&
          task.status !== "ARCHIVED"
      )
    }

    return {calendarOptions}
  }
}
</script>

<style scoped>
/* 原有样式保持不变 */
.fc-daygrid-event-dot {
  width: 12px;
  height: 12px;
  margin-top: 2px;
  margin-right: 4px;
  border-width: 2px;
}

.fc-event-title {
  display: none;
}
</style>