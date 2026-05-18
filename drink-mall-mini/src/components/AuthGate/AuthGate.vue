<template>
  <view v-if="show" class="auth-mask">
    <view class="auth-card">
      <view class="brand-mark">醇</view>
      <text class="title">{{ title || '请先登录' }}</text>
      <text class="desc">{{ message || '登录后可使用购买、分享获佣、提现等完整功能' }}</text>
      <button class="wechat" @click="$emit('login')">微信一键登录</button>
      <button v-if="allowGuest" class="guest" @click="$emit('guest')">继续游客浏览</button>
      <view v-if="showAgreement" class="agreement">
        已阅读并同意
        <text>《用户协议》</text>
        <text>《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  show: boolean
  title?: string
  message?: string
  allowGuest?: boolean
  showAgreement?: boolean
}>(), {
  allowGuest: true,
  showAgreement: true
})

defineEmits<{
  (e: 'login'): void
  (e: 'guest'): void
}>()
</script>

<style scoped lang="scss">
.auth-mask {
  position: fixed;
  inset: 0;
  z-index: 90;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48rpx;
  background: rgba(0, 0, 0, 0.55);
  box-sizing: border-box;
}

.auth-card {
  width: 100%;
  max-width: 600rpx;
  padding: 48rpx 40rpx 0;
  box-sizing: border-box;
  text-align: center;
  background: #FFFFFF;
  border-radius: 28rpx;
  box-shadow: var(--dm-shadow-pop);
}

.brand-mark {
  width: 112rpx;
  height: 112rpx;
  margin: 0 auto;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFAEB;
  font-size: 44rpx;
  font-weight: 800;
  background: var(--dm-grad-gold);
  box-shadow: 0 16rpx 56rpx rgba(228, 165, 22, 0.45);
}

.title {
  display: block;
  margin-top: 28rpx;
  color: var(--dm-text-1);
  font-size: 34rpx;
  font-weight: 700;
}

.desc {
  display: block;
  margin-top: 20rpx;
  color: var(--dm-text-2);
  font-size: 26rpx;
  line-height: 1.7;
}

button {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: var(--dm-radius-pill);
  font-size: 32rpx;
  font-weight: 700;
}

button::after {
  border: 0;
}

.wechat {
  margin-top: 44rpx;
  color: #FFFFFF;
  background: var(--dm-green-wechat);
}

.guest {
  margin-top: 20rpx;
  color: var(--dm-gold-500);
  background: var(--dm-cream-50);
  border: 2rpx solid var(--dm-line);
}

.agreement {
  padding: 36rpx 0 28rpx;
  color: var(--dm-text-3);
  font-size: 24rpx;
  line-height: 1.6;
}

.agreement text {
  color: var(--dm-gold-500);
  margin-left: 6rpx;
}
</style>
