<!-- SideBar.vue -->
<template>
  <transition name="cover">
    <!-- 遮罩层 -->
    <div v-if="isVisible" class="overlay" @click="$emit('close')"></div>
  </transition>

  <transition name="slide">
    <div v-show="isVisible" class="sidebar">
      <h2>{{ title }}</h2>
      <button class="closeBtn" @click="$emit('close')">✖ 关闭</button>
      <component :is="contentComponent" v-bind="contentProps"></component>
    </div>
  </transition>
</template>

<script>
export default {
  props: {
    title: String,
    isVisible: Boolean,
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
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5); /* 半透明灰色 */
  z-index: 999; /* 确保遮罩层在侧边栏之下 */
}

.sidebar {
  width: 640px;
  height: 100%;
  position: fixed;
  top: 0;
  right: 0;
  background: white;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
  padding: 20px;
  overflow-y: auto;
  z-index: 1000;
  box-sizing: border-box;
}

.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
}

.cover-enter-active,
.cover-leave-active {
  transition: opacity 0.3s ease;
}

.cover-enter-from,
.cover-leave-to {
  opacity: 0;
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