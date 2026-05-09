<template>
  <view class="product-detail">
    <swiper class="swiper" indicator-dots autoplay circular>
      <swiper-item v-for="(img, idx) in product.images?.split(',')" :key="idx">
        <image :src="img" mode="aspectFill" class="swiper-img" />
      </swiper-item>
    </swiper>

    <view class="info-card">
      <view class="price-row">
        <text class="price">¥{{ product.price }}</text>
        <text v-if="product.pointsPrice" class="points">或{{ product.pointsPrice }}积分</text>
      </view>
      <view class="name">{{ product.name }}</view>
      <view class="sales">已售{{ product.sales }}件</view>
    </view>

    <view class="detail-card">
      <view class="title">商品详情</view>
      <rich-text :nodes="product.detail"></rich-text>
    </view>

    <view class="bottom-bar">
      <view class="icons">
        <button class="icon-btn" open-type="contact">
          <uni-icons type="chat" size="22" />
          <text>客服</text>
        </button>
        <button class="icon-btn" @click="goCart">
          <uni-icons type="cart" size="22" />
          <text>购物车</text>
        </button>
      </view>
      <view class="actions">
        <button class="add-cart" @click="handleAddCart">加入购物车</button>
        <button class="buy-now" @click="handleBuyNow">立即购买</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request'

const product = ref<any>({})
const productId = ref(0)

const loadProduct = async () => {
  const res = await request.get(`/api/v1/product/${productId.value}`)
  product.value = res.data || {}
}

const handleAddCart = async () => {
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  await request.post('/api/v1/cart', { productId: productId.value, quantity: 1 })
  uni.showToast({ title: '已加入购物车', icon: 'success' })
}

const handleBuyNow = () => {
  handleAddCart()
  uni.navigateTo({ url: '/pages/checkout/index?ids=' + productId.value })
}

const goCart = () => {
  uni.switchTab({ url: '/pages/cart/index' })
}

onLoad((options: any) => {
  productId.value = options.id
  loadProduct()
})
</script>

<style scoped>
.product-detail { padding-bottom: 100rpx; }
.swiper { height: 750rpx; }
.swiper-img { width: 100%; height: 100%; }
.info-card { background: #fff; padding: 30rpx; margin-bottom: 20rpx; }
.price-row { display: flex; align-items: baseline; gap: 20rpx; }
.price { font-size: 48rpx; color: #e93b3d; font-weight: bold; }
.points { font-size: 28rpx; color: #ff6b00; }
.name { font-size: 32rpx; margin-top: 20rpx; }
.sales { font-size: 24rpx; color: #999; margin-top: 10rpx; }
.detail-card { background: #fff; padding: 30rpx; }
.detail-card .title { font-size: 28rpx; font-weight: bold; padding-bottom: 20rpx; border-bottom: 1rpx solid #eee; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; height: 100rpx; background: #fff; display: flex; align-items: center; padding: 0 20rpx; box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.1); }
.icons { display: flex; gap: 30rpx; }
.icon-btn { display: flex; flex-direction: column; align-items: center; font-size: 20rpx; background: transparent; border: none; padding: 0; line-height: 1.4; }
.actions { flex: 1; display: flex; gap: 20rpx; justify-content: flex-end; }
.add-cart, .buy-now { font-size: 28rpx; padding: 16rpx 40rpx; border-radius: 40rpx; }
.add-cart { background: #fff; border: 1rpx solid #e93b3d; color: #e93b3d; }
.buy-now { background: linear-gradient(to right, #ff6034, #e93b3d); color: #fff; border: none; }
</style>