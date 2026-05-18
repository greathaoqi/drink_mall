<template>
  <view class="dm-page profile-page">
    <view class="profile-hero">
      <view class="top-actions">
        <text @click="go('/pages/settings/index')">设置</text>
      </view>
      <view class="user-row">
        <image v-if="store.userInfo?.avatarUrl" class="avatar" :src="store.userInfo.avatarUrl" mode="aspectFill" />
        <view v-else class="avatar text-avatar">{{ initial }}</view>
        <view class="user-main">
          <text class="name">{{ profileName }}</text>
          <text class="phone">{{ maskedPhone }}</text>
          <view class="badges">
            <text>{{ realNameLabel(store.userInfo?.realNameStatus) }}</text>
            <text>{{ store.userInfo?.levelName || '普通会员' }}</text>
          </view>
        </view>
        <button v-if="!store.isLoggedIn" class="login-btn" @click="go('/pages/login/index', false)">登录</button>
      </view>
    </view>

    <view class="stats dm-card">
      <view v-for="item in topStats" :key="item.label" class="stat">
        <text class="stat-value">{{ item.value }}</text>
        <text class="stat-label">{{ item.label }}</text>
      </view>
    </view>

    <view class="content">
      <view class="dm-card order-card">
        <view class="section-head">
          <text class="section-title">我的订单</text>
          <text class="more" @click="go('/pages/order/list/index')">全部订单</text>
        </view>
        <view class="order-grid">
          <view v-for="item in orderMenus" :key="item.text" class="entry" @click="go(item.url)">
            <view class="entry-icon">{{ item.icon }}</view>
            <text>{{ item.text }}</text>
          </view>
        </view>
      </view>

      <view class="dm-card asset-card">
        <view class="section-head">
          <text class="section-title">我的资产</text>
          <text class="more" @click="go('/pages/wallet/index')">提现</text>
        </view>
        <view class="asset-grid">
          <view v-for="item in assetCards" :key="item.key" class="asset" @click="go(item.url)">
            <text class="asset-value">{{ item.value }}</text>
            <text class="asset-name">{{ item.name }}</text>
          </view>
        </view>
      </view>

      <view class="dm-card menu-card">
        <view v-for="item in menus" :key="item.text" class="row-link" @click="go(item.url)">
          <view class="row-icon" :class="item.tone">{{ item.icon }}</view>
          <text class="row-text">{{ item.text }}</text>
          <text class="row-extra">{{ item.extra || '' }}</text>
          <text class="chevron">›</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { refreshUserInfo } from '@/services/user'
import { realNameLabel } from '@/utils/auth'

const store = useUserStore()

const profileName = computed(() => store.isLoggedIn ? (store.userInfo?.nickname || '用户') : '未登录')
const initial = computed(() => store.isLoggedIn ? (store.userInfo?.nickname || '用').slice(0, 1) : '未')
const maskedPhone = computed(() => store.isLoggedIn ? (store.userInfo?.mobile || store.userInfo?.phone || '手机号未绑定') : '登录后查看会员权益')

const topStats = computed(() => [
  { label: '可提现余额', value: displayMoney(store.userInfo?.balance) },
  { label: '团队业绩', value: displayMoney(store.userInfo?.teamPerformance) },
  { label: '积分', value: displayValue(store.userInfo?.points) }
])

const assetCards = computed(() => [
  { key: 'balance', name: '可提现余额', value: displayMoney(store.userInfo?.balance), url: '/pages/wallet/history/index?type=balance' },
  { key: 'frozen', name: '冻结余额', value: displayMoney(store.userInfo?.frozenBalance), url: '/pages/wallet/history/index?type=frozenBalance' },
  { key: 'df', name: 'DF', value: displayMoney(store.userInfo?.df), url: '/pages/wallet/history/index?type=df' },
  { key: 'wineBean', name: '酒豆', value: displayValue(store.userInfo?.wineBean), url: '/pages/wallet/history/index?type=wineBean' },
  { key: 'points', name: '积分', value: displayValue(store.userInfo?.points), url: '/pages/wallet/history/index?type=points' },
  { key: 'option', name: '期权', value: displayValue(store.userInfo?.optionValue), url: '/pages/wallet/history/index?type=option' }
])

const orderMenus = [
  { text: '待付款', icon: '付', url: '/pages/order/list/index?status=pending' },
  { text: '待发货', icon: '发', url: '/pages/order/list/index?status=paid' },
  { text: '待收货', icon: '收', url: '/pages/order/list/index?status=shipped' },
  { text: '已完成', icon: '完', url: '/pages/order/list/index?status=done' },
  { text: '售后', icon: '售', url: '/pages/order/list/index?status=aftersale' }
]

const menus = [
  { text: '我的团队', icon: '团', url: '/pages/team/index', tone: 'blue' },
  { text: '分享邀请', icon: '邀', url: '/pages/team/index', tone: 'gold', extra: store.userInfo?.inviteCode || '' },
  { text: '实名认证', icon: '实', url: '/pages/auth/realname/index', tone: 'green' },
  { text: '地址管理', icon: '址', url: '/pages/address/list/index', tone: 'blue' },
  { text: '内容中心', icon: '文', url: '/pages/content/list/index', tone: 'pink' },
  { text: '公告', icon: '告', url: '/pages/announcement/list/index', tone: 'gold' },
  { text: '等级说明', icon: '级', url: '/pages/distribution/level/index', tone: 'green' },
  { text: '帮助中心', icon: '客', url: '/pages/help/index', tone: 'blue' },
  { text: '设置', icon: '设', url: '/pages/settings/index', tone: 'gold' }
]

