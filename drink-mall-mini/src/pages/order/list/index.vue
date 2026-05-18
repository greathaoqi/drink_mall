<template>
  <view class="dm-page order-page">
    <view class="tabs-wrap">
      <scroll-view scroll-x class="tabs-scroll" show-scrollbar="false">
        <view class="tabs">
          <view
            v-for="tab in statusTabs"
            :key="tab.value"
            class="tab dm-press"
            :class="{ active: status === tab.value }"
            @click="setStatus(tab.value)"
          >
            <text>{{ tab.label }}</text>
            <text v-if="tab.count" class="badge">{{ tab.count }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <view v-if="summaryText" class="summary" :class="summaryClass">
      <text class="summary-icon">{{ summaryIcon }}</text>
      <text>{{ summaryText }}</text>
    </view>

    <PageState
      :loading="loading"
      :empty="orders.length === 0"
      empty-text="暂无订单"
      empty-tip="下单后可以在这里查看订单详情和物流"
    >
      <view class="orders">
        <view v-for="order in orders" :key="order.id || order.orderNo" class="order-card dm-card dm-press" @click="go(order)">
          <view class="order-head">
            <text class="order-no">订单号 {{ shortNo(order) }}</text>
            <text class="order-status" :class="'status-' + normalizedStatus(order.status)">
              {{ order.statusText || statusLabel(order.status) }}
            </text>
          </view>

          <view class="goods-list">
            <view v-for="item in displayItems(order)" :key="item.id || item.productId || item.productName" class="goods-row">
              <image class="thumb" :src="itemImage(item)" mode="aspectFill" />
              <view class="goods-main">
                <text class="goods-name">{{ item.productName || item.name || '订单商品' }}</text>
                <text class="goods-spec">{{ item.specName || item.skuName || '500ml 单瓶' }}</text>
              </view>
              <view class="goods-side">
                <text class="price">￥{{ money(item.price || item.payAmount || item.salePrice) }}</text>
                <text class="qty">x{{ item.quantity || 1 }}</text>
              </view>
            </view>
          </view>

          <view class="order-total">
            <text class="time">{{ orderHint(order) }}</text>
            <text class="total">共 {{ itemCount(order) }} 件 · 实付 <text class="amount">￥{{ money(order.payAmount || order.totalAmount) }}</text></text>
          </view>

          <view class="actions">
            <button v-for="action in actionsFor(order)" :key="action.key" :class="{ primary: action.primary }" @click.stop="handleAction(action.key, order)">
              {{ action.label }}
            </button>
          </view>
        </view>
      </view>
    </PageState>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import PageState from '@/components/PageState/PageState.vue'
import { orderApi } from '@/services/order'
import { listOf, money } from '@/utils/format'
import { requirePageLogin } from '@/utils/auth'

type TabValue = '' | 'pending' | 'paid' | 'shipped' | 'completed' | 'aftersale'

const status = ref<TabValue>('')
const orders = ref<any[]>([])
const loading = ref(false)

const statusTabs = computed(() => [
  { label: '全部', value: '' },
  { label: '待付款', value: 'pending', count: countByStatus('pending') },
  { label: '待发货', value: 'paid', count: countByStatus('paid') },
  { label: '待收货', value: 'shipped', count: countByStatus('shipped') },
  { label: '已完成', value: 'completed' },
  { label: '售后', value: 'aftersale' }
])

const summaryText = computed(() => {
  const count = orders.value.length
  if (status.value === 'paid') return `${count} 个订单等待发货 · 平台承诺 48 小时内出库`
  if (status.value === 'shipped') return `${count} 个订单运输中 · 可查看物流或确认收货`
  if (status.value === 'completed') return `已完成订单可再次购买或申请售后`
  return ''
})
const summaryIcon = computed(() => {
  if (status.value === 'paid') return '箱'
  if (status.value === 'shipped') return '运'
  if (status.value === 'completed') return '✓'
  return ''
})
const summaryClass = computed(() => ({
  ship: status.value === 'paid',
  receive: status.value === 'shipped',
  done: status.value === 'completed'
}))

async function load() {
  if (!requirePageLogin()) return
  loading.value = true
  try {
    orders.value = listOf((await orderApi.list({ status: status.value })).data)
  } finally {
    loading.value = false
  }
}

function setStatus(value: string) {
  status.value = value as TabValue
  load()
}

function go(order: any) {
  uni.navigateTo({ url: '/pages/order/detail/index?id=' + (order.id || order.orderId) })
}

function aftersale(order: any) {
  uni.navigateTo({ url: '/pages/aftersale/index?orderId=' + (order.id || order.orderId) })
}

function logistics(order: any) {
  uni.navigateTo({ url: '/pages/order/logistics/index?orderId=' + (order.id || order.orderId) })
}

async function confirm(order: any) {
  await orderApi.confirm(order.id || order.orderId)
  uni.showToast({ title: '已确认收货', icon: 'success' })
  load()
}

function handleAction(key: string, order: any) {
  if (key === 'pay' || key === 'detail') go(order)
  if (key === 'logistics') logistics(order)
  if (key === 'confirm') confirm(order)
  if (key === 'aftersale') aftersale(order)
  if (key === 'again') uni.switchTab({ url: '/pages/product/list' })
  if (key === 'service') uni.navigateTo({ url: '/pages/help/index' })
}

function normalizedStatus(value: string) {
  if (['unpaid', 'wait_pay'].includes(value)) return 'pending'
  if (['wait_ship', 'paid'].includes(value)) return 'paid'
  if (['wait_receive', 'delivered', 'shipping'].includes(value)) return 'shipped'
  if (['done', 'finished'].includes(value)) return 'completed'
  return value || 'unknown'
}

function statusLabel(value: string) {
  const map: Record<string, string> = {
    pending: '待付款',
    paid: '待发货',
    shipped: '待收货',
    completed: '已完成',
    cancelled: '已取消',
    aftersale: '售后中'
  }
  return map[normalizedStatus(value)] || value || '订单'
}

function countByStatus(value: string) {
  const count = orders.value.filter((order) => normalizedStatus(order.status) === value).length
  return count > 0 ? count : 0
}

function itemsOf(order: any) {
  return order.items || order.orderItems || []
}

function displayItems(order: any) {
  const items = itemsOf(order)
  if (items.length) return items
  return [{ productName: order.productName || '订单商品', productImage: order.productImage, price: order.payAmount || order.totalAmount, quantity: order.quantity || 1 }]
}

function itemImage(item: any) {
  return item.productImage || item.mainImage || '/static/images/product-wine.png'
}

function shortNo(order: any) {
  const no = String(order.orderNo || order.id || '')
  return no.length > 18 ? no.slice(0, 18) + '...' : no
}

function itemCount(order: any) {
  const items = itemsOf(order)
  if (!items.length) return Number(order.quantity || 1)
  return items.reduce((sum: number, item: any) => sum + Number(item.quantity || 1), 0)
}

function orderHint(order: any) {
  if (order.countdownText) return order.countdownText
  const state = normalizedStatus(order.status)
  if (state === 'pending') return order.expireText || '待付款 · 请尽快完成支付'
  if (state === 'paid') return order.paidAt ? `已付款 · ${order.paidAt}` : '已付款 · 备货中'
  if (state === 'shipped') return order.logisticsCompany ? `${order.logisticsCompany} · 配送中` : '已发货 · 配送中'
  if (state === 'completed') return order.commissionText || '交易完成'
  return order.createdAt || ''
}

function actionsFor(order: any) {
  const state = normalizedStatus(order.status)
  if (state === 'pending') return [{ key: 'detail', label: '取消订单' }, { key: 'pay', label: '立即支付', primary: true }]
  if (state === 'paid') return [{ key: 'service', label: '联系客服' }, { key: 'detail', label: '提醒发货', primary: true }]
  if (state === 'shipped') return [{ key: 'logistics', label: '查看物流' }, { key: 'confirm', label: '确认收货', primary: true }]
  if (state === 'completed') return [{ key: 'again', label: '再次购买' }, { key: 'aftersale', label: '申请售后' }]
  return [{ key: 'detail', label: '查看详情' }]
}

onLoad((options: any) => {
  status.value = (options.status || '') as TabValue
})
onShow(load)
onPullDownRefresh(async () => {
  await load()
  uni.stopPullDownRefresh()
})
</script>

<style scoped lang="scss">
.order-page {
  padding-bottom: 40rpx;
}

.tabs-wrap {
  position: sticky;
  top: 0;
  z-index: 10;
  background: #FFFFFF;
  border-bottom: 2rpx solid var(--dm-line-soft);
}

.tabs-scroll {
  width: 100%;
  white-space: nowrap;
}

.tabs {
  display: inline-flex;
  min-width: 100%;
  padding: 0 16rpx;
  box-sizing: border-box;
}

.tab {
  position: relative;
  min-width: 112rpx;
  flex: 1;
  height: 88rpx;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
  color: var(--dm-text-2);
  font-size: 26rpx;
}

.tab.active {
  color: var(--dm-gold-500);
  font-weight: 700;
}

.tab.active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 44rpx;
  height: 4rpx;
  transform: translateX(-50%);
  border-radius: 4rpx;
  background: var(--dm-grad-gold);
}

