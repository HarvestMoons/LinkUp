<!-- SideBar.vue -->
<template>
  <transition name="slide">
    <div v-if="isVisible" class="sidebar">
      <h2>{{ title }}</h2>
      <button class="closeBtn" @click="$emit('close')">✖ 关闭</button>
      <component :is="contentComponent" v-bind="contentProps"></component>
    </div>
  </transition>
</template>

<script>
//todo:点击侧边栏之外的地方时缩起侧边栏
export default {
  props: {
    title: String,
    isVisible: Boolean, // 控制侧边栏是否显示
    contentComponent: {
      type: Object, // 这里传递的是 Vue 组件对象
      required: true,
    },
    contentProps: {
      type: Object, // 传递给组件的 props
      default: () => ({}), // 默认是空对象
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