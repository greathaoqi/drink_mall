<template>
  <u-popup :show="show" mode="center" :round="16">
    <view class="login-prompt">
      <text class="prompt-title">登录后可使用更多功能</text>
      <text class="prompt-desc">{{ message || '请先登录后再进行操作' }}</text>
      <view class="prompt-actions">
        <u-button type="default" plain shape="circle" @click="handleClose" customStyle="width: 45%">
          稍后再说
        </u-button>
        <u-button type="primary" shape="circle" @click="handleLogin" color="#07C160" customStyle="width: 45%">
          立即登录
        </u-button>
      </view>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
const props = defineProps<{
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
}

.prompt-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  text-align: center;
  margin-bottom: 16rpx;
}

.prompt-desc {
  display: block;
  font-size: 28rpx;
  color: #999999;
  text-align: center;
  margin-bottom: 32rpx;
}

.prompt-actions {
  display: flex;
  justify-content: space-between;
}
</style>