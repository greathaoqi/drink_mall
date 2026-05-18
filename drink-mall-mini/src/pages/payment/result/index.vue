<template>
  <view class="dm-page result-page">
    <view class="result-card">
      <view class="icon" :class="{ ok: isSuccess }">
        <uni-icons :type="isSuccess ? 'checkmarkempty' : 'closeempty'" size="42" :color="isSuccess ? '#fff' : '#D6453B'" />
      </view>
      <text class="title">{{ title }}</text>
      <text v-if="amountText" class="amount">{{ amountText }}</text>
      <text class="muted">{{ copy }}</text>
      <text class="order-no">订单号：{{ orderNo }}</text>
    </view>

    <view v-if="isSuccess" class="reward-card">
      <view>
        <uni-icons type="gift" size="20" color="#D38A00" />
        <text>本单权益</text>
      </view>
      <text>佣金、冻结期和到账时间以后端配置及订单状态为准。</text>
    </view>

    <view v-else class="detail-card">
      <view><text>订单号</text><text>{{ orderNo }}</text></view>
      <view><text>支付方式</text><text>{{ payMethodText }}</text></view>
      <view><text>失败原因</text><text>{{ failReason }}</text></view>
    </view>

    <view class="actions">
      <button class="outline" @click="goOrder">查看订单</button>
      <button v-if="isSuccess" class="primary" open-type="share">分享获佣</button>
      <button v-else class="primary" @click="goOrder">重新支付</button>
    </view>
    <button class="home" @click="goHome">返回首页</button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { paymentApi } from '@/services/payment'
import { money } from '@/utils/format'
import { requirePageLogin } from '@/utils/auth'

const orderId = ref('')
const status = ref('submitted')
const result = ref<any>({})
const isSuccess = computed(() => ['success', 'paid', 'submitted'].includes(status.value))
const title = computed(() => status.value === 'submitted' ? '订单已提交' : (isSuccess.value ? '支付成功' : '支付未完成'))
const copy = computed(() => status.value === 'submitted' ? '请在订单详情中完成单一支付方式付款。' : (isSuccess.value ? '订单和资产状态以后端确认为准。' : '可返回订单详情重新发起支付。'))
const amountText = computed(() => {
  const amount = result.value.amount || result.value.payAmount || result.value.totalAmount
  return amount ? '￥' + money(amount) : ''
})
const orderNo = computed(() => result.value.orderNo || orderId.value || '待生成')
const payMethodText = computed(() => result.value.paymentMethodText || result.value.paymentMethod || '未完成')
const failReason = computed(() => result.value.failReason || result.value.message || '余额不足或支付已取消')

async function loadResult() {
  if (!orderId.value) return
  try {
    const data = (await paymentApi.result(orderId.value)).data || {}
    result.value = data
    if (data.status) status.value = data.status
  } catch {}
}

function goOrder() {
  uni.redirectTo({ url: '/pages/order/detail/index?id=' + orderId.value })
}

function goHome() {
  uni.switchTab({ url: '/pages/index/index' })
}

onLoad((options: any) => {
  if (!requirePageLogin()) return
  orderId.value = options.orderId || ''
  status.value = options.status || 'submitted'
  loadResult()
})
</script>

<style scoped lang="scss">
.result-page { padding: 70rpx 28rpx 30rpx; }
.result-card, .reward-card, .detail-card { background: #fff; border-radius: 26rpx; padding: 34rpx 28rpx; box-shadow: var(--dm-shadow-card); }
.result-card { text-align: center; }
.icon { width: 160rpx; height: 160rpx; margin: 0 auto 28rpx; border-radius: 50%; background: #FFE9E6; display: flex; align-items: center; justify-content: center; }
.icon.ok { background: var(--dm-grad-gold); box-shadow: 0 20rpx 60rpx rgba(211,138,0,.32); }
.title { display: block; font-size: 40rpx; font-weight: 900; color: var(--dm-text-1); }
.amount { display: block; color: var(--dm-gold-500); font-size: 58rpx; font-weight: 900; margin-top: 10rpx; }
.muted { display: block; color: var(--dm-text-3); font-size: 25rpx; line-height: 1.6; margin-top: 14rpx; }
.order-no { display: block; margin-top: 14rpx; color: var(--dm-text-3); font-size: 23rpx; }
.reward-card { margin-top: 26rpx; background: var(--dm-gold-50); border: 1rpx solid #F2E0B0; color: #7A5610; font-size: 24rpx; line-height: 1.6; }
.reward-card view { display: flex; align-items: center; gap: 10rpx; font-size: 27rpx; font-weight: 800; margin-bottom: 10rpx; }
.detail-card { margin-top: 26rpx; }
.detail-card view { display: flex; justify-content: space-between; gap: 20rpx; padding: 10rpx 0; color: var(--dm-text-3); font-size: 25rpx; }
.detail-card text:last-child { color: var(--dm-text-1); text-align: right; }
.actions { display: flex; gap: 18rpx; margin-top: 34rpx; }
button { height: 84rpx; line-height: 84rpx; border-radius: 999rpx; border: 0; font-weight: 800; }
.actions button { flex: 1; }
.outline { background: var(--dm-gold-50); color: var(--dm-gold-600); border: 1rpx solid #F2D78A; }
.primary { background: var(--dm-grad-gold); color: #3A1A00; }
.home { width: 100%; margin-top: 20rpx; background: transparent; color: var(--dm-text-3); }
button:after { border: 0; }
</style>