function displayValue(value?: number | string) {
  if (!store.isLoggedIn) return '--'
  return value ?? 0
}

function displayMoney(value?: number | string) {
  if (!store.isLoggedIn) return '--'
  return `¥${Number(value || 0).toLocaleString('zh-CN')}`
}

function go(url: string, needLogin = true) {
  if (needLogin && !store.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  uni.navigateTo({ url })
}

onShow(() => {
  if (store.isLoggedIn) refreshUserInfo().catch(() => {})
})
</script>

<style scoped lang="scss">
.profile-page {
  padding-bottom: 40rpx;
}

.profile-hero {
  min-height: 260rpx;
  padding: 64rpx var(--dm-page-x) 62rpx;
  color: var(--dm-text-on-brown);
  background:
    radial-gradient(ellipse 80% 60% at 30% 12%, rgba(228, 165, 22, 0.22), transparent 60%),
    var(--dm-grad-brown);
}

.top-actions {
  text-align: right;
  color: #FFD685;
  font-size: 26rpx;
}

.user-row,
.section-head,
.row-link {
  display: flex;
  align-items: center;
}

.user-row {
  margin-top: 22rpx;
  gap: 24rpx;
}

.avatar {
  width: 128rpx;
  height: 128rpx;
  border-radius: 50%;
  background: var(--dm-grad-gold);
  flex: 0 0 128rpx;
}

.text-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3A1A00;
  font-size: 42rpx;
  font-weight: 900;
}

.user-main {
  flex: 1;
  min-width: 0;
}

.name {
  display: block;
  color: #FFE0A0;
  font-size: 40rpx;
  font-weight: 800;
}

.phone {
  display: block;
  margin-top: 6rpx;
  color: rgba(246, 231, 194, 0.68);
  font-size: 24rpx;
}

.badges {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 12rpx;
}

.badges text {
  height: 40rpx;
  line-height: 40rpx;
  padding: 0 14rpx;
  border-radius: 8rpx;
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-size: 21rpx;
  font-weight: 700;
}

.login-btn {
  width: 120rpx;
  height: 60rpx;
  line-height: 60rpx;
  border: 0;
  border-radius: var(--dm-radius-pill);
  background: var(--dm-grad-gold);
  color: #3A1A00;
  font-weight: 800;
}

.stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin: -28rpx var(--dm-page-x) 0;
  padding: 26rpx 0;
  position: relative;
  z-index: 2;
}

.stat {
  text-align: center;
  border-left: 1rpx solid var(--dm-line-soft);
}

.stat:first-child {
  border-left: 0;
}

.stat-value {
  display: block;
  color: var(--dm-gold-500);
  font-size: 31rpx;
  font-weight: 800;
}

.stat-label {
  display: block;
  margin-top: 6rpx;
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.content {
  padding: 22rpx var(--dm-page-x) 0;
}

.order-card,
.asset-card,
.menu-card {
  padding: 26rpx;
  margin-bottom: 22rpx;
}

.section-head {
  justify-content: space-between;
  margin-bottom: 22rpx;
}

.section-title {
  font-size: 31rpx;
  font-weight: 800;
}

.more {
  color: var(--dm-gold-500);
  font-size: 23rpx;
}

.order-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8rpx;
}

.entry {
  text-align: center;
  color: var(--dm-text-2);
  font-size: 22rpx;
}

.entry-icon,
.row-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
}

.entry-icon {
  width: 58rpx;
  height: 58rpx;
  margin: 0 auto 8rpx;
  border-radius: 50%;
  color: var(--dm-text-1);
  background: var(--dm-cream-100);
}

.asset-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12rpx;
}

.asset {
  min-height: 112rpx;
  padding: 18rpx 12rpx;
  border-radius: var(--dm-radius-md);
  background: var(--dm-cream-100);
}

.asset-value,
.asset-name {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.asset-value {
  color: var(--dm-text-1);
  font-size: 27rpx;
  font-weight: 800;
}

.asset-name {
  margin-top: 8rpx;
  color: var(--dm-text-3);
  font-size: 21rpx;
}

.menu-card {
  padding: 0 26rpx;
}

.row-link {
  min-height: 92rpx;
  border-bottom: 1rpx solid var(--dm-line-soft);
}

.row-link:last-child {
  border-bottom: 0;
}

.row-icon {
  width: 54rpx;
  height: 54rpx;
  margin-right: 18rpx;
  border-radius: 14rpx;
  color: #fff;
  font-size: 22rpx;
}

.row-icon.gold { background: var(--dm-gold-500); }
.row-icon.blue { background: var(--dm-blue-500); }
.row-icon.green { background: var(--dm-green-500); }
.row-icon.pink { background: var(--dm-pink-500); }

.row-text {
  flex: 1;
  color: var(--dm-text-1);
  font-size: 27rpx;
  font-weight: 700;
}

.row-extra {
  max-width: 180rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.chevron {
  margin-left: 8rpx;
  color: #C9BFA9;
  font-size: 38rpx;
}

button:after {
  border: 0;
}
</style>
