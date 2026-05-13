<template>
  <view class="profile-page">
    <view class="profile-hero">
      <view class="status-spacer"></view>
      <view class="settings-row">
        <view></view>
        <uni-icons type="gear" size="24" color="#ffffff" />
      </view>

      <view class="user-card">
        <view class="avatar-wrap">
          <image v-if="userStore.userInfo?.avatarUrl" class="avatar-img" :src="userStore.userInfo.avatarUrl" mode="aspectFill" />
          <text v-else>{{ userStore.isLoggedIn ? displayName.slice(0, 1) : '酒' }}</text>
        </view>
        <view class="user-meta">
          <text class="nickname">{{ userStore.isLoggedIn ? displayName : '未登录用户' }}</text>
          <text class="phone">{{ userStore.isLoggedIn ? maskedPhone : '登录后查看会员权益' }}</text>
          <text class="member-pill">{{ userStore.isLoggedIn ? memberTitle : '游客模式 · 浏览中' }}</text>
        </view>
      </view>
    </view>

    <view class="summary-card">
      <view class="summary-item">
        <text class="summary-value gold">¥ {{ formatMoney(withdrawable) }}</text>
        <text class="summary-label">可提现余额</text>
      </view>
      <view class="summary-divider"></view>
      <view class="summary-item">
        <text class="summary-value">¥ {{ formatMoney(teamPerformance) }}</text>
        <text class="summary-label">团队业绩</text>
      </view>
      <view class="summary-divider"></view>
      <view class="summary-item">
        <text class="summary-value olive">{{ points }}</text>
        <text class="summary-label">积分</text>
      </view>
    </view>

    <view class="section orders-section">
      <view class="section-header">
        <text>我的订单</text>
        <text class="section-more" @click="navigateToOrders('')">全部订单 ></text>
      </view>
      <view class="order-grid">
        <view class="order-item" v-for="item in orderItems" :key="item.label" @click="navigateToOrders(item.status)">
          <uni-icons :type="item.icon" size="31" color="#6f6f6f" />
          <text>{{ item.label }}</text>
        </view>
      </view>
    </view>

    <view class="section assets-section">
      <view class="section-header">
        <text>我的资产</text>
        <text class="section-more" @click="navigateToWithdraw">提现 ></text>
      </view>
      <view class="asset-grid">
        <view class="asset-card warm" @click="navigateToWallet">
          <text class="asset-value">¥{{ formatMoney(withdrawable) }}</text>
          <text class="asset-label">可提现余额</text>
        </view>
        <view class="asset-card gray">
          <text class="asset-value">¥{{ formatMoney(frozenAmount) }}</text>
          <text class="asset-label">冻结金额</text>
        </view>
        <view class="asset-card blue">
          <text class="asset-value">¥{{ formatMoney(dfBalance) }}</text>
          <text class="asset-label">DF余额</text>
        </view>
        <view class="asset-card pink" @click="navigateToPoints">
          <text class="asset-value">{{ points }}</text>
          <text class="asset-label">积分</text>
        </view>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item" @click="goDistribution">
        <view class="menu-left">
          <view class="menu-icon">
            <uni-icons type="staff" size="24" color="#d39200" />
          </view>
          <text>团队管理</text>
        </view>
        <uni-icons type="right" size="18" color="#c7c7c7" />
      </view>
      <view class="menu-item">
        <view class="menu-left">
          <view class="menu-icon">
            <uni-icons type="redo" size="24" color="#d39200" />
          </view>
          <text>分享邀请</text>
        </view>
        <uni-icons type="right" size="18" color="#c7c7c7" />
      </view>
      <view v-if="!userStore.isLoggedIn" class="login-actions">
        <button class="login-btn" @click="handleLogin">微信登录</button>
        <button class="demo-btn" :loading="demoLoading" @click="handleDemoLogin">演示登录</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { demoLogin } from '@/services/auth'
import { http } from '@/utils/request'

interface MemberCenter {
  profile: {
    nickname: string
    avatarUrl: string
    maskedPhone: string
    memberTitle: string
  }
  summary: {
    withdrawableBalance: number | string
    teamPerformance: number | string
    points: number
  }
  assets: {
    frozenBalance: number | string
    dfBalance: number | string
  }
}

const userStore = useUserStore()
const demoLoading = ref(false)
const memberCenter = ref<MemberCenter | null>(null)

const displayName = computed(() => memberCenter.value?.profile.nickname || userStore.userInfo?.nickname || '李明远')
const maskedPhone = computed(() => memberCenter.value?.profile.maskedPhone || '138****8888')
const memberTitle = computed(() => memberCenter.value?.profile.memberTitle || '县级联营商 · 生态合伙人')
const withdrawable = computed(() => Number(memberCenter.value?.summary.withdrawableBalance ?? userStore.userInfo?.balance ?? 24860))
const points = computed(() => Number(memberCenter.value?.summary.points ?? userStore.userInfo?.points ?? 1280))
const teamPerformance = computed(() => Number(memberCenter.value?.summary.teamPerformance ?? 58400))
const frozenAmount = computed(() => Number(memberCenter.value?.assets.frozenBalance ?? 3200))
const dfBalance = computed(() => Number(memberCenter.value?.assets.dfBalance ?? 1500))

