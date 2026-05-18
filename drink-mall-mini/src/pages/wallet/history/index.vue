<template>
  <view class="dm-page history-page">
    <view v-if="isDfGift" class="df-gift">
      <view class="blue-hero">
        <text class="eyebrow">DF 赠送</text>
        <text class="hero-title">¥{{ fmt(overview.df) }}</text>
        <text class="hero-copy">仅可按后台配置赠送给可选成员，提交后以服务端流水为准。</text>
      </view>

      <view class="dm-card form-card">
        <text class="section-title">赠送信息</text>
        <view class="field">
          <text>接收人用户 ID</text>
          <input v-model.trim="giftForm.toUserId" placeholder="请输入接收人用户 ID" />
        </view>
        <view class="field">
          <text>赠送金额</text>
          <input v-model.trim="giftForm.amount" type="digit" placeholder="请输入 DF 金额" />
        </view>
        <view class="field">
          <text>备注</text>
          <input v-model.trim="giftForm.remark" placeholder="选填，最多 50 字" />
        </view>
        <button class="submit blue-btn" :loading="loading" @click="submitGift">确认赠送</button>
      </view>

      <view class="tip-box">DF 账户与余额、酒豆、积分相互独立；赠送限额、对象范围和到账规则以后端配置为准。</view>
    </view>

    <view v-else>
      <view class="summary" :class="summaryTone">
        <view>
          <text class="eyebrow">{{ pageTitle }}</text>
          <text class="summary-value">{{ summaryPrefix }}{{ summaryValue }}</text>
          <text class="summary-copy">{{ summaryCopy }}</text>
        </view>
        <button v-if="type === 'points'" class="mini-btn" @click="goPoints">去兑换</button>
      </view>

      <view class="filter-row">
        <view
          v-for="f in filters"
          :key="f.key"
          class="filter"
          :class="{ active: filter === f.key }"
          @click="filter = f.key"
        >
          {{ f.text }}
        </view>
      </view>

      <view class="record-list dm-card">
        <view v-for="r in displayRecords" :key="r.id || r.createdAt || r.title" class="record">
          <view class="record-main">
            <text class="record-title">{{ r.title || r.bizType || fallbackTitle }}</text>
            <text class="record-sub">{{ r.createdAt || r.createTime || r.remark || '资产流水' }}</text>
          </view>
          <text class="amount" :class="{ neg: isNegative(r.amount) }">{{ signedAmount(r.amount) }}</text>
        </view>
        <view v-if="!displayRecords.length" class="empty">暂无记录</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { assetApi } from '@/services/asset'
import { listOf } from '@/utils/format'
import { requirePageLogin } from '@/utils/auth'

const type = ref('balance')
const filter = ref('all')
const records = ref<any[]>([])
const overview = ref<any>({})
const loading = ref(false)
const giftForm = ref({ toUserId: '', amount: '', remark: '' })

const isDfGift = computed(() => type.value === 'dfGift')
const isDfGiftRecords = computed(() => type.value === 'dfGiftRecords')

const filters = computed(() => {
  if (isDfGiftRecords.value) return [{ key: 'all', text: '全部' }, { key: 'out', text: '我送出' }, { key: 'in', text: '我收到' }]
  return [{ key: 'all', text: '全部' }, { key: 'income', text: '收入' }, { key: 'expense', text: '支出' }]
})

const pageTitle = computed(() => {
  const map: Record<string, string> = {
    balance: '余额明细',
    frozenBalance: '冻结余额明细',
    df: 'DF 明细',
    wineBean: '酒豆明细',
    points: '积分明细',
    option: '期权明细',
    dfGiftRecords: 'DF 赠送记录'
  }
  return map[type.value] || '资产明细'
})

const fallbackTitle = computed(() => isDfGiftRecords.value ? 'DF 赠送' : '资产变动')
const summaryTone = computed(() => {
  if (type.value === 'df' || isDfGiftRecords.value) return 'blue'
  if (type.value === 'wineBean') return 'red'
  if (type.value === 'points') return 'pink'
  if (type.value === 'option') return 'green'
  return 'gold'
})
const summaryPrefix = computed(() => ['balance', 'frozenBalance', 'df', 'dfGiftRecords'].includes(type.value) ? '¥' : '')
const summaryValue = computed(() => {
  const key = type.value === 'dfGiftRecords' ? 'df' : type.value
  return fmt(overview.value[key] ?? overview.value.optionValue, ['wineBean', 'points', 'option'].includes(type.value) ? 0 : 2)
})
const summaryCopy = computed(() => {
  const copy: Record<string, string> = {
    balance: '可提现余额流水',
    frozenBalance: '冻结与解冻记录',
    df: 'DF 收支流水',
    wineBean: '酒豆获得、使用与到期记录',
    points: '积分获得与使用记录',
    option: '期权账户展示以后端配置为准',
    dfGiftRecords: 'DF 送出与收到记录'
  }
  return copy[type.value] || '资产账户明细'
})

