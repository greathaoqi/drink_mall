<template>
  <view class="dm-page detail-page">
    <view class="hero">
      <text class="hero-title">{{ detail.statusText || statusLabel(detail.status) }}</text>
      <text class="hero-copy">客服正在审核您的售后申请，预计 24 小时内回复。</text>
    </view>

    <view class="content">
      <view class="progress dm-card">
        <view v-for="(step, index) in steps" :key="step.label" class="step" :class="{ active: step.active, current: step.current }">
          <view v-if="index < steps.length - 1" class="connector" :class="{ active: step.active }"></view>
          <view class="circle">{{ step.active ? '✓' : '' }}</view>
          <text class="step-label">{{ step.label }}</text>
          <text class="step-time">{{ step.time }}</text>
        </view>
      </view>

      <view class="goods-card dm-card">
        <image class="thumb" :src="item.productImage || item.mainImage || '/static/images/product-wine.png'" mode="aspectFill" />
        <view class="goods-main">
          <text class="goods-name">{{ item.productName || item.name || detail.productName || '订单商品' }}</text>
          <text class="goods-spec">{{ item.specName || item.skuName || '500ml 单瓶' }} x{{ item.quantity || 1 }}</text>
        </view>
        <text class="price">￥{{ money(item.price || detail.refundAmount || detail.amount) }}</text>
      </view>

      <view class="info-card dm-card">
        <view v-for="row in infoRows" :key="row.label" class="info-row">
          <text class="label">{{ row.label }}</text>
          <text class="value">{{ row.value }}</text>
        </view>
        <text class="desc-label">问题描述</text>
        <text class="desc">{{ detail.description || detail.reason || '售后申请已提交，后续由后台人工审核和线下处理。' }}</text>
        <text v-if="detail.auditRemark || detail.remark" class="desc-label">审核说明</text>
        <text v-if="detail.auditRemark || detail.remark" class="desc">{{ detail.auditRemark || detail.remark }}</text>
      </view>

      <view v-if="timeline.length" class="info-card dm-card">
        <text class="section-title">处理记录</text>
        <view v-for="(record, index) in timeline" :key="index" class="record">
          <text class="record-title">{{ record.title || record.statusText || record.status || '进度' }}</text>
          <text class="record-copy">{{ record.createdAt || record.time || '' }} {{ record.remark || record.content || '' }}</text>
        </view>
      </view>

      <view class="actions">
        <button class="light" @click="withdraw">撤销售后</button>
        <button class="primary" @click="contact">联系客服</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { aftersaleApi } from '@/services/aftersale'
import { listOf, money } from '@/utils/format'
import { requirePageLogin } from '@/utils/auth'

const id = ref('')
const orderId = ref('')
const detail = ref<any>({})
const timeline = computed(() => listOf(detail.value.timeline || detail.value.logs || []))
const item = computed(() => {
  const items = detail.value.items || detail.value.orderItems || []
  return items[0] || {}
})
const steps = computed(() => {
  const status = detail.value.status || 'pending'
  const approved = ['approved', 'refund_pending', 'completed'].includes(status)
  const completed = ['completed', 'closed'].includes(status)
  return [
    { label: '提交申请', time: formatShort(detail.value.createdAt || detail.value.applyTime) || '已提交', active: true },
    { label: '商家审核', time: approved || completed ? '已通过' : '审核中', active: true, current: !approved && !completed },
    { label: '退款到账', time: completed ? '已完成' : '待处理', active: completed, current: approved && !completed }
  ]
})
const infoRows = computed(() => [
  { label: '服务单号', value: detail.value.serviceNo || detail.value.aftersaleNo || detail.value.id || id.value || '暂无' },
  { label: '售后类型', value: detail.value.typeText || typeLabel(detail.value.type) },
  { label: '退款金额', value: `￥${money(detail.value.refundAmount || detail.value.amount || 0)}` },
  { label: '申请时间', value: detail.value.createdAt || detail.value.applyTime || '暂无' },
  { label: '退款原因', value: detail.value.reason || '暂无' }
])

function statusLabel(status: string) {
  const map: Record<string, string> = {
    applied: '退款处理中',
    pending: '退款处理中',
    approved: '审核已通过',
    rejected: '审核未通过',
    completed: '退款已完成',
    closed: '售后已关闭'
  }
  return map[status] || '退款处理中'
}

function typeLabel(type: string) {
  if (type === 'exchange') return '换货'
  if (type === 'return_refund') return '退货退款'
  return '仅退款'
}

function formatShort(value: string) {
  if (!value) return ''
  const [date, time] = String(value).split(' ')
  return `${date?.slice(5) || ''} ${time?.slice(0, 5) || ''}`.trim()
}

