<template>
  <view class="dm-page wallet-page">
    <view class="asset-hero">
      <view class="hero-line">
        <text class="eyebrow">资产独立记账</text>
        <text class="detail-link" @click="history('balance')">明细</text>
      </view>
      <text class="hero-title">{{ money(totalAssets) }}</text>
      <text class="hero-copy">余额、冻结余额、DF、酒豆、积分、期权分别展示，资产流转以后端配置为准。</text>
    </view>

    <view class="asset-grid">
      <view
        v-for="a in assets"
        :key="a.key"
        class="asset-card"
        :class="a.tone"
        @click="history(a.key)"
      >
        <text class="asset-name">{{ a.name }}</text>
        <text class="asset-value">{{ a.prefix }}{{ a.value }}{{ a.suffix || '' }}</text>
        <text class="asset-tip">{{ a.tip }}</text>
      </view>
    </view>

    <view class="dm-card action-grid">
      <view v-for="action in actions" :key="action.text" class="action" @click="go(action.url)">
        <view class="action-icon" :class="action.tone">{{ action.icon }}</view>
        <text>{{ action.text }}</text>
      </view>
    </view>

    <view class="recent">
      <view class="section-head">
        <text class="section-title">最近变动</text>
        <text class="more" @click="history('balance')">查看更多</text>
      </view>
      <view class="dm-card record-list">
        <view v-for="r in recentRecords" :key="r.id || r.title" class="record">
          <view class="record-main">
            <text class="record-title">{{ r.title || r.bizType || '资产变动' }}</text>
            <text class="record-sub">{{ r.createdAt || r.createTime || r.assetType || '资产明细' }}</text>
          </view>
          <text class="record-amount" :class="{ neg: isNegative(r.amount) }">{{ signedAmount(r.amount) }}</text>
        </view>
        <view v-if="!recentRecords.length" class="empty">暂无资产变动记录</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { assetApi } from '@/services/asset'
import { useUserStore } from '@/store/user'
import { requirePageLogin } from '@/utils/auth'
import { listOf } from '@/utils/format'

const data = ref<any>({})
const recentRecords = ref<any[]>([])

const source = computed(() => {
  const u = useUserStore().userInfo || {}
  return { ...u, ...data.value }
})

const assets = computed(() => {
  const d = source.value
  return [
    { key: 'balance', name: '可提现余额', value: fmt(d.balance), prefix: '¥', tip: '提现与佣金结算账户', tone: 'gold' },
    { key: 'frozenBalance', name: '冻结余额', value: fmt(d.frozenBalance), prefix: '¥', tip: '售后或审核冻结', tone: 'dark' },
    { key: 'df', name: 'DF', value: fmt(d.df), prefix: '¥', tip: '可消费，可赠送', tone: 'blue' },
    { key: 'wineBean', name: '酒豆', value: fmt(d.wineBean, 0), prefix: '', tip: '按配置抵扣或兑换', tone: 'red' },
    { key: 'points', name: '积分', value: fmt(d.points, 0), prefix: '', tip: '用于积分礼包兑换', tone: 'pink' },
    { key: 'option', name: '期权', value: fmt(d.optionValue ?? d.option, 0), prefix: '', tip: '按后台配置展示', tone: 'green' }
  ]
})

const totalAssets = computed(() => {
  const d = source.value
  return Number(d.balance || 0) + Number(d.frozenBalance || 0) + Number(d.df || 0)
})

const actions = [
  { text: '提现', icon: '¥', url: '/pages/wallet/withdraw/index', tone: 'gold' },
  { text: '余额明细', icon: '账', url: '/pages/wallet/history/index?type=balance', tone: 'blue' },
  { text: '酒豆明细', icon: '豆', url: '/pages/wallet/history/index?type=wineBean', tone: 'red' },
  { text: '积分明细', icon: '积', url: '/pages/wallet/history/index?type=points', tone: 'pink' },
  { text: 'DF 赠送', icon: 'DF', url: '/pages/wallet/history/index?type=dfGift', tone: 'blue' },
  { text: 'DF 记录', icon: '记', url: '/pages/wallet/history/index?type=dfGiftRecords', tone: 'blue' },
  { text: '礼包兑换', icon: '礼', url: '/pages/points/index', tone: 'green' }
]

function fmt(value: any, digits = 2) {
  const n = Number(value || 0)
  return n.toLocaleString('zh-CN', { minimumFractionDigits: digits, maximumFractionDigits: digits })
}