.badge {
  color: var(--dm-red-500);
  font-size: 18rpx;
  transform: translateY(-8rpx);
}

.summary {
  min-height: 72rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 0 32rpx;
  color: #7A5610;
  background: #FFF6DA;
  font-size: 24rpx;
}

.summary.receive {
  color: #1F4B9A;
  background: #E8EFFD;
}

.summary.done {
  color: #1F6B40;
  background: #E6F5EB;
}

.summary-icon {
  width: 32rpx;
  height: 32rpx;
  line-height: 32rpx;
  border-radius: 50%;
  text-align: center;
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-size: 20rpx;
  font-weight: 800;
}

.orders {
  padding: 24rpx 32rpx;
}

.order-card {
  padding: 28rpx;
  margin-bottom: 24rpx;
}

.order-head,
.order-total,
.actions {
  display: flex;
  align-items: center;
}

.order-head {
  justify-content: space-between;
  gap: 16rpx;
}

.order-no {
  min-width: 0;
  color: var(--dm-text-3);
  font-size: 24rpx;
}

.order-status {
  flex: 0 0 auto;
  color: var(--dm-gold-500);
  font-size: 26rpx;
  font-weight: 700;
}

.status-pending {
  color: var(--dm-red-500);
}

.status-shipped {
  color: var(--dm-blue-500);
}

