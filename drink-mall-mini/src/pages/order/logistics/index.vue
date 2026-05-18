<template>
  <view class="dm-page logistics-page">
    <view class="logistics-card dm-card">
      <view class="truck">运</view>
      <view class="logistics-main">
        <text class="company">{{ detail.company || detail.logisticsCompany || '后台物流' }}</text>
        <text class="muted">{{ detail.trackingNo || detail.expressNo || '暂无运单号' }}</text>
        <text class="muted">{{ itemCountText }} · {{ detail.estimateText || '一期由后台人工录入物流信息' }}</text>
      </view>
      <text v-if="detail.trackingNo || detail.expressNo" class="copy" @click="copyNo">复制</text>
    </view>

    <view class="timeline dm-card">
      <view v-for="(trace, index) in traces" :key="index" class="trace" :class="{ active: index === 0 }">
        <view class="trace-time">
          <text>{{ traceDate(trace) }}</text>
          <text>{{ traceTime(trace) }}</text>
        </view>
        <view class="trace-line">
          <view class="dot"></view>
          <view v-if="index < traces.length - 1" class="line"></view>
        </view>
        <text class="trace-text">{{ trace.content || trace.desc || trace.status || '暂无轨迹' }}</text>
      </view>
      <view v-if="!traces.length" class="empty-track">
        <text class="empty-title">暂无物流轨迹</text>
        <text class="empty-copy">一期由后台人工录入物流信息，暂不接入第三方物流 API。</text>
      </view>
    </view>

    <button class="service" @click="contact">联系客服</button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { orderApi } from '@/services/order'
import { listOf } from '@/utils/format'
import { requirePageLogin } from '@/utils/auth'

const orderId = ref('')
const detail = ref<any>({})
const traces = computed(() => listOf(detail.value.traces || detail.value.items || detail.value.timeline || []))
const itemCountText = computed(() => {
  const count = detail.value.itemCount || detail.value.goodsCount || detail.value.count
  return count ? `${count} 件商品` : '订单商品'
})

async function load() {
  detail.value = (await orderApi.logistics(orderId.value)).data || {}
}

function copyNo() {
  const no = detail.value.trackingNo || detail.value.expressNo
  if (no) uni.setClipboardData({ data: String(no) })
}

function contact() {
  uni.navigateTo({ url: '/pages/help/index' })
}

function traceDate(trace: any) {
  const value = trace.time || trace.createdAt || ''
  if (!value) return '暂无'
  if (trace.date) return trace.date
  const [date] = String(value).split(' ')
  return date?.slice(5) || date
}

function traceTime(trace: any) {
  const value = trace.time || trace.createdAt || ''
  if (!value) return '时间'
  if (trace.clock) return trace.clock
  const parts = String(value).split(' ')
  return (parts[1] || parts[0]).slice(0, 5)
}

onLoad((options: any) => {
  if (!requirePageLogin()) return
  orderId.value = options.orderId || options.id || ''
  load()
})
</script>

<style scoped lang="scss">
.logistics-page {
  padding: 32rpx;
}

.logistics-card {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 28rpx;
}

.truck {
  width: 112rpx;
  height: 112rpx;
  line-height: 112rpx;
  flex: 0 0 112rpx;
  border-radius: 16rpx;
  text-align: center;
  color: var(--dm-gold-500);
  background: var(--dm-cream-100);
  font-weight: 800;
}

.logistics-main {
  flex: 1;
  min-width: 0;
}

.company,
.muted,
.copy,
.trace-time text,
.trace-text,
.empty-title,
.empty-copy {
  display: block;
}

.company {
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 800;
}

.muted {
  margin-top: 6rpx;
  color: var(--dm-text-3);
  font-size: 24rpx;
}

.copy {
  color: var(--dm-gold-500);
  font-size: 24rpx;
}

.timeline {
  margin-top: 20rpx;
  padding: 28rpx 32rpx;
}

.trace {
  display: flex;
  gap: 20rpx;
  min-height: 104rpx;
}

.trace-time {
  width: 96rpx;
  flex: 0 0 96rpx;
  padding-top: 2rpx;
  color: var(--dm-text-3);
  font-size: 22rpx;
  text-align: right;
}

.trace-line {
  width: 28rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 0 0 28rpx;
}

.dot {
  width: 16rpx;
  height: 16rpx;
  margin-top: 8rpx;
  border-radius: 50%;
  background: var(--dm-line);
}

.active .dot {
  width: 28rpx;
  height: 28rpx;
  background: var(--dm-grad-gold);
  box-shadow: 0 0 0 8rpx rgba(228, 165, 22, 0.18);
}

.line {
  flex: 1;
  width: 2rpx;
  min-height: 44rpx;
  margin-top: 8rpx;
  background: var(--dm-line);
}

.trace-text {
  flex: 1;
  min-width: 0;
  padding-bottom: 24rpx;
  color: var(--dm-text-2);
  font-size: 26rpx;
  line-height: 1.55;
}

.active .trace-text {
  color: var(--dm-text-1);
  font-weight: 700;
}

.empty-track {
  padding: 80rpx 20rpx;
  text-align: center;
}

.empty-title {
  color: var(--dm-text-1);
  font-size: 30rpx;
  font-weight: 800;
}

.empty-copy {
  margin-top: 12rpx;
  color: var(--dm-text-3);
  font-size: 24rpx;
  line-height: 1.6;
}

.service {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  margin-top: 24rpx;
  border-radius: 999rpx;
  color: var(--dm-gold-500);
  background: var(--dm-gold-50);
  border: 2rpx solid #F2D78A;
  font-size: 28rpx;
  font-weight: 700;
}

button::after {
  border: 0;
}
</style>
