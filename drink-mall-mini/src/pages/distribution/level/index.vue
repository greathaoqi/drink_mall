<template>
  <view class="level-page">
    <view class="top-nav">
      <view class="status-spacer"></view>
      <view class="nav-row">
        <view class="nav-icon" @click="goBack">
          <uni-icons type="left" size="24" color="#ffffff" />
        </view>
        <text class="nav-title">联营商等级体系</text>
        <view class="nav-icon"></view>
      </view>
    </view>

    <view class="tab-row">
      <view v-for="tab in tabs" :key="tab" class="tab-item" :class="{ active: tab === activeTab }" @click="activeTab = tab">
        {{ tab }}
      </view>
    </view>

    <view class="performance-card">
      <view class="card-top">
        <text class="small-label">当前等级</text>
        <text class="status-pill">已达成</text>
      </view>
      <text class="performance-title">累计业绩 ¥{{ formatMoney(performanceAmount) }}</text>
      <view class="progress-track">
        <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
      </view>
      <view class="progress-meta">
        <text>已达成 ¥{{ formatMoney(performanceAmount) }}</text>
        <text>市级目标 ¥{{ formatMoney(nextTargetAmount) }}</text>
      </view>
    </view>

    <view class="path-section">
      <text class="section-title">等级晋升路径</text>

      <view v-for="level in levels" :key="level.code" class="level-card" :class="{ 'active-card': level.code === currentLevelCode }">
        <view class="level-head">
          <view class="level-title">
            <view class="dot" :class="level.achieved ? 'active-dot' : 'muted-dot'"></view>
            <text>{{ level.name }}</text>
          </view>
          <text v-if="level.achieved" class="done-pill">{{ level.name }}</text>
          <text v-else class="upgrade-pill">差价升级 ¥{{ formatMoney(level.upgradeDifference) }}</text>
        </view>
        <text class="entry">入门金额：¥{{ formatMoney(level.entryAmount) }}</text>
        <view class="benefit" :class="level.achieved ? 'active-benefit' : 'muted-benefit'">
          <text>核心权益：</text>
          <text v-for="benefit in level.benefits" :key="benefit">· {{ benefit }}</text>
        </view>
      </view>
    </view>

    <button class="upgrade-btn">立即升级市级联营商</button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

interface DistributionLevel {
  code: string
  name: string
  entryAmount: number | string
  upgradeDifference: number | string
  achieved: boolean
  benefits: string[]
}

interface DistributionLevelOverview {
  currentLevel: DistributionLevel
  performanceAmount: number | string
  nextTargetAmount: number | string
  upgradeDifference: number | string
  progressPercent: number | string
  levels: DistributionLevel[]
}

const tabs = ['联营商等级', '合伙人等级', '奖励明细']
const activeTab = ref('联营商等级')
const overview = ref<DistributionLevelOverview | null>(null)

const fallbackLevels: DistributionLevel[] = [
  {
    code: 'county',
    name: '县级联营商',
    entryAmount: 50000,
    upgradeDifference: 0,
    achieved: true,
    benefits: ['直推首购 20% 佣金', '招商奖励 20%', '扶商奖励 10%', '自购 5 折优惠', '可提交体验店申请']
  },
  {
    code: 'city',
    name: '市级联营商',
    entryAmount: 150000,
    upgradeDifference: 91600,
    achieved: false,
    benefits: ['直推首购 20% 佣金', '招商奖励 20%', '扶商奖励 10%', '可提交体验店申请']
  }
]

const performanceAmount = computed(() => Number(overview.value?.performanceAmount ?? 58400))
const nextTargetAmount = computed(() => Number(overview.value?.nextTargetAmount ?? 150000))
const progressPercent = computed(() => Number(overview.value?.progressPercent ?? 38.93))
const levels = computed(() => overview.value?.levels || fallbackLevels)
const currentLevelCode = computed(() => overview.value?.currentLevel?.code || 'county')

onShow(() => {
  loadDistributionLevel()
})

async function loadDistributionLevel() {
  try {
    const res = await http.get<DistributionLevelOverview>('/distribution/level', {}, { showError: false })
    if (res.code === 200 && res.data) {
      overview.value = res.data
    }
  } catch (error) {
    console.error('Failed to load distribution level:', error)
  }
}

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.switchTab({ url: '/pages/profile/index' })
  }
}

