<template>
  <view class="mall-page zone-page">
    <view class="zone-hero gift">
      <view class="status-space"></view>
      <view class="nav">
        <uni-icons type="left" size="22" color="#F6E7C2" @click="back" />
        <text>礼包专区</text>
        <view class="nav-gap"></view>
      </view>
      <view class="hero-copy">
        <text class="kicker">EXCLUSIVE ZONE</text>
        <text class="title">礼包专区</text>
        <text class="subtitle">商务礼赠 · 积分单一方式兑换</text>
      </view>
    </view>

    <view class="content-shell">
      <scroll-view scroll-x class="filter-scroll" show-scrollbar="false">
        <view class="filter-tabs">
          <text v-for="item in filters" :key="item" :class="{ on: activeFilter === item }" @click="activeFilter = item">{{ item }}</text>
        </view>
      </scroll-view>

      <view class="rule-card">
        <text class="rule-title">资产独立提示</text>
        <text class="rule-copy">礼包商品仅按后台配置展示可用支付方式。当前兑换入口只提交积分方式，不组合余额、现金或酒豆。</text>
      </view>

      <PageState :loading="loading" :empty="products.length === 0" empty-text="暂无礼包商品">
        <view class="product-grid">
          <view v-for="p in products" :key="p.id" class="gift-item">
            <ProductCard :product="p" @tap="goDetail" />
            <button class="redeem mall-press" @click="redeem(p)">积分兑换</button>
          </view>
        </view>
      </PageState>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import ProductCard from '@/components/ProductCard/ProductCard.vue'
import PageState from '@/components/PageState/PageState.vue'
import { productApi } from '@/services/product'
import { listOf } from '@/utils/format'
import { useUserStore } from '@/store/user'
import { requireLogin } from '@/utils/auth'
import { buildProductDetailUrl, normalizeProductId } from '@/utils/product'

const products = ref<any[]>([])
const loading = ref(false)
const activeFilter = ref('全部')
const filters = ['全部', '商务礼盒', '节庆礼盒', '积分优选', '双瓶装']

async function load() {
  loading.value = true
  try {
    products.value = listOf((await productApi.list({ zoneType: 'gift', page: 1, size: 20 })).data)
  } finally {
    loading.value = false
  }
}

function goDetail(product: any) {
  const url = buildProductDetailUrl(product)
  if (!url) {
    uni.showToast({ title: '商品ID缺失', icon: 'none' })
    return
  }
  uni.navigateTo({ url })
}

async function redeem(product: any) {
  if (!requireLogin()) return
  const productId = normalizeProductId(product)
  if (!productId) {
    uni.showToast({ title: '商品ID缺失', icon: 'none' })
    return
  }
  const need = Number(product.pointsPrice || product.price || 0)
  const has = Number(useUserStore().userInfo?.points || 0)
  if (has < need) {
    uni.showToast({ title: '积分不足，不能提交', icon: 'none' })
    return
  }
  await productApi.redeemGift(productId, 1)
  uni.showToast({ title: '兑换成功', icon: 'success' })
}

function back() {
  const pages = getCurrentPages()
  if (pages.length > 1) uni.navigateBack()
  else uni.switchTab({ url: '/pages/index/index' })
}

onShow(load)
</script>

<style scoped lang="scss">
.zone-page {
  padding-bottom: 0;
}

.zone-hero {
  color: var(--dm-text-on-brown);
  background: radial-gradient(ellipse 80% 60% at 50% 28%, rgba(209, 70, 122, .26), transparent 60%), var(--dm-grad-brown);
}

.status-space {
  height: 78rpx;
}

.nav {
  height: 88rpx;
  padding: 0 28rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--dm-text-on-brown);
  font-size: 34rpx;
  font-weight: 800;
}

.nav-gap {
  width: 44rpx;
}

.hero-copy {
  padding: 18rpx 40rpx 56rpx;
}

.kicker,
.title,
.subtitle,
.rule-title,
.rule-copy {
  display: block;
}

.kicker {
  color: rgba(246, 231, 194, .62);
  font-size: 22rpx;
  letter-spacing: 8rpx;
}

.title {
  margin-top: 8rpx;
  color: #F4BFD0;
  font-size: 52rpx;
  font-weight: 900;
}

.subtitle {
  margin-top: 12rpx;
  color: rgba(246, 231, 194, .78);
  font-size: 26rpx;
}

.content-shell {
  min-height: calc(100vh - 300rpx);
  margin-top: -28rpx;
  padding: 28rpx var(--dm-page-x) 40rpx;
  border-radius: 40rpx 40rpx 0 0;
  background: var(--dm-bg-app);
}

.filter-scroll {
  width: 100%;
  white-space: nowrap;
}

.filter-tabs {
  display: inline-flex;
  gap: 16rpx;
  padding-bottom: 24rpx;
}

.filter-tabs text {
  height: 58rpx;
  line-height: 58rpx;
  padding: 0 28rpx;
  border-radius: var(--dm-radius-pill);
  background: #FFFFFF;
  color: var(--dm-text-2);
  border: 2rpx solid var(--dm-line);
  font-size: 24rpx;
  font-weight: 700;
}

.filter-tabs text.on {
  background: #F4BFD0;
  color: #6C1234;
  border-color: transparent;
}

.rule-card {
  margin-bottom: 22rpx;
  padding: 24rpx;
  border-radius: var(--dm-radius-md);
  background: #FFFFFF;
  box-shadow: var(--dm-shadow-card);
  border-left: 8rpx solid #D1467A;
}

.rule-title {
  color: var(--dm-text-1);
  font-size: 30rpx;
  font-weight: 900;
}

.rule-copy {
  margin-top: 8rpx;
  color: var(--dm-text-2);
  font-size: 24rpx;
  line-height: 1.5;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
}

.gift-item {
  min-width: 0;
}

.redeem {
  width: 100%;
  height: 72rpx;
  line-height: 72rpx;
  margin-top: 14rpx;
  border-radius: var(--dm-radius-pill);
  background: var(--dm-grad-gold);
  color: #3A1A00;
  font-size: 26rpx;
  font-weight: 800;
}
</style>
