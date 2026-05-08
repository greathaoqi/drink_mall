<template>
  <view class="home-page">
    <swiper class="banner-swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500" indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#FFFFFF">
      <swiper-item v-for="banner in banners" :key="banner.id">
        <image class="banner-image" :src="banner.imageUrl" mode="aspectFill" @click="handleBannerClick(banner)" />
      </swiper-item>
    </swiper>

    <view class="section">
      <view class="section-header">
        <text class="section-title">商品分类</text>
      </view>
      <view class="category-grid">
        <view class="category-item" v-for="category in categories" :key="category.id" @click="navigateToCategory(category)">
          <image class="category-icon" :src="category.iconUrl || '/static/icons/default-category.png'" />
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
          <image class="product-image" :src="product.mainImage" mode="aspectFill" />
          <text class="product-name">{{ product.name }}</text>
          <text class="product-price">¥{{ product.price }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { http } from '@/utils/request'

interface Banner { id: number; imageUrl: string; linkType: string; linkValue: string }
interface Category { id: number; name: string; iconUrl: string }
interface Product { id: number; name: string; mainImage: string; price: number }

const banners = ref<Banner[]>([])
const categories = ref<Category[]>([])
const products = ref<Product[]>([])

onMounted(() => {
  loadPublicContent()
})

async function loadPublicContent() {
  try {
    const bannerRes = await http.get<Banner[]>('/public/banners')
    if (bannerRes.code === 200) banners.value = bannerRes.data

    const categoryRes = await http.get<Category[]>('/public/categories')
    if (categoryRes.code === 200) categories.value = categoryRes.data

    const productRes = await http.get<{ records: Product[] }>('/public/products', { pageSize: 4 })
    if (productRes.code === 200) products.value = productRes.data.records || productRes.data
  } catch (error) {
    console.error('Failed to load public content:', error)
  }
}

function handleBannerClick(banner: Banner) {
  if (banner.linkType === 'product' && banner.linkValue) {
    uni.navigateTo({ url: `/pages/product/detail?id=${banner.linkValue}` })
  } else if (banner.linkType === 'category' && banner.linkValue) {
    uni.navigateTo({ url: `/pages/product/list?categoryId=${banner.linkValue}` })
  }
}

function navigateToCategory(category: Category) {
  uni.navigateTo({ url: `/pages/product/list?categoryId=${category.id}` })
}

function navigateToProducts() {
  uni.switchTab({ url: '/pages/product/list' })
}

function navigateToProductDetail(product: Product) {
  uni.navigateTo({ url: `/pages/product/detail?id=${product.id}` })
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

.banner-image {
  width: 100%;
  height: 100%;
}

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
}

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
</style>