async function load() {
  if (id.value) {
    detail.value = (await aftersaleApi.detail(id.value)).data || {}
  } else if (orderId.value) {
    const list = listOf((await aftersaleApi.list({ orderId: orderId.value })).data)
    detail.value = list[0] || { orderId: orderId.value, status: 'pending' }
  }
}

function contact() {
  uni.navigateTo({ url: '/pages/help/index' })
}

function withdraw() {
  uni.showToast({ title: '撤销需联系客服处理', icon: 'none' })
}

onLoad((options: any) => {
  if (!requirePageLogin()) return
  id.value = options.id || ''
  orderId.value = options.orderId || ''
  load()
})
</script>

<style scoped lang="scss">
.detail-page {
  padding-bottom: 32rpx;
}

.hero {
  min-height: 188rpx;
  padding: 32rpx 40rpx 54rpx;
  color: var(--dm-text-on-brown);
  background: var(--dm-grad-brown);
}

.hero-title,
.hero-copy,
.step-label,
.step-time,
.goods-name,
.goods-spec,
.price,
.label,
.value,
.desc-label,
.desc,
.section-title,
.record-title,
.record-copy {
  display: block;
}

.hero-title {
  color: #FFE0A0;
  font-size: 36rpx;
  font-weight: 800;
}

.hero-copy {
  margin-top: 8rpx;
  color: rgba(246, 231, 194, 0.72);
  font-size: 24rpx;
  line-height: 1.5;
}

.content {
  padding: 0 32rpx;
}

.progress {
  display: flex;
  align-items: flex-start;
  margin-top: -22rpx;
  padding: 32rpx 24rpx;
}

.step {
  position: relative;
  flex: 1;
  text-align: center;
}

.connector {
  position: absolute;
  top: 24rpx;
  left: 60%;
  right: -40%;
  height: 4rpx;
  background: var(--dm-line);
}

.connector.active {
  background: var(--dm-gold-500);
}

.circle {
  position: relative;
  z-index: 1;
  width: 52rpx;
  height: 52rpx;
  line-height: 52rpx;
  margin: 0 auto;
  border-radius: 50%;
  color: #FFFFFF;
  background: var(--dm-line);
  font-size: 24rpx;
  font-weight: 800;
}

.active .circle {
  background: var(--dm-gold-500);
}

.current .circle {
  background: var(--dm-grad-gold);
  box-shadow: 0 0 0 10rpx rgba(228, 165, 22, 0.18);
}

.step-label {
  margin-top: 12rpx;
  color: var(--dm-text-1);
  font-size: 24rpx;
  font-weight: 700;
}

.step-time {
  margin-top: 4rpx;
  color: var(--dm-text-3);
  font-size: 20rpx;
}

.goods-card,
.info-card {
  margin-top: 20rpx;
  padding: 28rpx;
}

.goods-card {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
}

.thumb {
  width: 120rpx;
  height: 120rpx;
  flex: 0 0 120rpx;
  border-radius: 12rpx;
  background: var(--dm-cream-100);
}

.goods-main {
  flex: 1;
  min-width: 0;
}

.goods-name {
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 700;
}

.goods-spec,
.label,
.desc-label,
.record-copy {
  color: var(--dm-text-3);
  font-size: 24rpx;
}

.goods-spec {
  margin-top: 8rpx;
}

.price {
  color: var(--dm-gold-500);
  font-size: 28rpx;
  font-weight: 800;
}

.info-row {
  display: flex;
  gap: 20rpx;
  min-height: 54rpx;
}

.label {
  width: 148rpx;
}

.value {
  flex: 1;
  min-width: 0;
  color: var(--dm-text-1);
  font-size: 26rpx;
}

.desc-label {
  margin-top: 18rpx;
}

.desc {
  margin-top: 12rpx;
  padding: 20rpx 24rpx;
  color: var(--dm-text-2);
  background: var(--dm-bg-app);
  border-radius: 12rpx;
  font-size: 24rpx;
  line-height: 1.6;
}

.section-title {
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 800;
}

.record {
  padding: 20rpx 0;
  border-bottom: 2rpx solid var(--dm-line-soft);
}

.record:last-child {
  border-bottom: 0;
}

.record-title {
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 700;
}

.record-copy {
  margin-top: 8rpx;
  line-height: 1.5;
}

.actions {
  display: flex;
  gap: 20rpx;
  margin-top: 28rpx;
}

.actions button {
  flex: 1;
  height: 84rpx;
  line-height: 84rpx;
  border-radius: 999rpx;
  font-size: 28rpx;
  font-weight: 800;
}

.light {
  color: var(--dm-gold-500);
  background: var(--dm-gold-50);
  border: 2rpx solid #F2D78A;
}

.primary {
  color: #3A1A00;
  background: var(--dm-grad-gold);
  box-shadow: 0 10rpx 28rpx rgba(211, 138, 0, 0.25);
}

button::after {
  border: 0;
}
</style>
