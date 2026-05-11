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
          <view class="nickname-row" @click="showEditNickname = true">
            <text class="nickname">{{ userStore.userInfo?.nickname || '微信用户' }}</text>
            <uni-icons type="compose" size="16" color="#aaa" style="margin-left: 8rpx;" />
          </view>
          <view class="status-row">
            <u-tag text="已实名" v-if="userStore.ageVerified" type="success" size="mini" />
          </view>
        </view>
      </view>
      <view class="stats-row">
        <view class="stat-item" @click="navigateToWallet">
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

  <view v-if="showEditNickname" class="nickname-overlay" @click.stop="showEditNickname = false">
    <view class="nickname-dialog" @click.stop>
      <text class="dialog-title">修改昵称</text>
      <input v-model="newNickname" class="dialog-input" placeholder="请输入新昵称" maxlength="30" />
      <view class="dialog-btns">
        <button class="dialog-btn cancel" @click="showEditNickname = false">取消</button>
        <button class="dialog-btn confirm" @click="saveNickname">确定</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { demoLogin } from '@/services/auth'
import { http } from '@/utils/request'

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

function navigateToWallet() {
  uni.navigateTo({ url: '/pages/wallet/index' })
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

const showEditNickname = ref(false)
const newNickname = ref('')

async function saveNickname() {
  if (!newNickname.value.trim()) {
    uni.showToast({ title: '昵称不能为空', icon: 'none' }); return
  }
  const res = await http.put('/user/profile', { nickname: newNickname.value.trim() })
  if (res.code === 200) {
    if (userStore.userInfo) userStore.userInfo.nickname = newNickname.value.trim()
    showEditNickname.value = false
    uni.showToast({ title: '修改成功', icon: 'success' })
  } else {
    uni.showToast({ title: res.message || '修改失败', icon: 'none' })
  }
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

.nickname-row { display: flex; align-items: center; }
.nickname-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 999; }
.nickname-dialog { background: #fff; border-radius: 16rpx; padding: 40rpx; width: 560rpx; }
.dialog-title { font-size: 32rpx; font-weight: 600; display: block; margin-bottom: 30rpx; text-align: center; }
.dialog-input { border: 1rpx solid #eee; border-radius: 8rpx; padding: 16rpx; font-size: 28rpx; width: 100%; margin-bottom: 30rpx; box-sizing: border-box; }
.dialog-btns { display: flex; gap: 20rpx; }
.dialog-btn { flex: 1; border-radius: 40rpx; font-size: 28rpx; padding: 16rpx; }
.cancel { background: #f5f5f5; color: #666; border: none; }
.confirm { background: #8a4f22; color: #fff; border: none; }
</style>
