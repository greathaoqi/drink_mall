<template>
  <view class="dm-page detail-page">
    <view class="hero">
      <swiper class="hero-swiper" indicator-dots circular>
        <swiper-item v-for="img in images" :key="img">
          <image :src="img" mode="aspectFill" @click="previewImage(img)" />
        </swiper-item>
      </swiper>
      <view class="float-nav">
        <view class="nav-circle" @click="goBack"><uni-icons type="left" size="20" color="#fff" /></view>
        <text>商品详情</text>
        <view class="nav-circle" @click="showShareSheet = true"><uni-icons type="redo" size="18" color="#fff" /></view>
      </view>
    </view>

    <view class="content">
      <view class="price-card">
        <view>
          <view class="price-line">
            <text class="price">￥{{ money(product.discountPrice || product.price) }}</text>
            <text v-if="product.marketPrice" class="market">￥{{ money(product.marketPrice) }}</text>
          </view>
          <text class="level">{{ product.levelPriceText || '会员价' }}</text>
        </view>
        <view class="sales">
          <text>已售 {{ product.salesCount || product.sales || 0 }} 件</text>
          <text>库存 {{ product.stock ?? '充足' }}</text>
        </view>
      </view>

      <text class="title">{{ product.name || '精选酱香白酒 500ml' }}</text>
      <view class="tags">
        <text v-if="zoneLabel">{{ zoneLabel }}</text>
        <text v-if="product.flavor || product.aroma">{{ product.flavor || product.aroma }}</text>
        <text v-if="commissionText">{{ commissionText }}</text>
        <text>单一支付</text>
      </view>

      <view class="panel selector" @click="showSpec = true">
        <view>
          <text class="panel-title">规格选择</text>
          <text class="panel-sub">{{ selectedSkuText }}，{{ qty }} 件</text>
        </view>
        <uni-icons type="right" size="16" color="#C9BFA9" />
      </view>

      <view v-if="product.zoneType === 'investment'" class="panel notice">
        <text class="panel-title">招商购买提示</text>
        <text class="notice-copy">购买前需完成实名认证，并确认合作声明。升级、复购和奖励规则均以后端配置为准。</text>
        <label class="agree">
          <checkbox :checked="cooperationConfirmed" @click="cooperationConfirmed = !cooperationConfirmed" />
          <text>我已阅读并确认合作声明</text>
        </label>
      </view>

      <view v-if="product.zoneType === 'gift'" class="panel notice">
        <text class="panel-title">礼包兑换提示</text>
        <text class="notice-copy">礼包专区仅支持纯积分兑换，不支持现金、余额、酒豆或组合支付。</text>
      </view>

      <view class="panel params">
        <text class="panel-title">商品参数</text>
        <view v-for="item in params" :key="item.label" class="param-row">
          <text>{{ item.label }}</text>
          <text>{{ item.value }}</text>
        </view>
      </view>

      <view class="panel detail-section">
        <text class="panel-title">商品详情</text>
        <image class="detail-image" :src="images[0]" mode="aspectFill" />
        <text class="detail-copy">{{ product.description || '甄选优质酒品，规格、价格、支付方式与权益说明均以后端配置为准。' }}</text>
      </view>
    </view>

    <view v-if="showSpec" class="mask" @click="showSpec = false">
      <view class="spec-panel" @click.stop>
        <view class="spec-head">
          <image :src="images[0]" mode="aspectFill" />
          <view class="spec-main">
            <text class="spec-price">￥{{ money(product.discountPrice || product.price) }}</text>
            <text class="spec-stock">库存 {{ product.stock ?? '充足' }} · 已选 {{ selectedSkuText }}</text>
          </view>
          <uni-icons type="closeempty" size="24" color="#999" @click="showSpec = false" />
        </view>
        <view class="spec-row">
          <text class="spec-title">规格</text>
          <view class="sku-list">
            <text class="sku-chip active">{{ selectedSkuText }}</text>
            <text v-if="product.backupSkuName" class="sku-chip">{{ product.backupSkuName }}</text>
            <text class="sku-chip disabled">整箱装（缺货）</text>
          </view>
        </view>
        <view class="spec-row qty-row">
          <text class="spec-title">购买数量</text>
          <view class="stepper">
            <button @click="qty = Math.max(1, qty - 1)">-</button>
            <text>{{ qty }}</text>
            <button @click="qty++">+</button>
          </view>
        </view>
        <view v-if="commissionText" class="commission-tip">分享成交后可获得 {{ commissionText }}</view>
        <view class="spec-actions">
          <button class="outline" :disabled="product.zoneType === 'gift'" @click="addCart">加入购物车</button>
          <button class="primary" @click="buyNow">{{ product.zoneType === 'gift' ? '积分兑换' : '立即购买' }}</button>
        </view>
      </view>
    </view>

    <view v-if="showShareSheet" class="mask" @click="showShareSheet = false">
      <view class="share-panel" @click.stop>
        <text class="share-title">分享此商品</text>
        <text class="share-sub">好友通过你的分享下单后，奖励以后端配置为准</text>
        <view class="share-preview">
          <image :src="images[0]" mode="aspectFill" />
          <view>
            <text>{{ product.name || '精选酒品' }}</text>
            <text class="preview-price">￥{{ money(product.discountPrice || product.price) }}</text>
          </view>
        </view>
        <view class="share-grid">
          <button open-type="share"><uni-icons type="weixin" size="26" color="#12B95B" /><text>微信好友</text></button>
          <button open-type="share"><uni-icons type="pyq" size="26" color="#0FA34F" /><text>朋友圈</text></button>
          <button @click="showPoster = true"><uni-icons type="paperplane" size="26" color="#D38A00" /><text>生成海报</text></button>
          <button @click="copySharePath"><uni-icons type="link" size="26" color="#3A6FD1" /><text>复制链接</text></button>
        </view>
        <text class="cancel" @click="showShareSheet = false">取消</text>
      </view>
    </view>

    <view v-if="showPoster" class="poster-mask">
      <view class="poster-nav">
        <uni-icons type="left" size="22" color="#FFD685" @click="showPoster = false" />
        <text>商品海报</text>
        <view></view>
      </view>
      <view class="poster-card">
        <view class="poster-brand">
          <text class="brand-mark">醇</text>
          <view><text>醇品汇</text><text>甄选美酒 · 共创财富</text></view>
        </view>
        <image :src="images[0]" mode="aspectFill" />
        <text class="poster-name">{{ product.name || '精选酒品' }}</text>
        <view class="poster-bottom">
          <view><text>会员价</text><text>￥{{ money(product.discountPrice || product.price) }}</text></view>
          <view class="qr">码</view>
        </view>
      </view>
      <view class="poster-actions">
        <button class="outline" @click="savePosterPreview">保存到相册</button>
        <button class="primary" open-type="share">分享给好友</button>
      </view>
    </view>

    <BottomActionBar>
      <template #left>
        <view class="mini-actions">
          <button open-type="contact"><uni-icons type="headphones" size="20" color="#D38A00" /><text>客服</text></button>
          <button @click="goCart"><uni-icons type="cart" size="20" color="#D38A00" /><text>购物车</text></button>
        </view>
      </template>
      <button class="cart-btn" :disabled="product.zoneType === 'gift'" @click="addCart">加入购物车</button>
      <button class="buy-btn" @click="buyNow">{{ product.zoneType === 'gift' ? '积分兑换' : '立即购买' }}</button>
    </BottomActionBar>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onShareAppMessage } from '@dcloudio/uni-app'
