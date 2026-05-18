<template>
  <view v-if="loading" class="state state-loading">
    <view class="spinner"></view>
    <text class="title">{{ loadingText || '加载中...' }}</text>
    <text v-if="loadingDesc" class="desc">{{ loadingDesc }}</text>
  </view>
  <view v-else-if="authRequired" class="state state-auth">
    <image class="state-image" src="/static/images/empty-state.png" mode="aspectFill" />
    <text class="title">{{ authTitle || '请先登录' }}</text>
    <text class="desc">{{ authText || '登录后可使用购买、分享获佣、提现等完整功能' }}</text>
    <button class="primary" @click="$emit('login')">{{ authButtonText || '微信一键登录' }}</button>
    <button v-if="allowGuest" class="ghost" @click="$emit('guest')">继续浏览</button>
  </view>
  <view v-else-if="error" class="state state-error">
    <view class="error-icon">!</view>
    <text class="title">{{ errorTitle || '网络连接异常' }}</text>
    <text class="desc">{{ errorText || error }}</text>
    <button class="primary" @click="$emit('retry')">{{ retryText || '重新加载' }}</button>
  </view>
  <view v-else-if="empty" class="state state-empty">
    <image class="state-image" src="/static/images/empty-state.png" mode="aspectFill" />
    <text class="title">{{ emptyTitle || '暂无数据' }}</text>
    <text class="desc">{{ emptyText }}</text>
    <button v-if="emptyActionText" class="primary" @click="$emit('empty-action')">{{ emptyActionText }}</button>
  </view>
  <slot v-else />
</template>

<script setup lang="ts">
defineProps<{
  loading?: boolean
  loadingText?: string
  loadingDesc?: string
  error?: string
  errorTitle?: string
  errorText?: string
  retryText?: string
  empty?: boolean
  emptyTitle?: string
  emptyText?: string
  emptyActionText?: string
  authRequired?: boolean
  authTitle?: string
  authText?: string
  authButtonText?: string
  allowGuest?: boolean
}>()

defineEmits<{
  (e: 'retry'): void
  (e: 'empty-action'): void
  (e: 'login'): void
  (e: 'guest'): void
}>()
</script>

<style scoped lang="scss">
.state {
  min-height: 420rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 72rpx 48rpx;
  color: var(--dm-text-3);
  text-align: center;
}

.state-image {
  width: 240rpx;
  height: 240rpx;
  border-radius: 50%;
  margin-bottom: 28rpx;
  background: var(--dm-cream-100);
}

.spinner {
  width: 64rpx;
  height: 64rpx;
  margin-bottom: 28rpx;
  border: 6rpx solid rgba(255, 214, 70, 0.25);
  border-top-color: var(--dm-gold-500);
  border-radius: 50%;
  animation: dm-spin 0.9s linear infinite;
}

.error-icon {
  width: 128rpx;
  height: 128rpx;
  border-radius: 50%;
  margin-bottom: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FFF1EF;
  color: var(--dm-red-500);
  font-size: 64rpx;
  font-weight: 800;
}

.title {
  font-size: 32rpx;
  font-weight: 700;
  color: var(--dm-text-1);
}

.desc {
  margin-top: 14rpx;
  max-width: 560rpx;
  font-size: 26rpx;
  line-height: 1.6;
  color: var(--dm-text-3);
}

button {
  min-width: 220rpx;
  margin-top: 32rpx;
  height: 76rpx;
  line-height: 76rpx;
  border-radius: var(--dm-radius-pill);
  font-size: 26rpx;
  font-weight: 700;
}

button::after {
  border: 0;
}

.primary {
  color: #3A1A00;
  background: var(--dm-grad-gold);
}

.ghost {
  margin-top: 18rpx;
  color: var(--dm-gold-500);
  background: var(--dm-gold-50);
}

@keyframes dm-spin {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
