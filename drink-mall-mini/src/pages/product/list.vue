<template>
  <view class="product-list-page">
    <view class="product-grid">
      <view class="product-card" v-for="product in products" :key="product.id" @click="navigateToDetail(product)">
        <view class="product-image">
          <view class="product-bottle"></view>
          <text class="product-zone">DEMO</text>
        </view>
        <text class="product-name">{{ product.name }}</text>
        <text class="product-price">¥{{ product.price }}</text>
      </view>
    </view>
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    <view v-if="!loading && products.length === 0" class="empty">
      <text>暂无商品</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

interface Product { id: number; name: string; mainImage: string; price: number }

const products = ref<Product[]>([])
const loading = ref(false)
const currentCategoryId = ref<number | null>(null)

onShow(() => {
  const app = getApp()
  const categoryId = app.globalData.productListCategoryId
  app.globalData.productListCategoryId = null
  currentCategoryId.value = categoryId
  loadProducts()
})

onPullDownRefresh(() => {
  loadProducts().then(() => {
    uni.stopPullDownRefresh()
  })
})

async function loadProducts() {
  loading.value = true
  try {
    const params: Record<string, any> = { pageSize: 20 }
    if (currentCategoryId.value) {
      params.categoryId = currentCategoryId.value
    }
    const res = await http.get<{ records: Product[] }>('/public/products', params, { requireAuth: false })
    if (res.code === 200) {
      products.value = res.data.records || res.data || []
    }
  } catch (error) {
    console.error('Failed to load products:', error)
  } finally {
    loading.value = false
  }
}

function navigateToDetail(product: Product) {
  uni.navigateTo({ url: `/pages/product/detail/index?id=${product.id}` })
}
</script>

<style scoped lang="scss">
.product-list-page {
  min-height: 100vh;
  background: #F7F7F7;
  padding: 16rpx;
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
  background: radial-gradient(circle at 75% 18%, #ffe3a3 0, transparent 32%), linear-gradient(135deg, #fbf1db, #bd7a35);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.product-bottle { width: 58rpx; height: 148rpx; border-radius: 26rpx 26rpx 12rpx 12rpx; background: #4d2a13; }
.product-bottle::before { content: ''; display: block; width: 26rpx; height: 44rpx; border-radius: 12rpx 12rpx 0 0; background: #4d2a13; margin: -38rpx auto 0; }
.product-zone { position: absolute; right: 16rpx; bottom: 16rpx; color: #fff7e6; font-size: 20rpx; letter-spacing: 2rpx; }

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

.loading, .empty {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60rpx;
  color: #999999;
}
</style>