function formatMoney(value: number | string) {
  return Number(value || 0).toLocaleString('zh-CN')
}
</script>

<style scoped lang="scss">
.level-page {
  min-height: 100vh;
  padding-bottom: 140rpx;
  background: #f4f0e8;
  color: #1f160f;
}

.top-nav {
  background: #4a1700;
}

.status-spacer {
  height: 92rpx;
}

.nav-row {
  height: 76rpx;
  padding: 0 30rpx;
  display: grid;
  grid-template-columns: 58rpx 1fr 58rpx;
  align-items: center;
}

.nav-icon {
  width: 58rpx;
  height: 58rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  color: #ffffff;
  font-size: 33rpx;
  font-weight: 900;
}

.tab-row {
  height: 70rpx;
  background: #ffffff;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  align-items: center;
}

.tab-item {
  height: 70rpx;
  line-height: 70rpx;
  text-align: center;
  color: #77716b;
  font-size: 27rpx;
  position: relative;
}

.tab-item.active {
  color: #c37a00;
  font-weight: 900;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 64rpx;
  height: 4rpx;
  border-radius: 999rpx;
  transform: translateX(-50%);
  background: #d39200;
}

.performance-card {
  margin: 0 30rpx;
  padding: 40rpx;
  border-radius: 24rpx;
  background:
    radial-gradient(circle at 85% 20%, rgba(255, 208, 67, 0.22), transparent 32%),
    linear-gradient(135deg, #2b0f00, #1a0700);
  color: #f5bc25;
}

.card-top,
.progress-meta,
.level-head,
.level-title {
  display: flex;
  align-items: center;
}

.card-top,
.level-head,
.progress-meta {
  justify-content: space-between;
}

.small-label {
  color: rgba(245, 188, 37, 0.62);
  font-size: 23rpx;
}

.status-pill,
.done-pill,
.upgrade-pill {
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 800;
}

.status-pill {
  color: #2b0f00;
  background: #f4cf35;
}

.performance-title {
  display: block;
  margin-top: 28rpx;
  font-size: 40rpx;
  font-weight: 900;
}

.progress-track {
  height: 14rpx;
  margin-top: 28rpx;
  border-radius: 999rpx;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.16);
}

.progress-fill {
  height: 100%;
  border-radius: inherit;
  background: #f5bc25;
}

.progress-meta {
  margin-top: 20rpx;
  color: rgba(245, 188, 37, 0.5);
  font-size: 22rpx;
}

.path-section {
  padding: 26rpx 30rpx 0;
}

.section-title {
  display: block;
  margin-bottom: 18rpx;
  color: #1f160f;
  font-size: 30rpx;
  font-weight: 900;
}

.level-card {
  padding: 30rpx;
  margin-bottom: 24rpx;
  border: 2rpx solid #e0c89e;
  border-radius: 24rpx;
  background: #ffffff;
}

.active-card {
  border: 4rpx solid #d39200;
}

.level-title {
  gap: 14rpx;
  color: #1f160f;
  font-size: 30rpx;
  font-weight: 900;
}

.dot {
  width: 22rpx;
  height: 22rpx;
  border-radius: 50%;
}

.active-dot {
  background: #d39200;
}

.muted-dot {
  background: #c7c7c7;
}

.done-pill {
  color: #ffffff;
  background: #dff2dc;
}

.upgrade-pill {
  color: #d39200;
  background: #fff0c8;
}

.entry {
  display: block;
  margin-top: 24rpx;
  color: #5f554c;
  font-size: 25rpx;
}

.benefit {
  margin-top: 26rpx;
  padding: 24rpx;
  border-radius: 14rpx;
  display: flex;
  flex-direction: column;
  gap: 9rpx;
  color: #7b4c10;
  font-size: 24rpx;
  line-height: 1.5;
}

.active-benefit {
  background: #fff6dc;
}

.muted-benefit {
  color: #8e8a85;
  background: #f4f4f4;
}

.upgrade-btn {
  position: fixed;
  left: 30rpx;
  right: 30rpx;
  bottom: 32rpx;
  height: 92rpx;
  line-height: 92rpx;
  border: none;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #ffd43d, #c97c00);
  color: #ffffff;
  font-size: 31rpx;
  font-weight: 900;
}

.upgrade-btn::after {
  border: none;
}
</style>