function money(value: number) {
  return `¥${fmt(value)}`
}

function isNegative(value: any) {
  return String(value || '').startsWith('-') || Number(value || 0) < 0
}

function signedAmount(value: any) {
  const text = String(value ?? '0')
  if (text.startsWith('-') || text.startsWith('+')) return text
  return Number(value || 0) >= 0 ? `+${text}` : text
}

function history(t: string) {
  uni.navigateTo({ url: `/pages/wallet/history/index?type=${t}` })
}

function go(url: string) {
  uni.navigateTo({ url })
}

onShow(async () => {
  if (!requirePageLogin()) return
  try {
    data.value = (await assetApi.overview()).data || {}
    recentRecords.value = listOf((await assetApi.records('balance', { pageSize: 4 })).data).slice(0, 4)
  } catch {
    recentRecords.value = []
  }
})
</script>

<style scoped lang="scss">
.wallet-page {
  padding: 0 var(--dm-page-x) 44rpx;
}

.asset-hero {
  margin: 0 calc(var(--dm-page-x) * -1);
  padding: 72rpx var(--dm-page-x) 56rpx;
  color: var(--dm-text-on-brown);
  background:
    radial-gradient(ellipse 80% 60% at 30% 12%, rgba(228, 165, 22, 0.22), transparent 60%),
    var(--dm-grad-brown);
}

.hero-line,
.section-head,
.record {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.eyebrow {
  color: rgba(246, 231, 194, 0.62);
  font-size: 23rpx;
  letter-spacing: 4rpx;
}

.detail-link {
  color: #FFD685;
  font-size: 25rpx;
}

.hero-title {
  display: block;
  margin-top: 12rpx;
  color: #FFE0A0;
  font-size: 66rpx;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.hero-copy {
  display: block;
  margin-top: 8rpx;
  color: rgba(246, 231, 194, 0.62);
  font-size: 23rpx;
  line-height: 1.6;
}

.asset-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
  margin-top: -28rpx;
}

.asset-card {
  min-height: 176rpx;
  padding: 26rpx;
  border-radius: var(--dm-radius-lg);
  color: #fff;
  overflow: hidden;
  box-sizing: border-box;
}

.asset-name,
.asset-tip {
  display: block;
  font-size: 22rpx;
  opacity: 0.82;
}

.asset-value {
  display: block;
  margin-top: 8rpx;
  font-size: 36rpx;
  font-weight: 800;
  word-break: break-all;
  font-variant-numeric: tabular-nums;
}

.asset-tip {
  margin-top: 6rpx;
  opacity: 0.68;
}

.gold { background: linear-gradient(135deg, #FFD646, #D18A00); color: #3A1A00; }
.dark { background: linear-gradient(135deg, #4A3F30, #2A2018); color: #FFE0A0; }
.blue { background: linear-gradient(135deg, #3A6FD1, #1F4B9A); }
.red { background: linear-gradient(135deg, #C4322B, #771810); }
.pink { background: linear-gradient(135deg, #D1467A, #99294F); }
.green { background: linear-gradient(135deg, #6BA45C, #3F7A2A); }

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 22rpx 8rpx;
  margin-top: 22rpx;
  padding: 26rpx 10rpx;
}

.action {
  text-align: center;
  color: var(--dm-text-2);
  font-size: 22rpx;
}

.action-icon {
  width: 76rpx;
  height: 76rpx;
  margin: 0 auto 10rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24rpx;
  font-weight: 800;
}

.recent {
  margin-top: 28rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 800;
}

.more,
.empty,
.record-sub {
  color: var(--dm-text-3);
  font-size: 23rpx;
}

.record-list {
  margin-top: 14rpx;
  overflow: hidden;
}

.record {
  min-height: 94rpx;
  padding: 18rpx 24rpx;
  border-bottom: 1rpx solid var(--dm-line-soft);
}

.record:last-child {
  border-bottom: 0;
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
  color: var(--dm-text-1);
  font-size: 27rpx;
  font-weight: 700;
}

.record-sub {
  margin-top: 4rpx;
}

.record-amount {
  margin-left: 18rpx;
  color: var(--dm-gold-500);
  font-size: 30rpx;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.record-amount.neg {
  color: var(--dm-text-1);
}

.empty {
  padding: 30rpx 24rpx;
}
</style>
