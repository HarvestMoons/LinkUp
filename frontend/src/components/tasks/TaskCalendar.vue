<template>
  <FullCalendar :options="calendarOptions" />
</template>

<script>
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import allLocales from '@fullcalendar/core/locales-all'
import { TaskStatusColorMap } from '@/config/constants'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

export default {
  components: { FullCalendar },
  props: { tasks: Array },
  setup(props) {
    const { locale } = useI18n()

    // 静态配置（无需响应式）
    const calendarOptions = computed(() => ({
      plugins: [dayGridPlugin],
      initialView: 'dayGridMonth',
      locale: locale.value, // 直接使用 localStorage 的值
      locales: allLocales,
      events: props.tasks.map((task) => ({
        start: task.dueDate,
        end: task.dueDate.split('T')[0] + 'T23:59:59',
        color: isOverdue(task) ? 'red' : TaskStatusColorMap[task.status],
      })),
    }))

    // 逾期判断函数
    const isOverdue = (task) => {
      const now = new Date()
      const dueDate = new Date(task.dueDate)
      return dueDate < now && task.status !== 'COMPLETED' && task.status !== 'ARCHIVED'
    }
    return { calendarOptions }
  },
}
</script>
