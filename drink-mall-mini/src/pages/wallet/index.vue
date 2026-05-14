<template>
  <view class="page">
    <view class="hero">
      <text class="eyebrow">资产独立记账</text>
      <text class="hero-title">我的资产</text>
      <text class="hero-copy">余额、DF、酒豆、积分、期权分别展示，不做组合支付或自动互通。</text>
    </view>
    <view class="grid">
      <view v-for="a in assets" :key="a.key" class="asset" @click="history(a.key)">
        <text class="asset-name">{{ a.name }}</text>
        <text class="price">{{ a.value }}</text>
        <text class="muted">{{ a.tip }}</text>
      </view>
    </view>
    <button class="primary" @click="goWithdraw">提现申请</button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { assetApi } from '@/services/asset'
import { useUserStore } from '@/store/user'

const data = ref<any>({})
const assets = computed(() => {
  const u = useUserStore().userInfo || {}
  const d = data.value
  const arr = [
    { key: 'balance', name: '余额', value: d.balance ?? u.balance ?? 0, tip: '可提现余额' },
    { key: 'frozenBalance', name: '冻结余额', value: d.frozenBalance ?? u.frozenBalance ?? 0, tip: '售后或审核冻结' },
    { key: 'df', name: 'DF', value: d.df ?? u.df ?? 0, tip: '以后端配置流转' },
    { key: 'wineBean', name: '酒豆', value: d.wineBean ?? u.wineBean ?? 0, tip: '购买指定商品' },
    { key: 'points', name: '积分', value: d.points ?? u.points ?? 0, tip: '纯积分兑换礼包' }
  ]
  if (d.showOption || u.showOption) arr.push({ key: 'option', name: '期权', value: d.optionValue ?? u.optionValue ?? 0, tip: '按后端配置展示' })
  return arr
})

function history(t: string) { uni.navigateTo({ url: '/pages/wallet/history/index?type=' + t }) }
function goWithdraw() { uni.navigateTo({ url: '/pages/wallet/withdraw/index' }) }
onShow(async () => { try { data.value = (await assetApi.overview()).data || {} } catch {} })
</script>

<style scoped lang="scss">
.page{min-height:100vh;background:#f6f1e8;color:#24170c;padding:24rpx 20rpx 40rpx}
.hero{background:#2b1207;color:#fff;border-radius:18rpx;padding:34rpx 30rpx;margin-bottom:20rpx}
.eyebrow{display:block;color:#f0bf55;font-size:23rpx}
.hero-title{display:block;font-size:42rpx;font-weight:900;margin-top:10rpx}
.hero-copy{display:block;color:#e8d6b8;font-size:24rpx;line-height:1.5;margin-top:14rpx}
.grid{display:grid;grid-template-columns:1fr 1fr;gap:18rpx}
.asset{background:#fff;padding:26rpx;border-radius:16rpx;min-height:178rpx;box-shadow:0 8rpx 24rpx rgba(58,32,12,.06)}
.asset-name{display:block;font-weight:800;font-size:28rpx}
.price{display:block;color:#b97700;font-weight:900;font-size:36rpx;margin-top:14rpx;word-break:break-all}
.muted{display:block;color:#8d8175;font-size:23rpx;line-height:1.35;margin-top:10rpx}
button{margin-top:28rpx;height:84rpx;line-height:84rpx;width:100%;border:0;border-radius:999rpx}
.primary{background:#b97700;color:#fff}
button:after{border:0}
</style>
