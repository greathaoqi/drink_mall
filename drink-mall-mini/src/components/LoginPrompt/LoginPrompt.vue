<template>
  <u-popup :show="show" mode="center" :round="14">
    <view class="login-prompt">
      <view class="brand-mark">醇</view>
      <text class="prompt-title">登录后可使用更多功能</text>
      <text class="prompt-desc">{{ message || '请先登录后再进行操作' }}</text>
      <view class="prompt-actions">
        <u-button type="default" plain shape="circle" @click="handleClose" customStyle="width: 45%; color: #D38A00; border-color: #F0E0B7; background: #FCFAF3">
          稍后再说
        </u-button>
        <u-button type="primary" shape="circle" @click="handleLogin" color="#2A0E00" customStyle="width: 45%; color: #FFD685">
          立即登录
        </u-button>
      </view>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
defineProps<{
  show: boolean
  message?: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'login'): void
}>()

function handleClose() {
  emit('close')
}

function handleLogin() {
  emit('login')
}
</script>

<style scoped lang="scss">
.login-prompt {
  width: 560rpx;
  padding: 48rpx;
  background: #FFFFFF;
  border-radius: 28rpx;
  box-sizing: border-box;
}

.brand-mark {
  width: 96rpx;
  height: 96rpx;
  margin: 0 auto 24rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFAEB;
  font-size: 38rpx;
  font-weight: 800;
  background: var(--dm-grad-gold);
}

.prompt-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: var(--dm-text-1);
  text-align: center;
  margin-bottom: 18rpx;
}

.prompt-desc {
  display: block;
  font-size: 26rpx;
  color: var(--dm-text-2);
  line-height: 1.6;
  text-align: center;
  margin-bottom: 36rpx;
}

.prompt-actions {
  display: flex;
  justify-content: space-between;
}
</style>
