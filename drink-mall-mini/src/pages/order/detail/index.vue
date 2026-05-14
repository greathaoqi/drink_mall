<template>
  <view class="page">
    <view class="card">
      <text class="title">订单 {{ order.orderNo || order.id }}</text>
      <text class="muted">状态：{{ order.statusText || order.status }}</text>
      <text class="price">¥{{ order.payAmount }}</text>
      <view class="line"></view>
      <text class="muted">物流单号：{{ order.trackingNo || '暂无' }}</text>
    </view>

    <PayMethodSelector v-if="order.status === 'pending'" v-model="payMethod" :methods="methods" title="待付款支付方式" />

    <view class="card actions">
      <button v-if="order.status === 'pending'" class="primary" @click="pay">付款</button>
      <button v-if="order.status === 'pending'" @click="cancel">取消</button>
      <button v-if="order.status === 'shipped'" class="primary" @click="confirm">确认收货</button>
      <button v-if="['paid','shipped','completed'].includes(order.status)" @click="aftersale">申请售后</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import PayMethodSelector from '@/components/PayMethodSelector/PayMethodSelector.vue'
import { orderApi } from '@/services/order'
import { paymentApi } from '@/services/payment'

const id = ref('')
const order = ref<any>({})
const payMethod = ref('')
const methods = ref<any[]>([])
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
  order.value = (await orderApi.detail(id.value)).data || {}
  if (order.value.status === 'pending') {
    const raw = order.value.payMethods || order.value.availablePayMethods
    if (Array.isArray(raw) && raw.length) {
      methods.value = normalizeMethods(raw)
    } else {
      try { methods.value = normalizeMethods((await paymentApi.methods({ scene: 'order', orderId: id.value })).data || []) } catch { methods.value = [] }
    }
    payMethod.value = order.value.payMethod || methods.value.find((m) => !m.disabled)?.value || ''
  }
}

async function pay() {
  if (!payMethod.value || !selectedMethod.value) {
    uni.showToast({ title: '请选择单一支付方式', icon: 'none' })
    return
  }
  if (selectedMethod.value.disabled) {
    uni.showToast({ title: selectedMethod.value.insufficientText || '当前支付方式不可用', icon: 'none' })
    return
  }
  await paymentApi.pay(id.value, payMethod.value)
  uni.showToast({ title: '支付成功', icon: 'success' })
  load()
}
async function cancel() { await orderApi.cancel(id.value); load() }
async function confirm() { await orderApi.confirm(id.value); load() }
function aftersale() { uni.navigateTo({ url: '/pages/aftersale/index?orderId=' + id.value }) }
onLoad((o: any) => { id.value = o.id; load() })
</script>

<style scoped lang="scss">
.page{min-height:100vh;background:#f6f1e8;color:#24170c}
.card{background:#fff;margin:20rpx;padding:28rpx;border-radius:16rpx;box-shadow:0 8rpx 24rpx rgba(58,32,12,.06)}
.title{display:block;font-size:34rpx;font-weight:900}
.muted{display:block;color:#8d8175;font-size:24rpx;margin-top:12rpx}
.price{display:block;color:#b97700;font-weight:900;margin-top:12rpx;font-size:40rpx}
.line{height:1rpx;background:#eee7db;margin:20rpx 0}
.actions{display:grid;grid-template-columns:1fr 1fr;gap:18rpx}
button{height:78rpx;line-height:78rpx;background:#fff4df;color:#8a5b0e;border:0;border-radius:999rpx;font-size:26rpx}
.primary{background:#b97700;color:#fff}
button:after{border:0}
</style>
