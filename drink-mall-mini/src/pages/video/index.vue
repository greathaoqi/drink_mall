<template>
  <view class="video-page">
    <view v-for="video in videos" :key="video.id" class="video-card">
      <video
        :src="video.videoUrl"
        :poster="video.coverImage"
        class="video-player"
        :title="video.title"
        controls
        show-center-play-btn
      />
      <view class="video-info">
        <text class="video-title">{{ video.title }}</text>
        <text class="video-duration">{{ formatDuration(video.duration) }}</text>
      </view>
    </view>
    <uni-load-more :status="loadStatus" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const videos = ref<any[]>([])
const page = ref(1)
const hasMore = ref(true)
const loadStatus = ref('more')

const formatDuration = (seconds: number) => {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}

const loadVideos = async () => {
  loadStatus.value = 'loading'
  const res = await request.get('/content/videos', { params: { page: page.value } })
  const newVideos = res.data?.records || []
  videos.value = [...videos.value, ...newVideos]
  hasMore.value = newVideos.length >= 10
  page.value++
  loadStatus.value = hasMore.value ? 'more' : 'noMore'
}

onShow(() => {
  videos.value = []
  page.value = 1
  loadVideos()
})
</script>

<style scoped>
.video-page { background: #f5f5f5; min-height: 100vh; padding: 20rpx; }
.video-card { background: #fff; border-radius: 12rpx; overflow: hidden; margin-bottom: 20rpx; }
.video-player { width: 100%; height: 420rpx; }
.video-info { padding: 20rpx; display: flex; justify-content: space-between; }
.video-title { font-size: 28rpx; font-weight: bold; }
.video-duration { font-size: 24rpx; color: #999; }
</style>