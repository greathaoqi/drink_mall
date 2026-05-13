<template>
  <view class="home-page">
    <view class="home-header">
      <view class="status-spacer"></view>
      <view class="brand-row">
        <view class="brand-left">
          <text class="brand-name">醇品汇</text>
          <text class="level-pill">县级联营商</text>
        </view>
        <view class="header-actions">
          <view class="header-icon" @click="goCart">
            <uni-icons type="cart" size="26" color="#f5bc25" />
          </view>
          <view class="header-icon">
            <uni-icons type="notification" size="25" color="#f5bc25" />
          </view>
        </view>
      </view>
      <view class="search-bar" @click="navigateToProducts">
        <uni-icons type="search" size="17" color="#a8947d" />
        <text>搜索酒水商品...</text>
      </view>
    </view>

    <swiper
      class="hero-swiper"
      :indicator-dots="false"
      :autoplay="true"
      :interval="3200"
      :duration="500"
    >
      <swiper-item v-for="banner in displayBanners" :key="banner.id">
        <view class="hero-card" @click="handleBannerClick(banner)">
          <image v-if="banner.imageUrl" class="hero-image" :src="banner.imageUrl" mode="aspectFill" />
          <view v-else class="hero-placeholder">
            <view class="wine-bottle wine-bottle-a"></view>
            <view class="wine-bottle wine-bottle-b"></view>
            <view class="wine-glass"></view>
          </view>
          <view class="hero-mask"></view>
          <view class="hero-copy">
            <text class="hero-kicker">{{ banner.kicker || '精选佳酿' }}</text>
            <text class="hero-title">{{ banner.title || '秋季新品上市' }}</text>
            <text class="hero-desc">{{ banner.description || '品质酒水与联营商福利专区' }}</text>
          </view>
        </view>
      </swiper-item>
    </swiper>

    <view class="notice-bar" @click="goAnnouncements">
      <text class="notice-label">公告</text>
      <text class="notice-text">{{ displayAnnouncements[0]?.title || '关于秋季新品上市及联营商福利活动通知...' }}</text>
    </view>

    <view class="channel-row">
      <view class="channel-item" v-for="item in channels" :key="item.title" @click="item.action">
        <view class="channel-icon" :class="item.className">
          <uni-icons :type="item.icon" size="27" :color="item.color" />
        </view>
        <text>{{ item.title }}</text>
      </view>
    </view>

    <view class="section-block">
      <view class="section-title-row">
        <view class="title-left">
          <view class="title-mark"></view>
          <text>主产品专区</text>
        </view>
        <view class="more-link" @click="navigateToProducts">更多 ></view>
      </view>

      <view class="product-grid">
        <view class="product-card" v-for="product in displayProducts" :key="product.id" @click="navigateToProductDetail(product)">
          <view class="product-cover">
            <image v-if="product.mainImage" class="product-img" :src="product.mainImage" mode="aspectFill" />
            <view v-else class="product-art" :class="product.id % 2 === 0 ? 'product-art-bottle' : 'product-art-box'">
              <view class="box-lid"></view>
              <view class="box-body"></view>
            </view>
            <view class="chat-float" v-if="product.id === displayProducts[1]?.id">
              <uni-icons type="chat" size="26" color="#ffffff" />
            </view>
          </view>
          <view class="product-info">
            <text class="product-name">{{ product.name }}</text>
            <view class="price-row">
              <text class="product-price">¥{{ formatPrice(product.price) }}</text>
              <view class="add-btn" @click.stop="quickAdd(product)">
                <uni-icons type="plusempty" size="18" color="#ffffff" />
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onReady, onShow } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

interface Banner {
  id: number
  title: string
  imageUrl?: string
  linkType?: string
  linkValue?: string
  kicker?: string
  description?: string
}
interface Category { id: number; name: string; iconUrl?: string }
interface Product { id: number; name: string; mainImage?: string; price: number; sales?: number; zoneType?: string }
interface Announcement { id: number; title: string; createdAt?: string }

