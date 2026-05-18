<template>
  <view class="dm-page points-page">
    <view class="points-hero">
      <text class="eyebrow">礼包兑换</text>
      <text class="title">{{ points }}</text>
      <text class="copy">积分与酒豆独立展示；兑换商品使用后台配置的可用支付方式。</text>
    </view>

    <view class="dm-card exchange-card">
      <view class="section-head">
        <text class="section-title">积分明细</text>
        <text class="more" @click="goHistory">查看记录</text>
      </view>
      <view class="exchange-row">
        <view>
          <text class="gift-title">积分礼包兑换</text>
          <text class="gift-sub">具体商品、兑换比例与库存以礼包专区配置为准。</text>
        </view>
        <button @click="goGift">去兑换</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const points = computed(() => Number(useUserStore().userInfo?.points || 0).toLocaleString('zh-CN'))

function goHistory() {
  uni.navigateTo({ url: '/pages/wallet/history/index?type=points' })
}

function goGift() {
  uni.navigateTo({ url: '/pages/zone/gift/index' })
}
</script>

<style scoped lang="scss">
.points-page {
  padding: 24rpx var(--dm-page-x) 44rpx;
}

.points-hero {
  padding: 34rpx 30rpx;
  border-radius: var(--dm-radius-lg);
  color: #fff;
  background: linear-gradient(135deg, #D1467A, #99294F);
}

.eyebrow,
.copy,
.gift-sub,
.more {
  display: block;
  font-size: 23rpx;
  opacity: 0.78;
}

.title {
  display: block;
  margin-top: 8rpx;
  font-size: 58rpx;
  font-weight: 900;
}

.copy {
  margin-top: 8rpx;
  line-height: 1.6;
}

.exchange-card {
  margin-top: 22rpx;
  padding: 28rpx;
}

.section-head,
.exchange-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.section-title,
.gift-title {
  display: block;
  color: var(--dm-text-1);
  font-size: 30rpx;
  font-weight: 800;
}

.more {
  color: var(--dm-gold-500);
}

.exchange-row {
  margin-top: 24rpx;
  padding: 24rpx;
  border-radius: var(--dm-radius-md);
  background: var(--dm-cream-100);
}

.gift-sub {
  margin-top: 8rpx;
  color: var(--dm-text-3);
  line-height: 1.5;
}

.exchange-row button {
  width: 144rpx;
  height: 62rpx;
  line-height: 62rpx;
  border: 0;
  border-radius: var(--dm-radius-pill);
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-weight: 800;
  flex: 0 0 144rpx;
}

button:after {
  border: 0;
}
</style>
