<template>
  <view class="detail-page">
    <view class="custom-nav">
      <view class="status-spacer"></view>
      <view class="nav-row">
        <view class="nav-icon" @click="goBack">
          <uni-icons type="left" size="24" color="#1f160f" />
        </view>
        <text class="nav-title">商品详情</text>
        <view class="nav-icon">
          <uni-icons type="redo" size="24" color="#1f160f" />
        </view>
      </view>
    </view>

    <swiper class="hero-swiper" indicator-dots autoplay circular indicator-active-color="#d39200" indicator-color="#dddddd">
      <swiper-item v-for="(img, idx) in productImages" :key="idx">
        <view class="hero-visual">
          <image v-if="img !== 'demo'" class="hero-image" :src="img" mode="aspectFill" />
          <view v-else class="hero-placeholder">
            <view class="detail-bottle"></view>
            <view class="detail-glass"></view>
            <view class="cloth-shape"></view>
          </view>
        </view>
      </swiper-item>
    </swiper>

    <view class="product-panel">
      <view class="price-meta">
        <view class="price-left">
          <text class="currency">¥</text>
          <text class="price">{{ formatPrice(product.price || fallbackProduct.price) }}</text>
          <text class="joint-price">联营商价</text>
        </view>
        <view class="stats">
          <text>已售 {{ product.sales || fallbackProduct.sales }} 件</text>
          <text>库存 {{ product.stock || 500 }}</text>
          <text class="tag-text">酱香型</text>
        </view>
      </view>

      <text class="product-name">{{ product.name || fallbackProduct.name }}</text>
      <view class="tags">
        <text>主产品区</text>
        <text>酱香型</text>
        <text>首购享20%佣金</text>
      </view>
    </view>

    <view class="option-card">
      <text class="option-title">规格选择</text>
      <view class="sku-row">
        <view
          v-for="sku in skus"
          :key="sku"
          class="sku-chip"
          :class="{ active: sku === selectedSku }"
          @click="selectedSku = sku"
        >
          {{ sku }}
        </view>
      </view>
    </view>

    <view class="quantity-card">
      <text>购买数量</text>
      <view class="qty-stepper">
        <button @click="decrease">－</button>
        <text>{{ quantity }}</text>
        <button @click="increase">＋</button>
      </view>
    </view>

    <view class="detail-card">
      <text class="detail-title">商品说明</text>
      <text class="detail-desc">{{ product.description || '甄选品质酒水，适合商务宴请、礼赠与家庭聚会。暂无后台详情时展示占位说明。' }}</text>
    </view>

    <view class="bottom-bar">
      <button class="cart-shortcut" @click="goCart">
        <uni-icons type="cart" size="25" color="#d39200" />
        <text>购物车</text>
      </button>
      <button class="buy-btn" @click="handleBuyNow">立即购买</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { http } from '@/utils/request'
import { useUserStore } from '@/store/user'

const fallbackProduct = {
  id: 0,
  name: '茅台飞天53度酱香型白酒 500ml',
  price: 2980,
  sales: 3280,
  stock: 500
}

const product = ref<any>({})
const productId = ref(0)
const quantity = ref(1)
const selectedSku = ref('500ml 单瓶')
const skus = ['500ml 单瓶', '500ml 6瓶装']

const productImages = computed(() => {
  const source = product.value.images || product.value.mainImage
  if (!source) return ['demo']
  try {
    const images = JSON.parse(source)
    return Array.isArray(images) && images.length > 0 ? images : ['demo']
  } catch {
    const images = String(source).split(',').map((img: string) => img.trim()).filter(Boolean)
    return images.length > 0 ? images : ['demo']
  }
})

const loadProduct = async () => {
  if (!productId.value) {
    product.value = fallbackProduct
    return
  }
  const res = await http.get<any>(`/public/products/${productId.value}`, {}, { requireAuth: false, showError: false })
  if (res.code === 200) {
    product.value = res.data || fallbackProduct
  }
}

const handleAddCart = async () => {
  const userStore = useUserStore()
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return false
  }
  if (!productId.value) {
    uni.showToast({ title: '示例商品，请先选择真实商品', icon: 'none' })
    return false
  }
  await http.post('/cart', { productId: productId.value, quantity: quantity.value })
  uni.showToast({ title: '已加入购物车', icon: 'success' })
  return true
}

const handleBuyNow = async () => {
  const ok = await handleAddCart()
  if (ok) {
    uni.navigateTo({ url: '/pages/checkout/index' })
  }
}

const goCart = () => {
  uni.navigateTo({ url: '/pages/cart/index' })
}

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.switchTab({ url: '/pages/index/index' })
  }
}

function decrease() {
  quantity.value = Math.max(1, quantity.value - 1)
}

function increase() {
  quantity.value += 1
}

function formatPrice(price: number) {
  return Number(price || 0).toLocaleString('zh-CN')
}

onLoad((options: any) => {
  productId.value = Number(options.id || 0)
  loadProduct()
})
</script>

<style scoped lang="scss">
.detail-page {
  min-height: 100vh;
  padding-bottom: 154rpx;
  background: #f4f0e8;
  color: #1f160f;
}

.custom-nav {
  background: #ffffff;
}

.status-spacer {
  height: 54rpx;
}

