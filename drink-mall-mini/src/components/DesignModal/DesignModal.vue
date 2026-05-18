<template>
  <view v-if="show" class="modal-mask" @click="handleMask">
    <view class="modal-card" @click.stop>
      <slot name="icon" />
      <text v-if="title" class="modal-title">{{ title }}</text>
      <text v-if="content" class="modal-content">{{ content }}</text>
      <slot />
      <view class="modal-actions">
        <button class="action cancel" @click="$emit('cancel')">{{ cancelText }}</button>
        <button class="action confirm" @click="$emit('confirm')">{{ confirmText }}</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  show: boolean
  title?: string
  content?: string
  cancelText?: string
  confirmText?: string
  closeOnMask?: boolean
}>(), {
  cancelText: '取消',
  confirmText: '确认',
  closeOnMask: false
})

const emit = defineEmits<{
  (e: 'cancel'): void
  (e: 'confirm'): void
  (e: 'mask'): void
}>()

function handleMask() {
  emit('mask')
  if (props.closeOnMask) emit('cancel')
}
</script>

<style scoped lang="scss">
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 80;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
}

.modal-card {
  width: 600rpx;
  overflow: hidden;
  background: #FFFFFF;
  border-radius: 28rpx;
  box-shadow: var(--dm-shadow-pop);
  text-align: center;
  padding: 44rpx 40rpx 0;
  box-sizing: border-box;
}

.modal-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: var(--dm-text-1);
}

.modal-content {
  display: block;
  margin-top: 20rpx;
  font-size: 26rpx;
  line-height: 1.6;
  color: var(--dm-text-2);
}

.modal-actions {
  display: flex;
  margin: 36rpx -40rpx 0;
  border-top: 2rpx solid var(--dm-line);
}

.action {
  flex: 1;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 0;
  background: transparent;
  color: var(--dm-text-2);
  font-size: 32rpx;
  font-weight: 600;
}

.action::after {
  border: 0;
}

.confirm {
  color: var(--dm-gold-500);
  border-left: 2rpx solid var(--dm-line);
}
</style>
