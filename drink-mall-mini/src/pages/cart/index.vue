<template>
  <view class="cart-page">
    <view v-if="items.length === 0" class="empty">
      <text class="empty-title">购物车还是空的</text>
      <text class="empty-desc">去挑一瓶适合演示的好酒吧</text>
      <button class="shop-btn" @click="goProducts">去选购</button>
    </view>

    <view v-else class="cart-list">
      <view v-for="item in items" :key="item.cartId" class="cart-card">
        <checkbox :checked="item.selected" @click="toggleSelected(item)" />
        <view class="product-img"><view class="mini-bottle"></view></view>
        <view class="product-info">
          <text class="product-name">{{ item.productName }}</text>
          <text class="stock">库存 {{ item.stock }}</text>
          <view class="bottom-row">
            <text class="price">¥{{ item.price }}</text>
            <view class="qty">
              <button class="qty-btn" @click="changeQuantity(item, -1)">-</button>
              <text>{{ item.quantity }}</text>
              <button class="qty-btn" @click="changeQuantity(item, 1)">+</button>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view v-if="items.length > 0" class="settle-bar">
      <view>
        <text class="total-label">合计</text>
        <text class="total-price">¥{{ selectedPrice }}</text>
      </view>
      <button class="settle-btn" @click="checkout">去结算</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const items = ref<any[]>([])

const selectedPrice = computed(() => {
  return items.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
    .toFixed(2)
})

const loadCart = async () => {
  const res = await request.get('/cart')
  items.value = res.data?.items || []
}

const toggleSelected = async (item: any) => {
  await request.put(`/cart/${item.cartId}/selected?selected=${!item.selected}`)
  loadCart()
}

const changeQuantity = async (item: any, delta: number) => {
  const quantity = item.quantity + delta
  if (quantity < 1 || quantity > item.stock) return
  await request.put(`/cart/${item.cartId}/quantity?quantity=${quantity}`)
  loadCart()
}

const checkout = () => {
  const selected = items.value.filter(item => item.selected)
  if (selected.length === 0) {
    uni.showToast({ title: '请选择商品', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages/checkout/index?ids=${selected.map(item => item.cartId).join(',')}` })
}

const goProducts = () => {
  uni.navigateTo({ url: '/pages/product/list' })
}

onShow(() => loadCart())
</script>

<style scoped lang="scss">
.cart-page { min-height: 100vh; background: #f6f1e8; padding: 24rpx 24rpx 140rpx; }
.empty { min-height: 70vh; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #7a5a35; }
.empty-title { font-size: 38rpx; font-weight: 700; }
.empty-desc { margin-top: 16rpx; font-size: 26rpx; opacity: 0.75; }
.shop-btn { margin-top: 36rpx; background: #8B5A2B; color: #fff; border-radius: 999rpx; padding: 12rpx 60rpx; }
.cart-card { display: flex; align-items: center; gap: 20rpx; background: #fffaf2; border-radius: 24rpx; padding: 24rpx; margin-bottom: 20rpx; box-shadow: 0 12rpx 30rpx rgba(92, 55, 18, 0.08); }
.product-img { width: 156rpx; height: 156rpx; border-radius: 18rpx; background: linear-gradient(135deg, #f7dfb4, #9a5c27); display: flex; align-items: center; justify-content: center; }
.mini-bottle { width: 34rpx; height: 82rpx; border-radius: 16rpx 16rpx 8rpx 8rpx; background: #4d2a13; }
.mini-bottle::before { content: ''; display: block; width: 16rpx; height: 24rpx; border-radius: 8rpx 8rpx 0 0; background: #4d2a13; margin: -20rpx auto 0; }
.product-info { flex: 1; min-width: 0; }
.product-name { display: block; font-size: 28rpx; font-weight: 700; color: #2d2116; }
.stock { display: block; margin-top: 10rpx; font-size: 22rpx; color: #9b846a; }
.bottom-row { display: flex; align-items: center; justify-content: space-between; margin-top: 28rpx; }
.price { color: #b83220; font-size: 32rpx; font-weight: 800; }
.qty { display: flex; align-items: center; gap: 18rpx; }
.qty-btn { width: 48rpx; height: 48rpx; line-height: 42rpx; padding: 0; border-radius: 50%; background: #f1e5d4; color: #6d421e; }
.settle-bar { position: fixed; left: 0; right: 0; bottom: 0; background: #fffaf2; display: flex; align-items: center; justify-content: space-between; padding: 24rpx 32rpx; box-shadow: 0 -10rpx 30rpx rgba(92, 55, 18, 0.1); }
.total-label { color: #7a5a35; margin-right: 12rpx; }
.total-price { color: #b83220; font-size: 36rpx; font-weight: 800; }
.settle-btn { margin: 0; background: linear-gradient(135deg, #8B5A2B, #C97931); color: #fff; border-radius: 999rpx; padding: 14rpx 56rpx; }
</style>
