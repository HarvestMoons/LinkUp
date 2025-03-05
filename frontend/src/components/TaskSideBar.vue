<!-- TaskSideBar.vue -->
<template>
  <transition name="slide">
    <div v-if="isVisible" class="sidebar">
      <h2>{{ title }}</h2>
      <button class="closeBtn" @click="$emit('close')">✖ 关闭</button>
      <TaskList
        :tasks="tasks"
        :taskListLoading="taskListLoading"
        :groupId="groupId"
        :fetchTasks="fetchTasks"
      />
    </div>
  </transition>
</template>

<script>
import TaskList from "@/components/TaskList.vue";
export default {
  components: { TaskList },
  props: {
    title: String,
    isVisible: Boolean, // 控制侧边栏是否显示
    tasks: Array,
    taskListLoading: Boolean,
    groupId: Number,
    fetchTasks: {
      type: Function,
      required: true, // 确保调用 TaskList 时必须提供
    },
  },
};
</script>

<style scoped>
.sidebar {
  width: 600px;
  height: 100%;
  position: fixed;
  top: 0;
  right: 0;
  background: white;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
  padding: 20px;
  overflow-y: auto;
}
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease;
}
.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
}
.closeBtn {
  background: red;
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  color: white;
  width: 100px;
}
</style>