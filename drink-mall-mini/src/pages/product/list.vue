<template>
  <view class="product-list-page">
    <view class="search-bar">
      <uni-icons type="search" size="18" color="#999" />
      <input
        v-model="keyword"
        class="search-input"
        placeholder="搜索商品"
        confirm-type="search"
        @confirm="onSearch"
      />
      <text v-if="keyword" class="clear" @click="clearSearch">✕</text>
    </view>

    <view class="sort-bar">
      <view class="sort-item" :class="{ active: sortBy === '' }" @click="setSort('')">综合</view>
      <view class="sort-item" :class="{ active: sortBy === 'sales' }" @click="setSort('sales')">销量</view>
      <view class="sort-item" :class="{ active: sortBy === 'price_asc' }" @click="setSort('price_asc')">价格↑</view>
      <view class="sort-item" :class="{ active: sortBy === 'price_desc' }" @click="setSort('price_desc')">价格↓</view>
    </view>

    <view class="product-grid">
      <view class="product-card" v-for="product in products" :key="product.id" @click="navigateToDetail(product)">
        <view class="product-image">
          <view class="product-bottle"></view>
          <text class="product-zone">{{ product.zoneType === 'gift' ? '礼品' : product.zoneType === 'retail' ? '零售' : '精选' }}</text>
        </view>
        <text class="product-name">{{ product.name }}</text>
        <view class="product-bottom">
          <text class="product-price">¥{{ product.price }}</text>
          <text class="product-sales">已售{{ product.sales || 0 }}</text>
        </view>
      </view>
    </view>

    <view v-if="loading" class="loading"><text>加载中...</text></view>
    <view v-if="!loading && products.length === 0" class="empty"><text>暂无商品</text></view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

interface Product { id: number; name: string; mainImage: string; price: number; sales: number; zoneType: string }

const products = ref<Product[]>([])
const loading = ref(false)
const currentCategoryId = ref<number | null>(null)
const keyword = ref('')
const sortBy = ref('')

onShow(() => {
  const app = getApp()
  const categoryId = app.globalData.productListCategoryId
  app.globalData.productListCategoryId = null
  currentCategoryId.value = categoryId
  loadProducts()
})

onPullDownRefresh(() => {
  loadProducts().then(() => uni.stopPullDownRefresh())
})

async function loadProducts() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: 1, size: 20 }
    if (currentCategoryId.value) params.categoryId = currentCategoryId.value
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    if (sortBy.value) params.sortBy = sortBy.value
    const res = await http.get<{ records: Product[] }>('/public/products', params, { requireAuth: false })
    if (res.code === 200) products.value = res.data.records || res.data || []
  } catch (error) {
    console.error('Failed to load products:', error)
  } finally {
    loading.value = false
  }
}

function onSearch() { loadProducts() }
function clearSearch() { keyword.value = ''; loadProducts() }
function setSort(sort: string) { sortBy.value = sort; loadProducts() }
function navigateToDetail(product: Product) {
  uni.navigateTo({ url: `/pages/product/detail/index?id=${product.id}` })
}
</script>

<style scoped lang="scss">
.product-list-page { min-height: 100vh; background: #F7F7F7; padding: 0 0 16rpx; }

.search-bar {
  display: flex;
  align-items: center;
  background: #fff;
  margin: 16rpx;
  border-radius: 40rpx;
  padding: 16rpx 24rpx;
  gap: 12rpx;
}
.search-input { flex: 1; font-size: 28rpx; color: #333; }
.clear { font-size: 28rpx; color: #aaa; padding: 0 8rpx; }

.sort-bar {
  display: flex;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
  margin-bottom: 16rpx;
}
.sort-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 26rpx;
  color: #666;
}
.sort-item.active { color: #8a4f22; font-weight: 600; border-bottom: 4rpx solid #8a4f22; }

.product-grid { display: flex; flex-wrap: wrap; gap: 16rpx; padding: 0 16rpx; }
.product-card { width: calc(50% - 8rpx); background: #FFFFFF; border-radius: 12rpx; overflow: hidden; }
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
.product-zone { position: absolute; left: 14rpx; bottom: 14rpx; font-size: 20rpx; color: #704118; background: rgba(255,255,255,0.72); border-radius: 999rpx; padding: 4rpx 12rpx; }
.product-name { display: block; padding: 12rpx 12rpx 4rpx; font-size: 28rpx; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-bottom { display: flex; justify-content: space-between; align-items: baseline; padding: 0 12rpx 12rpx; }
.product-price { font-size: 32rpx; font-weight: 600; color: #FA5151; }
.product-sales { font-size: 22rpx; color: #aaa; }

.loading, .empty { text-align: center; padding: 80rpx; color: #aaa; font-size: 28rpx; }
</style>
