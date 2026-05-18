<template>
  <view class="dm-page detail-page">
    <view class="article">
      <text class="tag">{{ categoryText(detail.category || detail.type) }}</text>
      <text class="title">{{ detail.title || '公告详情' }}</text>
      <view class="meta">
        <text>{{ formatDateTime(detail.createdAt || detail.publishTime) }}</text>
        <text>· 醇品汇官方</text>
        <text class="read">阅读 {{ detail.viewCount || detail.views || 0 }}</text>
      </view>
      <rich-text class="content" :nodes="detail.content || detail.summary || ''" />
      <view class="tip">本公告为官方解释，最终解释权以平台规则与后台配置为准。</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { contentApi } from '@/services/content'

const detail = ref<any>({})

function categoryText(value?: string) {
  const map: Record<string, string> = {
    system: '系统公告',
    rule: '规则公告',
    activity: '活动公告',
    notice: '平台公告'
  }
  return map[value || ''] || value || '平台公告'
}

function formatDateTime(value?: string) {
  if (!value) return '刚刚'
  return String(value).replace('T', ' ').slice(0, 16)
}

onLoad(async (options: any) => {
  detail.value = (await contentApi.announcementDetail(options.id)).data || {}
})
</script>

<style scoped lang="scss">
.detail-page {
  min-height: 100vh;
  background: #FFFFFF;
}

.article {
  padding: 32rpx 40rpx 60rpx;
}

.tag {
  display: inline-flex;
  padding: 6rpx 14rpx;
  border-radius: var(--dm-radius-xs);
  background: var(--dm-gold-50);
  color: var(--dm-gold-600);
  font-size: 22rpx;
  font-weight: 700;
}

.title {
  display: block;
  margin-top: 18rpx;
  color: var(--dm-text-1);
  font-size: 40rpx;
  font-weight: 900;
  line-height: 1.42;
}

.meta {
  display: flex;
  gap: 16rpx;
  margin-top: 22rpx;
  color: var(--dm-text-3);
  font-size: 23rpx;
}

.read {
  margin-left: auto;
}

.content {
  display: block;
  margin-top: 40rpx;
  color: var(--dm-text-1);
  font-size: 29rpx;
  line-height: 1.9;
}

.tip {
  margin-top: 40rpx;
  padding: 24rpx 28rpx;
  border-radius: 20rpx;
  background: var(--dm-gold-50);
  color: #7A5610;
  font-size: 24rpx;
  line-height: 1.6;
}
</style>