const fallbackBanners: Banner[] = [
  { id: -1, title: '秋季新品上市', kicker: '新品首发', description: '好酒入库，会员专享' }
]
const fallbackCategories: Category[] = [
  { id: -1, name: '主产品区' },
  { id: -2, name: '招商专区' },
  { id: -3, name: '零售专区' },
  { id: -4, name: '礼包专区' }
]
const fallbackProducts: Product[] = [
  { id: -1, name: '茅台飞天53度', price: 2980, sales: 3280 },
  { id: -2, name: '五粮液普五', price: 1380, sales: 1580 }
]
const fallbackAnnouncements: Announcement[] = [
  { id: -1, title: '关于秋季新品上市及联营商福利活动通知...' }
]

const banners = ref<Banner[]>([])
const categories = ref<Category[]>([])
const products = ref<Product[]>([])
const announcements = ref<Announcement[]>([])
const pageReady = ref(false)
let loadingPublicContent = false

const displayBanners = computed(() => banners.value.length > 0 ? banners.value : fallbackBanners)
const displayCategories = computed(() => categories.value.length > 0 ? categories.value.slice(0, 4) : fallbackCategories)
const displayProducts = computed(() => (products.value.length > 0 ? products.value : fallbackProducts).slice(0, 2))
const displayAnnouncements = computed(() => announcements.value.length > 0 ? announcements.value : fallbackAnnouncements)

const channels = computed(() => [
  { title: displayCategories.value[0]?.name || '主产品区', icon: 'medal', color: '#d08a00', className: 'gold', action: () => navigateToCategory(displayCategories.value[0]) },
  { title: displayCategories.value[1]?.name || '招商专区', icon: 'briefcase', color: '#2368bb', className: 'blue', action: () => navigateToCategory(displayCategories.value[1]) },
  { title: displayCategories.value[2]?.name || '零售专区', icon: 'shop', color: '#069f48', className: 'green', action: () => navigateToCategory(displayCategories.value[2]) },
  { title: displayCategories.value[3]?.name || '礼包专区', icon: 'gift', color: '#d91d82', className: 'pink', action: () => navigateToCategory(displayCategories.value[3]) }
])

onShow(() => {
  if (pageReady.value) {
    loadPublicContent()
  }
})

onReady(() => {
  pageReady.value = true
  loadPublicContent()
})

async function loadPublicContent() {
  if (loadingPublicContent) return
  loadingPublicContent = true
  try {
    const [bannerRes, categoryRes, productRes, annRes] = await Promise.all([
      http.get<Banner[]>('/public/banners', {}, { requireAuth: false, showError: false }),
      http.get<Category[]>('/public/categories', {}, { requireAuth: false, showError: false }),
      http.get<{ records?: Product[] } | Product[]>('/public/products', { pageSize: 4 }, { requireAuth: false, showError: false }),
      http.get<Announcement[]>('/content/announcements', {}, { requireAuth: false, showError: false })
    ])

    if (bannerRes.code === 200) banners.value = Array.isArray(bannerRes.data) ? bannerRes.data : []
    if (categoryRes.code === 200) categories.value = Array.isArray(categoryRes.data) ? categoryRes.data : []
    if (productRes.code === 200) {
      const productData = Array.isArray(productRes.data) ? productRes.data : productRes.data?.records
      products.value = productData || []
    }
    if (annRes.code === 200) announcements.value = (Array.isArray(annRes.data) ? annRes.data : []).slice(0, 3)
  } catch (error) {
    console.error('Failed to load public content:', error)
  } finally {
    loadingPublicContent = false
  }
}

function handleBannerClick(banner: Banner) {
  if (banner.linkType === 'product' && banner.linkValue) {
    uni.navigateTo({ url: `/pages/product/detail/index?id=${banner.linkValue}` })
  } else if (banner.linkType === 'category' && banner.linkValue) {
    const app = getApp()
    app.globalData.productListCategoryId = Number(banner.linkValue)
    uni.navigateTo({ url: '/pages/product/list' })
  }
}

function navigateToCategory(category?: Category) {
  const app = getApp()
  app.globalData.productListCategoryId = category && category.id > 0 ? category.id : null
  uni.navigateTo({ url: '/pages/product/list' })
}

