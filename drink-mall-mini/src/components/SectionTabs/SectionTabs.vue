<template>
  <scroll-view class="tabs-scroll" scroll-x :show-scrollbar="false">
    <view class="tabs">
      <view
        v-for="item in items"
        :key="String(item.value)"
        class="tab dm-press"
        :class="{ active: item.value === modelValue }"
        @click="select(item.value)"
      >
        <text>{{ item.label }}</text>
        <text v-if="item.badge" class="badge">{{ item.badge }}</text>
      </view>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
export interface SectionTabItem {
  label: string
  value: string | number
  badge?: string | number
}

defineProps<{
  items: SectionTabItem[]
  modelValue?: string | number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number): void
  (e: 'change', value: string | number): void
}>()

function select(value: string | number) {
  emit('update:modelValue', value)
  emit('change', value)
}
</script>

<style scoped lang="scss">
.tabs-scroll {
  width: 100%;
  background: #FFFFFF;
  border-bottom: 2rpx solid var(--dm-line-soft);
}

.tabs {
  min-width: 100%;
  display: flex;
  align-items: center;
  padding: 0 16rpx;
  box-sizing: border-box;
}

.tab {
  min-width: 112rpx;
  height: 88rpx;
  padding: 0 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  position: relative;
  color: var(--dm-text-2);
  font-size: 26rpx;
  font-weight: 500;
}

.tab.active {
  color: var(--dm-gold-500);
  font-weight: 700;
}

.tab.active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 44rpx;
  height: 4rpx;
  border-radius: 999rpx;
  transform: translateX(-50%);
  background: var(--dm-grad-gold);
}

.badge {
  min-width: 28rpx;
  height: 28rpx;
  padding: 0 8rpx;
  border-radius: 999rpx;
  background: var(--dm-gold-50);
  color: var(--dm-gold-600);
  font-size: 20rpx;
  line-height: 28rpx;
}
</style>