import BottomActionBar from '@/components/BottomActionBar/BottomActionBar.vue'
import { productApi } from '@/services/product'
import { cartApi } from '@/services/cart'
import { money } from '@/utils/format'
import { requireLogin, requireRealName } from '@/utils/auth'
import { buildProductSharePath, captureReferral } from '@/utils/referral'
import { useUserStore } from '@/store/user'

const product = ref<any>({})
const productId = ref('')
const qty = ref(1)
const payMethods = ref<any[]>([])
const cooperationConfirmed = ref(false)
const showSpec = ref(false)
const showShareSheet = ref(false)
const showPoster = ref(false)

const images = computed(() => {
  const raw = product.value.images || product.value.mainImage
  if (!raw) return ['/static/images/product-wine.png']
  return Array.isArray(raw) ? raw : String(raw).split(',').filter(Boolean)
})

const selectedSkuText = computed(() => product.value.skuName || product.value.specName || '500ml 单瓶')
const commissionText = computed(() => product.value.commissionText || product.value.rewardText || '')
const zoneLabel = computed(() => {
  const map: Record<string, string> = { main: '主产品专区', investment: '招商专区', retail: '零售专区', gift: '礼包专区' }
  return map[product.value.zoneType] || product.value.zoneName || ''
})
const params = computed(() => [
  { label: '品牌', value: product.value.brand || '甄选酒品' },
  { label: '香型', value: product.value.flavor || product.value.aroma || '以后端为准' },
  { label: '规格', value: selectedSkuText.value },
  { label: '支付', value: payMethodText.value }
])
const payMethodText = computed(() => {
  const text = payMethods.value.map((m) => m.label || m.name || m.value || m.code).filter(Boolean).join('、')
  return text || product.value.payMethodText || '以后端配置为准'
})

