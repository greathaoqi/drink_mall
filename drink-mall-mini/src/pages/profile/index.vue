<template>
  <view class="profile-page">
    <view v-if="!userStore.isLoggedIn" class="guest-section">
      <image class="guest-avatar" src="/static/icons/default-avatar.png" />
      <text class="guest-text">未登录</text>
      <u-button type="primary" shape="circle" color="#07C160" @click="showLoginPrompt = true">
        立即登录
      </u-button>
    </view>

    <view v-else class="user-section">
      <view class="user-info">
        <image class="avatar" :src="userStore.userInfo?.avatarUrl || '/static/icons/default-avatar.png'" />
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

    <LoginPrompt :show="showLoginPrompt" message="登录后可查看个人中心和订单" @close="showLoginPrompt = false" @login="handleLogin" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import LoginPrompt from '@/components/LoginPrompt/LoginPrompt.vue'

const userStore = useUserStore()
const showLoginPrompt = ref(false)

function handleLogin() {
  showLoginPrompt.value = false
  uni.navigateTo({ url: '/pages/login/index' })
}

function navigateToBalance() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function navigateToPoints() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function navigateToHelp() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function navigateToOrders(status: string) {
  uni.showToast({ title: '功能开发中', icon: 'none' })
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
}

.guest-text {
  font-size: 32rpx;
  color: #999999;
  margin-bottom: 32rpx;
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