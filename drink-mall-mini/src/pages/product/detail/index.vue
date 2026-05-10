<template>
  <view class="product-detail">
    <swiper class="swiper" indicator-dots autoplay circular>
      <swiper-item v-for="(img, idx) in productImages" :key="idx">
        <view class="swiper-visual">
          <view class="hero-bottle"></view>
          <text class="hero-label">{{ product.name || '演示商品' }}</text>
        </view>
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
      <rich-text :nodes="product.description"></rich-text>
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
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

const product = ref<any>({})
const productId = ref(0)

const productImages = computed(() => {
  if (!product.value.images) return ['demo']
  try {
    const images = JSON.parse(product.value.images)
    return Array.isArray(images) && images.length > 0 ? images : ['demo']
  } catch {
    const images = String(product.value.images).split(',').map((img: string) => img.trim()).filter(Boolean)
    return images.length > 0 ? images : ['demo']
  }
})

const loadProduct = async () => {
  const res = await http.get<any>(`/public/products/${productId.value}`, {}, { requireAuth: false })
  if (res.code === 200) {
    product.value = res.data || {}
  }
}

const handleAddCart = async () => {
  const userStore = await import('@/store/user').then(m => m.useUserStore())
  if (!userStore().isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return
  }
  await http.post('/cart', { productId: productId.value, quantity: 1 })
  uni.showToast({ title: '已加入购物车', icon: 'success' })
}

const handleBuyNow = async () => {
  await handleAddCart()
  uni.navigateTo({ url: '/pages/checkout/index' })
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
.swiper-visual { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; background: radial-gradient(circle at 65% 20%, #ffe6a5 0, transparent 30%), linear-gradient(145deg, #2a1608, #9b5927 56%, #e3b76d); color: #fff8e8; }
.hero-bottle { width: 108rpx; height: 260rpx; border-radius: 48rpx 48rpx 18rpx 18rpx; background: rgba(255,255,255,0.18); box-shadow: inset 0 0 0 6rpx rgba(255,255,255,0.26); }
.hero-bottle::before { content: ''; display: block; width: 46rpx; height: 72rpx; border-radius: 18rpx 18rpx 0 0; background: rgba(255,255,255,0.18); margin: -62rpx auto 0; }
.hero-label { margin-top: 44rpx; font-size: 34rpx; font-weight: 800; max-width: 620rpx; text-align: center; }
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