async function load() {
  const res = await productApi.detail(productId.value)
  product.value = res.data || {}
  try {
    payMethods.value = (await productApi.payMethods(productId.value)).data || []
  } catch {
    payMethods.value = []
  }
}

function previewImage(current: string) {
  uni.previewImage({ urls: images.value, current })
}

function ensureInvestmentReady() {
  if (product.value.zoneType !== 'investment') return true
  if (!requireRealName('招商购买前请先完成实名认证')) return false
  if (!cooperationConfirmed.value) {
    uni.showToast({ title: '请先确认合作声明', icon: 'none' })
    return false
  }
  return true
}

async function addCart() {
  if (!requireLogin()) return false
  if (product.value.zoneType === 'gift') {
    uni.showToast({ title: '礼包专区仅支持纯积分兑换', icon: 'none' })
    return false
  }
  if (!ensureInvestmentReady()) return false
  await cartApi.add({ productId: productId.value, quantity: qty.value })
  showSpec.value = false
  uni.showToast({ title: '已加入购物车', icon: 'success' })
  return true
}

function buyNow() {
  if (!requireLogin()) return
  if (product.value.zoneType !== 'gift' && !ensureInvestmentReady()) return
  uni.navigateTo({ url: '/pages/checkout/index?productId=' + productId.value + '&quantity=' + qty.value })
}

function goBack() {
  uni.navigateBack({ fail: () => uni.switchTab({ url: '/pages/product/list' }) })
}

function goCart() {
  uni.switchTab({ url: '/pages/cart/index' })
}

function sharePath() {
  return buildProductSharePath({ productId: productId.value, inviteCode: useUserStore().userInfo?.inviteCode, scene: 'share_product' })
}

function copySharePath() {
  uni.setClipboardData({ data: sharePath() })
}

function savePosterPreview() {
  uni.showToast({ title: '海报预览已生成，保存能力待接入画布', icon: 'none' })
}

onLoad((options: any) => {
  captureReferral(options, useUserStore())
  productId.value = options.id || ''
  if (options.confirmCooperation === '1') cooperationConfirmed.value = true
  load()
})

onShareAppMessage(() => ({
  title: product.value.name || '商品',
  path: sharePath()
}))
</script>

