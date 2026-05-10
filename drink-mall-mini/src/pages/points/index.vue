<template>
  <view class="points-page">
    <view class="balance-card">
      <view class="balance">积分余额</view>
      <view class="amount">{{ pointsBalance }}</view>
    </view>

    <view class="section-title">礼包专区</view>
    <view class="product-grid">
      <view v-for="product in products" :key="product.id" class="product-card" @click="showDetail(product)">
        <view class="product-img"><view class="gift-icon">礼</view></view>
        <view class="product-info">
          <text class="product-name">{{ product.name }}</text>
          <view class="product-bottom">
            <text class="product-points">{{ product.pointsPrice || Number(product.price || 0).toFixed(0) }}积分</text>
            <button class="exchange-btn" @click.stop="handleExchange(product)">兑换</button>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

const pointsBalance = ref(0)
const products = ref<any[]>([])

const loadPoints = async () => {
  try {
    const res = await http.get('/points/balance')
    pointsBalance.value = res.data || 0
  } catch (e) {}
}

const loadProducts = async () => {
  const res = await http.get('/public/products', { zoneType: 'gift' })
  products.value = res.data?.records || []
}

const showDetail = (product: any) => {
  uni.navigateTo({ url: `/pages/product/detail/index?id=${product.id}` })
}

const handleExchange = async (product: any) => {
  const pointsPrice = product.pointsPrice || Number(product.price || 0)
  if (pointsBalance.value < pointsPrice) {
    uni.showToast({ title: '积分不足', icon: 'none' })
    return
  }
  uni.showModal({
    title: '确认兑换',
    content: `使用${pointsPrice}积分兑换${product.name}?`,
    success: async (res) => {
      if (res.confirm) {
        await http.post(`/points/redeem/${product.id}`)
        uni.showToast({ title: '兑换成功', icon: 'success' })
        loadPoints()
      }
    }
  })
}

onShow(() => {
  loadPoints()
  loadProducts()
})
</script>

<style scoped>
.points-page { background: #f5f5f5; min-height: 100vh; }
.balance-card { background: linear-gradient(135deg, #ff6b6b, #e93b3d); margin: 20rpx; padding: 40rpx; border-radius: 12rpx; color: #fff; }
.balance { font-size: 28rpx; opacity: 0.9; }
.amount { font-size: 48rpx; font-weight: bold; margin-top: 10rpx; }
.section-title { font-size: 32rpx; font-weight: bold; padding: 20rpx; }
.product-grid { display: flex; flex-wrap: wrap; padding: 0 10rpx; }
.product-card { width: calc(50% - 20rpx); margin: 10rpx; background: #fff; border-radius: 12rpx; overflow: hidden; }
.product-img { width: 100%; height: 340rpx; background: radial-gradient(circle at 70% 20%, #fff1b8 0, transparent 30%), linear-gradient(135deg, #fbefe0, #ef8b35); display: flex; align-items: center; justify-content: center; }
.gift-icon { width: 110rpx; height: 110rpx; border-radius: 28rpx; background: rgba(255,255,255,0.75); color: #c55b16; display: flex; align-items: center; justify-content: center; font-size: 54rpx; font-weight: 800; }
.product-info { padding: 20rpx; }
.product-name { font-size: 28rpx; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 10rpx; }
.product-points { color: #ff6b00; font-size: 28rpx; font-weight: bold; }
.exchange-btn { font-size: 24rpx; background: #ff6b00; color: #fff; border-radius: 20rpx; padding: 8rpx 20rpx; border: none; }
</style>
