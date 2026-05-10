<template>
  <view class="announcement-detail">
    <view v-if="loading" class="loading">加载中...</view>
    <view v-else-if="!announcement" class="empty">公告不存在</view>
    <view v-else class="content-wrap">
      <text class="title">{{ announcement.title }}</text>
      <view class="meta">
        <text class="publisher">{{ announcement.publisher || '平台公告' }}</text>
        <text class="date">{{ formatDate(announcement.createdAt) }}</text>
      </view>
      <view class="divider" />
      <text class="content">{{ announcement.content }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

interface Announcement {
  id: number
  title: string
  publisher: string
  content: string
  createdAt: string
}

const announcement = ref<Announcement | null>(null)
const loading = ref(true)

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return dateStr.slice(0, 10)
}

onLoad(async (options: any) => {
  try {
    const res = await http.get<Announcement>(`/content/announcements/${options.id}`, {}, { requireAuth: false })
    if (res.code === 200) announcement.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.announcement-detail {
  padding: 30rpx;
  min-height: 100vh;
  background: #fff;
}
.loading, .empty {
  text-align: center;
  padding: 80rpx;
  color: #999;
  font-size: 28rpx;
}
.content-wrap {
  display: flex;
  flex-direction: column;
}
.title {
  font-size: 36rpx;
  font-weight: 700;
  color: #222;
  line-height: 1.5;
}
.meta {
  display: flex;
  justify-content: space-between;
  margin: 20rpx 0;
}
.publisher {
  font-size: 26rpx;
  color: #888;
}
.date {
  font-size: 26rpx;
  color: #aaa;
}
.divider {
  height: 1rpx;
  background: #eee;
  margin-bottom: 30rpx;
}
.content {
  font-size: 30rpx;
  color: #444;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
