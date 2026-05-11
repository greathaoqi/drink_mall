<template>
  <view class="wallet-page">
    <view class="balance-card">
      <view class="balance-row">
        <view class="balance-item">
          <text class="amount">¥{{ userInfo.balance || '0.00' }}</text>
          <text class="label">可用余额</text>
        </view>
        <view class="divider-v" />
        <view class="balance-item">
          <text class="amount frozen">¥{{ userInfo.frozenBalance || '0.00' }}</text>
          <text class="label">冻结余额</text>
        </view>
        <view class="divider-v" />
        <view class="balance-item">
          <text class="amount points">{{ userInfo.points || 0 }}</text>
          <text class="label">积分</text>
        </view>
      </view>
      <button class="withdraw-btn" @click="goWithdraw">申请提现</button>
    </view>

    <view class="menu-section">
      <view class="menu-item" @click="goHistory('balance')">
        <text class="menu-label">余额流水</text>
        <uni-icons type="right" size="16" color="#ccc" />
      </view>
      <view class="menu-item" @click="goHistory('points')">
        <text class="menu-label">积分记录</text>
        <uni-icons type="right" size="16" color="#ccc" />
      </view>
      <view class="menu-item" @click="goHistory('withdrawal')">
        <text class="menu-label">提现记录</text>
        <uni-icons type="right" size="16" color="#ccc" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

const userInfo = ref<{ balance?: string; frozenBalance?: string; points?: number }>({})

onShow(async () => {
  const res = await http.get('/user/info')
  if (res.code === 200) userInfo.value = res.data || {}
})

const goWithdraw = () => {
  uni.navigateTo({ url: '/pages/wallet/withdraw/index' })
}

const goHistory = (tab: string) => {
  uni.navigateTo({ url: `/pages/wallet/history/index?tab=${tab}` })
}
</script>

<style scoped lang="scss">
.wallet-page { background: #f5f5f5; min-height: 100vh; }
.balance-card {
  background: linear-gradient(135deg, #2b1608, #8a4f22);
  padding: 40rpx 30rpx 30rpx;
  color: #fff;
}
.balance-row { display: flex; justify-content: space-around; margin-bottom: 40rpx; }
.balance-item { display: flex; flex-direction: column; align-items: center; }
.amount { font-size: 40rpx; font-weight: 700; }
.frozen { opacity: 0.7; }
.points { color: #ffd700; }
.label { font-size: 24rpx; opacity: 0.8; margin-top: 8rpx; }
.divider-v { width: 1rpx; background: rgba(255,255,255,0.3); margin: 10rpx 0; }
.withdraw-btn {
  width: 100%;
  background: rgba(255,255,255,0.2);
  color: #fff;
  border: 1rpx solid rgba(255,255,255,0.5);
  border-radius: 40rpx;
  font-size: 28rpx;
  padding: 16rpx 0;
}
.menu-section {
  background: #fff;
  margin: 20rpx;
  border-radius: 12rpx;
  overflow: hidden;
}
.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.menu-item:last-child { border-bottom: none; }
.menu-label { font-size: 30rpx; color: #333; }
</style>
