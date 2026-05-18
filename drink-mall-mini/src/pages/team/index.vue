<template>
  <view class="dm-page team-page">
    <view class="team-hero">
      <view class="hero-top">
        <view>
          <text class="eyebrow">合规展示</text>
          <text class="title">我的团队</text>
        </view>
        <button class="invite-btn" @click="copyInviteCode">邀请码</button>
      </view>
      <text class="hero-copy">仅展示我的邀请人、直推人员、间推人员和团队总人数。</text>
      <view class="stats">
        <view class="stat">
          <text class="num">{{ overview.totalCount || 0 }}</text>
          <text>团队总人数</text>
        </view>
        <view class="stat">
          <text class="num">{{ directs.length }}</text>
          <text>直推人员</text>
        </view>
        <view class="stat">
          <text class="num">{{ indirects.length }}</text>
          <text>间推人员</text>
        </view>
      </view>
    </view>

    <view class="dm-card inviter-card">
      <text class="section-title">我的邀请人</text>
      <view class="person-row">
        <view class="avatar">{{ firstChar(overview.inviterName || '无') }}</view>
        <view class="person-main">
          <text class="person-name">{{ overview.inviterName || '暂无邀请人' }}</text>
          <text class="person-sub">{{ overview.inviterPhone || '后台种子账号可无邀请人' }}</text>
        </view>
      </view>
    </view>

    <view class="dm-card list-card">
      <view class="section-head">
        <text class="section-title">直推人员</text>
        <text class="count">{{ directs.length }} 人</text>
      </view>
      <view v-for="m in directs" :key="m.userId" class="member">
        <view class="avatar small">{{ firstChar(m.nickname || m.userId) }}</view>
        <view class="person-main">
          <text class="person-name">{{ m.nickname || m.userId }}</text>
          <text class="person-sub">{{ m.mobile || m.phone || '直推成员' }}</text>
        </view>
        <button class="gift-btn" @click="openDfGift(m)">赠送 DF</button>
      </view>
      <view v-if="!directs.length" class="empty">暂无直推人员</view>
    </view>

    <view class="dm-card list-card">
      <view class="section-head">
        <text class="section-title">间推人员</text>
        <text class="count">{{ indirects.length }} 人</text>
      </view>
      <view v-for="m in indirects" :key="m.userId" class="member">
        <view class="avatar small muted-avatar">{{ firstChar(m.nickname || m.userId) }}</view>
        <view class="person-main">
          <text class="person-name">{{ m.nickname || m.userId }}</text>
          <text class="person-sub">{{ m.mobile || m.phone || '间推成员' }}</text>
        </view>
      </view>
      <view v-if="!indirects.length" class="empty">暂无间推人员</view>
    </view>

    <view class="note">后台用于奖励计算和审计的推荐链路不在小程序端展开。</view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { teamApi } from '@/services/team'
import { assetApi } from '@/services/asset'
import { listOf } from '@/utils/format'
import { requirePageLogin } from '@/utils/auth'
import { useUserStore } from '@/store/user'

const overview = ref<any>({})
const directs = ref<any[]>([])
const indirects = ref<any[]>([])

function firstChar(value: string) {
  return String(value || '酒').slice(0, 1)
}

async function load() {
  if (!requirePageLogin()) return
  overview.value = (await teamApi.overview()).data || {}
  directs.value = listOf((await teamApi.directs()).data)
  indirects.value = listOf((await teamApi.indirects()).data)
}

function copyInviteCode() {
  const inviteCode = useUserStore().userInfo?.inviteCode || overview.value.inviteCode
  if (!inviteCode) {
    uni.showToast({ title: '暂无邀请码', icon: 'none' })
    return
  }
  uni.setClipboardData({ data: inviteCode })
}

function openDfGift(m: any) {
  if (!requirePageLogin()) return
  uni.showModal({
    title: '赠送 DF',
    editable: true,
    placeholderText: '请输入赠送金额',
    success: async (r) => {
      if (!r.confirm || !r.content) return
      await assetApi.transferDf({ toUserId: m.userId, amount: r.content })
      uni.showToast({ title: '赠送申请已提交', icon: 'success' })
    }
  })
}

onShow(load)
</script>

<style scoped lang="scss">
.team-page {
  padding: 24rpx var(--dm-page-x) 40rpx;
}

.team-hero {
  color: var(--dm-text-on-brown);
  padding: 30rpx 30rpx 28rpx;
  border-radius: 0 0 24rpx 24rpx;
  background:
    radial-gradient(ellipse 80% 60% at 30% 10%, rgba(228, 165, 22, 0.22), transparent 60%),
    var(--dm-grad-brown);
  box-shadow: var(--dm-shadow-pop);
}

.hero-top,
.section-head,
.person-row,
.member {
  display: flex;
  align-items: center;
}

.hero-top {
  justify-content: space-between;
}

.eyebrow {
  display: block;
  color: rgba(246, 231, 194, 0.62);
  font-size: 22rpx;
  letter-spacing: 4rpx;
}

.title {
  display: block;
  margin-top: 8rpx;
  color: #FFE0A0;
  font-size: 42rpx;
  font-weight: 800;
}

.hero-copy {
  display: block;
  margin-top: 16rpx;
  color: rgba(246, 231, 194, 0.72);
  font-size: 24rpx;
}

.invite-btn,
.gift-btn {
  border: 0;
  border-radius: var(--dm-radius-pill);
  font-weight: 700;
}

.invite-btn {
  width: 136rpx;
  height: 58rpx;
  line-height: 58rpx;
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-size: 24rpx;
}

.stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12rpx;
  margin-top: 28rpx;
}

.stat {
  padding: 18rpx 8rpx;
  border: 1rpx solid rgba(228, 165, 22, 0.18);
  border-radius: var(--dm-radius-md);
  background: rgba(0, 0, 0, 0.18);
  text-align: center;
  color: rgba(246, 231, 194, 0.72);
  font-size: 21rpx;
}

.num {
  display: block;
  color: #FFE0A0;
  font-size: 34rpx;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.inviter-card,
.list-card {
  margin-top: 22rpx;
  padding: 26rpx;
}

.section-head {
  justify-content: space-between;
  margin-bottom: 10rpx;
}

.section-title {
  display: block;
  font-size: 30rpx;
  font-weight: 800;
}

.count,
.person-sub,
.empty,
.note {
  color: var(--dm-text-3);
  font-size: 23rpx;
}

.person-row,
.member {
  gap: 18rpx;
}

.member {
  min-height: 92rpx;
  border-bottom: 1rpx solid var(--dm-line-soft);
}

.member:last-child {
  border-bottom: 0;
}

.avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: var(--dm-grad-gold);
  color: #3A1A00;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 800;
  flex: 0 0 72rpx;
}

.small {
  width: 64rpx;
  height: 64rpx;
  flex-basis: 64rpx;
  font-size: 25rpx;
}

.muted-avatar {
  background: var(--dm-cream-200);
  color: var(--dm-text-2);
}

.person-main {
  min-width: 0;
  flex: 1;
}

.person-name,
.person-sub {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.person-name {
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 700;
}

.person-sub {
  margin-top: 4rpx;
}

.gift-btn {
  width: 154rpx;
  height: 58rpx;
  line-height: 58rpx;
  color: var(--dm-blue-500);
  background: #E8EFFD;
  font-size: 23rpx;
}

.empty {
  padding: 28rpx 0 8rpx;
}

.note {
  padding: 22rpx 8rpx 0;
  line-height: 1.6;
}

button:after {
  border: 0;
}
</style>
