<template>
  <view class="mall-page home">
    <view class="brand-head">
      <view class="status-space"></view>
      <view class="brand-row">
        <view class="brand-left">
          <text class="brand">醇品汇</text>
          <text class="level-badge">{{ memberLevel }}</text>
        </view>
        <view class="head-actions">
          <view class="head-icon mall-press" @click="switchTab('/pages/cart/index')">
            <uni-icons type="cart" size="22" color="#FFD685" />
          </view>
          <view class="head-icon mall-press" @click="go('/pages/announcement/list/index')">
            <uni-icons type="notification" size="22" color="#FFD685" />
          </view>
        </view>
      </view>
      <MallSearchBar placeholder="搜索酒水商品..." @tap="go('/pages/product/list?mode=search')" />
    </view>

    <view class="content">
      <swiper class="banner" indicator-dots autoplay circular>
        <swiper-item v-for="b in banners" :key="b.id || b.imageUrl">
          <view v-if="!b.imageUrl" class="banner-fallback">
            <view>
              <text class="banner-kicker">2026 · AUTUMN</text>
              <text class="banner-title">秋季新品季</text>
              <text class="banner-copy">限时联营商福利 · 最高省￥600</text>
            </view>
            <image class="banner-bottle" src="/static/images/banner-wine.png" mode="aspectFit" />
          </view>
          <image v-else class="banner-image" :src="b.imageUrl" mode="aspectFill" />
        </swiper-item>
      </swiper>

      <view class="notice mall-press" @click="go('/pages/announcement/list/index')">
        <text class="notice-badge">公告</text>
        <text class="notice-title">{{ noticeTitle }}</text>
        <uni-icons type="right" size="15" color="#B07000" />
      </view>

      <view class="zone-grid">
        <view v-for="z in zones" :key="z.title" class="zone mall-press" @click="openZone(z)">
          <view class="zone-icon" :style="{ background: z.bg }">
            <image :src="z.image" mode="aspectFit" />
          </view>
          <text>{{ z.title }}</text>
        </view>
      </view>

      <view class="section-head">
        <view class="section-title"><text class="title-bar"></text>主产品专区</view>
        <text class="more mall-press" @click="go('/pages/product/list?zoneType=main')">更多 ›</text>
      </view>
      <PageState :loading="loading" :empty="mainProducts.length === 0" empty-text="暂无主产品商品">
        <view class="product-grid">
          <ProductCard v-for="p in mainProducts" :key="p.id" :product="p" @tap="goProduct" />
        </view>
      </PageState>

      <view class="section-head">
        <view class="section-title"><text class="title-bar"></text>零售专区</view>
        <text class="more mall-press" @click="go('/pages/product/list?zoneType=retail')">更多 ›</text>
      </view>
      <PageState :loading="retailLoading" :empty="retailProducts.length === 0" empty-text="暂无零售商品">
        <view class="product-grid">
          <ProductCard v-for="p in retailProducts" :key="p.id" :product="p" @tap="goProduct" />
        </view>
      </PageState>
    </view>

    <button open-type="contact" class="float-service mall-press">
      <uni-icons type="chat" size="24" color="#3A1A00" />
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onPullDownRefresh, onShareAppMessage, onShow } from '@dcloudio/uni-app'
import MallSearchBar from '@/components/MallSearchBar/MallSearchBar.vue'
import ProductCard from '@/components/ProductCard/ProductCard.vue'
import PageState from '@/components/PageState/PageState.vue'
import { configApi } from '@/services/config'
import { productApi } from '@/services/product'
import { listOf } from '@/utils/format'
import { buildProductDetailUrl } from '@/utils/product'
import { buildMiniProgramSharePath, captureReferral } from '@/utils/referral'
import { useUserStore } from '@/store/user'

const store = useUserStore()
const loading = ref(false)
const retailLoading = ref(false)
const banners = ref<any[]>([{ id: 0 }])
const notices = ref<any[]>([])
const mainProducts = ref<any[]>([])
const retailProducts = ref<any[]>([])

const zones = [
  { title: '主产品专区', image: '/static/images/zone-main.png', bg: '#FBE9B6', url: '/pages/product/list?zoneType=main' },
  { title: '招商专区', image: '/static/images/zone-invest.png', bg: '#DCE5FA', url: '/pages/zone/investment/index' },
  { title: '零售专区', image: '/static/images/zone-retail.png', bg: '#D9F0E0', url: '/pages/product/list?zoneType=retail' },
  { title: '礼包专区', image: '/static/images/zone-gift.png', bg: '#FCDDE6', url: '/pages/zone/gift/index' }
]

const memberLevel = computed(() => store.userInfo?.levelName || '县级联营商')
const noticeTitle = computed(() => notices.value[0]?.title || '关于秋季新品上市及联营商福利活动通知')

