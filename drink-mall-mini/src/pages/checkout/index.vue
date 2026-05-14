<template>
  <view class="page">
    <view class="card address" @click="selectAddress">
      <view>
        <text class="title">收货地址</text>
        <view v-if="address" class="muted">{{ address.name }} {{ address.phone }}\n{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</view>
        <view v-else class="muted">请选择收货地址</view>
      </view>
      <uni-icons type="right" size="18" color="#b97700" />
    </view>

    <view class="card" v-for="i in items" :key="i.cartId || i.productId">
      <text class="title item-title">{{ i.productName || i.name }}</text>
      <text class="muted">数量 x{{ i.quantity }}</text>
      <text class="price">¥{{ i.price }}</text>
    </view>

    <PayMethodSelector v-model="payMethod" :methods="methods" title="支付方式" />

    <view class="card">
      <text class="muted">资产独立记账，禁止组合支付；请选择一种后端允许的支付方式。</text>
      <view class="line"></view>
      <view class="total-row">
        <text>应付</text>
        <text class="price total">¥{{ total }}</text>
      </view>
    </view>

    <button class="submit primary" :loading="submitting" @click="submit">提交订单</button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import PayMethodSelector from '@/components/PayMethodSelector/PayMethodSelector.vue'
import { addressApi } from '@/services/address'
import { cartApi } from '@/services/cart'
import { orderApi } from '@/services/order'
import { paymentApi } from '@/services/payment'
import { listOf, money } from '@/utils/format'

const items = ref<any[]>([])
const ids = ref<number[]>([])
const address = ref<any>(null)
const payMethod = ref('')
const methods = ref<any[]>([])
const submitting = ref(false)

const total = computed(() => money(items.value.reduce((s, i) => s + Number(i.price || 0) * Number(i.quantity || 1), 0)))
const selectedMethod = computed(() => methods.value.find((m) => m.value === payMethod.value))

function normalizeMethods(raw: any[]) {
  return raw.map((m) => ({
    value: m.value || m.code || m.payMethod || m.method,
    label: m.label || m.name || m.title || m.value || m.code,
    tip: m.tip || m.description,
    disabled: Boolean(m.disabled || m.available === false),
    insufficientText: m.insufficientText || m.unavailableReason || m.reason
  })).filter((m) => m.value)
}

async function load() {
  const cart = await cartApi.list()
  const all = listOf<any>(cart.data)
  items.value = ids.value.length ? all.filter((i) => ids.value.includes(Number(i.cartId))) : all.filter((i) => i.selected)
  try { address.value = (await addressApi.default()).data } catch {}
  try {
    methods.value = normalizeMethods((await paymentApi.methods({ scene: 'order' })).data || [])
  } catch {
    methods.value = normalizeMethods([
      { value: 'balance', label: '余额支付' },
      { value: 'wineBean', label: '酒豆支付' },
      { value: 'wechat', label: '微信支付' }
    ])
  }
  if (!methods.value.find((m) => m.value === payMethod.value && !m.disabled)) {
    payMethod.value = methods.value.find((m) => !m.disabled)?.value || ''
  }
}

function selectAddress() {
  uni.navigateTo({ url: '/pages/address/list/index?select=1' })
}

async function submit() {
  if (submitting.value) return
  if (!items.value.length) {
    uni.showToast({ title: '请选择要结算的商品', icon: 'none' })
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
  submitting.value = true
  try {
    const res = await orderApi.create({
      addressId: address.value.id,
      payMethod: payMethod.value,
      items: items.value.map((i) => ({ cartId: i.cartId, productId: i.productId, quantity: i.quantity }))
    })
    uni.redirectTo({ url: '/pages/order/detail/index?id=' + (res.data.id || res.data.orderId) })
  } finally {
    submitting.value = false
  }
}

onLoad((o: any) => {
  if (o.ids) ids.value = String(o.ids).split(',').map(Number)
  uni.$on('addressSelected', (a: any) => { address.value = a })
})
onShow(load)
</script>

<style scoped lang="scss">
.page{min-height:100vh;background:#f6f1e8;color:#24170c;padding-bottom:36rpx}
.card{background:#fff;margin:20rpx;padding:28rpx;border-radius:16rpx;box-shadow:0 8rpx 24rpx rgba(58,32,12,.06)}
.address{display:flex;align-items:center;justify-content:space-between;gap:20rpx}
.title{display:block;font-size:34rpx;font-weight:900}
.item-title{line-height:1.35}
.muted{display:block;color:#8d8175;font-size:24rpx;margin-top:12rpx;line-height:1.5;white-space:pre-line}
.price{color:#b97700;font-weight:900}
.line{height:1rpx;background:#eee7db;margin:20rpx 0}
.total-row{display:flex;align-items:center;justify-content:space-between}
.total{font-size:42rpx}
.submit{margin:30rpx 20rpx;height:88rpx;line-height:88rpx;border:0;border-radius:999rpx}
.primary{background:#b97700;color:#fff}
button:after{border:0}
</style>
