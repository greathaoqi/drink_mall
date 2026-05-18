<template>
  <view class="dm-page level-page">
    <view class="brown-head">
      <text class="page-title">联营商等级体系</text>
      <view class="tabs">
        <view v-for="tab in tabs" :key="tab.key" class="tab" :class="{ active: activeTab === tab.key }" @click="activeTab = tab.key">
          {{ tab.text }}
        </view>
      </view>
    </view>

    <view v-if="activeTab === 'level'" class="level-content">
      <view class="current-card">
        <view class="current-line">
          <text>当前等级</text>
          <text class="tag">以后端配置为准</text>
        </view>
        <text class="current-title">{{ currentLevelName }}</text>
        <view class="bar"><view class="fill" :style="{ width: progressPercent + '%' }"></view></view>
        <view class="progress-copy">
          <text>累计业绩 {{ money(performance) }}</text>
          <text>目标 {{ money(nextTarget) }}</text>
        </view>
      </view>

      <text class="section-title">等级晋升路径</text>
      <view v-for="level in levels" :key="level.name" class="dm-card level-card" :class="{ current: level.current }">
        <view class="level-head">
          <view>
            <text class="level-name">{{ level.name }}</text>
            <text class="level-condition">{{ level.condition }}</text>
          </view>
          <text class="level-status">{{ level.current ? '当前等级' : level.status }}</text>
        </view>
        <view class="benefits">
          <text v-for="benefit in level.benefits" :key="benefit" class="benefit">{{ benefit }}</text>
        </view>
      </view>
    </view>

    <view v-else class="records">
      <view class="record-summary" :class="activeTab">
        <text class="eyebrow">{{ activeTab === 'reward' ? '奖励明细' : '佣金明细' }}</text>
        <text class="summary-value">{{ money(summaryAmount) }}</text>
        <text class="summary-copy">比例、封顶、冻结期等规则均以后端配置为准。</text>
      </view>
      <view class="dm-card record-list">
        <view v-for="r in visibleRecords" :key="r.id || r.createdAt || r.title" class="record">
          <view class="record-main">
            <text class="record-title">{{ r.title || r.bizType || recordFallback }}</text>
            <text class="record-sub">{{ r.createdAt || r.createTime || r.remark || '奖励流水' }}</text>
          </view>
          <text class="amount">{{ signedAmount(r.amount) }}</text>
        </view>
        <view v-if="!visibleRecords.length" class="empty">暂无记录</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { assetApi } from '@/services/asset'
import { useUserStore } from '@/store/user'
import { requirePageLogin } from '@/utils/auth'
import { listOf } from '@/utils/format'

const activeTab = ref<'level' | 'reward' | 'commission'>('level')
const records = ref<any[]>([])
const tabs = [
  { key: 'level', text: '联营商等级' },
  { key: 'reward', text: '奖励明细' },
  { key: 'commission', text: '佣金明细' }
] as const

const store = useUserStore()
const currentLevelName = computed(() => store.userInfo?.levelName || '普通会员')
const performance = computed(() => Number(store.userInfo?.teamPerformance || 0))
const nextTarget = computed(() => Number((store.userInfo as any)?.nextLevelTarget || 0))
const progressPercent = computed(() => {
  if (!nextTarget.value) return 0
  return Math.min(100, Math.round((performance.value / nextTarget.value) * 100))
})
const summaryAmount = computed(() => records.value.reduce((sum, r) => sum + Number(r.amount || 0), 0))
const visibleRecords = computed(() => records.value)
const recordFallback = computed(() => activeTab.value === 'reward' ? '招商/扶商奖励' : '推广佣金')

const levels = computed(() => {
  const configured = (store.userInfo as any)?.levelRules
  if (Array.isArray(configured) && configured.length) return configured
  return [
    {
      name: currentLevelName.value,
      condition: '当前账户等级',
      current: true,
      status: '已达成',
      benefits: ['折扣、佣金、奖励比例读取后台配置', '提现与结算需通过实名认证']
    },
    {
      name: '下一等级',
      condition: nextTarget.value ? `累计业绩达到 ${money(nextTarget.value)}` : '待业务配置晋升条件',
      current: false,
      status: nextTarget.value ? `还差 ${money(Math.max(nextTarget.value - performance.value, 0))}` : '待确认',
      benefits: ['晋升条件、折扣、佣金比例不在小程序写死', '招商奖励封顶与冻结期由后台配置']
    }
  ]
})

