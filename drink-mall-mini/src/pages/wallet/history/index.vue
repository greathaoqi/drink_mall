<template>
  <view class="history-page">
    <view class="tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab"
        :class="{ active: activeTab === tab.key }"
        @click="switchTab(tab.key)"
      >{{ tab.label }}</view>
    </view>

    <view class="list">
      <view v-if="loading" class="empty">加载中...</view>
      <view v-else-if="records.length === 0" class="empty">暂无记录</view>
      <view v-else>
        <view v-for="item in records" :key="item.id" class="record-item">
          <view class="record-left">
            <text class="record-type">{{ formatType(item) }}</text>
            <text class="record-time">{{ item.createdAt?.slice(0, 16) }}</text>
            <text v-if="item.remark" class="record-remark">{{ item.remark }}</text>
          </view>
          <text class="record-amount" :class="amountClass(item)">{{ formatAmount(item) }}</text>
        </view>
        <view v-if="hasMore" class="load-more" @click="loadMore">加载更多</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

const tabs = [
  { key: 'balance', label: '余额流水' },
  { key: 'points', label: '积分记录' },
  { key: 'withdrawal', label: '提现记录' }
]
const activeTab = ref('balance')
const records = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(false)

const urlMap: Record<string, string> = {
  balance: '/user/balance-logs',
  points: '/user/points-logs',
  withdrawal: '/user/withdrawals'
}

const switchTab = (key: string) => {
  activeTab.value = key
  page.value = 1
  records.value = []
  load()
}

const load = async () => {
  loading.value = true
  try {
    const res = await http.get(urlMap[activeTab.value], { page: page.value, size: 20 })
    if (res.code === 200) {
      const data = res.data
      const newItems = data.records || []
      records.value = page.value === 1 ? newItems : [...records.value, ...newItems]
      hasMore.value = records.value.length < (data.total || 0)
    }
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  page.value++
  load()
}

const formatType = (item: any) => {
  const balanceMap: Record<string, string> = { payment: '消费', refund: '退款', recharge: '充值', withdrawal: '提现' }
  const pointsMap: Record<string, string> = { purchase: '购物获得', redeem: '积分兑换', adjust: '手动调整' }
  const withdrawalMap: Record<string, string> = { pending: '审核中', approved: '已通过', rejected: '已拒绝' }
  if (activeTab.value === 'balance') return balanceMap[item.changeType] || item.changeType
  if (activeTab.value === 'points') return pointsMap[item.changeType] || item.changeType
  return withdrawalMap[item.status] || item.status
}

const formatAmount = (item: any) => {
  if (activeTab.value === 'balance') {
    const n = Number(item.amount)
    return (n >= 0 ? '+' : '') + '¥' + Math.abs(n).toFixed(2)
  }
  if (activeTab.value === 'points') {
    const n = Number(item.points)
    return (n >= 0 ? '+' : '') + n
  }
  return '¥' + Number(item.amount).toFixed(2)
}

const amountClass = (item: any) => {
  if (activeTab.value === 'balance') return Number(item.amount) >= 0 ? 'positive' : 'negative'
  if (activeTab.value === 'points') return Number(item.points) >= 0 ? 'positive' : 'negative'
  return ''
}

onLoad((options: any) => {
  if (options?.tab) activeTab.value = options.tab
  load()
})
</script>

<style scoped lang="scss">
.history-page { background: #f5f5f5; min-height: 100vh; }
.tabs { display: flex; background: #fff; border-bottom: 1rpx solid #eee; }
.tab { flex: 1; text-align: center; padding: 24rpx 0; font-size: 28rpx; color: #666; }
.tab.active { color: #8a4f22; border-bottom: 4rpx solid #8a4f22; font-weight: 600; }
.list { padding: 20rpx; }
.empty { text-align: center; padding: 80rpx; color: #999; font-size: 28rpx; }
.record-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx 30rpx;
  margin-bottom: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.record-left { display: flex; flex-direction: column; }
.record-type { font-size: 30rpx; color: #333; }
.record-time { font-size: 24rpx; color: #aaa; margin-top: 8rpx; }
.record-remark { font-size: 24rpx; color: #888; margin-top: 4rpx; }
.record-amount { font-size: 32rpx; font-weight: 600; }
.positive { color: #07c160; }
.negative { color: #fa5151; }
.load-more { text-align: center; padding: 20rpx; color: #8a4f22; font-size: 28rpx; }
</style>
