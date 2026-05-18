<template>
  <view class="dm-page settings-page">
    <view class="dm-card profile-edit">
      <view class="avatar">{{ initial }}</view>
      <view class="edit-main">
        <text class="title">个人资料</text>
        <text class="sub">{{ store.userInfo?.nickname || '完善昵称、头像与联系方式' }}</text>
      </view>
      <button class="save-btn" @click="saveProfile">保存</button>
    </view>

    <view class="dm-card form-list">
      <view class="row">
        <text class="label">昵称</text>
        <input v-model.trim="profile.nickname" placeholder="请输入昵称" />
      </view>
      <view class="row">
        <text class="label">手机号</text>
        <text class="value">{{ store.userInfo?.mobile || store.userInfo?.phone || '未绑定' }}</text>
      </view>
      <view class="row" @click="go('/pages/auth/realname/index')">
        <text class="label">实名认证</text>
        <text class="value">{{ realNameLabel(store.userInfo?.realNameStatus) }}</text>
      </view>
      <view class="row">
        <text class="label">邀请人</text>
        <text class="value">{{ store.userInfo?.inviterName || '暂无' }}</text>
      </view>
    </view>

    <view class="dm-card form-list">
      <view class="row" @click="about">
        <text class="label">关于我们</text>
        <text class="value">酒水分销商城一期</text>
      </view>
      <view class="row" @click="contact">
        <text class="label">联系客服</text>
        <text class="value">小程序客服入口</text>
      </view>
      <view class="row" @click="clear">
        <text class="label">清除缓存</text>
        <text class="value">立即清理</text>
      </view>
    </view>

    <button class="logout" @click="logoutNow">退出登录</button>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { logout } from '@/services/auth'
import { refreshUserInfo } from '@/services/user'
import { useUserStore } from '@/store/user'
import { realNameLabel } from '@/utils/auth'

const store = useUserStore()
const profile = ref({ nickname: '' })
const initial = ref('用')

onShow(() => {
  profile.value.nickname = store.userInfo?.nickname || ''
  initial.value = (store.userInfo?.nickname || '用').slice(0, 1)
})

function saveProfile() {
  uni.showToast({ title: '资料保存需后端接口支持', icon: 'none' })
}

function go(url: string) {
  uni.navigateTo({ url })
}

function about() {
  uni.showModal({ title: '关于我们', content: '酒水分销商城一期' })
}

function contact() {
  uni.showModal({ title: '联系我们', content: '请通过小程序客服入口联系' })
}

function clear() {
  uni.clearStorageSync()
  uni.showToast({ title: '已清除', icon: 'success' })
  refreshUserInfo().catch(() => {})
}

async function logoutNow() {
  await logout()
  uni.switchTab({ url: '/pages/index/index' })
}
</script>

<style scoped lang="scss">
.settings-page {
  padding: 24rpx var(--dm-page-x) 44rpx;
}

.profile-edit,
.row {
  display: flex;
  align-items: center;
}

.profile-edit {
  gap: 20rpx;
  padding: 28rpx;
}

.avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-size: 34rpx;
  font-weight: 900;
}

.edit-main {
  min-width: 0;
  flex: 1;
}

.title,
.sub {
  display: block;
}

.title {
  font-size: 32rpx;
  font-weight: 800;
}

.sub,
.value {
  color: var(--dm-text-3);
  font-size: 24rpx;
}

.save-btn {
  width: 116rpx;
  height: 58rpx;
  line-height: 58rpx;
  border: 0;
  border-radius: var(--dm-radius-pill);
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-weight: 800;
  font-size: 24rpx;
}

.form-list {
  margin-top: 22rpx;
  padding: 0 26rpx;
}

.row {
  min-height: 96rpx;
  border-bottom: 1rpx solid var(--dm-line-soft);
}

.row:last-child {
  border-bottom: 0;
}

.label {
  width: 180rpx;
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 700;
}

.row input {
  flex: 1;
  min-width: 0;
  text-align: right;
  color: var(--dm-text-1);
}

.value {
  flex: 1;
  text-align: right;
}

.logout {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  margin-top: 28rpx;
  border: 0;
  border-radius: var(--dm-radius-pill);
  color: var(--dm-red-500);
  background: #fff;
  font-weight: 800;
}

button:after {
  border: 0;
}
</style>
