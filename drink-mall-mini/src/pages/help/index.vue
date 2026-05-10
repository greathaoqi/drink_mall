<template>
  <view class="help-page">
    <view v-for="article in articles" :key="article.id" class="help-card" @click="showDetail(article)">
      <text class="help-title">{{ article.title }}</text>
      <uni-icons type="right" />
    </view>
    <view class="empty" v-if="articles.length === 0">
      <uni-icons type="help" size="60" color="#ccc" />
      <text>暂无帮助内容</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const articles = ref<any[]>([])

const loadArticles = async () => {
  const res = await request.get('/content/help')
  articles.value = res.data || []
}

const showDetail = (article: any) => {
  uni.showModal({
    title: article.title,
    content: article.content,
    showCancel: false,
    confirmText: '我知道了'
  })
}

onShow(() => loadArticles())
</script>

<style scoped>
.help-page { background: #f5f5f5; min-height: 100vh; padding: 20rpx; }
.help-card { background: #fff; padding: 30rpx; margin-bottom: 20rpx; border-radius: 12rpx; display: flex; justify-content: space-between; align-items: center; }
.help-title { font-size: 28rpx; }
.empty { display: flex; flex-direction: column; align-items: center; padding: 100rpx; color: #999; }
</style>