async function load() {
  loading.value = true
  retailLoading.value = true
  try {
    const [bs, ns, main, retail] = await Promise.allSettled([
      configApi.banners(),
      configApi.announcements(),
      productApi.list({ zoneType: 'main', page: 1, size: 4 }),
      productApi.list({ zoneType: 'retail', page: 1, size: 2 })
    ])
    if (bs.status === 'fulfilled' && bs.value.data?.length) banners.value = bs.value.data.slice(0, 5)
    if (ns.status === 'fulfilled') notices.value = ns.value.data || []
    if (main.status === 'fulfilled') mainProducts.value = listOf(main.value.data).slice(0, 4)
    if (retail.status === 'fulfilled') retailProducts.value = listOf(retail.value.data).slice(0, 2)
  } finally {
    loading.value = false
    retailLoading.value = false
  }
}

function go(url: string) {
  uni.navigateTo({ url })
}

function switchTab(url: string) {
  uni.switchTab({ url })
}

function openZone(zone: { url: string }) {
  go(zone.url)
}

function goProduct(product: any) {
  const url = buildProductDetailUrl(product)
  if (!url) {
    uni.showToast({ title: '商品ID缺失', icon: 'none' })
    return
  }
  go(url)
}

onLoad((options: any) => {
  captureReferral(options, store)
})
onShow(load)
onPullDownRefresh(async () => {
  await load()
  uni.stopPullDownRefresh()
})
onShareAppMessage(() => ({
  title: '醇品汇酒水商城',
  path: buildMiniProgramSharePath(store.userInfo?.inviteCode)
}))
</script>

<style scoped lang="scss">
.home {
  padding-bottom: 32rpx;
}

.brand-head {
  padding: 0 var(--dm-page-x) 28rpx;
  background: var(--dm-grad-brown);
  color: var(--dm-text-on-brown);
}

.status-space {
  height: 78rpx;
}

.brand-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.brand-left,
.head-actions {
  display: flex;
  align-items: center;
}

.brand-left {
  gap: 18rpx;
  min-width: 0;
}

.brand {
  color: #FFE0A0;
  font-size: 44rpx;
  font-weight: 900;
  letter-spacing: 2rpx;
}

.level-badge {
  height: 38rpx;
  line-height: 38rpx;
  padding: 0 16rpx;
  border-radius: var(--dm-radius-xs);
  background: var(--dm-grad-gold);
  color: #3A1A00;
  font-size: 22rpx;
  font-weight: 700;
}

.head-actions {
  gap: 24rpx;
}

.head-icon {
  width: 44rpx;
  height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.content {
  padding: 24rpx var(--dm-page-x) 110rpx;
}

.banner {
  height: clamp(260rpx, 40vw, 336rpx);
  border-radius: 28rpx;
  overflow: hidden;
  background: #2A1408;
}

.banner-image,
.banner-fallback {
  width: 100%;
  height: 100%;
}

.banner-fallback {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  padding: 0 44rpx;
  background: radial-gradient(ellipse at 72% 40%, rgba(255, 214, 70, .22), transparent 38%), linear-gradient(135deg, #2A1408 0%, #4A2410 54%, #1F0E04 100%);
}

.banner-kicker,
.banner-title,
.banner-copy {
  display: block;
}

.banner-kicker {
  color: rgba(255, 224, 160, .68);
  font-size: 22rpx;
  letter-spacing: 6rpx;
}

.banner-title {
  margin-top: 10rpx;
  color: #FFE0A0;
  font-size: 44rpx;
  font-weight: 900;
}

.banner-copy {
  margin-top: 8rpx;
  color: rgba(255, 224, 160, .78);
  font-size: 24rpx;
}

.banner-bottle {
  width: 180rpx;
  height: 220rpx;
  margin-left: auto;
}

.notice {
  margin-top: 24rpx;
  min-height: 72rpx;
  padding: 0 24rpx;
  border-radius: 20rpx;
  background: var(--dm-gold-50);
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.notice-badge {
  flex: 0 0 auto;
  height: 40rpx;
  line-height: 40rpx;
  padding: 0 16rpx;
  border-radius: var(--dm-radius-xs);
  background: var(--dm-grad-gold);
  color: #3A1A00;
  font-size: 22rpx;
  font-weight: 700;
}

.notice-title {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #7A5610;
  font-size: 24rpx;
}

.zone-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12rpx;
  margin-top: 36rpx;
}

.zone {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  min-width: 0;
  color: var(--dm-text-1);
  font-size: clamp(22rpx, 3vw, 24rpx);
  font-weight: 600;
}

.zone-icon {
  width: clamp(92rpx, 14vw, 104rpx);
  height: clamp(92rpx, 14vw, 104rpx);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.zone-icon image {
  width: 72%;
  height: 72%;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 34rpx;
  margin-bottom: 20rpx;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 14rpx;
  color: var(--dm-text-1);
  font-size: 32rpx;
  font-weight: 900;
}

.title-bar {
  width: 8rpx;
  height: 32rpx;
  border-radius: 4rpx;
  background: var(--dm-grad-gold);
}

.more {
  color: var(--dm-text-3);
  font-size: 24rpx;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(16rpx, 2.6vw, 20rpx);
}

.float-service {
  position: fixed;
  right: 28rpx;
  bottom: calc(112rpx + env(safe-area-inset-bottom));
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: var(--dm-grad-gold);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 16rpx 40rpx rgba(211, 138, 0, .35);
}
</style>
