<template>
  <view class="page detail">
    <swiper class="hero" indicator-dots>
      <swiper-item v-for="img in images" :key="img">
        <image v-if="img" :src="img" mode="aspectFill" />
        <view v-else class="hero-empty">商品图</view>
      </swiper-item>
    </swiper>

    <view class="card">
      <text class="price">¥{{ product.discountPrice || product.price || '0.00' }}</text>
      <text class="title">{{ product.name || '商品详情' }}</text>
      <view class="tags">
        <text>等级折扣：{{ product.levelDiscountText || '以后端返回为准' }}</text>
        <text>赠送积分：{{ product.giftPoints || 0 }}</text>
      </view>
      <view class="muted">支付方式：{{ payMethodText }}</view>
    </view>

    <view class="card notice" v-if="product.zoneType === 'investment'">
      <text class="title">招商购买提示</text>
      <text class="muted">购买前需完成实名认证，并确认合作声明。跳级购买/补差价金额由后端配置返回。</text>
      <label class="agree"><checkbox :checked="cooperationConfirmed" @click="cooperationConfirmed = !cooperationConfirmed" />我已阅读并确认合作声明</label>
    </view>

    <view class="card notice" v-if="product.zoneType === 'gift'">
      <text class="title">礼包兑换提示</text>
      <text class="muted">礼包专区仅允许纯积分兑换，不支持现金、余额、酒豆或组合支付。</text>
    </view>

    <view class="card">
      <text class="title">数量</text>
      <view class="qty">
        <button @click="qty = Math.max(1, qty - 1)">-</button>
        <text>{{ qty }}</text>
        <button @click="qty++">+</button>
      </view>
    </view>

    <view class="bottom">
      <button :disabled="product.zoneType === 'gift'" @click="addCart">加入购物车</button>
      <button class="primary" @click="buyNow">{{ product.zoneType === 'gift' ? '去积分兑换' : '立即购买' }}</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onShareAppMessage } from '@dcloudio/uni-app'
import { productApi } from '@/services/product'
import { cartApi } from '@/services/cart'
import { requireLogin } from '@/utils/auth'
import { useUserStore } from '@/store/user'

const product = ref<any>({})
const productId = ref('')
const qty = ref(1)
const payMethods = ref<any[]>([])
const cooperationConfirmed = ref(false)

const images = computed(() => {
  const raw = product.value.images || product.value.mainImage
  if (!raw) return ['']
  return Array.isArray(raw) ? raw : String(raw).split(',')
})
const payMethodText = computed(() => payMethods.value.map((m) => m.label || m.name).join('、') || product.value.payMethodText || '以后端配置为准')

async function load() {
  const res = await productApi.detail(productId.value)
  product.value = res.data || {}
  try { payMethods.value = (await productApi.payMethods(productId.value)).data || [] } catch {}
}

function ensureInvestmentReady() {
  if (product.value.zoneType !== 'investment') return true
  const store = useUserStore()
  if (!store.realNameApproved) {
    uni.showToast({ title: '请先完成实名认证', icon: 'none' })
    setTimeout(() => uni.navigateTo({ url: '/pages/auth/realname/index' }), 250)
    return false
  }
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
  uni.showToast({ title: '已加入购物车', icon: 'success' })
  return true
}

async function buyNow() {
  if (!requireLogin()) return
  if (product.value.zoneType === 'gift') {
    uni.navigateTo({ url: '/pages/zone/gift/index' })
    return
  }
  if (!ensureInvestmentReady()) return
  const ok = await addCart()
  if (ok) uni.navigateTo({ url: '/pages/checkout/index' })
}

onLoad((o: any) => {
  productId.value = o.id || ''
  if (o.confirmCooperation === '1') cooperationConfirmed.value = true
  load()
})
onShareAppMessage(() => ({
  title: product.value.name || '商品',
  path: '/pages/product/detail/index?id=' + productId.value + '&inviteCode=' + (useUserStore().userInfo?.inviteCode || '')
}))
</script>

<style scoped lang="scss">
.page{min-height:100vh;background:#f6f1e8;color:#24170c;padding-bottom:130rpx}
.hero{height:620rpx;background:#2b1207}
.hero image,.hero-empty{width:100%;height:100%}
.hero-empty{display:flex;align-items:center;justify-content:center;color:#f0bf55;font-size:42rpx}
.card{background:#fff;margin:20rpx;padding:28rpx;border-radius:16rpx;box-shadow:0 8rpx 24rpx rgba(58,32,12,.06)}
.title{display:block;font-size:34rpx;font-weight:900;line-height:1.35}
.price{display:block;color:#b97700;font-weight:900;font-size:48rpx}
.muted{display:block;color:#8d8175;font-size:24rpx;margin-top:12rpx;line-height:1.5}
.tags{display:flex;gap:12rpx;flex-wrap:wrap;margin:18rpx 0}
.tags text{background:#fff4df;color:#8a5b0e;padding:8rpx 14rpx;border-radius:8rpx;font-size:23rpx}
.notice{border-left:8rpx solid #b97700}
.agree{display:flex;align-items:center;margin-top:22rpx;color:#6f6254;font-size:25rpx}
.qty{display:flex;align-items:center;gap:30rpx;margin-top:20rpx}
.qty button{width:70rpx;height:60rpx;border:0;background:#fff4df;color:#8a5b0e;border-radius:10rpx}
.bottom{position:fixed;bottom:0;left:0;right:0;display:grid;grid-template-columns:1fr 1fr;gap:18rpx;padding:18rpx 24rpx;background:#fff;box-shadow:0 -8rpx 24rpx rgba(58,32,12,.06)}
.bottom button{height:88rpx;line-height:88rpx;background:#fff4df;color:#8a5b0e;border:0;border-radius:999rpx}
.bottom button[disabled]{opacity:.45}
.bottom .primary{background:#b97700;color:#fff}
button:after{border:0}
</style>
