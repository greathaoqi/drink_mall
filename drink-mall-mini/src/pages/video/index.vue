<template>
  <view class="dm-page video-page">
    <view class="hero">
      <text class="hero-title">视频</text>
      <text class="hero-subtitle">品酒课堂、活动回放与联营商经验分享</text>
    </view>

    <view class="video-list">
      <view v-if="loading" class="state dm-card">加载中...</view>
      <view v-else-if="!items.length" class="state dm-card">暂无视频</view>
      <view v-for="item in items" :key="item.id" class="video-card dm-card dm-press" @click="go(item)">
        <view class="cover-wrap">
          <image class="cover" :src="item.coverUrl || item.imageUrl || '/static/images/page-content.png'" mode="aspectFill" />
          <view class="play">▶</view>
          <text v-if="item.paid" class="pay-tag">{{ item.canView ? '已解锁' : '权限内容' }}</text>
        </view>
        <view class="video-main">
          <text class="title">{{ item.title }}</text>
          <text class="summary">{{ item.summary || item.description || '点击查看视频详情。' }}</text>
          <view class="meta">
            <text>{{ item.viewCount || item.views || 0 }} 播放</text>
            <text>{{ formatDate(item.createdAt || item.publishTime) }}</text>
          </view>
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
    items.value = listOf((await contentApi.list({ type: 'video' })).data)
  } finally {
    loading.value = false
  }
}

function go(item: any) {
  uni.navigateTo({ url: '/pages/content/detail/index?id=' + item.id + '&type=' + (item.type || 'video') })
}

function formatDate(value?: string) {
  if (!value) return '刚刚'
  return String(value).slice(0, 10)
}

onShow(load)
</script>

<style scoped lang="scss">
.video-page {
  padding-bottom: 40rpx;
}

.hero {
  padding: calc(var(--status-bar-height) + 28rpx) 32rpx 54rpx;
  background: var(--dm-grad-brown);
  color: var(--dm-text-on-brown);
}

.hero-title,
.hero-subtitle,
.title,
.summary {
  display: block;
}

.hero-title {
  color: #FFE0A0;
  font-size: 44rpx;
  font-weight: 900;
}

.hero-subtitle {
  margin-top: 8rpx;
  color: rgba(246, 231, 194, 0.66);
  font-size: 24rpx;
}

.video-list {
  margin-top: -28rpx;
  padding: 0 32rpx 40rpx;
}

.video-card {
  overflow: hidden;
  margin-bottom: 24rpx;
}

.cover-wrap {
  position: relative;
  height: 360rpx;
}

.cover {
  width: 100%;
  height: 100%;
  background: var(--dm-cream-100);
}

.play {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 92rpx;
  height: 92rpx;
  line-height: 92rpx;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.48);
  color: #FFFFFF;
  text-align: center;
  font-size: 34rpx;
}

.pay-tag {
  position: absolute;
  left: 20rpx;
  top: 20rpx;
  border-radius: var(--dm-radius-xs);
  padding: 6rpx 14rpx;
  background: var(--dm-grad-gold);
  color: #3A1A00;
  font-size: 22rpx;
  font-weight: 800;
}

.video-main {
  padding: 24rpx;
}

.title {
  color: var(--dm-text-1);
  font-size: 31rpx;
  font-weight: 900;
  line-height: 1.42;
}

.summary {
  margin-top: 10rpx;
  color: var(--dm-text-3);
  font-size: 24rpx;
  line-height: 1.5;
}

.meta {
  display: flex;
  justify-content: space-between;
  margin-top: 18rpx;
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.state {
  padding: 56rpx 28rpx;
  color: var(--dm-text-3);
  text-align: center;
}
</style>
