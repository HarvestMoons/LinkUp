<!-- ConfirmDialog.vue -->
<template>
  <div v-if="isVisible" class="confirm-dialog-overlay" @click.self="close">
    <div class="confirm-dialog">
      <h3>{{ message }}</h3>
      <div class="doubleButtonContainer">
        <button class="button warningButton" @click="onConfirm">
          {{ $t('common.confirm') }}
        </button>
        <button class="button normalButton" @click="onCancel">
          {{ $t('common.cancel') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    message: {
      type: String,
      required: true,
    },
    isVisible: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    close() {
      this.$emit('update:isVisible', false)
    },
    onConfirm() {
      this.$emit('confirm')
      this.close()
    },
    onCancel() {
      this.$emit('cancel')
      this.close()
    },
  },
}
</script>

<style scoped>
.confirm-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5); /* 半透明灰色 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
}

.confirm-dialog {
  display: flex;
  flex-direction: column;
  margin: 25px;
  padding: 25px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.2);
  text-align: left;
  width: 300px;
  gap: 20px;
}

.confirm-dialog h3 {
  margin: 0;
}
</style>
