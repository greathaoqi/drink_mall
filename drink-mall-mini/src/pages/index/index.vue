<template>
  <view class="home-page">
    <swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500" indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#FFFFFF">
      <swiper-item v-for="banner in banners" :key="banner.id">
        <view class="banner-card" @click="handleBannerClick(banner)">
          <view class="banner-copy">
            <text class="banner-kicker">DEMO CELLAR</text>
            <text class="banner-title">{{ banner.title || '余额支付完整演示' }}</text>
            <text class="banner-desc">浏览、加购、下单、支付，一次走通</text>
          </view>
          <view class="bottle-shape"></view>
        </view>
      </swiper-item>
    </swiper>

    <view class="section">
      <view class="section-header">
        <text class="section-title">平台公告</text>
        <text class="section-more" @click="goAnnouncements">更多</text>
      </view>
      <view v-for="item in announcements" :key="item.id" class="announcement-row" @click="goAnnouncementDetail(item.id)">
        <text class="ann-title">{{ item.title }}</text>
        <text class="ann-date">{{ item.createdAt?.slice(0, 10) }}</text>
      </view>
      <view v-if="announcements.length === 0" class="ann-empty">暂无公告</view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">商品分类</text>
      </view>
      <view class="category-grid">
        <view class="category-item" v-for="category in categories" :key="category.id" @click="navigateToCategory(category)">
          <view class="category-icon">{{ category.name.slice(0, 1) }}</view>
          <text class="category-name">{{ category.name }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-header">
        <text class="section-title">热门商品</text>
        <text class="section-more" @click="navigateToProducts">更多</text>
      </view>
      <view class="product-grid">
        <view class="product-card" v-for="product in products" :key="product.id" @click="navigateToProductDetail(product)">
          <view class="product-image">
            <view class="product-bottle"></view>
            <text class="product-tag">酒水精选</text>
          </view>
          <text class="product-name">{{ product.name }}</text>
          <text class="product-price">¥{{ product.price }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onReady, onShow } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

interface Banner { id: number; title: string; imageUrl: string; linkType: string; linkValue: string }
interface Category { id: number; name: string; iconUrl: string }
interface Product { id: number; name: string; mainImage: string; price: number }

const banners = ref<Banner[]>([])
const categories = ref<Category[]>([])
const products = ref<Product[]>([])
const announcements = ref<{ id: number; title: string; createdAt: string }[]>([])
const pageReady = ref(false)
let loadingPublicContent = false

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
      http.get<Banner[]>('/public/banners', {}, { requireAuth: false }),
      http.get<Category[]>('/public/categories', {}, { requireAuth: false }),
      http.get<{ records?: Product[] } | Product[]>('/public/products', { pageSize: 4 }, { requireAuth: false }),
      http.get<{ id: number; title: string; createdAt: string }[]>('/content/announcements', {}, { requireAuth: false })
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
    uni.switchTab({ url: '/pages/product/list' })
  }
}

function navigateToCategory(category: Category) {
  const app = getApp()
  app.globalData.productListCategoryId = category.id
  uni.switchTab({ url: '/pages/product/list' })
}

function navigateToProducts() {
  uni.switchTab({ url: '/pages/product/list' })
}

function navigateToProductDetail(product: Product) {
  uni.navigateTo({ url: `/pages/product/detail/index?id=${product.id}` })
}

function goAnnouncements() {
  uni.navigateTo({ url: '/pages/announcement/list/index' })
}
function goAnnouncementDetail(id: number) {
  uni.navigateTo({ url: `/pages/announcement/detail/index?id=${id}` })
}
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
  background: #F7F7F7;
}

.banner-swiper {
  width: 100%;
  height: 320rpx;
}

.banner-card {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 42rpx;
  background: linear-gradient(135deg, #2b1608 0%, #8a4f22 48%, #e2b15f 100%);
  overflow: hidden;
}

.banner-copy { display: flex; flex-direction: column; color: #fff7e6; position: relative; z-index: 1; }
.banner-kicker { font-size: 22rpx; letter-spacing: 4rpx; opacity: 0.8; }
.banner-title { margin-top: 18rpx; font-size: 42rpx; font-weight: 800; }
.banner-desc { margin-top: 14rpx; font-size: 24rpx; opacity: 0.82; }
.bottle-shape { width: 120rpx; height: 210rpx; border-radius: 52rpx 52rpx 18rpx 18rpx; background: rgba(255,255,255,0.18); box-shadow: inset 0 0 0 4rpx rgba(255,255,255,0.3); position: relative; }
.bottle-shape::before { content: ''; position: absolute; top: -52rpx; left: 36rpx; width: 48rpx; height: 70rpx; border-radius: 18rpx 18rpx 0 0; background: rgba(255,255,255,0.18); }

.section {
  background: #FFFFFF;
  margin: 16rpx;
  padding: 24rpx;
  border-radius: 16rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.section-more {
  font-size: 28rpx;
  color: #07C160;
}

.category-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
}

.category-item {
  width: calc(25% - 18rpx);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.category-icon {
  width: 88rpx;
  height: 88rpx;
  margin-bottom: 12rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #fff0d6, #8b5a2b);
  color: #4b2d14;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34rpx;
  font-weight: 800;
}

.category-name {
  font-size: 24rpx;
  color: #333333;
}

.product-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.product-card {
  width: calc(50% - 8rpx);
  background: #FFFFFF;
  border-radius: 12rpx;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 280rpx;
  background: radial-gradient(circle at 70% 20%, #ffe4a8 0, transparent 30%), linear-gradient(135deg, #faf2df, #d9a65d);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.product-bottle { width: 62rpx; height: 156rpx; border-radius: 28rpx 28rpx 12rpx 12rpx; background: #5b2f14; box-shadow: inset 0 0 0 8rpx rgba(255,255,255,0.12); }
.product-bottle::before { content: ''; display: block; width: 28rpx; height: 48rpx; border-radius: 12rpx 12rpx 0 0; background: #5b2f14; margin: -42rpx auto 0; }
.product-tag { position: absolute; left: 18rpx; bottom: 16rpx; font-size: 22rpx; color: #704118; background: rgba(255,255,255,0.72); border-radius: 999rpx; padding: 4rpx 14rpx; }

.product-name {
  display: block;
  padding: 12rpx;
  font-size: 28rpx;
  color: #333333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  display: block;
  padding: 0 12rpx 12rpx;
  font-size: 32rpx;
  font-weight: 600;
  color: #FA5151;
}

.announcement-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}
.ann-title {
  font-size: 28rpx;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 20rpx;
}
.ann-date {
  font-size: 24rpx;
  color: #aaa;
  flex-shrink: 0;
}
.ann-empty {
  text-align: center;
  padding: 20rpx;
  color: #ccc;
  font-size: 26rpx;
}
</style>
