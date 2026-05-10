<template>
  <view class="profile-page">
    <view v-if="!userStore.isLoggedIn" class="guest-section">
      <view class="guest-avatar">酒</view>
      <text class="guest-text">未登录</text>
      <button class="demo-login-btn" :loading="demoLoading" @click="handleDemoLogin">演示登录（余额9999元）</button>
      <button class="wechat-login-btn" @click="handleLogin">微信登录</button>
    </view>

    <view v-else class="user-section">
      <view class="user-info">
        <view class="avatar">酒</view>
        <view class="user-detail">
          <text class="nickname">{{ userStore.userInfo?.nickname || '微信用户' }}</text>
          <view class="status-row">
            <u-tag text="已实名" v-if="userStore.ageVerified" type="success" size="mini" />
          </view>
        </view>
      </view>
      <view class="stats-row">
        <view class="stat-item" @click="navigateToBalance">
          <text class="stat-value">¥{{ userStore.userInfo?.balance || 0 }}</text>
          <text class="stat-label">余额</text>
        </view>
        <view class="stat-item" @click="navigateToPoints">
          <text class="stat-value">{{ userStore.userInfo?.points || 0 }}</text>
          <text class="stat-label">积分</text>
        </view>
      </view>
    </view>

    <view class="menu-section">
      <view class="section-title">帮助与客服</view>
      <view class="menu-list">
        <button class="menu-item" open-type="contact">
          <view class="menu-left">
            <u-icon name="kefu" size="40" color="#07C160" />
            <text>在线客服</text>
          </view>
          <u-icon name="arrow-right" color="#CCCCCC" />
        </button>
        <view class="menu-item" @click="navigateToHelp">
          <view class="menu-left">
            <u-icon name="question-circle" size="40" />
            <text>帮助中心</text>
          </view>
          <u-icon name="arrow-right" color="#CCCCCC" />
        </view>
      </view>
    </view>

    <view class="menu-section" v-if="userStore.isLoggedIn">
      <view class="section-title">我的订单</view>
      <view class="menu-list">
        <view class="menu-item" @click="navigateToOrders('pending')">
          <view class="menu-left">
            <u-icon name="clock" size="40" />
            <text>待付款</text>
          </view>
          <u-icon name="arrow-right" color="#CCCCCC" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { demoLogin } from '@/services/auth'

const userStore = useUserStore()
const demoLoading = ref(false)

function handleLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

async function handleDemoLogin() {
  demoLoading.value = true
  try {
    const result = await demoLogin()
    if (result) {
      uni.showToast({ title: '演示登录成功', icon: 'success' })
    } else {
      uni.showToast({ title: '请确认后端已启动', icon: 'none' })
    }
  } finally {
    demoLoading.value = false
  }
}

function navigateToBalance() {
  uni.navigateTo({ url: '/pages/order/list/index' })
}

function navigateToPoints() {
  uni.navigateTo({ url: '/pages/points/index' })
}

function navigateToHelp() {
  uni.navigateTo({ url: '/pages/help/index' })
}

function navigateToOrders(status: string) {
  uni.navigateTo({ url: `/pages/order/list/index?status=${status}` })
}
</script>

<style scoped lang="scss">
.profile-page {
  min-height: 100vh;
  background: #F7F7F7;
}

.guest-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx;
  background: #FFFFFF;
}

.guest-avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  margin-bottom: 24rpx;
  background: linear-gradient(135deg, #2f1909, #a4622b);
  color: #fff6de;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 72rpx;
  font-weight: 800;
}

.guest-text {
  font-size: 32rpx;
  color: #999999;
  margin-bottom: 32rpx;
}

.demo-login-btn {
  width: 420rpx;
  height: 84rpx;
  line-height: 84rpx;
  border-radius: 999rpx;
  color: #fff;
  background: linear-gradient(135deg, #8B5A2B, #C97931);
  font-size: 30rpx;
  font-weight: 700;
  border: none;
}

.demo-login-btn::after,
.wechat-login-btn::after {
  border: none;
}

.wechat-login-btn {
  width: 420rpx;
  height: 76rpx;
  line-height: 76rpx;
  margin-top: 20rpx;
  border-radius: 999rpx;
  color: #6b4a28;
  background: #fff6e8;
  font-size: 28rpx;
}

.user-section {
  background: #FFFFFF;
  padding: 32rpx;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  margin-right: 24rpx;
  background: linear-gradient(135deg, #2f1909, #a4622b);
  color: #fff6de;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 52rpx;
  font-weight: 800;
}

.user-detail {
  flex: 1;
}

.nickname {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.status-row {
  margin-top: 12rpx;
}

.stats-row {
  display: flex;
  border-top: 1rpx solid #F7F7F7;
  padding-top: 24rpx;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 40rpx;
  font-weight: 600;
  color: #333333;
}

.stat-label {
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
}

.menu-section {
  background: #FFFFFF;
  margin-top: 16rpx;
}

.section-title {
  font-size: 28rpx;
  color: #999999;
  padding: 24rpx;
  padding-bottom: 0;
}

.menu-list {
  padding: 0 24rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #F7F7F7;
  background: transparent;
  border: none;
  margin: 0;
  line-height: normal;
}

.menu-item::after {
  border: none;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

button[open-type="contact"] {
  width: 100%;
}
</style>
