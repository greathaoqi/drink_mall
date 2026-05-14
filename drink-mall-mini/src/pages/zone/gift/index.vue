<template>
  <view class="page">
    <view class="hero">
      <text class="eyebrow">纯积分兑换</text>
      <text class="title">礼包专区</text>
      <text class="muted">仅支持积分一种方式，不展示现金、余额或酒豆组合支付。</text>
    </view>
    <view class="card" v-for="p in products" :key="p.id">
      <text class="item-title">{{ p.name }}</text>
      <text class="price">{{ p.pointsPrice || p.price }} 积分</text>
      <button class="primary" @click="redeem(p)">积分兑换</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { productApi } from '@/services/product'
import { listOf } from '@/utils/format'
import { useUserStore } from '@/store/user'
import { requireLogin } from '@/utils/auth'

const products = ref<any[]>([])
async function load() { products.value = listOf((await productApi.list({ zoneType: 'gift' })).data) }
async function redeem(p: any) {
  if (!requireLogin()) return
  const need = Number(p.pointsPrice || p.price || 0)
  const has = Number(useUserStore().userInfo?.points || 0)
  if (has < need) {
    uni.showToast({ title: '积分不足，不能提交', icon: 'none' })
    return
  }
  await productApi.redeemGift(p.id, 1)
  uni.showToast({ title: '兑换成功', icon: 'success' })
}
onShow(load)
</script>

<style scoped lang="scss">
.page{min-height:100vh;background:#f6f1e8;color:#24170c;padding:20rpx}
.hero{background:#2b1207;color:#fff;border-radius:18rpx;padding:32rpx 28rpx;margin-bottom:20rpx}
.eyebrow{display:block;color:#f0bf55;font-size:23rpx}
.title{display:block;font-size:42rpx;font-weight:900;margin-top:10rpx}
.muted{display:block;color:#e8d6b8;font-size:24rpx;line-height:1.5;margin-top:12rpx}
.card{background:#fff;margin-bottom:20rpx;padding:28rpx;border-radius:16rpx;box-shadow:0 8rpx 24rpx rgba(58,32,12,.06)}
.item-title{display:block;font-size:32rpx;font-weight:900;line-height:1.35}
.price{display:block;color:#b97700;font-weight:900;margin-top:16rpx;font-size:36rpx}
.card button{margin-top:24rpx;height:78rpx;line-height:78rpx;width:100%;border:0;border-radius:999rpx}
.primary{background:#b97700;color:#fff}
button:after{border:0}
</style>