const displayRecords = computed(() => {
  if (filter.value === 'all') return records.value
  if (filter.value === 'income' || filter.value === 'in') return records.value.filter((r) => !isNegative(r.amount) || r.direction === 'IN')
  return records.value.filter((r) => isNegative(r.amount) || r.direction === 'OUT')
})

function fmt(value: any, digits = 2) {
  const n = Number(value || 0)
  return n.toLocaleString('zh-CN', { minimumFractionDigits: digits, maximumFractionDigits: digits })
}

function isNegative(value: any) {
  return String(value || '').startsWith('-') || Number(value || 0) < 0
}

function signedAmount(value: any) {
  const text = String(value ?? '0')
  if (text.startsWith('+') || text.startsWith('-')) return text
  return Number(value || 0) >= 0 ? `+${text}` : text
}

function goPoints() {
  uni.navigateTo({ url: '/pages/points/index' })
}

async function submitGift() {
  if (!giftForm.value.toUserId || !giftForm.value.amount) {
    uni.showToast({ title: '请填写接收人和金额', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await assetApi.transferDf(giftForm.value)
    uni.showToast({ title: '赠送申请已提交', icon: 'success' })
    giftForm.value = { toUserId: '', amount: '', remark: '' }
  } finally {
    loading.value = false
  }
}

onLoad(async (o: any) => {
  if (!requirePageLogin()) return
  type.value = o.type || o.tab || 'balance'
  const recordType = isDfGiftRecords.value ? 'df' : type.value
  try {
    overview.value = (await assetApi.overview()).data || {}
    if (!isDfGift.value) records.value = listOf((await assetApi.records(recordType)).data)
  } catch {
    records.value = []
  }
})
</script>

<style scoped lang="scss">
.history-page {
  padding: 24rpx var(--dm-page-x) 44rpx;
}

.summary,
.blue-hero {
  border-radius: var(--dm-radius-lg);
  padding: 30rpx;
  color: #fff;
  overflow: hidden;
}

.summary.gold { background: var(--dm-grad-gold); color: #3A1A00; }
.summary.blue,
.blue-hero { background: linear-gradient(135deg, #3A6FD1, #1F4B9A); }
.summary.red { background: linear-gradient(135deg, #C4322B, #771810); }
.summary.pink { background: linear-gradient(135deg, #D1467A, #99294F); }
.summary.green { background: linear-gradient(135deg, #6BA45C, #3F7A2A); }

.summary {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
}

.eyebrow,
.summary-copy,
.hero-copy {
  display: block;
  font-size: 23rpx;
  opacity: 0.78;
}

.summary-value,
.hero-title {
  display: block;
  margin-top: 8rpx;
  font-size: 54rpx;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.summary-copy,
.hero-copy {
  margin-top: 6rpx;
}

.mini-btn {
  align-self: flex-start;
  width: 142rpx;
  height: 58rpx;
  line-height: 58rpx;
  border: 0;
  border-radius: var(--dm-radius-pill);
  background: #fff;
  color: var(--dm-pink-500);
  font-size: 23rpx;
  font-weight: 700;
}

.filter-row {
  display: flex;
  gap: 12rpx;
  margin: 24rpx 0 14rpx;
}

.filter {
  height: 54rpx;
  line-height: 54rpx;
  padding: 0 24rpx;
  border: 1rpx solid var(--dm-line);
  border-radius: var(--dm-radius-pill);
  color: var(--dm-text-2);
  font-size: 24rpx;
}

.filter.active {
  color: var(--dm-gold-600);
  background: var(--dm-gold-50);
  border-color: #F2D78A;
  font-weight: 700;
}

.record-list,
.form-card {
  overflow: hidden;
}

.record {
  display: flex;
  align-items: center;
  min-height: 96rpx;
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
  margin-left: 20rpx;
  color: var(--dm-gold-500);
  font-size: 30rpx;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.amount.neg {
  color: var(--dm-text-1);
}

.empty {
  padding: 32rpx 24rpx;
}

.form-card {
  margin-top: 22rpx;
  padding: 28rpx;
}

.section-title {
  display: block;
  font-size: 31rpx;
  font-weight: 800;
}

.field {
  margin-top: 24rpx;
}

.field text {
  display: block;
  color: var(--dm-text-2);
  font-size: 24rpx;
  margin-bottom: 10rpx;
}

.field input {
  height: 82rpx;
  padding: 0 22rpx;
  border-radius: var(--dm-radius-md);
  background: var(--dm-cream-100);
}

.submit {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  margin-top: 30rpx;
  border: 0;
  border-radius: var(--dm-radius-pill);
  color: #fff;
  font-weight: 800;
}

.blue-btn {
  background: linear-gradient(135deg, #3A6FD1, #1F4B9A);
}

.tip-box {
  margin-top: 18rpx;
  padding: 22rpx;
  border-radius: var(--dm-radius-md);
  background: #E8EFFD;
  color: #1F4B9A;
  font-size: 24rpx;
  line-height: 1.6;
}

button:after {
  border: 0;
}
</style>
