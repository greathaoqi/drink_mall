<template>
  <view class="mall-page zone-page">
    <view class="zone-hero invest">
      <view class="status-space"></view>
      <view class="nav">
        <uni-icons type="left" size="22" color="#F6E7C2" @click="back" />
        <text>招商专区</text>
        <view class="nav-gap"></view>
      </view>
      <view class="hero-copy">
        <text class="kicker">EXCLUSIVE ZONE</text>
        <text class="title">招商专区</text>
        <text class="subtitle">低门槛起步 · 奖励规则以后端配置为准</text>
      </view>
    </view>

    <view class="content-shell">
      <scroll-view scroll-x class="filter-scroll" show-scrollbar="false">
        <view class="filter-tabs">
          <text v-for="item in filters" :key="item" :class="{ on: activeFilter === item }" @click="activeFilter = item">{{ item }}</text>
        </view>
      </scroll-view>

      <view class="guard-card" v-if="!approved">
        <view>
          <text class="guard-title">实名认证后购买</text>
          <text class="guard-copy">招商商品为联营商升级套餐，购买前需完成实名认证并确认合作声明。</text>
        </view>
        <button @click="goReal">去实名认证</button>
      </view>

      <label class="agree-card">
        <checkbox :checked="confirmed" @click="confirmed = !confirmed" />
        <text>我已阅读并确认合作声明，金额与规则以后端配置为准</text>
      </label>

      <PageState :loading="loading" :empty="products.length === 0" empty-text="暂无招商商品">
        <view class="product-grid">
          <ProductCard v-for="p in products" :key="p.id" :product="p" @tap="buy" />
        </view>
      </PageState>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import ProductCard from '@/components/ProductCard/ProductCard.vue'
import PageState from '@/components/PageState/PageState.vue'
import { productApi } from '@/services/product'
import { useUserStore } from '@/store/user'
import { listOf } from '@/utils/format'

const store = useUserStore()
const products = ref<any[]>([])
const loading = ref(false)
const confirmed = ref(false)
const activeFilter = ref('全部')
const filters = ['全部', '酱香型', '浓香型', '清香型', '兼香型']
const approved = computed(() => store.realNameApproved)

async function load() {
  loading.value = true
  try {
    products.value = listOf((await productApi.list({ zoneType: 'investment', page: 1, size: 20 })).data)
  } finally {
    loading.value = false
  }
}

function goReal() {
  uni.navigateTo({ url: '/pages/auth/realname/index' })
}

function buy(product: any) {
  if (!approved.value) {
    goReal()
    return
  }
  if (!confirmed.value) {
    uni.showToast({ title: '请先确认合作声明', icon: 'none' })
    return
  }
  uni.navigateTo({ url: '/pages/product/detail/index?id=' + product.id + '&confirmCooperation=1' })
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
  background: radial-gradient(ellipse 80% 60% at 50% 28%, rgba(58, 111, 209, .24), transparent 60%), var(--dm-grad-brown);
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
.subtitle {
  display: block;
}

.kicker {
  color: rgba(246, 231, 194, .62);
  font-size: 22rpx;
  letter-spacing: 8rpx;
}

.title {
  margin-top: 8rpx;
  color: #BFD2F4;
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
  background: #BFD2F4;
  color: #102A5C;
  border-color: transparent;
}

.guard-card,
.agree-card {
  margin-bottom: 22rpx;
  padding: 24rpx;
  border-radius: var(--dm-radius-md);
  background: #FFFFFF;
  box-shadow: var(--dm-shadow-card);
}

.guard-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  border-left: 8rpx solid #3A6FD1;
}

.guard-title,
.guard-copy {
  display: block;
}

.guard-title {
  color: var(--dm-text-1);
  font-size: 30rpx;
  font-weight: 900;
}

.guard-copy {
  margin-top: 8rpx;
  color: var(--dm-text-2);
  font-size: 24rpx;
  line-height: 1.5;
}

.guard-card button {
  flex: 0 0 auto;
  height: 64rpx;
  line-height: 64rpx;
  padding: 0 22rpx;
  border-radius: var(--dm-radius-pill);
  background: #E8EFFD;
  color: #3A6FD1;
  font-size: 24rpx;
  font-weight: 800;
}

.agree-card {
  display: flex;
  align-items: center;
  gap: 12rpx;
  color: var(--dm-text-2);
  font-size: 25rpx;
  line-height: 1.45;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
}
</style>