const orderItems = [
  { label: '待付款', icon: 'wallet', status: 'pending' },
  { label: '待发货', icon: 'cube', status: 'paid' },
  { label: '待收货', icon: 'car', status: 'shipped' },
  { label: '已完成', icon: 'checkbox', status: 'completed' },
  { label: '售后', icon: 'headphones', status: 'aftersale' }
]

onShow(() => {
  if (userStore.isLoggedIn) {
    loadMemberCenter()
  }
})

async function loadMemberCenter() {
  try {
    const res = await http.get<MemberCenter>('/user/member-center', {}, { showError: false })
    if (res.code === 200 && res.data) {
      memberCenter.value = res.data
    }
  } catch (error) {
    console.error('Failed to load member center:', error)
  }
}

function handleLogin() {
  uni.navigateTo({ url: '/pages/login/index' })
}

async function handleDemoLogin() {
  demoLoading.value = true
  try {
    const result = await demoLogin()
    if (result) {
      uni.showToast({ title: '演示登录成功', icon: 'success' })
      loadMemberCenter()
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

function navigateToWithdraw() {
  uni.navigateTo({ url: '/pages/wallet/withdraw/index' })
}

function navigateToPoints() {
  uni.navigateTo({ url: '/pages/points/index' })
}

function navigateToOrders(status: string) {
  uni.navigateTo({ url: `/pages/order/list/index?status=${status}` })
}

function goDistribution() {
  uni.switchTab({ url: '/pages/distribution/level/index' })
}

function formatMoney(value: number) {
  return Number(value || 0).toLocaleString('zh-CN')
}
</script>

<style scoped lang="scss">
.profile-page {
  min-height: 100vh;
  padding-bottom: 28rpx;
  background: #f4f0e8;
  color: #1f160f;
}

.profile-hero {
  height: 346rpx;
  padding: 0 30rpx;
  background: #2b0f00;
  box-sizing: border-box;
}

.status-spacer {
  height: 92rpx;
}

.settings-row {
  height: 54rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-card {
  margin-top: 18rpx;
  display: flex;
  align-items: center;
  gap: 22rpx;
}

.avatar-wrap {
  width: 122rpx;
  height: 122rpx;
  border: 4rpx solid #f5bc25;
  border-radius: 50%;
  background: linear-gradient(135deg, #f4d8a4, #c68a32);
  color: #2b0f00;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-size: 44rpx;
  font-weight: 900;
}

.avatar-img {
  width: 100%;
  height: 100%;
}

.user-meta {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.nickname {
  color: #ffffff;
  font-size: 35rpx;
  font-weight: 900;
}

.phone {
  margin-top: 10rpx;
  color: rgba(255, 255, 255, 0.66);
  font-size: 25rpx;
}

.member-pill {
  align-self: flex-start;
  margin-top: 12rpx;
  padding: 9rpx 18rpx;
  border-radius: 999rpx;
  background: #d4930e;
  color: #fff8d8;
  font-size: 22rpx;
  font-weight: 800;
}

.summary-card {
  position: relative;
  z-index: 1;
  height: 142rpx;
  margin-top: -24rpx;
  border-radius: 24rpx 24rpx 0 0;
  background: #ffffff;
  display: grid;
  grid-template-columns: 1fr 2rpx 1fr 2rpx 1fr;
  align-items: center;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}

.summary-value {
  color: #19120b;
  font-size: 30rpx;
  font-weight: 900;
}

.summary-value.gold {
  color: #d39200;
}

.summary-value.olive {
  color: #987124;
}

.summary-label {
  color: #9a9085;
  font-size: 22rpx;
}

.summary-divider {
  width: 2rpx;
  height: 72rpx;
  background: #eee9df;
}

.section {
  margin-top: 20rpx;
  padding: 30rpx;
  background: #ffffff;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #1f160f;
  font-size: 31rpx;
  font-weight: 900;
}

.section-more {
  color: #d39200;
  font-size: 23rpx;
  font-weight: 700;
}

.order-grid {
  margin-top: 32rpx;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
}

.order-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  color: #716a62;
  font-size: 23rpx;
}

.asset-grid {
  margin-top: 26rpx;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx 20rpx;
}

.asset-card {
  height: 136rpx;
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 9rpx;
}

.asset-card.warm { background: #fff0c8; }
.asset-card.gray { background: #f2f2f2; }
.asset-card.blue { background: #dfeaff; }
.asset-card.pink { background: #ffe1f0; }

.asset-value {
  color: #d39200;
  font-size: 30rpx;
  font-weight: 900;
}

.gray .asset-value { color: #6f6f6f; }
.blue .asset-value { color: #175bd2; }
.pink .asset-value { color: #d91d82; }

.asset-label {
  color: #8d8175;
  font-size: 22rpx;
}

.menu-list {
  margin-top: 20rpx;
  background: #ffffff;
}

.menu-item {
  height: 100rpx;
  padding: 0 30rpx;
  border-bottom: 1rpx solid #eee9df;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 18rpx;
  color: #1f160f;
  font-size: 29rpx;
  font-weight: 800;
}

.menu-icon {
  width: 54rpx;
  height: 54rpx;
  border-radius: 50%;
  background: #fff1ce;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-actions {
  padding: 30rpx;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}

.login-btn,
.demo-btn {
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 999rpx;
  font-size: 28rpx;
  font-weight: 800;
}

.login-btn {
  background: #10b85a;
  color: #ffffff;
}

.demo-btn {
  background: #fff4d6;
  color: #d39200;
}

.login-btn::after,
.demo-btn::after {
  border: none;
}
</style>
