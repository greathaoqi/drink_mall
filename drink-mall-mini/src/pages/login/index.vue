<template>
  <view class="login-page">
    <view class="logo-section">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="app-name">酒水商城</text>
      <text class="app-slogan">精选美酒，品质生活</text>
    </view>

    <view class="login-section">
      <AgreementBox ref="agreementRef" @change="handleAgreementChange" />

      <u-button type="primary" shape="circle" :disabled="!canLogin" @click="handleLogin" color="#07C160" customStyle="margin-top: 32rpx; width: 100%">
        <u-icon name="weixin-fill" color="#FFFFFF" size="40" />
        <text class="login-text">微信登录</text>
      </u-button>

      <view class="tips">
        <text class="tip-text">登录即表示您已阅读并同意相关协议</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import AgreementBox from '@/components/AgreementBox/AgreementBox.vue'
import { wechatLogin } from '@/services/auth'

const agreementRef = ref()
const agreements = ref({
  userAgreement: false,
  privacyPolicy: false
})
const loading = ref(false)

const canLogin = computed(() => {
  return agreements.value.userAgreement && agreements.value.privacyPolicy
})

function handleAgreementChange(value: { userAgreement: boolean; privacyPolicy: boolean }) {
  agreements.value = value
}

async function handleLogin() {
  if (!canLogin.value) {
    uni.showToast({ title: '请先同意用户协议和隐私政策', icon: 'none' })
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
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: #FFFFFF;
  display: flex;
  flex-direction: column;
  padding: 0 48rpx;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 160rpx;
  margin-bottom: 128rpx;
}

.logo {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 32rpx;
}

.app-name {
  font-size: 40rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16rpx;
}

.app-slogan {
  font-size: 28rpx;
  color: #999999;
}

.login-section {
  padding: 0 16rpx;
}

.login-text {
  margin-left: 16rpx;
  color: #FFFFFF;
}

.tips {
  margin-top: 32rpx;
  text-align: center;
}

.tip-text {
  font-size: 24rpx;
  color: #CCCCCC;
}
</style>