<style scoped lang="scss">
.detail-page { padding-bottom: calc(148rpx + env(safe-area-inset-bottom)); }
.hero { position: relative; height: 690rpx; background: #0E0805; }
.hero-swiper, .hero image { width: 100%; height: 100%; }
.float-nav { position: absolute; top: 68rpx; left: 24rpx; right: 24rpx; display: flex; align-items: center; justify-content: space-between; color: #fff; font-size: 32rpx; font-weight: 700; }
.nav-circle { width: 72rpx; height: 72rpx; border-radius: 50%; background: rgba(0,0,0,.42); display: flex; align-items: center; justify-content: center; }
.content { margin-top: -24rpx; position: relative; padding: 0 24rpx 24rpx; }
.price-card, .panel { background: #fff; border-radius: 24rpx; padding: 28rpx; box-shadow: var(--dm-shadow-card); margin-bottom: 20rpx; }
.price-card { display: flex; justify-content: space-between; gap: 18rpx; }
.price-line { display: flex; align-items: baseline; gap: 14rpx; }
.price, .spec-price, .preview-price { color: var(--dm-gold-500); font-weight: 900; font-variant-numeric: tabular-nums; }
.price { font-size: 52rpx; }
.market { color: var(--dm-text-3); text-decoration: line-through; font-size: 24rpx; }
.level, .sales text { display: block; color: var(--dm-text-3); font-size: 22rpx; margin-top: 8rpx; }
.title { display: block; margin: 8rpx 4rpx 16rpx; font-size: 34rpx; font-weight: 800; line-height: 1.42; }
.tags { display: flex; flex-wrap: wrap; gap: 10rpx; margin-bottom: 20rpx; }
.tags text { height: 36rpx; line-height: 36rpx; padding: 0 14rpx; border-radius: 6rpx; background: var(--dm-gold-50); color: var(--dm-gold-600); font-size: 22rpx; }
.selector { display: flex; align-items: center; justify-content: space-between; }
.panel-title { display: block; color: var(--dm-text-1); font-size: 28rpx; font-weight: 800; }
.panel-sub, .notice-copy, .detail-copy { display: block; color: var(--dm-text-2); font-size: 25rpx; line-height: 1.65; margin-top: 10rpx; }
.agree { display: flex; align-items: center; gap: 8rpx; margin-top: 18rpx; font-size: 25rpx; color: var(--dm-text-2); }
.param-row { display: flex; padding-top: 14rpx; font-size: 25rpx; }
.param-row text:first-child { width: 130rpx; color: var(--dm-text-3); }
.detail-image { width: 100%; height: 420rpx; border-radius: 18rpx; margin-top: 18rpx; background: var(--dm-cream-100); }
.mask { position: fixed; inset: 0; z-index: 50; background: rgba(0,0,0,.55); display: flex; align-items: flex-end; }
.spec-panel, .share-panel { width: 100%; background: #fff; border-radius: 36rpx 36rpx 0 0; padding: 32rpx 28rpx calc(28rpx + env(safe-area-inset-bottom)); box-sizing: border-box; }
.spec-head { display: flex; gap: 20rpx; align-items: flex-start; }
.spec-head image { width: 170rpx; height: 170rpx; border-radius: 18rpx; margin-top: -70rpx; border: 6rpx solid #fff; box-shadow: 0 8rpx 28rpx rgba(0,0,0,.16); }
.spec-main { flex: 1; min-width: 0; }
.spec-price { display: block; font-size: 42rpx; }
.spec-stock { display: block; color: var(--dm-text-3); font-size: 23rpx; margin-top: 6rpx; }
.spec-row { margin-top: 30rpx; }
.spec-title { display: block; font-size: 26rpx; color: var(--dm-text-2); margin-bottom: 14rpx; }
.sku-list { display: flex; flex-wrap: wrap; gap: 14rpx; }
.sku-chip { padding: 14rpx 26rpx; border-radius: 12rpx; background: var(--dm-cream-100); color: var(--dm-text-2); font-size: 25rpx; }
.sku-chip.active { background: var(--dm-gold-50); color: var(--dm-gold-600); border: 2rpx solid var(--dm-gold-500); }
.sku-chip.disabled { color: var(--dm-text-3); text-decoration: line-through; }
.qty-row { display: flex; align-items: center; justify-content: space-between; }
.stepper { display: flex; align-items: center; border: 1rpx solid var(--dm-line); border-radius: 12rpx; overflow: hidden; }
.stepper button { width: 64rpx; height: 58rpx; line-height: 58rpx; background: #fff; }
.stepper text { min-width: 78rpx; text-align: center; font-weight: 800; }
.commission-tip { margin-top: 24rpx; padding: 20rpx; border-radius: 16rpx; background: var(--dm-gold-50); color: #7A5610; font-size: 24rpx; }
.spec-actions, .poster-actions { display: flex; gap: 18rpx; margin-top: 28rpx; }
.outline, .primary, .cart-btn, .buy-btn { border-radius: 999rpx; font-weight: 800; }
.outline { color: var(--dm-gold-600); background: var(--dm-gold-50); border: 1rpx solid #F2D78A; }
.primary, .buy-btn { color: #3A1A00; background: var(--dm-grad-gold); }
.cart-btn { width: 200rpx; color: var(--dm-gold-600); background: var(--dm-gold-50); }
.cart-btn[disabled] { opacity: .45; }
.buy-btn { width: 220rpx; }
.mini-actions { display: flex; gap: 8rpx; }
.mini-actions button { width: 88rpx; height: 76rpx; line-height: 1.1; background: transparent; color: var(--dm-text-2); font-size: 20rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 4rpx; }
.share-title, .share-sub { display: block; text-align: center; }
.share-title { font-size: 30rpx; font-weight: 800; }
.share-sub { color: var(--dm-text-3); font-size: 24rpx; margin-top: 8rpx; }
.share-preview { display: flex; gap: 18rpx; padding: 20rpx; margin-top: 24rpx; background: var(--dm-cream-50); border-radius: 18rpx; }
.share-preview image { width: 112rpx; height: 112rpx; border-radius: 12rpx; }
.share-preview text { display: block; font-size: 25rpx; font-weight: 700; }
.share-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12rpx; margin-top: 30rpx; }
.share-grid button { background: transparent; font-size: 22rpx; color: var(--dm-text-2); display: flex; flex-direction: column; align-items: center; gap: 8rpx; }
.cancel { display: block; text-align: center; color: var(--dm-text-3); font-size: 28rpx; margin-top: 28rpx; }
.poster-mask { position: fixed; inset: 0; z-index: 60; background: linear-gradient(180deg, #1A0A02, #2A0E00); padding: 70rpx 60rpx 40rpx; box-sizing: border-box; }
.poster-nav { display: flex; align-items: center; justify-content: space-between; color: #F6E7C2; font-size: 32rpx; font-weight: 700; }
.poster-card { margin-top: 42rpx; background: #fff; border-radius: 28rpx; padding: 28rpx; box-shadow: 0 20rpx 80rpx rgba(0,0,0,.45); }
.poster-brand { background: var(--dm-grad-brown); border-radius: 18rpx; padding: 20rpx; display: flex; align-items: center; gap: 18rpx; color: #FFD685; }
.brand-mark { width: 72rpx; height: 72rpx; border-radius: 50%; background: var(--dm-grad-gold); color: #3A1A00; display: flex; align-items: center; justify-content: center; font-weight: 900; }
.poster-brand text { display: block; }
.poster-brand text:last-child { font-size: 20rpx; opacity: .75; }
.poster-card image { width: 100%; height: 480rpx; border-radius: 18rpx; margin-top: 24rpx; }
.poster-name { display: block; margin-top: 20rpx; font-size: 28rpx; font-weight: 800; line-height: 1.4; }
.poster-bottom { display: flex; justify-content: space-between; align-items: flex-end; margin-top: 26rpx; }
.poster-bottom text { display: block; color: var(--dm-text-3); font-size: 22rpx; }
.poster-bottom text:last-child { color: var(--dm-gold-500); font-size: 40rpx; font-weight: 900; }
.qr { width: 120rpx; height: 120rpx; border: 1rpx solid var(--dm-line); border-radius: 12rpx; display: flex; align-items: center; justify-content: center; color: var(--dm-text-3); }
.poster-actions { position: absolute; left: 32rpx; right: 32rpx; bottom: calc(32rpx + env(safe-area-inset-bottom)); }
button:after { border: 0; }
</style>
