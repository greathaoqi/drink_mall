<template>
  <view class="page">
    <view class="card">
      <text class="title">{{ detail.title }}</text>
      <view class="meta">
        <text>{{ detail.type === 'video' ? '视频' : '图文' }}</text>
        <text>{{ detail.levelText || '等级权限以后端为准' }}</text>
      </view>
      <video v-if="detail.type === 'video' && canView" :src="detail.videoUrl" controls class="video" />
      <rich-text v-if="detail.type !== 'video' && canView" :nodes="detail.content || ''" />
      <view v-if="canView && detail.paid" class="paid">已购买，后续免重复购买</view>
      <view v-if="!canView" class="locked">
        <text class="muted">当前等级或购买状态不可观看</text>
        <PayMethodSelector v-if="detail.price && !detail.paid" v-model="payMethod" :methods="methods" title="内容支付方式" />
        <button class="primary" v-if="detail.price && !detail.paid" @click="buy">购买内容</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import PayMethodSelector from '@/components/PayMethodSelector/PayMethodSelector.vue'
import { contentApi } from '@/services/content'
import { paymentApi } from '@/services/payment'
import { requireLogin } from '@/utils/auth'

const id = ref('')
const detail = ref<any>({})
const payMethod = ref('')
const methods = ref<any[]>([])
const canView = computed(() => detail.value.canView || detail.value.paid || !detail.value.price)
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
  detail.value = (await contentApi.detail(id.value)).data || {}
  if (detail.value.price && !detail.value.paid && !canView.value) {
    const raw = detail.value.payMethods || detail.value.availablePayMethods
    if (Array.isArray(raw) && raw.length) {
      methods.value = normalizeMethods(raw)
    } else {
      try { methods.value = normalizeMethods((await paymentApi.methods({ scene: 'content', contentId: id.value })).data || []) } catch { methods.value = [] }
    }
    payMethod.value = methods.value.find((m) => !m.disabled)?.value || ''
  }
}

async function buy() {
  if (!requireLogin()) return
  if (detail.value.paid) {
    uni.showToast({ title: '已购买，无需重复购买', icon: 'none' })
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
  await contentApi.buy(id.value, payMethod.value)
  uni.showToast({ title: '购买成功', icon: 'success' })
  load()
}
onLoad((o: any) => { id.value = o.id; load() })
</script>

<style scoped lang="scss">
.page{min-height:100vh;background:#f6f1e8;color:#24170c}
.card{background:#fff;margin:20rpx;padding:28rpx;border-radius:16rpx;box-shadow:0 8rpx 24rpx rgba(58,32,12,.06)}
.title{display:block;font-size:34rpx;font-weight:900;line-height:1.35}
.meta{display:flex;gap:12rpx;flex-wrap:wrap;margin:18rpx 0}
.meta text,.paid{background:#fff4df;color:#8a5b0e;padding:8rpx 14rpx;border-radius:8rpx;font-size:23rpx}
.muted{display:block;color:#8d8175;font-size:24rpx;margin-top:12rpx;line-height:1.5}
.video{width:100%;height:420rpx;margin-top:20rpx;border-radius:12rpx;overflow:hidden}
.locked{margin-top:20rpx}
.paid{display:inline-flex;margin-top:20rpx}
button{margin-top:24rpx;height:78rpx;line-height:78rpx;width:100%;border:0;border-radius:999rpx}
.primary{background:#b97700;color:#fff}
button:after{border:0}
</style>
