<template>
  <view v-if="show" class="sheet-layer">
    <view class="sheet-mask" @click="$emit('close')"></view>
    <view class="sheet">
      <view v-if="title" class="sheet-title">{{ title }}</view>
      <view
        v-for="action in actions"
        :key="action.key || action.label"
        class="sheet-item dm-press"
        :class="{ danger: action.danger }"
        @click="choose(action)"
      >
        <view class="item-icon" :style="{ color: action.color || 'var(--dm-gold-500)' }">
          <text>{{ action.icon || '' }}</text>
        </view>
        <view class="item-main">
          <text class="item-label">{{ action.label }}</text>
          <text v-if="action.desc" class="item-desc">{{ action.desc }}</text>
        </view>
        <text class="chevron">›</text>
      </view>
      <view class="sheet-gap"></view>
      <button class="cancel" @click="$emit('close')">{{ cancelText }}</button>
    </view>
  </view>
</template>

<script setup lang="ts">
export interface DesignActionSheetItem {
  key?: string | number
  label: string
  desc?: string
  icon?: string
  color?: string
  danger?: boolean
}

withDefaults(defineProps<{
  show: boolean
  title?: string
  actions: DesignActionSheetItem[]
  cancelText?: string
}>(), {
  title: '请选择操作',
  cancelText: '取消'
})

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'select', action: DesignActionSheetItem): void
}>()

function choose(action: DesignActionSheetItem) {
  emit('select', action)
}
</script>

<style scoped lang="scss">
.sheet-layer,
.sheet-mask {
  position: fixed;
  inset: 0;
}

.sheet-layer {
  z-index: 70;
}

.sheet-mask {
  background: rgba(0, 0, 0, 0.45);
}

.sheet {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 71;
  overflow: hidden;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: #FFFFFF;
  border-radius: 40rpx 40rpx 0 0;
  box-shadow: var(--dm-shadow-pop);
}

.sheet-title {
  padding: 28rpx 32rpx;
  color: var(--dm-text-3);
  font-size: 26rpx;
  text-align: center;
}

.sheet-item {
  min-height: 112rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 20rpx 40rpx;
  border-top: 2rpx solid var(--dm-line-soft);
  box-sizing: border-box;
}

.item-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--dm-gold-50);
  font-size: 32rpx;
  font-weight: 700;
}

.item-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.item-label {
  color: var(--dm-text-1);
  font-size: 30rpx;
  font-weight: 600;
}

.item-desc {
  color: var(--dm-gold-500);
  font-size: 22rpx;
}

.danger .item-label {
  color: var(--dm-red-500);
}

.chevron {
  color: #C9BFA9;
  font-size: 36rpx;
}

.sheet-gap {
  height: 16rpx;
  background: var(--dm-bg-app);
}

.cancel {
  height: 92rpx;
  line-height: 92rpx;
  background: #FFFFFF;
  color: var(--dm-text-2);
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 0;
}

.cancel::after {
  border: 0;
}
</style>
