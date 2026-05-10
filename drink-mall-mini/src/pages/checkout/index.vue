<template>
  <view class="checkout-page">
    <view class="address-card" @click="selectAddress">
      <template v-if="address">
        <view class="receiver">{{ address.name }} {{ address.phone }}</view>
        <view class="address">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</view>
      </template>
      <template v-else>
        <view class="no-address">请选择收货地址</view>
      </template>
      <uni-icons type="right" />
    </view>

    <view class="goods-card">
      <view v-for="item in cartItems" :key="item.cartId" class="goods-item">
        <view class="goods-img"><view class="mini-bottle"></view></view>
        <view class="goods-info">
          <text class="goods-name">{{ item.productName }}</text>
          <view class="goods-bottom">
            <text class="goods-price">¥{{ item.price }}</text>
            <text class="goods-quantity">x{{ item.quantity }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="summary-card">
      <view class="summary-item">
        <text>商品金额</text>
        <text>¥{{ totalAmount }}</text>
      </view>
      <view class="summary-item">
        <text>运费</text>
        <text>包邮</text>
      </view>
      <view class="summary-item total">
        <text>实付金额</text>
        <text class="price">¥{{ totalAmount }}</text>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="total-info">
        <text>合计: </text>
        <text class="price">¥{{ totalAmount }}</text>
      </view>
      <button class="submit-btn" @click="handleSubmit">提交订单</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const cartItems = ref<any[]>([])
const address = ref<any>(null)
const selectedIds = ref<number[]>([])

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
})

const loadCartItems = async () => {
  const res = await request.get('/cart')
  const allItems = res.data?.items || []
  cartItems.value = selectedIds.value.length > 0
    ? allItems.filter((item: any) => selectedIds.value.includes(item.cartId))
    : allItems.filter((item: any) => item.selected)
}

const loadDefaultAddress = async () => {
  try {
    const res = await request.get('/address/default')
    address.value = res.data
  } catch (e) {
    address.value = null
  }
}

const selectAddress = () => {
  uni.navigateTo({ url: '/pages/address/list/index?select=1' })
}

const handleSubmit = async () => {
  if (!address.value) {
    uni.showToast({ title: '请选择收货地址', icon: 'none' })
    return
  }
  const items = cartItems.value.map(item => ({ productId: item.productId, quantity: item.quantity }))
  const res = await request.post('/order', { addressId: address.value.id, items })
  uni.redirectTo({ url: `/pages/order/detail/index?id=${res.data.id}` })
}

onLoad((options: any) => {
  if (options.ids) {
    selectedIds.value = options.ids.split(',').map(Number)
  }
  uni.$on('addressSelected', (addr: any) => {
    address.value = addr
  })
})

onShow(() => {
  loadCartItems()
  loadDefaultAddress()
})
</script>

<style scoped>
.checkout-page { background: #f5f5f5; min-height: 100vh; padding-bottom: 120rpx; }
.address-card { background: #fff; margin: 20rpx; padding: 30rpx; border-radius: 12rpx; display: flex; align-items: center; }
.receiver { font-size: 30rpx; font-weight: bold; }
.address { font-size: 26rpx; color: #666; margin-top: 10rpx; }
.no-address { color: #999; }
.goods-card { background: #fff; margin: 20rpx; padding: 20rpx; border-radius: 12rpx; }
.goods-item { display: flex; padding: 20rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.goods-img { width: 160rpx; height: 160rpx; border-radius: 12rpx; background: linear-gradient(135deg, #f7dfb4, #9a5c27); display: flex; align-items: center; justify-content: center; }
.mini-bottle { width: 34rpx; height: 82rpx; border-radius: 16rpx 16rpx 8rpx 8rpx; background: #4d2a13; }
.mini-bottle::before { content: ''; display: block; width: 16rpx; height: 24rpx; border-radius: 8rpx 8rpx 0 0; background: #4d2a13; margin: -20rpx auto 0; }
.goods-info { flex: 1; margin-left: 20rpx; }
.goods-name { font-size: 28rpx; }
.goods-bottom { display: flex; justify-content: space-between; margin-top: 60rpx; }
.goods-price { color: #e93b3d; }
.goods-quantity { color: #999; }
.summary-card { background: #fff; margin: 20rpx; padding: 20rpx; border-radius: 12rpx; }
.summary-item { display: flex; justify-content: space-between; padding: 15rpx 0; font-size: 28rpx; }
.summary-item.total { border-top: 1rpx solid #f5f5f5; margin-top: 10rpx; padding-top: 20rpx; }
.price { color: #e93b3d; font-weight: bold; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; padding: 20rpx; display: flex; justify-content: space-between; align-items: center; }
.total-info { font-size: 28rpx; }
.submit-btn { background: linear-gradient(to right, #ff6034, #e93b3d); color: #fff; font-size: 30rpx; padding: 20rpx 60rpx; border-radius: 40rpx; border: none; }
</style>
