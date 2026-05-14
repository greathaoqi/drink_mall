<template>
  <view v-if="loading" class="state"><uni-load-more status="loading" :content-text="text" /></view>
  <view v-else-if="error" class="state"><text class="title">加载失败</text><text class="desc">{{ error }}</text><button @click="$emit('retry')">重试</button></view>
  <view v-else-if="empty" class="state"><text class="title">暂无数据</text><text class="desc">{{ emptyText }}</text></view>
  <slot v-else />
</template>
<script setup lang="ts">
defineProps<{ loading?: boolean; error?: string; empty?: boolean; emptyText?: string }>()
const text = { contentdown: '加载中', contentrefresh: '加载中', contentnomore: '没有更多' }
defineEmits<{ (e: 'retry'): void }>()
</script>
<style scoped>
.state{min-height:360rpx;display:flex;flex-direction:column;align-items:center;justify-content:center;color:#8a7b6a}.title{font-size:32rpx;font-weight:700}.desc{margin-top:12rpx;font-size:26rpx;color:#9a9085}button{margin-top:28rpx;height:72rpx;line-height:72rpx;border-radius:36rpx;background:#b97700;color:#fff;font-size:26rpx}button:after{border:0}
</style>
