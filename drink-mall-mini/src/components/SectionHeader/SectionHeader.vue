<template>
  <view class="section-head">
    <view class="title-wrap">
      <text class="mark"></text>
      <text class="title">{{ title }}</text>
      <text v-if="subtitle" class="subtitle">{{ subtitle }}</text>
    </view>
    <view v-if="actionText" class="action mall-press" @click="$emit('action')">
      <text>{{ actionText }}</text>
      <uni-icons type="right" size="14" color="#999" />
    </view>
  </view>
</template>

<script setup lang="ts">
defineProps<{ title: string; subtitle?: string; actionText?: string }>()
defineEmits<{ (e: 'action'): void }>()
</script>

<style scoped lang="scss">
.section-head {
  height: 76rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}

.title-wrap {
  display: flex;
  align-items: center;
  gap: 12rpx;
  min-width: 0;
}

.mark {
  width: 8rpx;
  height: 30rpx;
  border-radius: 999rpx;
  background: var(--dm-grad-gold);
}

.title {
  font-size: 32rpx;
  color: var(--dm-text-1);
  font-weight: 800;
}

.subtitle {
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.action {
  display: flex;
  align-items: center;
  flex: 0 0 auto;
  color: var(--dm-text-3);
  font-size: 24rpx;
}
</style>
