<template>
  <view class="bar">
    <slot name="left" />
    <view class="actions">
      <slot />
    </view>
  </view>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 30;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  min-height: 112rpx;
  padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
  background: #FFFFFF;
  border-top: 2rpx solid var(--dm-line-soft);
  box-shadow: var(--dm-shadow-action);
}

.actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16rpx;
  flex: 1;
}
</style>
