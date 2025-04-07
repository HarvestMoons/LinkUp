<!-- HelpTooltio.vue -->
<template>
  <div
    class="help-tooltip-wrapper"
    @mouseenter="show = true"
    @mouseleave="show = false"
  >
    <!-- 问号图标 -->
    <div class="help-icon">?</div>

    <!-- 浮窗 -->
    <div v-if="show" class="tooltip-box">
      {{ message }}
    </div>
  </div>
</template>

<script>
export default {
  name: "HelpTooltip",
  props: {
    message: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      show: false,
    };
  },
};
</script>

<style scoped>
.help-tooltip-wrapper {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 100;
}

.help-icon {
  width: 16px;
  height: 16px;
  background-color: #938a8a;
  color: white;
  font-weight: bold;
  text-align: center;
  line-height: 16px;
  border-radius: 50%;
  font-size: 12px;
  cursor: pointer;
}

.tooltip-box {
  position: absolute;
  top: 100%;
  right: 8px;
  transform: translateY(-8px);
  background-color: #333;
  color: #fff;
  padding: 8px 10px;
  border-radius: 6px;
  font-size: 13px;
  white-space: pre-wrap;
  min-width: 140px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}
</style>
