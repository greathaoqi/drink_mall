<template>
  <view class="login-page">
    <view class="login-hero">
      <view class="status-spacer"></view>
      <view class="logo-orb">
        <view class="glass-logo"></view>
      </view>
      <text class="brand-name">醇品汇</text>
      <text class="brand-slogan">甄选美酒 · 共创财富</text>
    </view>

    <view class="login-sheet">
      <text class="sheet-title">欢迎登录</text>
      <text class="sheet-subtitle">登录后享受完整购物与分销服务</text>

      <button class="wechat-btn" :loading="loading" :disabled="!canLogin || loading" @click="handleLogin">
        <uni-icons type="weixin" size="25" color="#ffffff" />
        <text>微信快捷登录</text>
      </button>

      <button class="guest-btn" :loading="demoLoading" @click="handleDemoLogin">游客浏览</button>

      <view class="agreement-row" @click="toggleAgreements">
        <view class="check-box" :class="{ checked: canLogin }">
          <uni-icons v-if="canLogin" type="checkmarkempty" size="16" color="#ffffff" />
        </view>
        <text>已阅读并同意</text>
        <text class="link-text">《用户协议》</text>
        <text class="link-text">《隐私协议》</text>
      </view>

      <view class="invite-tip">
        <uni-icons type="info" size="16" color="#c27e00" />
        <text>请通过好友分享码进入小程序完成注册</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { demoLogin, wechatLogin } from '@/services/auth'

const agreements = ref({
  userAgreement: true,
  privacyPolicy: true
})
const loading = ref(false)
const demoLoading = ref(false)

const canLogin = computed(() => agreements.value.userAgreement && agreements.value.privacyPolicy)

function toggleAgreements() {
  const next = !canLogin.value
  agreements.value = {
    userAgreement: next,
    privacyPolicy: next
  }
}

async function handleLogin() {
  if (!canLogin.value) {
    uni.showToast({ title: '请先同意用户协议和隐私协议', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const result = await wechatLogin({
      userAgreement: agreements.value.userAgreement,
      privacyPolicy: agreements.value.privacyPolicy
    })

    if (result) {
      uni.showToast({ title: '登录成功', icon: 'success' })
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        uni.switchTab({ url: '/pages/index/index' })
      }
    } else {
      uni.showToast({ title: '登录失败，请重试', icon: 'none' })
    }
  } catch (error) {
    console.error('Login failed:', error)
    uni.showToast({ title: '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function handleDemoLogin() {
  demoLoading.value = true
  try {
    const result = await demoLogin()
    if (result) {
      uni.showToast({ title: '已进入演示账号', icon: 'success' })
      uni.switchTab({ url: '/pages/index/index' })
    } else {
      uni.switchTab({ url: '/pages/index/index' })
    }
  } finally {
    demoLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: #ffffff;
  color: #1f160f;
}

.login-hero {
  min-height: 610rpx;
  background: #2b0f00;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.status-spacer {
  height: 140rpx;
}

.logo-orb {
  width: 192rpx;
  height: 192rpx;
  border-radius: 50%;
  background: linear-gradient(145deg, #ffcf3d 0%, #d18b00 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 26rpx 58rpx rgba(0, 0, 0, 0.18);
}

.glass-logo {
  position: relative;
  width: 52rpx;
  height: 76rpx;
  border: 8rpx solid #ffffff;
  border-top: 0;
  border-radius: 8rpx 8rpx 28rpx 28rpx;
}

.glass-logo::before,
.glass-logo::after {
  content: '';
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  background: #ffffff;
}

.glass-logo::before {
  bottom: -48rpx;
  width: 8rpx;
  height: 46rpx;
}

.glass-logo::after {
  bottom: -52rpx;
  width: 54rpx;
  height: 8rpx;
  border-radius: 999rpx;
}

.brand-name {
  margin-top: 34rpx;
  color: #f7c22b;
  font-size: 50rpx;
  font-weight: 900;
  letter-spacing: 4rpx;
}

.brand-slogan {
  margin-top: 22rpx;
  color: #d59a27;
  font-size: 26rpx;
  letter-spacing: 4rpx;
}

.login-sheet {
  margin-top: -54rpx;
  min-height: 520rpx;
  padding: 62rpx 54rpx 40rpx;
  border-radius: 56rpx 56rpx 0 0;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.sheet-title {
  color: #1f160f;
  font-size: 40rpx;
  font-weight: 900;
}

.sheet-subtitle {
  margin-top: 26rpx;
  color: #b1a79a;
  font-size: 25rpx;
}

.wechat-btn,
.guest-btn {
  width: 100%;
  height: 104rpx;
  line-height: 104rpx;
  border-radius: 999rpx;
  font-size: 31rpx;
  font-weight: 800;
}

.wechat-btn {
  margin-top: 42rpx;
  color: #ffffff;
  background: #10b85a;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14rpx;
  border: none;
}

.guest-btn {
  margin-top: 36rpx;
  color: #c27e00;
  background: #fffdf8;
  border: 2rpx solid #e7c982;
}

.wechat-btn::after,
.guest-btn::after {
  border: none;
}

.agreement-row {
  width: 100%;
  margin-top: 36rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  color: #8c8377;
  font-size: 23rpx;
}

.check-box {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #d39200;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.check-box.checked {
  background: #d39200;
}

.link-text {
  color: #6f665c;
}

.invite-tip {
  width: 100%;
  height: 80rpx;
  margin-top: 40rpx;
  padding: 0 28rpx;
  border-radius: 16rpx;
  background: #fff0c4;
  color: #9b6a0d;
  font-size: 23rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  box-sizing: border-box;
}
</style>