function navigateToProducts() {
  uni.navigateTo({ url: '/pages/product/list' })
}

function navigateToProductDetail(product: Product) {
  if (product.id > 0) {
    uni.navigateTo({ url: `/pages/product/detail/index?id=${product.id}` })
  } else {
    uni.navigateTo({ url: '/pages/product/detail/index?id=0' })
  }
}

function goAnnouncements() {
  uni.navigateTo({ url: '/pages/announcement/list/index' })
}

function goCart() {
  uni.navigateTo({ url: '/pages/cart/index' })
}

function quickAdd(product: Product) {
  if (product.id <= 0) {
    uni.showToast({ title: '示例商品，请进入商品列表选择', icon: 'none' })
    return
  }
  uni.navigateTo({ url: `/pages/product/detail/index?id=${product.id}` })
}

function formatPrice(price: number) {
  return Number(price || 0).toLocaleString('zh-CN')
}
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
  padding-bottom: 28rpx;
  background: #f4f0e8;
  color: #1f160f;
}

.home-header {
  padding: 26rpx 30rpx 16rpx;
  background: #2b0f00;
}

.status-spacer {
  height: 44rpx;
}

.brand-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 88rpx;
}

.brand-left,
.header-actions,
.search-bar,
.channel-row,
.title-left,
.price-row {
  display: flex;
  align-items: center;
}

.brand-left {
  gap: 14rpx;
}

.brand-name {
  color: #f5bc25;
  font-size: 42rpx;
  font-weight: 900;
  letter-spacing: 1rpx;
}

.level-pill {
  padding: 9rpx 18rpx;
  border-radius: 999rpx;
  background: #d4930e;
  color: #fff8d8;
  font-size: 21rpx;
  font-weight: 700;
}

.header-actions {
  gap: 28rpx;
}

.header-icon {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-bar {
  height: 62rpx;
  margin-top: 14rpx;
  padding: 0 24rpx;
  gap: 12rpx;
  border: 2rpx solid rgba(209, 146, 30, 0.62);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.12);
  color: #a8947d;
  font-size: 25rpx;
}

.hero-swiper {
  width: 100%;
  height: 366rpx;
  background: #160900;
}

