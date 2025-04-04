<template>
  <FullCalendar :options="calendarOptions" />
</template>

<script>
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import { TaskStatusColorMap } from "@/config/constants";

export default {
  components: { FullCalendar },
  props: { tasks: Array },
  computed: {
    calendarOptions() {
      return {
        plugins: [dayGridPlugin],
        initialView: "dayGridMonth",
        events: this.tasks.map((task) => ({
          // title: task.title,
          start: task.dueDate,
          end: task.dueDate.split("T")[0] + "T23:59:59",
          color: this.isOverdue(task) ? "red" : TaskStatusColorMap[task.status],
          // allDay: true,
        })),
        // eventContent: this.renderEventContent,
      };
    },
  },
  methods: {
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
    /*
    // 自定义事件内容渲染
    renderEventContent(eventInfo) {
      const { event } = eventInfo;

      // 自定义样式
      const circleStyle = `background-color: ${event.color}; width: 10px; height: 10px;`;

      // 你可以根据 `event.title` 或 `taskId` 来定制内容
      return {
        domNodes: [
          // 添加自定义的圆圈
          this.createCircle(circleStyle),
          // 是否显示标题
          this.createTitle(),
        ],
      };
    },
    // 创建圆圈
    createCircle(style) {
      const circle = document.createElement("div");
      circle.style.borderRadius = "50%";
      circle.style.cssText = style;
      return circle;
    },
    // 创建标题
    createTitle() {
      const titleElement = document.createElement("div");
      titleElement.style.display = "none";
    },
    */
  },
};
</script>

<style scoped>
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
