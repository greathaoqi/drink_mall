<template>
  <view class="announcement-list">
    <view v-if="loading" class="loading">加载中...</view>
    <view v-else-if="announcements.length === 0" class="empty">暂无公告</view>
    <view v-else>
      <view
        v-for="item in announcements"
        :key="item.id"
        class="announcement-item"
        @click="goDetail(item.id)"
      >
        <text class="title">{{ item.title }}</text>
        <view class="meta">
          <text class="publisher">{{ item.publisher || '平台公告' }}</text>
          <text class="date">{{ formatDate(item.createdAt) }}</text>
        </view>
        <text class="summary">{{ item.summary || item.content?.slice(0, 80) }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

interface Announcement {
  id: number
  title: string
  publisher: string
  summary: string
  content: string
  createdAt: string
}

const announcements = ref<Announcement[]>([])
const loading = ref(true)

const load = async () => {
  loading.value = true
  try {
    const res = await http.get<Announcement[]>('/content/announcements', {}, { requireAuth: false })
    if (res.code === 200) announcements.value = res.data || []
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return dateStr.slice(0, 10)
}

const goDetail = (id: number) => {
  uni.navigateTo({ url: `/pages/announcement/detail/index?id=${id}` })
}

onShow(load)
</script>

<style scoped lang="scss">
.announcement-list {
  padding: 20rpx;
  background: #f5f5f5;
  min-height: 100vh;
}
.loading, .empty {
  text-align: center;
  padding: 80rpx;
  color: #999;
  font-size: 28rpx;
}
.announcement-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}
.title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  display: block;
}
.meta {
  display: flex;
  justify-content: space-between;
  margin: 14rpx 0;
}
.publisher {
  font-size: 24rpx;
  color: #888;
}
.date {
  font-size: 24rpx;
  color: #aaa;
}
.summary {
  font-size: 26rpx;
  color: #666;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
</style>