.hero-card {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.hero-image,
.hero-placeholder,
.hero-mask {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.hero-placeholder {
  background:
    radial-gradient(circle at 78% 34%, rgba(239, 203, 121, 0.16) 0, rgba(239, 203, 121, 0) 32%),
    linear-gradient(110deg, #06100d 0%, #102019 48%, #331208 100%);
}

.hero-mask {
  background: linear-gradient(90deg, rgba(5, 10, 9, 0.82), rgba(5, 10, 9, 0.28), rgba(5, 10, 9, 0.72));
}

.hero-copy {
  position: relative;
  z-index: 1;
  width: 460rpx;
  padding: 64rpx 34rpx;
  display: flex;
  flex-direction: column;
}

.hero-kicker {
  align-self: flex-start;
  padding: 7rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(245, 188, 37, 0.92);
  color: #3a1500;
  font-size: 20rpx;
  font-weight: 800;
}

.hero-title {
  margin-top: 20rpx;
  color: #fff7e5;
  font-size: 42rpx;
  font-weight: 900;
}

.hero-desc {
  margin-top: 12rpx;
  color: rgba(255, 247, 229, 0.76);
  font-size: 24rpx;
}

.wine-bottle {
  position: absolute;
  bottom: 42rpx;
  width: 82rpx;
  height: 236rpx;
  border-radius: 44rpx 44rpx 18rpx 18rpx;
  background: rgba(31, 48, 35, 0.88);
  box-shadow: inset 0 0 0 8rpx rgba(255, 255, 255, 0.12);
}

.wine-bottle::before {
  content: '';
  display: block;
  width: 34rpx;
  height: 70rpx;
  margin: -62rpx auto 0;
  border-radius: 14rpx 14rpx 0 0;
  background: inherit;
}

.wine-bottle-a {
  right: 240rpx;
  transform: rotate(-3deg);
}

.wine-bottle-b {
  right: 120rpx;
  height: 250rpx;
  transform: rotate(5deg);
}

.wine-glass {
  position: absolute;
  right: 44rpx;
  bottom: 46rpx;
  width: 54rpx;
  height: 124rpx;
  border: 5rpx solid rgba(255, 239, 203, 0.62);
  border-top: 0;
  border-radius: 10rpx 10rpx 28rpx 28rpx;
}

.notice-bar {
  height: 78rpx;
  padding: 0 30rpx;
  background: #fff7df;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.notice-label {
  flex-shrink: 0;
  padding: 8rpx 14rpx;
  border-radius: 8rpx;
  background: #cc8600;
  color: #ffffff;
  font-size: 22rpx;
  font-weight: 800;
}

.notice-text {
  color: #7b4c10;
  font-size: 25rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.channel-row {
  justify-content: space-around;
  padding: 32rpx 20rpx 30rpx;
  background: #ffffff;
}

.channel-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14rpx;
  color: #2c2118;
  font-size: 24rpx;
  font-weight: 700;
}

.channel-icon {
  width: 92rpx;
  height: 92rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.channel-icon.gold { background: #fff0c6; }
.channel-icon.blue { background: #dcebff; }
.channel-icon.green { background: #d7f7de; }
.channel-icon.pink { background: #ffe0ef; }

.section-block {
  margin-top: 20rpx;
  padding: 28rpx 30rpx;
  background: #ffffff;
}

.section-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22rpx;
}

.title-left {
  gap: 12rpx;
  color: #1f160f;
  font-size: 32rpx;
  font-weight: 900;
}

.title-mark {
  width: 8rpx;
  height: 30rpx;
  border-radius: 999rpx;
  background: #d39200;
}

.more-link {
  color: #d39200;
  font-size: 24rpx;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 22rpx;
}

.product-card {
  min-width: 0;
  border-radius: 14rpx 14rpx 0 0;
  overflow: hidden;
  background: #ffffff;
  box-shadow: 0 8rpx 24rpx rgba(80, 50, 20, 0.08);
}

.product-cover {
  position: relative;
  height: 248rpx;
  overflow: hidden;
  border-radius: 14rpx 14rpx 0 0;
  background: #e7dac8;
}

.product-img,
.product-art {
  width: 100%;
  height: 100%;
}

.product-art {
  position: relative;
  background:
    radial-gradient(circle at 78% 24%, rgba(245, 188, 37, 0.25), transparent 36%),
    linear-gradient(135deg, #5e0f0e, #d1221d);
}

.product-art-bottle {
  background:
    radial-gradient(circle at 75% 20%, rgba(255, 239, 203, 0.36), transparent 34%),
    linear-gradient(135deg, #050505, #bf8b4b);
}

.product-art-bottle::before {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 18rpx;
  width: 88rpx;
  height: 210rpx;
  border-radius: 42rpx 42rpx 18rpx 18rpx;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.28);
  box-shadow: inset 0 0 0 8rpx rgba(255, 255, 255, 0.22);
}

.box-lid,
.box-body {
  position: absolute;
  left: 38rpx;
  right: 28rpx;
  border-radius: 12rpx;
  background: #d71616;
  border: 4rpx solid #ff6c39;
  transform: skew(-12deg) rotate(-8deg);
}

.box-lid {
  top: 62rpx;
  height: 58rpx;
}

.box-body {
  top: 112rpx;
  height: 84rpx;
}

.chat-float {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 86rpx;
  height: 86rpx;
  border-radius: 50%;
  background: #d89a05;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-info {
  padding: 12rpx 0 0;
}

.product-name {
  display: block;
  padding: 0 4rpx;
  color: #21160e;
  font-size: 28rpx;
  font-weight: 800;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-row {
  justify-content: space-between;
  margin-top: 12rpx;
}

.product-price {
  color: #d98a00;
  font-size: 30rpx;
  font-weight: 900;
}

.add-btn {
  width: 54rpx;
  height: 54rpx;
  border-radius: 50%;
  background: #d39200;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