function money(value: any) {
  return `¥${Number(value || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function signedAmount(value: any) {
  const text = String(value ?? '0')
  if (text.startsWith('+') || text.startsWith('-')) return text
  return Number(value || 0) >= 0 ? `+${text}` : text
}

async function loadRecords() {
  const assetType = activeTab.value === 'commission' ? 'commission' : 'reward'
  try {
    records.value = listOf((await assetApi.records(assetType)).data)
  } catch {
    records.value = []
  }
}

onLoad(async (o: any) => {
  if (!requirePageLogin()) return
  if (['reward', 'commission'].includes(o.tab)) activeTab.value = o.tab
  await loadRecords()
})
</script>

<style scoped lang="scss">
.level-page {
  padding-bottom: 44rpx;
}

.brown-head {
  padding: 72rpx var(--dm-page-x) 0;
  color: var(--dm-text-on-brown);
  background: var(--dm-grad-brown);
}

.page-title {
  display: block;
  color: #FFE0A0;
  font-size: 36rpx;
  font-weight: 800;
}

.tabs {
  display: flex;
  margin-top: 24rpx;
}

.tab {
  flex: 1;
  padding-bottom: 18rpx;
  text-align: center;
  color: rgba(246, 231, 194, 0.62);
  font-size: 26rpx;
  position: relative;
}

.tab.active {
  color: #FFE0A0;
  font-weight: 800;
}

.tab.active:after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 44rpx;
  height: 4rpx;
  border-radius: 4rpx;
  transform: translateX(-50%);
  background: var(--dm-grad-gold);
}

.level-content,
.records {
  padding: 0 var(--dm-page-x) 44rpx;
}

.current-card {
  margin-top: -1rpx;
  padding: 30rpx;
  border-radius: 0 0 var(--dm-radius-lg) var(--dm-radius-lg);
  color: var(--dm-text-on-brown);
  background: linear-gradient(135deg, #2A0E00, #4A1E08);
  box-shadow: var(--dm-shadow-pop);
}

.current-line,
.level-head,
.record {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.current-line {
  color: rgba(246, 231, 194, 0.62);
  font-size: 23rpx;
}

.tag,
.level-status,
.benefit {
  border-radius: 8rpx;
  font-size: 22rpx;
}

.tag {
  padding: 4rpx 12rpx;
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-weight: 800;
}

.current-title {
  display: block;
  margin-top: 14rpx;
  color: #FFE0A0;
  font-size: 42rpx;
  font-weight: 800;
}

.bar {
  height: 12rpx;
  margin-top: 24rpx;
  border-radius: 12rpx;
  background: rgba(255, 255, 255, 0.12);
  overflow: hidden;
}

.fill {
  height: 100%;
  background: var(--dm-grad-gold);
}

.progress-copy {
  display: flex;
  justify-content: space-between;
  margin-top: 12rpx;
  color: rgba(246, 231, 194, 0.68);
  font-size: 22rpx;
}

.section-title {
  display: block;
  margin: 28rpx 0 16rpx;
  font-size: 31rpx;
  font-weight: 800;
}

.level-card {
  padding: 28rpx;
  margin-bottom: 18rpx;
  border: 2rpx solid transparent;
}

.level-card.current {
  border-color: var(--dm-gold-500);
  box-shadow: 0 8rpx 28rpx rgba(228, 165, 22, 0.16);
}

.level-name,
.level-condition {
  display: block;
}

.level-name {
  font-size: 30rpx;
  font-weight: 800;
}

.level-condition {
  margin-top: 6rpx;
  color: var(--dm-text-3);
  font-size: 23rpx;
}

.level-status {
  padding: 6rpx 14rpx;
  color: var(--dm-gold-600);
  background: var(--dm-gold-50);
}

.benefits {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 20rpx;
}

.benefit {
  padding: 8rpx 14rpx;
  color: var(--dm-text-2);
  background: var(--dm-cream-100);
}

.record-summary {
  margin-top: 24rpx;
  padding: 28rpx;
  border-radius: var(--dm-radius-lg);
  color: #fff;
}

.record-summary.reward { background: linear-gradient(135deg, #D1467A, #99294F); }
.record-summary.commission { background: var(--dm-grad-gold); color: #3A1A00; }

.eyebrow,
.summary-copy {
  display: block;
  font-size: 23rpx;
  opacity: 0.78;
}

.summary-value {
  display: block;
  margin-top: 8rpx;
  font-size: 54rpx;
  font-weight: 800;
}

.summary-copy {
  margin-top: 6rpx;
}

.record-list {
  margin-top: 18rpx;
  overflow: hidden;
}

.record {
  min-height: 96rpx;
  padding: 18rpx 24rpx;
  border-bottom: 1rpx solid var(--dm-line-soft);
}

.record-main {
  min-width: 0;
  flex: 1;
}

.record-title,
.record-sub {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-title {
  font-size: 28rpx;
  font-weight: 700;
}

.record-sub,
.empty {
  color: var(--dm-text-3);
  font-size: 23rpx;
}

.record-sub {
  margin-top: 4rpx;
}

.amount {
  margin-left: 18rpx;
  color: var(--dm-gold-500);
  font-size: 30rpx;
  font-weight: 800;
}

.empty {
  padding: 34rpx 24rpx;
}
</style>
