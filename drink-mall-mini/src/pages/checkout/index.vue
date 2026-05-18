<template>
  <view class="dm-page checkout-page">
    <view class="address-card" @click="selectAddress">
      <uni-icons type="location-filled" size="24" color="#D38A00" />
      <view class="address-main">
        <view v-if="address">
          <text class="address-name">{{ address.name }} <text>{{ address.phone }}</text></text>
          <text class="address-detail"><text v-if="address.isDefault" class="default-tag">默认</text>{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</text>
        </view>
        <view v-else>
          <text class="address-name">请选择收货地址</text>
          <text class="address-detail">用于配送和售后联系</text>
        </view>
      </view>
      <uni-icons type="right" size="16" color="#C9BFA9" />
    </view>

    <view class="goods-card">
      <view class="card-title">商品 · 共 {{ items.length }} 件</view>
      <view v-for="i in items" :key="i.cartId || i.productId" class="goods-row">
        <image class="item-image" :src="i.productImage || i.mainImage || '/static/images/product-wine.png'" mode="aspectFill" />
        <view class="item-info">
          <text class="item-title">{{ i.productName || i.name }}</text>
          <text class="item-sub">{{ i.skuName || i.zoneName || '默认规格' }}</text>
        </view>
        <view class="item-price">
          <text>{{ isGiftItem(i) ? giftPointsPrice(i) + ' 积分' : '￥' + money(i.price) }}</text>
          <text>x{{ i.quantity }}</text>
        </view>
      </view>
    </view>

    <view v-if="hasGiftAndNormal" class="warn-card">
      <text>礼包专区仅支持纯积分兑换，请与普通商品分开提交。</text>
    </view>

    <PayMethodSelector v-model="payMethod" :methods="methods" title="选择支付方式" />

    <view class="goods-card form-card">
      <view class="form-row">
        <text>备注</text>
        <input v-model="remark" placeholder="选填，请留言" />
      </view>
      <view class="form-row">
        <text>资产说明</text>
        <text>余额、DF、酒豆、积分独立记账，不支持组合支付。</text>
      </view>
    </view>

    <view class="goods-card amount-card">
      <view class="amount-line"><text>商品金额</text><text>￥{{ cashTotal }}</text></view>
      <view v-if="pointsTotal !== '0.00'" class="amount-line"><text>积分兑换</text><text>{{ pointsTotal }} 积分</text></view>
      <view class="amount-line"><text>运费</text><text>免运费</text></view>
      <view class="amount-line total-line"><text>应付</text><text>{{ hasGift ? pointsTotal + ' 积分' : '￥' + cashTotal }}</text></view>
    </view>

    <BottomActionBar>
      <template #left>
        <view class="pay-total">
          <text>合计应付</text>
          <text>{{ hasGift ? pointsTotal + ' 积分' : '￥' + cashTotal }}</text>
        </view>
      </template>
      <button class="submit" :loading="submitting" @click="submit">提交订单</button>
    </BottomActionBar>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import BottomActionBar from '@/components/BottomActionBar/BottomActionBar.vue'
import PayMethodSelector from '@/components/PayMethodSelector/PayMethodSelector.vue'
import { addressApi } from '@/services/address'
import { cartApi } from '@/services/cart'
import { orderApi } from '@/services/order'
import { paymentApi } from '@/services/payment'
import { productApi } from '@/services/product'
import { canUseSinglePayMethod, giftPointsPrice, isGiftItem, isInvestmentItem, normalizePayMethods } from '@/utils/business'
import { listOf, money } from '@/utils/format'
import { requirePageLogin, requireRealName } from '@/utils/auth'
import { useUserStore } from '@/store/user'

const items = ref<any[]>([])
const ids = ref<number[]>([])
const directProductId = ref('')
const directQuantity = ref(1)
const address = ref<any>(null)
const payMethod = ref('')
const methods = ref<any[]>([])
const submitting = ref(false)
const remark = ref('')

const hasGift = computed(() => items.value.some(isGiftItem))
const hasNormal = computed(() => items.value.some((item) => !isGiftItem(item)))
const hasGiftAndNormal = computed(() => hasGift.value && hasNormal.value)
const hasInvestment = computed(() => items.value.some(isInvestmentItem))
const cashTotal = computed(() => money(items.value.reduce((s, i) => s + Number(isGiftItem(i) ? 0 : (i.price || 0)) * Number(i.quantity || 1), 0)))
const pointsTotal = computed(() => money(items.value.reduce((s, i) => s + Number(isGiftItem(i) ? giftPointsPrice(i) : 0) * Number(i.quantity || 1), 0)))
const selectedMethod = computed(() => methods.value.find((m) => m.value === payMethod.value))

async function load() {
  if (!requirePageLogin()) return
  if (directProductId.value) {
    const product = (await productApi.detail(directProductId.value)).data || {}
    items.value = [{ ...product, productId: product.id || directProductId.value, productName: product.name, quantity: directQuantity.value }]
  } else {
    const cart = await cartApi.list()
    const all = listOf<any>(cart.data)
    items.value = ids.value.length ? all.filter((i) => ids.value.includes(Number(i.cartId))) : all.filter((i) => i.selected)
  }
  try { address.value = (await addressApi.default()).data } catch {}
  try {
    methods.value = normalizePayMethods((await paymentApi.methods({ scene: 'order', productId: directProductId.value || undefined })).data || [])
  } catch {
    methods.value = normalizePayMethods([
      { value: 'balance', label: '余额支付' },
      { value: 'wineBean', label: '酒豆支付' },
      { value: 'points', label: '积分兑换' },
      { value: 'wechat', label: '微信支付' }
    ])
  }
  if (hasGift.value) {
    methods.value = methods.value.filter((m) => m.value === 'points')
    if (!methods.value.length) methods.value = [{ value: 'points', label: '积分兑换', tip: '礼包专区纯积分支付' }]
  }
  if (!methods.value.find((m) => m.value === payMethod.value && !m.disabled)) {
    payMethod.value = methods.value.find((m) => !m.disabled)?.value || ''
  }
}

function selectAddress() {
  uni.navigateTo({ url: '/pages/address/list/index?select=1' })
}

async function submit() {
  if (!requirePageLogin()) return
  if (submitting.value) return
  if (!items.value.length) {
    uni.showToast({ title: '请选择要结算的商品', icon: 'none' })
    return
  }
  if (hasGiftAndNormal.value) {
    uni.showToast({ title: '礼包需单独积分兑换', icon: 'none' })
    return
  }
  if (hasInvestment.value && !requireRealName('招商购买前请先完成实名认证')) return
  if (hasGift.value && Number(useUserStore().userInfo?.points || 0) < Number(pointsTotal.value)) {
    uni.showToast({ title: '积分不足，不能提交', icon: 'none' })
    return
  }
  if (!address.value) {
    uni.showToast({ title: '请选择收货地址', icon: 'none' })
    return
  }
  if (!payMethod.value || !selectedMethod.value) {
    uni.showToast({ title: '请选择单一支付方式', icon: 'none' })
    return
  }
  if (selectedMethod.value.disabled) {
    uni.showToast({ title: selectedMethod.value.insufficientText || '当前支付方式不可用', icon: 'none' })
    return
  }
  if (!canUseSinglePayMethod(items.value, payMethod.value)) {
    uni.showToast({ title: '当前商品仅允许指定单一支付方式', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const res = await orderApi.create({
      addressId: address.value.id,
      paymentMethod: payMethod.value,
      remark: remark.value,
      items: items.value.map((i) => ({ cartId: i.cartId, productId: i.productId, quantity: i.quantity }))
    })
    const orderId = res.data.id || res.data.orderId
    uni.redirectTo({ url: '/pages/payment/result/index?status=submitted&orderId=' + orderId })
  } finally {
    submitting.value = false
  }
}

onLoad((o: any) => {
  if (o.ids) ids.value = String(o.ids).split(',').map(Number)
  if (o.productId) directProductId.value = String(o.productId)
  if (o.quantity) directQuantity.value = Math.max(1, Number(o.quantity))
  uni.$on('addressSelected', (a: any) => { address.value = a })
})
onShow(load)
</script>

<style scoped lang="scss">
.checkout-page { padding: 16rpx 0 calc(150rpx + env(safe-area-inset-bottom)); }
.address-card, .goods-card, .warn-card { background: #fff; margin: 20rpx; border-radius: 24rpx; box-shadow: var(--dm-shadow-card); }
.address-card { padding: 28rpx; display: flex; align-items: flex-start; gap: 20rpx; }
.address-main { flex: 1; min-width: 0; }
.address-name, .address-detail { display: block; }
.address-name { font-size: 28rpx; font-weight: 800; color: var(--dm-text-1); }
.address-name text { margin-left: 12rpx; font-weight: 400; color: var(--dm-text-2); }
.address-detail { margin-top: 10rpx; color: var(--dm-text-2); font-size: 25rpx; line-height: 1.5; }
.default-tag { margin-right: 8rpx; padding: 2rpx 10rpx; border-radius: 6rpx; background: var(--dm-gold-50); color: var(--dm-gold-600); font-size: 21rpx; }
.goods-card { padding: 24rpx 28rpx; }
.card-title { font-size: 27rpx; font-weight: 800; margin-bottom: 12rpx; }
.goods-row { display: flex; gap: 18rpx; padding: 16rpx 0; border-top: 1rpx solid var(--dm-line-soft); }
.goods-row:first-of-type { border-top: 0; }
.item-image { width: 120rpx; height: 120rpx; border-radius: 12rpx; background: var(--dm-cream-100); flex: 0 0 120rpx; }
.item-info { min-width: 0; flex: 1; }
.item-title { display: block; font-size: 26rpx; font-weight: 700; color: var(--dm-text-1); line-height: 1.4; }
.item-sub { display: block; color: var(--dm-text-3); font-size: 22rpx; margin-top: 6rpx; }
.item-price { text-align: right; color: var(--dm-text-3); font-size: 22rpx; }
.item-price text:first-child { display: block; color: var(--dm-gold-500); font-size: 26rpx; font-weight: 900; }
.warn-card { padding: 22rpx 28rpx; border-left: 8rpx solid var(--dm-gold-500); color: #7A5610; font-size: 24rpx; background: var(--dm-gold-50); }
.form-row, .amount-line { display: flex; align-items: center; justify-content: space-between; gap: 18rpx; min-height: 64rpx; font-size: 25rpx; color: var(--dm-text-2); }
.form-row input { flex: 1; text-align: right; }
.form-row text:last-child { flex: 1; text-align: right; line-height: 1.4; }
.amount-line text:last-child { color: var(--dm-text-1); font-weight: 700; }
.total-line { margin-top: 12rpx; padding-top: 16rpx; border-top: 1rpx solid var(--dm-line-soft); }
.total-line text:last-child { color: var(--dm-gold-500); font-size: 38rpx; font-weight: 900; }
.pay-total text { display: block; font-size: 22rpx; color: var(--dm-text-3); }
.pay-total text:last-child { color: var(--dm-gold-500); font-size: 38rpx; font-weight: 900; }
.submit { width: 240rpx; background: var(--dm-grad-gold); color: #3A1A00; }
button:after { border: 0; }
</style>
