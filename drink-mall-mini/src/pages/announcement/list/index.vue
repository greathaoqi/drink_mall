<template>
  <view class="dm-page notice-page">
    <view class="list">
      <view v-if="loading" class="state dm-card">加载中...</view>
      <view v-else-if="!items.length" class="state dm-card">暂无公告</view>
      <view v-for="(item, index) in items" :key="item.id" class="notice-item dm-press" @click="go(item.id)">
        <view class="notice-icon" :class="categoryClass(item.category || item.type)">
          <text>{{ iconText(item.category || item.type) }}</text>
        </view>
        <view class="notice-main">
          <view class="notice-meta">
            <text class="tag">{{ categoryText(item.category || item.type) }}</text>
            <text class="date">{{ formatDate(item.createdAt || item.publishTime) }}</text>
            <text v-if="index === 0" class="new-dot"></text>
          </view>
          <text class="title">{{ item.title }}</text>
          <text v-if="item.summary" class="summary">{{ item.summary }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { contentApi } from '@/services/content'
import { listOf } from '@/utils/format'

const items = ref<any[]>([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    items.value = listOf((await contentApi.announcements()).data)
  } finally {
    loading.value = false
  }
}

function go(id: string | number) {
  uni.navigateTo({ url: '/pages/announcement/detail/index?id=' + id })
}

function categoryText(value?: string) {
  const map: Record<string, string> = {
    system: '系统',
    rule: '规则',
    activity: '活动',
    notice: '公告'
  }
  return map[value || ''] || value || '公告'
}

function categoryClass(value?: string) {
  if (value === 'activity') return 'activity'
  if (value === 'rule') return 'rule'
  return 'system'
}

function iconText(value?: string) {
  if (value === 'activity') return '礼'
  if (value === 'rule') return '规'
  return '告'
}

function formatDate(value?: string) {
  if (!value) return '刚刚'
  return String(value).slice(0, 10)
}

onShow(load)
</script>

<style scoped lang="scss">
.notice-page {
  background: var(--dm-bg-app);
}

.list {
  padding: 18rpx 0 40rpx;
}

.notice-item {
  display: flex;
  gap: 24rpx;
  padding: 28rpx 32rpx;
  border-bottom: 2rpx solid var(--dm-line-soft);
  background: #FFFFFF;
}

.notice-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 88rpx;
  font-size: 30rpx;
  font-weight: 900;
}

.notice-icon.system {
  background: var(--dm-gold-100);
  color: var(--dm-gold-600);
}

.notice-icon.activity {
  background: #FCDDE6;
  color: var(--dm-pink-500);
}

.notice-icon.rule {
  background: #DCE5FA;
  color: var(--dm-blue-500);
}

.notice-main {
  flex: 1;
  min-width: 0;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.tag {
  border-radius: var(--dm-radius-xs);
  padding: 5rpx 12rpx;
  background: var(--dm-gold-50);
  color: var(--dm-gold-600);
  font-size: 22rpx;
  font-weight: 700;
}

.date {
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.new-dot {
  width: 12rpx;
  height: 12rpx;
  margin-left: auto;
  border-radius: 50%;
  background: var(--dm-red-500);
}

.title,
.summary {
  display: block;
}

.title {
  margin-top: 12rpx;
  color: var(--dm-text-1);
  font-size: 29rpx;
  font-weight: 800;
  line-height: 1.45;
}

.summary {
  margin-top: 8rpx;
  overflow: hidden;
  color: var(--dm-text-3);
  font-size: 24rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.state {
  margin: 24rpx 32rpx;
  padding: 56rpx 28rpx;
  color: var(--dm-text-3);
  text-align: center;
}
</style>
