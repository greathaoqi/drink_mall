<template>
  <view class="order-list">
    <view class="tabs">
      <view v-for="tab in tabs" :key="tab.value" :class="['tab-item', { active: activeTab === tab.value }]" @click="activeTab = tab.value; loadOrders()">
        {{ tab.label }}
      </view>
    </view>

    <scroll-view scroll-y class="order-scroll" @scrolltolower="loadMore">
      <view v-for="order in orders" :key="order.orderId" class="order-card" @click="goDetail(order.orderId)">
        <view class="order-header">
          <text class="order-no">{{ order.orderNo }}</text>
          <text :class="['status', order.status]">{{ statusText(order.status) }}</text>
        </view>
        <view v-for="item in order.items" :key="item.itemId" class="order-item">
          <image :src="item.productImage" class="item-img" />
          <view class="item-info">
            <text class="item-name">{{ item.productName }}</text>
            <text class="item-price">¥{{ item.price }} x {{ item.quantity }}</text>
          </view>
        </view>
        <view class="order-footer">
          <text>共{{ order.items?.length || 0 }}件商品</text>
          <text class="total">实付: ¥{{ order.payAmount }}</text>
        </view>
      </view>
      <uni-load-more :status="loadStatus" />
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const tabs = [
  { label: '全部', value: '' },
  { label: '待付款', value: 'pending' },
  { label: '待发货', value: 'paid' },
  { label: '待收货', value: 'shipped' },
  { label: '已完成', value: 'completed' }
]

const activeTab = ref('')
const orders = ref<any[]>([])
const page = ref(1)
const hasMore = ref(true)
const loadStatus = ref('more')

const statusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待付款', paid: '待发货', shipped: '待收货', completed: '已完成', cancelled: '已取消'
  }
  return map[status] || status
}

const loadOrders = async () => {
  page.value = 1
  hasMore.value = true
  orders.value = []
  await loadMore()
}

const loadMore = async () => {
  if (!hasMore.value) return
  loadStatus.value = 'loading'
  const res = await request.get('/api/v1/order', { params: { status: activeTab.value, page: page.value } })
  const newOrders = res.data?.records || []
  orders.value = [...orders.value, ...newOrders]
  hasMore.value = newOrders.length >= 10
  page.value++
  loadStatus.value = hasMore.value ? 'more' : 'noMore'
}

const goDetail = (orderId: number) => {
  uni.navigateTo({ url: `/pages/order/detail/index?id=${orderId}` })
}

onShow(() => loadOrders())
</script>

<style scoped>
.order-list { background: #f5f5f5; min-height: 100vh; }
.tabs { display: flex; background: #fff; padding: 20rpx 0; position: sticky; top: 0; z-index: 10; }
.tab-item { flex: 1; text-align: center; font-size: 28rpx; color: #666; }
.tab-item.active { color: #e93b3d; font-weight: bold; }
.order-scroll { height: calc(100vh - 80rpx); }
.order-card { background: #fff; margin: 20rpx; border-radius: 12rpx; overflow: hidden; }
.order-header { display: flex; justify-content: space-between; padding: 20rpx; border-bottom: 1rpx solid #f5f5f5; }
.order-no { font-size: 24rpx; color: #999; }
.status { font-size: 28rpx; }
.status.pending { color: #ff6b00; }
.status.paid { color: #1890ff; }
.status.shipped { color: #52c41a; }
.status.completed { color: #999; }
.order-item { display: flex; padding: 20rpx; }
.item-img { width: 160rpx; height: 160rpx; border-radius: 8rpx; }
.item-info { flex: 1; margin-left: 20rpx; }
.item-name { font-size: 28rpx; display: block; }
.item-price { font-size: 24rpx; color: #999; margin-top: 10rpx; display: block; }
.order-footer { display: flex; justify-content: space-between; padding: 20rpx; border-top: 1rpx solid #f5f5f5; font-size: 28rpx; }
.total { color: #e93b3d; font-weight: bold; }
</style>