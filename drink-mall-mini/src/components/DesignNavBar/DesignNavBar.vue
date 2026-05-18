<template>
  <view class="nav-shell" :class="[`nav-${theme}`, { fixed, 'no-status': !safeAreaInsetTop }]">
    <view v-if="safeAreaInsetTop" class="status-spacer"></view>
    <view class="nav-bar">
      <view class="nav-side left" @click="handleLeft">
        <slot name="left">
          <text v-if="leftIcon === 'back'" class="nav-icon">‹</text>
          <text v-else-if="leftIcon === 'close'" class="nav-icon">×</text>
        </slot>
      </view>
      <text class="nav-title">{{ title }}</text>
      <view class="nav-side right">
        <slot name="right" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  title?: string
  theme?: 'light' | 'brown' | 'transparent'
  leftIcon?: 'back' | 'close' | 'none'
  fixed?: boolean
  safeAreaInsetTop?: boolean
}>(), {
  title: '',
  theme: 'light',
  leftIcon: 'back',
  fixed: false,
  safeAreaInsetTop: true
})

const emit = defineEmits<{
  (e: 'back'): void
  (e: 'close'): void
}>()

function handleLeft() {
  if (props.leftIcon === 'none') return
  emit(props.leftIcon)
  if (props.leftIcon === 'back') {
    uni.navigateBack({ delta: 1 })
  }
}
</script>

<style scoped lang="scss">
.nav-shell {
  width: 100%;
  background: #FFFFFF;
  color: var(--dm-text-1);
  position: relative;
  z-index: 20;
}

.fixed {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
}

.status-spacer {
  height: var(--status-bar-height);
}

.nav-bar {
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 28rpx;
  box-sizing: border-box;
}

.nav-title {
  max-width: 420rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 34rpx;
  font-weight: 700;
}

.nav-side {
  position: absolute;
  top: calc(var(--status-bar-height) + 44rpx);
  transform: translateY(-50%);
  min-width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
}

.no-status .nav-side {
  top: 44rpx;
}

.left {
  left: 8rpx;
  justify-content: center;
}

.right {
  right: 24rpx;
  justify-content: flex-end;
}

.nav-icon {
  font-size: 52rpx;
  line-height: 1;
  font-weight: 400;
}

.nav-brown {
  color: var(--dm-text-on-brown);
  background: var(--dm-grad-brown);
}

.nav-transparent {
  color: var(--dm-text-on-brown);
  background: transparent;
}
</style>
