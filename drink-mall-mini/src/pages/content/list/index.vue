<template>
  <view class="dm-page content-page">
    <view class="hero">
      <view class="status-space"></view>
      <view class="hero-copy">
        <text class="hero-title">动态</text>
        <text class="hero-subtitle">关注平台公告、品酒知识与新品上市</text>
      </view>
      <view class="hero-tabs">
        <view
          v-for="tab in tabs"
          :key="tab.key"
          class="hero-tab"
          :class="{ active: activeTab === tab.key }"
          @click="selectTab(tab.key)"
        >
          <text>{{ tab.label }}</text>
        </view>
      </view>
    </view>

    <view class="content-body">
      <view v-if="activeTab === 'recommend' && pinnedAnnouncement" class="pinned dm-card dm-press" @click="goAnnouncement(pinnedAnnouncement.id)">
        <text class="pin-tag">置顶</text>
        <view class="pin-main">
          <text class="pin-title">{{ pinnedAnnouncement.title }}</text>
          <text class="pin-meta">{{ formatDate(pinnedAnnouncement.createdAt || pinnedAnnouncement.publishTime) }} · 平台公告</text>
        </view>
      </view>

      <view v-if="activeTab === 'announcement'" class="notice-entry dm-card dm-press" @click="goAnnouncementList">
        <view class="notice-icon">告</view>
        <view class="notice-main">
          <text class="notice-title">平台公告</text>
          <text class="notice-subtitle">查看系统通知、规则调整和活动说明</text>
        </view>
        <text class="chevron">›</text>
      </view>

      <view v-if="loading" class="state dm-card">加载中...</view>
      <view v-else-if="!visibleItems.length && activeTab !== 'announcement'" class="state dm-card">暂无内容</view>

      <view
        v-for="(item, index) in visibleItems"
        :key="item.id"
        class="feed-card dm-card dm-press"
        :class="{ feature: index === 0 && activeTab === 'recommend' }"
        @click="go(item)"
      >
        <image class="cover" :src="coverOf(item, index)" mode="aspectFill" />
        <view class="feed-main">
          <view class="feed-tags">
            <text class="tag">{{ tagOf(item) }}</text>
            <text v-if="item.paid" class="tag pay-tag">{{ item.canView ? '已解锁' : '权限内容' }}</text>
          </view>
          <text class="feed-title">{{ item.title }}</text>
          <text class="feed-summary">{{ item.summary || item.description || '精选内容持续更新中，点击查看详情。' }}</text>
          <view class="feed-meta">
            <text>{{ item.viewCount || item.views || 0 }} 阅读</text>
            <text>{{ item.likeCount || item.likes || 0 }} 喜欢</text>
            <text class="date">{{ formatDate(item.createdAt || item.publishTime) }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { contentApi } from '@/services/content'
import { listOf } from '@/utils/format'

const tabs = [
  { key: 'recommend', label: '推荐', type: '' },
  { key: 'announcement', label: '公告', type: '' },
  { key: 'knowledge', label: '品酒知识', type: 'article' },
  { key: 'activity', label: '联营商动态', type: 'activity' }
]

const activeTab = ref('recommend')
const items = ref<any[]>([])
const announcements = ref<any[]>([])
const loading = ref(false)

const activeType = computed(() => tabs.find((tab) => tab.key === activeTab.value)?.type || '')
const pinnedAnnouncement = computed(() => announcements.value[0])
const visibleItems = computed(() => {
  if (activeTab.value === 'announcement') return []
  return items.value
})

function selectTab(key: string) {
  activeTab.value = key
  if (key === 'announcement') {
    loadAnnouncements()
    return
  }
  load()
}

async function load() {
  loading.value = true
  try {
    const params = activeType.value ? { type: activeType.value } : {}
    items.value = listOf((await contentApi.list(params)).data)
  } finally {
    loading.value = false
  }
}

async function loadAnnouncements() {
  announcements.value = listOf((await contentApi.announcements()).data)
}

function go(item: any) {
  const contentType = item.type || activeType.value || 'article'
  uni.navigateTo({ url: '/pages/content/detail/index?id=' + item.id + '&type='+contentType })
}

function goAnnouncement(id: string | number) {
  uni.navigateTo({ url: '/pages/announcement/detail/index?id=' + id })
}

function goAnnouncementList() {
  uni.navigateTo({ url: '/pages/announcement/list/index' })
}

function tagOf(item: any) {
  if (item.categoryName) return item.categoryName
  if (item.type === 'video') return '视频'
  if (activeTab.value === 'activity') return '联营商'
  return '品酒知识'
}

function coverOf(item: any, index: number) {
  return item.coverUrl || item.imageUrl || item.cover || (index % 2 === 0 ? '/static/images/product-wine.png' : '/static/images/page-content.png')
}

function formatDate(value?: string) {
  if (!value) return '刚刚'
  return String(value).slice(0, 10)
}

onLoad((options: any) => {
  if (options.type === 'video') activeTab.value = 'recommend'
  if (options.type === 'article') activeTab.value = 'knowledge'
  if (options.type === 'activity') activeTab.value = 'activity'
})

onShow(() => {
  load()
  loadAnnouncements()
})
</script>

<style scoped lang="scss">
.content-page {
  padding-bottom: 40rpx;
}

.hero {
  background: var(--dm-grad-brown);
  color: var(--dm-text-on-brown);
  padding-bottom: 28rpx;
}

.status-space {
  height: var(--status-bar-height);
}

.hero-copy {
  padding: 16rpx 32rpx 22rpx;
}

.hero-title,
.hero-subtitle,
.feed-title,
.feed-summary,
.pin-title,
.pin-meta,
.notice-title,
.notice-subtitle {
  display: block;
}

.hero-title {
  color: #FFE0A0;
  font-size: 44rpx;
  font-weight: 800;
}

.hero-subtitle {
  margin-top: 6rpx;
  color: rgba(246, 231, 194, 0.66);
  font-size: 24rpx;
}

.hero-tabs {
  display: flex;
  gap: 32rpx;
  padding: 0 32rpx;
  overflow-x: auto;
}

.hero-tab {
  position: relative;
  flex: 0 0 auto;
  padding-bottom: 16rpx;
  color: rgba(246, 231, 194, 0.58);
  font-size: 28rpx;
}

.hero-tab.active {
  color: #FFE0A0;
  font-weight: 700;
}

.hero-tab.active::after {
  content: "";
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 36rpx;
  height: 4rpx;
  transform: translateX(-50%);
  background: var(--dm-grad-gold);
}

.content-body {
  padding: 24rpx 32rpx;
}

.pinned,
.notice-entry {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 24rpx;
  padding: 26rpx 28rpx;
}

.pinned {
  border-left: 6rpx solid var(--dm-gold-500);
}

.pin-tag,
.tag {
  display: inline-flex;
  align-items: center;
  border-radius: var(--dm-radius-xs);
  padding: 6rpx 14rpx;
  background: var(--dm-gold-50);
  color: var(--dm-gold-600);
  font-size: 22rpx;
  font-weight: 700;
}

.pin-main,
.notice-main {
  flex: 1;
  min-width: 0;
}

.pin-title,
.notice-title {
  overflow: hidden;
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 800;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pin-meta,
.notice-subtitle {
  margin-top: 8rpx;
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.notice-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 20rpx;
  background: var(--dm-gold-100);
  color: var(--dm-gold-600);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  font-weight: 900;
}

.chevron {
  color: #C9BFA9;
  font-size: 42rpx;
}

.feed-card {
  display: flex;
  gap: 24rpx;
  margin-bottom: 24rpx;
  padding: 24rpx;
}

.feed-card.feature {
  display: block;
  overflow: hidden;
  padding: 0;
}

.cover {
  width: 180rpx;
  height: 180rpx;
  flex: 0 0 180rpx;
  border-radius: var(--dm-radius-sm);
  background: var(--dm-cream-100);
}

.feature .cover {
  width: 100%;
  height: 330rpx;
  border-radius: 0;
}

.feed-main {
  flex: 1;
  min-width: 0;
}

.feature .feed-main {
  padding: 24rpx;
}

.feed-tags {
  display: flex;
  gap: 10rpx;
}

.pay-tag {
  background: #E8EFFD;
  color: var(--dm-blue-500);
}

.feed-title {
  margin-top: 14rpx;
  color: var(--dm-text-1);
  font-size: 30rpx;
  font-weight: 800;
  line-height: 1.42;
}

.feed-summary {
  margin-top: 10rpx;
  color: var(--dm-text-3);
  font-size: 24rpx;
  line-height: 1.55;
  overflow: hidden;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.feed-meta {
  display: flex;
  gap: 20rpx;
  margin-top: 16rpx;
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.date {
  margin-left: auto;
}

.state {
  padding: 48rpx 28rpx;
  color: var(--dm-text-3);
  text-align: center;
}
</style>