.status-completed {
  color: var(--dm-text-3);
}

.goods-list {
  margin-top: 20rpx;
}

.goods-row {
  display: flex;
  gap: 20rpx;
  padding: 8rpx 0;
}

.thumb {
  width: 112rpx;
  height: 112rpx;
  flex: 0 0 112rpx;
  border-radius: 12rpx;
  background: var(--dm-cream-100);
}

.goods-main {
  flex: 1;
  min-width: 0;
}

.goods-name,
.goods-spec,
.price,
.qty {
  display: block;
}

.goods-name {
  overflow: hidden;
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-spec,
.qty,
.time {
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.goods-spec {
  margin-top: 6rpx;
}

.goods-side {
  flex: 0 0 auto;
  text-align: right;
}

.price {
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 700;
}

.qty {
  margin-top: 6rpx;
}

.order-total {
  justify-content: space-between;
  gap: 16rpx;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 2rpx solid var(--dm-line-soft);
}

.time {
  flex: 1;
  min-width: 0;
}

.total {
  color: var(--dm-text-2);
  font-size: 24rpx;
}

.amount {
  color: var(--dm-gold-500);
  font-size: 30rpx;
  font-weight: 800;
}

.actions {
  justify-content: flex-end;
  gap: 16rpx;
  margin-top: 24rpx;
}

.actions button {
  height: 60rpx;
  line-height: 60rpx;
  padding: 0 28rpx;
  border-radius: 999rpx;
  background: #FFFFFF;
  color: var(--dm-text-2);
  border: 2rpx solid var(--dm-line);
  font-size: 24rpx;
  font-weight: 600;
}

.actions button.primary {
  color: #3A1A00;
  border: 0;
  background: var(--dm-grad-gold);
  box-shadow: 0 8rpx 20rpx rgba(211, 138, 0, 0.25);
}

button::after {
  border: 0;
}
</style>