.nav-row {
  height: 88rpx;
  padding: 0 30rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-title {
  font-size: 32rpx;
  font-weight: 900;
}

.nav-icon {
  width: 54rpx;
  height: 54rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-swiper {
  height: 620rpx;
  background: #21130f;
}

.hero-visual,
.hero-image,
.hero-placeholder {
  width: 100%;
  height: 100%;
}

.hero-placeholder {
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(circle at 62% 52%, rgba(110, 73, 119, 0.45), transparent 35%),
    linear-gradient(135deg, #321014 0%, #69505e 50%, #120d13 100%);
}

.detail-bottle {
  position: absolute;
  left: 72rpx;
  top: 64rpx;
  width: 124rpx;
  height: 372rpx;
  border-radius: 62rpx 62rpx 20rpx 20rpx;
  background: rgba(16, 6, 10, 0.82);
  box-shadow: inset 0 0 0 10rpx rgba(255, 255, 255, 0.12), 0 22rpx 60rpx rgba(0, 0, 0, 0.35);
}

.detail-bottle::before {
  content: '';
  display: block;
  width: 54rpx;
  height: 88rpx;
  margin: -72rpx auto 0;
  border-radius: 20rpx 20rpx 0 0;
  background: rgba(240, 211, 166, 0.8);
}

.detail-glass {
  position: absolute;
  left: 260rpx;
  bottom: 68rpx;
  width: 88rpx;
  height: 158rpx;
  border: 6rpx solid rgba(245, 225, 198, 0.58);
  border-top: 0;
  border-radius: 12rpx 12rpx 38rpx 38rpx;
}

.cloth-shape {
  position: absolute;
  right: -34rpx;
  bottom: 0;
  width: 460rpx;
  height: 310rpx;
  border-radius: 120rpx 0 0 0;
  background: rgba(14, 14, 22, 0.62);
}

.product-panel,
.option-card,
.quantity-card,
.detail-card {
  background: #ffffff;
}

.product-panel {
  padding: 42rpx 30rpx 28rpx;
}

.price-meta {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.price-left {
  display: flex;
  align-items: baseline;
  gap: 10rpx;
}

.currency,
.price {
  color: #d18400;
  font-weight: 900;
}

.currency {
  font-size: 24rpx;
}

.price {
  font-size: 52rpx;
}

.joint-price {
  margin-left: 14rpx;
  color: #aaa39a;
  font-size: 25rpx;
}

.stats {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6rpx;
  color: #aba295;
  font-size: 24rpx;
}

.tag-text {
  color: #d39200;
}

.product-name {
  display: block;
  margin-top: 18rpx;
  color: #1f160f;
  font-size: 34rpx;
  font-weight: 900;
}

.tags {
  margin-top: 20rpx;
  display: flex;
  gap: 12rpx;
  flex-wrap: wrap;
}

.tags text {
  padding: 8rpx 14rpx;
  border-radius: 6rpx;
  color: #d18400;
  background: #fff0c4;
  font-size: 22rpx;
}

.tags text:nth-child(3) {
  color: #1d62c5;
  background: #e5f0ff;
}

.option-card {
  margin-top: 2rpx;
  padding: 26rpx 30rpx 28rpx;
}

.option-title {
  display: block;
  color: #1f160f;
  font-size: 28rpx;
  font-weight: 800;
}

.sku-row {
  margin-top: 18rpx;
  display: flex;
  gap: 18rpx;
}

.sku-chip {
  padding: 17rpx 26rpx;
  border: 2rpx solid #e0c89e;
  border-radius: 14rpx;
  color: #7f6a51;
  font-size: 26rpx;
}

.sku-chip.active {
  border-color: #d39200;
  color: #f7c22b;
  background: #351400;
  box-shadow: inset 0 0 0 2rpx #d39200;
}

.quantity-card {
  height: 104rpx;
  margin-top: 2rpx;
  padding: 0 30rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #1f160f;
  font-size: 28rpx;
  font-weight: 800;
}

.qty-stepper {
  width: 216rpx;
  height: 64rpx;
  border: 2rpx solid #e8cf9f;
  border-radius: 16rpx;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  overflow: hidden;
}

.qty-stepper button,
.qty-stepper text {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64rpx;
  line-height: 64rpx;
  padding: 0;
  border: 0;
  border-radius: 0;
  background: #fffaf2;
  color: #d39200;
  font-size: 30rpx;
}

.qty-stepper text {
  color: #1f160f;
  background: #ffffff;
  font-weight: 900;
}

.qty-stepper button::after {
  border: none;
}

.detail-card {
  margin-top: 20rpx;
  padding: 28rpx 30rpx 36rpx;
}

.detail-title {
  display: block;
  color: #1f160f;
  font-size: 30rpx;
  font-weight: 900;
}

.detail-desc {
  display: block;
  margin-top: 18rpx;
  color: #75695c;
  font-size: 26rpx;
  line-height: 1.7;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 126rpx;
  padding: 18rpx 30rpx;
  background: #ffffff;
  display: grid;
  grid-template-columns: 110rpx 1fr;
  gap: 24rpx;
  box-sizing: border-box;
}

.cart-shortcut {
  height: 90rpx;
  padding: 0;
  border: 2rpx solid #f0c54d;
  border-radius: 50%;
  background: #ffffff;
  color: #d39200;
  font-size: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.buy-btn {
  height: 90rpx;
  line-height: 90rpx;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #ffd43d, #c97c00);
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 900;
}

.cart-shortcut::after,
.buy-btn::after {
  border: none;
}
</style>
